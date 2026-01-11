package com.reivaj.clarity.presentation.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject

@Composable
fun SimonGameScreen(
    onNavigateHome: () -> Unit,
    viewModel: SimonGameViewModel = koinInject()
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
            // Intro Screen - Enhanced
            Text(
                "Simon Sequence",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 42.sp,
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "Watch the sequence of lights\nthen repeat it back",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
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
                "Well Done!",
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
            
            Text(
                "Sequence Length: ${state.score}",
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
                "Sequence: ${state.score}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            
            Text(
                state.message ?: "",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            
            Spacer(Modifier.height(48.dp))

            // Simon Pads (2x2 grid) - Enhanced with uniform sizing
            Box(modifier = Modifier.fillMaxWidth(0.85f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
                    ) {
                        SimonButton(
                            color = Color(0xFFE53935),
                            isLit = state.activeLight == 0,
                            onClick = { viewModel.onColorTap(0) },
                            modifier = Modifier.weight(1f),
                        )
                        SimonButton(
                            color = Color(0xFF43A047),
                            isLit = state.activeLight == 1,
                            onClick = { viewModel.onColorTap(1) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
                    ) {
                        SimonButton(
                            color = Color(0xFF1E88E5),
                            isLit = state.activeLight == 2,
                            onClick = { viewModel.onColorTap(2) },
                            modifier = Modifier.weight(1f),
                        )
                        SimonButton(
                            color = Color(0xFFFDD835),
                            isLit = state.activeLight == 3,
                            onClick = { viewModel.onColorTap(3) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimonButton(
    color: Color,
    isLit: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val displayColor by animateColorAsState(
        targetValue = if (isLit) color else color.copy(alpha = 0.3f),
        animationSpec = tween(150)
    )
    
    val shadowElevation by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isLit) 16.dp else 4.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = modifier
            .aspectRatio(1f) // Perfect circle
            .clip(CircleShape) // Clip first
            .background(displayColor)
            .clickable { onClick() },
    )
}
