package com.example.omegatracker.di

import com.example.omegatracker.ui.statistics.graph.StatisticsGraphHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StatisticsModule {
    @Singleton
    @Provides
    fun provideStatisticsGraphHelper(): StatisticsGraphHelper {
        return StatisticsGraphHelper()
    }
}