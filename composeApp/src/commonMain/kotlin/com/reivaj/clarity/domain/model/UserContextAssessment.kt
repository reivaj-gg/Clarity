package com.reivaj.clarity.domain.model

import kotlinx.datetime.Instant

/**
 * Represents a single Ecological Momentary Assessment (EMA) of the user's current context.
 * This data class captures mood, stress, sleep quality, and recent life events
 * to establish a psychological baseline.
 *
 * All inputs are validated in the init block to ensure data integrity.
 *
 * @property id A unique identifier for this assessment.
 * @property timestamp The exact moment the assessment was recorded.
 * @property mood A subjective rating of current mood on a 5-point Likert scale (1=Very Sad, 5=Very Happy).
 *              Reference: Sanz, J. (2001). EVEA: Escala de valoración del estado de ánimo.
 * @property stress A subjective rating of current stress on a 5-point Likert scale (1=Not Stressed, 5=Very Stressed).
 *              Reference: Brantley, P. J., et al. (2003). Daily Stress Inventory. Journal of Behavioral Medicine.
 * @property sleepQuality A subjective rating of last night's sleep quality (1=Poor, 5=Excellent).
 *              Reference: Buysse, D. J., et al. (1989). Pittsburgh Sleep Quality Index. Psychiatry Research.
 * @property hoursSlept The total hours of sleep from the previous night.
 * @property hasRecentNegativeEvent Indicates if a significant negative event occurred recently.
 */
data class UserContextAssessment(
    val id: String,
    val timestamp: Instant,
    val mood: Int,
    val stress: Int,
    val sleepQuality: Int,
    val hoursSlept: Double,
    val hasRecentNegativeEvent: Boolean,
) {
    /**
     * Validates the constraints for each assessment parameter upon object initialization.
     *
     * @throws IllegalArgumentException if any parameter is outside its valid range.
     */
    init {
        require(mood in 1..5) { "Mood must be between 1 and 5." }
        require(stress in 1..5) { "Stress must be between 1 and 5." }
        require(sleepQuality in 1..5) { "Sleep quality must be between 1 and 5." }
        require(hoursSlept >= 0.0) { "Hours slept cannot be negative." }
    }

    /**
     * Determines if the current assessment qualifies as a "baseline" measurement.
     * A baseline is defined by the absence of recent significant stress and adequate sleep.
     * This is crucial because stress-induced cortisol can impair hippocampal function,
     * and poor sleep degrades cognitive performance.
     *
     * @return `true` if the session is considered a baseline, `false` otherwise.
     */
    fun isBaseline(): Boolean = !hasRecentNegativeEvent && hoursSlept >= 6.0
}
