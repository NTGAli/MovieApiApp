package com.ntg.movieapiapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Path
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.ntg.movieapiapp.R
import com.ntg.movieapiapp.data.model.Movie
import com.ntg.movieapiapp.data.model.SnackType
import com.ntg.movieapiapp.databinding.ActivityMainBinding
import com.ntg.movieapiapp.util.Constants.Animator.LOGO_ANIMATION_DURATION
import com.ntg.movieapiapp.util.Constants.ItemViews.LANDSCAPE_MODE_ITEM_SIZE
import com.ntg.movieapiapp.util.Constants.ItemViews.PORTRAIT_MODE_ITEM_SIZE
import com.ntg.movieapiapp.util.dp
import com.ntg.movieapiapp.util.gone
import com.ntg.movieapiapp.util.isInternetAvailable
import com.ntg.movieapiapp.util.showSnack
import com.ntg.movieapiapp.util.timber
import com.ntg.movieapiapp.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.logging.Handler


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var binding: ActivityMainBinding
    private val animatorSet = AnimatorSet()
    private var animationStated = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movieViewModel = ViewModelProvider(
            this
        )[MovieViewModel::class.java]

        binding.bindUI(
            pagingData = movieViewModel.moviePagingFlow,
        )
    }

    private fun ActivityMainBinding.bindUI(
        pagingData: Flow<PagingData<Movie>>,
    ) {

        val movieAdapter = MoviePagerAdapter()
        val footer = MovieLoadStateAdapter { movieAdapter.retry() }


        val spanCount =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) LANDSCAPE_MODE_ITEM_SIZE else PORTRAIT_MODE_ITEM_SIZE
        val gridLayoutManager = GridLayoutManager(this@MainActivity, spanCount)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {

                return if (position == movieAdapter.itemCount && footer.itemCount > 0) {
                    spanCount
                } else {
                    1
                }
            }
        }

        movieRV.apply {
            adapter = movieAdapter.withLoadStateFooter(
                footer = footer
            )
            layoutManager = gridLayoutManager
        }

        retry.setOnClickListener { movieAdapter.retry() }

        bindList(
            movieAdapter = movieAdapter,
            pagingData = pagingData,
            footer = footer
        )

//        movieRV.setOnScrollChangeListener { _, _, _, _, _ ->
//            divider.isVisible = movieRV.computeVerticalScrollOffset() > 38
//        }

    }

    private fun ActivityMainBinding.bindList(
        movieAdapter: MoviePagerAdapter,
        pagingData: Flow<PagingData<Movie>>,
        footer: MovieLoadStateAdapter
    ) {

        lifecycleScope.launch {
            pagingData.collectLatest(movieAdapter::submitData)
        }

        lifecycleScope.launch {
            movieAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    (loadState.refresh is LoadState.NotLoading || loadState.refresh is LoadState.Error) && movieAdapter.itemCount == 0

                internetErrorParent.isVisible =
                    loadState.mediator?.refresh is LoadState.Error && movieAdapter.itemCount == 0
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    root.showSnack(getString(R.string.internet_error), SnackType.Error)
                }


                if ((isInternetAvailable(this@MainActivity) && !isListEmpty && movieAdapter.itemCount > 0) ||
                    (!isInternetAvailable(
                        this@MainActivity
                    ) && !isListEmpty)
                ) {
                    startLogoAnimation()
                } else if (!isInternetAvailable(this@MainActivity) && isListEmpty) {
                    internetErrorParent.visible()
                } else if (isInternetAvailable(this@MainActivity) && isListEmpty) {
                    binding.internetErrorParent.gone()
                    binding.noDataView.visible()
                    binding.loadDataProgress.visible()
                }

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
            }
        }


    }

    private fun startLogoAnimation() {
        binding.internetErrorParent.gone()
        binding.loadDataProgress.gone()
        binding.noDataView.gone()

        if (!animationStated){
            animationStated = true
            binding.parent.viewTreeObserver
                .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        binding.parent.viewTreeObserver.removeOnGlobalLayoutListener(this)

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
                                duration =
                                    if (movieViewModel.isAnimationStarted) 0 else LOGO_ANIMATION_DURATION
                            }

                        val scaleAnimatorX =
                            ObjectAnimator.ofFloat(binding.viewAnimate, View.SCALE_X, scale).apply {
                                duration =
                                    if (movieViewModel.isAnimationStarted) 0 else LOGO_ANIMATION_DURATION
                            }

                        val scaleAnimatorY =
                            ObjectAnimator.ofFloat(binding.viewAnimate, View.SCALE_Y, scale).apply {
                                duration =
                                    if (movieViewModel.isAnimationStarted) 0 else LOGO_ANIMATION_DURATION
                            }


                        animatorSet.playTogether(
                            scaleAnimatorX, scaleAnimatorY, transactionAnimator
                        )
                        movieViewModel.isAnimationStarted = true

                        if (!animatorSet.isRunning) {
                            animatorSet.start()
                        }
                    }
                })
        }


    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val spanCount =
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) LANDSCAPE_MODE_ITEM_SIZE else PORTRAIT_MODE_ITEM_SIZE
        (binding.movieRV.layoutManager as GridLayoutManager).spanCount = spanCount
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val firstVisibleItemPosition =
            binding.movieRV.computeVerticalScrollOffset()
        outState.putInt("scroll_position", firstVisibleItemPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val scrollPosition = savedInstanceState.getInt("scroll_position", 0)
        binding.movieRV.post {
            binding.movieRV.scrollToPosition(scrollPosition)
        }
    }

}

