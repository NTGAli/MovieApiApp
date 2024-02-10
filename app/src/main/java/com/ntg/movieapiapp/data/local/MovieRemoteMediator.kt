package com.ntg.movieapiapp.data.local

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ntg.movieapiapp.data.remote.MovieApi
import com.ntg.movieapiapp.util.orZero
import com.ntg.movieapiapp.util.toEntity
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val movieDB: AppDB,
    private val movieApi: MovieApi
): RemoteMediator<Int, MovieEntity>() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        return try {

            val loadKey = when(loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if(lastItem == null) {
                        1
                    } else {
                        lastItem.page + 1
                    }
                }
            }

            val movies = movieApi.getUpcomingList(
                page = loadKey,
            ).body()

            movieDB.withTransaction {
                if(loadType == LoadType.REFRESH) {
                    movieDB.movieDao.clear()
                }
                val movieEntities = movies?.results?.map { it.toEntity(movies.page ?: 1) }
                movieDB.movieDao.upsertAll(movieEntities.orEmpty())
            }

            MediatorResult.Success(
                endOfPaginationReached = movies?.results.orEmpty().isEmpty()
            )
        } catch(e: IOException) {
            MediatorResult.Error(e)
        } catch(e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}