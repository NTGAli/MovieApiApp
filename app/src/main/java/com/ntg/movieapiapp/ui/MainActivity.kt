package com.ntg.movieapiapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Path
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.ntg.movieapiapp.databinding.ActivityMainBinding
import com.ntg.movieapiapp.util.Constants.Animator.LOGO_ANIMATION_DURATION
import com.ntg.movieapiapp.util.dp
import com.ntg.movieapiapp.util.showSnack
import com.ntg.movieapiapp.util.timber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MovieViewModel
    private lateinit var adapter: MoviePagerAdapter
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            this
        )[MovieViewModel::class.java]

        setupAdapter()

        binding.parent.viewTreeObserver
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    startLogoAnimation()
                    binding.parent.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })

    }

    private fun setupAdapter() {
        adapter = MoviePagerAdapter {
            binding.root.showSnack(it.title)
        }
        val spanCount =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 5 else 3
        val gridLayoutManager = GridLayoutManager(this, spanCount)
        binding.movieRV.adapter = adapter
        binding.movieRV.layoutManager = gridLayoutManager



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

        lifecycleScope.launch {
            viewModel.moviePagingFlow.collect {
                adapter.submitData(lifecycle, it)
            }
        }
    }

    private fun startLogoAnimation(){
        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels.toFloat()


        val appbarHeight = binding.appBar.height.toFloat()
        val finalHeight = appbarHeight - 16.dp
        val scale = finalHeight / binding.viewAnimate.height



        val centerX: Float =
            (binding.parent.width - binding.viewAnimate.width) / 2.0f
        val centerY: Float =
            (binding.parent.height - binding.viewAnimate.height) / 2.0f
        binding.viewAnimate.x = centerX
        binding.viewAnimate.y = centerY


        val endX = screenWidth - binding.viewAnimate.width
        val endY = -((binding.viewAnimate.height - (binding.viewAnimate.height * scale)) / 2f) + ((appbarHeight - (binding.viewAnimate.height * scale)) / 2)


        val path = Path().apply {
            moveTo(centerX, centerY)
            quadTo(endX, endY, endX, endY)
        }




        val transactionAnimator =
            ObjectAnimator.ofFloat(binding.viewAnimate, View.X, View.Y, path).apply {
                duration = LOGO_ANIMATION_DURATION
            }

        val scaleAnimatorX =
            ObjectAnimator.ofFloat(binding.viewAnimate, View.SCALE_X, scale).apply {
                duration = LOGO_ANIMATION_DURATION
            }

        val scaleAnimatorY =
            ObjectAnimator.ofFloat(binding.viewAnimate, View.SCALE_Y, scale).apply {
                duration = LOGO_ANIMATION_DURATION
            }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            scaleAnimatorX,
            scaleAnimatorY,
            transactionAnimator
        )
        animatorSet.start()
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