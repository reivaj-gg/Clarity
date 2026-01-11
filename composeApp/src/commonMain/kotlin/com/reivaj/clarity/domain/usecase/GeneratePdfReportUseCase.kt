package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.export.PdfGenerator
import com.reivaj.clarity.data.repository.ClarityRepository
import kotlinx.coroutines.flow.first

/**
 * Use case to generate a PDF report of user's performance data.
 *
 * Orchestrates data collection from repository and PDF generation.
 */
class GeneratePdfReportUseCase(
    private val repository: ClarityRepository,
    private val pdfGenerator: PdfGenerator,
    private val getProfileStatsUseCase: GetProfileStatsUseCase,
    private val calculateAnalyticsUseCase: CalculateAnalyticsUseCase,
) {
    suspend operator fun invoke(): ByteArray {
        val stats = getProfileStatsUseCase()
        val analytics = calculateAnalyticsUseCase()
        val sessions = repository.getAllGameSessions().first()
        
        return pdfGenerator.generateReport(
            stats = stats,
            analytics = analytics,
            recentSessions = sessions,
        )
    }
}
