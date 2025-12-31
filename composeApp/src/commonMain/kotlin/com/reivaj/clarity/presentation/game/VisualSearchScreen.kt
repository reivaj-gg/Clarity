package com.reivaj.clarity.presentation.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    if (!state.isPlaying && !state.isGameOver) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Visual Search", style = MaterialTheme.typography.headlineLarge)
                Text("Find the letter 'T' as fast as possible", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(32.dp))
                Button(onClick = viewModel::startGame) { Text("Start Game") }
            }
        }
    } else if (state.isGameOver) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
             Column(horizontalAlignment = Alignment.CenterHorizontally) {
                 Text("Time's Up!", style = MaterialTheme.typography.headlineLarge)
                 Text("Score: ${state.score}", style = MaterialTheme.typography.headlineMedium)
                 Spacer(Modifier.height(32.dp))
                 Button(onClick = onNavigateHome) { Text("Back to Menu") }
             }
         }
    } else {
         Column(
            Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Score: ${state.score}", style = MaterialTheme.typography.titleMedium)
                Text("Time: ${state.timeLeft}s", style = MaterialTheme.typography.titleMedium, color = if (state.timeLeft < 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(16.dp))
            Text("Find: ${state.target}", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(state.gridSize),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.Center
            ) {
                items(state.items) { item ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { viewModel.onItemClick(item) }
                    ) {
                        Text(item, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
