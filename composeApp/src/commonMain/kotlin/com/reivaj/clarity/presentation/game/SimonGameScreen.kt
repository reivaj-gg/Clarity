package com.reivaj.clarity.presentation.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun SimonGameScreen(
    onNavigateHome: () -> Unit,
    viewModel: SimonGameViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    if (!state.isPlaying && !state.isGameOver) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Simon Sequence", style = MaterialTheme.typography.headlineLarge)
                Text("Repeat the sequence of lights", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(32.dp))
                Button(onClick = viewModel::startGame) { Text("Start Game") }
            }
        }
    } else if (state.isGameOver) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Game Over", style = MaterialTheme.typography.headlineLarge)
                Text("Score: ${state.score}", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(32.dp))
                Button(onClick = onNavigateHome) { Text("Back to Menu") }
            }
        }
    } else {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Score: ${state.score}", style = MaterialTheme.typography.titleLarge)
             Spacer(Modifier.height(16.dp))
            Text(state.message ?: "", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(32.dp))

            // Simon Pads (2x2 grid)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SimonButton(color = Color.Red, isLit = state.activeLight == 0, onClick = { viewModel.onColorTap(0) })
                SimonButton(color = Color.Green, isLit = state.activeLight == 1, onClick = { viewModel.onColorTap(1) })
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SimonButton(color = Color.Blue, isLit = state.activeLight == 2, onClick = { viewModel.onColorTap(2) })
                SimonButton(color = Color.Yellow, isLit = state.activeLight == 3, onClick = { viewModel.onColorTap(3) })
            }
        }
    }
}

@Composable
fun SimonButton(
    color: Color,
    isLit: Boolean,
    onClick: () -> Unit
) {
    val displayColor by animateColorAsState(
        targetValue = if (isLit) color else color.copy(alpha = 0.3f),
        animationSpec = tween(150)
    )

    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(CircleShape) // Or RoundedCorner
            .background(displayColor)
            .clickable { onClick() }
    )
}
