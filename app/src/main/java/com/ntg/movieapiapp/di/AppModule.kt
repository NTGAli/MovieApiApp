package com.ntg.movieapiapp.di

import com.ntg.movieapiapp.data.remote.AuthorizeInterceptor
import com.ntg.movieapiapp.data.remote.LoggingInterceptor
import com.ntg.movieapiapp.data.remote.MovieApi
import com.ntg.movieapiapp.util.Constants.API.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
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
    fun provideRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideOKHttp(): OkHttpClient{
        return OkHttpClient.Builder()
            .readTimeout(15,TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(LoggingInterceptor().httpLoggingInterceptor())
            .addInterceptor(AuthorizeInterceptor())
            .build()
    }
}