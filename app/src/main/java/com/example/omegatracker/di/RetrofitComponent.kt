package com.example.omegatracker.di

import com.example.omegatracker.data.YouTrackApi
import dagger.BindsInstance
import dagger.Subcomponent
import retrofit2.Retrofit


@AlternativeSingleton
@Subcomponent(modules = [RetrofitModule::class])
interface RetrofitComponent {
    fun retrofit(): Retrofit
    fun youTrackApi(): YouTrackApi

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun baseUrl(baseUrl: String): Builder
        fun build(): RetrofitComponent
    }
}