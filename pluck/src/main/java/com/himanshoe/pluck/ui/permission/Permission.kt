package com.himanshoe.pluck.ui.permission

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.himanshoe.pluck.R
import com.himanshoe.pluck.theme.PluckDimens

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun Permission(
    permissions: List<String>,
    goToAppSettings: () -> Unit,
    appContent: @Composable () -> Unit,
) {
    val permissionState = rememberMultiplePermissionsState(permissions = permissions)
    val isPermissionDenied = remember {
        mutableStateOf(false)
    }
    PermissionsRequired(
        multiplePermissionsState = permissionState,
        permissionsNotGrantedContent = {
            Scaffold(
                Modifier
                    .background(MaterialTheme.colors.surface)
                    .fillMaxSize()
                    .padding(PluckDimens.One),
            ) {
                isPermissionDenied.value = false
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(PluckDimens.Six),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = rememberImagePainter(R.drawable.ic_camera_moments),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1F)
                    )
                    Spacer(modifier = Modifier.height(PluckDimens.Three))

                    Text(
                        stringResource(R.string.permission_prompt), textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onSurface,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                    )
                    Spacer(modifier = Modifier.height(PluckDimens.Three))

                    Text(
                        modifier = Modifier.alpha(0.3F),
                        text = "Allowing access to your camera/storage will let you pick your memories asap!",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onSurface,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.height(PluckDimens.Three))

                    Button(
                        onClick = {
                            permissionState.launchMultiplePermissionRequest()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.primary),
                    ) {
                        Text(
                            text = "Enable permissions",
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }
        },
        permissionsNotAvailableContent = {
            isPermissionDenied.value = true
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(PluckDimens.Six),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberImagePainter(R.drawable.ic_sad),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(PluckDimens.Sixteen)
                        .aspectRatio(1F)
                )
                Spacer(modifier = Modifier.height(PluckDimens.Three))

                Text(
                    stringResource(R.string.permissions_rationale), textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onSurface,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                )
                Spacer(modifier = Modifier.height(PluckDimens.Three))

                Button(
                    onClick = { goToAppSettings() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primary),
                ) {
                    Text(
                        text = "Go to Settings!",
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    ) {
        appContent()
    }
}
