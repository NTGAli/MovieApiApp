package com.ntg.movieapiapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.ntg.movieapiapp.data.model.Movie
import com.ntg.movieapiapp.data.model.NetworkResult
import com.ntg.movieapiapp.data.model.ResponseBody
import com.ntg.movieapiapp.data.remote.MovieApi
import com.ntg.movieapiapp.data.remote.MoviePagingSource
import com.ntg.movieapiapp.util.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val movieApi: MovieApi
): ViewModel() {

    private var movies: MutableLiveData<NetworkResult<ResponseBody<List<Movie?>?>>> = MutableLiveData()

    fun getMovies(): MutableLiveData<NetworkResult<ResponseBody<List<Movie?>?>>> {
//        viewModelScope.launch {
//            movies = safeApiCall(Dispatchers.IO){
//                movieApi.getUpcomingList(1)
//            } as MutableLiveData<NetworkResult<ResponseBody<List<Movie?>?>>>
//        }
        return movies
    }

    val errorMessage = MutableLiveData<String>()

    fun getMovieList(): LiveData<PagingData<Movie>> {
        return getAllMovies().cachedIn(viewModelScope)
    }

    private fun getAllMovies(): LiveData<PagingData<Movie>> {

        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 1
            ),
            pagingSourceFactory = {
                MoviePagingSource(movieApi)
            }
            , initialKey = 1
        ).liveData
    }


}