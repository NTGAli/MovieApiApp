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
import com.ntg.movieapiapp.util.showSnack

class MoviePagerAdapter :
    PagingDataAdapter<Movie, RecyclerView.ViewHolder>(MovieComparator) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MovieViewHolder).bindData(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(movie: Movie?) {

            // placeholder color
            val placeholderTypeValue = TypedValue()
            binding.root.context.theme.resolveAttribute(
                com.google.android.material.R.attr.colorSurface,
                placeholderTypeValue,
                true
            )
            val placeholderColor = placeholderTypeValue.data
            val placeholderDrawable = ColorDrawable(placeholderColor)

            // error placeholder color
            val errorTypeValue = TypedValue()
            binding.root.context.theme.resolveAttribute(
                com.google.android.material.R.attr.colorError,
                errorTypeValue,
                true
            )
            val errorPlaceholderColor = errorTypeValue.data
            val errorPlaceholderDrawable = ColorDrawable(errorPlaceholderColor)

            binding.title = movie?.title
            Glide.with(binding.root.context)
                .load("https://image.tmdb.org/t/p/w500" + movie?.backdrop_path)
                .placeholder(placeholderDrawable)
                .error(errorPlaceholderDrawable)
                .into(binding.cover)

            binding.item.setOnClickListener {
                if (movie != null)
                    binding.root.showSnack(movie.title)
            }

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