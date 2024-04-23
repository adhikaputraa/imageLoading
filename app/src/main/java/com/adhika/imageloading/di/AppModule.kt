package com.adhika.imageloading.di

import android.content.Context
import androidx.room.Room
import com.adhika.imageloading.repository.ApiService
import com.adhika.imageloading.database.AppDatabase
import com.adhika.imageloading.database.ImageDataDao
import com.adhika.imageloading.repository.ImageDataRepository
import com.adhika.imageloading.repository.ImageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://acharyaprashant.org/api/v2/content/misc/"

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "image_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideImageDataDao(appDatabase: AppDatabase): ImageDataDao {
        return appDatabase.imageDataDao()
    }

    @Provides
    @Singleton
    fun provideImageDataRepository(apiService: ApiService, imageDataDao: ImageDataDao): ImageRepository {
        return ImageDataRepository(apiService, imageDataDao)
    }


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL.toHttpUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }
}