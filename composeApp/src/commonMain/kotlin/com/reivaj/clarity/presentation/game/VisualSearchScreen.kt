package com.reivaj.clarity.presentation.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject

@Composable
fun VisualSearchScreen(
    onNavigateHome: () -> Unit,
    viewModel: VisualSearchViewModel = koinInject()
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
                "Visual Search",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 42.sp,
                ),
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "Find the target letter\nas quickly as possible",
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
                "Time's Up!",
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
                "Targets Found: ${state.score}",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 28.sp),
                color = MaterialTheme.colorScheme.secondary,
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            Button(onClick = onNavigateHome) {
                Text("Back to Training", fontSize = 18.sp)
            }
        } else {
            // Game Play Screen - Enhanced
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Score: ${state.score}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Text(
                    "${state.timeLeft}s",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = if (state.timeLeft < 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                )
            }
            
            Spacer(Modifier.height(24.dp))
            
            Text(
                "Find: ${state.target}",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.tertiary,
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(state.gridSize),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.items) { item ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { viewModel.onItemClick(item) }
                    ) {
                        Text(
                            item,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
