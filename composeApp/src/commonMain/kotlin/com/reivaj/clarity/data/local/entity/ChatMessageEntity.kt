package com.reivaj.clarity.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.reivaj.clarity.domain.model.ChatMessage
import kotlinx.datetime.Instant

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val info: String,
    val timestamp: Long,
    val isUser: Boolean,
    val isError: Boolean
)

fun ChatMessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        content = content,
        info = info,
        timestamp = Instant.fromEpochMilliseconds(timestamp),
        isUser = isUser,
        isError = isError
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        id = id,
        content = content,
        info = info,
        timestamp = timestamp.toEpochMilliseconds(),
        isUser = isUser,
        isError = isError
    )
}
