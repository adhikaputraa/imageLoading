package com.adhika.imageloading.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.adhika.imageloading.database.ImageDataDao
import com.adhika.imageloading.model.ImageDataEntity

class ImageDataPagingSource(
    private val imageDataDao: ImageDataDao
) : PagingSource<Int, ImageDataEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageDataEntity> {
        val page = params.key ?: 0
        val pageSize = params.loadSize

        return try {
            val images = imageDataDao.getImages(page * pageSize, pageSize)
            LoadResult.Page(
                data = images,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (images.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageDataEntity>): Int? {
        return state.anchorPosition
    }
}