package com.himanshoe.pluck.ui
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
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.himanshoe.pluck.data.PluckImage
import com.himanshoe.pluck.data.PluckRepository
import com.himanshoe.pluck.util.PluckUriManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class PluckViewModel(
    private val pluckRepository: PluckRepository,
    private val pluckUriManager: PluckUriManager,
) : ViewModel() {

    private val selectedImageList: MutableList<PluckImage> = ArrayList()
    private val _selectedImage = MutableStateFlow(emptyList<PluckImage>())
    private var uri: Uri? = null

    val selectedImage: StateFlow<List<PluckImage>> = _selectedImage

    fun getPluckImage() = pluckUriManager.getPluckImage(uri)

    fun getImages(): Flow<PagingData<PluckImage>> = Pager(
        config = PagingConfig(pageSize = 50, initialLoadSize = 50, enablePlaceholders = true)
    ) {
        pluckRepository.getPicturePagingSource()
    }.flow.cachedIn(viewModelScope)

    fun isPhotoSelected(pluckImage: PluckImage, isSelected: Boolean) {
        if (isSelected) {
            selectedImageList.add(pluckImage)
        } else {
            selectedImageList.filter { it.id == pluckImage.id }
                .forEach { selectedImageList.remove(it) }
        }
        _selectedImage.value = (selectedImageList).toSet().toList()
    }

    fun getCameraImageUri(): Uri? {
        uri = pluckUriManager.newUri
        return uri
    }
}
