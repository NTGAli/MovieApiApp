package com.ntg.movieapiapp.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.ntg.movieapiapp.R
import com.ntg.movieapiapp.data.model.NetworkResult
import com.ntg.movieapiapp.databinding.ActivityMainBinding
import com.ntg.movieapiapp.util.timber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MovieViewModel
    private val adapter = MoviePagerAdapter()
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val gridLayoutManager = GridLayoutManager(this, 3)
        binding.movieRV.adapter = adapter
        binding.movieRV.layoutManager = gridLayoutManager

        viewModel = ViewModelProvider(
            this
        )[MovieViewModel::class.java]

        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        adapter.addLoadStateListener { loadState ->
            timber("AAAAAAAAAAAAAAAAAA LOADSS  $loadState")
            // show empty list
            if (loadState.refresh is LoadState.Loading ||
                loadState.append is LoadState.Loading
            )
//                binding.progressDialog.isVisible = true
            else {
//                binding.progressDialog.isVisible = false
                // If we have an error, show a toast
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



        viewModel.getMovieList().observe(this) {
            timber("AAAAAAAAAAAAAAAAAA $it")
            lifecycleScope.launch {
                adapter.submitData(lifecycle, it)
            }
        }


    }

}