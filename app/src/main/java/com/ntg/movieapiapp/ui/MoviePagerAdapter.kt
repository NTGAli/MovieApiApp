package com.ntg.movieapiapp.ui

import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ntg.movieapiapp.R
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

            binding.title = movie?.title
            Glide.with(binding.root.context)
                .load("https://image.tmdb.org/t/p/w500" + movie?.backdropPath)
                .placeholder(R.drawable.placholder)
                .error(R.drawable.error_item)
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