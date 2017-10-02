package com.ujujzk.tryindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ujujzk on 02.10.2017
 * Softensy Digital Studio
 * softensiteam@gmail.com
 */

public class ViewPagerIndicator extends LinearLayoutCompat {
    private static final float EDGE_SCALE = .6f;
    private static final float SELECTED_SCALE = 1.6f;
    private static final int COMMON_SCALE = 1;
    private static final int DEF_VALUE = 10;

    private static final int DOTE_COUNT = 6;

    private int mPageCount;
    private int mSelectedIndex;
    private int mOldIndex;
    private int mItemSize = DEF_VALUE;
    private int mDelimiterSize = DEF_VALUE;

    @NonNull
    private final List<ImageView> mIndexImages = new ArrayList<>();
    @Nullable
    private ViewPager.OnPageChangeListener mListener;

    public ViewPagerIndicator(@NonNull final Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator, 0, 0);
        try {
            mItemSize = attributes.getDimensionPixelSize(R.styleable.ViewPagerIndicator_itemSize, DEF_VALUE);
            mDelimiterSize = attributes.getDimensionPixelSize(R.styleable.ViewPagerIndicator_delimiterSize, DEF_VALUE);
        } finally {
            attributes.recycle();
        }
        if (isInEditMode()) {
            createEditModeLayout();
        }
    }

    private void createEditModeLayout() {
        for (int i = 0; i < 5; ++i) {
            final FrameLayout boxedItem = createBoxedItem(i);
            addView(boxedItem);
            if (i == 1) {
                final View item = boxedItem.getChildAt(0);
                final ViewGroup.LayoutParams layoutParams = item.getLayoutParams();
                layoutParams.height *= SELECTED_SCALE;
                layoutParams.width *= SELECTED_SCALE;
                item.setLayoutParams(layoutParams);
            }
        }
    }

    public void setupWithViewPager(@NonNull final ViewPager viewPager) {
        setPageCount(viewPager.getAdapter().getCount());
        viewPager.addOnPageChangeListener(new OnPageChangeListener());
    }

    public void addOnPageChangeListener(final ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    private void setSelectedIndex(final int selectedIndex) {
        if (selectedIndex < 0 || selectedIndex > mPageCount - 1) {
            return;
        }

        if (mPageCount < DOTE_COUNT) { //less

            final ImageView unselectedView = mIndexImages.get(mSelectedIndex);
            unselectedView.setImageResource(R.drawable.white_circle);
            unselectedView.animate().scaleX(COMMON_SCALE).scaleY(COMMON_SCALE).setDuration(300).start();

            final ImageView selectedView = mIndexImages.get(selectedIndex);
            selectedView.animate().scaleX(SELECTED_SCALE).scaleY(SELECTED_SCALE).setDuration(300).start();
            selectedView.setImageResource(R.drawable.blue_circle);

            mSelectedIndex = selectedIndex;

        } else { //more


            if (selectedIndex > mOldIndex) { //increase

                if (mSelectedIndex < DOTE_COUNT - 1) {
                    mSelectedIndex++;
                }

                if (mSelectedIndex < DOTE_COUNT - 1) {

                    final ImageView unselectedView = mIndexImages.get(mSelectedIndex - 1);
                    unselectedView.animate().scaleX(COMMON_SCALE).scaleY(COMMON_SCALE).setDuration(300).start();
                    unselectedView.setImageResource(R.drawable.white_circle);

                    final ImageView selectedView = mIndexImages.get(mSelectedIndex);
                    selectedView.animate().scaleX(SELECTED_SCALE).scaleY(SELECTED_SCALE).setDuration(300).start();
                    selectedView.setImageResource(R.drawable.blue_circle);

                } else if (mSelectedIndex == DOTE_COUNT - 1) {

                    final ImageView firstImage = mIndexImages.get(0);
                    firstImage.animate().scaleX(EDGE_SCALE).scaleY(EDGE_SCALE).setDuration(300).start();

                }
                if (selectedIndex == mPageCount - 1) {

                    final ImageView last = mIndexImages.get(DOTE_COUNT - 1);
                    last.animate().scaleX(SELECTED_SCALE).scaleY(SELECTED_SCALE).setDuration(300).start();
                    last.setImageResource(R.drawable.blue_circle);

                    final ImageView preLast = mIndexImages.get(DOTE_COUNT - 2);
                    preLast.setImageResource(R.drawable.white_circle);
                    preLast.animate().scaleX(COMMON_SCALE).scaleY(COMMON_SCALE).setDuration(300).start();

                } else if (selectedIndex == mPageCount - 2) {
                    final ImageView last = mIndexImages.get(DOTE_COUNT - 1);
                    last.animate().scaleX(COMMON_SCALE).scaleY(COMMON_SCALE).setDuration(300).start();
                }

            } else { //decrease

                if (mSelectedIndex > 0) {
                    mSelectedIndex--;
                }

                if (mSelectedIndex == 0) {

                    final ImageView lastImage = mIndexImages.get(DOTE_COUNT - 1);
                    lastImage.animate().scaleX(EDGE_SCALE).scaleY(EDGE_SCALE).setDuration(300).start();

                } else if (mSelectedIndex > 0) {

                    final ImageView unselectedView = mIndexImages.get(mSelectedIndex + 1);
                    unselectedView.setImageResource(R.drawable.white_circle);
                    unselectedView.animate().scaleX(COMMON_SCALE).scaleY(COMMON_SCALE).setDuration(300).start();

                    final ImageView selectedView = mIndexImages.get(mSelectedIndex);
                    selectedView.animate().scaleX(SELECTED_SCALE).scaleY(SELECTED_SCALE).setDuration(300).start();
                    selectedView.setImageResource(R.drawable.blue_circle);

                }

                if (selectedIndex == 0) {

                    final ImageView first = mIndexImages.get(0);
                    first.animate().scaleX(SELECTED_SCALE).scaleY(SELECTED_SCALE).setDuration(300).start();
                    first.setImageResource(R.drawable.blue_circle);

                    final ImageView second = mIndexImages.get(1);
                    second.setImageResource(R.drawable.white_circle);
                    second.animate().scaleX(COMMON_SCALE).scaleY(COMMON_SCALE).setDuration(300).start();

                } else if (selectedIndex == 1) {
                    final ImageView first = mIndexImages.get(0);
                    first.animate().scaleX(COMMON_SCALE).scaleY(COMMON_SCALE).setDuration(300).start();
                }


            }

            mOldIndex = selectedIndex;

        }


    }


    private void setPageCount(final int pageCount) {
        mPageCount = pageCount;
        mSelectedIndex = 0;
        removeAllViews();
        mIndexImages.clear();

        if (pageCount > DOTE_COUNT) {
            for (int i = 0; i < DOTE_COUNT; ++i) {
                addView(createBoxedItem(i));
            }
        } else {
            for (int i = 0; i < pageCount; ++i) {
                addView(createBoxedItem(i));
            }
        }

        setSelectedIndex(mSelectedIndex);
    }

    @NonNull
    private FrameLayout createBoxedItem(final int position) {
        final FrameLayout box = new FrameLayout(getContext());
        final ImageView item = createItem();
        box.addView(item);
        mIndexImages.add(item);

        final LinearLayoutCompat.LayoutParams boxParams = new LinearLayoutCompat.LayoutParams(
                (int) (mItemSize * SELECTED_SCALE),
                (int) (mItemSize * SELECTED_SCALE)
        );
        if (position > 0) {
            boxParams.setMargins(mDelimiterSize, 0, 0, 0);
        }
        box.setLayoutParams(boxParams);
        return box;
    }

    @NonNull
    private ImageView createItem() {
        final ImageView index = new ImageView(getContext());
        final FrameLayout.LayoutParams indexParams = new FrameLayout.LayoutParams(
                mItemSize,
                mItemSize
        );
        indexParams.gravity = Gravity.CENTER;
        index.setLayoutParams(indexParams);
        index.setImageResource(R.drawable.white_circle);
        index.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return index;
    }

    private class OnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
            if (mListener != null) {
                mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(final int position) {
            setSelectedIndex(position);
            if (mListener != null) {
                mListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
            if (mListener != null) {
                mListener.onPageScrollStateChanged(state);
            }
        }
    }
}
