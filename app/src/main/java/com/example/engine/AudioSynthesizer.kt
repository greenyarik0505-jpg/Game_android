package com.example.engine

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class AudioSynthesizer {
    private val sampleRate = 22050
    private val scope = CoroutineScope(Dispatchers.Default)

    private var lastEngineSoundTime = 0L

    fun playEngineSound(speedRatio: Float, throttle: Float) {
        if (throttle <= 0.05f) return
        val now = System.currentTimeMillis()
        if (now - lastEngineSoundTime < 180L) return
        lastEngineSoundTime = now
        scope.launch {
            val baseFreq = 80.0 + speedRatio * 180.0 + throttle * 60.0
            val durationMs = 120
            val numSamples = sampleRate * durationMs / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val sample = sin(2.0 * Math.PI * baseFreq * t) * 0.25 + sin(2.0 * Math.PI * (baseFreq * 0.5) * t) * 0.15
                buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    fun playCrystalPickup() {
        scope.launch {
            val durationMs = 150
            val numSamples = sampleRate * durationMs / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = if (i < numSamples / 2) 880.0 else 1320.0
                val envelope = 1.0 - (i.toDouble() / numSamples)
                val sample = sin(2.0 * Math.PI * freq * t) * envelope * 0.4
                buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    fun playGravityShift() {
        scope.launch {
            val durationMs = 300
            val numSamples = sampleRate * durationMs / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = 200.0 + (i.toDouble() / numSamples) * 600.0
                val sample = sin(2.0 * Math.PI * freq * t) * 0.35
                buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    fun playImpactExplosion() {
        scope.launch {
            val durationMs = 250
            val numSamples = sampleRate * durationMs / 1000
            val buffer = ShortArray(numSamples)
            val random = java.util.Random()
            for (i in 0 until numSamples) {
                val envelope = 1.0 - (i.toDouble() / numSamples)
                val noise = (random.nextDouble() * 2.0 - 1.0) * envelope * 0.5
                buffer[i] = (noise * Short.MAX_VALUE).toInt().toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    fun playLowEnergyAlert() {
        scope.launch {
            val durationMs = 200
            val numSamples = sampleRate * durationMs / 1000
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val sample = sin(2.0 * Math.PI * 440.0 * t) * 0.3
                buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    private fun playPcmBuffer(buffer: ShortArray) {
        try {
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
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            // Release resources automatically after playback
            scope.launch {
                kotlinx.coroutines.delay(400)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // Fallback gracefully if audio hardware is unavailable
        }
    }
}
