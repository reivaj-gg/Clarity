package com.reivaj.clarity.presentation.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.reivaj.clarity.domain.model.Insight
import com.reivaj.clarity.domain.model.InsightType
import org.koin.compose.koinInject

@Composable
fun InsightsScreen() {
    val viewModel = koinInject<InsightsViewModel>()
    val insights by viewModel.insights.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Insights",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = { viewModel.loadInsights() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
                Text(
                    "AI-powered analysis of your performance",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // AI Wisdom
            val aiInsight = insights.find { it.type == InsightType.AI_GENERATED }
            if (aiInsight != null) {
                item {
                    Text("Daily Wisdom", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    AiWisdomCard(aiInsight)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Metrics Carousel
            val metricInsights = insights.filter { it.type != InsightType.AI_GENERATED && it.type != InsightType.TIP }
            if (metricInsights.isNotEmpty()) {
                item {
                    Text("Key Metrics", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(metricInsights) { insight ->
                            MetricCard(insight)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Tips List
            val tips = insights.filter { it.type == InsightType.TIP }
            if (tips.isNotEmpty()) {
                item {
                    Text("Observations", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(tips) { tip ->
                    TipCard(tip)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            if (insights.isEmpty() && !isLoading) {
                item {
                    EmptyState()
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun AiWisdomCard(insight: Insight) {
    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clarity Coach", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
            Text(
                text = "\"${insight.description}\"",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun MetricCard(insight: Insight) {
    val containerColor = when(insight.type) {
        InsightType.POSITIVE -> MaterialTheme.colorScheme.tertiaryContainer // Green-ish usually (or adjust theme)
        InsightType.WARNING -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val icon = when(insight.relatedMetric) {
        "Sleep" -> Icons.Default.Bedtime
        "Time" -> Icons.Default.LightMode
        "Stress" -> Icons.Default.Bolt
        else -> Icons.Default.Timer
    }

    Card(
        modifier = Modifier.width(200.dp).height(150.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(insight.relatedMetric ?: "Metric", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                insight.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                insight.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3
            )
        }
    }
}

@Composable
fun TipCard(insight: Insight) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Psychology, null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(insight.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(insight.description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun EmptyState() {
     Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Psyche, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.height(16.dp))
        Text("No Data Yet", style = MaterialTheme.typography.titleMedium)
        Text("Play some games to get insights!", style = MaterialTheme.typography.bodyMedium)
    }
}

// Fallback for missing icon
val androidx.compose.material.icons.Icons.Filled.Psyche get() = Icons.Default.Psychology
