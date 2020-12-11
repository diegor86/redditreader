package com.diegor.redditreader.di

import com.diegor.redditreader.BuildConfig
import com.diegor.redditreader.data.api.RedditService
import com.diegor.redditreader.di.qualifiers.AuthorizationInterceptor
import com.diegor.redditreader.di.qualifiers.LogginInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
object DataModule {

    @Provides
    fun provideGson(): Gson {
        val builder = GsonBuilder()
        return builder.create()
    }

    @AuthorizationInterceptor
    @Provides
    fun provideAuthorizationInterceptor(): Interceptor {
        return Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()

            // TODO: Hardcoded ATM
            requestBuilder.addHeader("User-Agent", "redditReader by r/Geredis")
            requestBuilder.addHeader("Authorization", "bearer -16xa6X2c5vnQv6b4EF9rIp6PczqmTw")

            chain.proceed(requestBuilder.build())
        }
    }

    @LogginInterceptor
    @Provides
    fun provideLogginInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    fun provideOkHttpClient(
        @AuthorizationInterceptor authInterceptor: Interceptor,
        @LogginInterceptor logginInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(authInterceptor)
            addInterceptor(logginInterceptor)
        }.build()
    }

    @Provides
    @Singleton
    fun provideRedditService(
        okHttpClient: OkHttpClient
    ): RedditService {
        return Retrofit.Builder()
            .baseUrl("https://oauth.reddit.com")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RedditService::class.java)
    }
}