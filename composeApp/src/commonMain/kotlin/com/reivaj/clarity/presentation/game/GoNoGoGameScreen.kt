package com.reivaj.clarity.presentation.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!state.isPlaying && !state.isGameOver) {
            // Initial Screen
            Text(
                "Go/No-Go Task",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 42.sp,
                ),
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "Tap when you see ✓\nDo NOT tap for ✗",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                modifier = Modifier.padding(16.dp)
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = viewModel::startGame,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Start Game", fontSize = 20.sp)
            }
        } else if (state.isGameOver) {
            // Game Over Screen - Enhanced
            Text(
                "Game Over!",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp,
                ),
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "Score: ${state.score}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Display average reaction time
            val avgReaction = if (state.reactionCount > 0) {
                state.totalReactionTime / state.reactionCount
            } else {
                0L
            }
            Text(
                "Avg Reaction: ${avgReaction}ms",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 28.sp),
                color = MaterialTheme.colorScheme.secondary,
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            Button(onClick = onNavigateHome) {
                Text("Back to Training", fontSize = 18.sp)
            }
        } else {
            // Game Play Screen - Enhanced
            Text(
                "Score: ${state.score}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(modifier = Modifier.height(48.dp))

            GameStimulus(state.currentSymbol, onTap = viewModel::onStimulusResponse)

            Spacer(modifier = Modifier.height(48.dp))

            GameFeedback(state.feedback, state.lastReactionTime)
        }
    }
}

@Composable
private fun GameStimulus(symbol: String?, onTap: () -> Unit) {
    Box(
        modifier = Modifier
            .size(240.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(4.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .clickable(enabled = symbol != null, onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.animation.Crossfade(
            targetState = symbol,
            animationSpec = androidx.compose.animation.core.tween(150)
        ) { currentSym ->
            when (currentSym) {
                "+", "✓", "GO" -> Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "GO - Tap Now!",
                    modifier = Modifier.size(160.dp),
                    tint = Color(0xFF4CAF50), // Vibrant Green
                )
                "x", "✗", "NOGO" -> Icon(
                    Icons.Default.Cancel,
                    contentDescription = "NO-GO - Don't Tap!",
                    modifier = Modifier.size(160.dp),
                    tint = Color(0xFFF44336), // Vibrant Red
                )
                else -> Spacer(Modifier.size(160.dp)) // Maintain layout size
            }
        }
    }
}

@Composable
private fun GameFeedback(feedback: String?, reactionTime: Long?) {
    LaunchedEffect(feedback) {
        if (feedback != null) {
            delay(1800) // Keep feedback visible longer
        }
    }

    Column(
        modifier = Modifier.height(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AnimatedVisibility(
            visible = feedback != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = feedback ?: "",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                    ),
                    color = if (feedback == "Correct!") Color(0xFF4CAF50) else Color(0xFFF44336),
                )
                
                // Show reaction time only for correct responses
                if (feedback == "Correct!" && reactionTime != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "${reactionTime}ms",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                        ),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
