package com.ntg.movieapiapp.data.model

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Long?=  null,
    @field:SerializedName("backdrop_path") val backdropPath: String? = null,
    val title: String
)
