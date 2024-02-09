package com.ntg.movieapiapp.data.model

data class Movie(
    val adult: Boolean,
    val backdrop_path: String? = null,
    val title: String,
//    val genre_ids: List<Int?>? = null,
    val id: Int?=  null,
//    val original_language: String,
//    val original_title: String,
//    val overview: String,
//    val popularity: String,
//    val poster_path: String,
//    val release_date: String,
//    val video: Boolean,
//    val vote_average: Float?,
//    val vote_count: Int?,
)
