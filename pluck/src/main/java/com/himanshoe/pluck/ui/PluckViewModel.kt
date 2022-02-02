package com.himanshoe.pluck.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.himanshoe.pluck.data.PluckImage
import com.himanshoe.pluck.data.PluckRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PluckViewModel(private val pluckRepository: PluckRepository) : ViewModel() {

    private val selectedImageList: MutableList<PluckImage> = ArrayList()
    private val _selectedImage = MutableStateFlow(emptyList<PluckImage>())

    val selectedImage: StateFlow<List<PluckImage>> = _selectedImage

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
}
