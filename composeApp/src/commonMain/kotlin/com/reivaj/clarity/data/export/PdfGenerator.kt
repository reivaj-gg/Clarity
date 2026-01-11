package com.reivaj.clarity.data.export

import com.reivaj.clarity.domain.model.AnalyticsSummary
import com.reivaj.clarity.domain.model.GameSession
import com.reivaj.clarity.domain.model.ProfileStats

/**
 * Platform-specific PDF generator.
 *
 * Android: Uses android.graphics.pdf.PdfDocument
 * iOS: Uses PDFKit
 */
expect class PdfGenerator {
    /**
     * Generate a PDF report from profile statistics and analytics.
     *
     * @param stats User profile statistics
     * @param analytics Performance analytics summary
     * @param recentSessions Recent game sessions for detailed history
     * @return PDF file as ByteArray
     */
    suspend fun generateReport(
        stats: ProfileStats,
        analytics: AnalyticsSummary?,
        recentSessions: List<GameSession>,
    ): ByteArray
}
