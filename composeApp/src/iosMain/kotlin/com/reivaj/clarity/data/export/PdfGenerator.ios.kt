package com.reivaj.clarity.data.export

import com.reivaj.clarity.domain.model.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.CoreGraphics.*
import platform.Foundation.*
import platform.UIKit.*
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * iOS implementation of PDF generator using Core Graphics.
 * Matches Android implementation exactly for contest multiplatform requirement.
 */
@OptIn(ExperimentalForeignApi::class)
actual class PdfGenerator {
    
    // Page dimensions (A4)
    private val pageWidth = 595.0
    private val pageHeight = 842.0
    private val margin = 40.0
    private val contentWidth = pageWidth - (margin * 2)
    
    // Text sizes
    private val headingSize = 11.0
    private val bodySize = 9.0
    private val smallSize = 7.0
    
    // Line spacing
    private val lineHeight = 14.0
    private val sectionGap = 20.0
    
    actual suspend fun generateReport(reportData: PdfReportData): ByteArray {
        val pdfData = NSMutableData()
        
        UIGraphicsBeginPDFContextToData(pdfData, CGRectMake(0.0, 0.0, pageWidth, pageHeight), null)
        
        drawPage1ExecutiveSummary(reportData)
        drawPage2EmaAnalysis(reportData)
        drawPage3PerformanceChart(reportData)
        drawPage4Recommendations(reportData)
        drawPage5SessionLog(reportData)
        
        UIGraphicsEndPDFContext()
        
        return pdfData.toByteArray()
    }
    
    // ==================== PAGE 1: EXECUTIVE SUMMARY ====================
    private fun drawPage1ExecutiveSummary(data: PdfReportData) {
        UIGraphicsBeginPDFPage()
        var y = margin
        
        y = drawHeader(y, "COGNITIVE PERFORMANCE REPORT", data.userName)
        y += 10
        
        y = drawInfoBox(y, listOf(
            "Patient/User: ${data.userName}",
            "Period: ${data.reportPeriod.label} (${data.reportPeriod.days} days)",
            "Generated: ${formatDate(data.generatedAt)}",
            "Sessions: ${data.totalSessions} | Check-ins: ${data.totalEmas}"
        ))
        y += sectionGap
        
        y = drawSectionHeader(y, "Cognitive Performance Index")
        y = drawScoreDisplay(y, data.performanceScore, data.performanceScoreBreakdown)
        y += sectionGap
        
        y = drawSectionHeader(y, "Key Metrics")
        y = drawStatsGrid(y, listOf(
            "Accuracy" to "${(data.averageAccuracy * 100).roundToInt()}%",
            "Streak" to "${data.currentStreak} days",
            "Games" to "${data.gameStats.size} types",
            "Sessions" to "${data.totalSessions}"
        ))
        y += sectionGap
        
        y = drawSectionHeader(y, "Clinical Indicators")
        y = drawIndicator(y, "Cognitive Consistency", getCognitiveLevel(data.averageAccuracy))
        y = drawIndicator(y, "Sleep Impact", getSleepImpactLevel(data.sleepStats))
        y = drawIndicator(y, "Stress Level", getStressLevel(data.moodStats))
        y = drawIndicator(y, "Engagement", getEngagementLevel(data.currentStreak))
        
        if (data.coachInsights.isNotEmpty()) {
            y += sectionGap
            y = drawSectionHeader(y, "Primary Finding")
            y = drawBodyText(y, data.coachInsights.first().take(80))
        }
        
        drawFooter(1, 5, data.userName)
    }
    
    // ==================== PAGE 2: EMA ANALYSIS ====================
    private fun drawPage2EmaAnalysis(data: PdfReportData) {
        UIGraphicsBeginPDFPage()
        var y = margin
        
        y = drawHeader(y, "PSYCHOLOGICAL & WELLNESS ANALYSIS", data.userName)
        y += 10
        
        y = drawSectionHeader(y, "Self-Assessment Summary (EMA Questionnaire)")
        y = drawBodyText(y, "Based on ${data.totalEmas} ecological momentary assessments:")
        y += 8
        
        val mood = data.moodStats
        y = drawMoodBar(y, "Happiness (Positive Affect)", mood.avgHappiness)
        y = drawMoodBar(y, "Anxiety (Worry/Tension)", mood.avgAnxiety)
        y = drawMoodBar(y, "Sadness (Low Mood)", mood.avgSadness)
        y = drawMoodBar(y, "Anger (Irritability)", mood.avgAnger)
        y += 10
        
        y = drawBodyText(y, "Interpretation: ${mood.interpretation}", bold = true)
        y += sectionGap
        
        y = drawSectionHeader(y, "Sleep Pattern Analysis")
        val sleep = data.sleepStats
        y = drawDataRow(y, "Average Duration", "${String.format("%.1f", sleep.avgHours)} hours/night")
        y = drawDataRow(y, "Range", "${String.format("%.1f", sleep.minHours)} - ${String.format("%.1f", sleep.maxHours)} hours")
        y = drawDataRow(y, "Quality Rating", "${String.format("%.1f", sleep.avgQuality)}/5 (${sleep.qualityInterpretation})")
        
        if (sleep.impactOnPerformance > 0) {
            y = drawDataRow(y, "Performance Impact", "+${sleep.impactOnPerformance.roundToInt()}% with adequate sleep")
        }
        y += sectionGap
        
        y = drawSectionHeader(y, "Lifestyle Factors")
        val lifestyle = data.lifestyleNotes
        y = drawDataRow(y, "Caffeine Before Sessions", "${lifestyle.caffeineUsagePercent.roundToInt()}%")
        y = drawDataRow(y, "Alcohol Use Reported", "${lifestyle.alcoholUsagePercent.roundToInt()}%")
        y = drawDataRow(y, "Days with Stress Events", "${lifestyle.stressfulEventsPercent.roundToInt()}%")
        y += sectionGap
        
        y = drawSectionHeader(y, "Sleep Duration Impact on Performance")
        val table = data.sleepImpactTable
        y = drawTableHeader(y, listOf("Sleep Hours", "Accuracy", "Sessions", "Rating"))
        y = drawTableRow(y, listOf("Under 6h", "${table.under6Hours.avgAccuracy.roundToInt()}%", "${table.under6Hours.sessionCount}", getRating(table.under6Hours.avgAccuracy)))
        y = drawTableRow(y, listOf("6-7 hours", "${table.sixTo7Hours.avgAccuracy.roundToInt()}%", "${table.sixTo7Hours.sessionCount}", getRating(table.sixTo7Hours.avgAccuracy)))
        y = drawTableRow(y, listOf("7-9 hours", "${table.sevenTo9Hours.avgAccuracy.roundToInt()}%", "${table.sevenTo9Hours.sessionCount}", getRating(table.sevenTo9Hours.avgAccuracy)))
        y = drawTableRow(y, listOf("Over 9h", "${table.over9Hours.avgAccuracy.roundToInt()}%", "${table.over9Hours.sessionCount}", getRating(table.over9Hours.avgAccuracy)))
        
        drawFooter(2, 5, data.userName)
    }
    
    // ==================== PAGE 3: PERFORMANCE CHART ====================
    private fun drawPage3PerformanceChart(data: PdfReportData) {
        UIGraphicsBeginPDFPage()
        var y = margin
        
        y = drawHeader(y, "PERFORMANCE TRENDS & ANALYTICS", data.userName)
        y += 10
        
        y = drawSectionHeader(y, "Performance Over Time (${data.reportPeriod.label})")
        y = drawDispersionChart(y, data.recentSessions)
        y += sectionGap
        
        y = drawSectionHeader(y, "Circadian Rhythm Analysis")
        val circadian = data.circadianProfile
        y = drawDataRow(y, "Peak Performance", "${formatHour(circadian.peakHour)} (${circadian.peakAccuracy.roundToInt()}% accuracy)")
        y = drawDataRow(y, "Low Performance", "${formatHour(circadian.lowestHour)} (${circadian.lowestAccuracy.roundToInt()}% accuracy)")
        y = drawDataRow(y, "Chronotype", getChronotype(circadian.peakHour))
        y += 8
        y = drawBodyText(y, "Recommendation: ${circadian.recommendation}", bold = true)
        y += sectionGap
        
        y = drawSectionHeader(y, "Cognitive Error Patterns")
        val errors = data.errorAnalysis
        y = drawBodyText(y, "Omission Errors (missed responses - attention/vigilance):", bold = true)
        y = drawDataRow(y, "   Total", "${errors.totalOmissionErrors} errors")
        y = drawDataRow(y, "   When Tired", "${errors.omissionWhenTired} (sleep < 6h)")
        y = drawBodyText(y, "   Trend: ${errors.omissionTrend}")
        y += 8
        
        y = drawBodyText(y, "Commission Errors (false alarms - impulsivity):", bold = true)
        y = drawDataRow(y, "   Total", "${errors.totalCommissionErrors} errors")
        y = drawDataRow(y, "   When Stressed", "${errors.commissionWhenStressed} (anxiety >= 4)")
        y = drawBodyText(y, "   Trend: ${errors.commissionTrend}")
        y += sectionGap
        
        y = drawSectionHeader(y, "Cognitive Domain Performance")
        data.gameStats.forEach { (gameType, stats) ->
            val trend = if (stats.improvementPercent > 0) "+${stats.improvementPercent.roundToInt()}%" else "${stats.improvementPercent.roundToInt()}%"
            y = drawGameRow(y, getGameName(gameType), stats.avgScore.roundToInt(), stats.sessionsPlayed, trend)
        }
        
        drawFooter(3, 5, data.userName)
    }
    
    // ==================== PAGE 4: RECOMMENDATIONS ====================
    private fun drawPage4Recommendations(data: PdfReportData) {
        UIGraphicsBeginPDFPage()
        var y = margin
        
        y = drawHeader(y, "PERSONALIZED RECOMMENDATIONS", data.userName)
        y += 10
        
        y = drawSectionHeader(y, "Evidence-Based Recommendations")
        data.coachInsights.forEachIndexed { index, insight ->
            y = drawNumberedItem(y, index + 1, insight)
            y += 4
        }
        y += sectionGap
        
        y = drawSectionHeader(y, "Sleep Optimization Protocol")
        getSleepProtocol(data.sleepStats).forEach { item ->
            y = drawBulletPoint(y, item)
        }
        y += sectionGap
        
        y = drawSectionHeader(y, "Stress Management Strategies")
        getStressProtocol(data.moodStats).forEach { item ->
            y = drawBulletPoint(y, item)
        }
        y += sectionGap
        
        y = drawSectionHeader(y, "Optimal Training Schedule")
        getScheduleProtocol(data.circadianProfile).forEach { item ->
            y = drawBulletPoint(y, item)
        }
        y += sectionGap
        
        y = drawSectionHeader(y, "Important Notice")
        y = drawBodyText(y, "This report is generated by an AI-assisted cognitive training")
        y = drawBodyText(y, "application for self-improvement purposes only. It does not")
        y = drawBodyText(y, "constitute medical advice. Consult a healthcare professional")
        y = drawBodyText(y, "for any health concerns.")
        
        drawFooter(4, 5, data.userName)
    }
    
    // ==================== PAGE 5: SESSION LOG ====================
    private fun drawPage5SessionLog(data: PdfReportData) {
        UIGraphicsBeginPDFPage()
        var y = margin
        
        y = drawHeader(y, "SESSION HISTORY LOG", data.userName)
        y += 10
        
        y = drawSectionHeader(y, "Recent Sessions (${data.recentSessions.size.coerceAtMost(25)})")
        y = drawSessionTableHeader(y)
        
        val timeZone = TimeZone.currentSystemDefault()
        var count = 0
        data.recentSessions.take(25).forEach { session ->
            if (y > pageHeight - 80 || count >= 25) return@forEach
            
            val dt = session.timestamp.toLocalDateTime(timeZone)
            y = drawSessionRow(y,
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
        
        y = drawSectionHeader(y, "Period Summary")
        y = drawDataRow(y, "Total Sessions", "${data.totalSessions}")
        y = drawDataRow(y, "Average Accuracy", "${(data.averageAccuracy * 100).roundToInt()}%")
        y = drawDataRow(y, "Current Streak", "${data.currentStreak} days")
        y = drawDataRow(y, "Cognitive Domains", "${data.gameStats.size}")
        
        drawFooter(5, 5, data.userName)
    }
    
    // ==================== DISPERSION CHART ====================
    private fun drawDispersionChart(startY: Double, sessions: List<GameSession>): Double {
        if (sessions.isEmpty()) {
            return drawBodyText(startY, "No session data available for chart.")
        }
        
        val chartWidth = contentWidth - 40.0
        val chartHeight = 100.0
        val chartX = margin + 30.0
        val chartY = startY
        
        // Draw axes
        drawLine(chartX, chartY + chartHeight, chartX + chartWidth, chartY + chartHeight)
        drawLine(chartX, chartY, chartX, chartY + chartHeight)
        
        // Y-axis labels
        drawText("100%", margin, chartY + 8, smallSize)
        drawText("50%", margin + 5, chartY + chartHeight / 2, smallSize)
        drawText("0%", margin + 10, chartY + chartHeight, smallSize)
        
        // Plot points
        val sortedSessions = sessions.sortedBy { it.timestamp }
        if (sortedSessions.isNotEmpty()) {
            val xStep = chartWidth / sortedSessions.size.coerceAtLeast(1)
            
            sortedSessions.forEachIndexed { index, session ->
                val x = chartX + (index + 0.5) * xStep
                val accuracy = session.accuracy.toDouble().coerceIn(0.0, 1.0)
                val y = chartY + chartHeight - (accuracy * chartHeight)
                
                drawCircle(x, y, 3.0)
            }
        }
        
        // X-axis label
        drawText("Sessions over time (oldest -> newest)", chartX + chartWidth / 4, chartY + chartHeight + 12, smallSize)
        
        return chartY + chartHeight + 30
    }
    
    // ==================== DRAWING HELPERS ====================
    
    private fun drawText(text: String, x: Double, y: Double, size: Double, bold: Boolean = false) {
        val font = if (bold) UIFont.boldSystemFontOfSize(size) else UIFont.systemFontOfSize(size)
        val attributes = mapOf<Any?, Any?>(
            NSFontAttributeName to font,
            NSForegroundColorAttributeName to UIColor.blackColor
        )
        NSString.create(string = text).drawAtPoint(CGPointMake(x, y), withAttributes = attributes)
    }
    
    private fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double) {
        val context = UIGraphicsGetCurrentContext() ?: return
        CGContextSetStrokeColorWithColor(context, UIColor.grayColor.CGColor)
        CGContextSetLineWidth(context, 0.5)
        CGContextMoveToPoint(context, x1, y1)
        CGContextAddLineToPoint(context, x2, y2)
        CGContextStrokePath(context)
    }
    
    private fun drawCircle(x: Double, y: Double, radius: Double) {
        val context = UIGraphicsGetCurrentContext() ?: return
        CGContextSetFillColorWithColor(context, UIColor.blueColor.CGColor)
        CGContextFillEllipseInRect(context, CGRectMake(x - radius, y - radius, radius * 2, radius * 2))
    }
    
    private fun drawHeader(y: Double, title: String, userName: String): Double {
        drawText("CLARITY", margin, y, 14.0, bold = true)
        drawText("Cognitive Performance Monitoring", margin, y + 15, 8.0)
        drawText(title, margin, y + 35, 12.0, bold = true)
        drawLine(margin, y + 45, pageWidth - margin, y + 45)
        return y + 55
    }
    
    private fun drawInfoBox(y: Double, lines: List<String>): Double {
        var currentY = y + 12
        lines.forEach { line ->
            drawText(line, margin + 10, currentY, bodySize)
            currentY += 14
        }
        return currentY + 5
    }
    
    private fun drawSectionHeader(y: Double, title: String): Double {
        drawText(title, margin, y, headingSize, bold = true)
        return y + 16
    }
    
    private fun drawBodyText(y: Double, text: String, bold: Boolean = false): Double {
        drawText(text.take(85), margin, y, bodySize, bold)
        return y + 12
    }
    
    private fun drawDataRow(y: Double, label: String, value: String): Double {
        drawText(label, margin, y, bodySize)
        drawText(value, margin + 160, y, bodySize, bold = true)
        return y + 13
    }
    
    private fun drawIndicator(y: Double, label: String, level: String): Double {
        drawText(label, margin, y, bodySize)
        drawText(level, margin + 180, y, bodySize, bold = true)
        return y + 14
    }
    
    private fun drawMoodBar(y: Double, label: String, value: Double): Double {
        drawText(label, margin, y, bodySize)
        drawText("${String.format("%.1f", value)}/5", margin + 260, y, bodySize)
        return y + 16
    }
    
    private fun drawScoreDisplay(y: Double, score: Int, breakdown: PerformanceScoreBreakdown): Double {
        drawText("$score", margin + 12, y + 25, 24.0, bold = true)
        drawText("/100", margin + 50, y + 25, bodySize)
        
        drawText("Accuracy: ${breakdown.accuracyScore}/50", margin + 85, y + 8, 8.0)
        drawText("Streak: ${breakdown.streakScore}/20", margin + 85, y + 20, 8.0)
        drawText("Improvement: ${breakdown.improvementScore}/20", margin + 85, y + 32, 8.0)
        drawText("Variety: ${breakdown.varietyScore}/10", margin + 85, y + 44, 8.0)
        
        return y + 55
    }
    
    private fun drawStatsGrid(y: Double, stats: List<Pair<String, String>>): Double {
        val colWidth = contentWidth / 4
        stats.forEachIndexed { i, (label, value) ->
            val x = margin + i * colWidth
            drawText(label, x, y, 8.0)
            drawText(value, x, y + 12, 10.0, bold = true)
        }
        return y + 26
    }
    
    private fun drawTableHeader(y: Double, headers: List<String>): Double {
        val colWidth = contentWidth / headers.size
        headers.forEachIndexed { i, h -> drawText(h, margin + i * colWidth, y, 8.0, bold = true) }
        return y + 12
    }
    
    private fun drawTableRow(y: Double, values: List<String>): Double {
        val colWidth = contentWidth / values.size
        values.forEachIndexed { i, v -> drawText(v, margin + i * colWidth, y, 8.0) }
        return y + 11
    }
    
    private fun drawSessionTableHeader(y: Double): Double {
        val cols = listOf("Date", "Time", "Game", "Score", "Accuracy", "Baseline")
        val widths = listOf(0.0, 50.0, 95.0, 175.0, 225.0, 290.0)
        cols.forEachIndexed { i, c -> drawText(c, margin + widths[i], y, 8.0, bold = true) }
        return y + 12
    }
    
    private fun drawSessionRow(y: Double, date: String, time: String, game: String, score: String, accuracy: String, baseline: String): Double {
        val widths = listOf(0.0, 50.0, 95.0, 175.0, 225.0, 290.0)
        listOf(date, time, game, score, accuracy, baseline).forEachIndexed { i, v -> drawText(v, margin + widths[i], y, smallSize) }
        return y + 10
    }
    
    private fun drawGameRow(y: Double, name: String, avgScore: Int, sessions: Int, trend: String): Double {
        drawText(name, margin, y, bodySize)
        drawText("Avg: $avgScore | Sessions: $sessions | Trend: $trend", margin + 150, y, bodySize)
        return y + 14
    }
    
    private fun drawNumberedItem(y: Double, num: Int, text: String): Double {
        drawText("$num.", margin, y, bodySize, bold = true)
        drawText(text.take(70), margin + 15, y, bodySize)
        return y + 14
    }
    
    private fun drawBulletPoint(y: Double, text: String): Double {
        drawText("• $text", margin, y, bodySize)
        return y + 12
    }
    
    private fun drawFooter(page: Int, total: Int, userName: String) {
        drawText("Clarity Cognitive Report | $userName | Page $page of $total", margin, pageHeight - 20, smallSize)
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
    
    private fun getSleepImpactLevel(sleep: SleepStats): String = when {
        sleep.avgHours >= 7 -> "Optimal"
        sleep.avgHours >= 6 -> "Adequate"
        else -> "Insufficient"
    }
    
    private fun getStressLevel(mood: MoodStats): String = when {
        mood.avgAnxiety >= 4 -> "High"
        mood.avgAnxiety >= 2.5 -> "Moderate"
        else -> "Low"
    }
    
    private fun getEngagementLevel(streak: Int): String = when {
        streak >= 14 -> "Excellent"
        streak >= 7 -> "Good"
        streak >= 3 -> "Moderate"
        else -> "Low"
    }
    
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
            add("Optimize sleep environment")
            add("Limit screen time before bed")
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
        "Schedule training during peak: ${formatHour(circadian.peakHour)}",
        "Take 10-15 minute breaks every 90 minutes",
        "Avoid demanding tasks during low periods"
    )
    
    private fun NSData.toByteArray(): ByteArray {
        val length = this.length.toInt()
        if (length == 0) return ByteArray(0)
        
        val bytes = ByteArray(length)
        bytes.usePinned { pinned ->
            platform.posix.memcpy(pinned.addressOf(0), this.bytes, this.length)
        }
        return bytes
    }
}
