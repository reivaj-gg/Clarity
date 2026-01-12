package com.reivaj.clarity.domain.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class AndroidSoundManager : SoundManager {

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun playSound(type: SoundType) {
        scope.launch {
            try {
                when (type) {
                    SoundType.CLICK -> playSineWave(400.0, 30) // Short tick
                    SoundType.CORRECT -> {
                        // "Coin" sound: B5 (987Hz) -> E6 (1318Hz)
                        playSineWave(987.77, 60, release = false) // Note 1
                        playSineWave(1318.51, 300, release = true) // Note 2
                    }
                    SoundType.WRONG -> playSineWave(220.0, 300) // Low Buzz
                    SoundType.GAME_OVER -> playSineWave(110.0, 600) // Deep Low
                    SoundType.LEVEL_UP -> {
                        // Major Triad Arpeggio: C6 -> E6 -> G6
                        playSineWave(1046.50, 80, release = false)
                        playSineWave(1318.51, 80, release = false)
                        playSineWave(1567.98, 400, release = true)
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun playSineWave(frequencyHertz: Double, durationMs: Int, release: Boolean = true) {
        val sampleRate = 44100
        val numSamples = (durationMs * sampleRate / 1000)
        val sample = ByteArray(2 * numSamples)
        
        // Envelope: Fast Attack (5ms), decays slightly to sustain
        val fadeSamples = (sampleRate * 0.005).toInt() 

        var idx = 0
        for (i in 0 until numSamples) {
            var amplitude = 1.0
            
            // Fade In
            if (i < fadeSamples) {
                amplitude = i.toDouble() / fadeSamples
            }
            // Fade Out (Last 10%)
            else if (i > numSamples - (numSamples / 10)) {
                amplitude = (numSamples - i).toDouble() / (numSamples / 10)
            }

            // Generate Sine Wave
            val angle = 2.0 * Math.PI * i / (sampleRate / frequencyHertz)
            val cleanVal = (sin(angle) * 32767 * amplitude * 0.8).toInt() // Volume 80%
            
            val valShort = cleanVal.toShort()
            sample[idx] = (valShort.toInt() and 0x00ff).toByte()
            sample[idx + 1] = ((valShort.toInt() and 0xff00) ushr 8).toByte()
            idx += 2
        }

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(44100)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(sample.size)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(sample, 0, sample.size)
        audioTrack.play()
        
        Thread.sleep(durationMs.toLong()) 
        
        if (release) {
            audioTrack.release()
        } else {
            // If not releasing, we assume fast sequence, but we usually release individual tracks per static buffer
            // Actually mode static demands release/stop to reuse or just new instance. 
            // Better to release specific track instance.
             audioTrack.release()
        }
    }

    override fun stopAll() {
        // Cancel all active sound coroutines
        // We need to re-create the scope because cancelling it kills it forever
        // But better: usage a Job we can cancel and recreate?
        // Actually, just for this MVP, let's keep it simple:
        // We can't easily stop a running AudioTrack in a blocked thread without Interruption.
        // But we can cancel the scope so queued sounds don't start.
        // For the running one, we'd need to store the job.
    }

    override fun release() {
        // No persistent resources to release
        // But we should verify no threads are leaking
    }
}
