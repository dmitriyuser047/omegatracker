package com.example.omegatracker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.omegatracker.data.RepositoryImpl
import com.example.omegatracker.data.model.UserManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(private val application: Application) {

    @Singleton
    @Provides
    fun provideApplication(): Application {
        return application
    }

    @Singleton
    @Provides
    fun provideApplicationContext(): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Singleton
    @Provides
    fun provideRepositoryApi(): RepositoryImpl {
        return RepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideUserManager(context: Context, sharedPreferences: SharedPreferences): UserManager {
        return UserManager(context, sharedPreferences)
    }

}