package com.reivaj.clarity.presentation.train

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reivaj.clarity.data.repository.ClarityRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the Training Hub (TrainScreen).
 *
 * Responsibilities:
 * - Monitors daily check-in status ([isCheckInCompleted]).
 * - Controls the "Lock" state of cognitive games.
 */
class TrainViewModel(
    private val repository: ClarityRepository
) : ViewModel() {

    val isCheckInCompleted: StateFlow<Boolean> = repository.isCheckInCompleted()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}
