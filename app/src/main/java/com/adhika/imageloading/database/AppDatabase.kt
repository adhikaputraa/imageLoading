package com.adhika.imageloading.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adhika.imageloading.model.ImageDataEntity

@Database(entities = [ImageDataEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDataDao(): ImageDataDao
}