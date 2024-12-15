package com.example.quick_reply.data.di

import com.example.quick_reply.BuildConfig
import com.example.quick_reply.data.remote.MainApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val retrofitModule = module {

    fun provideLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    fun provideHttpClient(
        loggingInterceptor: Interceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor)
        }
        return builder.build()
    }

    fun provideRetrofitBuilder(client: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
    }

    single(named("loggingInterceptor")) { provideLoggingInterceptor() }
    single { provideHttpClient(get(named("loggingInterceptor"))) }
    factory { provideRetrofitBuilder(get()) }
}

val apiServiceModule = module {

    fun provideMainApiService(retrofit: Retrofit.Builder): MainApiService {
        return retrofit.baseUrl(BuildConfig.API_BASE_URL)
            .build()
            .create(MainApiService::class.java)
    }

    single { provideMainApiService(get()) }
}