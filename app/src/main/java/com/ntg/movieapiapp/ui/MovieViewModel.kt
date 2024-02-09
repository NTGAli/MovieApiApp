package com.ntg.movieapiapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.movieapiapp.data.model.Movie
import com.ntg.movieapiapp.data.model.NetworkResult
import com.ntg.movieapiapp.data.model.ResponseBody
import com.ntg.movieapiapp.data.remote.MovieApi
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
        viewModelScope.launch {
            movies = safeApiCall(Dispatchers.IO){
                movieApi.getUpcomingList(1)
            } as MutableLiveData<NetworkResult<ResponseBody<List<Movie?>?>>>
        }
        return movies
    }

}