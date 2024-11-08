package com.example.omegatracker.di.builders

import com.example.omegatracker.data.YouTrackApi
import com.example.omegatracker.di.RetrofitModule
import com.example.omegatracker.di.scope.RetrofitScope
import dagger.BindsInstance
import dagger.Subcomponent
import retrofit2.Retrofit


@RetrofitScope
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