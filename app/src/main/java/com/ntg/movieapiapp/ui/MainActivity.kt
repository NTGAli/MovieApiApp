package com.ntg.movieapiapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Path
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.ntg.movieapiapp.databinding.ActivityMainBinding
import com.ntg.movieapiapp.util.Constants.Animator.LOGO_ANIMATION_DURATION
import com.ntg.movieapiapp.util.Constants.ItemViews.LANDSCAPE_MODE_ITEM_SIZE
import com.ntg.movieapiapp.util.Constants.ItemViews.PORTRAIT_MODE_ITEM_SIZE
import com.ntg.movieapiapp.util.dp
import com.ntg.movieapiapp.util.gone
import com.ntg.movieapiapp.util.isInternetAvailable
import com.ntg.movieapiapp.util.timber
import com.ntg.movieapiapp.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MovieViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var footer: MovieLoadStateAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            this
        )[MovieViewModel::class.java]

        setupAdapter()

        viewModel.isCashAvailable().observe(this) { cashSize ->
            if (cashSize == 0 && !isInternetAvailable(this)) {
                binding.internetErrorParent.visible()
            } else if (cashSize > 0) {
                binding.internetErrorParent.gone()
                startLogoAnimation()
            }
        }

        binding.retry.setOnClickListener {
            viewModel.dataAdapter.retry()
        }
    }

    private fun setupAdapter() {

        val spanCount =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) LANDSCAPE_MODE_ITEM_SIZE else PORTRAIT_MODE_ITEM_SIZE
        val gridLayoutManager = GridLayoutManager(this, spanCount)
        footer = MovieLoadStateAdapter(retry = {
            if (isInternetAvailable(this)) {
                viewModel.dataAdapter.retry()
            }
        })
        binding.movieRV.apply {
            layoutManager = gridLayoutManager
            adapter = viewModel.dataAdapter.withLoadStateFooter(footer)
        }

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {

                return if (position == viewModel.dataAdapter.itemCount && footer.itemCount > 0) {
                    spanCount
                } else {
                    1
                }
            }
        }

        observeData()
    }


    private fun observeData() {

        lifecycleScope.launch {
            viewModel.moviePagingFlow.collect {
                viewModel.setAdapterData(it)
            }
        }

        lifecycleScope.launch {
            viewModel.dataAdapter.loadStateFlow.collect { loadState ->
                timber("LOOOADDSTATE ::::::::::::: $loadState")

                val isListEmpty = loadState.refresh is LoadState.Error && viewModel.dataAdapter.itemCount == 0

                when {
                    loadState.prepend is LoadState.Error || loadState.prepend is LoadState.Loading -> {
                        footer.loadState = loadState.prepend
                    }

                    loadState.append is LoadState.Error || loadState.append is LoadState.Loading -> {
                        footer.loadState = loadState.append
                    }

                    loadState.refresh is LoadState.Error || loadState.refresh is LoadState.Loading -> {
                        footer.loadState = loadState.refresh
                    }
                }

                if (isListEmpty && isInternetAvailable(this@MainActivity)) binding.internetErrorParent.visible()

            }
        }

    }


    private fun startLogoAnimation() {
        binding.loadDataProgress.gone()
        binding.noDataView.gone()

        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels.toFloat()


        val appbarHeight = binding.appBar.height.toFloat()
        val finalHeight = appbarHeight - 24.dp
        val scale = finalHeight / binding.viewAnimate.height


        val centerX: Float = (binding.parent.width - binding.viewAnimate.width) / 2.0f
        val centerY: Float = (binding.parent.height - binding.viewAnimate.height) / 2.0f
        binding.viewAnimate.x = centerX
        binding.viewAnimate.y = centerY


        val endX = screenWidth - binding.viewAnimate.width
        val endY =
            -((binding.viewAnimate.height - (binding.viewAnimate.height * scale)) / 2f) + ((appbarHeight - (binding.viewAnimate.height * scale)) / 2)


        val path = Path().apply {
            moveTo(centerX, centerY)
            quadTo(endX, endY, endX, endY)
        }


        val transactionAnimator =
            ObjectAnimator.ofFloat(binding.viewAnimate, View.X, View.Y, path).apply {
                duration = if (viewModel.isAnimationStarted) 0 else LOGO_ANIMATION_DURATION
            }

        val scaleAnimatorX =
            ObjectAnimator.ofFloat(binding.viewAnimate, View.SCALE_X, scale).apply {
                duration = if (viewModel.isAnimationStarted) 0 else LOGO_ANIMATION_DURATION
            }

        val scaleAnimatorY =
            ObjectAnimator.ofFloat(binding.viewAnimate, View.SCALE_Y, scale).apply {
                duration = if (viewModel.isAnimationStarted) 0 else LOGO_ANIMATION_DURATION
            }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            scaleAnimatorX, scaleAnimatorY, transactionAnimator
        )
        if (!animatorSet.isRunning){
            animatorSet.start()
        }
        viewModel.isAnimationStarted = true


    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val spanCount =
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) LANDSCAPE_MODE_ITEM_SIZE else PORTRAIT_MODE_ITEM_SIZE
        (binding.movieRV.layoutManager as GridLayoutManager).spanCount = spanCount
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        val firstVisibleItemPosition =
//            (binding.movieRV.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
//        outState.putInt("scroll_position", firstVisibleItemPosition)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        val scrollPosition = savedInstanceState.getInt("scroll_position", 0)
//        (binding.movieRV.layoutManager as GridLayoutManager).scrollToPosition(scrollPosition)
//    }


}

