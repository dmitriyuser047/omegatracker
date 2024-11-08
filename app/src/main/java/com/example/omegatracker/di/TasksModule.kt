package com.example.omegatracker.di

import com.example.omegatracker.ui.main.AddCustomTask
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class TasksModule {
    @Singleton
    @Provides
    fun provideTaskTracking(): AddCustomTask {
        return AddCustomTask()
    }
}