package io.github.vekat.spaceevader

import android.content.Context
import android.graphics.Point
import android.view.Display
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.RelativeLayout
import io.github.vekat.spaceevader.MenuActivity.IMenuEventListener
import kotlinx.android.synthetic.main.view_menu.view.*
import kotlin.math.sin

// TODO: passo 2 - adicione a interface apropriada na assinatura da classe `MenuView`.

/**
 * A view que declara e controla o menu principal do jogo.
 */
class MenuView(context: Context, display: Display, private val listener: IMenuEventListener) : RelativeLayout(context) {

    // TODO: passo 6 - implemente no callback da interface utilizada o procedimento para obter a orientação.

    /**
     * Um valor representando a coordenada X do último evento de toque na tela, convertido para uma
     * escala de valores do intervalo [-1, 1].
     * E.g.: Se (xRelativeToWidth == 0f) o último toque foi exatamente no centro da tela.
     */
    var xRelativeToWidth: Float = 0f

    private var screenWidth: Int
    private var screenHeight: Int

    private var isTapping: Boolean = false
    private var downX: Float = 0f
    private var downY: Float = 0f
    private val moveThreshold: Float = 20f

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

                    // conversão de valores em [0, 1] para [-1, 1]
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

    private fun atualizarEixo(azimuth: Float) {
        val seno = sin(azimuth)

        xRelativeToWidth = seno

        updateView()
    }
}
