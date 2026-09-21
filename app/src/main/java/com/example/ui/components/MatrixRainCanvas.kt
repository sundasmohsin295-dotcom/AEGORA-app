package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import com.example.hardware.DeviceThermalState
import com.example.hardware.rememberDeviceThermalState
import kotlin.random.Random

private val MATRIX_CHARS = "0123456789ABCDEFｦｱｳｴｵｶｷｹｺｻｼｽｾｿﾀﾂﾃﾅﾆﾇﾈﾊﾋﾎﾏﾐﾑﾒﾓﾔﾕﾗﾘﾜ".toList()

private data class MatrixDrop(
  var y: Float,
  val speed: Float,
  val charHistory: MutableList<Char>,
  val length: Int,
  val alpha: Float
)

/**
 * High-performance Matrix Rain Canvas for AI analyst card processing states.
 * Dynamically adjusts frame rates and drop density based on hardware thermal status
 * to prevent thermal throttling and conserve battery.
 */
@Composable
fun MatrixRainCanvas(
  modifier: Modifier = Modifier,
  primaryColor: Color = Color(0xFF34D399),
  leadColor: Color = Color(0xFFE6FFFA),
  dropCount: Int = 18
) {
  val thermalState by rememberDeviceThermalState()

  // Adapt frame rate delay and drop count dynamically to hardware thermal conditions
  val (frameDelayMs, effectiveDropCount) = when (thermalState) {
    DeviceThermalState.NORMAL -> 35L to dropCount
    DeviceThermalState.MODERATE -> 65L to (dropCount * 3 / 4).coerceAtLeast(6)
    DeviceThermalState.SEVERE -> 120L to (dropCount / 2).coerceAtLeast(4)
    DeviceThermalState.CRITICAL -> 200L to (dropCount / 3).coerceAtLeast(2)
  }

  val drops = remember { mutableStateListOf<MatrixDrop>() }
  var frameTick by remember { mutableLongStateOf(0L) }

  // Lightweight frame animation ticker adapted to thermal state
  LaunchedEffect(frameDelayMs) {
    while (true) {
      kotlinx.coroutines.delay(frameDelayMs)
      frameTick++
    }
  }

  Canvas(
    modifier = modifier
      .fillMaxSize()
      .testTag("matrix_rain_canvas")
  ) {
    val canvasWidth = size.width
    val canvasHeight = size.height
    if (canvasWidth <= 0 || canvasHeight <= 0) return@Canvas

    val columnWidth = canvasWidth / effectiveDropCount.coerceAtLeast(1)

    // Initialize drops if empty or resized due to thermal state
    if (drops.size != effectiveDropCount) {
      drops.clear()
      for (i in 0 until effectiveDropCount) {
        val len = Random.nextInt(6, 14)
        val initialChars = MutableList(len) { MATRIX_CHARS[Random.nextInt(MATRIX_CHARS.size)] }
        drops.add(
          MatrixDrop(
            y = Random.nextFloat() * -canvasHeight * 0.8f,
            speed = Random.nextFloat() * 12f + 8f,
            charHistory = initialChars,
            length = len,
            alpha = Random.nextFloat() * 0.5f + 0.5f
          )
        )
      }
    }

    // Update and draw drops
    val nativePaint = android.graphics.Paint().apply {
      isAntiAlias = true
      textSize = columnWidth * 0.75f
      typeface = android.graphics.Typeface.MONOSPACE
      isFakeBoldText = true
    }

    drops.forEachIndexed { colIndex, drop ->
      // Advance drop position
      drop.y += drop.speed
      if (drop.y - (drop.length * columnWidth) > canvasHeight) {
        // Reset to top
        drop.y = -columnWidth * Random.nextInt(1, 5)
        drop.charHistory.clear()
        repeat(drop.length) {
          drop.charHistory.add(MATRIX_CHARS[Random.nextInt(MATRIX_CHARS.size)])
        }
      } else if (frameTick % 2L == 0L && drop.charHistory.isNotEmpty()) {
        // Randomly mutate head or tail character
        val mutateIndex = Random.nextInt(drop.charHistory.size)
        drop.charHistory[mutateIndex] = MATRIX_CHARS[Random.nextInt(MATRIX_CHARS.size)]
      }

      val x = colIndex * columnWidth + (columnWidth * 0.15f)

      // Draw trails from tail to head
      drop.charHistory.forEachIndexed { charIdx, char ->
        val charY = drop.y - ((drop.charHistory.size - 1 - charIdx) * columnWidth)
        if (charY in -columnWidth..(canvasHeight + columnWidth)) {
          val isHead = charIdx == drop.charHistory.size - 1
          val trailRatio = (charIdx + 1).toFloat() / drop.charHistory.size.toFloat()

          val drawColor = if (isHead) {
            leadColor
          } else {
            primaryColor.copy(alpha = drop.alpha * trailRatio * 0.85f)
          }

          nativePaint.color = android.graphics.Color.argb(
            (drawColor.alpha * 255).toInt(),
            (drawColor.red * 255).toInt(),
            (drawColor.green * 255).toInt(),
            (drawColor.blue * 255).toInt()
          )

          drawContext.canvas.nativeCanvas.drawText(
            char.toString(),
            x,
            charY,
            nativePaint
          )
        }
      }
    }
  }
}
