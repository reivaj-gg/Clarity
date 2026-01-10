package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.repository.ClarityRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

/**
 * Use case to get session statistics for the last 7 days.
 *
 * Returns data formatted for chart display.
 */
class GetLast7DaysStatsUseCase(
    private val repository: ClarityRepository,
) {
    suspend operator fun invoke(): List<Pair<String, Int>> {
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        
        // Get last 7 days
        val days = (6 downTo 0).map { daysAgo ->
            today.minus(daysAgo, DateTimeUnit.DAY)
        }
        
        // Get all sessions
        val sessions = repository.getAllGameSessions().first()
        
        // Group by date
        val sessionsByDate = sessions.groupBy {
            it.timestamp.toLocalDateTime(timeZone).date
        }
        
        // Create chart data
        return days.map { date ->
            val dayLabel = when {
                date == today -> "Today"
                date == today.minus(1, DateTimeUnit.DAY) -> "Yest"
                else -> date.dayOfWeek.name.take(3)
            }
            val count = sessionsByDate[date]?.size ?: 0
            dayLabel to count
        }
    }
}
