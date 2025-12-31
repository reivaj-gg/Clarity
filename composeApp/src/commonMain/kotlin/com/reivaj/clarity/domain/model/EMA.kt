package com.reivaj.clarity.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents the intensity of alcohol consumption for the day.
 */
@Serializable
enum class AlcoholUseToday {
    NONE,
    SMALL, // 1-2 drinks
    MODERATE, // 3-4 drinks
    HIGH // 5+ drinks
}

/**
 * Categorizes the type of substance or medication consumed.
 */
@Serializable
enum class SubstanceType {
    NONE,
    PRESCRIBED,
    OTC, // Over-the-counter
    RECREATIONAL
}

/**
 * Describes the activity performed immediately before the session.
 */
@Serializable
enum class PreSessionActivity {
    STUDYING_WORKING,
    PHYSICAL_ACTIVITY,
    RELAXING,
    SOCIAL_MEDIA,
    JUST_WOKE_UP,
    OTHER
}

/**
 * Describes the social setting during the session.
 */
@Serializable
enum class SocialContext {
    ALONE,
    WITH_FAMILY,
    WITH_FRIENDS,
    WITH_COLLEAGUES,
    PUBLIC_STRANGERS,
    OTHER
}

/**
 * Describes the auditory environment.
 */
@Serializable
enum class EnvironmentContext {
    QUIET,
    MODERATE,
    LOUD
}

/**
 * Ecological Momentary Assessment (EMA).
 *
 * This data class captures the user's instantaneous state (mood, context, physiology)
 * before a training session. It serves as the "Independent Variable" for analysis.
 *
 * @param id Unique identifier (UUID).
 * @param timestamp Time of assessment.
 * @param anger Self-reported anger (1-5).
 * @param anxiety Self-reported anxiety (1-5).
 * @param sadness Self-reported sadness (1-5).
 * @param happiness Self-reported happiness (1-5).
 * @param recentStressfulEvent True if a stressful event occurred in the last 2 hours.
 * @param sleepHours Hours slept the previous night.
 * @param sleepQuality Self-reported sleep quality (1-5).
 * @param caffeineRecent True if caffeine was consumed in the last hour.
 * @param alcoholUse Level of alcohol consumption today.
 * @param substanceType Type of other substances consumed.
 * @param hasPositiveEvent True if a positive event occurred recently.
 * @param hasNegativeEvent True if a negative event occurred recently.
 */
@Serializable
data class EMA(
    val id: String, // UUID or random string
    val timestamp: Instant = Clock.System.now(),
    
    // 1-5 scales (Mood)
    val anger: Int,
    val anxiety: Int,
    val sadness: Int,
    val happiness: Int,
    
    val recentStressfulEvent: Boolean, // "Recent stressful event in the last 2 hours"
    
    // Sleep
    val sleepHours: Double, // 0.5 - 12
    val sleepQuality: Int, // 1-5
    
    // Intake
    val caffeineRecent: Boolean, // "Caffeine in the last hour"
    
    // Expanded Substance Use
    val alcoholUse: AlcoholUseToday = AlcoholUseToday.NONE,
    val substanceType: SubstanceType = SubstanceType.NONE,
    val substanceDescription: String? = null,
    
    // Events
    val hasPositiveEvent: Boolean = false,
    val positiveEventIntensity: Int? = null,
    val positiveEventDescription: String? = null,
    
    val hasNegativeEvent: Boolean = false,
    val negativeEventIntensity: Int? = null,
    val negativeEventDescription: String? = null,

    // Context
    val preSessionActivity: PreSessionActivity = PreSessionActivity.OTHER,
    val socialContext: SocialContext = SocialContext.OTHER,
    val environmentContext: EnvironmentContext = EnvironmentContext.MODERATE

) {
    /**
     * Determines if this session is considered "Baseline" (clean state).
     *
     * A baseline session is defined as:
     * - No recent stressful events
     * - Sufficient sleep (>= 6.0 hours)
     * - No alcohol consumption
     */
    val isBaseline: Boolean
        get() = !recentStressfulEvent && sleepHours >= 6.0 && alcoholUse == AlcoholUseToday.NONE
}
