package io.github.vekat.spaceevader

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class GameActivity : AppCompatActivity() {
    lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameView = GameView(this, windowManager.defaultDisplay)

        setContentView(gameView)
    }

    override fun onResume() {
        super.onResume()

        gameView.resume()
    }

    override fun onPause() {
        super.onPause()

        gameView.pause()
    }
}
