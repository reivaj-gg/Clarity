package com.reivaj.clarity.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reivaj.clarity.domain.util.DataSeeder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val dataSeeder: DataSeeder
) : ViewModel() {

    private val _isSeeding = MutableStateFlow(false)
    val isSeeding = _isSeeding.asStateFlow()
    
    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun seedData() {
        viewModelScope.launch {
            _isSeeding.value = true
            _message.value = "Generating synthetic history..."
            try {
                dataSeeder.seedData()
                _message.value = "Success! generated 14 days of data."
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            } finally {
                _isSeeding.value = false
            }
        }
    }
    
    fun clearMessage() {
        _message.value = null
    }
}
