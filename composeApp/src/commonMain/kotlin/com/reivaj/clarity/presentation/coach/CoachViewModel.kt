package com.reivaj.clarity.presentation.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.ChatMessage
import com.reivaj.clarity.domain.usecase.SendChatMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CoachState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val inputText: String = "",
    val userProfilePictureUri: String? = null
)

class CoachViewModel(
    private val repository: ClarityRepository,
    private val sendChatMessage: SendChatMessageUseCase
) : ViewModel() {

    private val _messages = repository.getRecentChatMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    private val _profilePictureUri = repository.getProfilePictureUri()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _inputText = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)

    val state: StateFlow<CoachState> = combine(_messages, _inputText, _isLoading, _profilePictureUri) { messages, text, loading, picUri ->
        CoachState(
            messages = messages.reversed(),
            inputText = text,
            isLoading = loading,
            userProfilePictureUri = picUri
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CoachState())

    fun onInputChanged(text: String) {
        _inputText.value = text
    }

    fun onSendMessage() {
        val text = _inputText.value.trim()
        if (text.isBlank()) return

        _inputText.value = ""
        _isLoading.value = true

        viewModelScope.launch {
            try {
                sendChatMessage(text)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
