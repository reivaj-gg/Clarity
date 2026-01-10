package com.reivaj.clarity.presentation.game

/**
 * Represents the UI state for the Go/No-Go game.
 */
data class GoNoGoState(
    val isPlaying: Boolean = false,
    val isGameOver: Boolean = false,
    val score: Int = 0,
    val rounds: Int = 0,
    val currentSymbol: String? = null,
    val isGoStimulus: Boolean = false,
    val feedback: String? = null,
    val totalReactionTime: Long = 0L,
    val reactionCount: Int = 0
)
