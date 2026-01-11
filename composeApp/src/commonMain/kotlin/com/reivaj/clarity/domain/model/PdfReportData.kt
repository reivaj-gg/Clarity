package com.reivaj.clarity.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Report period for PDF generation.
 */
@Serializable
enum class ReportPeriod(val days: Int, val label: String) {
    LAST_7_DAYS(7, "Last 7 Days"),
    LAST_14_DAYS(14, "Last 14 Days"),
    LAST_30_DAYS(30, "Last 30 Days"),
}

/**
 * Comprehensive data model for PDF report generation.
 * Contains all calculated statistics, insights, and session data.
 */
@Serializable
data class PdfReportData(
    val reportPeriod: ReportPeriod,
    val generatedAt: Instant,
    val userName: String = "Guest User", // Personalization
    
    // Performance Score (0-100)
    val performanceScore: Int,
    val performanceScoreBreakdown: PerformanceScoreBreakdown,
    
    // Mood Stats (7-day averages)
    val moodStats: MoodStats,
    
    // Sleep Stats
    val sleepStats: SleepStats,
    
    // Lifestyle Notes
    val lifestyleNotes: LifestyleNotes,
    
    // Sleep Impact Table
    val sleepImpactTable: SleepImpactTable,
    
    // Circadian Profile
    val circadianProfile: CircadianProfile,
    
    // Error Analysis
    val errorAnalysis: ErrorAnalysis,
    
    // Coach Insights (4-6 personalized tips)
    val coachInsights: List<String>,
    
    // Game Stats
    val gameStats: Map<GameType, GameStatsSummary>,
    
    // Recent Sessions
    val recentSessions: List<GameSession>,
    
    // Summary Stats
    val totalSessions: Int,
    val totalEmas: Int,
    val currentStreak: Int,
    val averageAccuracy: Double,
)

/**
 * Breakdown of performance score calculation.
 */
@Serializable
data class PerformanceScoreBreakdown(
    val accuracyScore: Int, // 0-50 points
    val streakScore: Int, // 0-20 points
    val improvementScore: Int, // 0-20 points
    val varietyScore: Int, // 0-10 points
    val improvementPercent: Double,
    val gamesPlayed: Int,
)

/**
 * Mood statistics from EMA data.
 */
@Serializable
data class MoodStats(
    val avgHappiness: Double,
    val avgAnxiety: Double,
    val avgSadness: Double,
    val avgAnger: Double,
    val interpretation: String, // e.g., "Generally positive mood"
)

/**
 * Sleep statistics from EMA data.
 */
@Serializable
data class SleepStats(
    val avgHours: Double,
    val minHours: Double,
    val maxHours: Double,
    val avgQuality: Double, // 1-5 scale
    val impactOnPerformance: Double, // % difference
    val qualityInterpretation: String, // e.g., "Good sleep quality"
)

/**
 * Lifestyle factors from EMA data.
 */
@Serializable
data class LifestyleNotes(
    val caffeineUsagePercent: Double, // % of sessions with caffeine
    val alcoholUsagePercent: Double, // % of sessions with alcohol
    val stressfulEventsPercent: Double, // % of sessions with stress events
)

/**
 * Sleep impact analysis table data.
 */
@Serializable
data class SleepImpactTable(
    val under6Hours: SleepImpactRow,
    val sixTo7Hours: SleepImpactRow,
    val sevenTo9Hours: SleepImpactRow,
    val over9Hours: SleepImpactRow,
)

@Serializable
data class SleepImpactRow(
    val avgAccuracy: Double,
    val sessionCount: Int,
)

/**
 * Circadian profile data.
 */
@Serializable
data class CircadianProfile(
    val peakHour: Int, // 0-23
    val peakAccuracy: Double,
    val lowestHour: Int, // 0-23
    val lowestAccuracy: Double,
    val recommendation: String, // e.g., "Schedule training 9am-11am"
)

/**
 * Error analysis data.
 */
@Serializable
data class ErrorAnalysis(
    val totalOmissionErrors: Int,
    val totalCommissionErrors: Int,
    val omissionWhenTired: Int, // When sleep < 6h
    val commissionWhenStressed: Int, // When stress >= 4
    val omissionTrend: String, // "Increases with poor sleep"
    val commissionTrend: String, // "Increases with stress"
)

/**
 * Per-game statistics summary.
 */
@Serializable
data class GameStatsSummary(
    val sessionsPlayed: Int,
    val avgScore: Double,
    val avgAccuracy: Double,
    val bestScore: Int,
    val improvementPercent: Double, // First half vs second half
)
