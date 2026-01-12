package com.reivaj.clarity.data.repository

import com.reivaj.clarity.domain.model.EMA
import com.reivaj.clarity.domain.model.GameSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An in-memory implementation of [ClarityRepository] for the contest MVP.
 * Data is lost when the app process is killed.
 *
 * Future improvements: Replace with Room or SQLDelight for persistence.
 */
class InMemoryClarityRepository : ClarityRepository {
    private val _emas = MutableStateFlow<List<EMA>>(emptyList())
    private val _sessions = MutableStateFlow<List<GameSession>>(emptyList())

    override suspend fun saveEMA(ema: EMA) {
        _emas.update { it + ema }
    }

    override suspend fun getRecentEMA(): EMA? {
        return _emas.value.maxByOrNull { it.timestamp }
    }

    override fun getAllEMAs(): Flow<List<EMA>> {
        return _emas.asStateFlow()
    }

    override suspend fun saveGameSession(session: GameSession) {
        _sessions.update { it + session }
    }

    override fun getAllGameSessions(): Flow<List<GameSession>> {
        return _sessions.asStateFlow()
    }

    override suspend fun getSessionsWithEMA(): List<Pair<GameSession, EMA?>> {
        val currentEmas = _emas.value
        val currentSessions = _sessions.value
        return currentSessions.map { session ->
            session to currentEmas.find { it.id == session.emaId }
        }
    }

    private val _isCheckInCompleted = MutableStateFlow(false)

    override fun isCheckInCompleted(): Flow<Boolean> {
        return _isCheckInCompleted.asStateFlow()
    }

    override suspend fun setCheckInCompleted(completed: Boolean) {
        _isCheckInCompleted.update { completed }
    }
    
    // AI Coach Chat
    private val _chatMessages = MutableStateFlow<List<com.reivaj.clarity.domain.model.ChatMessage>>(emptyList())
    
    override fun getRecentChatMessages(): Flow<List<com.reivaj.clarity.domain.model.ChatMessage>> {
        return _chatMessages.asStateFlow()
    }
    
    override suspend fun saveChatMessage(message: com.reivaj.clarity.domain.model.ChatMessage) {
        _chatMessages.update { it + message }
    }
    
    override suspend fun clearChatHistory() {
        _chatMessages.value = emptyList()
    }
    
    private val _profilePictureUri = MutableStateFlow<String?>(null)

    override fun getProfilePictureUri(): Flow<String?> = _profilePictureUri.asStateFlow()

    override suspend fun saveProfilePictureUri(uri: String) {
        _profilePictureUri.value = uri
    }

    private val _userName = MutableStateFlow<String?>(null)

    override fun getUserName(): Flow<String?> = _userName.asStateFlow()

    override suspend fun saveUserName(name: String) {
        _userName.value = name
    }

    override suspend fun clearAllData() {
        _emas.value = emptyList()
        _sessions.value = emptyList()
        _chatMessages.value = emptyList()
        _isCheckInCompleted.value = false
    }
}
