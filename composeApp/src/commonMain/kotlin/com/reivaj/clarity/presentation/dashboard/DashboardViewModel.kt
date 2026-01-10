package com.reivaj.clarity.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.EMA
import com.reivaj.clarity.domain.model.GameSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.reivaj.clarity.domain.usecase.GenerateInsightUseCase
import com.reivaj.clarity.domain.usecase.GetSessionsWithEmaUseCase

/**
 * UI State for the Dashboard screen.
 * @property recentSessions List of recent game sessions played by the user.
 * @property insights List of AI-generated insights based on performance and EMA data.
 * @property isLoading True if data is currently being fetched.
 */
data class DashboardState(
    val recentSessions: List<GameSession> = emptyList(),
    val insights: List<String> = emptyList(),
    val isLoading: Boolean = false
)

/**
 * ViewModel for the Dashboard.
 *
 * Responsibilities:
 * - Aggregating GameSession and EMA data.
 * - Generating simple insights (the "AI Coach") based on correlations.
 * - Exposing state to the UI.
 */
class DashboardViewModel(
    private val getSessionsWithEmaUseCase: GetSessionsWithEmaUseCase,
    private val generateInsightUseCase: GenerateInsightUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val sessionsPair = getSessionsWithEmaUseCase()
            val sessions = sessionsPair.map { it.first }.sortedByDescending { it.timestamp }
            
            // Map Domain Insights to Strings for MVP UI
            val domainInsights = generateInsightUseCase()
            val insightStrings = domainInsights.map { "${it.title}: ${it.description}" }
            
            _state.update { 
                it.copy(
                    recentSessions = sessions,
                    insights = insightStrings,
                    isLoading = false
                ) 
            }
        }
    }

    private fun generateInsights(data: List<Pair<GameSession, EMA?>>): List<String> {
        val insights = mutableListOf<String>()
        if (data.isEmpty()) {
            insights.add("Complete your first training session to get insights!")
            return insights
        }

        // 1. Sleep vs Performance (Hypothetical logic)
        val lowSleepSessions = data.filter { (it.second?.sleepHours ?: 0.0) < 6.0 }
        val highSleepSessions = data.filter { (it.second?.sleepHours ?: 0.0) >= 7.0 }

        if (lowSleepSessions.isNotEmpty() && highSleepSessions.isNotEmpty()) {
            val lowSleepScore = lowSleepSessions.map { it.first.score }.average()
            val highSleepScore = highSleepSessions.map { it.first.score }.average()
            
            if (highSleepScore > lowSleepScore) {
                insights.add("Coach: You score ${(highSleepScore - lowSleepScore).toInt()} points higher when you sleep 7+ hours.")
            }
        }

        // 2. Stress
        val highStress = data.filter { (it.second?.anxiety ?: 0) >= 4 }
        if (highStress.isNotEmpty()) {
            insights.add("Coach: You've been reporting high anxiety. Try a breathing exercise before playing.")
        }
        
        // 3. Generic encouragement
        insights.add("Keep up the daily training for better neuroplasticity!")
        
        return insights
    }
}
