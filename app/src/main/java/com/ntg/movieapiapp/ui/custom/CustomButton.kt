package com.ntg.movieapiapp.ui.custom

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import com.ntg.movieapiapp.R
import com.ntg.movieapiapp.ui.custom.ButtonSizes.PROGRESS_HEIGHT
import com.ntg.movieapiapp.ui.custom.ButtonSizes.PROGRESS_WIDTH
import com.ntg.movieapiapp.ui.custom.ButtonSizes.RADIUS
import com.ntg.movieapiapp.ui.custom.ButtonSizes.TEXT_VIEW_SIZE
import com.ntg.movieapiapp.util.dp


object ButtonSizes{
    const val RADIUS = 4
    const val TEXT_VIEW_SIZE = 14f
    const val PROGRESS_WIDTH = 24f
    const val PROGRESS_HEIGHT = 24f
}

class CustomButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs) {

    private var myAttrs: AttributeSet? = null

    // attributes
    private var text: String? = null
        set(value) {
            field = value
            textView.text = value
        }

    private var isLoading: Boolean = false
        set(value) {
            field = value
            setBackgroundColorState()
        }

    private var viewContext: Context = context

    //views
    private lateinit var linearLayout: LinearLayout
    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar



    init {
        myAttrs =attrs
        isClickable = true
        try {
            setupButton()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setupButton() {
        initViews()
        getAttributes(myAttrs)
        setViews()
        setBackgroundColorState()
        setSize()
        setPaddingViews()

    }


    private fun initViews() {
        linearLayout = LinearLayout(context)
        textView = TextView(context)
        progressBar = ProgressBar(context)
    }


    private fun setBackgroundColorState() {
        val states = StateListDrawable()
        val shape = GradientDrawable()
        val radius = floatArrayOf(
            RADIUS.dp.toFloat(),
            RADIUS.dp.toFloat(),
            RADIUS.dp.toFloat(),
            RADIUS.dp.toFloat(),
            RADIUS.dp.toFloat(),
            RADIUS.dp.toFloat(),
            RADIUS.dp.toFloat(),
            RADIUS.dp.toFloat()
        )

        val backgroundColor = TypedValue()
        context.theme.resolveAttribute(com.google.android.material.R.attr.colorTertiary, backgroundColor, true)

        val backgroundColorSelected = TypedValue()
        context.theme.resolveAttribute(com.google.android.material.R.attr.colorSurfaceVariant, backgroundColorSelected, true)

        val borderColor = TypedValue()
        context.theme.resolveAttribute(com.google.android.material.R.attr.colorOnTertiary, borderColor, true)

        val textColor = TypedValue()
        context.theme.resolveAttribute(com.google.android.material.R.attr.colorError, textColor, true)

        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadii = radius

        val selectedShape = GradientDrawable()
        selectedShape.shape = GradientDrawable.RECTANGLE
        selectedShape.cornerRadii = radius

        selectedShape.setColor(backgroundColorSelected.data)
        selectedShape.setStroke(2, borderColor.data)


        shape.setColor(backgroundColor.data)
        shape.setStroke(2, borderColor.data)

        states.addState(intArrayOf(-android.R.attr.state_enabled), shape)

//        states.addState(intArrayOf(android.R.attr.state_selected), selectedShape)
        states.addState(intArrayOf(android.R.attr.state_pressed), selectedShape)

        states.setExitFadeDuration(100)

        textView.setTextColor(textColor.data)
        background = states
    }

    private fun setPaddingViews() {
        textView.setPadding(16.dp, 8.dp, 16.dp, 8.dp)
    }

    private fun setParamsViews() {
        //params
        // -- parent layout --
        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }
        // -- linearLayout --
        linearLayout.layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }
        // -- textView --
        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        //---- progressBar -----
        val progressParams = LayoutParams(
            PROGRESS_WIDTH.toInt().dp,
            PROGRESS_HEIGHT.toInt().dp
        ).apply {
            gravity = Gravity.CENTER
        }
        progressBar.layoutParams = progressParams
    }

    private fun setViews() {
        setParamsViews()
        addView(linearLayout)
        when {
            isLoading -> {
                linearLayout.addView(textView)
                linearLayout.visibility = GONE
                addView(progressBar)
            }
            else -> {
                linearLayout.addView(textView)

            }
        }

    }

    private fun getAttributes(attrs: AttributeSet?) {
        viewContext.withStyledAttributes(attrs, R.styleable.CustomButton) {
            text = getString(R.styleable.CustomButton_text)
            isLoading = getBoolean(R.styleable.CustomButton_isLoading, false)
        }
    }

    private fun setSize() {
        textView.textSize = TEXT_VIEW_SIZE
        textView.text = text
    }
}