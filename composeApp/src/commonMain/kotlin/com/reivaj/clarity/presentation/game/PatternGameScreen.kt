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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject

@Composable
fun PatternGameScreen(
    onNavigateHome: () -> Unit,
    viewModel: PatternGameViewModel = koinInject()
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
                "Pattern Grid",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 42.sp,
                ),
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "Memorize the highlighted pattern\nthen recreate it from memory",
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
                "Complete!",
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
                "Level Reached: ${state.level}",
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
            Spacer(Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Level ${state.level}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    "•",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    "Lives: ${state.lives}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = if (state.lives <= 1) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                state.message ?: "",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
            
            Spacer(Modifier.height(32.dp))

            val gridSize = state.gridSize
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridSize),
                modifier = Modifier.wrapContentSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(gridSize * gridSize) { index ->
                    val isPattern = state.pattern.contains(index)
                    val isSelected = state.userSelection.contains(index)
                    
                    val targetColor = when (state.phase) {
                        PatternGamePhase.PREVIEW -> if (isPattern) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        PatternGamePhase.INPUT -> if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        PatternGamePhase.FEEDBACK -> {
                            if (isPattern) Color(0xFF4CAF50) // Green for correct
                            else if (isSelected) Color(0xFFF44336) // Red for wrong
                            else MaterialTheme.colorScheme.surfaceVariant
                        }
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    
                    val animatedColor by animateColorAsState(targetColor, animationSpec = tween(300))

                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(12.dp))
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
