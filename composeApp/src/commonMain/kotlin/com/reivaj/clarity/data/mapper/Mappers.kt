package com.reivaj.clarity.data.mapper

import com.reivaj.clarity.data.local.entity.EmaEntity
import com.reivaj.clarity.data.local.entity.GameSessionEntity
import com.reivaj.clarity.domain.model.*
import kotlinx.datetime.Instant

// --- EMA Mappers ---

fun EmaEntity.toDomain(): EMA {
    return EMA(
        id = id,
        timestamp = Instant.fromEpochMilliseconds(timestamp),
        anger = anger,
        anxiety = anxiety,
        sadness = sadness,
        happiness = happiness,
        recentStressfulEvent = recentStressfulEvent,
        sleepHours = sleepHours,
        sleepQuality = sleepQuality,
        caffeineRecent = caffeineRecent,
        alcoholUse = AlcoholUseToday.valueOf(alcoholUse),
        substanceType = SubstanceType.valueOf(substanceType),
        substanceDescription = substanceDescription,
        hasPositiveEvent = hasPositiveEvent,
        positiveEventIntensity = positiveEventIntensity,
        positiveEventDescription = positiveEventDescription,
        hasNegativeEvent = hasNegativeEvent,
        negativeEventIntensity = negativeEventIntensity,
        negativeEventDescription = negativeEventDescription,
        preSessionActivity = PreSessionActivity.valueOf(preSessionActivity),
        socialContext = SocialContext.valueOf(socialContext),
        environmentContext = EnvironmentContext.valueOf(environmentContext)
    )
}

fun EMA.toEntity(): EmaEntity {
    return EmaEntity(
        id = id,
        timestamp = timestamp.toEpochMilliseconds(),
        anger = anger,
        anxiety = anxiety,
        sadness = sadness,
        happiness = happiness,
        recentStressfulEvent = recentStressfulEvent,
        sleepHours = sleepHours,
        sleepQuality = sleepQuality,
        caffeineRecent = caffeineRecent,
        alcoholUse = alcoholUse.name,
        substanceType = substanceType.name,
        substanceDescription = substanceDescription,
        hasPositiveEvent = hasPositiveEvent,
        positiveEventIntensity = positiveEventIntensity,
        positiveEventDescription = positiveEventDescription,
        hasNegativeEvent = hasNegativeEvent,
        negativeEventIntensity = negativeEventIntensity,
        negativeEventDescription = negativeEventDescription,
        preSessionActivity = preSessionActivity.name,
        socialContext = socialContext.name,
        environmentContext = environmentContext.name,
        isBaseline = isBaseline
    )
}

// --- GameSession Mappers ---

fun GameSessionEntity.toDomain(): GameSession {
    return GameSession(
        id = id,
        timestamp = Instant.fromEpochMilliseconds(timestamp),
        gameType = GameType.valueOf(gameType),
        difficultyLevel = difficultyLevel,
        score = score,
        accuracy = accuracy,
        reactionTimeMs = reactionTimeMs,
        emaId = emaId,
        isBaselineSession = isBaselineSession
    )
}

fun GameSession.toEntity(): GameSessionEntity {
    return GameSessionEntity(
        id = id,
        timestamp = timestamp.toEpochMilliseconds(),
        gameType = gameType.name,
        difficultyLevel = difficultyLevel,
        score = score,
        accuracy = accuracy,
        reactionTimeMs = reactionTimeMs,
        emaId = emaId,
        isBaselineSession = isBaselineSession
    )
}
