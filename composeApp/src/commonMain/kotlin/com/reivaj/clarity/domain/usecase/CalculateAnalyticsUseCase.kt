package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.*
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Use case to calculate comprehensive analytics from game session and EMA data.
 *
 * Analyzes performance patterns including:
 * - Average scores per game type
 * - Best performing game
 * - Sleep impact on performance
 * - Peak performance time of day
 * - Baseline vs stressed performance comparison
 */
class CalculateAnalyticsUseCase(
    private val repository: ClarityRepository,
) {
    suspend operator fun invoke(): AnalyticsSummary? {
        val sessions = repository.getAllGameSessions().first()
        val emas = repository.getAllEMAs().first()
        
        if (sessions.isEmpty()) {
            return null
        }
        
        // Create EMA lookup map
        val emaMap = emas.associateBy { it.id }
        
        // Calculate average score per game type
        val scoresByGame = sessions
            .groupBy { it.gameType }
            .mapValues { (_, gameSessions) ->
                gameSessions.map { it.score }.average()
            }
        
        val bestGame = scoresByGame.maxByOrNull { it.value }?.key
        
        // Calculate sleep impact
        val sleepImpact = calculateSleepImpact(sessions, emaMap)
        
        // Calculate peak performance hour and value
        val (peakHour, peakValue) = calculatePeakPerformanceHourAndValue(sessions)
        
        // Calculate baseline vs stressed comparison
        val baselineComparison = calculateBaselineComparison(sessions, emaMap)
        
        // Advanced analytics
        val omissionErrorsWhenTired = calculateOmissionErrorsWhenTired(sessions, emaMap)
        val commissionErrorsWhenStressed = calculateCommissionErrorsWhenStressed(sessions, emaMap)
        val (fatigueDetected, recentVariabilityPercentage) = detectFatigue(sessions)
        
        return AnalyticsSummary(
            averageScorePerGame = scoresByGame,
            bestGame = bestGame,
            sleepImpact = sleepImpact,
            peakPerformanceHour = peakHour,
            peakPerformanceValue = peakValue,
            baselineVsStressed = baselineComparison,
            totalSessions = sessions.size,
            omissionErrorsWhenTired = omissionErrorsWhenTired,
            commissionErrorsWhenStressed = commissionErrorsWhenStressed,
            fatigueDetected = fatigueDetected,
            recentVariabilityPercentage = recentVariabilityPercentage,
        )
    }
    
    private fun calculateSleepImpact(
        sessions: List<GameSession>,
        emaMap: Map<String, EMA>,
    ): SleepImpactData {
        val sessionsWithEma = sessions.mapNotNull { session ->
            session.emaId?.let { emaId ->
                session to emaMap[emaId]
            }
        }
        
        val goodSleepSessions = sessionsWithEma.filter { (_, ema) ->
            ema != null && ema.sleepHours >= 6.0
        }
        
        val poorSleepSessions = sessionsWithEma.filter { (_, ema) ->
            ema != null && ema.sleepHours < 6.0
        }
        
        val hasEnoughData = goodSleepSessions.size >= 3 && poorSleepSessions.size >= 3
        
        if (!hasEnoughData) {
            return SleepImpactData(
                averageScoreWithGoodSleep = 0.0,
                averageScoreWithPoorSleep = 0.0,
                performanceDifference = 0.0,
                hasEnoughData = false,
            )
        }
        
        val goodSleepAvg = goodSleepSessions.map { it.first.score }.average()
        val poorSleepAvg = poorSleepSessions.map { it.first.score }.average()
        val difference = ((goodSleepAvg - poorSleepAvg) / goodSleepAvg) * 100
        
        return SleepImpactData(
            averageScoreWithGoodSleep = goodSleepAvg,
            averageScoreWithPoorSleep = poorSleepAvg,
            performanceDifference = difference,
            hasEnoughData = true,
        )
    }
    
    /**
     * Calculate peak performance hour and average score at that hour.
     */
    private fun calculatePeakPerformanceHourAndValue(sessions: List<GameSession>): Pair<Int?, Double> {
        if (sessions.size < 5) return null to 0.0
        
        val timeZone = TimeZone.currentSystemDefault()
        
        val scoresByHour = sessions
            .groupBy { session ->
                session.timestamp.toLocalDateTime(timeZone).hour
            }
            .mapValues { (_, hourSessions) ->
                hourSessions.map { it.score }.average()
            }
        
        val peakEntry = scoresByHour.maxByOrNull { it.value }
        return peakEntry?.key to (peakEntry?.value ?: 0.0)
    }
    
    /**
     * Count omission errors during sessions with poor sleep (<6h).
     */
    private fun calculateOmissionErrorsWhenTired(
        sessions: List<GameSession>,
        emaMap: Map<String, EMA>,
    ): Int {
        return sessions.sumOf { session ->
            val ema = session.emaId?.let { emaMap[it] }
            if (ema != null && ema.sleepHours < 6.0) {
                session.omissionErrors
            } else {
                0
            }
        }
    }
    
    /**
     * Count commission errors during stressed sessions (anxiety or sadness >= 4).
     */
    private fun calculateCommissionErrorsWhenStressed(
        sessions: List<GameSession>,
        emaMap: Map<String, EMA>,
    ): Int {
        return sessions.sumOf { session ->
            val ema = session.emaId?.let { emaMap[it] }
            if (ema != null && (ema.anxiety >= 4 || ema.sadness >= 4)) {
                session.commissionErrors
            } else {
                0
            }
        }
    }
    
    /**
     * Detect fatigue based on recent reaction time variability.
     * Returns (fatigueDetected, recentVariabilityPercentage).
     */
    private fun detectFatigue(sessions: List<GameSession>): Pair<Boolean, Double> {
        if (sessions.size < 10) return false to 0.0
        
        val last10Sessions = sessions.takeLast(10)
        val highVariabilitySessions = last10Sessions.count { it.reactionTimeVariability > 100.0 }
        val percentage = (highVariabilitySessions.toDouble() / last10Sessions.size) * 100
        val fatigueDetected = percentage > 40.0
        
        return fatigueDetected to percentage
    }
    
    private fun calculateBaselineComparison(
        sessions: List<GameSession>,
        emaMap: Map<String, EMA>,
    ): BaselineComparisonData {
        val sessionsWithEma = sessions.mapNotNull { session ->
            session.emaId?.let { emaId ->
                session to emaMap[emaId]
            }
        }
        
        val baselineSessions = sessionsWithEma.filter { (_, ema) ->
            ema != null && !ema.hasNegativeEvent && ema.sleepHours >= 6.0
        }
        
        val stressedSessions = sessionsWithEma.filter { (_, ema) ->
            ema != null && (ema.hasNegativeEvent || ema.sleepHours < 6.0)
        }
        
        val hasEnoughData = baselineSessions.size >= 3 && stressedSessions.size >= 3
        
        if (!hasEnoughData) {
            return BaselineComparisonData(
                baselineAverageScore = 0.0,
                stressedAverageScore = 0.0,
                performanceDifference = 0.0,
                hasEnoughData = false,
            )
        }
        
        val baselineAvg = baselineSessions.map { it.first.score }.average()
        val stressedAvg = stressedSessions.map { it.first.score }.average()
        val difference = ((baselineAvg - stressedAvg) / baselineAvg) * 100
        
        return BaselineComparisonData(
            baselineAverageScore = baselineAvg,
            stressedAverageScore = stressedAvg,
            performanceDifference = difference,
            hasEnoughData = true,
        )
    }
}
