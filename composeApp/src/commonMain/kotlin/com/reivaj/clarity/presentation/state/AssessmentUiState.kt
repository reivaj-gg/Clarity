package com.reivaj.clarity.presentation.state

/**
 * Represents the state of the Assessment screen's UI.
 * The data types have been corrected to match the UI components that use them,
 * preventing runtime crashes and simplifying the UI logic.
 *
 * @property mood The current value for the mood slider (1f-10f).
 * @property stress The current value for the stress slider (1f-10f).
 * @property sleepQuality The current value for the sleep quality slider (1f-10f).
 * @property hoursSlept The value for the hours slept slider (0f-24f).
 * @property hasRecentNegativeEvent The boolean flag for recent negative events.
 * @property validationError An optional message describing any data validation errors.
 */
data class AssessmentUiState(
    val mood: Float = 5f,
    val stress: Float = 5f,
    val sleepQuality: Float = 5f,
    val hoursSlept: Float = 8f,
    val hasRecentNegativeEvent: Boolean = false,
    val validationError: String? = null,
)
