package com.ujujzk.tryindicator

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import java.util.ArrayList


class InfinitePagerIndicator : LinearLayoutCompat {

    private val EDGE_SCALE = .6f
    private val SELECTED_SCALE = 1.6f
    private val COMMON_SCALE = 1f
    private val DEF_VALUE = 10

    private val DOTE_COUNT = 5

    private var mPageCount: Int = 0
    private var mSelectedIndex: Int = 0
    private var oldPageIndex: Int = 0
    private var oldDoteIndex: Int = 0
    private var doteIndex: Int = 0
    private var mItemSize = DEF_VALUE
    private var mDelimiterSize = DEF_VALUE

    private val mIndexImages = ArrayList<ImageView>()
    private var mListener: ViewPager.OnPageChangeListener? = null

    constructor(context: Context) : super(context) {
        clipChildren = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clipToOutline = false
        }
        clipToPadding = false
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        clipChildren = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clipToOutline = false
        }
        clipToPadding = false
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        orientation = HORIZONTAL
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator, 0, 0)
        try {
            mItemSize = attributes.getDimensionPixelSize(R.styleable.ViewPagerIndicator_itemSize, DEF_VALUE)
            mDelimiterSize = attributes.getDimensionPixelSize(R.styleable.ViewPagerIndicator_delimiterSize, DEF_VALUE)
        } finally {
            attributes.recycle()
        }
        if (isInEditMode) {
            createEditModeLayout()
        }
        clipChildren = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clipToOutline = false
        }
        clipToPadding = false

    }


    fun setupWithViewPager(viewPager: ViewPager) {
        setPageCount(viewPager.adapter.count)
        viewPager.addOnPageChangeListener(OnPageChangeListener())

    }

    private fun setPageCount(count: Int) {
        mPageCount = count
        mSelectedIndex = 0
        removeAllViews()
        mIndexImages.clear()

        if (count > DOTE_COUNT) {
            (0..DOTE_COUNT - 1).forEach {
                addView(createBoxedItem(it))
            }
        } else {
            (0..count).forEach {
                addView(createBoxedItem(it))
            }
        }
        setSelectedIndex(mSelectedIndex)
    }


    private fun createEditModeLayout() {
        (0..DOTE_COUNT).forEach {
            val boxedItem = createBoxedItem(it)
            addView(boxedItem)
            if (it == 1) {
                val item: View = boxedItem.getChildAt(0)
                val layoutParams: ViewGroup.LayoutParams = item.layoutParams
                layoutParams.height *= (SELECTED_SCALE.toInt())
                layoutParams.width *= (SELECTED_SCALE.toInt())

                item.layoutParams = layoutParams
            }
        }
    }

    private fun createBoxedItem(position: Int): FrameLayout {
        val box = FrameLayout(context)
        val item = createItem()
        box.addView(item)
        mIndexImages.add(item)

        val boxParams = LinearLayoutCompat.LayoutParams(
                (mItemSize * SELECTED_SCALE).toInt(),
                (mItemSize * SELECTED_SCALE).toInt()
        )
        if (position > 0) {
            boxParams.setMargins(mDelimiterSize, 0, 0, 0)
        }
        box.layoutParams = boxParams
        return box
    }

    private fun createItem(): ImageView {
        val index = ImageView(context)
        val indexParams = FrameLayout.LayoutParams(
                mItemSize,
                mItemSize
        )
        indexParams.gravity = Gravity.CENTER
        index.layoutParams = indexParams
        index.setImageResource(R.drawable.white_circle)
        index.scaleType = ImageView.ScaleType.FIT_CENTER
        return index
    }

    private fun setSelectedIndex(pageIndex: Int) {
        if (pageIndex < 0 || pageIndex > mPageCount - 1) {
            return
        }

        val goRight = oldPageIndex < pageIndex


        if (pageIndex == 0) {
            doteIndex = 0
        } else if (pageIndex == mPageCount - 1) {
            doteIndex = 4
        } else if (goRight && doteIndex < 3) {
            doteIndex++
        } else if (!goRight && doteIndex > 1) {
            doteIndex--
        }

        Log.w("TAG", "index " + doteIndex)


        if (mPageCount < DOTE_COUNT) {
            val unselectedView = mIndexImages.get(mSelectedIndex)
            unselectedView.setImageResource(R.drawable.white_circle)
            unselectedView.animate().scaleX(COMMON_SCALE).scaleY(COMMON_SCALE).setDuration(300).start()

            val selectedView = mIndexImages.get(pageIndex)
            selectedView.animate().scaleX(SELECTED_SCALE).scaleY(SELECTED_SCALE).setDuration(300).start()
            selectedView.setImageResource(R.drawable.blue_circle)

            mSelectedIndex = pageIndex
        } else {
            if (doteIndex == 0) {
                firstLeft()
            } else if (doteIndex == 1 && goRight) {
                secondRight()
            } else if (doteIndex == 1 && !goRight) {

                if (doteIndex == oldDoteIndex) {
                    secondLeftWithShift()
                } else {
                    secondLeft()
                }

            } else if (doteIndex == 2 && goRight) {
                thirdRight()
            } else if (doteIndex == 2 && !goRight) {
                thirdLeft()
            } else if (doteIndex == 3 && goRight) {

                if (oldDoteIndex == doteIndex) {
                    forthRightWithShift()
                } else {
                    forthRight()
                }

            } else if (doteIndex == 3 && !goRight) {
                forthLeft()
            } else if (doteIndex == 4 && goRight) {
                fifthRight()
            } else {

            }
        }

        oldPageIndex = pageIndex
        oldDoteIndex = doteIndex
    }

    private val STEP: Float = 26f
    private val DURATION: Long = 300L

    private fun firstLeft() {
        val d0 = mIndexImages[0]
        val d1 = mIndexImages[1]
        val d4 = mIndexImages[4]

        d4.scaleX = EDGE_SCALE
        d4.scaleY = EDGE_SCALE


        ValueAnimator.ofFloat(0f, STEP).apply {
            duration = DURATION
            addUpdateListener {
                val value = it.animatedValue as Float

                d0.scaleX = COMMON_SCALE + (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d0.scaleY = COMMON_SCALE + (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d0.setImageResource(R.drawable.blue_circle)

                d1.scaleX = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d1.scaleY = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d1.setImageResource(R.drawable.white_circle)

            }
            start()
        }
    }

    private fun secondRight() {
        val d0 = mIndexImages[0]
        val d1 = mIndexImages[1]
        val d4 = mIndexImages[4]

        d4.scaleX = EDGE_SCALE
        d4.scaleY = EDGE_SCALE


        ValueAnimator.ofFloat(0f, STEP).apply {
            duration = DURATION
            addUpdateListener {
                val value = it.animatedValue as Float

                d0.scaleX = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d0.scaleY = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d0.setImageResource(R.drawable.white_circle)

                d1.scaleX = COMMON_SCALE + (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d1.scaleY = COMMON_SCALE + (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d1.setImageResource(R.drawable.blue_circle)

            }
            start()
        }
    }

    private fun secondLeft() {
        val d1 = mIndexImages[1]
        val d2 = mIndexImages[2]
        val d4 = mIndexImages[4]

        d4.scaleX = EDGE_SCALE
        d4.scaleY = EDGE_SCALE


        ValueAnimator.ofFloat(0f, STEP).apply {
            duration = DURATION
            addUpdateListener {
                val value = it.animatedValue as Float

                d1.scaleX = COMMON_SCALE + (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d1.scaleY = COMMON_SCALE + (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d1.setImageResource(R.drawable.blue_circle)

                d2.scaleX = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d2.scaleY = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d2.setImageResource(R.drawable.white_circle)
            }
            start()
        }
    }

    private fun secondLeftWithShift() {
        val d0 = mIndexImages[0]
        val d1 = mIndexImages[1]
        val d2 = mIndexImages[2]
        val d3 = mIndexImages[3]
        val d4 = mIndexImages[4]

        ValueAnimator.ofFloat(0F, STEP).apply {
            duration = DURATION
            addUpdateListener {
                val value = it.animatedValue as Float

                d4.translationX = value
                d4.scaleX = EDGE_SCALE - (EDGE_SCALE) * value / STEP
                d4.scaleY = EDGE_SCALE - (EDGE_SCALE) * value / STEP

                d3.translationX = value
                d3.scaleX = COMMON_SCALE - (COMMON_SCALE - EDGE_SCALE) * value / STEP
                d3.scaleY = COMMON_SCALE - (COMMON_SCALE - EDGE_SCALE) * value / STEP

                d2.translationX = value

                d1.setImageResource(R.drawable.white_circle)
                d1.translationX = value
                d1.scaleX = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d1.scaleY = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP

                d0.translationX = value
                d0.scaleX = EDGE_SCALE + (SELECTED_SCALE - EDGE_SCALE) * value / STEP
                d0.scaleY = EDGE_SCALE + (SELECTED_SCALE - EDGE_SCALE) * value / STEP
                d0.setImageResource(R.drawable.blue_circle)

                if (value == STEP) {
                    d4.scaleX = EDGE_SCALE
                    d4.scaleY = EDGE_SCALE
                    d4.translationX = 0f

                    d3.translationX = 0f
                    d3.scaleX = COMMON_SCALE
                    d3.scaleY = COMMON_SCALE

                    d2.translationX = 0f

                    d1.translationX = 0f
                    d1.scaleX = SELECTED_SCALE
                    d1.scaleY = SELECTED_SCALE
                    d1.setImageResource(R.drawable.blue_circle)

                    d0.setImageResource(R.drawable.white_circle)
                    d0.translationX = 0f
                    d0.scaleX = EDGE_SCALE
                    d0.scaleY = EDGE_SCALE
                }
            }
            start()
        }
    }

    private fun thirdRight() {
        val d1 = mIndexImages[1]
        val d2 = mIndexImages[2]
        val d4 = mIndexImages[4]

        d4.scaleX = EDGE_SCALE
        d4.scaleY = EDGE_SCALE


        ValueAnimator.ofFloat(0f, STEP).apply {
            duration = DURATION
            addUpdateListener {
                val value = it.animatedValue as Float

                d1.scaleX = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d1.scaleY = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d1.setImageResource(R.drawable.white_circle)

                d2.scaleX = COMMON_SCALE + (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d2.scaleY = COMMON_SCALE + (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d2.setImageResource(R.drawable.blue_circle)

            }
            start()
        }
    }

    private fun thirdLeft() {
        val d2 = mIndexImages[2]
        val d3 = mIndexImages[3]
        val d4 = mIndexImages[4]

        d4.scaleX = EDGE_SCALE
        d4.scaleY = EDGE_SCALE


        ValueAnimator.ofFloat(0f, STEP).apply {
            duration = DURATION
            addUpdateListener {
                val value = it.animatedValue as Float

                d3.scaleX = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d3.scaleY = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d3.setImageResource(R.drawable.white_circle)

                d2.scaleX = COMMON_SCALE + (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d2.scaleY = COMMON_SCALE + (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d2.setImageResource(R.drawable.blue_circle)

            }
            start()
        }
    }

    private fun forthRight() {
        val d2 = mIndexImages[2]
        val d3 = mIndexImages[3]

        ValueAnimator.ofFloat(0f, STEP).apply {
            duration = DURATION
            addUpdateListener {
                val value = it.animatedValue as Float

                d2.scaleX = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d2.scaleY = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d2.setImageResource(R.drawable.white_circle)

                d3.scaleX = COMMON_SCALE + (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d3.scaleY = COMMON_SCALE + (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d3.setImageResource(R.drawable.blue_circle)

            }
            start()
        }
    }

    private fun forthRightWithShift() {
        val d0 = mIndexImages[0]
        val d1 = mIndexImages[1]
        val d2 = mIndexImages[2]
        val d3 = mIndexImages[3]
        val d4 = mIndexImages[4]

        ValueAnimator.ofFloat(0F, STEP).apply {
            duration = DURATION
            addUpdateListener {
                val value = it.animatedValue as Float

                d0.translationX = -value
                d0.scaleX = EDGE_SCALE - (EDGE_SCALE) * value / STEP
                d0.scaleY = EDGE_SCALE - (EDGE_SCALE) * value / STEP

                d1.translationX = -value
                d1.scaleX = COMMON_SCALE - (COMMON_SCALE - EDGE_SCALE) * value / STEP
                d1.scaleY = COMMON_SCALE - (COMMON_SCALE - EDGE_SCALE) * value / STEP

                d2.translationX = -value

                d3.setImageResource(R.drawable.white_circle)
                d3.translationX = -value
                d3.scaleX = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d3.scaleY = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP

                d4.translationX = -value
                d4.scaleX = EDGE_SCALE + (SELECTED_SCALE - EDGE_SCALE) * value / STEP
                d4.scaleY = EDGE_SCALE + (SELECTED_SCALE - EDGE_SCALE) * value / STEP
                d4.setImageResource(R.drawable.blue_circle)

                if (value == STEP) {
                    d0.scaleX = EDGE_SCALE
                    d0.scaleY = EDGE_SCALE
                    d0.translationX = 0f

                    d1.translationX = 0f
                    d1.scaleX = COMMON_SCALE
                    d1.scaleY = COMMON_SCALE

                    d2.translationX = 0f

                    d3.translationX = 0f
                    d3.scaleX = SELECTED_SCALE
                    d3.scaleY = SELECTED_SCALE
                    d3.setImageResource(R.drawable.blue_circle)

                    d4.setImageResource(R.drawable.white_circle)
                    d4.translationX = 0f
                    d4.scaleX = EDGE_SCALE
                    d4.scaleY = EDGE_SCALE
                }
            }
            start()
        }
    }

    private fun forthLeft() {
        val d3 = mIndexImages[3]
        val d4 = mIndexImages[4]

        ValueAnimator.ofFloat(0f, STEP).apply {
            duration = DURATION
            addUpdateListener {
                val value = it.animatedValue as Float

                d4.scaleX = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d4.scaleY = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d4.setImageResource(R.drawable.white_circle)

                d3.scaleX = COMMON_SCALE + (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d3.scaleY = COMMON_SCALE + (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d3.setImageResource(R.drawable.blue_circle)
            }
            start()
        }
    }

    private fun fifthRight() {
        val d3 = mIndexImages[3]
        val d4 = mIndexImages[4]

        ValueAnimator.ofFloat(0f, STEP).apply {
            duration = DURATION
            addUpdateListener {
                val value = it.animatedValue as Float

                d3.scaleX = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d3.scaleY = SELECTED_SCALE - (SELECTED_SCALE - COMMON_SCALE) * value / STEP
                d3.setImageResource(R.drawable.white_circle)

                d4.scaleX = EDGE_SCALE + (SELECTED_SCALE - EDGE_SCALE) * value / STEP
                d4.scaleY = EDGE_SCALE + (SELECTED_SCALE - EDGE_SCALE) * value / STEP
                d4.setImageResource(R.drawable.blue_circle)

            }
            start()
        }
    }


    fun addOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        mListener = listener
    }


    inner class OnPageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            mListener?.onPageScrollStateChanged(state)
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            mListener?.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            setSelectedIndex(position)
            mListener?.onPageSelected(position)
        }

    }


}