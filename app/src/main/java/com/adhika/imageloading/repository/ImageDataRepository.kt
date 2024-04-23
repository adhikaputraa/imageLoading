package com.adhika.imageloading.repository

import com.adhika.imageloading.database.ImageDataDao
import com.adhika.imageloading.model.ImageData
import com.adhika.imageloading.model.ImageDataEntity
import retrofit2.Response
import javax.inject.Inject

class ImageDataRepository @Inject constructor(
    private val apiService: ApiService,
    private val imageDataDao: ImageDataDao
) : ImageRepository {

    override suspend fun getImages(): List<ImageDataEntity> {
        val response = apiService.getImages()
        if (response.isSuccessful) {
            return response.body()?.map { imageData ->
                val imageUrl = imageData.thumbnail.domain + "/" + imageData.thumbnail.basePath + "/0/" + imageData.thumbnail.key
                ImageDataEntity(
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
            } ?: emptyList()
        } else {
            throw Exception("Error fetching images")
        }
    }

    override suspend fun insert(imageDataEntity: ImageDataEntity) {
        imageDataDao.insert(imageDataEntity)
    }

    override suspend fun count(): Int {
        return imageDataDao.count()
    }

    override suspend fun getImageData(): Response<List<ImageData>> {
        return apiService.getImages()
    }

    override fun getImagesPagingSource(): ImageDataPagingSource {
        return ImageDataPagingSource(imageDataDao)
    }
}