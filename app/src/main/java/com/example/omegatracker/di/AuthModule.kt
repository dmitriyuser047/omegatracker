package com.example.omegatracker.di

import com.example.omegatracker.ui.auth.AuthInstruction
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AuthModule {
    @Singleton
    @Provides
    fun provideAuthFragment(): AuthInstruction {
        return AuthInstruction()
    }

}