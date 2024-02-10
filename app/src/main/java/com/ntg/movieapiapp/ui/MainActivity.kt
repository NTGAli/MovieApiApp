package com.ntg.movieapiapp.ui

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.ntg.movieapiapp.databinding.ActivityMainBinding
import com.ntg.movieapiapp.util.showSnack
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MovieViewModel
    private lateinit var adapter: MoviePagerAdapter
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MoviePagerAdapter {
            binding.root.showSnack(it.title)
        }
        val spanCount =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 5 else 3
        val gridLayoutManager = GridLayoutManager(this, spanCount)
        binding.movieRV.adapter = adapter
        binding.movieRV.layoutManager = gridLayoutManager

        viewModel = ViewModelProvider(
            this
        )[MovieViewModel::class.java]

        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        adapter.addLoadStateListener { loadState ->


            when {
                loadState.prepend is LoadState.Error -> {
                }

                loadState.append is LoadState.Error -> {
                }

                loadState.refresh is LoadState.Error -> {
                }
            }

            if (loadState.refresh is LoadState.Loading ||
                loadState.append is LoadState.Loading
            )
                binding.loading.isVisible = true
            else {
                binding.loading.isVisible = false
                val errorState = when {
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                errorState?.let {
                    Toast.makeText(this, it.error.toString(), Toast.LENGTH_LONG).show()
                }

            }
        }


//        viewModel.getMovieList().observe(this) {
//            lifecycleScope.launch {
//                adapter.submitData(lifecycle, it)
//            }
//        }

        lifecycleScope.launchWhenStarted {
            viewModel.moviePagingFlow.collect {
                adapter.submitData(lifecycle, it)
            }
        }

    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val spanCount = if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) 5 else 3
        (binding.movieRV.layoutManager as GridLayoutManager).spanCount = spanCount
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val firstVisibleItemPosition =
            (binding.movieRV.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
        outState.putInt("scroll_position", firstVisibleItemPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val scrollPosition = savedInstanceState.getInt("scroll_position", 0)
        (binding.movieRV.layoutManager as GridLayoutManager).scrollToPosition(scrollPosition)
    }

}