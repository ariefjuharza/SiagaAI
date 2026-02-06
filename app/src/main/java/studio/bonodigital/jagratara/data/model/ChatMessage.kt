package studio.bonodigital.jagratara.data.model

data class ChatMessage(
    val text: String, val isUser: Boolean, val timestamp: Long = System.currentTimeMillis()
)