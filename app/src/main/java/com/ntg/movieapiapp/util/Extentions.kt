package com.ntg.movieapiapp.util

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.snackbar.Snackbar
import com.ntg.movieapiapp.R
import com.ntg.movieapiapp.data.local.MovieEntity
import com.ntg.movieapiapp.data.model.Movie
import com.ntg.movieapiapp.data.model.SnackType
import timber.log.Timber


fun Long?.orDefault() = this ?: 0L

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone(){
    this.visibility = View.GONE
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


fun timber(msg: String) {
    Timber.d(msg)
}


fun Movie.toEntity(): MovieEntity{
    return MovieEntity(
        id = 0,
        backdropPath = backdropPath,
        title = title,
        movieId = id.orDefault()
    )
}

fun MovieEntity.toMovie(): Movie{
    return Movie(
        id = id,
        backdropPath = backdropPath,
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
