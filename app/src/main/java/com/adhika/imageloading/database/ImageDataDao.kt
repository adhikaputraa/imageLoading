package com.adhika.imageloading.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adhika.imageloading.model.ImageDataEntity

@Dao
interface ImageDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(imageData: ImageDataEntity)

    @Query("SELECT * FROM image_data")
    suspend fun getAll(): List<ImageDataEntity>

    @Query("SELECT * FROM image_data LIMIT :pageSize OFFSET :offset")
    suspend fun getImages(offset: Int, pageSize: Int): List<ImageDataEntity>

    @Query("SELECT COUNT(*) FROM image_data")
    suspend fun count(): Int
}