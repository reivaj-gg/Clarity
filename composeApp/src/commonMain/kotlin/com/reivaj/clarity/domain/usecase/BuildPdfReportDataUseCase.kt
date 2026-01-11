package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.*
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt

/**
 * Builds comprehensive PDF report data by aggregating stats from sessions and EMAs.
 */
class BuildPdfReportDataUseCase(
    private val repository: ClarityRepository,
    private val performanceScoreCalculator: PerformanceScoreCalculator,
    private val coachInsightGenerator: CoachInsightGenerator,
    private val getProfileStatsUseCase: GetProfileStatsUseCase,
) {
    suspend operator fun invoke(period: ReportPeriod): PdfReportData {
        val now = Clock.System.now()
        val cutoffTime = now.minus(period.days, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        
        // Get all data
        val allSessions = repository.getAllGameSessions().first()
        val allEmas = repository.getAllEMAs().first()
        val profileStats = getProfileStatsUseCase()
        
        // Filter by period
        val sessions = allSessions.filter { it.timestamp >= cutoffTime }
        val emas = allEmas.filter { it.timestamp >= cutoffTime }
        val emaMap = allEmas.associateBy { it.id }
        
        // Calculate performance score
        val (score, breakdown) = performanceScoreCalculator.calculate(sessions, profileStats.currentStreak)
        
        // Calculate mood stats
        val moodStats = calculateMoodStats(emas)
        
        // Calculate sleep stats
        val sleepStats = calculateSleepStats(emas, sessions, emaMap)
        
        // Calculate lifestyle notes
        val lifestyleNotes = calculateLifestyleNotes(emas)
        
        // Build sleep impact table
        val sleepImpactTable = buildSleepImpactTable(sessions, emaMap)
        
        // Build circadian profile
        val circadianProfile = buildCircadianProfile(sessions)
        
        // Build error analysis
        val errorAnalysis = buildErrorAnalysis(sessions, emaMap)
        
        // Calculate per-game stats
        val gameStats = calculateGameStats(sessions)
        
        // Find weakest game
        val weakestGame = gameStats.minByOrNull { it.value.avgAccuracy }?.key?.name?.replace("_", " ")
        
        // Detect fatigue
        val fatigueDetected = detectFatigue(sessions)
        
        // Generate coach insights
        val coachInsights = coachInsightGenerator.generate(
            sleepStats = sleepStats,
            moodStats = moodStats,
            circadianProfile = circadianProfile,
            errorAnalysis = errorAnalysis,
            improvementPercent = breakdown.improvementPercent,
            weakestGame = weakestGame,
            fatigueDetected = fatigueDetected,
        )
        
        return PdfReportData(
            reportPeriod = period,
            generatedAt = now,
            performanceScore = score,
            performanceScoreBreakdown = breakdown,
            moodStats = moodStats,
            sleepStats = sleepStats,
            lifestyleNotes = lifestyleNotes,
            sleepImpactTable = sleepImpactTable,
            circadianProfile = circadianProfile,
            errorAnalysis = errorAnalysis,
            coachInsights = coachInsights,
            gameStats = gameStats,
            recentSessions = sessions.sortedByDescending { it.timestamp }.take(20),
            totalSessions = sessions.size,
            totalEmas = emas.size,
            currentStreak = profileStats.currentStreak,
            averageAccuracy = if (sessions.isNotEmpty()) sessions.map { it.accuracy.toDouble() }.average() else 0.0,
        )
    }
    
    private fun calculateMoodStats(emas: List<EMA>): MoodStats {
        if (emas.isEmpty()) {
            return MoodStats(0.0, 0.0, 0.0, 0.0, "No mood data available")
        }
        
        val avgHappiness = emas.map { it.happiness }.average()
        val avgAnxiety = emas.map { it.anxiety }.average()
        val avgSadness = emas.map { it.sadness }.average()
        val avgAnger = emas.map { it.anger }.average()
        
        val interpretation = when {
            avgHappiness >= 4 && avgAnxiety < 2.5 -> "Generally positive mood 😊"
            avgAnxiety >= 3.5 || avgSadness >= 3.5 -> "Elevated stress indicators detected"
            avgHappiness >= 3 -> "Stable neutral mood"
            else -> "Mixed emotional patterns"
        }
        
        return MoodStats(avgHappiness, avgAnxiety, avgSadness, avgAnger, interpretation)
    }
    
    private fun calculateSleepStats(
        emas: List<EMA>,
        sessions: List<GameSession>,
        emaMap: Map<String, EMA>,
    ): SleepStats {
        if (emas.isEmpty()) {
            return SleepStats(0.0, 0.0, 0.0, 0.0, 0.0, "No sleep data")
        }
        
        val sleepHours = emas.map { it.sleepHours }
        val avgHours = sleepHours.average()
        val avgQuality = emas.map { it.sleepQuality }.average()
        
        // Calculate sleep impact on performance
        val goodSleepSessions = sessions.filter { s ->
            s.emaId?.let { emaMap[it]?.sleepHours }?.let { it >= 6.0 } == true
        }
        val poorSleepSessions = sessions.filter { s ->
            s.emaId?.let { emaMap[it]?.sleepHours }?.let { it < 6.0 } == true
        }
        
        val impact = if (goodSleepSessions.isNotEmpty() && poorSleepSessions.isNotEmpty()) {
            val goodAvg = goodSleepSessions.map { it.accuracy }.average()
            val poorAvg = poorSleepSessions.map { it.accuracy }.average()
            ((goodAvg - poorAvg) / goodAvg) * 100
        } else 0.0
        
        val qualityInterpretation = when {
            avgQuality >= 4.0 -> "Excellent sleep quality"
            avgQuality >= 3.0 -> "Good sleep quality"
            avgQuality >= 2.0 -> "Fair sleep quality"
            else -> "Poor sleep quality - consider improvements"
        }
        
        return SleepStats(
            avgHours = avgHours,
            minHours = sleepHours.minOrNull() ?: 0.0,
            maxHours = sleepHours.maxOrNull() ?: 0.0,
            avgQuality = avgQuality,
            impactOnPerformance = impact,
            qualityInterpretation = qualityInterpretation,
        )
    }
    
    private fun calculateLifestyleNotes(emas: List<EMA>): LifestyleNotes {
        if (emas.isEmpty()) {
            return LifestyleNotes(0.0, 0.0, 0.0)
        }
        
        val caffeinePercent = (emas.count { it.caffeineRecent }.toDouble() / emas.size) * 100
        val alcoholPercent = (emas.count { it.alcoholUse != AlcoholUseToday.NONE }.toDouble() / emas.size) * 100
        val stressPercent = (emas.count { it.hasNegativeEvent }.toDouble() / emas.size) * 100
        
        return LifestyleNotes(caffeinePercent, alcoholPercent, stressPercent)
    }
    
    private fun buildSleepImpactTable(
        sessions: List<GameSession>,
        emaMap: Map<String, EMA>,
    ): SleepImpactTable {
        fun getRow(min: Double, max: Double): SleepImpactRow {
            val filtered = sessions.filter { s ->
                s.emaId?.let { emaMap[it]?.sleepHours }?.let { it >= min && it < max } == true
            }
            return SleepImpactRow(
                avgAccuracy = if (filtered.isNotEmpty()) filtered.map { it.accuracy.toDouble() }.average() * 100 else 0.0,
                sessionCount = filtered.size,
            )
        }
        
        return SleepImpactTable(
            under6Hours = getRow(0.0, 6.0),
            sixTo7Hours = getRow(6.0, 7.0),
            sevenTo9Hours = getRow(7.0, 9.0),
            over9Hours = getRow(9.0, 24.0),
        )
    }
    
    private fun buildCircadianProfile(sessions: List<GameSession>): CircadianProfile {
        if (sessions.size < 5) {
            return CircadianProfile(9, 0.0, 15, 0.0, "Not enough data for circadian analysis")
        }
        
        val timeZone = TimeZone.currentSystemDefault()
        val byHour = sessions.groupBy { it.timestamp.toLocalDateTime(timeZone).hour }
            .mapValues { (_, hourSessions) -> hourSessions.map { it.accuracy }.average() }
        
        val peak = byHour.maxByOrNull { it.value }
        val lowest = byHour.minByOrNull { it.value }
        
        val peakHour = peak?.key ?: 9
        val lowestHour = lowest?.key ?: 15
        
        val recommendation = when {
            peakHour in 6..11 -> "Schedule training between ${peakHour - 1}:00 - ${peakHour + 2}:00 AM"
            peakHour in 12..17 -> "Afternoon training around ${peakHour}:00 works best for you"
            else -> "Evening sessions around ${peakHour}:00 suit your rhythm"
        }
        
        return CircadianProfile(
            peakHour = peakHour,
            peakAccuracy = (peak?.value ?: 0.0) * 100,
            lowestHour = lowestHour,
            lowestAccuracy = (lowest?.value ?: 0.0) * 100,
            recommendation = recommendation,
        )
    }
    
    private fun buildErrorAnalysis(
        sessions: List<GameSession>,
        emaMap: Map<String, EMA>,
    ): ErrorAnalysis {
        val totalOmission = sessions.sumOf { it.omissionErrors }
        val totalCommission = sessions.sumOf { it.commissionErrors }
        
        val omissionWhenTired = sessions.filter { s ->
            s.emaId?.let { emaMap[it]?.sleepHours }?.let { it < 6.0 } == true
        }.sumOf { it.omissionErrors }
        
        val commissionWhenStressed = sessions.filter { s ->
            s.emaId?.let { ema -> emaMap[ema]?.let { it.anxiety >= 4 || it.sadness >= 4 } } == true
        }.sumOf { it.commissionErrors }
        
        return ErrorAnalysis(
            totalOmissionErrors = totalOmission,
            totalCommissionErrors = totalCommission,
            omissionWhenTired = omissionWhenTired,
            commissionWhenStressed = commissionWhenStressed,
            omissionTrend = if (omissionWhenTired > totalOmission / 3) "Increases significantly with poor sleep" else "Stable across conditions",
            commissionTrend = if (commissionWhenStressed > totalCommission / 3) "Increases with stress/anxiety" else "Stable across conditions",
        )
    }
    
    private fun calculateGameStats(sessions: List<GameSession>): Map<GameType, GameStatsSummary> {
        return sessions.groupBy { it.gameType }.mapValues { (_, gameSessions) ->
            val sorted = gameSessions.sortedBy { it.timestamp }
            val halfPoint = sorted.size / 2
            
            val firstHalfAvg = if (halfPoint > 0) sorted.take(halfPoint).map { it.score }.average() else 0.0
            val secondHalfAvg = if (halfPoint > 0) sorted.drop(halfPoint).map { it.score }.average() else 0.0
            val improvement = if (firstHalfAvg > 0) ((secondHalfAvg - firstHalfAvg) / firstHalfAvg) * 100 else 0.0
            
            GameStatsSummary(
                sessionsPlayed = gameSessions.size,
                avgScore = gameSessions.map { it.score }.average(),
                avgAccuracy = gameSessions.map { it.accuracy.toDouble() }.average(),
                bestScore = gameSessions.maxOfOrNull { it.score } ?: 0,
                improvementPercent = improvement,
            )
        }
    }
    
    private fun detectFatigue(sessions: List<GameSession>): Boolean {
        if (sessions.size < 10) return false
        
        val last10 = sessions.sortedByDescending { it.timestamp }.take(10)
        val highVariabilityCount = last10.count { it.reactionTimeVariability > 100.0 }
        return (highVariabilityCount.toDouble() / 10) > 0.4
    }
}
