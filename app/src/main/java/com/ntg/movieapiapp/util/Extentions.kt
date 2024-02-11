package com.ntg.movieapiapp.util

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.android.material.snackbar.Snackbar
import com.ntg.movieapiapp.R
import com.ntg.movieapiapp.data.local.MovieEntity
import com.ntg.movieapiapp.data.model.Movie
import com.ntg.movieapiapp.data.model.NetworkResult
import com.ntg.movieapiapp.data.model.SnackType
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException


fun Float?.orZero() = this ?: 0f
fun Long?.orDefault() = this ?: 0L
fun String?.orDefault() = this ?: ""
fun Int?.orZero() = this ?: 0
fun Boolean?.orFalse() = this ?: false
fun Boolean?.orTrue() = this ?: true

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone(){
    this.visibility = View.GONE
}

val Int.px: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


fun timber(msg: String) {
    Timber.d(msg)
}


fun Movie.toEntity(page: Int): MovieEntity{
    return MovieEntity(
        id = 0,
        backdropPath = backdrop_path,
        title = title,
        page = page
    )
}

fun MovieEntity.toMovie(): Movie{
    return Movie(
        id = id,
        backdrop_path = backdropPath,
        title = title
    )
}

fun View.showSnack(text: String, type: SnackType = SnackType.Default){

    val snackBar = Snackbar.make(this, "", Snackbar.LENGTH_SHORT)
    val inflater = LayoutInflater.from(context)
    val customView = inflater.inflate(R.layout.snack_view, null)
    val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
    snackBarLayout.addView(customView, 0)
    snackBarLayout.setPadding(0, 0, 0, 0)

    snackBarLayout.findViewById<AppCompatTextView>(R.id.snackTitle).text = text

    if (type == SnackType.Error){
        snackBarLayout.findViewById<AppCompatImageView>(R.id.errorIcon).visible()
    }
    snackBar.show()
}


fun timber(title: String, msg: String) {
    Timber.d("$title ----------> $msg")
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.toast(resId: Int) {
    Toast.makeText(this, this.getString(resId), Toast.LENGTH_SHORT).show()
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val activeNetwork =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiToBeCalled: suspend () -> Response<T>
): LiveData<NetworkResult<T>> {


    return liveData(dispatcher) {

        var response: Response<T>? = null
        try {
            emit(NetworkResult.Loading())
            timber("MovieAPI Response ::: $response")
            response = apiToBeCalled.invoke()
            if (response.isSuccessful) {
                emit(NetworkResult.Success(data = response.body()))
            } else {
                emit(
                    NetworkResult.Error(message = response.errorBody().toString())
                )

            }
        } catch (e: HttpException) {
            emit(NetworkResult.Error(message = "HttpException ::: ${e.message}"))
        } catch (e: IOException) {
            emit(NetworkResult.Error(message = "IOException ::: ${e.message} --- ${e.printStackTrace()}"))
        } catch (e: Exception) {
            emit(NetworkResult.Error(message = "Exception ::: ${e.message}"))
        }
    }
}
