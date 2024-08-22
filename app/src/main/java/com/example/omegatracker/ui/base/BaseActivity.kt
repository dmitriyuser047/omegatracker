package com.example.omegatracker.ui.base

import android.content.Intent
import android.os.Bundle
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.auth.AuthActivity
import com.example.omegatracker.ui.start.StartActivity
import com.example.omegatracker.ui.tasks.TasksActivity
import com.example.omegatracker.ui.timer.TimerActivity
import com.omega_r.base.components.OmegaActivity

abstract class BaseActivity : OmegaActivity(), BaseView {

    abstract override val presenter: BasePresenter<out BaseView>

    override fun showScreen(screen: Screens, extras: Bundle?) {
        intent = when (screen) {
            Screens.AuthScreen -> Intent(this, AuthActivity::class.java)
            Screens.StartScreen -> Intent(this, StartActivity::class.java)
            Screens.TasksScreen -> Intent(this, TasksActivity::class.java)
            Screens.TimerScreen -> Intent(this, TimerActivity::class.java)
        }
        extras?.let { intent.putExtras(it) }
        startActivity(intent)
        finish()
    }

}
