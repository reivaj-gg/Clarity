package com.reivaj.clarity.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reivaj.clarity.domain.model.AnalyticsSummary
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinInject(),
    darkModeEnabled: Boolean = false,
    onToggleDarkMode: () -> Unit = {},
) {
    val isSeeding by viewModel.isSeeding.collectAsState()
    val message by viewModel.message.collectAsState()
    val profileStats by viewModel.profileStats.collectAsState()
    val last7DaysData by viewModel.last7DaysData.collectAsState()
    val profileAnalytics by viewModel.analytics.collectAsState()
    val exportedData by viewModel.exportedData.collectAsState()
    val profilePictureUri by viewModel.profilePictureUri.collectAsState()

    // Local state for interactive settings (not persisted in this MVP build)
    var notificationsEnabled by remember { mutableStateOf(false) }

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
                    label = "Total Sessions",
                    value = stats.totalSessions.toString(),
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Default.LocalFireDepartment, null) },
                    label = "Current Streak",
                    value = "${stats.currentStreak} days",
                )
            }

            Spacer(Modifier.height(12.dp))

            // Second row: Average Score and Most Played
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Default.SportsScore, null) },
                    label = "Overall Avg",
                    value = stats.averageScore.roundToInt().toString(),
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Default.EmojiEvents, null) },
                    label = "Most Played",
                    value = stats.favoriteGame?.name?.replace("_", " ") ?: "N/A",
                )
            }

            Spacer(Modifier.height(24.dp))
        }

        // Analytics Section
        profileAnalytics?.let { analytics ->
            AnalyticsSection(analytics)
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

        // Settings Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Settings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                
                // Notifications
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Notifications")
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }
                
                HorizontalDivider()
                
                // Dark Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Dark Mode")
                    }
                    Switch(
                        checked = darkModeEnabled,
                        onCheckedChange = { onToggleDarkMode() },
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Data Export Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Data Export",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = viewModel::exportPdf,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.PictureAsPdf, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Export PDF Report")
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
fun StatCard(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    subtitle: String? = null,
) {
    Card(
        modifier = modifier.height(140.dp), // Fixed height ensures uniform sizing
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            icon()
            Spacer(Modifier.height(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            subtitle?.let {
                Spacer(Modifier.height(2.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }
    }
}
