package com.himanshoe.pluck.util

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import android.util.Log
import com.himanshoe.pluck.data.PluckImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getImages(
    contentResolver: ContentResolver,
    images: MutableList<PluckImage>,
) = withContext(Dispatchers.IO) {
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )

    val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
    val cursor = contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
    )

    cursor?.use {
        val idColumn = it.getColumnIndexOrThrow(projection[0])
        val displayNameColumn = it.getColumnIndexOrThrow(projection[1])
        val dateTakenColumn = it.getColumnIndexOrThrow(projection[2])
        val bucketDisplayName = it.getColumnIndexOrThrow(projection[3])

        while (it.moveToNext()) {
            val id = it.getLong(idColumn)
            val dateTaken = it.getLong(dateTakenColumn)
            val displayName = it.getString(displayNameColumn)
            val contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )
            val bucketId = it.getString(bucketDisplayName)

            Log.d("images",
                "id : $id, contentUri: $contentUri, diplayName: $displayName, folder: $bucketId")
            images.add(PluckImage(contentUri,
                dateTaken,
                displayName,
                id,
                bucketDisplayName.toString()))
        }
    }
    cursor?.close()
    images
}

