package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.export.PdfGenerator
import com.reivaj.clarity.domain.model.ReportPeriod

/**
 * Use case to generate a comprehensive PDF report of user's performance data.
 *
 * Orchestrates data collection, analysis, and PDF generation.
 */
class GeneratePdfReportUseCase(
    private val buildPdfReportDataUseCase: BuildPdfReportDataUseCase,
    private val pdfGenerator: PdfGenerator,
) {
    /**
     * Generate a PDF report for the specified time period.
     *
     * @param period Report period (7, 14, or 30 days)
     * @return PDF file as ByteArray
     */
    suspend operator fun invoke(period: ReportPeriod = ReportPeriod.LAST_7_DAYS): ByteArray {
        val reportData = buildPdfReportDataUseCase(period)
        return pdfGenerator.generateReport(reportData)
    }
}
