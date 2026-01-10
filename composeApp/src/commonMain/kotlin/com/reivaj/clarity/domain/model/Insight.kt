package com.reivaj.clarity.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents an analytical insight generated from user data.
 *
 * @param title The short headline of the insight.
 * @param description Detailed explanation drawn from data correlation.
 * @param confidence A value between 0.0 and 1.0 indicating statistical strength of the insight.
 */
@Serializable
data class Insight(
    val title: String,
    val description: String,
    val confidence: Float // 0.0 - 1.0
)
