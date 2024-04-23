package com.adhika.imageloading.view

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.collectAsLazyPagingItems
import com.adhika.imageloading.util.ImageUtils
import com.adhika.imageloading.ui.theme.ImageLoadingTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchImages()

        lifecycleScope.launch {
            viewModel.toastState.collect {
                if (it.isNotEmpty()) {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        }

        setContent {
            ImageLoadingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ImageGrid()
                }
            }
        }
    }

    private suspend fun getImageBitmap(url: String): Bitmap? {
        return ImageUtils.downloadAndCacheImage(this@MainActivity, url)
    }

    @Composable
    fun ImageGrid() {
        val imageState by viewModel.imageState.collectAsState()
        val lazyPagingItems = viewModel.pager.collectAsLazyPagingItems()
        when (imageState) {
            is ImageState.Empty -> {
                LoadingImage()
            }
            is ImageState.Error -> {
                ErrorState()
            }
            is ImageState.ErrorToast -> {
                Toast.makeText(this, "Error fetching latest data", Toast.LENGTH_LONG).show()
            }
            else -> {
                LazyVerticalGrid(columns =  GridCells.Fixed(3), modifier = Modifier.fillMaxWidth()) {
                    items(lazyPagingItems.itemCount) { index ->
                        val url = lazyPagingItems[index]?.imageUrl ?: ""
                        DisplayImage(url)
                    }
                }
            }
        }
    }

    @Composable
    fun ErrorState() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = {
                    viewModel.fetchImages()
                },
            ) {
                Text("Retry")
            }
        }
    }

    @Composable
    fun LoadingImage() {
        //show circular progress bar
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }

    }

    @Composable
    fun DisplayImage(url: String) {
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }
        var error by remember { mutableStateOf(false) }

        LaunchedEffect(url) {
            try {
                bitmap = getImageBitmap(url)
            } catch (e: Exception) {
                error = true
            }
        }

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        } ?: if (error) {
            // Show retry button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth()
            ) {
            Button(
                onClick = {
                    error = false
                    lifecycleScope.launch {
                        try {
                            bitmap = getImageBitmap(url)
                        } catch (e: Exception) {
                            error = true
                        }
                    }
                },
            ) {
                Text("Retry")
            }
        }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Text("Loading...")
            }
        }
    }
}