package com.himanshoe.pluck.util

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.himanshoe.pluck.data.PluckImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun loadPhotosFromExternalStorageIntoVariable(
    context: Context,
    photos: SnapshotStateList<PluckImage>,
) {
    photos.addAll(loadPhotosFromExternalStorage(context))
}


private suspend fun loadPhotosFromExternalStorage(
    context: Context,
): List<PluckImage> {
    return withContext(Dispatchers.IO) {
        val collection = sdk29AndUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
        )

        val photos = mutableListOf<PluckImage>()

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                photos.add(PluckImage(contentUri))
            }
            photos.toList()
        } ?: emptyList()
    }
}
