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

data class SimonGameState(
    val isPlaying: Boolean = false,
    val isGameOver: Boolean = false,
    val sequence: List<Int> = emptyList(), // 0,1,2,3 for 4 colors
    val userStep: Int = 0, // Current index user needs to input
    val activeLight: Int? = null, // Which light is ON
    val isUserInputAllowed: Boolean = false,
    val score: Int = 0,
    val message: String? = null
)

class SimonGameViewModel(
    private val repository: ClarityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SimonGameState())
    val state = _state.asStateFlow()

    fun startGame() {
        _state.update { SimonGameState(isPlaying = true, message = "Watch the sequence") }
        addToSequenceAndPlay()
    }

    private fun addToSequenceAndPlay() {
        viewModelScope.launch {
            val newStep = Random.nextInt(4)
            val newSequence = _state.value.sequence + newStep
            
            _state.update { 
                it.copy(
                    sequence = newSequence, 
                    userStep = 0, 
                    isUserInputAllowed = false,
                    message = "Watch..." 
                ) 
            }
            delay(1000)

            // Playback
            for (step in newSequence) {
                _state.update { it.copy(activeLight = step) }
                delay(600) // Light on duration
                _state.update { it.copy(activeLight = null) }
                delay(200) // Gap
            }

            _state.update { 
                it.copy(
                    isUserInputAllowed = true,
                    message = "Your turn!" 
                ) 
            }
        }
    }

    fun onColorTap(index: Int) {
        val s = _state.value
        if (!s.isUserInputAllowed) return

        // Flash light briefly on tap
        viewModelScope.launch {
             _state.update { it.copy(activeLight = index) }
             delay(200)
             _state.update { it.copy(activeLight = null) }
        }

        val expected = s.sequence[s.userStep]
        if (index == expected) {
            // Correct step
            val nextStep = s.userStep + 1
            if (nextStep >= s.sequence.size) {
                // Round complete
                 _state.update { it.copy(score = s.score + 1, message = "Good job!") }
                 viewModelScope.launch {
                     delay(500)
                     addToSequenceAndPlay()
                 }
            } else {
                _state.update { it.copy(userStep = nextStep) }
            }
        } else {
            // Wrong
            endGame()
        }
    }

    private fun endGame() {
        viewModelScope.launch {
            repository.saveGameSession(
                GameSession(
                    id = Random.nextLong().toString(),
                    gameType = GameType.SIMON_SEQUENCE,
                    difficultyLevel = _state.value.sequence.size,
                    score = _state.value.score,
                    accuracy = 1.0f,
                    reactionTimeMs = 0,
                    emaId = repository.getRecentEMA()?.id
                )
            )
            _state.update { it.copy(isPlaying = false, isGameOver = true, message = "Game Over") }
        }
    }
}
