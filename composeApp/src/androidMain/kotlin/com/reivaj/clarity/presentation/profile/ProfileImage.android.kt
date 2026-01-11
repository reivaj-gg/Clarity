package com.reivaj.clarity.presentation.profile

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

/**
 * Android implementation using BitmapFactory for image loading.
 */
@Composable
actual fun ProfileImage(uri: String, modifier: Modifier) {
    val context = LocalContext.current
    
    val bitmap = remember(uri) {
        try {
            val parsedUri = Uri.parse(uri)
            context.contentResolver.openInputStream(parsedUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Profile Picture",
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    }
}
