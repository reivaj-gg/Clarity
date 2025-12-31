package com.reivaj.clarity.presentation.insights

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Data model for a single insight card.
 * @property title Short headline.
 * @property description Detailed explanation or recommendation.
 * @property icon Icon representing the insight category.
 */
data class InsightData(
    val title: String,
    val description: String,
    val icon: ImageVector
)

/**
 * Dedicated screen for detailed AI insights.
 *
 * Currently displays a list of mock/demo insights to showcase the
 * type of feedback the "AI Coach" provides (e.g., Sleep vs Performance).
 */
@Composable
fun InsightsScreen() {
    // Mock data for UI development
    val insights = listOf(
        InsightData(
            "Sleep Impact",
            "On days with less than 6h of sleep, your reaction time is slower.",
            Icons.Default.Bedtime
        ),
        InsightData(
            "Stress & Memory",
            "You perform better in memory tasks on low-stress days.",
            Icons.Default.Bolt
        )
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            "Insights",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            "AI generated recommendations",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(insights) { insight ->
                InsightCard(insight)
            }
        }
    }
}

@Composable
fun InsightCard(insight: InsightData) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = insight.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = insight.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
