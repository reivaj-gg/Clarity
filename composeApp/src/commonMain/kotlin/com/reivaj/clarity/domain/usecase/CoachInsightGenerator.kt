package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.domain.model.CircadianProfile
import com.reivaj.clarity.domain.model.ErrorAnalysis
import com.reivaj.clarity.domain.model.MoodStats
import com.reivaj.clarity.domain.model.SleepStats
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Generates personalized coaching insights based on user's performance data.
 *
 * Rules for insight generation:
 * 1. Sleep < 6h average → Sleep improvement tip
 * 2. Peak hours 8-11am → Morning person tip
 * 3. High RT variability → Fatigue detection tip
 * 4. High stress → Stress management tip
 * 5. Improvement > 5% → Encouragement tip
 * 6. Weak game exists → Focus area tip
 */
class CoachInsightGenerator {
    
    /**
     * Generate 4-6 personalized coaching insights.
     */
    fun generate(
        sleepStats: SleepStats,
        moodStats: MoodStats,
        circadianProfile: CircadianProfile,
        errorAnalysis: ErrorAnalysis,
        improvementPercent: Double,
        weakestGame: String?,
        fatigueDetected: Boolean,
    ): List<String> {
        val insights = mutableListOf<String>()
        
        // 1. Sleep-based insight
        if (sleepStats.avgHours < 6.0) {
            val potentialImprovement = ((6.0 - sleepStats.avgHours) * 3).roundToInt()
            insights.add(
                "💤 Sleep opportunity: Getting 7-8 hours could improve performance by ~$potentialImprovement%"
            )
        } else if (sleepStats.avgHours >= 7.0 && sleepStats.avgQuality >= 3.5) {
            insights.add(
                "✅ Great sleep habits! Your ${sleepStats.avgHours.roundToInt()}h average supports optimal cognitive function"
            )
        }
        
        // 2. Peak time insight
        val peakHour = circadianProfile.peakHour
        val peakTime = formatHour(peakHour)
        when {
            peakHour in 6..11 -> {
                insights.add(
                    "🌅 Morning person detected! Schedule important tasks between ${formatHour(peakHour - 1)} - ${formatHour(peakHour + 2)}"
                )
            }
            peakHour in 12..17 -> {
                insights.add(
                    "☀️ Afternoon peak: Your best performance is around $peakTime. Plan challenging work then"
                )
            }
            peakHour in 18..23 || peakHour in 0..5 -> {
                insights.add(
                    "🌙 Evening performer: You're sharpest around $peakTime. Consider evening training sessions"
                )
            }
        }
        
        // 3. Fatigue detection
        if (fatigueDetected) {
            insights.add(
                "⚠️ Fatigue signals detected in recent sessions. Try 15-minute breaks between training"
            )
        }
        
        // 4. Stress impact
        if (moodStats.avgAnxiety >= 3.5 || moodStats.avgSadness >= 3.5) {
            val stressImpact = abs(sleepStats.impactOnPerformance).roundToInt()
            insights.add(
                "🧘 Elevated stress detected (${stressImpact}% performance impact). Deep breathing before sessions may help"
            )
        }
        
        // 5. Improvement encouragement
        when {
            improvementPercent >= 10.0 -> {
                insights.add(
                    "🚀 Outstanding progress! ${improvementPercent.roundToInt()}% improvement shows your training is working"
                )
            }
            improvementPercent >= 5.0 -> {
                insights.add(
                    "📈 Great momentum! ${improvementPercent.roundToInt()}% improvement. Keep up the consistent practice"
                )
            }
            improvementPercent < -5.0 -> {
                insights.add(
                    "💡 Recent dip in scores. Consider checking sleep quality or taking rest days"
                )
            }
        }
        
        // 6. Weak game focus
        if (weakestGame != null) {
            insights.add(
                "🎯 Growth opportunity: Focus on $weakestGame to build a more balanced cognitive profile"
            )
        }
        
        // 7. Error patterns
        if (errorAnalysis.omissionWhenTired > 3) {
            insights.add(
                "😴 Omission errors increase when tired. Ensure adequate rest before training"
            )
        }
        
        if (errorAnalysis.commissionWhenStressed > 3) {
            insights.add(
                "⚡ Impulsive errors rise with stress. Slow down and breathe during high-stakes moments"
            )
        }
        
        // Limit to 6 insights max, prioritize first 6
        return insights.take(6)
    }
    
    private fun formatHour(hour: Int): String {
        val adjustedHour = when {
            hour < 0 -> hour + 24
            hour >= 24 -> hour - 24
            else -> hour
        }
        return when {
            adjustedHour == 0 -> "12 AM"
            adjustedHour < 12 -> "$adjustedHour AM"
            adjustedHour == 12 -> "12 PM"
            else -> "${adjustedHour - 12} PM"
        }
    }
}
