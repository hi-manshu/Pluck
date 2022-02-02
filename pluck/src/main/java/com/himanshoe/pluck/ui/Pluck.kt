package com.himanshoe.pluck.ui

import android.graphics.Bitmap
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
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
import com.himanshoe.pluck.theme.PluckDimens
import com.himanshoe.pluck.util.PluckViewModelFactory
import kotlinx.coroutines.flow.StateFlow

private const val SELECT = "SELECT"

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun Pluck(
    onPhotoSelected: (List<PluckImage>) -> Unit,
    onPhotoClicked: (Bitmap?) -> Unit,
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
            text = { Text(text = SELECT) },
            onClick = { onPhotoSelected(pluckViewModel.selectedImage.value) },
            icon = { Icon(Icons.Filled.Check, "") }
        )
    }) {
        val modifier = Modifier.padding(2.dp)
        val cameraLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                onPhotoClicked(it)
            }
        LazyVerticalGrid(
            modifier = Modifier.background(MaterialTheme.colors.surface),
            cells = GridCells.Fixed(3)) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .size(PluckDimens.Sixteen)
                        .clickable { handleCamera(cameraLauncher) }
                        .then(Modifier.background(MaterialTheme.colors.background))) {
                    Icon(
                        Icons.Filled.Add,
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = "add-photo"
                    )
                }
            }
            items(lazyPluckImages.itemCount) { index ->
                lazyPluckImages[index]?.let { pluckImage ->
                    PluckImage(
                        modifier = modifier,
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

private fun handleCamera(onPhotoClicked: ManagedActivityResultLauncher<Void?, Bitmap?>) {
    onPhotoClicked.launch()
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
        if (isSelected) PluckDimens.One else PluckDimens.Zero
    }

    Box(
        modifier = modifier
            .clickable {
                selected.value = !selected.value
                onSelectedPhoto(pluckImage, selected.value)
            }
            .size(PluckDimens.Sixteen),
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
                .size(PluckDimens.Sixteen),
            contentAlignment = Alignment.TopEnd,
        ) {
            PluckImageIndicator(text = images.indexOf(pluckImage).toString())
        }
    }
}

@Composable
internal fun PluckImageIndicator(text: String) {
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
                .padding(PluckDimens.One)
        )
    }
}
