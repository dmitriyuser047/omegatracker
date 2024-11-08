package com.example.omegatracker.di

import com.example.omegatracker.data.YouTrackApi
import com.example.omegatracker.di.scope.RetrofitScope
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
class RetrofitModule {
    @RetrofitScope
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @RetrofitScope
    @Provides
    fun provideYouTrackApi(retrofit: Retrofit): YouTrackApi {
        return retrofit.create(YouTrackApi::class.java)
    }
}