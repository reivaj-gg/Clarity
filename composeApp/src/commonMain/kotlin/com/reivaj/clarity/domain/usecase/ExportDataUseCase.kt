package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.repository.ClarityRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Use case to export all user data as JSON.
 *
 * Retrieves all EMA and GameSession data and serializes to JSON format.
 */
class ExportDataUseCase(
    private val repository: ClarityRepository,
) {
    suspend operator fun invoke(): String {
        val emas = repository.getAllEMAs().first()
        val sessions = repository.getAllGameSessions().first()
        
        val data = mapOf(
            "emas" to emas,
            "sessions" to sessions,
            "exportTimestamp" to kotlinx.datetime.Clock.System.now().toString(),
        )
        
        return Json {
            prettyPrint = true
        }.encodeToString(data)
    }
}
