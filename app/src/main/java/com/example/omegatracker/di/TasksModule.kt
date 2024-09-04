package com.example.omegatracker.di

import com.example.omegatracker.ui.tasks.TasksTracking
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class TasksModule {
    @Singleton
    @Provides
    fun provideTaskTracking(): TasksTracking {
        return TasksTracking()
    }
}