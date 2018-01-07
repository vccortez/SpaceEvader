package io.github.vekat.spaceevader

import android.content.Intent
import android.view.MotionEvent
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
class MenuActivityTest {
    lateinit var activity: MenuActivity
    lateinit var view: MenuView

    @Before
    fun setup() {
        activity = Robolectric.setupActivity(MenuActivity::class.java)
        view = activity.menuView
    }

    @Test
    fun shouldStartGameActivityOnClick() {
        val touchEvent = MotionEvent.obtain(200, 300, MotionEvent.ACTION_MOVE, 10f, 10f, 0)

        view.dispatchTouchEvent(touchEvent)
        view.performClick()

        val expectedIntent = Intent(activity, GameActivity::class.java)
        val actualIntent = ShadowApplication.getInstance().nextStartedActivity

        assertEquals(expectedIntent.component, actualIntent.component)
    }
}