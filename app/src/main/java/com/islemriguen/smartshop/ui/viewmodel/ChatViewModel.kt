// ============================================================
// FIX 3: ChatViewModel.kt - Ensure ChatMessage is defined here
// ============================================================
// Make sure this file has the ChatMessage class defined

package com.islemriguen.smartshop.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

// ✅ ChatMessage data class (define it here, not in ChatbotScreen)
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val botResponses = mapOf(
        "inventory" to "📦 To manage your inventory:\n1. Go to Products\n2. Click + to add\n3. Edit or delete items\n4. Check stats anytime",
        "statistics" to "📊 Your Statistics:\n• Total Products: Check Dashboard\n• Total Value: Displayed on cards\n• Search to find specific items",
        "export" to "📥 Export your data:\n• CSV for Excel\n• JSON for backup\n• PDF reports",
        "search" to "🔍 Search products:\n1. Go to Search tab\n2. Type product name\n3. Filter results instantly",
        "help" to "❓ I can help with:\n• Inventory management\n• Statistics\n• Exporting data\n• Product search\n\nTry asking about: inventory, statistics, export, search",
        "default" to "😊 I understand you're asking: '{query}'\n\nI can help with inventory, statistics, search, and exports. What would you like to know?"
    )

    fun updateInput(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val message = inputText.value.trim()
        if (message.isEmpty()) return

        viewModelScope.launch {
            // Add user message
            val userMessage = ChatMessage(
                text = message,
                isUser = true
            )
            _messages.value = _messages.value + userMessage
            _inputText.value = ""

            // Simulate typing
            _isLoading.value = true
            kotlinx.coroutines.delay(800)

            // Generate bot response
            val response = generateBotResponse(message)
            val botMessage = ChatMessage(
                text = response,
                isUser = false
            )
            _messages.value = _messages.value + botMessage
            _isLoading.value = false
        }
    }

    private fun generateBotResponse(userMessage: String): String {
        val lowerMessage = userMessage.lowercase()

        return when {
            lowerMessage.contains("inventory") -> botResponses["inventory"]!!
            lowerMessage.contains("statistics") || lowerMessage.contains("stats") -> botResponses["statistics"]!!
            lowerMessage.contains("export") || lowerMessage.contains("share") -> botResponses["export"]!!
            lowerMessage.contains("search") || lowerMessage.contains("find") -> botResponses["search"]!!
            lowerMessage.contains("help") || lowerMessage.contains("what") -> botResponses["help"]!!
            else -> botResponses["default"]!!.replace("{query}", userMessage)
        }
    }
}