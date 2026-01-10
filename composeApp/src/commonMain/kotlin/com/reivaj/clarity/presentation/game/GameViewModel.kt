package com.reivaj.clarity.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.GameSession
import com.reivaj.clarity.domain.model.GameType
import com.reivaj.clarity.util.randomUUID
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.random.Random

class GameViewModel(
    private val repository: ClarityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GoNoGoState())
    val state = _state.asStateFlow()

    private var gameJob: Job? = null
    private var stimulusTime: Long = 0

    fun startGame() {
        _state.value = GoNoGoState(isPlaying = true)
        gameJob = viewModelScope.launch {
            while (_state.value.rounds < 20 && _state.value.isPlaying) {
                // 1-3 second delay before showing next stimulus
                delay(Random.nextLong(1000, 3000))
                if (!_state.value.isPlaying) break // Check again after delay

                val isGo = Random.nextFloat() < 0.75 // 75% chance of being a "Go" stimulus
                val symbol = if (isGo) "+" else "x"

                _state.update { it.copy(currentSymbol = symbol, isGoStimulus = isGo, feedback = null) }
                stimulusTime = Clock.System.now().toEpochMilliseconds()

                // Stimulus shown for 1 second
                delay(1000)

                // If it was a "Go" stimulus and player did not respond, it's a miss.
                if (_state.value.currentSymbol != null && _state.value.isGoStimulus) {
                    handleIncorrectResponse("Miss!")
                }

                // Clear symbol after response window
                if (_state.value.isPlaying) {
                    _state.update { it.copy(currentSymbol = null, rounds = it.rounds + 1) }
                }
            }
            // Finish game if loop completes
            if (_state.value.isPlaying) {
                finishGame()
            }
        }
    }

    fun onStimulusResponse() {
        if (_state.value.currentSymbol == null) return // Ignore clicks when no stimulus is shown

        val reactionTime = Clock.System.now().toEpochMilliseconds() - stimulusTime

        if (_state.value.isGoStimulus) {
            _state.update {
                it.copy(
                    score = it.score + 10,
                    feedback = "Correct!",
                    currentSymbol = null, // Clear stimulus immediately on correct response
                    totalReactionTime = it.totalReactionTime + reactionTime,
                    reactionCount = it.reactionCount + 1
                )
            }
        } else {
            // Responded to a "No-Go" stimulus, which is incorrect.
            handleIncorrectResponse("Oops!")
        }
    }

    private fun handleIncorrectResponse(feedback: String) {
        _state.update {
            it.copy(
                score = (it.score - 5).coerceAtLeast(0), // Prevent score from going below zero
                feedback = feedback,
                currentSymbol = null // Clear stimulus
            )
        }
    }

    private fun finishGame() {
        gameJob?.cancel()
        val finalState = _state.value
        _state.value = finalState.copy(isPlaying = false, isGameOver = true)

        viewModelScope.launch {
            val avgReaction = if (finalState.reactionCount > 0) finalState.totalReactionTime / finalState.reactionCount else 0L
            val accuracy = if (finalState.rounds > 0) finalState.reactionCount.toFloat() / finalState.rounds else 0f

            val session = GameSession(
                id = randomUUID(),
                timestamp = Clock.System.now(),
                gameType = GameType.GO_NO_GO,
                score = finalState.score,
                reactionTimeMs = avgReaction,
                accuracy = accuracy,
                difficultyLevel = 1 // Placeholder for now
            )
            repository.saveGameSession(session)
        }
    }

    fun stopGame() {
        gameJob?.cancel()
        _state.value = GoNoGoState() // Reset state
    }

    override fun onCleared() {
        super.onCleared()
        gameJob?.cancel()
    }
}
