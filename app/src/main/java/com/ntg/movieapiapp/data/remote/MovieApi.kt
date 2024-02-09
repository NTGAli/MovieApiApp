package com.ntg.movieapiapp.data.remote

import com.ntg.movieapiapp.data.model.Movie
import com.ntg.movieapiapp.data.model.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {
    @GET("/3/movie/upcoming?language=en-US")
    fun getUpcomingList(
        @Query("page") page: Int,
    ):Response<ResponseBody<List<Movie?>?>>
}