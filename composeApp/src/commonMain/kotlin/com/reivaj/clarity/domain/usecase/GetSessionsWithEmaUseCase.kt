package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.EMA
import com.reivaj.clarity.domain.model.GameSession

class GetSessionsWithEmaUseCase(private val repository: ClarityRepository) {
    suspend operator fun invoke(): List<Pair<GameSession, EMA?>> {
        return repository.getSessionsWithEMA()
    }
}
