package com.himanshoe.pluck.ui

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberImagePainter
import com.himanshoe.pluck.data.PluckDataSource
import com.himanshoe.pluck.data.PluckImage
import com.himanshoe.pluck.data.PluckRepository
import com.himanshoe.pluck.util.PluckViewModelFactory
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PluckPicker(
    onSelectedPhotos: (List<PluckImage>) -> Unit,
) {
    val context = LocalContext.current

    val pluckViewModel: PluckViewModel = viewModel(factory = PluckViewModelFactory(
        PluckRepository(
            PluckDataSource(context.contentResolver)
        )
    ))
    val lazyPluckImages: LazyPagingItems<PluckImage> =
        pluckViewModel.getImages().collectAsLazyPagingItems()

    Scaffold(floatingActionButton = {
        ExtendedFloatingActionButton(
            modifier = Modifier,
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            text = { Text(text = "SELECT") },
            onClick = { onSelectedPhotos(pluckViewModel.selectedImage.value) },
            icon = { Icon(Icons.Filled.Check, "") }
        )
    }) {

        LazyVerticalGrid(cells = GridCells.Fixed(3)) {
            items(lazyPluckImages.itemCount) { index ->
                lazyPluckImages[index]?.let { pluckImage ->
                    PluckImage(
                        modifier = Modifier,
                        pluckImage = pluckImage,
                        selectedImages = pluckViewModel.selectedImage,
                        onSelectedPhoto = { image, isSelected ->
                            pluckViewModel.isPhotoSelected(pluckImage = image,
                                isSelected = isSelected)
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
    }


}

@Composable
internal fun PluckImage(
    modifier: Modifier,
    pluckImage: PluckImage,
    selectedImages: StateFlow<List<PluckImage>>,
    onSelectedPhoto: (PluckImage, isSelected: Boolean) -> Unit,
) {
    val selected = remember { mutableStateOf(false) }
    val images by selectedImages.collectAsState(initial = emptyList())
    val transition = updateTransition(selected.value, label = "change-padding")

    val animatedPadding by transition.animateDp(label = "change-padding") { isSelected ->
        if (isSelected) 8.dp else 0.dp
    }

    Box(
        modifier = modifier
            .padding(2.dp)
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
            modifier = Modifier.padding(animatedPadding)
        )
        Box(
            modifier = Modifier
                .clickable {
                    selected.value = !selected.value
                    onSelectedPhoto(pluckImage, selected.value)
                }
                .size(128.dp),
            contentAlignment = Alignment.TopEnd,
        ) {
            PluckImageIndicator(text = images.indexOf(pluckImage).toString())
        }
    }
}

@Composable
fun PluckImageIndicator(text: String) {
    if (text.toInt() > -1) {
        val backgroundColor = MaterialTheme.colors.primary
        val textColor = MaterialTheme.colors.onPrimary

        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = textColor,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            modifier = Modifier
                .drawBehind {
                    drawCircle(backgroundColor)
                }
                .padding(8.dp)
        )
    }
}
