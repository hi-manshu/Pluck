package com.himanshoe.pluck.data

import android.content.ContentResolver
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.himanshoe.pluck.util.getImages

class PluckDataSource(private val contentResolver: ContentResolver) :
    PagingSource<Int, PluckImage>() {
    private val images = mutableListOf<PluckImage>()

    override fun getRefreshKey(state: PagingState<Int, PluckImage>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PluckImage> {
        return try {
            val pagedKeyDate = params.key ?: System.currentTimeMillis()
            val images = getImages(contentResolver, images)
            val lastItemDate = images.last().dateTaken

            if (pagedKeyDate == lastItemDate) {
                return LoadResult.Error(Throwable())
            }

            LoadResult.Page(
                data = images,
                prevKey = null,
                nextKey = images.last().id.toInt()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
