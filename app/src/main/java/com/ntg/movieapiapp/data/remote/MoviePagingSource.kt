package com.ntg.movieapiapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ntg.movieapiapp.data.model.Movie

class MoviePagingSource(private val apiService: MovieApi): PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {

        return try {
            val position = params.key ?: 1
            val response = apiService.getUpcomingList(page = position)
            LoadResult.Page(data = response.body()?.results.orEmpty(), prevKey = if (position == 1) null else position - 1,
                nextKey = position + 1)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}