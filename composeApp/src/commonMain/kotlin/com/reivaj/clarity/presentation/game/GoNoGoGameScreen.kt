package com.reivaj.clarity.presentation.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun GoNoGoGameScreen(
    onNavigateHome: () -> Unit,
    viewModel: GameViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!state.isPlaying && !state.isGameOver) {
            // Initial Screen
            Text("Go/No-Go Task", style = MaterialTheme.typography.headlineMedium)
            Text("Tap when you see a ‘+’. Do not tap for ‘x’.", textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(16.dp))
            Button(onClick = viewModel::startGame) {
                Text("Start Game")
            }
        } else if (state.isGameOver) {
            // Game Over Screen
            Text("Game Over!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Final Score: ${state.score}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onNavigateHome) {
                Text("Back to Training")
            }
        } else {
            // Game Play Screen
            Text("Score: ${state.score}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(64.dp))

            GameStimulus(state.currentSymbol, onTap = viewModel::onStimulusResponse)

            Spacer(modifier = Modifier.height(64.dp))

            GameFeedback(state.feedback)
        }
    }
}

@Composable
private fun GameStimulus(symbol: String?, onTap: () -> Unit) {
    val stimulusColor = when (symbol) {
        "+" -> Color.Green
        "x" -> Color.Red
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .size(150.dp)
            .background(Color.DarkGray)
            .clickable(enabled = symbol != null, onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(visible = symbol != null, enter = fadeIn(), exit = fadeOut()) {
            Text(
                text = symbol ?: "",
                fontSize = 80.sp,
                color = stimulusColor,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GameFeedback(feedback: String?) {
    // Use a key to reset the LaunchedEffect when feedback changes
    LaunchedEffect(feedback) {
        if (feedback != null) {
            delay(1000) // Keep feedback on screen for 1 second
            // In a real app, you might want to clear the feedback in the ViewModel
        }
    }

    AnimatedVisibility(visible = feedback != null, enter = fadeIn(), exit = fadeOut()) {
        Text(
            text = feedback ?: "",
            style = MaterialTheme.typography.titleMedium,
            color = if (feedback == "Correct!") Color.Green else Color.Red
        )
    }
}
