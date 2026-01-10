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
    val maxSessions = (data.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
    val lineColor = MaterialTheme.colorScheme.primary
    val pointColor = MaterialTheme.colorScheme.tertiary
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            val chartWidth = size.width
            val chartHeight = size.height - 20.dp.toPx()
            val pointRadius = 6.dp.toPx()
            val spacing = chartWidth / (data.size - 1).coerceAtLeast(1)

            // Draw grid lines (horizontal)
            for (i in 0..4) {
                val y = chartHeight * i / 4
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(chartWidth, y),
                    strokeWidth = 1.dp.toPx(),
                )
            }

            // Draw connecting lines between points
            if (data.size > 1) {
                val points = data.mapIndexed { index, (_, count) ->
                    val x = if (data.size == 1) chartWidth / 2 else index * spacing
                    val y = if (maxSessions > 0) {
                        chartHeight - (count.toFloat() / maxSessions) * chartHeight
                    } else {
                        chartHeight
                    }
                    Offset(x, y)
                }

                for (i in 0 until points.size - 1) {
                    drawLine(
                        color = lineColor,
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
            }

            // Draw data points (circles)
            data.forEachIndexed { index, (_, count) ->
                val x = if (data.size == 1) chartWidth / 2 else index * spacing
                val y = if (maxSessions > 0) {
                    chartHeight - (count.toFloat() / maxSessions) * chartHeight
                } else {
                    chartHeight
                }

                // Draw point with glow effect
                drawCircle(
                    color = pointColor.copy(alpha = 0.3f),
                    radius = pointRadius * 1.5f,
                    center = Offset(x, y),
                )
                drawCircle(
                    color = pointColor,
                    radius = pointRadius,
                    center = Offset(x, y),
                )
            }
        }

        // Day labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
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
