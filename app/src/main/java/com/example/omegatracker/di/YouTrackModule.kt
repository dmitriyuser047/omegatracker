package com.example.omegatracker.di

import com.example.omegatracker.entity.task.Value
import com.example.omegatracker.utils.ObjectJsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

const val TYPE = "$" + "type"

@Module
class YouTrackModule() {
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideTypeJsonAdapterFactory(): PolymorphicJsonAdapterFactory<Value> {
        return PolymorphicJsonAdapterFactory.of(Value::class.java, TYPE)
            .withSubtype(Value.StateValue::class.java, "StateBundleElement")
            .withSubtype(Value.PeriodValue::class.java, "PeriodValue")
            .withDefaultValue(null)
    }

    @Singleton
    @Provides
    fun provideObjectJsonAdapter(): ObjectJsonAdapter {
        return ObjectJsonAdapter()
    }

    @Singleton
    @Provides
    fun provideMoshi(
        polymorphicAdapter: PolymorphicJsonAdapterFactory<Value>,
        objectJsonAdapter: ObjectJsonAdapter
    ): Moshi {
        return Moshi.Builder()
            .add(objectJsonAdapter)
            .add(polymorphicAdapter)
            .add(KotlinJsonAdapterFactory())
            .build()
    }
}
