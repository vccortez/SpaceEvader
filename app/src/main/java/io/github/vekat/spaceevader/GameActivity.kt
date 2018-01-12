package io.github.vekat.spaceevader

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * A activity secundária da aplicação que apresenta o jogo.
 */
class GameActivity : AppCompatActivity() {
    lateinit var gameView: GameView

    // TODO: passo 7 - declare as variáveis como propriedades da activity.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameView = GameView(this, windowManager.defaultDisplay)

        setContentView(gameView)

        // TODO: passo 9 - inicialize e configure as variáveis declaradas no passo 7.
    }

    override fun onResume() {
        super.onResume()

        gameView.resume()

        // TODO: passo 10 - inicie o recebimento de eventos no método `onResume()`.
    }

    override fun onPause() {
        super.onPause()

        gameView.pause()

        // TODO: passo 11 - cancele o recebimento de eventos no método `onPause()`.
    }
}
