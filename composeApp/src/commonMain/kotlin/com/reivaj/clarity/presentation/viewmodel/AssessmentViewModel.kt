package com.reivaj.clarity.presentation.viewmodel

import com.reivaj.clarity.domain.model.UserContextAssessment
import com.reivaj.clarity.domain.repository.AssessmentRepository
import com.reivaj.clarity.presentation.state.AssessmentUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * ViewModel for the Assessment screen.
 * This ViewModel now manages its own CoroutineScope, decoupling it from the UI layer.
 *
 * @property repository The repository for data operations.
 */
class AssessmentViewModel(
    private val repository: AssessmentRepository,
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _uiState = MutableStateFlow(AssessmentUiState())
    val uiState: StateFlow<AssessmentUiState> = _uiState.asStateFlow()

    /**
     * Updates the mood value in the UI state.
     */
    fun onMoodChanged(mood: Float) {
        _uiState.update { it.copy(mood = mood, validationError = null) }
    }

    /**
     * Updates the stress value in the UI state.
     */
    fun onStressChanged(stress: Float) {
        _uiState.update { it.copy(stress = stress, validationError = null) }
    }

    /**
     * Updates the sleep quality value in the UI state.
     */
    fun onSleepQualityChanged(sleepQuality: Float) {
        _uiState.update { it.copy(sleepQuality = sleepQuality, validationError = null) }
    }

    /**
     * Updates the hours slept value in the UI state.
     */
    fun onHoursSleptChanged(hours: Float) {
        _uiState.update { it.copy(hoursSlept = hours, validationError = null) }
    }

    /**
     * Updates the recent negative event flag in the UI state.
     */
    fun onHasRecentNegativeEventChanged(hasEvent: Boolean) {
        _uiState.update { it.copy(hasRecentNegativeEvent = hasEvent) }
    }

    /**
     * Validates the current UI state and attempts to submit the assessment.
     */
    fun submitAssessment() {
        val currentState = _uiState.value

        // Validation is simplified as the types are already correct.
        val assessment = UserContextAssessment(
            id = "ema-" + Clock.System.now().toEpochMilliseconds(), // Simple unique ID
            timestamp = Clock.System.now(),
            mood = currentState.mood.toInt(),
            stress = currentState.stress.toInt(),
            sleepQuality = currentState.sleepQuality.toInt(),
            hoursSlept = currentState.hoursSlept.toDouble(),
            hasRecentNegativeEvent = currentState.hasRecentNegativeEvent,
        )

        viewModelScope.launch {
            repository.submitAssessment(assessment)
                .onFailure {
                    _uiState.update { state ->
                        state.copy(validationError = "Submission failed: ${it.message}")
                    }
                }
                .onSuccess {
                    // Reset state to default values for the next entry.
                    _uiState.value = AssessmentUiState()
                }
        }
    }
}
