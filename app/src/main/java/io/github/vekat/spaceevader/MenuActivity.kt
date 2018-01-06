package io.github.vekat.spaceevader

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MenuActivity : AppCompatActivity() {
    private lateinit var menuView: MenuView

    private val listener: IMenuEventListener = object : IMenuEventListener {
        override fun onStartGame() {
            // TODO: start game activity
        }

        override fun onCloseGame() {
            // TODO: close menu activity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        menuView = MenuView(this, windowManager.defaultDisplay, listener)

        setContentView(menuView)
    }

    interface IMenuEventListener {
        fun onStartGame()
        fun onCloseGame()
    }
}
