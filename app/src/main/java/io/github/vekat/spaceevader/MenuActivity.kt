package io.github.vekat.spaceevader

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MenuActivity : AppCompatActivity() {
    lateinit var menuView: MenuView

    private val listener: IMenuEventListener = object : IMenuEventListener {
        override fun onStartGame() {
            this@MenuActivity.startActivity(Intent(this@MenuActivity, GameActivity::class.java))
        }

        override fun onCloseGame() {
            this@MenuActivity.finish()
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
