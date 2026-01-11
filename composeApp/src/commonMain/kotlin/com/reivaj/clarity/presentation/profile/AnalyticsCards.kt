package com.reivaj.clarity.presentation.profile

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reivaj.clarity.domain.model.AnalyticsSummary
import com.reivaj.clarity.domain.model.GameType
import kotlin.math.abs

/**
 * Display analytics insights as horizontally scrollable cards with info tooltips.
 */
@Composable
fun AnalyticsSection(analytics: AnalyticsSummary) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Performance Insights",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.Default.TrendingUp,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        
        Text(
            "Swipe to explore your performance patterns →",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
        
        Spacer(Modifier.height(12.dp))
        
        // Horizontal scrollable row of analytics cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BestGameCard(
                modifier = Modifier.width(180.dp),
                bestGame = analytics.bestGame,
                averageScore = analytics.averageScorePerGame[analytics.bestGame] ?: 0.0,
            )
            SleepImpactCard(
                modifier = Modifier.width(180.dp),
                sleepImpact = analytics.sleepImpact,
            )
            PeakTimeCard(
                modifier = Modifier.width(180.dp),
                peakHour = analytics.peakPerformanceHour,
            )
            BaselineCard(
                modifier = Modifier.width(180.dp),
                baselineData = analytics.baselineVsStressed,
            )
        }
    }
}

@Composable
fun BestGameCard(
    modifier: Modifier = Modifier,
    bestGame: GameType?,
    averageScore: Double,
) {
    var showTooltip by remember { mutableStateOf(false) }
    
    InsightCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            IconButton(
                onClick = { showTooltip = !showTooltip },
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "More info",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
        }
        
        if (showTooltip) {
            Text(
                "Your strongest game based on average score across all sessions. This shows where your cognitive abilities excel most.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
        
        Spacer(Modifier.height(8.dp))
        Text(
            "Top Performer",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            "by avg score",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
        Spacer(Modifier.height(4.dp))
        if (bestGame != null) {
            Text(
                bestGame.name.replace("_", " "),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                "%.0f avg".format(averageScore),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        } else {
            Text(
                "No data yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun SleepImpactCard(
    modifier: Modifier = Modifier,
    sleepImpact: com.reivaj.clarity.domain.model.SleepImpactData,
) {
    var showTooltip by remember { mutableStateOf(false) }
    
    InsightCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.Bedtime,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.tertiary,
            )
            IconButton(
                onClick = { showTooltip = !showTooltip },
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "More info",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
        }
        
        if (showTooltip) {
            Text(
                "Analyzes how less than 6 hours of sleep affects your scores. Negative % shows performance decline. Prioritize 6+ hours for optimal cognitive function.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding (vertical = 4.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Sleep Impact",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            "poor sleep (<6h)",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
        Spacer(Modifier.height(4.dp))
        if (sleepImpact.hasEnoughData) {
            val impact = abs(sleepImpact.performanceDifference)
            Text(
                if (sleepImpact.performanceDifference > 0) {
                    "-%.0f%%".format(impact)
                } else {
                    "+%.0f%%".format(impact)
                },
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = if (sleepImpact.performanceDifference > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            )
            Text(
                "performance drop",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Text(
                "Need more data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun PeakTimeCard(
    modifier: Modifier = Modifier,
    peakHour: Int?,
) {
    var showTooltip by remember { mutableStateOf(false) }
    
    InsightCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.secondary,
            )
            IconButton(
                onClick = { showTooltip = !showTooltip },
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "More info",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
        }
        
        if (showTooltip) {
            Text(
                "Based on your session history, this is when your brain performs best. Schedule challenging activities and important cognitive tasks during this time window.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Peak Performance",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            "best time of day",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
        Spacer(Modifier.height(4.dp))
        if (peakHour != null) {
            val amPm = if (peakHour < 12) "AM" else "PM"
            val displayHour = if (peakHour == 0) 12 else if (peakHour > 12) peakHour - 12 else peakHour
            Text(
                "$displayHour $amPm",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                "highest scores",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Text(
                "Need more data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun BaselineCard(
    modifier: Modifier = Modifier,
    baselineData: com.reivaj.clarity.domain.model.BaselineComparisonData,
) {
    var showTooltip by remember { mutableStateOf(false) }
    
    InsightCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.TrendingUp,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            IconButton(
                onClick = { showTooltip = !showTooltip },
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "More info",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
        }
        
        if (showTooltip) {
            Text(
                "Compares baseline state (well-rested, no stress events) vs. stressed conditions. Shows how stress affects your cognitive performance. Take breaks when stressed.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Stress Impact",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            "when stressed",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
        Spacer(Modifier.height(4.dp))
        if (baselineData.hasEnoughData) {
            val impact = abs(baselineData.performanceDifference)
            Text(
                if (baselineData.performanceDifference > 0) {
                    "-%.0f%%".format(impact)
                } else {
                    "+%.0f%%".format(impact)
                },
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = if (baselineData.performanceDifference > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            )
            Text(
                "performance drop",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Text(
                "Need more data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun InsightCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
        }
    }
}
