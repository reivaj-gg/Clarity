package com.reivaj.clarity.presentation.ema

import com.reivaj.clarity.domain.model.AlcoholUseToday
import com.reivaj.clarity.domain.model.EnvironmentContext
import com.reivaj.clarity.domain.model.PreSessionActivity
import com.reivaj.clarity.domain.model.SocialContext
import com.reivaj.clarity.domain.model.SubstanceType

/**
 * Represents the complete UI state for the multi-step EMA questionnaire.
 */
data class EMAState(
    val anger: Float = 3f,
    val anxiety: Float = 3f,
    val sadness: Float = 3f,
    val happiness: Float = 3f,
    val recentStressfulEvent: Boolean = false,
    val sleepHours: Float = 8f,
    val sleepQuality: Float = 3f,
    val caffeineRecent: Boolean = false,
    val alcoholUse: AlcoholUseToday = AlcoholUseToday.NONE,
    val substanceType: SubstanceType = SubstanceType.NONE,
    val substanceDescription: String = "",
    val hasPositiveEvent: Boolean = false,
    val positiveEventIntensity: Float = 3f,
    val positiveEventDescription: String = "",
    val hasNegativeEvent: Boolean = false,
    val negativeEventIntensity: Float = 3f,
    val negativeEventDescription: String = "",
    val preSessionActivity: PreSessionActivity = PreSessionActivity.RELAXING,
    val socialContext: SocialContext = SocialContext.ALONE,
    val environmentContext: EnvironmentContext = EnvironmentContext.QUIET,
)
