package io.github.vekat.spaceevader

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class Enemy(unit: Int) : GameEntity {
    var visible = true

    private val width: Float = unit * 2f
    private val height: Float = unit * 2f

    private val velocity: Float = -200f

    override val positionRect: RectF = RectF(0f, 0f, width, height)
    override val displayRect: RectF = RectF(positionRect)

    override fun update(delta: Float) {
        if (visible) {
            offset(dy = velocity * delta)
        }
    }

    override fun draw(delta: Float, alpha: Float, canvas: Canvas, paint: Paint) {
        if (visible) {
            displayRect.set(positionRect)
            offsetDisplay(dy = velocity * delta * alpha)
            canvas.drawRect(displayRect, paint)
        }
    }
}