package com.example.omegatracker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.omegatracker.data.RepositoryImpl
import com.example.omegatracker.data.model.UserManager
import com.example.omegatracker.service.TasksManager
import com.example.omegatracker.service.TasksRunner
import com.example.omegatracker.service.TasksService
import com.example.omegatracker.ui.auth.AuthInstruction
import com.example.omegatracker.ui.tasks.TasksTracking
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, AuthModule::class, YouTrackModule::class, TasksModule::class, ServiceModule::class, TimerModule::class])
interface AppComponent {
    fun context(): Context
    fun repository(): RepositoryImpl
    fun sharedPreferences(): SharedPreferences
    fun authFragment(): AuthInstruction
    fun retrofitComponentBuilder(): RetrofitComponent.Builder
    fun userManager(): UserManager
    fun tasksTracking(): TasksTracking
    fun tasksRunner(): TasksRunner
    fun tasksManager(): TasksManager
    fun tasksService(): TasksService
}