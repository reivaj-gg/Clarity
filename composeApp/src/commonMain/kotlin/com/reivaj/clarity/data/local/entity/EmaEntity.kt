package com.reivaj.clarity.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ema_table")
data class EmaEntity(
    @PrimaryKey
    val id: String,
    val timestamp: Long, // Epoch milliseconds
    
    // Mood (1-5)
    val anger: Int,
    val anxiety: Int,
    val sadness: Int,
    val happiness: Int,
    
    // Status
    val recentStressfulEvent: Boolean,
    val sleepHours: Double,
    val sleepQuality: Int,
    val caffeineRecent: Boolean,
    
    // Details (Stored as Strings/Ints)
    val alcoholUse: String, // Enum Name
    val substanceType: String, // Enum Name
    val substanceDescription: String?,
    
    val hasPositiveEvent: Boolean,
    val positiveEventIntensity: Int?,
    val positiveEventDescription: String?,
    
    val hasNegativeEvent: Boolean,
    val negativeEventIntensity: Int?,
    val negativeEventDescription: String?,
    
    val preSessionActivity: String, // Enum Name
    val socialContext: String, // Enum Name
    val environmentContext: String, // Enum Name
    
    // Calculated
    val isBaseline: Boolean
)
