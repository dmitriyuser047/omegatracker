package com.example.omegatracker.ui.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.auth.AuthActivity
import com.example.omegatracker.ui.start.StartActivity
import com.example.omegatracker.ui.tasks.TasksActivity
import com.example.omegatracker.ui.timer.TimerActivity
import com.omega_r.base.components.OmegaActivity

abstract class BaseActivity : OmegaActivity(), BaseView {

    abstract override val presenter: BasePresenter<out BaseView>
    companion object {
        fun createIntent(view: AppCompatActivity, screen: Screens) {
            val intent =  when (screen) {
                is Screens.StartScreen -> Intent(view, StartActivity::class.java)
                is Screens.AuthScreen -> Intent(view, AuthActivity::class.java)
                is Screens.TasksScreen -> Intent(view, TasksActivity::class.java)
                is Screens.TimerScreen -> {
                    Intent(view, TimerActivity::class.java).apply {
                        putExtra("taskRun", screen.taskRun)
                    }
                }
            }
            view.startActivity(intent)
            view.finish()
        }
    }
}
