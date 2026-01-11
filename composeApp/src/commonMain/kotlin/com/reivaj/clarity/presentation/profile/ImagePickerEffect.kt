package com.reivaj.clarity.presentation.profile

import androidx.compose.runtime.Composable

/**
 * Platform-specific image picker effect.
 * Launches the native image picker when showPicker becomes true.
 */
@Composable
expect fun ImagePickerEffect(
    showPicker: Boolean,
    onImageSelected: (String?) -> Unit,
    onDismiss: () -> Unit,
)
