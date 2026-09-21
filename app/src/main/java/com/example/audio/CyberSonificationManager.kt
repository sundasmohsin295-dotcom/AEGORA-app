package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.SoundPool
import android.util.Log
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sin

/**
 * Cyber Sonification Engine (The Geiger Counter Effect).
 *
 * Implements high-frequency micro-acoustic clicks via Android SoundPool and an in-memory
 * synthesized PCM click waveform. When analyzing live telemetry or when ThreatScore is dynamically
 * escalating, the click frequency accelerates exponentially up to 100/100, creating intense
 * psychological immersion and tactile auditory tension.
 */
class CyberSonificationManager private constructor(private val context: Context) {

  private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
  private var geigerJob: Job? = null

  private var soundPool: SoundPool? = null
  private var clickSoundId: Int = 0
  private var isSoundLoaded: Boolean = false

  @Volatile
  private var currentThreatScore: Int = 0

  @Volatile
  private var isMuted: Boolean = false

  init {
    initSoundPool()
  }

  private fun initSoundPool() {
    val audioAttributes = AudioAttributes.Builder()
      .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
      .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
      .build()

    soundPool = SoundPool.Builder()
      .setMaxStreams(8)
      .setAudioAttributes(audioAttributes)
      .build()

    soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
      if (status == 0 && sampleId == clickSoundId) {
        isSoundLoaded = true
      }
    }

    // Generate a high-tech synthesized acoustic click WAV file in cache
    try {
      val clickWav = generateSynthesizedClickWav(context)
      clickSoundId = soundPool?.load(clickWav.absolutePath, 1) ?: 0
    } catch (e: Exception) {
      Log.w(TAG, "SoundPool synthesis fallback: ${e.message}")
    }
  }

  /**
   * Sets the current ThreatScore (0 to 100).
   * Automatically calculates click intervals and pitch shift.
   */
  fun setThreatScore(score: Int) {
    val clamped = score.coerceIn(0, 100)
    currentThreatScore = clamped
  }

  /**
   * Starts the Geiger Counter acoustic ticking loop.
   */
  fun startGeigerMonitoring(initialScore: Int = 0) {
    setThreatScore(initialScore)
    if (geigerJob?.isActive == true) return

    geigerJob = coroutineScope.launch {
      while (isActive) {
        if (!isMuted && currentThreatScore > 0) {
          playAcousticTick(currentThreatScore)
        }

        // Delay interval maps non-linearly from score 0 -> 100
        // Score 1-20: 800ms - 1500ms (occasional ambient click)
        // Score 50: ~300ms (steady concern)
        // Score 85+: 60ms - 120ms (frenetic radiation Geiger chatter)
        val delayMs = calculateDelayForThreatScore(currentThreatScore)
        delay(delayMs)
      }
    }
  }

  /**
   * Stops active sonification loop.
   */
  fun stopGeigerMonitoring() {
    geigerJob?.cancel()
    geigerJob = null
  }

  fun setMuted(muted: Boolean) {
    isMuted = muted
  }

  private fun playAcousticTick(score: Int) {
    val pool = soundPool ?: return
    if (!isSoundLoaded || clickSoundId == 0) return

    // Dynamic pitch modulation: 0.95f at low threat to 1.45f at critical 100 threat
    val rate = 0.95f + ((score.toFloat() / 100f) * 0.5f)
    // Volume scales with threat level: 0.25f up to 0.85f
    val volume = 0.20f + ((score.toFloat() / 100f) * 0.65f)

    try {
      pool.play(clickSoundId, volume, volume, 1, 0, rate)
    } catch (e: Exception) {
      // Ignored
    }
  }

  private fun calculateDelayForThreatScore(score: Int): Long {
    if (score <= 0) return 2000L
    // Inverse exponential calculation for Geiger counter clicking frequency
    // Score 10 -> ~1100ms
    // Score 50 -> ~320ms
    // Score 80 -> ~120ms
    // Score 95+ -> ~55ms
    val normalized = (score.toFloat() / 100f).coerceIn(0.01f, 1.0f)
    val ms = 60L + ((1.0f - normalized) * (1.0f - normalized) * 1400L).toLong()
    // Add realistic randomized micro-jitter (+- 15%) to mimic authentic nuclear Geiger radiation
    val jitter = (Math.random() * (ms * 0.2f) - (ms * 0.1f)).toLong()
    return (ms + jitter).coerceAtLeast(45L)
  }

  fun release() {
    stopGeigerMonitoring()
    soundPool?.release()
    soundPool = null
    isSoundLoaded = false
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

    /**
     * Synthesizes a crisp, high-tech micro-click audio wave (16-bit PCM Mono, 44.1 kHz, 15ms duration)
     * and saves as a standard RIFF/WAV file in cache for SoundPool ingestion.
     */
    private fun generateSynthesizedClickWav(context: Context): File {
      val outFile = File(context.cacheDir, "geiger_click_synth.wav")
      if (outFile.exists() && outFile.length() > 100) {
        return outFile
      }

      val sampleRate = 44100
      val durationMs = 18
      val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
      val pcmData = ShortArray(numSamples)

      // Synthesize high-frequency exponential decayed resonant click (approx 2400 Hz down-chirp)
      for (i in 0 until numSamples) {
        val t = i.toDouble() / sampleRate
        val progress = i.toDouble() / numSamples
        val decay = Math.exp(-progress * 14.0) // rapid acoustic transient decay
        val frequency = 2800.0 - (progress * 1200.0) // down-chirp snap
        val sampleVal = (sin(2.0 * Math.PI * frequency * t) * decay * 32000.0).toInt()
        pcmData[i] = sampleVal.coerceIn(-32768, 32767).toShort()
      }

      FileOutputStream(outFile).use { fos ->
        writeWavHeader(fos, sampleRate, 1, 16, numSamples * 2)
        val byteBuffer = ByteBuffer.allocate(numSamples * 2).order(ByteOrder.LITTLE_ENDIAN)
        for (sample in pcmData) {
          byteBuffer.putShort(sample)
        }
        fos.write(byteBuffer.array())
      }

      return outFile
    }

    private fun writeWavHeader(
      out: FileOutputStream,
      sampleRate: Int,
      channels: Int,
      bitsPerSample: Int,
      dataSize: Int
    ) {
      val totalDataLen = dataSize + 36
      val byteRate = sampleRate * channels * bitsPerSample / 8

      val header = ByteArray(44)
      header[0] = 'R'.code.toByte()
      header[1] = 'I'.code.toByte()
      header[2] = 'F'.code.toByte()
      header[3] = 'F'.code.toByte()
      header[4] = (totalDataLen and 0xff).toByte()
      header[5] = ((totalDataLen shr 8) and 0xff).toByte()
      header[6] = ((totalDataLen shr 16) and 0xff).toByte()
      header[7] = ((totalDataLen shr 24) and 0xff).toByte()
      header[8] = 'W'.code.toByte()
      header[9] = 'A'.code.toByte()
      header[10] = 'V'.code.toByte()
      header[11] = 'E'.code.toByte()
      header[12] = 'f'.code.toByte()
      header[13] = 'm'.code.toByte()
      header[14] = 't'.code.toByte()
      header[15] = ' '.code.toByte()
      header[16] = 16 // 16 bytes for fmt chunk
      header[17] = 0
      header[18] = 0
      header[19] = 0
      header[20] = 1 // PCM = 1
      header[21] = 0
      header[22] = channels.toByte()
      header[23] = 0
      header[24] = (sampleRate and 0xff).toByte()
      header[25] = ((sampleRate shr 8) and 0xff).toByte()
      header[26] = ((sampleRate shr 16) and 0xff).toByte()
      header[27] = ((sampleRate shr 24) and 0xff).toByte()
      header[28] = (byteRate and 0xff).toByte()
      header[29] = ((byteRate shr 8) and 0xff).toByte()
      header[30] = ((byteRate shr 16) and 0xff).toByte()
      header[31] = ((byteRate shr 24) and 0xff).toByte()
      header[32] = (channels * bitsPerSample / 8).toByte() // block align
      header[33] = 0
      header[34] = bitsPerSample.toByte()
      header[35] = 0
      header[36] = 'd'.code.toByte()
      header[37] = 'a'.code.toByte()
      header[38] = 't'.code.toByte()
      header[39] = 'a'.code.toByte()
      header[40] = (dataSize and 0xff).toByte()
      header[41] = ((dataSize shr 8) and 0xff).toByte()
      header[42] = ((dataSize shr 16) and 0xff).toByte()
      header[43] = ((dataSize shr 24) and 0xff).toByte()

      out.write(header, 0, 44)
    }
  }
}
