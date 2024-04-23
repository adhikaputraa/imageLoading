package com.adhika.imageloading.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_data")
data class ImageDataEntity(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val language: String = "",
    val mediaType: Int = 0,
    val coverageURL: String = "",
    val publishedAt: String = "",
    val publishedBy: String = "",
    val imageId: String = "",
    val version: Int = 0,
    val imageUrl: String = "",
)