package com.adhika.imageloading.repository

import com.adhika.imageloading.model.ImageData
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("media-coverages?limit=100")
    suspend fun getImages(): Response<List<ImageData>>
}
