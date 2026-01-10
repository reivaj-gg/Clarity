package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.EMA

class SaveEmaUseCase(private val repository: ClarityRepository) {
    suspend operator fun invoke(ema: EMA) {
        // Business Logic: Validate? Calculate baseline here?
        // For now, simple pass-through.
        repository.saveEMA(ema)
    }
}
