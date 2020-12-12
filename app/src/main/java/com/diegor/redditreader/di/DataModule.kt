package com.diegor.redditreader.di

import com.diegor.redditreader.BuildConfig
import com.diegor.redditreader.data.api.AuthorizationHolder
import com.diegor.redditreader.data.api.AuthorizationHolderImpl
import com.diegor.redditreader.data.api.AuthorizationService
import com.diegor.redditreader.data.api.AuthorizationService.Companion.REDDIT_APP_ID
import com.diegor.redditreader.data.api.AuthorizationService.Companion.AUTH_BASE_URL
import com.diegor.redditreader.data.api.RedditService
import com.diegor.redditreader.data.api.RedditService.Companion.REDDIT_BASE_URL
import com.diegor.redditreader.di.qualifiers.AuthOkHttpClient
import com.diegor.redditreader.di.qualifiers.RedditOkHttpClient
import com.diegor.redditreader.util.result.DateTypeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DataModule {

    @Provides
    fun provideGson(): Gson {
        val builder = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())

        return builder.create()
    }

    @Provides
    fun provideAccessTokenInterceptor(authorizationHolder: AuthorizationHolder): Interceptor {
        return Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()

            // TODO: Hardcoded ATM
            requestBuilder.addHeader("User-Agent", "redditReader by r/Geredis")
            requestBuilder.addHeader("Authorization", "bearer " + authorizationHolder.authorization)

            chain.proceed(requestBuilder.build())
        }
    }

    private fun getLogginInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    private fun getBasicAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()

            val credentials = Credentials.basic(REDDIT_APP_ID, "")
            requestBuilder.addHeader("Authorization", credentials)

            chain.proceed(requestBuilder.build())
        }
    }

    @RedditOkHttpClient
    @Provides
    fun provideRedditOkHttpClient(accessTokenInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(accessTokenInterceptor)
            addInterceptor(getLogginInterceptor())
        }.build()
    }

    @AuthOkHttpClient
    @Provides
    fun provideAuthHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(getBasicAuthInterceptor())
            addInterceptor(getLogginInterceptor())
        }.build()
    }

    @Provides
    @Singleton
    fun provideRedditService(
        gson: Gson,
        @RedditOkHttpClient okHttpClient: OkHttpClient
    ): RedditService {
        return Retrofit.Builder()
            .baseUrl(REDDIT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(RedditService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthorizationService(
        @AuthOkHttpClient okHttpClient: OkHttpClient
    ): AuthorizationService {
        return Retrofit.Builder()
            .baseUrl(AUTH_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthorizationService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthorizationHolder(): AuthorizationHolder {
        return AuthorizationHolderImpl
    }
}