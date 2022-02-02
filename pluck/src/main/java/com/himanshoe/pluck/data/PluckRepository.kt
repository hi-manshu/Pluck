package com.himanshoe.pluck.data

import android.content.Context
import androidx.paging.PagingSource
import com.himanshoe.pluck.util.createCursor
import com.himanshoe.pluck.util.fetchPagePicture

interface PluckRepository {
    suspend fun getCount(): Int
    suspend fun getByOffset(offset: Int): PluckImage?
    fun getPicturePagingSource(): PagingSource<Int, PluckImage>
}

internal class PluckRepositoryImpl(private val context: Context) : PluckRepository {

    override suspend fun getCount(): Int {
        val cursor = context.createCursor(Int.MAX_VALUE, 0) ?: return 0
        val count = cursor.count
        cursor.close()
        return count
    }

    override suspend fun getByOffset(offset: Int): PluckImage? {
        return context.fetchPagePicture(1, offset).firstOrNull()
    }

    override fun getPicturePagingSource(): PagingSource<Int, PluckImage> {
        return PluckDataSource { limit, offset -> context.fetchPagePicture(limit, offset) }
    }
}
