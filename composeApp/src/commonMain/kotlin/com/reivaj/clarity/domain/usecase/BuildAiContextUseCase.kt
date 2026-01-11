package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.AiCoachContext
import com.reivaj.clarity.domain.model.GameSession
import com.reivaj.clarity.domain.model.EMA

class BuildAiContextUseCase(
    private val repository: ClarityRepository
) {
    suspend operator fun invoke(): AiCoachContext {
        val allData = repository.getSessionsWithEMA()
        
        // Take last 20 sessions for analysis
        val recentData = allData.sortedByDescending { it.first.timestamp }.take(20)
        
        val totalSessions = allData.size
        
        // Calculate streak (simplified)
        // In a real app, we'd check consecutive days. For now, just a place holder or simple count.
        val streak = calculateStreak(allData.map { it.first.timestamp.toEpochMilliseconds() })

        // Summaries
        val perfSummary = calculatePerformance(recentData.map { it.first })
        val moodSummary = calculateMood(recentData.mapNotNull { it.second })
        val sleepSummary = calculateSleep(recentData.mapNotNull { it.second })
        
        val recentActivity = recentData.take(5).map { (session, _) ->
            "${session.gameType.name}: ${session.score} pts"
        }

        return AiCoachContext(
            userName = "User", // TODO: Get from preferences
            performanceSummary = perfSummary,
            moodSummary = moodSummary,
            sleepSummary = sleepSummary,
            recentActivity = recentActivity,
            streak = streak,
            totalSessions = totalSessions
        )
    }

    private fun calculatePerformance(sessions: List<GameSession>): String {
        if (sessions.isEmpty()) return "No recent games."
        val avgScore = sessions.map { it.score }.average().toInt()
        val accuracy = sessions.mapNotNull { it.accuracy }.average()
        val accString = if (!accuracy.isNaN()) "${(accuracy * 100).round(1)}%" else "N/A"
        return "Avg Score: $avgScore, Avg Accuracy: $accString"
    }

    private fun calculateMood(emas: List<EMA>): String {
        if (emas.isEmpty()) return "No recent mood data."
        val avgHappiness = emas.map { it.happiness }.average()
        val avgStress = emas.map { if (it.recentStressfulEvent) 1.0 else 0.0 }.average()
        return "Avg Happiness: ${avgHappiness.round(1)}/5, Stress Freq: ${(avgStress * 100).round(0)}%"
    }

    private fun calculateSleep(emas: List<EMA>): String {
        if (emas.isEmpty()) return "No recent sleep data."
        val avgSleep = emas.map { it.sleepHours }.average()
        val avgQuality = emas.map { it.sleepQuality }.average()
        return "Avg Sleep: ${avgSleep.round(1)}h, Quality: ${avgQuality.round(1)}/5"
    }
    
    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
    
    private fun calculateStreak(timestamps: List<Long>): Int {
        // Simplified placeholder
        if (timestamps.isEmpty()) return 0
        return 1 // At least today if played
    }
}
