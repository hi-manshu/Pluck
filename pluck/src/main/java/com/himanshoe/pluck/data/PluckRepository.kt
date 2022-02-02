package com.himanshoe.pluck.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class PluckRepository constructor(
    private val pluckDataSource: PluckDataSource,
) {
    fun getImages(): Flow<PagingData<PluckImage>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                pluckDataSource
            }).flow
    }
}
