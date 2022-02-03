package com.himanshoe.pluck.ui

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
