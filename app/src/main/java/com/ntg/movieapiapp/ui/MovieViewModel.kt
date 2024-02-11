package com.ntg.movieapiapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.ntg.movieapiapp.data.local.AppDB
import com.ntg.movieapiapp.data.local.MovieEntity
import com.ntg.movieapiapp.data.model.Movie
import com.ntg.movieapiapp.util.toMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    pager: Pager<Int, MovieEntity>,
    private val appDB: AppDB
) : ViewModel() {

    private var size : LiveData<Int> = MutableLiveData()
    var isAnimationStarted = false

    val moviePagingFlow = pager
        .flow
        .map { pagingData ->
            pagingData.map { it.toMovie() }
        }
        .cachedIn(viewModelScope)


    fun isCashAvailable(): LiveData<Int> {
        viewModelScope.launch {
            size = appDB.movieDao().size()
        }
        return size
    }

    var dataAdapter: MoviePagerAdapter = MoviePagerAdapter()


    fun setAdapterData(data: PagingData<Movie>) {
        viewModelScope.launch {
            dataAdapter.submitData(data)
        }
    }
}