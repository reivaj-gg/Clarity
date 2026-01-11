package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.remote.GeminiAiService
import com.reivaj.clarity.domain.insight.InsightAnalyzers
import com.reivaj.clarity.domain.model.Insight
import com.reivaj.clarity.domain.model.InsightType

class GenerateInsightsUseCase(
    private val calculateAnalytics: CalculateAnalyticsUseCase,
    private val buildAiContext: BuildAiContextUseCase,
    private val geminiService: GeminiAiService
) {
    suspend operator fun invoke(): List<Insight> {
        val localInsights = mutableListOf<Insight>()
        
        // 1. Local Analytics
        try {
            val summary = calculateAnalytics()
            if (summary != null) {
                InsightAnalyzers.analyzeSleep(summary.sleepImpact)?.let { localInsights.add(it) }
                InsightAnalyzers.analyzeChronotype(summary.peakPerformanceHour)?.let { localInsights.add(it) }
                InsightAnalyzers.analyzeStress(summary.baselineVsStressed)?.let { localInsights.add(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. AI Daily Wisdom
        try {
            val context = buildAiContext()
            val wisdom = geminiService.generateDailyInsight(context)
            // Add to top
            localInsights.add(0, Insight(
                title = "Daily Coach Wisdom",
                description = wisdom,
                type = InsightType.AI_GENERATED,
                score = 1.0
            ))
        } catch (e: Exception) {
            // Fallback tip if AI fails or offline (though use case should handle it)
            localInsights.add(0, Insight(
                title = "Daily Tip",
                description = "Consistency is key. Try to play at the same time each day.",
                type = InsightType.TIP,
                score = 0.5
            ))
        }
        
        // Ensure at least one insight
        if (localInsights.isEmpty()) {
            localInsights.add(Insight(
                title = "Gathering Data",
                description = "Play a few more games and log your sleep to unlock personalized insights.",
                type = InsightType.NEUTRAL
            ))
        }

        return localInsights
    }
}
