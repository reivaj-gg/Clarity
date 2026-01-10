package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.GameType
import com.reivaj.clarity.domain.model.ProfileStats
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

/**
 * Use case to calculate user profile statistics.
 *
 * Computes aggregated metrics including streaks, average score, and favorite game.
 */
class GetProfileStatsUseCase(
    private val repository: ClarityRepository,
) {
    suspend operator fun invoke(): ProfileStats {
        val sessions = repository.getAllGameSessions().first()
        val emas = repository.getAllEMAs().first()
        
        if (sessions.isEmpty()) {
            return ProfileStats(
                totalSessions = 0,
                totalEMAs = emas.size,
                currentStreak = 0,
                longestStreak = 0,
                averageScore = 0.0,
                favoriteGame = null,
                firstSessionDate = null,
            )
        }
        
        // Calculate average score
        val avgScore = sessions.map { it.score }.average()
        
        // Find favorite game (most played)
        val favoriteGame = sessions
            .groupBy { it.gameType }
            .maxByOrNull { it.value.size }
            ?.key
        
        // Calculate streaks
        val (currentStreak, longestStreak) = calculateStreaks(sessions.map { it.timestamp })
        
        return ProfileStats(
            totalSessions = sessions.size,
            totalEMAs = emas.size,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            averageScore = avgScore,
            favoriteGame = favoriteGame,
            firstSessionDate = sessions.minOfOrNull { it.timestamp },
        )
    }
    
    /**
     * Calculates current and longest streaks based on session timestamps.
     *
     * @return Pair of (currentStreak, longestStreak)
     */
    private fun calculateStreaks(timestamps: List<kotlinx.datetime.Instant>): Pair<Int, Int> {
        if (timestamps.isEmpty()) return 0 to 0
        
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        
        // Group sessions by date and get unique dates
        val sessionDates = timestamps
            .map { it.toLocalDateTime(timeZone).date }
            .distinct()
            .sorted()
        
        if (sessionDates.isEmpty()) return 0 to 0
        
        // Calculate current streak
        var currentStreak = 0
        val lastSessionDate = sessionDates.last()
        
        // Check if streak is active (last session was today or yesterday)
        val daysSinceLastSession = today.toEpochDays() - lastSessionDate.toEpochDays()
        
        if (daysSinceLastSession <= 1) {
            // Count backwards from most recent date
            currentStreak = 1
            for (i in sessionDates.size - 2 downTo 0) {
                val daysDiff = sessionDates[i + 1].toEpochDays() - sessionDates[i].toEpochDays()
                if (daysDiff.toLong() == 1L) {
                    currentStreak++
                } else {
                    break
                }
            }
        }
        
        // Calculate longest streak
        var maxStreak = 1
        var tempStreak = 1
        
        for (i in 1 until sessionDates.size) {
            val daysDiff = sessionDates[i].toEpochDays() - sessionDates[i - 1].toEpochDays()
            
            if (daysDiff.toLong() == 1L) {
                tempStreak++
                maxStreak = maxOf(maxStreak, tempStreak)
            } else {
                tempStreak = 1
            }
        }
        
        return currentStreak to maxStreak
    }
}
