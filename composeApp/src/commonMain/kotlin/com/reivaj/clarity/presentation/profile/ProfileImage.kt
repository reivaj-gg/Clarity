package com.reivaj.clarity.presentation.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform-specific profile image display.
 * Uses appropriate image loading for each platform.
 */
@Composable
expect fun ProfileImage(uri: String, modifier: Modifier = Modifier.fillMaxSize())
