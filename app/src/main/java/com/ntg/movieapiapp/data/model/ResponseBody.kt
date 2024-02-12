package com.ntg.movieapiapp.data.model

import com.google.gson.annotations.SerializedName

data class ResponseBody<T>(
    val dates: Dates? = null,
    val page: Int? = null,
    @field:SerializedName("total_pages") val totalPages: Int? = null,
    @field:SerializedName("total_results")val totalResults: Int? = null,
    val results:T? = null,
)
