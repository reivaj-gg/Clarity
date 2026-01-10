package com.reivaj.clarity.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinInject(),
) {
    val isSeeding by viewModel.isSeeding.collectAsState()
    val message by viewModel.message.collectAsState()
    val profileStats by viewModel.profileStats.collectAsState()
    val last7DaysData by viewModel.last7DaysData.collectAsState()
    val exportedData by viewModel.exportedData.collectAsState()
    val profilePictureUri by viewModel.profilePictureUri.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            "Profile",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        )
        
        Spacer(Modifier.height(24.dp))

        // Profile Photo - Centered Circle (Clickable)
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { viewModel.selectProfilePicture() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Select Profile Picture",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        
        // Hint text
        Text(
            "Tap to change picture",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(16.dp))

        // User Info
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Guest User",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                "Version 1.0.0 (Contest Build)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "Made with Kotlin Multiplatform",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(24.dp))

        // Statistics Section
        profileStats?.let { stats ->
            Text(
                "Your Statistics",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.height(12.dp))
            
            // First row: Sessions and Streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Default.FitnessCenter, null) },
                    label = "Sessions",
                    value = stats.totalSessions.toString(),
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Default.LocalFireDepartment, null) },
                    label = "Streak",
                    value = "${stats.currentStreak} days",
                )
            }

            Spacer(Modifier.height(12.dp))

            // Second row: Average Score (smaller) and Favorite Game (larger)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Default.SportsScore, null) },
                    label = "Avg Score",
                    value = "%.0f".format(stats.averageScore),
                )
                // Larger Favorite Game Card
                Card(
                    modifier = Modifier.weight(2f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                        Column {
                            Text(
                                stats.favoriteGame?.name?.replace("_", " ") ?: "N/A",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                            Text(
                                "Favorite Game",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // Performance Chart
        if (last7DaysData.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Performance (Last 7 Days)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.height(8.dp))
                    PerformanceChart(data = last7DaysData)
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // Settings / Export Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Data Export",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = viewModel::exportData,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.Info, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Export Data as JSON")
                }

                exportedData?.let { json ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Exported ${json.length} characters. Check console/logs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    println("=== EXPORTED DATA ===")
                    println(json)
                    println("=== END OF EXPORT ===")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Judge Utilities (Collapsible)
        var showJudgeUtils by remember { mutableStateOf(false) }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Column(Modifier.padding(16.dp)) {
                TextButton(
                    onClick = { showJudgeUtils = !showJudgeUtils },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        if (showJudgeUtils) "▼ Contest Judge Utilities" else "▶ Contest Judge Utilities",
                        style = MaterialTheme.typography.titleSmall,
                    )
                }

                if (showJudgeUtils) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Generate 14 days of synthetic history to test 'Insights' feature immediately.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.height(16.dp))

                    if (isSeeding) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        Button(
                            onClick = viewModel::seedData,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Inject Demo Data")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Feedback message
        message?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Text(
                    it,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            LaunchedEffect(it) {
                delay(4000)
                viewModel.clearMessage()
            }
        }

        // Bottom padding for scroll
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun StatCard(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            icon()
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}
