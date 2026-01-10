package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.GameSession

class SaveGameSessionUseCase(private val repository: ClarityRepository) {
    suspend operator fun invoke(session: GameSession) {
        repository.saveGameSession(session)
    }
}
