package com.example.omegatracker.ui.base.activity

import android.content.Context
import android.content.Intent
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.auth.AuthActivity
import com.example.omegatracker.ui.main.MainActivity
import com.example.omegatracker.ui.start.StartActivity
import com.example.omegatracker.ui.timer.TimerActivity
import com.omega_r.base.components.OmegaActivity

abstract class BaseActivity : OmegaActivity(), BaseView {

    companion object {
        fun startScreen(context: Context, screen: Screens) {
            val intent =  when (screen) {
                is Screens.StartScreen -> Intent(context, StartActivity::class.java)
                is Screens.AuthScreen -> Intent(context, AuthActivity::class.java)
                is Screens.TasksScreen -> Intent(context, MainActivity::class.java)
                is Screens.TimerScreen -> {
                    Intent(context, TimerActivity::class.java).apply {
                        putExtra("taskRun", screen.taskRun)
                    }
                }
            }
            context.startActivity(intent)
        }
    }

    abstract override val presenter: BasePresenter<out BaseView>

}
