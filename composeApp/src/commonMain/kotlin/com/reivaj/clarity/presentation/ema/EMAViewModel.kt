package com.reivaj.clarity.presentation.ema

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.random.Random

/**
 * State container for the EMA Wizard flow.
 * Holds all user inputs before submission.
 */
data class EMAState(
    val anger: Float = 1f,
    val anxiety: Float = 1f,
    val sadness: Float = 1f,
    val happiness: Float = 3f,
    val recentStressfulEvent: Boolean = false,
    val sleepHours: Float = 7.0f,
    val sleepQuality: Float = 3f,
    val caffeineRecent: Boolean = false,
    
    // New fields
    val alcoholUse: AlcoholUseToday = AlcoholUseToday.NONE,
    val substanceType: SubstanceType = SubstanceType.NONE,
    val substanceDescription: String = "",
    
    val hasPositiveEvent: Boolean = false,
    val positiveEventIntensity: Float = 3f,
    val positiveEventDescription: String = "",
    
    val hasNegativeEvent: Boolean = false,
    val negativeEventIntensity: Float = 3f,
    val negativeEventDescription: String = "",
    
    val preSessionActivity: PreSessionActivity = PreSessionActivity.OTHER,
    val socialContext: SocialContext = SocialContext.OTHER,
    val environmentContext: EnvironmentContext = EnvironmentContext.MODERATE,

    val isSubmitted: Boolean = false
)

/**
 * Manages the state and logic for the Ecological Momentary Assessment (EMA).
 * Handles user inputs from the UI and persists the daily check-in to the repository.
 */
class EMAViewModel(
    private val repository: ClarityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EMAState())
    val state = _state.asStateFlow()

    fun onAngerChange(value: Float) = _state.update { it.copy(anger = value) }
    fun onAnxietyChange(value: Float) = _state.update { it.copy(anxiety = value) }
    fun onSadnessChange(value: Float) = _state.update { it.copy(sadness = value) }
    fun onHappinessChange(value: Float) = _state.update { it.copy(happiness = value) }
    fun onStressfulEventChange(value: Boolean) = _state.update { it.copy(recentStressfulEvent = value) }
    fun onSleepHoursChange(value: Float) = _state.update { it.copy(sleepHours = value) }
    fun onSleepQualityChange(value: Float) = _state.update { it.copy(sleepQuality = value) }
    fun onCaffeineChange(value: Boolean) = _state.update { it.copy(caffeineRecent = value) }
    
    // New Setters
    fun onAlcoholChange(value: AlcoholUseToday) = _state.update { it.copy(alcoholUse = value) }
    fun onSubstanceTypeChange(value: SubstanceType) = _state.update { it.copy(substanceType = value) }
    fun onSubstanceDescriptionChange(value: String) = _state.update { it.copy(substanceDescription = value) }
    
    fun onHasPositiveEventChange(value: Boolean) = _state.update { it.copy(hasPositiveEvent = value) }
    fun onPositiveEventIntensityChange(value: Float) = _state.update { it.copy(positiveEventIntensity = value) }
    fun onPositiveEventDescriptionChange(value: String) = _state.update { it.copy(positiveEventDescription = value) }
    
    fun onHasNegativeEventChange(value: Boolean) = _state.update { it.copy(hasNegativeEvent = value) }
    fun onNegativeEventIntensityChange(value: Float) = _state.update { it.copy(negativeEventIntensity = value) }
    fun onNegativeEventDescriptionChange(value: String) = _state.update { it.copy(negativeEventDescription = value) }
    
    fun onActivityChange(value: PreSessionActivity) = _state.update { it.copy(preSessionActivity = value) }
    fun onSocialContextChange(value: SocialContext) = _state.update { it.copy(socialContext = value) }
    fun onEnvironmentContextChange(value: EnvironmentContext) = _state.update { it.copy(environmentContext = value) }


    fun submitEMA(onComplete: () -> Unit) {
        viewModelScope.launch {
            val s = _state.value
            val ema = EMA(
                id = Random.nextLong().toString(), // Simple ID for now
                timestamp = Clock.System.now(),
                anger = s.anger.toInt(),
                anxiety = s.anxiety.toInt(),
                sadness = s.sadness.toInt(),
                happiness = s.happiness.toInt(),
                recentStressfulEvent = s.recentStressfulEvent,
                sleepHours = s.sleepHours.toDouble(),
                sleepQuality = s.sleepQuality.toInt(),
                caffeineRecent = s.caffeineRecent,
                alcoholUse = s.alcoholUse,
                substanceType = s.substanceType,
                substanceDescription = if (s.substanceType != SubstanceType.NONE) s.substanceDescription else null,
                hasPositiveEvent = s.hasPositiveEvent,
                positiveEventIntensity = if (s.hasPositiveEvent) s.positiveEventIntensity.toInt() else null,
                positiveEventDescription = if (s.hasPositiveEvent) s.positiveEventDescription else null,
                hasNegativeEvent = s.hasNegativeEvent,
                negativeEventIntensity = if (s.hasNegativeEvent) s.negativeEventIntensity.toInt() else null,
                negativeEventDescription = if (s.hasNegativeEvent) s.negativeEventDescription else null,
                preSessionActivity = s.preSessionActivity,
                socialContext = s.socialContext,
                environmentContext = s.environmentContext
            )
            repository.saveEMA(ema)
            repository.setCheckInCompleted(true)
            _state.update { it.copy(isSubmitted = true) }
            onComplete()
        }
    }
}
