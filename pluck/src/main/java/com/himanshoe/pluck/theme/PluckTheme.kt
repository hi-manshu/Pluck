package com.himanshoe.pluck.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun PluckTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}
