package com.reivaj.clarity.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.reivaj.clarity.domain.model.GameSession
import org.koin.compose.koinInject

/**
 * The main Dashboard screen showing user progress and AI insights.
 *
 * Features:
 * - "AI Coach Insights" card (Top).
 * - List of recent game sessions.
 * - Floating action button to start training.
 *
 * @param onNavigateToGame Callback to navigate to the Game Selection screen.
 * @param viewModel Injected ViewModel for dashboard state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToGame: () -> Unit,
    viewModel: DashboardViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Clarity Dashboard") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToGame) {
                Text("+ Play")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("AI Coach Insights", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (state.insights.isEmpty()) {
                            Text("Play more games to unlock insights.")
                        } else {
                            state.insights.forEach { insight ->
                                Text("• $insight", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }

            item {
                Text("Recent Sessions", style = MaterialTheme.typography.titleLarge)
            }

            items(state.recentSessions) { session ->
                SessionItem(session)
            }
        }
    }
}

@Composable
fun SessionItem(session: GameSession) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(session.gameType.name.replace("_", " "), style = MaterialTheme.typography.titleMedium)
                Text("Score: ${session.score}", style = MaterialTheme.typography.bodyLarge)
            }
            if (session.reactionTimeMs != null && session.reactionTimeMs > 0) {
                 Text("${session.reactionTimeMs}ms", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
