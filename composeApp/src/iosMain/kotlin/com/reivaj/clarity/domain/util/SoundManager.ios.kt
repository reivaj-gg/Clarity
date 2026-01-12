package com.reivaj.clarity.domain.util

import platform.AudioToolbox.AudioServicesPlaySystemSound
import platform.AudioToolbox.kSystemSoundID_Vibrate

class IosSoundManager : SoundManager {
    override fun playSound(type: SoundType) {
        // iOS System Sound IDs: http://iphonedevwiki.net/index.php/AudioServices
        val soundId = when (type) {
            SoundType.CORRECT -> 1057u // Tock (Clean click) or 1001
            SoundType.WRONG -> 1053u // Low buzz / Error
            SoundType.CLICK -> 1104u // Keyboard Tock
            SoundType.GAME_OVER -> 1073u // Buzz
            SoundType.LEVEL_UP -> 1016u // Tweet / Chime
        }
        AudioServicesPlaySystemSound(soundId)
    }

    override fun release() {
        // No explicit release needed for system sounds
    }

    override fun stopAll() {
        // System sounds cannot be stopped mid-play on iOS easily via AudioServices
    }
}
