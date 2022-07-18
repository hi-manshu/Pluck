package com.himanshoe.pluck.ui
/*
* MIT License
*
* Copyright (c) 2022 Himanshu Singh
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberImagePainter
import com.himanshoe.pluck.PluckConfiguration
import com.himanshoe.pluck.R
import com.himanshoe.pluck.data.PluckImage
import com.himanshoe.pluck.data.PluckRepositoryImpl
import com.himanshoe.pluck.theme.PluckDimens
import com.himanshoe.pluck.theme.PluckDimens.Quarter
import com.himanshoe.pluck.util.PluckUriManager
import com.himanshoe.pluck.util.PluckViewModelFactory
import kotlinx.coroutines.flow.StateFlow

private const val SELECT = "SELECT"
private const val ONE = 1
private const val THREE = 3

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun Pluck(
    pluckConfiguration: PluckConfiguration = PluckConfiguration(),
    onPhotoSelected: (List<PluckImage>) -> Unit,
) {
    val context = LocalContext.current
    val pluckViewModel: PluckViewModel = viewModel(
        factory = PluckViewModelFactory(
            PluckRepositoryImpl(
                context,
            ),
            PluckUriManager(context),
            pluckConfiguration
        )
    )

    val lazyPluckImages: LazyPagingItems<PluckImage> =
        pluckViewModel.getImages().collectAsLazyPagingItems()

    Scaffold(floatingActionButton = {
        ExtendedFloatingActionButton(
            modifier = Modifier,
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            text = { Text(text = SELECT) },
            onClick = { onPhotoSelected(pluckViewModel.selectedImage.value) },
            icon = { Icon(Icons.Rounded.Check, "") }
        )
    }) {
        val modifier = Modifier.padding(Quarter)
        val cameraLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                onPhotoSelected(listOf(pluckViewModel.getPluckImage()) as List<PluckImage>)
            }
        LazyVerticalGrid(
            modifier = Modifier.background(MaterialTheme.colors.surface),
            cells = GridCells.Fixed(THREE)
        ) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .size(PluckDimens.Sixteen)
                        .clickable { handleCamera(pluckViewModel, cameraLauncher) }
                        .then(Modifier.background(MaterialTheme.colors.background))
                ) {
                    Image(
                        painter = rememberImagePainter(R.drawable.ic_camera),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(PluckDimens.Six)
                            .alpha(0.2F)
                    )
                }
            }
            items(lazyPluckImages.itemCount) { index ->
                lazyPluckImages[index]?.let { pluckImage ->
                    PluckImage(
                        modifier = modifier,
                        pluckImage = pluckImage,
                        pluckConfiguration = pluckConfiguration,
                        selectedImages = pluckViewModel.selectedImage,
                        onSelectedPhoto = { image, isSelected ->
                            pluckViewModel.isPhotoSelected(
                                pluckImage = image,
                                isSelected = isSelected
                            )
                        }
                    )
                }
            }
        }
    }
}

private fun handleCamera(
    pluckViewModel: PluckViewModel,
    onPhotoClicked: ManagedActivityResultLauncher<Uri, Boolean>,
) {
    onPhotoClicked.launch(pluckViewModel.getCameraImageUri())
}

@Composable
internal fun PluckImage(
    modifier: Modifier,
    pluckImage: PluckImage,
    selectedImages: StateFlow<List<PluckImage>>,
    pluckConfiguration: PluckConfiguration,
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
                    if (!pluckConfiguration.multipleImagesAllowed) {
                        if (images.count() < 1) {
                            selected.value = !selected.value
                            onSelectedPhoto(pluckImage, selected.value)
                        } else {
                            selected.value = false
                            onSelectedPhoto(pluckImage, selected.value)
                        }
                    } else {
                        selected.value = !selected.value
                        onSelectedPhoto(pluckImage, selected.value)
                    }
                }
                .size(PluckDimens.Sixteen),
            contentAlignment = Alignment.TopEnd,
        ) {
            PluckImageIndicator(text = images.indexOf(pluckImage).plus(ONE).toString())
        }
    }
}

@Composable
internal fun PluckImageIndicator(text: String) {
    if (text.toInt() > 0) {
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
