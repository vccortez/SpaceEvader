package io.github.vekat.spaceevader

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

// TODO: passo 1 - se necessário, declare as variáveis externas à activity.

/**
 * A activity inicial da aplicação que apresenta o menu principal do jogo.
 */
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

    // TODO: passo 1 - declare as variáveis como propriedades da activity.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        menuView = MenuView(this, windowManager.defaultDisplay, listener)

        setContentView(menuView)

        // TODO: passo 3 - inicialize e configure as variáveis declaradas no passo 1.
    }

    // TODO: passo 4 - adicione o método `onResume()` e inicie o recebimento de eventos.

    // TODO: passo 5 - adicione o método `onPause()` e cancele o recebimento de eventos.

    interface IMenuEventListener {
        fun onStartGame()
        fun onCloseGame()
    }
}
