package com.reivaj.clarity.data.export

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.reivaj.clarity.domain.model.AnalyticsSummary
import com.reivaj.clarity.domain.model.GameSession
import com.reivaj.clarity.domain.model.ProfileStats
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.ByteArrayOutputStream
import kotlin.math.abs

/**
 * Android implementation of PDF generator using PdfDocument API.
 */
actual class  PdfGenerator(private val context: Context) {
    
    actual suspend fun generateReport(
        stats: ProfileStats,
        analytics: AnalyticsSummary?,
        recentSessions: List<GameSession>,
    ): ByteArray {
        val document = PdfDocument()
        
        // Page setup
        val pageWidth = 595 // A4 width in points
        val pageHeight = 842 // A4 height in points
        val margin = 40f
        
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        
        // Paints
        val titlePaint = Paint().apply {
            textSize = 24f
            isFakeBoldText = true
            isAntiAlias = true
        }
        
        val headingPaint = Paint().apply {
            textSize = 18f
            isFakeBoldText = true
            isAntiAlias = true
        }
        
        val bodyPaint = Paint().apply {
            textSize = 12f
            isAntiAlias = true
        }
        
        val smallPaint = Paint().apply {
            textSize = 10f
            isAntiAlias = true
            color = android.graphics.Color.GRAY
        }
        
        var yPosition = margin + 40f
        
        // Header
        canvas.drawText("Clarity Performance Report", margin, yPosition, titlePaint)
        yPosition += 10f
        val currentDate = kotlinx.datetime.Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
        canvas.drawText(
            "Generated on ${currentDate.date}",
            margin,
            yPosition,
            smallPaint
        )
        yPosition += 40f
        
        // Summary Statistics
        canvas.drawText("Summary", margin, yPosition, headingPaint)
        yPosition += 25f
        
        canvas.drawText("Total Sessions: ${stats.totalSessions}", margin + 20,  yPosition, bodyPaint)
        yPosition += 20f
        canvas.drawText("Total Check-ins: ${stats.totalEMAs}", margin + 20, yPosition, bodyPaint)
        yPosition += 20f
        canvas.drawText("Current Streak: ${stats.currentStreak} days", margin + 20, yPosition, bodyPaint)
        yPosition += 20f
        canvas.drawText("Longest Streak: ${stats.longestStreak} days", margin + 20, yPosition, bodyPaint)
        yPosition += 20f
        canvas.drawText("Average Score: %.0f".format(stats.averageScore), margin + 20, yPosition, bodyPaint)
        yPosition += 20f
        canvas.drawText("Favorite Game: ${stats.favoriteGame?.name?.replace("_", " ") ?: "N/A"}", margin + 20, yPosition, bodyPaint)
        yPosition += 40f
        
        // Analytics Insights
        if (analytics != null) {
            canvas.drawText("Performance Insights", margin, yPosition, headingPaint)
            yPosition += 25f
            
            // Best Game
            if (analytics.bestGame != null) {
                val avgScore = analytics.averageScorePerGame[analytics.bestGame] ?: 0.0
                canvas.drawText(
                    "• Best Game: ${analytics.bestGame.name.replace("_", " ")} (Avg: %.0f)".format(avgScore),
                    margin + 20,
                    yPosition,
                    bodyPaint
                )
                yPosition += 20f
            }
            
            // Sleep Impact
            if (analytics.sleepImpact.hasEnoughData) {
                val impact = abs(analytics.sleepImpact.performanceDifference)
                val direction = if (analytics.sleepImpact.performanceDifference > 0) "decreases" else "increases"
                canvas.drawText(
                    "• Performance $direction %.0f%% with poor sleep (<6h)".format(impact),
                    margin + 20,
                    yPosition,
                    bodyPaint
                )
                yPosition += 20f
            }
            
            // Peak Time
            if (analytics.peakPerformanceHour != null) {
                val hour = analytics.peakPerformanceHour
                val amPm = if (hour < 12) "AM" else "PM"
                val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                canvas.drawText(
                    "• Peak Performance: $displayHour $amPm",
                    margin + 20,
                    yPosition,
                    bodyPaint
                )
                yPosition += 20f
            }
            
            // Baseline vs Stressed
            if (analytics.baselineVsStressed.hasEnoughData) {
                val impact = abs(analytics.baselineVsStressed.performanceDifference)
                val direction = if (analytics.baselineVsStressed.performanceDifference > 0) "decreases" else "increases"
                canvas.drawText(
                    "• Performance $direction %.0f%% when stressed".format(impact),
                    margin + 20,
                    yPosition,
                    bodyPaint
                )
                yPosition += 20f
            }
            
            yPosition += 20f
        }
        
        // Recent Sessions (last 10)
        canvas.drawText("Recent Sessions", margin, yPosition, headingPaint)
        yPosition += 25f
        
        val recentList = recentSessions.takeLast(10).reversed()
        for (session in recentList) {
            if (yPosition > pageHeight - margin - 60f) {
                // Need new page
                document.finishPage(page)
                val newPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create()
                val newPage = document.startPage(newPageInfo)
                // Continue on new page...
                break
            }
            
            val date = session.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
            val text = "${date.date} - ${session.gameType.name.replace("_", " ")}: Score ${session.score}"
            canvas.drawText(text, margin + 20, yPosition, bodyPaint)
            yPosition += 18f
        }
        
        // Footer
        canvas.drawText(
            "Generated by Clarity - KMP Cognitive Training App",
            margin,
            pageHeight - margin,
            smallPaint
        )
        
        document.finishPage(page)
        
        // Convert to ByteArray
        val outputStream = ByteArrayOutputStream()
        document.writeTo(outputStream)
        document.close()
        
        return outputStream.toByteArray()
    }
}
