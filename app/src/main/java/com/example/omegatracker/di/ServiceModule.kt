package com.example.omegatracker.di

import com.example.omegatracker.service.TasksManager
import com.example.omegatracker.service.TasksRunner
import com.example.omegatracker.service.TasksService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class ServiceModule {

    @Provides
    fun provideTaskRunner(): TasksRunner {
        return TasksRunner()
    }

    @Singleton
    @Provides
    fun provideTaskManager(): TasksManager {
        return TasksManager()
    }

    @Singleton
    @Provides
    fun provideTasksService(): TasksService {
        return TasksService()
    }

}