package com.example.omegatracker.ui.base

import android.app.Activity
import android.content.Intent
import com.example.omegatracker.entity.NavigationData
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.auth.AuthActivity
import com.example.omegatracker.ui.start.StartActivity
import com.example.omegatracker.ui.tasks.TasksActivity
import com.example.omegatracker.ui.timer.TimerActivity
import com.omega_r.base.components.OmegaActivity

abstract class BaseActivity : OmegaActivity(), BaseView {

    abstract override val presenter: BasePresenter<out BaseView>

    override fun showScreen(navigationData: NavigationData) {

        val activityClass = getActivityClassForScreen(navigationData.screen)

        val intent = Intent(this, activityClass).apply {
            putExtra("navigation_data", navigationData)
        }

        startActivity(intent)
        finish()
    }

    private fun getActivityClassForScreen(screen: Screens): Class<out Activity> {
        return when (screen) {
            Screens.AuthScreen -> AuthActivity::class.java
            Screens.StartScreen -> StartActivity::class.java
            Screens.TasksScreen -> TasksActivity::class.java
            Screens.TimerScreen -> TimerActivity::class.java
        }
    }

}
