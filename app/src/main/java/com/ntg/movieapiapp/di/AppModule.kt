package com.ntg.movieapiapp.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.ntg.movieapiapp.data.local.AppDB
import com.ntg.movieapiapp.data.local.MovieEntity
import com.ntg.movieapiapp.data.local.MovieRemoteMediator
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
object AppModule {

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


    @Provides
    @Singleton
    fun provideMovieDatabase(@ApplicationContext context: Context): AppDB {
        return Room.databaseBuilder(
            context,
            AppDB::class.java,
            "MoviesDB"
        ).build()
    }


    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @Singleton
    fun provideMoviePager(movieDb: AppDB, movieApi: MovieApi): Pager<Int, MovieEntity> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = MovieRemoteMediator(
                movieDB = movieDb,
                movieApi = movieApi
            ),
            pagingSourceFactory = {
                movieDb.movieDao.pagingSource()
            }
        )
    }
}