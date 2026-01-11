package com.reivaj.clarity.data.export

/**
 * Platform-specific PDF file handler for saving and sharing PDF files.
 */
expect class PdfFileHandler {
    /**
     * Save PDF bytes to device storage and return the file path.
     * @param pdfBytes The PDF content as ByteArray
     * @param fileName Desired file name (without extension)
     * @return The absolute path to the saved file
     */
    suspend fun savePdf(pdfBytes: ByteArray, fileName: String): String
    
    /**
     * Open a share dialog for the saved PDF file.
     * @param filePath Path to the PDF file
     */
    suspend fun sharePdf(filePath: String)
}
