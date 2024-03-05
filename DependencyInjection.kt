package com.mindbyromanzanoni.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.mindbyromanzanoni.BuildConfig
import com.mindbyromanzanoni.retrofit.ApiService
import com.mindbyromanzanoni.sharedPreference.SharedPrefs
import com.mindbyromanzanoni.utils.constant.AppConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DependencyInjection {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        interceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }


    @Provides
    @Singleton
    fun provideInterceptor(sharedPref: SharedPrefs): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
            val token = sharedPref.getString(AppConstants.USER_AUTH_TOKEN)
            if (token.isEmpty().not()) {
                Log.v(AppConstants.MY_LOG_TAG, "token ${sharedPref.getString(AppConstants.USER_AUTH_TOKEN)}")
                request.header("Authorization", sharedPref.getString(AppConstants.USER_AUTH_TOKEN)
                ).addHeader("Accept", "application/json").build()
            }
            request.build()
            chain.proceed(request.build())
        }
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun providerSharedPref(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            AppConstants.SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
    }
}