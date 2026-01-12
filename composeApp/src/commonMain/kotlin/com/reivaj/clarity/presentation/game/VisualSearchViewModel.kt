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
import com.reivaj.clarity.domain.util.SoundManager
import com.reivaj.clarity.domain.util.SoundType

/**
 * State for the Visual Search game.
 * @property items The grid of symbols currently displayed.
 * @property target The specific symbol the user must find.
 * @property timeLeft Seconds remaining in the session.
 */
data class SearchGameState(
    val isPlaying: Boolean = false,
    val isGameOver: Boolean = false,
    val items: List<String> = emptyList(), // Symbols/Chars to display
    val target: String = "T",
    val gridSize: Int = 4,
    val score: Int = 0,
    val timeLeft: Int = 30
)

/**
 * ViewModel for the Visual Search attention task.
 *
 * Logic:
 * - Generates a grid of distractor symbols with exactly one unique target.
 * - Runs a countdown timer.
 * - Handles correct taps (points + time bonus) and incorrect taps (time penalty).
 */
class VisualSearchViewModel(
    private val repository: ClarityRepository,
    private val soundManager: SoundManager
) : ViewModel() {

    private val _state = MutableStateFlow(SearchGameState())
    val state = _state.asStateFlow()
    
    // Symbols: Target "T", Distractors "L", "I", "7", "1"
    private val distractors = listOf("L", "I", "7", "1", "F", "E")
    private val target = "T"

    fun startGame() {
        soundManager.playSound(SoundType.CLICK)
        _state.update { SearchGameState(isPlaying = true) }
        nextRound()
        startTimeLoop()
    }

    private fun nextRound() {
        val size = 4 + (_state.value.score / 5) // Increase size every 5 points
        val totalCells = size * size
        val items = MutableList(totalCells) { distractors.random() }
        
        // Place target
        val targetIndex = Random.nextInt(totalCells)
        items[targetIndex] = target
        
        _state.update { 
            it.copy(
                gridSize = size,
                items = items,
                target = target
            )
        }
    }

    private fun startTimeLoop() {
        viewModelScope.launch {
            while (_state.value.timeLeft > 0 && _state.value.isPlaying) {
                delay(1000)
                _state.update { it.copy(timeLeft = it.timeLeft - 1) }
            }
            if (_state.value.isPlaying) {
                endGame()
            }
        }
    }

    fun onItemClick(symbol: String) {
        if (symbol == target) {
            // Correct
            soundManager.playSound(SoundType.CORRECT)
            _state.update { it.copy(score = it.score + 1, timeLeft = it.timeLeft + 2) } // Bonus time
            nextRound()
        } else {
            // Wrong - penalize time?
             soundManager.playSound(SoundType.WRONG)
             _state.update { it.copy(timeLeft = maxOf(0, it.timeLeft - 5)) }
        }
    }

    private fun endGame() {
        soundManager.playSound(SoundType.GAME_OVER)
        viewModelScope.launch {
            repository.saveGameSession(
                GameSession(
                    id = Random.nextLong().toString(),
                    gameType = GameType.VISUAL_SEARCH,
                    difficultyLevel = 1,
                    score = _state.value.score,
                    accuracy = 1.0f,
                    reactionTimeMs = 0,
                    emaId = repository.getRecentEMA()?.id
                )
            )
            _state.update { it.copy(isPlaying = false, isGameOver = true) }
        }
    }
}
