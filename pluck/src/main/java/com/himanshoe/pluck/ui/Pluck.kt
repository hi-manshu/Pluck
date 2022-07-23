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
import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.himanshoe.pluck.PluckConfiguration
import com.himanshoe.pluck.R
import com.himanshoe.pluck.data.PluckImage
import com.himanshoe.pluck.data.PluckRepositoryImpl
import com.himanshoe.pluck.theme.PluckDimens
import com.himanshoe.pluck.theme.PluckDimens.Quarter
import com.himanshoe.pluck.util.PluckUriManager
import com.himanshoe.pluck.util.PluckViewModelFactory
import kotlinx.coroutines.flow.StateFlow

private const val Select = "SELECT"
private const val One = 1
private const val Three = 3

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun Pluck(
    modifier: Modifier = Modifier,
    pluckConfiguration: PluckConfiguration = PluckConfiguration(),
    onPhotoSelected: (List<PluckImage>) -> Unit,
) {
    val context = LocalContext.current
    val gridState: LazyGridState = rememberLazyGridState()

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
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            text = { Text(text = Select) },
            onClick = { onPhotoSelected(pluckViewModel.selectedImage.value) },
            icon = { Icon(Icons.Rounded.Check, "fab-icon") }
        )
    }, content = {
        val cameraLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { it: Boolean ->
                onPhotoSelected(listOf(pluckViewModel.getPluckImage()) as List<PluckImage>)
            }

        LazyVerticalGrid(
            state = gridState,
            modifier = modifier
                .padding(Quarter)
                .background(MaterialTheme.colorScheme.surface),
            columns = GridCells.Fixed(pluckConfiguration.gridCount),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            item {
                CameraIcon(
                    modifier = modifier,
                    cameraLauncher = cameraLauncher,
                    pluckViewModel = pluckViewModel
                )
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
    })
}

@Composable
internal fun CameraIcon(
    modifier: Modifier,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    pluckViewModel: PluckViewModel,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable { handleCamera(pluckViewModel, cameraLauncher) }
            .then(Modifier.background(MaterialTheme.colorScheme.background))
            .padding(8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(R.drawable.ic_camera),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
                .alpha(0.2F)
        )
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
    val backgroundColor = if (selected.value) Color.Black else Color.Transparent

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = rememberAsyncImagePainter(pluckImage.uri),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.aspectRatio(1f)
        )

        Box(
            modifier = modifier
                .clickable {
                    if (!pluckConfiguration.multipleImagesAllowed) {
                        if (images.isEmpty()) {
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
                .fillMaxSize()
                .aspectRatio(1f)
                .alpha(0.5F)
                .background(color = backgroundColor),
        ) {
            PluckImageIndicator(
                modifier = modifier,
                text = images.indexOf(pluckImage).plus(One).toString()
            )
        }
    }
}

@Composable
internal fun PluckImageIndicator(modifier: Modifier = Modifier, text: String) {
    if (text.toInt() > 0) {
        val textColor = MaterialTheme.colorScheme.onPrimary

        Text(
            text = text,
            textAlign = TextAlign.End,
            color = textColor,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = modifier
                .fillMaxSize()
                .padding(PluckDimens.One)
        )
    }
}
