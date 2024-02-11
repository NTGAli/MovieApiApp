package com.ntg.movieapiapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ntg.movieapiapp.R
import com.ntg.movieapiapp.ui.custom.CustomButton
import com.ntg.movieapiapp.util.gone
import com.ntg.movieapiapp.util.visible

class MovieLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<MovieLoadStateAdapter.LoadStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        when(loadState){

            is LoadState.Error -> {
                holder.itemView.findViewById<LinearLayoutCompat>(R.id.errorParent).visible()
                holder.itemView.findViewById<ProgressBar>(R.id.loadMore).gone()
            }
            LoadState.Loading -> {
                holder.itemView.findViewById<LinearLayoutCompat>(R.id.errorParent).gone()
                holder.itemView.findViewById<ProgressBar>(R.id.loadMore).visible()
            }
            is LoadState.NotLoading -> {

            }
        }

        holder.itemView.findViewById<CustomButton>(R.id.retry).setOnClickListener {
            retry.invoke()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.loading_item, parent, false)
        )
    }

    class LoadStateViewHolder(view: View) : RecyclerView.ViewHolder(view)
}