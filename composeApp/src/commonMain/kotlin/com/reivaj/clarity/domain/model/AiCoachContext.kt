package com.reivaj.clarity.domain.model

import kotlinx.serialization.Serializable

/**
 * Contextual data gathered from the user's recent activity to inform the AI Coach.
 */
@Serializable
data class AiCoachContext(
    val userName: String,
    val performanceSummary: String, // "Avg Score: 75, Accuracy: 80%"
    val moodSummary: String, // "Recent mood: Anxious (4/5)"
    val sleepSummary: String, // "Avg Sleep: 6.5h"
    val recentActivity: List<String>, // ["Played Go/No-Go: 80 score"]
    val streak: Int,
    val totalSessions: Int
)
