package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.domain.model.GameSession
import com.reivaj.clarity.domain.model.PerformanceScoreBreakdown
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Calculates a performance score (0-100) based on user's cognitive training data.
 *
 * Formula breakdown:
 * - Accuracy: 50% weight (0-50 points)
 * - Streak: 20% weight (0-20 points)
 * - Improvement: 20% weight (0-20 points)
 * - Game Variety: 10% weight (0-10 points)
 */
class PerformanceScoreCalculator {
    
    /**
     * Calculate overall performance score.
     *
     * @param sessions List of game sessions in the report period
     * @param currentStreak Current consecutive day streak
     * @return Pair of (total score 0-100, breakdown details)
     */
    fun calculate(
        sessions: List<GameSession>,
        currentStreak: Int,
    ): Pair<Int, PerformanceScoreBreakdown> {
        if (sessions.isEmpty()) {
            return 0 to PerformanceScoreBreakdown(
                accuracyScore = 0,
                streakScore = 0,
                improvementScore = 0,
                varietyScore = 0,
                improvementPercent = 0.0,
                gamesPlayed = 0,
            )
        }
        
        // 1. Accuracy Score (0-50 points)
        // 100% accuracy = 50 points, 0% = 0 points
        val avgAccuracy = sessions.map { it.accuracy }.average()
        val accuracyScore = (avgAccuracy * 50).roundToInt()
        
        // 2. Streak Score (0-20 points)
        // 0 days = 0, 7 days = 10, 14+ days = 20
        val streakScore = when {
            currentStreak >= 14 -> 20
            currentStreak >= 7 -> 10 + ((currentStreak - 7) * 10 / 7)
            else -> (currentStreak * 10 / 7)
        }.coerceIn(0, 20)
        
        // 3. Improvement Score (0-20 points)
        // Compare first half avg vs second half avg
        val improvementPercent = calculateImprovementPercent(sessions)
        val improvementScore = when {
            improvementPercent >= 10.0 -> 20
            improvementPercent >= 5.0 -> 15
            improvementPercent >= 0.0 -> 10
            improvementPercent >= -5.0 -> 5
            else -> 0
        }
        
        // 4. Game Variety Score (0-10 points)
        // 1 game = 2.5, 2 games = 5, 3 games = 7.5, 4 games = 10
        val uniqueGames = sessions.map { it.gameType }.distinct().size
        val varietyScore = min(uniqueGames * 2.5, 10.0).roundToInt()
        
        val totalScore = (accuracyScore + streakScore + improvementScore + varietyScore)
            .coerceIn(0, 100)
        
        return totalScore to PerformanceScoreBreakdown(
            accuracyScore = accuracyScore,
            streakScore = streakScore,
            improvementScore = improvementScore,
            varietyScore = varietyScore,
            improvementPercent = improvementPercent,
            gamesPlayed = uniqueGames,
        )
    }
    
    private fun calculateImprovementPercent(sessions: List<GameSession>): Double {
        if (sessions.size < 4) return 0.0
        
        val sorted = sessions.sortedBy { it.timestamp }
        val halfPoint = sorted.size / 2
        
        val firstHalfAvg = sorted.take(halfPoint).map { it.score }.average()
        val secondHalfAvg = sorted.drop(halfPoint).map { it.score }.average()
        
        if (firstHalfAvg == 0.0) return 0.0
        
        return ((secondHalfAvg - firstHalfAvg) / firstHalfAvg) * 100
    }
}
