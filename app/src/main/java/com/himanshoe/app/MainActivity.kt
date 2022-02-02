package com.himanshoe.app

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.himanshoe.pluck.theme.PluckTheme
import com.himanshoe.pluck.ui.PluckPicker
import com.himanshoe.pluck.util.Permission

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PluckTheme {
                Permission(permission = Manifest.permission.READ_EXTERNAL_STORAGE,
                    goToAppSettings = { goToAppSettings() }) {
                    PluckPicker()
                }
            }
        }
    }

    private fun goToAppSettings() {
        Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${packageName}")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

}
