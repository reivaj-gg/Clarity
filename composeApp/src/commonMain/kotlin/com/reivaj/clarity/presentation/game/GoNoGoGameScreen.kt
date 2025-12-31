package com.reivaj.clarity.presentation.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            // Intro Screen
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Go/No-Go",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Tap when GREEN appears.\nDo NOT tap when RED appears.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(48.dp))
                Button(onClick = viewModel::startGame) {
                    Text("Start Game")
                }
            }
        } else if (state.isGameOver) {
             // Game Over Screen
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Game Over!", style = MaterialTheme.typography.headlineLarge)
                Text("Score: ${state.score}", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onNavigateHome) {
                    Text("Back to Menu")
                }
            }
        } else {
            // Active Game Screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        enabled = state.currentSymbol != null,
                        interactionSource = null, 
                        indication = null // Remove ripple for full screen area if desired, or keep it
                    ) { viewModel.onUserTap() }
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Score: ${state.score}", style = MaterialTheme.typography.titleMedium)
                    Text("Time: --:--", style = MaterialTheme.typography.titleMedium) // Placeholder for timer
                }

                // Instructions hint
                Text(
                    "Tap for GREEN, Wait for RED",
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 60.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Stimulus
                if (state.currentSymbol != null) {
                    // Logic: Assuming currentSymbol string is "GO" or "NO-GO" or similar from ViewModel
                    // We need to map this to Color.
                    // For now, let's assume the ViewModel emits "GREEN" or "RED" or "GO"/"NOGO" text.
                    // Let's rely on the text content to decide color for now, or just show the text in a colored circle.
                    
                    val isGo = state.currentSymbol?.contains("GO", ignoreCase = true) == true || 
                               state.currentSymbol?.contains("GREEN", ignoreCase = true) == true
                    
                    // Since the previous VM logic was generic symbols, we might want to update VM later to be specific.
                    // For now, let's just make a big circle with the text.
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(240.dp)
                            .clip(CircleShape)
                            .background(
                                // Use the booleans from state directly
                                if (state.isGoStimulus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (state.isGoStimulus) "GO" else "NO-GO",
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                }

                // Feedback
                state.feedback?.let {
                    Text(
                        it,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
