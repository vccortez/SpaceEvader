package io.github.vekat.spaceevader

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class Player(unit: Int) : GameEntity {
    companion object {
        val LEFT = -1
        val NONE = 0
        val RIGHT = 1
    }

    var movement = NONE

    val width: Float = unit * 4f
    val height: Float = unit * 2f

    private val velocity: Float = 250f

    override val positionRect: RectF = RectF(0f, 0f, width, height)
    override val displayRect: RectF = RectF(positionRect)

    override fun update(delta: Float) {
        when (movement) {
            LEFT -> offset(-velocity * delta)
            RIGHT -> offset(+velocity * delta)
        }
    }

    override fun draw(delta: Float, alpha: Float, canvas: Canvas, paint: Paint) {
        when (movement) {
            LEFT -> offsetDisplay(-velocity * delta * alpha)
            RIGHT -> offsetDisplay(+velocity * delta * alpha)
        }

        canvas.drawRect(displayRect, paint)
    }
}