package io.github.vekat.spaceevader

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.MotionEvent
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowSensorManager

@RunWith(RobolectricTestRunner::class)
class GameActivityTest {
    lateinit var activity: GameActivity
    lateinit var view: GameView

    lateinit var sensorManager: SensorManager
    lateinit var shadowSensorManager: ShadowSensorManager
    lateinit var accelerometer: Sensor
    lateinit var magnetometer: Sensor

    @Before
    fun setup() {
        val fieldType = Sensor::class.java.getDeclaredField("mType")
        fieldType.isAccessible = true

        accelerometer = Shadow.newInstanceOf(Sensor::class.java)
        fieldType.set(accelerometer, Sensor.TYPE_ACCELEROMETER)

        magnetometer = Shadow.newInstanceOf(Sensor::class.java)
        fieldType.set(magnetometer, Sensor.TYPE_MAGNETIC_FIELD)

        sensorManager = RuntimeEnvironment.application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shadowSensorManager = Shadows.shadowOf(sensorManager)

        shadowSensorManager.addSensor(accelerometer.type, accelerometer)
        shadowSensorManager.addSensor(magnetometer.type, magnetometer)

        activity = Robolectric.setupActivity(GameActivity::class.java)
        view = activity.gameView
    }

    @Test
    fun shouldMovePlayerWithTouch() {
        val touchEvent = MotionEvent.obtain(200, 300, MotionEvent.ACTION_MOVE, 10f, 10f, 0)

        val beforeTouch = view.touchXRatio

        view.dispatchTouchEvent(touchEvent)
        view.performClick()

        assertNotEquals(beforeTouch, view.touchXRatio)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun shouldStartGameActivityWithSensor() {
        val accelerometerEvent = shadowSensorManager.createSensorEvent()
        val magnetometerEvent = shadowSensorManager.createSensorEvent()

        val sensorField = SensorEvent::class.java.getField("sensor")
        sensorField.isAccessible = true

        sensorField.set(accelerometerEvent, accelerometer)
        sensorField.set(magnetometerEvent, magnetometer)

        val valuesField = SensorEvent::class.java.getField("values")
        valuesField.isAccessible = true

        valuesField.set(accelerometerEvent, floatArrayOf(0f, 0f, 10f))
        valuesField.set(magnetometerEvent, floatArrayOf(50f, 50f, 0f))

        val listenersField = ShadowSensorManager::class.java.getDeclaredField("listeners")
        listenersField.isAccessible = true

        val listeners = listenersField.get(shadowSensorManager) as List<SensorEventListener>

        val beforeEvents = view.touchXRatio

        val viewListener = listeners.firstOrNull()

        assertNotNull(viewListener)

        viewListener?.onSensorChanged(accelerometerEvent)
        viewListener?.onSensorChanged(magnetometerEvent)

        view.performClick()

        assertNotEquals("Should be different", beforeEvents, view.touchXRatio)
    }
}