package com.example.hardware

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HardwareThermalManagerTest {

  @Test
  fun testThermalState_defaultsToNormalAndUpdates() {
    val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    val manager = HardwareThermalManager.getInstance(context)

    // Initial state check
    assertEquals(DeviceThermalState.NORMAL, manager.thermalState.value)
    assertFalse(manager.thermalState.value.isThrottled)

    // Simulate severe thermal condition
    manager.updateSimulatedThermalState(DeviceThermalState.SEVERE)
    assertEquals(DeviceThermalState.SEVERE, manager.thermalState.value)
    assertTrue(manager.thermalState.value.isThrottled)
    assertTrue(manager.thermalState.value.isDegraded)

    // Reset back to normal
    manager.updateSimulatedThermalState(DeviceThermalState.NORMAL)
    assertEquals(DeviceThermalState.NORMAL, manager.thermalState.value)
    assertFalse(manager.thermalState.value.isThrottled)
  }
}
