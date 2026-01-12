package com.reivaj.clarity.presentation.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.Icons

/**
 * Renders a scatter plot with connecting lines for the last 7 days of session data.
 *
 * Uses Compose Canvas API to draw points and lines without external chart library dependencies,
 * maintaining pure KMP implementation.
 *
 * @param data List of (day label, session count) pairs
 */
@Composable
fun PerformanceChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier,
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    val targetColor = MaterialTheme.colorScheme.secondary
    val surfaceColor = MaterialTheme.colorScheme.surface
    
    // Info / Legend Header
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
             androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Insights,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "Score History",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Legend for Target
             Box(Modifier.size(12.dp, 2.dp).background(targetColor))
             Spacer(Modifier.width(4.dp))
             Text(
                 "Target (80%)", 
                 style = MaterialTheme.typography.labelSmall, 
                 color = targetColor
             )
        }
    }

    Row(modifier = modifier.height(160.dp)) {
        // Y-Axis Labels
        Column(
            modifier = Modifier.fillMaxHeight().padding(vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("100", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("80", style = MaterialTheme.typography.labelSmall, color = targetColor, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) // Target
            Text("50", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.width(8.dp))

        // Chart Area
        Column(modifier = Modifier.weight(1f)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                val chartWidth = size.width
                val chartHeight = size.height
                
                val maxScore = 100f
                val spacing = chartWidth / (data.size - 1).coerceAtLeast(1)

                // Draw horizontal grid lines corresponding to labels (0, 50, 80, 100)
                // 100 is at y=0
                // 0 is at y=chartHeight
                
                // 50 line
                drawLine(
                    color = gridColor,
                    start = Offset(0f, chartHeight * 0.5f),
                    end = Offset(chartWidth, chartHeight * 0.5f),
                    strokeWidth = 1.dp.toPx()
                )
                
                // Target line (80%) -> y = 20% of height
                val targetY = chartHeight * 0.2f
                drawLine(
                    color = targetColor.copy(alpha = 0.6f),
                    start = Offset(0f, targetY),
                    end = Offset(chartWidth, targetY),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )

                if (data.isNotEmpty()) {
                    val points = data.mapIndexed { index, (_, score) ->
                        val x = if (data.size == 1) chartWidth / 2 else index * spacing
                        val y = chartHeight - (score.toFloat() / maxScore) * chartHeight
                        Offset(x, y)
                    }

                    // Draw Line
                    val linePath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                    }
                    
                    drawPath(
                        path = linePath,
                        color = lineColor,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = androidx.compose.ui.graphics.StrokeJoin.Round
                        )
                    )

                    // Draw Points
                    points.forEach { point ->
                         drawCircle(
                            color = surfaceColor,
                            radius = 5.dp.toPx(),
                            center = point,
                        )
                        drawCircle(
                            color = lineColor,
                            radius = 3.5.dp.toPx(),
                            center = point,
                        )
                    }
                }
            }
            
            // X-Axis Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                data.forEach { (day, _) ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
