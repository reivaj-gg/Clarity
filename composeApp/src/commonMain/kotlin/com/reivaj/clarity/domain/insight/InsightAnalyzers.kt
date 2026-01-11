package com.reivaj.clarity.domain.insight

import com.reivaj.clarity.domain.model.BaselineComparisonData
import com.reivaj.clarity.domain.model.Insight
import com.reivaj.clarity.domain.model.InsightType
import com.reivaj.clarity.domain.model.SleepImpactData
import kotlin.math.roundToInt

/**
 * Collection of pure functions to generate insights from statistical data.
 */
object InsightAnalyzers {

    fun analyzeSleep(data: SleepImpactData): Insight? {
        if (!data.hasEnoughData) return null

        val impact = data.performanceDifference
        return when {
            impact > 5.0 -> {
                // Improves with good sleep
                Insight(
                    title = "Sleep Power",
                    description = "You perform ~${impact.roundToInt()}% better when well-rested (7h+). Sleep is your secret weapon.",
                    type = InsightType.POSITIVE,
                    relatedMetric = "Sleep",
                    score = impact / 100.0
                )
            }
            impact < -5.0 -> {
                 // Worsens with good sleep? Rare but possible (or inverted logic)
                 // Wait, performanceDifference = (good - poor) / good * 100 ?
                 // If good > poor, impact is positive.
                 // If data.performanceDifference is positive, it means Good Sleep > Poor Sleep.
                 // If impact is negative, it means Poor Sleep > Good Sleep (unlikely)
                 null
            }
            else -> {
                // Neutral
                Insight(
                     title = "Sleep Consistency",
                     description = "Your performance is stable regardless of sleep duration. Focus on sleep quality.",
                     type = InsightType.NEUTRAL,
                     relatedMetric = "Sleep"
                )
            }
        }
    }

    fun analyzeChronotype(peakHour: Int?): Insight? {
        if (peakHour == null) return null
        
        val peakTime = formatHour(peakHour)
        return when (peakHour) {
            in 5..11 -> Insight(
                title = "Morning Lark",
                description = "Your brain is sharpest around $peakTime. Tackle complex tasks before lunch.",
                type = InsightType.NEUTRAL,
                relatedMetric = "Time"
            )
            in 12..17 -> Insight(
                title = "Afternoon Peak",
                description = "You consistently score highest around $peakTime. Good time for training.",
                type = InsightType.NEUTRAL,
                relatedMetric = "Time"
            )
            else -> Insight(
                title = "Night Owl",
                description = "You truly shine in the evening ($peakTime). Don't force early starts if you can avoid them.",
                type = InsightType.NEUTRAL,
                relatedMetric = "Time"
            )
        }
    }

    fun analyzeStress(data: BaselineComparisonData): Insight? {
        if (!data.hasEnoughData) return null
        
        // performanceDifference = (baseline - stressed) / baseline * 100
        // Positive means Baseline > Stressed (Stress hurts)
        val impact = data.performanceDifference
        
        if (impact > 10.0) {
             return Insight(
                 title = "Stress Sensitive",
                 description = "High stress drops your accuracy by ~${impact.roundToInt()}%. Consider 5m box breathing before sessions.",
                 type = InsightType.WARNING,
                 relatedMetric = "Stress",
                 score = impact / 100.0
             )
        } else if (impact < -5.0) {
             return Insight(
                 title = "Pressure Performer",
                 description = "Surprisingly, you perform better under stress! Use this adrenaline for challenges.",
                 type = InsightType.POSITIVE,
                 relatedMetric = "Stress"
             )
        }
        return null
    }

    private fun formatHour(hour: Int): String {
        val adjusted = if (hour < 0) hour + 24 else if (hour >= 24) hour - 24 else hour
        return when {
            adjusted == 0 -> "12 AM"
            adjusted == 12 -> "12 PM"
            adjusted < 12 -> "$adjusted AM"
            else -> "${adjusted - 12} PM"
        }
    }
}
