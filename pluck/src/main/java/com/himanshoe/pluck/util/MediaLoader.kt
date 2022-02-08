package com.himanshoe.pluck.util
/*
* MIT License
*
* Copyright (c) 2022 Himanshu Singh
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.core.os.bundleOf
import com.himanshoe.pluck.data.PluckImage

private val projection = arrayOf(
    MediaStore.Images.Media._ID,
    MediaStore.Images.Media.DISPLAY_NAME,
    MediaStore.Images.Media.DATE_TAKEN,
    MediaStore.Images.Media.BUCKET_DISPLAY_NAME
)

internal fun Context.createCursor(limit: Int, offset: Int): Cursor? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val bundle = bundleOf(
            ContentResolver.QUERY_ARG_OFFSET to offset,
            ContentResolver.QUERY_ARG_LIMIT to limit,
            ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Images.Media.DATE_ADDED),
            ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
        )
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            bundle,
            null
        )
    } else {
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC LIMIT $limit OFFSET $offset",
            null
        )
    }
}

internal fun Context.fetchPagePicture(limit: Int, offset: Int): List<PluckImage> {
    val pictures = ArrayList<PluckImage>()
    val cursor = createCursor(limit, offset)
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
            val folderName = it.getString(bucketDisplayName)

            pictures.add(
                PluckImage(
                    contentUri,
                    dateTaken,
                    displayName,
                    id,
                    folderName.toString()
                )
            )
        }
    }
    cursor?.close()
    return pictures
}
