package com.adhika.imageloading.view

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.adhika.imageloading.model.ImageData
import com.adhika.imageloading.model.ImageDataEntity
import com.adhika.imageloading.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageDataRepository: ImageRepository
) : ViewModel() {

    var pager = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = true),
        pagingSourceFactory = { imageDataRepository.getImagesPagingSource() }
    ).flow

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var isTableEmpty = false

    private val _imageState = MutableStateFlow<ImageState>(ImageState.Loading)
    val imageState: StateFlow<ImageState> = _imageState.asStateFlow()

    private val _toastState = MutableStateFlow("")
    val toastState: StateFlow<String> = _toastState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->

        if (isTableEmpty) _imageState.value = ImageState.Error
        else {
            _toastState.value = "Error fetch data from server"
            _imageState.value = ImageState.Success
        }
    }

    init {
        viewModelScope.launch(exceptionHandler + Dispatchers.IO) {
            isTableEmpty = imageDataRepository.count() == 0
            if (isTableEmpty) _imageState.value = ImageState.Empty
            else {
                pager = Pager(
                    config = PagingConfig(pageSize = 20, enablePlaceholders = true),
                    pagingSourceFactory = { imageDataRepository.getImagesPagingSource() }
                ).flow
                _imageState.value = ImageState.Success
            }
        }
    }

    fun fetchImages() {
        if (isTableEmpty) _imageState.value = ImageState.Empty
        viewModelScope.launch(exceptionHandler + Dispatchers.IO) {
            val response = imageDataRepository.getImageData()
            if (response.isSuccessful) {
                onSuccessFetchData(response)
            } else {
                onErrorFetchData()
            }
        }
    }

    private suspend fun onSuccessFetchData(response: Response<List<ImageData>>) {
        response.body()?.forEach { imageData ->
            val imageUrl = imageData.thumbnail.domain + "/" + imageData.thumbnail.basePath + "/0/" + imageData.thumbnail.key
            val imageDataEntity = ImageDataEntity(
                id = imageData.id,
                title = imageData.title,
                language = imageData.language,
                mediaType = imageData.mediaType,
                coverageURL = imageData.coverageURL,
                publishedAt = imageData.publishedAt,
                publishedBy = imageData.publishedBy,
                imageId = imageData.thumbnail.id,
                version = imageData.thumbnail.version,
                imageUrl = imageUrl,
            )
            imageDataRepository.insert(imageDataEntity)
        }
        pager = Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = true),
            pagingSourceFactory = { imageDataRepository.getImagesPagingSource() }
        ).flow
        _imageState.value = ImageState.Success
    }

    private fun onErrorFetchData() {
        if (isTableEmpty) _imageState.value = ImageState.Error
        else {
            _imageState.value = ImageState.ErrorToast
            _imageState.value = ImageState.Success
        }
    }
}