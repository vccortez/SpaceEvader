package io.github.vekat.spaceevader

import android.content.Context
import android.graphics.Point
import android.view.Display
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.view_menu.view.*

import io.github.vekat.spaceevader.MenuActivity.IMenuEventListener

class MenuView(context: Context, display: Display, val listener: IMenuEventListener) : RelativeLayout(context) {
    private var screenWidth: Int
    private var screenHeight: Int

    private var isTapping: Boolean = false
    private var downX: Float = 0f
    private var downY: Float = 0f
    private val moveThreshold: Float = 20f

    /**
     * A value inside the range [-1, 1], representing the X coordinate of the latest screen touch
     * from left to right. E.g.: if (xRelativeToWidth == 0f) the latest touch was in the middle.
     */
    var xRelativeToWidth: Float = 0f

    init {
        LayoutInflater.from(context).inflate(R.layout.view_menu, this, true)

        val outSize = Point()

        display.getSize(outSize)

        screenWidth = outSize.x
        screenHeight = outSize.y

        updateView()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                isTapping = true
                return true
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> if (isTapping) {
                isTapping = false
                return performClick()
            }
            MotionEvent.ACTION_MOVE -> {
                if (Math.abs(downX - event.x) > moveThreshold || Math.abs(downY - event.y) > moveThreshold) {
                    isTapping = false
                }

                if (!isTapping) {
                    val xToWidthRatio = event.x / screenWidth

                    // Range conversion from [0, 1] to [-1, 1]
                    xRelativeToWidth = (xToWidthRatio * 2) - 1
                    updateView()
                }

                return true
            }
        }

        return false
    }

    override fun performClick(): Boolean {
        super.performClick()

        when (true) {
            play_label.isEnabled -> listener.onStartGame()
            quit_label.isEnabled -> listener.onCloseGame()
        }

        return true
    }

    private fun updateView() {
        image_arrow.rotation = 45 * xRelativeToWidth
        debug_axis.text = resources.getString(R.string.axis, xRelativeToWidth)

        when (xRelativeToWidth) {
            in -1f..-0.5f -> {
                play_label.isEnabled = true
                quit_label.isEnabled = false
            }
            in 0.5f..1f -> {
                play_label.isEnabled = false
                quit_label.isEnabled = true
            }
            else -> {
                play_label.isEnabled = false
                quit_label.isEnabled = false
            }
        }
    }

}