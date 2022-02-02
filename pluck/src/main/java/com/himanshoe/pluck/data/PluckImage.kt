package com.himanshoe.pluck.data

import android.net.Uri

data class PluckImage(
    val uri: Uri,
    internal val dateTaken: Long?,
    val displayName: String,
    internal val id: Long,
    internal val folderName: String,
)
