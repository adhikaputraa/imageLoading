package com.adhika.imageloading.view

sealed class ImageState {
    data object Loading : ImageState()
    data object Success : ImageState()
    data object Error : ImageState()
    data object ErrorToast: ImageState()
    data object Empty : ImageState()
}