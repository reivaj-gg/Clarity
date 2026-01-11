package com.reivaj.clarity.data.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Android implementation of PDF file handler.
 * Saves PDFs to app's files directory and opens share intent.
 */
actual class PdfFileHandler(private val context: Context) {
    
    actual suspend fun savePdf(pdfBytes: ByteArray, fileName: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val actualFileName = "${fileName}_$timestamp.pdf"
        
        // Use internal files directory (always available and accessible by FileProvider)
        val pdfDir = File(context.filesDir, "pdfs")
        if (!pdfDir.exists()) {
            pdfDir.mkdirs()
        }
        
        val file = File(pdfDir, actualFileName)
        file.writeBytes(pdfBytes)
        
        return file.absolutePath
    }
    
    actual suspend fun sharePdf(filePath: String) {
        val file = File(filePath)
        
        // Get URI using FileProvider for Android 7.0+
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Clarity Performance Report")
            putExtra(Intent.EXTRA_TEXT, "Here's my cognitive performance report from Clarity!")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        val chooserIntent = Intent.createChooser(shareIntent, "Share PDF Report").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        context.startActivity(chooserIntent)
    }
}
