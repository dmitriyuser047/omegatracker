package com.example.omegatracker.di

import android.content.Context
import com.example.omegatracker.data.RepositoryImpl
import com.example.omegatracker.data.model.UserManager
import com.example.omegatracker.di.builders.RetrofitComponent
import com.example.omegatracker.service.TasksManager
import com.example.omegatracker.service.TasksRunner
import com.example.omegatracker.service.TasksService
import com.example.omegatracker.ui.auth.AuthInstruction
import com.example.omegatracker.ui.main.AddCustomTask
import com.example.omegatracker.ui.statistics.StatisticsGraphHelper
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        AuthModule::class,
        YouTrackModule::class,
        TasksModule::class,
        ServiceModule::class,
        TimerModule::class,
        StatisticsModule::class
    ]
)
interface AppComponent {
    fun context(): Context
    fun repository(): RepositoryImpl
    fun authFragment(): AuthInstruction
    fun retrofitComponentBuilder(): RetrofitComponent.Builder
    fun userManager(): UserManager
    fun tasksManager(): TasksManager
    fun tasksService(): TasksService
    fun tasksRunner(): TasksRunner
    fun addTaskDialog(): AddCustomTask
    fun statisticsGraphHelper(): StatisticsGraphHelper
}