package com.himanshoe.pluck.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberImagePainter
import com.himanshoe.pluck.data.PluckDataSource
import com.himanshoe.pluck.data.PluckImage
import com.himanshoe.pluck.data.PluckRepository
import com.himanshoe.pluck.util.PluckViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryPicker() {
    val context = LocalContext.current

    val pluckViewModel: PluckViewModel = viewModel(factory = PluckViewModelFactory(
        PluckRepository(
            PluckDataSource(context.contentResolver)
        )
    ))
    val lazyPluckImages: LazyPagingItems<PluckImage> =
        pluckViewModel.getImages().collectAsLazyPagingItems()
    LazyVerticalGrid(cells = GridCells.Fixed(3)) {
        items(lazyPluckImages.itemCount) { index ->
            lazyPluckImages[index]?.let { pluckImage ->
                PluckSelectedImage(
                    modifier = Modifier,
                    pluckImage = pluckImage,
                    selectedImages = pluckViewModel.selectedImage,
                    onSelectedPhoto = { image, isSelected ->
                        pluckViewModel.isPhotoSelected(pluckImage = image, isSelected = isSelected)
                    })
            }
        }

        if (lazyPluckImages.loadState.append == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
//    var imageUri by remember {
//        mutableStateOf<Uri?>(null)
//    }
//    val bitmap = remember {
//        mutableStateOf<Bitmap?>(null)
//    }
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetMultipleContents()
//    ) { uri: List<Uri> ->
//        imageUri = uri.first()
//    }
//
//    Column {
//        Button(onClick = {
//            launcher.launch("image/*")
//        }) {
//            Text(text = "Pick image")
//        }
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        imageUri?.let {
//            if (Build.VERSION.SDK_INT < 28) {
//                bitmap.value = MediaStore.Images
//                    .Media.getBitmap(context.contentResolver, it)
//            } else {
//                val source = ImageDecoder
//                    .createSource(context.contentResolver, it)
//                bitmap.value = ImageDecoder.decodeBitmap(source)
//            }
//
//            bitmap.value?.let { btm ->
//                Image(
//                    bitmap = btm.asImageBitmap(),
//                    contentDescription = null,
//                    modifier = Modifier.size(400.dp)
//                )
//            }
//        }
//    }
}

@Composable
fun PluckSelectedImage(
    modifier: Modifier,
    pluckImage: PluckImage,
    selectedImages: StateFlow<List<PluckImage>>,
    onSelectedPhoto: (PluckImage, isSelected: Boolean) -> Unit,
) {
    val selected = remember { mutableStateOf(false) }
    val images by selectedImages.collectAsState(initial = emptyList())

    Box(
        modifier = modifier
            .clickable {
                selected.value = !selected.value
                onSelectedPhoto(pluckImage, selected.value)
            }
            .size(128.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = rememberImagePainter(pluckImage.uri),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = modifier
                .clickable {
                    selected.value = !selected.value
                    onSelectedPhoto(pluckImage, selected.value)
                }
                .size(128.dp),
            contentAlignment = Alignment.TopEnd,
        ) {
            Text(text = images.indexOf(pluckImage).toString(),
                textAlign = TextAlign.End,
                modifier = Modifier.border(width = 1.dp, color = Color.Blue))
        }
    }
}
