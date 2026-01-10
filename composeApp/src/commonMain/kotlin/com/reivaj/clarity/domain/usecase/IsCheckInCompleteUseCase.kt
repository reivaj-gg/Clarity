package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.repository.ClarityRepository
import kotlinx.coroutines.flow.Flow

class IsCheckInCompleteUseCase(private val repository: ClarityRepository) {
    operator fun invoke(): Flow<Boolean> {
        return repository.isCheckInCompleted()
    }
}
