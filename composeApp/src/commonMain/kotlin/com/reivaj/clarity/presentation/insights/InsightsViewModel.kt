package com.reivaj.clarity.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reivaj.clarity.domain.model.Insight
import com.reivaj.clarity.domain.usecase.GenerateInsightsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InsightsViewModel(
    private val generateInsights: GenerateInsightsUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _insights = MutableStateFlow<List<Insight>>(emptyList())
    val insights = _insights.asStateFlow()

    init {
        loadInsights()
    }

    fun loadInsights() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _insights.value = generateInsights()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
