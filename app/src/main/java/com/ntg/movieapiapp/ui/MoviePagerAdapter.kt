package com.ntg.movieapiapp.ui

import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ntg.movieapiapp.data.model.Movie
import com.ntg.movieapiapp.databinding.ItemMovieBinding


class MoviePagerAdapter :
    PagingDataAdapter<Movie, MoviePagerAdapter.MovieViewHolder>(MovieComparator) {

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(movie: Movie?) {

            // placeholder color
            val placeholderTypeValue = TypedValue()
            binding.root.context.theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, placeholderTypeValue, true)
            val placeholderColor = placeholderTypeValue.data
            val placeholderDrawable = ColorDrawable(placeholderColor)

            // error placeholder color
            val errorTypeValue = TypedValue()
            binding.root.context.theme.resolveAttribute(com.google.android.material.R.attr.colorError, errorTypeValue, true)
            val errorPlaceholderColor = errorTypeValue.data
            val errorPlaceholderDrawable = ColorDrawable(errorPlaceholderColor)

            binding.title = movie?.title
            Glide.with(binding.root.context)
                .load("http://image.tmdb.org/t/p/w500" + movie?.backdrop_path)
                .placeholder(placeholderDrawable)
                .error(errorPlaceholderDrawable)
                .into(binding.cover)

        }

    }

    object MovieComparator : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
}