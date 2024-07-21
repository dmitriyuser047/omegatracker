package com.example.omegatracker

import android.app.Application
import com.example.omegatracker.db.database.TaskBase
import com.example.omegatracker.di.AppComponent
import com.example.omegatracker.di.AppModule
import com.example.omegatracker.di.AuthModule
import com.example.omegatracker.di.DaggerAppComponent
import com.example.omegatracker.di.RetrofitComponent
import com.example.omegatracker.di.ServiceModule
import com.example.omegatracker.di.YouTrackModule


open class OmegaTrackerApplication : Application() {

    companion object {
        lateinit var appComponent: AppComponent
        lateinit var retrofitComponent: RetrofitComponent
        lateinit var taskDataBase: TaskBase

        fun retrofitChangeUrl(newUrl: String) {
            if (newUrl != appComponent.userManager().getUserUrl()) {
                retrofitComponent = appComponent.retrofitComponentBuilder()
                    .baseUrl(newUrl)
                    .build()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        taskDataBase = TaskBase.getDatabase(this)
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .authModule(AuthModule())
            .youTrackModule(YouTrackModule())
            .serviceModule(ServiceModule())
            .build()
        val userUrl = appComponent.userManager().getUserUrl()
        retrofitComponent = if (appComponent.userManager().getUserUrl() != null) {
            appComponent.retrofitComponentBuilder()
                .baseUrl(userUrl.toString())
                .build()
        } else {
            appComponent.retrofitComponentBuilder()
                .baseUrl("https://example.youtrack.cloud")
                .build()
        }
    }


}