package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Enterprise Audio & Haptic Sonification Engine.
 * Implements micro-acoustic cues, zero-day warbles, threat neutralized chimes,
 * and high-fidelity haptic pulses for cybersecurity telemetry.
 */
class CyberSonificationManager private constructor(private val context: Context) {

  private var soundPool: SoundPool? = null
  private var neutralizedSoundId = 0
  private var zeroDaySoundId = 0
  private var microClickSoundId = 0
  private var glitchSoundId = 0
  private var isMuted = false

  init {
    initSoundPool()
  }

  private fun initSoundPool() {
    try {
      val attrs = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

      soundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(attrs)
        .build()

      // Generate synthetic audio waveform files for instant playback
      neutralizedSoundId = generateToneFile(freqStart = 440.0, freqEnd = 880.0, durationMs = 250)
      zeroDaySoundId = generateToneFile(freqStart = 880.0, freqEnd = 220.0, durationMs = 400)
      microClickSoundId = generateToneFile(freqStart = 1200.0, freqEnd = 1200.0, durationMs = 30)
      glitchSoundId = generateToneFile(freqStart = 300.0, freqEnd = 150.0, durationMs = 150)
    } catch (e: Exception) {
      Log.w(TAG, "Failed to initialize SoundPool: ${e.message}")
    }
  }

  /**
   * Generates a dynamic linear frequency sweep WAV file in app cache and loads into SoundPool.
   */
  private fun generateToneFile(freqStart: Double, freqEnd: Double, durationMs: Int): Int {
    return try {
      val sampleRate = 22050
      val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
      val pcmData = ShortArray(numSamples)

      for (i in 0 until numSamples) {
        val t = i.toDouble() / sampleRate
        val progress = i.toDouble() / numSamples
        val currentFreq = freqStart + (freqEnd - freqStart) * progress
        val sample = Math.sin(2.0 * Math.PI * currentFreq * t)
        // Envelope: attack and decay
        val envelope = Math.sin(Math.PI * progress)
        pcmData[i] = (sample * envelope * Short.MAX_VALUE * 0.7).toInt().toShort()
      }

      val wavFile = File(context.cacheDir, "tone_${freqStart.toInt()}_${freqEnd.toInt()}_$durationMs.wav")
      FileOutputStream(wavFile).use { fos ->
        writeWavHeader(fos, sampleRate, numSamples * 2)
        val byteBuffer = ByteBuffer.allocate(numSamples * 2).order(ByteOrder.LITTLE_ENDIAN)
        for (s in pcmData) {
          byteBuffer.putShort(s)
        }
        fos.write(byteBuffer.array())
      }

      soundPool?.load(wavFile.absolutePath, 1) ?: 0
    } catch (e: Exception) {
      Log.w(TAG, "Tone synthesis fallback: ${e.message}")
      0
    }
  }

  private fun writeWavHeader(out: FileOutputStream, sampleRate: Int, dataSize: Int) {
    val totalSize = 36 + dataSize
    val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
    header.put("RIFF".toByteArray())
    header.putInt(totalSize)
    header.put("WAVE".toByteArray())
    header.put("fmt ".toByteArray())
    header.putInt(16) // Subchunk1Size (16 for PCM)
    header.putShort(1.toShort()) // AudioFormat (1 for PCM)
    header.putShort(1.toShort()) // NumChannels (1 = Mono)
    header.putInt(sampleRate)
    header.putInt(sampleRate * 2) // ByteRate
    header.putShort(2.toShort()) // BlockAlign
    header.putShort(16.toShort()) // BitsPerSample
    header.put("data".toByteArray())
    header.putInt(dataSize)
    out.write(header.array())
  }

  fun playThreatNeutralizedSound() {
    if (isMuted) return
    triggerHaptic(60)
    soundPool?.let { pool ->
      if (neutralizedSoundId != 0) {
        pool.play(neutralizedSoundId, 1f, 1f, 1, 0, 1f)
      }
    }
  }

  fun playZeroDayAlert() {
    if (isMuted) return
    triggerHaptic(120)
    soundPool?.let { pool ->
      if (zeroDaySoundId != 0) {
        pool.play(zeroDaySoundId, 1f, 1f, 1, 0, 1f)
      }
    }
  }

  fun playMicroClick() {
    if (isMuted) return
    triggerHaptic(20)
    soundPool?.let { pool ->
      if (microClickSoundId != 0) {
        pool.play(microClickSoundId, 0.6f, 0.6f, 0, 0, 1f)
      }
    }
  }

  fun playGlitchSound() {
    if (isMuted) return
    triggerHaptic(50)
    soundPool?.let { pool ->
      if (glitchSoundId != 0) {
        pool.play(glitchSoundId, 0.8f, 0.8f, 0, 0, 1f)
      }
    }
  }

  fun toggleMute(): Boolean {
    isMuted = !isMuted
    return isMuted
  }

  fun isAudioMuted(): Boolean = isMuted

  fun triggerHaptic(durationMs: Long = 40) {
    try {
      val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      }

      vibrator?.let { v ->
        if (v.hasVibrator()) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
          } else {
            @Suppress("DEPRECATION")
            v.vibrate(durationMs)
          }
        }
      }
    } catch (e: Exception) {
      Log.w(TAG, "Haptic trigger error: ${e.message}")
    }
  }

  fun release() {
    try {
      soundPool?.release()
      soundPool = null
    } catch (e: Exception) {
      Log.w(TAG, "SoundPool release error: ${e.message}")
    }
  }

  companion object {
    private const val TAG = "CyberSonification"

    @Volatile
    private var INSTANCE: CyberSonificationManager? = null

    fun getInstance(context: Context): CyberSonificationManager {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: CyberSonificationManager(context.applicationContext).also { INSTANCE = it }
      }
    }
  }
}
