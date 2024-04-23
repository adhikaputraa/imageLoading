package com.adhika.imageloading.repository

import com.adhika.imageloading.model.ImageData
import com.adhika.imageloading.model.ImageDataEntity
import retrofit2.Response

interface ImageRepository {
    suspend fun getImages(): List<ImageDataEntity>
    suspend fun insert(imageDataEntity: ImageDataEntity)
    suspend fun count(): Int
    suspend fun getImageData(): Response<List<ImageData>>
    fun getImagesPagingSource(): ImageDataPagingSource
}