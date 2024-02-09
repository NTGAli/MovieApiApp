package com.ntg.movieapiapp.data.model

sealed class NetworkResult<T>(
    val results: T? = null,
    val message: String? = null
){
    class Success<T>(data: T?) : NetworkResult<T>(data)

    class Error<T>(message: String?, data: T? = null) : NetworkResult<T>(results = data, message = message)

    class Loading<T> : NetworkResult<T>()

}