package com.reivaj.clarity.presentation.ema

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.AlcoholUseToday
import com.reivaj.clarity.domain.model.EMA
import com.reivaj.clarity.domain.model.EnvironmentContext
import com.reivaj.clarity.domain.model.PreSessionActivity
import com.reivaj.clarity.domain.model.SocialContext
import com.reivaj.clarity.domain.model.SubstanceType
import com.reivaj.clarity.util.randomUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class EMAViewModel(
    private val repository: ClarityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EMAState())
    val state = _state.asStateFlow()

    // --- Update Handlers ---
    fun onAngerChange(value: Float) = _state.update { it.copy(anger = value) }
    fun onAnxietyChange(value: Float) = _state.update { it.copy(anxiety = value) }
    fun onSadnessChange(value: Float) = _state.update { it.copy(sadness = value) }
    fun onHappinessChange(value: Float) = _state.update { it.copy(happiness = value) }
    fun onStressfulEventChange(value: Boolean) = _state.update { it.copy(recentStressfulEvent = value) }
    fun onSleepHoursChange(value: Float) = _state.update { it.copy(sleepHours = value) }
    fun onSleepQualityChange(value: Float) = _state.update { it.copy(sleepQuality = value) }
    fun onCaffeineChange(value: Boolean) = _state.update { it.copy(caffeineRecent = value) }
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
            val currentState = _state.value
            val ema = EMA(
                id = randomUUID(),
                timestamp = Clock.System.now(),
                anger = currentState.anger.toInt(),
                anxiety = currentState.anxiety.toInt(),
                sadness = currentState.sadness.toInt(),
                happiness = currentState.happiness.toInt(),
                recentStressfulEvent = currentState.recentStressfulEvent,
                sleepHours = currentState.sleepHours.toDouble(),
                sleepQuality = currentState.sleepQuality.toInt(),
                caffeineRecent = currentState.caffeineRecent,
                alcoholUse = currentState.alcoholUse,
                substanceType = currentState.substanceType,
                substanceDescription = currentState.substanceDescription.takeIf { it.isNotBlank() },
                hasPositiveEvent = currentState.hasPositiveEvent,
                positiveEventIntensity = if(currentState.hasPositiveEvent) currentState.positiveEventIntensity.toInt() else null,
                positiveEventDescription = currentState.positiveEventDescription.takeIf { it.isNotBlank() },
                hasNegativeEvent = currentState.hasNegativeEvent,
                negativeEventIntensity = if(currentState.hasNegativeEvent) currentState.negativeEventIntensity.toInt() else null,
                negativeEventDescription = currentState.negativeEventDescription.takeIf { it.isNotBlank() },
                preSessionActivity = currentState.preSessionActivity,
                socialContext = currentState.socialContext,
                environmentContext = currentState.environmentContext
            )
            repository.saveEMA(ema)
            repository.setCheckInCompleted(true) 
            onComplete()
        }
    }
}
