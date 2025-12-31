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

data class SearchGameState(
    val isPlaying: Boolean = false,
    val isGameOver: Boolean = false,
    val items: List<String> = emptyList(), // Symbols/Chars to display
    val target: String = "T",
    val gridSize: Int = 4,
    val score: Int = 0,
    val timeLeft: Int = 30
)

class VisualSearchViewModel(
    private val repository: ClarityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchGameState())
    val state = _state.asStateFlow()
    
    // Symbols: Target "T", Distractors "L", "I", "7", "1"
    private val distractors = listOf("L", "I", "7", "1", "F", "E")
    private val target = "T"

    fun startGame() {
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
            _state.update { it.copy(score = it.score + 1, timeLeft = it.timeLeft + 2) } // Bonus time
            nextRound()
        } else {
            // Wrong - penalize time?
             _state.update { it.copy(timeLeft = maxOf(0, it.timeLeft - 5)) }
        }
    }

    private fun endGame() {
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
