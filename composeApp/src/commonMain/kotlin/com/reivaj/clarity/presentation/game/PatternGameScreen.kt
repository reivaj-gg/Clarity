package com.reivaj.clarity.presentation.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun PatternGameScreen(
    onNavigateHome: () -> Unit,
    viewModel: PatternGameViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    if (!state.isPlaying && !state.isGameOver) {
         // Intro
         Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
             Column(horizontalAlignment = Alignment.CenterHorizontally) {
                 Text("Pattern Grid", style = MaterialTheme.typography.headlineLarge)
                 Text("Memorize the highlighted cells", style = MaterialTheme.typography.bodyLarge)
                 Spacer(Modifier.height(32.dp))
                 Button(onClick = viewModel::startGame) { Text("Start Game") }
             }
         }
    } else if (state.isGameOver) {
        // Game Over
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
             Column(horizontalAlignment = Alignment.CenterHorizontally) {
                 Text("Game Over", style = MaterialTheme.typography.headlineLarge)
                 Text("Score: ${state.score}", style = MaterialTheme.typography.headlineMedium)
                 Spacer(Modifier.height(32.dp))
                 Button(onClick = onNavigateHome) { Text("Back to Menu") }
             }
         }
    } else {
        // Game Board
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Score: ${state.score} | Level: ${state.level} | Lives: ${state.lives}")
            Spacer(Modifier.height(16.dp))
             Text(state.message ?: "", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(32.dp))

            val gridSize = state.gridSize
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridSize),
                modifier = Modifier.wrapContentSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(gridSize * gridSize) { index ->
                    val isPattern = state.pattern.contains(index)
                    val isSelected = state.userSelection.contains(index)
                    
                    // Determine Color
                    // If PREVIEW: Show pattern in Blue
                    // If INPUT: Show selected in Blue
                    // If FEEDBACK: Show pattern (Green if correct, missed?), show wrong selection (Red)
                    // Simplified feedback for now:
                    
                    val targetColor = when (state.phase) {
                        PatternGamePhase.PREVIEW -> if (isPattern) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        PatternGamePhase.INPUT -> if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        PatternGamePhase.FEEDBACK -> {
                            // Show correct pattern
                            if (isPattern) Color.Green 
                            else if (isSelected) Color.Red // Wrongly selected
                            else MaterialTheme.colorScheme.surfaceVariant
                        }
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    
                    val animatedColor by animateColorAsState(targetColor, animationSpec = tween(300))

                    Box(
                        modifier = Modifier
                            .size(60.dp) // Fixed size or adaptive? fixed for simplicity
                            .clip(RoundedCornerShape(8.dp))
                            .background(animatedColor)
                            .clickable(
                                enabled = state.phase == PatternGamePhase.INPUT && !isSelected
                            ) { viewModel.onCellClick(index) }
                    )
                }
            }
        }
    }
}
