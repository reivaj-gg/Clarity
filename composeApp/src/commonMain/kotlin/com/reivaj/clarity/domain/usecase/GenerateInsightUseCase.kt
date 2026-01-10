package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.EMA
import com.reivaj.clarity.domain.model.GameSession
import com.reivaj.clarity.domain.model.Insight
import kotlinx.coroutines.flow.first



class GenerateInsightUseCase(private val repository: ClarityRepository) {
    suspend operator fun invoke(): List<Insight> {
        val emas: List<EMA> = repository.getAllEMAs().first()
        val sessions: List<GameSession> = repository.getAllGameSessions().first()
        
        if (sessions.size < 3) {
            return listOf(
                Insight(
                    title = "Keep Playing",
                    description = "Play at least 3 sessions to unlock personalized insights about your brain.",
                    confidence = 1.0f
                )
            )
        }

        // 1. Data Preparation: Join Sessions with Context (EMA)
        val emaMap = emas.associateBy { it.id }
        val dataPoints = sessions.mapNotNull { session ->
            session.emaId?.let { emaId ->
                emaMap[emaId]?.let { ema -> session to ema }
            }
        }
        
        if (dataPoints.isEmpty()) return emptyList()

        val insights = mutableListOf<Insight>()

        // 2. Analyze Sleep Impact
        analyzeFactor(
            dataPoints = dataPoints,
            factorName = "Sleep",
            predicate = { _, ema -> ema.sleepHours < 6.0 },
            goodCondition = "rested (7+ hours)",
            badCondition = "sleep deprivated (<6 hours)"
        )?.let { insights.add(it) }

        // 3. Analyze Stress Impact (High Stress vs Low Stress)
        analyzeFactor(
            dataPoints = dataPoints,
            factorName = "Stress",
            predicate = { _, ema -> ema.anxiety >= 4 || ema.recentStressfulEvent },
            goodCondition = "relaxed",
            badCondition = "stressed"
        )?.let { insights.add(it) }

        // 4. Analyze Caffeine Impact (Reaction Time specific)
        analyzeCaffeine(dataPoints)?.let { insights.add(it) }

        // 5. Analyze Time of Day
        analyzeTimeOfDay(dataPoints)?.let { insights.add(it) }

        // Fallback if no patterns found yet
        if (insights.isEmpty()) {
            insights.add(
                Insight(
                    title = "Gathering Data",
                    description = "We are analyzing your patterns. Consistency is key!",
                    confidence = 0.5f
                )
            )
        }

        return insights.sortedByDescending { it.confidence }
    }

    private fun analyzeFactor(
        dataPoints: List<Pair<GameSession, EMA>>,
        factorName: String,
        predicate: (GameSession, EMA) -> Boolean,
        goodCondition: String,
        badCondition: String
    ): Insight? {
        val badState = dataPoints.filter { predicate(it.first, it.second) }
        val goodState = dataPoints.filter { !predicate(it.first, it.second) }

        if (badState.size < 2 || goodState.size < 2) return null

        val badAvgScore = badState.map { it.first.score }.average()
        val goodAvgScore = goodState.map { it.first.score }.average()

        val diffPercent = ((goodAvgScore - badAvgScore) / goodAvgScore) * 100

        return if (diffPercent > 10) { // Significant difference > 10%
            Insight(
                title = "$factorName Impacts Performance",
                description = "You perform ${diffPercent.toInt()}% better when you are $goodCondition compared to when you are $badCondition.",
                confidence = 0.85f
            )
        } else if (diffPercent < -10) {
           // Paradoxical result (performing better under "bad" conditions)
           Insight(
                title = "Surprising $factorName Pattern",
                description = "Interestingly, you performed ${(-diffPercent).toInt()}% better when $badCondition. This might be due to hyper-focus or other factors.",
                confidence = 0.7f
           )
        } else {
            null
        }
    }

    private fun analyzeCaffeine(dataPoints: List<Pair<GameSession, EMA>>): Insight? {
        val caffeinated = dataPoints.filter { it.second.caffeineRecent }
        val nonCaffeinated = dataPoints.filter { !it.second.caffeineRecent }

        if (caffeinated.size < 2 || nonCaffeinated.size < 2) return null

        // Reaction Time Analysis
        val cafRT = caffeinated.mapNotNull { it.first.reactionTimeMs }.average()
        val nonCafRT = nonCaffeinated.mapNotNull { it.first.reactionTimeMs }.average()
        
        if (cafRT.isNaN() || nonCafRT.isNaN()) return null

        val improvement = nonCafRT - cafRT // Lower is better for RT
        val percentFaster = (improvement / nonCafRT) * 100

        return if (percentFaster > 5) {
            Insight(
                title = "Caffeine Boost",
                description = "Caffeine seems to work! Your reaction times are %.1f%% faster after consuming caffeine.".format(percentFaster),
                confidence = 0.9f
            )
        } else {
            null
        }
    }
    
    private fun analyzeTimeOfDay(dataPoints: List<Pair<GameSession, EMA>>): Insight? {
        // Simple Morning vs Night split
        // 06:00 to 18:00 is "Day"
        
        // This requires parsing Instant to LocalTime, which is a bit complex in commonMain without extra utils, 
        // but we can look at basic clustering if we had hour data. 
        // For simplicity contributing to MVP, we'll skip complex time parsing for now 
        // unless we built a helper.
        return null
    }
}
