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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
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
import coil.compose.rememberImagePainter
import com.himanshoe.pluck.R
import com.himanshoe.pluck.data.PluckImage
import com.himanshoe.pluck.data.PluckRepositoryImpl
import com.himanshoe.pluck.theme.PluckDimens
import com.himanshoe.pluck.util.PluckViewModelFactory
import kotlinx.coroutines.flow.StateFlow

private const val SELECT = "SELECT"
private const val ONE = 1

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun Pluck(
    onPhotoSelected: (List<PluckImage>) -> Unit,
    onPhotoClicked: (Bitmap?) -> Unit,
) {
    val context = LocalContext.current
    val pluckViewModel: PluckViewModel = viewModel(
        factory = PluckViewModelFactory(
            PluckRepositoryImpl(
                context
            )
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
        val modifier = Modifier.padding(2.dp)
        val cameraLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                onPhotoClicked(it)
            }
        LazyVerticalGrid(
            modifier = Modifier.background(MaterialTheme.colors.surface),
            cells = GridCells.Fixed(3)
        ) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .size(PluckDimens.Sixteen)
                        .clickable { handleCamera(cameraLauncher) }
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
