package com.reivaj.clarity.data.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.pdf.PdfDocument
import com.reivaj.clarity.domain.model.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.ByteArrayOutputStream
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Android implementation of PDF generator using PdfDocument API.
 * Generates a professional cognitive assessment report.
 */
actual class PdfGenerator(private val context: Context) {
    
    // Page dimensions (A4)
    private val pageWidth = 595
    private val pageHeight = 842
    private val margin = 40f
    private val contentWidth = pageWidth - (margin * 2)
    
    // Colors
    private val primaryColor = Color.parseColor("#1565C0")
    private val accentColor = Color.parseColor("#2E7D32")
    private val warningColor = Color.parseColor("#EF6C00")
    private val dangerColor = Color.parseColor("#C62828")
    private val textColor = Color.parseColor("#212121")
    private val secondaryText = Color.parseColor("#757575")
    private val lightGray = Color.parseColor("#E0E0E0")
    private val chartBlue = Color.parseColor("#42A5F5")
    private val chartGreen = Color.parseColor("#66BB6A")
    
    // Text sizes
    private val titleSize = 16f
    private val headingSize = 12f
    private val bodySize = 10f
    private val smallSize = 8f
    
    // Line spacing
    private val lineHeight = 14f
    private val sectionGap = 20f
    
    actual suspend fun generateReport(reportData: PdfReportData): ByteArray {
        val document = PdfDocument()
        
        drawPage1ExecutiveSummary(document, reportData)
        drawPage2EmaAnalysis(document, reportData)
        drawPage3PerformanceChart(document, reportData)
        drawPage4Recommendations(document, reportData)
        drawPage5SessionLog(document, reportData)
        
        val outputStream = ByteArrayOutputStream()
        document.writeTo(outputStream)
        document.close()
        
        return outputStream.toByteArray()
    }
    
    // ==================== PAGE 1: EXECUTIVE SUMMARY ====================
    private fun drawPage1ExecutiveSummary(document: PdfDocument, data: PdfReportData) {
        val page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create())
        val canvas = page.canvas
        var y = margin
        
        // Header
        y = drawHeader(canvas, y, "COGNITIVE PERFORMANCE REPORT", data.userName)
        y += 10f
        
        // Report Info Box
        y = drawInfoBox(canvas, y, listOf(
            "Patient/User: ${data.userName}",
            "Period: ${data.reportPeriod.label} (${data.reportPeriod.days} days)",
            "Generated: ${formatDate(data.generatedAt)}",
            "Sessions: ${data.totalSessions} | Check-ins: ${data.totalEmas}"
        ))
        y += sectionGap
        
        // Performance Score
        y = drawSectionHeader(canvas, y, "Cognitive Performance Index")
        y = drawScoreDisplay(canvas, y, data.performanceScore, data.performanceScoreBreakdown)
        y += sectionGap
        
        // Quick Stats Grid
        y = drawSectionHeader(canvas, y, "Key Metrics")
        y = drawStatsGrid(canvas, y, listOf(
            "Accuracy" to "${(data.averageAccuracy * 100).roundToInt()}%",
            "Streak" to "${data.currentStreak} days",
            "Games" to "${data.gameStats.size} types",
            "Sessions" to "${data.totalSessions}"
        ))
        y += sectionGap
        
        // Clinical Indicators
        y = drawSectionHeader(canvas, y, "Clinical Indicators")
        y = drawIndicator(canvas, y, "Cognitive Consistency", getCognitiveLevel(data.averageAccuracy), getLevelColor(data.averageAccuracy))
        y = drawIndicator(canvas, y, "Sleep Impact", getSleepImpactLevel(data.sleepStats), getSleepColor(data.sleepStats))
        y = drawIndicator(canvas, y, "Stress Level", getStressLevel(data.moodStats), getStressColor(data.moodStats))
        y = drawIndicator(canvas, y, "Engagement", getEngagementLevel(data.currentStreak), getEngagementColor(data.currentStreak))
        
        // Key Finding
        if (data.coachInsights.isNotEmpty()) {
            y += sectionGap
            y = drawSectionHeader(canvas, y, "Primary Finding")
            y = drawWrappedText(canvas, y, data.coachInsights.first(), primaryColor)
        }
        
        drawFooter(canvas, 1, 5, data.userName)
        document.finishPage(page)
    }
    
    // ==================== PAGE 2: EMA ANALYSIS ====================
    private fun drawPage2EmaAnalysis(document: PdfDocument, data: PdfReportData) {
        val page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create())
        val canvas = page.canvas
        var y = margin
        
        y = drawHeader(canvas, y, "PSYCHOLOGICAL & WELLNESS ANALYSIS", data.userName)
        y += 10f
        
        // EMA Questionnaire Summary
        y = drawSectionHeader(canvas, y, "Self-Assessment Summary (EMA Questionnaire)")
        y = drawBodyText(canvas, y, "Based on ${data.totalEmas} ecological momentary assessments:")
        y += 8f
        
        // Mood Scales with visual bars
        val mood = data.moodStats
        y = drawMoodBar(canvas, y, "Happiness (Positive Affect)", mood.avgHappiness, accentColor)
        y = drawMoodBar(canvas, y, "Anxiety (Worry/Tension)", mood.avgAnxiety, getAnxietyColor(mood.avgAnxiety))
        y = drawMoodBar(canvas, y, "Sadness (Low Mood)", mood.avgSadness, getSadnessColor(mood.avgSadness))
        y = drawMoodBar(canvas, y, "Anger (Irritability)", mood.avgAnger, getAngerColor(mood.avgAnger))
        y += 10f
        
        y = drawBodyText(canvas, y, "Interpretation: ${mood.interpretation}", primaryColor, true)
        y += sectionGap
        
        // Sleep Analysis
        y = drawSectionHeader(canvas, y, "Sleep Pattern Analysis")
        val sleep = data.sleepStats
        y = drawDataRow(canvas, y, "Average Duration", "${String.format("%.1f", sleep.avgHours)} hours/night")
        y = drawDataRow(canvas, y, "Range", "${String.format("%.1f", sleep.minHours)} - ${String.format("%.1f", sleep.maxHours)} hours")
        y = drawDataRow(canvas, y, "Quality Rating", "${String.format("%.1f", sleep.avgQuality)}/5 (${sleep.qualityInterpretation})")
        
        if (sleep.impactOnPerformance > 0) {
            y = drawDataRow(canvas, y, "Performance Impact", "+${sleep.impactOnPerformance.roundToInt()}% with adequate sleep")
        }
        y += sectionGap
        
        // Lifestyle Factors
        y = drawSectionHeader(canvas, y, "Lifestyle Factors")
        val lifestyle = data.lifestyleNotes
        y = drawLifestyleBar(canvas, y, "Caffeine Before Sessions", lifestyle.caffeineUsagePercent)
        y = drawLifestyleBar(canvas, y, "Alcohol Use Reported", lifestyle.alcoholUsagePercent)
        y = drawLifestyleBar(canvas, y, "Days with Stress Events", lifestyle.stressfulEventsPercent)
        y += sectionGap
        
        // Sleep Impact Table
        y = drawSectionHeader(canvas, y, "Sleep Duration Impact on Performance")
        val table = data.sleepImpactTable
        y = drawTableHeader(canvas, y, listOf("Sleep Hours", "Accuracy", "Sessions", "Rating"))
        y = drawTableRowData(canvas, y, listOf("Under 6h", "${table.under6Hours.avgAccuracy.roundToInt()}%", "${table.under6Hours.sessionCount}", getRating(table.under6Hours.avgAccuracy)))
        y = drawTableRowData(canvas, y, listOf("6-7 hours", "${table.sixTo7Hours.avgAccuracy.roundToInt()}%", "${table.sixTo7Hours.sessionCount}", getRating(table.sixTo7Hours.avgAccuracy)))
        y = drawTableRowData(canvas, y, listOf("7-9 hours", "${table.sevenTo9Hours.avgAccuracy.roundToInt()}%", "${table.sevenTo9Hours.sessionCount}", getRating(table.sevenTo9Hours.avgAccuracy)))
        y = drawTableRowData(canvas, y, listOf("Over 9h", "${table.over9Hours.avgAccuracy.roundToInt()}%", "${table.over9Hours.sessionCount}", getRating(table.over9Hours.avgAccuracy)))
        
        drawFooter(canvas, 2, 5, data.userName)
        document.finishPage(page)
    }
    
    // ==================== PAGE 3: PERFORMANCE CHART ====================
    private fun drawPage3PerformanceChart(document: PdfDocument, data: PdfReportData) {
        val page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 3).create())
        val canvas = page.canvas
        var y = margin
        
        y = drawHeader(canvas, y, "PERFORMANCE TRENDS & ANALYTICS", data.userName)
        y += 10f
        
        // Dispersion Chart
        y = drawSectionHeader(canvas, y, "Performance Over Time (${data.reportPeriod.label})")
        y = drawDispersionChart(canvas, y, data.recentSessions)
        y += sectionGap
        
        // Circadian Profile
        y = drawSectionHeader(canvas, y, "Circadian Rhythm Analysis")
        val circadian = data.circadianProfile
        y = drawDataRow(canvas, y, "Peak Performance", "${formatHour(circadian.peakHour)} (${circadian.peakAccuracy.roundToInt()}% accuracy)")
        y = drawDataRow(canvas, y, "Low Performance", "${formatHour(circadian.lowestHour)} (${circadian.lowestAccuracy.roundToInt()}% accuracy)")
        y = drawDataRow(canvas, y, "Chronotype", getChronotype(circadian.peakHour))
        y += 8f
        y = drawBodyText(canvas, y, "Recommendation: ${circadian.recommendation}", primaryColor, true)
        y += sectionGap
        
        // Error Analysis
        y = drawSectionHeader(canvas, y, "Cognitive Error Patterns")
        val errors = data.errorAnalysis
        y = drawBodyText(canvas, y, "Omission Errors (missed responses - attention/vigilance):", textColor, true)
        y = drawDataRow(canvas, y, "   Total", "${errors.totalOmissionErrors} errors")
        y = drawDataRow(canvas, y, "   When Tired", "${errors.omissionWhenTired} (sleep < 6h)")
        y = drawBodyText(canvas, y, "   Trend: ${errors.omissionTrend}", secondaryText)
        y += 8f
        
        y = drawBodyText(canvas, y, "Commission Errors (false alarms - impulsivity):", textColor, true)
        y = drawDataRow(canvas, y, "   Total", "${errors.totalCommissionErrors} errors")
        y = drawDataRow(canvas, y, "   When Stressed", "${errors.commissionWhenStressed} (anxiety >= 4)")
        y = drawBodyText(canvas, y, "   Trend: ${errors.commissionTrend}", secondaryText)
        y += sectionGap
        
        // Game Performance Summary
        y = drawSectionHeader(canvas, y, "Cognitive Domain Performance")
        data.gameStats.forEach { (gameType, stats) ->
            val trend = if (stats.improvementPercent > 0) "+${stats.improvementPercent.roundToInt()}%" else "${stats.improvementPercent.roundToInt()}%"
            y = drawGameRow(canvas, y, getGameName(gameType), stats.avgScore.roundToInt(), stats.sessionsPlayed, trend)
        }
        
        drawFooter(canvas, 3, 5, data.userName)
        document.finishPage(page)
    }
    
    // ==================== PAGE 4: RECOMMENDATIONS ====================
    private fun drawPage4Recommendations(document: PdfDocument, data: PdfReportData) {
        val page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 4).create())
        val canvas = page.canvas
        var y = margin
        
        y = drawHeader(canvas, y, "PERSONALIZED RECOMMENDATIONS", data.userName)
        y += 10f
        
        // Coach Insights
        y = drawSectionHeader(canvas, y, "Evidence-Based Recommendations")
        data.coachInsights.forEachIndexed { index, insight ->
            y = drawNumberedItem(canvas, y, index + 1, insight)
            y += 4f
        }
        y += sectionGap
        
        // Sleep Protocol
        y = drawSectionHeader(canvas, y, "Sleep Optimization Protocol")
        getSleepProtocol(data.sleepStats).forEach { item ->
            y = drawBulletPoint(canvas, y, item)
        }
        y += sectionGap
        
        // Stress Management
        y = drawSectionHeader(canvas, y, "Stress Management Strategies")
        getStressProtocol(data.moodStats).forEach { item ->
            y = drawBulletPoint(canvas, y, item)
        }
        y += sectionGap
        
        // Training Schedule
        y = drawSectionHeader(canvas, y, "Optimal Training Schedule")
        getScheduleProtocol(data.circadianProfile).forEach { item ->
            y = drawBulletPoint(canvas, y, item)
        }
        y += sectionGap
        
        // Disclaimer
        y = drawSectionHeader(canvas, y, "Important Notice")
        y = drawWrappedText(canvas, y, "This report is generated by an AI-assisted cognitive training application for self-improvement purposes only. It does not constitute medical advice, diagnosis, or treatment. Consult a qualified healthcare professional for any health concerns.", dangerColor)
        
        drawFooter(canvas, 4, 5, data.userName)
        document.finishPage(page)
    }
    
    // ==================== PAGE 5: SESSION LOG ====================
    private fun drawPage5SessionLog(document: PdfDocument, data: PdfReportData) {
        val page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 5).create())
        val canvas = page.canvas
        var y = margin
        
        y = drawHeader(canvas, y, "SESSION HISTORY LOG", data.userName)
        y += 10f
        
        y = drawSectionHeader(canvas, y, "Recent Sessions (${data.recentSessions.size.coerceAtMost(25)})")
        
        // Session Table Header
        y = drawSessionTableHeader(canvas, y)
        
        val timeZone = TimeZone.currentSystemDefault()
        var count = 0
        data.recentSessions.take(25).forEach { session ->
            if (y > pageHeight - 80 || count >= 25) return@forEach
            
            val dt = session.timestamp.toLocalDateTime(timeZone)
            y = drawSessionRow(canvas, y, 
                "${dt.monthNumber}/${dt.dayOfMonth}",
                "${String.format("%02d", dt.hour)}:${String.format("%02d", dt.minute)}",
                getGameShort(session.gameType),
                session.score.toString(),
                "${(session.accuracy * 100).roundToInt()}%",
                if (session.isBaselineSession) "Yes" else "No"
            )
            count++
        }
        
        y += sectionGap
        
        // Summary Statistics
        y = drawSectionHeader(canvas, y, "Period Summary")
        y = drawDataRow(canvas, y, "Total Sessions", "${data.totalSessions}")
        y = drawDataRow(canvas, y, "Average Accuracy", "${(data.averageAccuracy * 100).roundToInt()}%")
        y = drawDataRow(canvas, y, "Current Streak", "${data.currentStreak} days")
        y = drawDataRow(canvas, y, "Cognitive Domains", "${data.gameStats.size}")
        
        drawFooter(canvas, 5, 5, data.userName)
        document.finishPage(page)
    }
    
    // ==================== DISPERSION CHART ====================
    private fun drawDispersionChart(canvas: Canvas, startY: Float, sessions: List<GameSession>): Float {
        if (sessions.isEmpty()) {
            return drawBodyText(canvas, startY, "No session data available for chart.", secondaryText)
        }
        
        val chartWidth = contentWidth - 40f
        val chartHeight = 120f
        val chartX = margin + 30f
        val chartY = startY
        
        // Draw axes
        val axisPaint = Paint().apply { color = secondaryText; strokeWidth = 1f; isAntiAlias = true }
        canvas.drawLine(chartX, chartY + chartHeight, chartX + chartWidth, chartY + chartHeight, axisPaint)
        canvas.drawLine(chartX, chartY, chartX, chartY + chartHeight, axisPaint)
        
        // Y-axis labels (0-100%)
        val labelPaint = Paint().apply { color = secondaryText; textSize = 7f; isAntiAlias = true }
        canvas.drawText("100%", margin, chartY + 5f, labelPaint)
        canvas.drawText("50%", margin + 3f, chartY + chartHeight / 2, labelPaint)
        canvas.drawText("0%", margin + 8f, chartY + chartHeight, labelPaint)
        
        // Plot points
        val sortedSessions = sessions.sortedBy { it.timestamp }
        val dotPaint = Paint().apply { color = chartBlue; isAntiAlias = true }
        val avgPaint = Paint().apply { color = chartGreen; strokeWidth = 2f; isAntiAlias = true }
        
        if (sortedSessions.isNotEmpty()) {
            val xStep = chartWidth / sortedSessions.size.coerceAtLeast(1)
            
            var prevX = 0f
            var prevY = 0f
            val avgPath = Path()
            
            sortedSessions.forEachIndexed { index, session ->
                val x = chartX + (index + 0.5f) * xStep
                val accuracy = session.accuracy.coerceIn(0f, 1f)
                val y = chartY + chartHeight - (accuracy * chartHeight)
                
                // Draw dot
                canvas.drawCircle(x, y, 4f, dotPaint)
                
                // Draw average line
                if (index == 0) {
                    avgPath.moveTo(x, y)
                } else {
                    avgPath.lineTo(x, y)
                }
            }
            
            // Draw the trend line
            avgPaint.style = Paint.Style.STROKE
            canvas.drawPath(avgPath, avgPaint)
        }
        
        // X-axis label
        canvas.drawText("Sessions over time (oldest -> newest)", chartX + chartWidth / 3, chartY + chartHeight + 15f, labelPaint)
        
        // Legend
        val legendY = chartY + chartHeight + 28f
        canvas.drawCircle(chartX, legendY - 3f, 4f, dotPaint)
        canvas.drawText("Accuracy per session", chartX + 10f, legendY, labelPaint)
        
        canvas.drawLine(chartX + 120f, legendY - 3f, chartX + 140f, legendY - 3f, avgPaint)
        canvas.drawText("Performance trend", chartX + 145f, legendY, labelPaint)
        
        return chartY + chartHeight + 40f
    }
    
    // ==================== DRAWING HELPERS ====================
    
    private fun drawHeader(canvas: Canvas, y: Float, title: String, userName: String): Float {
        val brandPaint = Paint().apply { color = primaryColor; textSize = 14f; isFakeBoldText = true; isAntiAlias = true }
        canvas.drawText("CLARITY", margin, y + 15f, brandPaint)
        
        val subPaint = Paint().apply { color = secondaryText; textSize = 8f; isAntiAlias = true }
        canvas.drawText("Cognitive Performance Monitoring", margin, y + 28f, subPaint)
        
        val titlePaint = Paint().apply { color = textColor; textSize = 12f; isFakeBoldText = true; isAntiAlias = true }
        canvas.drawText(title, margin, y + 50f, titlePaint)
        
        val linePaint = Paint().apply { color = lightGray; strokeWidth = 1f }
        canvas.drawLine(margin, y + 58f, pageWidth - margin, y + 58f, linePaint)
        
        return y + 70f
    }
    
    private fun drawInfoBox(canvas: Canvas, y: Float, lines: List<String>): Float {
        val boxPaint = Paint().apply { color = Color.parseColor("#F5F5F5"); style = Paint.Style.FILL }
        canvas.drawRect(margin, y, pageWidth - margin, y + (lines.size * 14f) + 16f, boxPaint)
        
        val textPaint = Paint().apply { color = textColor; textSize = 9f; isAntiAlias = true }
        var currentY = y + 12f
        lines.forEach { line ->
            canvas.drawText(line, margin + 10f, currentY, textPaint)
            currentY += 14f
        }
        return currentY + 8f
    }
    
    private fun drawSectionHeader(canvas: Canvas, y: Float, title: String): Float {
        val paint = Paint().apply { color = primaryColor; textSize = 11f; isFakeBoldText = true; isAntiAlias = true }
        canvas.drawText(title, margin, y, paint)
        return y + 16f
    }
    
    private fun drawBodyText(canvas: Canvas, y: Float, text: String, color: Int = textColor, bold: Boolean = false): Float {
        val paint = Paint().apply { this.color = color; textSize = 9f; isFakeBoldText = bold; isAntiAlias = true }
        canvas.drawText(text.take(85), margin, y, paint)
        return y + 12f
    }
    
    private fun drawWrappedText(canvas: Canvas, y: Float, text: String, color: Int): Float {
        val paint = Paint().apply { this.color = color; textSize = 9f; isAntiAlias = true }
        var currentY = y
        val words = text.split(" ")
        var line = ""
        
        for (word in words) {
            val test = if (line.isEmpty()) word else "$line $word"
            if (paint.measureText(test) < contentWidth - 10) {
                line = test
            } else {
                canvas.drawText(line, margin, currentY, paint)
                currentY += 12f
                line = word
            }
        }
        if (line.isNotEmpty()) {
            canvas.drawText(line, margin, currentY, paint)
            currentY += 12f
        }
        return currentY
    }
    
    private fun drawDataRow(canvas: Canvas, y: Float, label: String, value: String): Float {
        val labelPaint = Paint().apply { color = secondaryText; textSize = 9f; isAntiAlias = true }
        val valuePaint = Paint().apply { color = textColor; textSize = 9f; isFakeBoldText = true; isAntiAlias = true }
        canvas.drawText(label, margin, y, labelPaint)
        canvas.drawText(value, margin + 160f, y, valuePaint)
        return y + 13f
    }
    
    private fun drawIndicator(canvas: Canvas, y: Float, label: String, level: String, color: Int): Float {
        val labelPaint = Paint().apply { this.color = textColor; textSize = 9f; isAntiAlias = true }
        val levelPaint = Paint().apply { this.color = color; textSize = 9f; isFakeBoldText = true; isAntiAlias = true }
        canvas.drawText(label, margin, y, labelPaint)
        canvas.drawText(level, margin + 180f, y, levelPaint)
        return y + 14f
    }
    
    private fun drawMoodBar(canvas: Canvas, y: Float, label: String, value: Double, color: Int): Float {
        val labelPaint = Paint().apply { this.color = textColor; textSize = 9f; isAntiAlias = true }
        canvas.drawText(label, margin, y, labelPaint)
        
        val barX = margin + 170f
        val barWidth = 80f
        val barHeight = 8f
        
        val bgPaint = Paint().apply { this.color = lightGray }
        canvas.drawRect(barX, y - barHeight, barX + barWidth, y, bgPaint)
        
        val fillPaint = Paint().apply { this.color = color }
        val fillWidth = (value.toFloat() / 5f) * barWidth
        canvas.drawRect(barX, y - barHeight, barX + fillWidth, y, fillPaint)
        
        val valuePaint = Paint().apply { this.color = textColor; textSize = 8f; isAntiAlias = true }
        canvas.drawText("${String.format("%.1f", value)}/5", barX + barWidth + 8f, y, valuePaint)
        
        return y + 16f
    }
    
    private fun drawLifestyleBar(canvas: Canvas, y: Float, label: String, percent: Double): Float {
        val labelPaint = Paint().apply { color = textColor; textSize = 9f; isAntiAlias = true }
        canvas.drawText(label, margin, y, labelPaint)
        
        val valuePaint = Paint().apply { color = secondaryText; textSize = 9f; isAntiAlias = true }
        canvas.drawText("${percent.roundToInt()}%", margin + 200f, y, valuePaint)
        
        return y + 14f
    }
    
    private fun drawScoreDisplay(canvas: Canvas, y: Float, score: Int, breakdown: PerformanceScoreBreakdown): Float {
        val boxPaint = Paint().apply { color = lightGray; style = Paint.Style.FILL }
        canvas.drawRect(margin, y, margin + 70f, y + 50f, boxPaint)
        
        val scorePaint = Paint().apply { 
            color = when { score >= 70 -> accentColor; score >= 40 -> warningColor; else -> dangerColor }
            textSize = 28f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText("$score", margin + 12f, y + 35f, scorePaint)
        
        val labelPaint = Paint().apply { color = secondaryText; textSize = 9f; isAntiAlias = true }
        canvas.drawText("/100", margin + 48f, y + 35f, labelPaint)
        
        val detailPaint = Paint().apply { color = textColor; textSize = 8f; isAntiAlias = true }
        canvas.drawText("Accuracy: ${breakdown.accuracyScore}/50", margin + 85f, y + 12f, detailPaint)
        canvas.drawText("Streak: ${breakdown.streakScore}/20", margin + 85f, y + 24f, detailPaint)
        canvas.drawText("Improvement: ${breakdown.improvementScore}/20", margin + 85f, y + 36f, detailPaint)
        canvas.drawText("Variety: ${breakdown.varietyScore}/10", margin + 85f, y + 48f, detailPaint)
        
        return y + 60f
    }
    
    private fun drawStatsGrid(canvas: Canvas, y: Float, stats: List<Pair<String, String>>): Float {
        val labelPaint = Paint().apply { color = secondaryText; textSize = 8f; isAntiAlias = true }
        val valuePaint = Paint().apply { color = textColor; textSize = 11f; isFakeBoldText = true; isAntiAlias = true }
        
        val colWidth = contentWidth / 4
        stats.forEachIndexed { i, (label, value) ->
            val x = margin + i * colWidth
            canvas.drawText(label, x, y, labelPaint)
            canvas.drawText(value, x, y + 14f, valuePaint)
        }
        return y + 28f
    }
    
    private fun drawTableHeader(canvas: Canvas, y: Float, headers: List<String>): Float {
        val paint = Paint().apply { color = primaryColor; textSize = 8f; isFakeBoldText = true; isAntiAlias = true }
        val colWidth = contentWidth / headers.size
        headers.forEachIndexed { i, h -> canvas.drawText(h, margin + i * colWidth, y, paint) }
        return y + 12f
    }
    
    private fun drawTableRowData(canvas: Canvas, y: Float, values: List<String>): Float {
        val paint = Paint().apply { color = textColor; textSize = 8f; isAntiAlias = true }
        val colWidth = contentWidth / values.size
        values.forEachIndexed { i, v -> canvas.drawText(v, margin + i * colWidth, y, paint) }
        return y + 11f
    }
    
    private fun drawSessionTableHeader(canvas: Canvas, y: Float): Float {
        val paint = Paint().apply { color = primaryColor; textSize = 8f; isFakeBoldText = true; isAntiAlias = true }
        val cols = listOf("Date", "Time", "Game", "Score", "Accuracy", "Baseline")
        val widths = listOf(0f, 50f, 95f, 175f, 225f, 290f)
        cols.forEachIndexed { i, c -> canvas.drawText(c, margin + widths[i], y, paint) }
        return y + 12f
    }
    
    private fun drawSessionRow(canvas: Canvas, y: Float, date: String, time: String, game: String, score: String, accuracy: String, baseline: String): Float {
        val paint = Paint().apply { color = textColor; textSize = 7f; isAntiAlias = true }
        val widths = listOf(0f, 50f, 95f, 175f, 225f, 290f)
        listOf(date, time, game, score, accuracy, baseline).forEachIndexed { i, v -> canvas.drawText(v, margin + widths[i], y, paint) }
        return y + 10f
    }
    
    private fun drawGameRow(canvas: Canvas, y: Float, name: String, avgScore: Int, sessions: Int, trend: String): Float {
        val namePaint = Paint().apply { color = textColor; textSize = 9f; isAntiAlias = true }
        val valuePaint = Paint().apply { color = secondaryText; textSize = 9f; isAntiAlias = true }
        canvas.drawText(name, margin, y, namePaint)
        canvas.drawText("Avg: $avgScore | Sessions: $sessions | Trend: $trend", margin + 180f, y, valuePaint)
        return y + 14f
    }
    
    private fun drawNumberedItem(canvas: Canvas, y: Float, num: Int, text: String): Float {
        val numPaint = Paint().apply { color = primaryColor; textSize = 9f; isFakeBoldText = true; isAntiAlias = true }
        val textPaint = Paint().apply { color = textColor; textSize = 9f; isAntiAlias = true }
        
        canvas.drawText("$num.", margin, y, numPaint)
        return drawWrappedTextAt(canvas, y, text, margin + 15f, textPaint)
    }
    
    private fun drawWrappedTextAt(canvas: Canvas, y: Float, text: String, startX: Float, paint: Paint): Float {
        var currentY = y
        val maxWidth = pageWidth - margin - startX
        val words = text.split(" ")
        var line = ""
        
        for (word in words) {
            val test = if (line.isEmpty()) word else "$line $word"
            if (paint.measureText(test) < maxWidth) {
                line = test
            } else {
                canvas.drawText(line, startX, currentY, paint)
                currentY += 12f
                line = word
            }
        }
        if (line.isNotEmpty()) {
            canvas.drawText(line, startX, currentY, paint)
            currentY += 12f
        }
        return currentY
    }
    
    private fun drawBulletPoint(canvas: Canvas, y: Float, text: String): Float {
        val paint = Paint().apply { color = textColor; textSize = 9f; isAntiAlias = true }
        return drawWrappedTextAt(canvas, y, "• $text", margin, paint)
    }
    
    private fun drawFooter(canvas: Canvas, page: Int, total: Int, userName: String) {
        val paint = Paint().apply { color = secondaryText; textSize = 7f; isAntiAlias = true }
        canvas.drawText("Clarity Cognitive Report | $userName | Page $page of $total", margin, pageHeight.toFloat() - 15f, paint)
    }
    
    // ==================== HELPER FUNCTIONS ====================
    
    private fun formatDate(instant: kotlinx.datetime.Instant): String {
        val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        return "${months[dt.monthNumber - 1]} ${dt.dayOfMonth}, ${dt.year}"
    }
    
    private fun formatHour(hour: Int): String = when {
        hour == 0 -> "12:00 AM"
        hour < 12 -> "$hour:00 AM"
        hour == 12 -> "12:00 PM"
        else -> "${hour - 12}:00 PM"
    }
    
    private fun getGameName(type: GameType): String = when (type) {
        GameType.GO_NO_GO -> "Response Inhibition"
        GameType.VISUOSPATIAL_GRID -> "Visuospatial Memory"
        GameType.SIMON_SEQUENCE -> "Sequential Memory"
        GameType.VISUAL_SEARCH -> "Visual Attention"
    }
    
    private fun getGameShort(type: GameType): String = when (type) {
        GameType.GO_NO_GO -> "Go/No-Go"
        GameType.VISUOSPATIAL_GRID -> "Pattern Grid"
        GameType.SIMON_SEQUENCE -> "Simon"
        GameType.VISUAL_SEARCH -> "Visual Search"
    }
    
    private fun getRating(accuracy: Double): String = when {
        accuracy >= 85 -> "Excellent"
        accuracy >= 70 -> "Good"
        accuracy >= 55 -> "Fair"
        accuracy > 0 -> "Needs Work"
        else -> "N/A"
    }
    
    private fun getCognitiveLevel(accuracy: Double): String = when {
        accuracy >= 0.85 -> "Excellent"
        accuracy >= 0.70 -> "Good"
        accuracy >= 0.55 -> "Moderate"
        else -> "Needs Improvement"
    }
    
    private fun getLevelColor(accuracy: Double): Int = when {
        accuracy >= 0.70 -> accentColor
        accuracy >= 0.55 -> warningColor
        else -> dangerColor
    }
    
    private fun getSleepImpactLevel(sleep: SleepStats): String = when {
        sleep.avgHours >= 7 -> "Optimal"
        sleep.avgHours >= 6 -> "Adequate"
        else -> "Insufficient"
    }
    
    private fun getSleepColor(sleep: SleepStats): Int = when {
        sleep.avgHours >= 7 -> accentColor
        sleep.avgHours >= 6 -> warningColor
        else -> dangerColor
    }
    
    private fun getStressLevel(mood: MoodStats): String = when {
        mood.avgAnxiety >= 4 -> "High"
        mood.avgAnxiety >= 2.5 -> "Moderate"
        else -> "Low"
    }
    
    private fun getStressColor(mood: MoodStats): Int = when {
        mood.avgAnxiety >= 4 -> dangerColor
        mood.avgAnxiety >= 2.5 -> warningColor
        else -> accentColor
    }
    
    private fun getEngagementLevel(streak: Int): String = when {
        streak >= 14 -> "Excellent"
        streak >= 7 -> "Good"
        streak >= 3 -> "Moderate"
        else -> "Low"
    }
    
    private fun getEngagementColor(streak: Int): Int = when {
        streak >= 7 -> accentColor
        streak >= 3 -> warningColor
        else -> dangerColor
    }
    
    private fun getAnxietyColor(v: Double): Int = if (v >= 4) dangerColor else if (v >= 2.5) warningColor else accentColor
    private fun getSadnessColor(v: Double): Int = if (v >= 4) dangerColor else if (v >= 2.5) warningColor else accentColor
    private fun getAngerColor(v: Double): Int = if (v >= 4) dangerColor else if (v >= 2.5) warningColor else accentColor
    
    private fun getChronotype(peakHour: Int): String = when {
        peakHour in 6..10 -> "Morning Type (Early Bird)"
        peakHour in 11..14 -> "Intermediate Type"
        else -> "Evening Type (Night Owl)"
    }
    
    private fun getSleepProtocol(sleep: SleepStats): List<String> = buildList {
        if (sleep.avgHours < 7) {
            add("Aim for 7-9 hours of sleep per night")
            add("Set a consistent sleep schedule")
        }
        if (sleep.avgQuality < 3.5) {
            add("Optimize sleep environment (dark, cool, quiet)")
            add("Limit screen time 1 hour before bed")
        }
        add("Avoid caffeine after 2 PM")
    }
    
    private fun getStressProtocol(mood: MoodStats): List<String> = buildList {
        if (mood.avgAnxiety >= 3) {
            add("Practice 10-15 minutes of mindfulness daily")
            add("Regular exercise (30+ min, 5x/week)")
        }
        add("Maintain social connections")
        add("Take regular breaks during work")
    }
    
    private fun getScheduleProtocol(circadian: CircadianProfile): List<String> = listOf(
        "Schedule training during your peak: ${formatHour(circadian.peakHour)}",
        "Take 10-15 minute breaks every 90 minutes",
        "Avoid demanding tasks during low periods"
    )
}
