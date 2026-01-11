package com.reivaj.clarity.data.export

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.*
import platform.UIKit.*

/**
 * iOS implementation of PDF file handler.
 * Saves PDFs to Documents folder and opens share sheet.
 */
@OptIn(ExperimentalForeignApi::class)
actual class PdfFileHandler {
    
    actual suspend fun savePdf(pdfBytes: ByteArray, fileName: String): String {
        val timestamp = NSDateFormatter().apply {
            dateFormat = "yyyyMMdd_HHmmss"
        }.stringFromDate(NSDate())
        
        val actualFileName = "${fileName}_$timestamp.pdf"
        
        // Get Documents directory
        val documentsDir = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        ).firstOrNull() as? String ?: ""
        
        val filePath = "$documentsDir/$actualFileName"
        
        // Convert ByteArray to NSData and write
        val nsData = pdfBytes.toNSData()
        nsData.writeToFile(filePath, atomically = true)
        
        return filePath
    }
    
    actual suspend fun sharePdf(filePath: String) {
        val fileUrl = NSURL.fileURLWithPath(filePath)
        
        val activityViewController = UIActivityViewController(
            activityItems = listOf(fileUrl),
            applicationActivities = null
        )
        
        // Get the top view controller and present share sheet
        val keyWindow = UIApplication.sharedApplication.keyWindow
        val rootViewController = keyWindow?.rootViewController
        
        // Find the topmost presented view controller
        var topController = rootViewController
        while (topController?.presentedViewController != null) {
            topController = topController.presentedViewController
        }
        
        topController?.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    }
    
    private fun ByteArray.toNSData(): NSData {
        if (this.isEmpty()) return NSData()
        
        return this.usePinned { pinned ->
            NSData.create(
                bytes = pinned.addressOf(0),
                length = this.size.toULong()
            )
        }
    }
}
