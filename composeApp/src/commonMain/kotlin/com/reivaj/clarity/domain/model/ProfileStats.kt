package com.reivaj.clarity.domain.model

import com.reivaj.clarity.domain.model.GameType
import kotlinx.datetime.Instant

/**
 * Statistics for the user's profile.
 *
 * Contains aggregated metrics about the user's activity and performance.
 */
data class ProfileStats(
    val totalSessions: Int,
    val totalEMAs: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val averageScore: Double,
    val favoriteGame: GameType?,
    val firstSessionDate: Instant?,
)
