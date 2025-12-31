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
import kotlin.random.Random

data class PatternGameState(
    val isPlaying: Boolean = false,
    val isGameOver: Boolean = false,
    val gridSize: Int = 3, // Start with 3x3
    val pattern: Set<Int> = emptySet(), // Indices of highlighted cells
    val userSelection: Set<Int> = emptySet(),
    val phase: PatternGamePhase = PatternGamePhase.IDLE, // PREVIEW, INPUT, FEEDBACK
    val score: Int = 0,
    val level: Int = 1,
    val lives: Int = 3,
    val message: String? = null
)

enum class PatternGamePhase { IDLE, PREVIEW, INPUT, FEEDBACK }

class PatternGameViewModel(
    private val repository: ClarityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PatternGameState())
    val state = _state.asStateFlow()

    fun startGame() {
        _state.update { PatternGameState(isPlaying = true, phase = PatternGamePhase.IDLE) }
        startLevel()
    }

    private fun startLevel() {
        viewModelScope.launch {
            val currentLevel = _state.value.level
            // Scale difficulty:
            // Level 1-2: 3x3 grid, 3 cells
            // Level 3-5: 4x4 grid, 4-5 cells
            // Level 6+: 5x5 grid, 6+ cells
            val size = when {
                currentLevel < 3 -> 3
                currentLevel < 6 -> 4
                else -> 5
            }
            val cellsToHighlight = currentLevel + 2 // Increase items to remember
            
            // Generate pattern
            val totalCells = size * size
            val newPattern = mutableSetOf<Int>()
            while (newPattern.size < cellsToHighlight) {
                newPattern.add(Random.nextInt(totalCells))
            }

            _state.update { 
                it.copy(
                    gridSize = size,
                    pattern = newPattern,
                    userSelection = emptySet(),
                    phase = PatternGamePhase.PREVIEW,
                    message = "Watch the pattern..."
                )
            }

            delay(2000) // Show pattern for 2 seconds

            _state.update {
                it.copy(
                    phase = PatternGamePhase.INPUT,
                    message = "Reproduce the pattern!"
                )
            }
        }
    }

    fun onCellClick(index: Int) {
        val s = _state.value
        if (s.phase != PatternGamePhase.INPUT) return

        if (s.userSelection.contains(index)) return // Already selected

        val newSelection = s.userSelection + index
        _state.update { it.copy(userSelection = newSelection) }

        // Check if finished selection
        if (newSelection.size == s.pattern.size) {
            validateSelection(newSelection, s.pattern)
        }
    }

    private fun validateSelection(userSelection: Set<Int>, targetPattern: Set<Int>) {
        viewModelScope.launch {
            if (userSelection == targetPattern) {
                // Correct
                _state.update { 
                    it.copy(
                        phase = PatternGamePhase.FEEDBACK, 
                        score = it.score + (it.level * 10),
                        message = "Correct!",
                        level = it.level + 1
                    ) 
                }
                delay(1000)
                startLevel()
            } else {
                // Wrong
                _state.update {
                    it.copy(
                        phase = PatternGamePhase.FEEDBACK,
                        lives = it.lives - 1,
                        message = "Wrong pattern!"
                    )
                }
                delay(1000)
                if (_state.value.lives <= 0) {
                    endGame()
                } else {
                    // Retry same level or show again? Let's show new pattern same level
                    startLevel()
                }
            }
        }
    }

    private fun endGame() {
         viewModelScope.launch {
            repository.saveGameSession(
                GameSession(
                    id = Random.nextLong().toString(),
                    gameType = GameType.VISUOSPATIAL_GRID,
                    difficultyLevel = _state.value.level,
                    score = _state.value.score,
                    accuracy = 1.0f, // Simplified
                    reactionTimeMs = 0,
                    emaId = repository.getRecentEMA()?.id
                )
            )
            _state.update { it.copy(isPlaying = false, isGameOver = true) }
        }
    }
}
