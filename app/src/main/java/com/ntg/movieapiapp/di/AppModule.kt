package com.ntg.movieapiapp.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.ntg.movieapiapp.data.remote.AuthorizeInterceptor
import com.ntg.movieapiapp.data.remote.LoggingInterceptor
import com.ntg.movieapiapp.data.remote.MovieApi
import com.ntg.movieapiapp.util.Constants.API.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideMovieApi(retrofit: Retrofit): MovieApi{
        return retrofit.create(MovieApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOKHttp(@ApplicationContext context:Context): OkHttpClient{
        return OkHttpClient.Builder()
            .readTimeout(15,TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(LoggingInterceptor().httpLoggingInterceptor())
            .addInterceptor(AuthorizeInterceptor())
            .addInterceptor(ChuckerInterceptor(context))
            .build()
    }
}