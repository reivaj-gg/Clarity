package com.reivaj.clarity.presentation.train

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.usecase.IsCheckInCompleteUseCase
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
    isCheckInCompleteUseCase: IsCheckInCompleteUseCase
) : ViewModel() {

    val isCheckInCompleted: StateFlow<Boolean> = isCheckInCompleteUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}
