package com.himanshoe.pluck.data
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
import android.content.Context
import androidx.paging.PagingSource
import com.himanshoe.pluck.util.createCursor
import com.himanshoe.pluck.util.fetchPagePicture

private const val Zero = 0
private const val One = 1

internal interface PluckRepository {
    suspend fun getCount(): Int
    suspend fun getByOffset(offset: Int): PluckImage?
    fun getPicturePagingSource(): PagingSource<Int, PluckImage>
}

internal class PluckRepositoryImpl(private val context: Context) : PluckRepository {

    override suspend fun getCount(): Int {
        val cursor = context.createCursor(Int.MAX_VALUE, Zero) ?: return Zero
        val count = cursor.count
        cursor.close()
        return count
    }

    override suspend fun getByOffset(offset: Int): PluckImage? {
        return context.fetchPagePicture(One, offset).firstOrNull()
    }

    override fun getPicturePagingSource(): PagingSource<Int, PluckImage> {
        return PluckDataSource { limit, offset -> context.fetchPagePicture(limit, offset) }
    }
}
