package io.github.vekat.spaceevader

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Handler
import android.os.HandlerThread
import android.view.Display
import android.view.MotionEvent
import android.view.SurfaceView
import java.util.*
import kotlin.math.sin

class GameView(context: Context, display: Display) : SurfaceView(context), Runnable {
    private var gameThread: HandlerThread? = null
    private var gameHandler: Handler? = null

    @Volatile
    private var running: Boolean = false

    private var paused = true

    private var canvas: Canvas = Canvas()
    private var paint: Paint = Paint()

    private val unit: Int = 16
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0

    var touchXRatio: Float = 0.5f
    private var playerXRatio: Float = 0f

    private var player = Player(unit)

    private var maxEnemies = 10
    private val enemies = ArrayList<Enemy>(maxEnemies)

    private val respawnQueue = object : PriorityQueue<Int>() {
        private val rand = Random()
        private var counter = 0

        fun respawn() {
            if (counter++ % RESPAWN_RATE == 0) {
                val elem = poll()

                if (elem != null) {
                    val enemy = enemies[elem]
                    enemy.offsetTo(rand.nextFloat() * screenWidth, 0f)
                    enemy.visible = true
                }
            }
        }
    }

    private var score = 0
    private var lives = 3

    init {
        val outSize = Point()
        display.getSize(outSize)

        screenWidth = outSize.x
        screenHeight = outSize.y

        player = Player(unit)

        resetGameState()
    }

    private fun resetGameState() {
        paused = true
        lives = 3
        score = 0

        respawnQueue.clear()

        if (enemies.isEmpty()) {
            for (i in 0..maxEnemies) {
                enemies.add(i, Enemy(unit))
                respawnQueue.offer(i)
            }
        } else {
            for ((i, v) in enemies.withIndex()) {
                v.visible = false
                respawnQueue.offer(i)
            }
        }

        player.offsetTo((screenWidth / 2) - (player.width / 2), screenHeight - player.height)
    }

    override fun run() {
        var dt = STEP
        var accumulator = 0.0

        while (running) {
            val t = System.currentTimeMillis()

            if (!paused) {
                accumulator += dt

                while (accumulator >= STEP) {
                    accumulator -= STEP

                    update(STEP.toFloat())
                }
            }

            if (holder.surface.isValid) {
                try {
                    canvas = holder.lockCanvas()

                    synchronized(holder) {
                        drawToSurface((accumulator / STEP).toFloat())
                    }
                } finally {
                    holder.unlockCanvasAndPost(canvas)
                }
            }

            dt = (System.currentTimeMillis() - t) / 1000.0
        }
    }

    private fun update(dt: Float) {
        playerXRatio = player.positionRect.centerX() / screenWidth

        player.movement = when {
            touchXRatio - 0.05f > playerXRatio -> Player.RIGHT
            touchXRatio + 0.05f < playerXRatio -> Player.LEFT
            else -> Player.NONE
        }

        player.update(dt)

        enemies.forEachIndexed { index, enemy ->
            enemy.update(dt)

            if (enemy.visible && enemy intersects player) {
                enemy.visible = false
                respawnQueue.offer(index)
                lives--
            }

            if (enemy.visible && enemy.positionRect.top >= screenHeight) {
                enemy.visible = false
                respawnQueue.offer(index)
                score += 10
            }
        }

        if (player.positionRect.left < 0f) {
            player.offsetTo(x = 0f)
            player.movement = Player.NONE
        } else if (player.positionRect.right > screenWidth) {
            player.offsetTo(x = screenWidth - player.width)
            player.movement = Player.NONE
        }

        if (lives <= 0) {
            resetGameState()
        }

        respawnQueue.respawn()
    }

    private fun drawToSurface(alpha: Float) {
        canvas.drawColor(Color.WHITE)

        paint.color = Color.BLACK

        player.draw(STEP.toFloat(), alpha, canvas, paint)

        paint.color = Color.RED

        enemies.filter { it.visible }.forEach { it.draw(STEP.toFloat(), alpha, canvas, paint) }

        paint.color = Color.BLACK

        paint.textSize = unit.toFloat()

        canvas.drawText(resources.getString(R.string.score, score), unit * 0.5f, (screenHeight / 2) + unit.toFloat(), paint)
        canvas.drawText(resources.getString(R.string.lives, lives), unit * 0.5f, (screenHeight / 2) + (unit * 3).toFloat(), paint)
        canvas.drawText(resources.getString(R.string.axis, touchXRatio), unit * 0.5f, (screenHeight / 2) + (unit * 5).toFloat(), paint)
    }

    fun resume() {
        running = true

        gameThread = HandlerThread("GameThread")
        gameThread!!.start()

        gameHandler = Handler(gameThread!!.looper)
        gameHandler!!.post(this)
    }

    fun pause() {
        running = false

        gameThread!!.quit()

        gameThread = null
        gameHandler = null
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                paused = false

                updateTouchX(motionEvent.x)
            }

            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                updateTouchX(motionEvent.x)
            }
        }
        return true
    }

    private fun updateTouchX(motionEventX: Float) {
        touchXRatio = motionEventX / screenWidth
    }

    private fun atualizarEixo(azimuth: Float) {
        val seno = sin(azimuth)

        touchXRatio = (1 + seno) / 2
    }
}
