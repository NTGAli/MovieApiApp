package com.ntg.movieapiapp.data.remote

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ntg.movieapiapp.data.local.AppDB
import com.ntg.movieapiapp.data.local.MovieEntity
import com.ntg.movieapiapp.data.model.RemoteKeys
import com.ntg.movieapiapp.util.Constants.API.MOVIE_API_STARTING_PAGE_INDEX
import com.ntg.movieapiapp.util.orDefault
import com.ntg.movieapiapp.util.toEntity
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val movieDB: AppDB,
    private val movieApi: MovieApi
): RemoteMediator<Int, MovieEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {


            val page = when(loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: MOVIE_API_STARTING_PAGE_INDEX
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    prevKey
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }
        return try {
            val movies = movieApi.getUpcomingList(
                page = page,
            )

            val mvieList = movies.body()?.results
            val endOfPaginationReached = mvieList.orEmpty().isEmpty()
            movieDB.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieDB.remoteKeysDao().clearRemoteKeys()
                    movieDB.movieDao().clear()
                }
            }

            val prevKey = movies.body()?.page?.minus(1)
            val nextKey = movies.body()?.page?.plus(1)

            val keys = mvieList?.map {
                RemoteKeys(movieId = it.id.orDefault(), prevKey = prevKey, nextKey = nextKey)
            }

            val entities = mvieList?.map {
                it.toEntity()
            }

            movieDB.remoteKeysDao().insertAll(keys.orEmpty())
            movieDB.movieDao().upsertAll(entities.orEmpty())

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch(e: IOException) {
            MediatorResult.Error(e)
        } catch(e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MovieEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { movie ->
                movieDB.remoteKeysDao().remoteKeysMovieItemId(movie.movieId)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MovieEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { movie ->
                movieDB.remoteKeysDao().remoteKeysMovieItemId(movie.movieId)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MovieEntity>
    ): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.movieId?.let { movieId ->
                movieDB.remoteKeysDao().remoteKeysMovieItemId(movieId)
            }
        }
    }
}