package com.reivaj.clarity.domain.model

import com.reivaj.clarity.domain.model.GameType

/**
 * Analytics summary containing performance insights and patterns.
 *
 * @property averageScorePerGame Average score for each game type
 * @property bestGame The game with highest average score
 * @property sleepImpact Analysis of sleep's effect on performance
 * @property peakPerformanceHour Best performing hour of day (0-23)
 * @property peakPerformanceValue Average score at peak hour
 * @property baselineVsStressed Performance comparison between baseline and stressed states
 * @property totalSessions Total number of game sessions analyzed
 * @property omissionErrorsWhenTired Total omission errors with <6h sleep
 * @property commissionErrorsWhenStressed Total commission errors during stress
 * @property fatigueDetected True if >40% of last 10 sessions show high RT variability
 * @property recentVariabilityPercentage Percentage of recent sessions with RT variability >100ms
 */
data class AnalyticsSummary(
    val averageScorePerGame: Map<GameType, Double>,
    val bestGame: GameType?,
    val sleepImpact: SleepImpactData,
    val peakPerformanceHour: Int?, // 0-23, null if not enough data
    val peakPerformanceValue: Double = 0.0, // Average score at peak hour
    val baselineVsStressed: BaselineComparisonData,
    val totalSessions: Int,
    
    // Advanced analytics metrics
    val omissionErrorsWhenTired: Int = 0,
    val commissionErrorsWhenStressed: Int = 0,
    val fatigueDetected: Boolean = false,
    val recentVariabilityPercentage: Double = 0.0,
)

/**
 * Sleep impact analysis data.
 *
 * @property averageScoreWithGoodSleep Average score with 6+ hours sleep
 * @property averageScoreWithPoorSleep Average score with <6 hours sleep
 * @property performanceDifference Percentage difference (negative = worse with poor sleep)
 * @property hasEnoughData True if we have sufficient data points for analysis
 */
data class SleepImpactData(
    val averageScoreWithGoodSleep: Double,
    val averageScoreWithPoorSleep: Double,
    val performanceDifference: Double, // Percentage: (good - poor) / good * 100
    val hasEnoughData: Boolean,
)

/**
 * Baseline vs stressed performance comparison.
 *
 * @property baselineAverageScore Average score during baseline conditions
 * @property stressedAverageScore Average score during stressed conditions
 * @property performanceDifference Percentage difference
 * @property hasEnoughData True if we have sufficient data points
 */
data class BaselineComparisonData(
    val baselineAverageScore: Double,
    val stressedAverageScore: Double,
    val performanceDifference: Double, // Percentage: (baseline - stressed) / baseline * 100
    val hasEnoughData: Boolean,
)
