package com.himanshoe.pluck.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.himanshoe.pluck.R
import com.himanshoe.pluck.theme.PluckDimens

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Permission(
    permissions: List<String>,
    goToAppSettings: () -> Unit,
    appContent: @Composable () -> Unit,
) {
    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    PermissionsRequired(
        multiplePermissionsState = permissionState,
        permissionsNotGrantedContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.surface)
                    .padding(PluckDimens.Six),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.permission_prompt))
                Spacer(modifier = Modifier.height(PluckDimens.Two))
                Row {
                    Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                        Text(stringResource(R.string.permission_prompt_button))
                    }
                }
            }
        },
        permissionsNotAvailableContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.surface)
                    .padding(PluckDimens.Six),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.permissions_rationale))
                Spacer(modifier = Modifier.height(PluckDimens.One))
                Button(onClick = goToAppSettings) {
                    Text(stringResource(R.string.open_settings))
                }
            }
        }) {
        appContent()
    }
}
