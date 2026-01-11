package com.reivaj.clarity.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a single piece of actionable feedback or analysis.
 *
 * @param title The short headline.
 * @param description Detailed explanation or recommendation.
 * @param type Category of the insight (POSITIVE, WARNING, etc.)
 * @param relatedMetric Metric source (e.g. "Sleep", "Focus")
 * @param score Confidence or Magnitude (0.0 - 1.0)
 */
@Serializable
data class Insight(
    val title: String,
    val description: String,
    val type: InsightType,
    val relatedMetric: String? = null,
    val score: Double = 0.0
)

enum class InsightType {
    POSITIVE,   // "On Fire!"
    WARNING,    // "Fatigue Detected"
    NEUTRAL,    // "Night Owl"
    TIP,        // "Try drinking water"
    AI_GENERATED // From Gemini
}
