package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.EMA

class GetLatestEmaUseCase(private val repository: ClarityRepository) {
    suspend operator fun invoke(): EMA? {
        return repository.getRecentEMA()
    }
}
