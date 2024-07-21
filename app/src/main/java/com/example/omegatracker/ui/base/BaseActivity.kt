package com.example.omegatracker.ui.base

import android.content.Intent
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.auth.AuthActivity
import com.example.omegatracker.ui.start.StartActivity
import com.example.omegatracker.ui.tasks.TasksActivity
import com.omega_r.base.components.OmegaActivity

abstract class BaseActivity : OmegaActivity(), BaseView {

    abstract override val presenter: BasePresenter<out BaseView>

    override fun showScreen(screen: Screens) {
        intent = when (screen) {
            Screens.AuthScreen -> Intent(this, AuthActivity::class.java)
            Screens.StartScreen -> Intent(this, StartActivity::class.java)
            Screens.TasksScreen -> Intent(this, TasksActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

}
