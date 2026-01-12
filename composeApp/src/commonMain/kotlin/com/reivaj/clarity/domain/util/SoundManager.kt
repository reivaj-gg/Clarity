package com.reivaj.clarity.domain.util

enum class SoundType {
    CORRECT,
    WRONG,
    CLICK,
    GAME_OVER,
    LEVEL_UP
}

interface SoundManager {
    fun playSound(type: SoundType)
    fun stopAll()
    fun release()
}
