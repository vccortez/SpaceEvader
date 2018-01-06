package io.github.vekat.spaceevader

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

interface GameEntity {
    val positionRect: RectF
    val displayRect: RectF

    infix fun intersects(other: GameEntity): Boolean = RectF.intersects(this.positionRect, other.positionRect)

    fun offsetTo(x: Float = positionRect.left, y: Float = positionRect.top) {
        positionRect.offsetTo(x, y)
    }

    fun offset(dx: Float = 0f, dy: Float = 0f) {
        positionRect.offset(dx, dy)
    }

    fun offsetDisplay(dx: Float = 0f, dy: Float = 0f) {
        displayRect.set(positionRect)
        displayRect.offset(dx, dy)
    }

    fun update(delta: Float) = Unit

    fun draw(delta: Float, alpha: Float, canvas: Canvas, paint: Paint)
}