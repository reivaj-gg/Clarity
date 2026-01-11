package com.reivaj.clarity.data.export

import com.reivaj.clarity.domain.model.PdfReportData

/**
 * Platform-specific PDF generator.
 *
 * Android: Uses android.graphics.pdf.PdfDocument
 * iOS: Uses PDFKit
 */
expect class PdfGenerator {
    /**
     * Generate a comprehensive PDF report from report data.
     *
     * @param reportData Comprehensive report data including stats, analytics, insights
     * @return PDF file as ByteArray
     */
    suspend fun generateReport(reportData: PdfReportData): ByteArray
}
