package com.reivaj.clarity.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import platform.PhotosUI.*
import platform.UIKit.*
import platform.darwin.NSObject

/**
 * iOS implementation of image picker using PHPickerViewController.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun ImagePickerEffect(
    showPicker: Boolean,
    onImageSelected: (String?) -> Unit,
    onDismiss: () -> Unit,
) {
    LaunchedEffect(showPicker) {
        if (showPicker) {
            val configuration = PHPickerConfiguration().apply {
                filter = PHPickerFilter.imagesFilter
                selectionLimit = 1
            }
            
            val picker = PHPickerViewController(configuration = configuration)
            
            // Create delegate
            val delegate = ImagePickerDelegate(
                onResult = { url ->
                    if (url != null) {
                        onImageSelected(url)
                    } else {
                        onDismiss()
                    }
                }
            )
            
            picker.delegate = delegate
            
            // Get top view controller
            val keyWindow = UIApplication.sharedApplication.keyWindow
            var topController = keyWindow?.rootViewController
            while (topController?.presentedViewController != null) {
                topController = topController.presentedViewController
            }
            
            topController?.presentViewController(
                picker,
                animated = true,
                completion = null
            )
        }
    }
}

/**
 * Delegate for PHPickerViewController.
 */
@OptIn(ExperimentalForeignApi::class)
private class ImagePickerDelegate(
    private val onResult: (String?) -> Unit,
) : NSObject(), PHPickerViewControllerDelegateProtocol {
    
    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, completion = null)
        
        val result = didFinishPicking.firstOrNull() as? PHPickerResult
        if (result != null) {
            result.itemProvider.loadFileRepresentationForTypeIdentifier(
                typeIdentifier = "public.image"
            ) { url, _ ->
                if (url != null) {
                    // Copy to Documents directory for persistence
                    val documentsPath = NSSearchPathForDirectoriesInDomains(
                        NSDocumentDirectory,
                        NSUserDomainMask,
                        true
                    ).firstOrNull() as? String
                    
                    if (documentsPath != null) {
                        val fileName = "profile_${NSDate().timeIntervalSince1970.toLong()}.jpg"
                        val destinationPath = "$documentsPath/$fileName"
                        
                        try {
                            NSFileManager.defaultManager.copyItemAtPath(
                                url.path ?: "",
                                destinationPath,
                                null
                            )
                            onResult(destinationPath)
                        } catch (_: Exception) {
                            onResult(null)
                        }
                    } else {
                        onResult(null)
                    }
                } else {
                    onResult(null)
                }
            }
        } else {
            onResult(null)
        }
    }
}
