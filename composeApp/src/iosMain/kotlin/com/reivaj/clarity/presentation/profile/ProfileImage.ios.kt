package com.reivaj.clarity.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.Foundation.*

/**
 * iOS implementation using Skia for image loading.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun ProfileImage(uri: String, modifier: Modifier) {
    val imageBitmap = remember(uri) {
        try {
            val data = NSData.dataWithContentsOfFile(uri)
            if (data != null && data.length > 0UL) {
                val bytes = data.toByteArray()
                val skiaImage = Image.makeFromEncoded(bytes)
                skiaImage.toComposeImageBitmap()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Profile Picture",
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    if (length == 0) return ByteArray(0)
    
    val bytes = ByteArray(length)
    bytes.usePinned { pinned ->
        platform.posix.memcpy(pinned.addressOf(0), this.bytes, this.length)
    }
    return bytes
}
