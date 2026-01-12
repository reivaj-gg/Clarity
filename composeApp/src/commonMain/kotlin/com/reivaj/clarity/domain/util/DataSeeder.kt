package com.reivaj.clarity.domain.util

import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.random.Random

/**
 * Helper class to seed the database with realistic fake data for demo purposes.
 * This is crucial for the contest screencast to show "Insights" without playing for weeks.
 */
class DataSeeder(private val repository: ClarityRepository) {

    suspend fun seedData() {
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        
        // Generate 14 days of history
        for (i in 1..14) {
            val dayOffset = 15 - i
            val timestamp = now.minus(dayOffset, DateTimeUnit.DAY, timeZone)
            
            // 1. Generate EMA
            // Scenario: 
            // - Weekends (simulated with i % 7 == 0 or 6) -> Good sleep, happy
            // - Weekdays -> Stressed, bad sleep
            
            val isWeekend = (i % 7 == 0) || (i % 7 == 6)
            
            val sleepHours = if (isWeekend) Random.nextDouble(7.5, 9.0) else Random.nextDouble(4.5, 6.5)
            val stress = if (isWeekend) Random.nextInt(1, 3) else Random.nextInt(4, 6) // High stress on weekdays
            val caffeine = !isWeekend // Caffeine on weekdays
            
            val emaId = "seed_ema_$i"
            val ema = EMA(
                id = emaId,
                timestamp = timestamp,
                anger = 1,
                anxiety = stress,
                sadness = 1,
                happiness = if (isWeekend) 4 else 2,
                recentStressfulEvent = stress >= 4,
                sleepHours = sleepHours,
                sleepQuality = if (sleepHours > 7) 5 else 2,
                caffeineRecent = caffeine,
                alcoholUse = if (isWeekend && i % 2 == 0) AlcoholUseToday.MODERATE else AlcoholUseToday.NONE
            )
            
            repository.saveEMA(ema)
            
            // 2. Generate Game Sessions related to EMA
            // Rule: Bad Sleep/Stress -> Worse Performance (0-100 scale)
            val basePerformance = if (sleepHours >= 7.0 && stress <= 3) 85 else 55
            val dailyFluctuation = Random.nextInt(-10, 15)
            val finalScore = (basePerformance + dailyFluctuation).coerceIn(10, 100)
            
            // Game 1: Go/No-Go
            val rtBase = if (finalScore > 75) 300L else 450L
            val rt = rtBase + Random.nextLong(-30, 30)
            
            repository.saveGameSession(
                GameSession(
                    id = "seed_game_${i}_1",
                    timestamp = timestamp.plus(10, DateTimeUnit.MINUTE, timeZone),
                    gameType = GameType.GO_NO_GO,
                    difficultyLevel = 1,
                    score = finalScore, // 0-100 Scale
                    accuracy = finalScore / 100f,
                    reactionTimeMs = rt,
                    emaId = emaId,
                    isBaselineSession = ema.isBaseline
                )
            )
            
            // Game 2: Simon (Memory)
            // Correlate with Go/No-Go but with variation
            val memScore = (finalScore + Random.nextInt(-5, 5)).coerceIn(0, 100)
            repository.saveGameSession(
                GameSession(
                    id = "seed_game_${i}_2",
                    timestamp = timestamp.plus(15, DateTimeUnit.MINUTE, timeZone),
                    gameType = GameType.SIMON_SEQUENCE,
                    difficultyLevel = 1,
                    score = memScore, // 0-100 Scale
                    accuracy = memScore / 100f,
                    emaId = emaId,
                    isBaselineSession = ema.isBaseline
                )
            )
        }
    }
}
