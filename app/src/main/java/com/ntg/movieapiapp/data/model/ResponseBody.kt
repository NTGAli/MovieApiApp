package com.ntg.movieapiapp.data.model

data class ResponseBody<T>(
    val dates: Dates? = null,
    val page: Int? = null,
    val total_pages: Int? = null,
    val total_results: Int? = null,
    val results:T? = null,
)
