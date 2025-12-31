package com.reivaj.clarity.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.GameSession
import com.reivaj.clarity.domain.model.GameType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.random.Random

/**
 * State container for the Go/No-Go game.
 * @property isPlaying True if the game loop is active.
 * @property currentSymbol The text/symbol currently displayed.
 * @property isGoStimulus True if the current symbol requires a reaction (Go) or inhibition (No-Go).
 * @property score Current score.
 * @property rounds Number of rounds completed.
 * @property maxRounds Total rounds in the session.
 * @property feedback Result message (Correct/Missed) for the last round.
 * @property isGameOver True when the session is finished.
 */
data class GoNoGoState(
    val isPlaying: Boolean = false,
    val currentSymbol: String? = null, // "GO" or "NO-GO" (or shapes)
    val isGoStimulus: Boolean = false,
    val score: Int = 0,
    val rounds: Int = 0,
    val maxRounds: Int = 20,
    val feedback: String? = null, // "Correct!", "Missed!", "Wrong!"
    val isGameOver: Boolean = false
)

/**
 * ViewModel managing the "Go/No-Go" cognitive task.
 *
 * Logic:
 * - Presents stimuli at random intervals.
 * - Tracks reaction time (Go trials) and errors (No-Go taps or Go misses).
 * - Saves the result to the repository upon completion.
 */
class GameViewModel(
    private val repository: ClarityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GoNoGoState())
    val state = _state.asStateFlow()

    private var currentReactionStartTime: Long = 0
    private var totalReactionTime: Long = 0
    private var reactionCount: Int = 0

    fun startGame() {
        _state.update { GoNoGoState(isPlaying = true) }
        runGameLoop()
    }

    private fun runGameLoop() {
        viewModelScope.launch {
            while (_state.value.rounds < _state.value.maxRounds && _state.value.isPlaying) {
                _state.update { it.copy(feedback = null, currentSymbol = null) }
                delay(Random.nextLong(1000, 2000)) // Inter-stimulus interval

                val isGo = Random.nextBoolean()
                val symbol = if (isGo) "🟢 GO" else "🔴 NO CHANGE" // Simple text/emoji for now
                
                currentReactionStartTime = Clock.System.now().toEpochMilliseconds()
                
                _state.update { 
                    it.copy(
                        currentSymbol = symbol, 
                        isGoStimulus = isGo, 
                        rounds = it.rounds + 1
                    ) 
                }

                delay(1500) // Time to respond
                
                // If user didn't tap and it was GO: Miss
                // If user didn't tap and it was NO-GO: Correct (handled in next loop start or implicit logic?)
                // Actually, let's process result if no tap occurred
                val currentState = _state.value
                if (currentState.currentSymbol != null) {
                   // Time expired for this stimulus
                   if (currentState.isGoStimulus) {
                       _state.update { it.copy(feedback = "Missed!", currentSymbol = null) }
                   } else {
                        // Correct rejection
                       _state.update { it.copy(feedback = "Good stay!", score = it.score + 10, currentSymbol = null) }
                   }
                }
            }
            endGame()
        }
    }

    fun onUserTap() {
        if (!_state.value.isPlaying || _state.value.currentSymbol == null) return

        val now = Clock.System.now().toEpochMilliseconds()
        val reactionTime = now - currentReactionStartTime
        val isGo = _state.value.isGoStimulus

        if (isGo) {
            totalReactionTime += reactionTime
            reactionCount++
            _state.update { it.copy(feedback = "Nice! ${reactionTime}ms", score = it.score + 20, currentSymbol = null) }
        } else {
            _state.update { it.copy(feedback = "Oops! Should have waited.", score = it.score - 10, currentSymbol = null) }
        }
    }

    private fun endGame() {
        val avgReaction = if (reactionCount > 0) totalReactionTime / reactionCount else 0L
        val finalScore = _state.value.score
        
        viewModelScope.launch {
            val session = GameSession(
                id = Random.nextLong().toString(),
                gameType = GameType.GO_NO_GO,
                difficultyLevel = 1,
                score = finalScore,
                accuracy = 1.0f, // TODO: calculate real accuracy
                reactionTimeMs = avgReaction,
                emaId = repository.getRecentEMA()?.id
            )
            repository.saveGameSession(session)
            _state.update { it.copy(isPlaying = false, isGameOver = true) }
        }
    }
}
