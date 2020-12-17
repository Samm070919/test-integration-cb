package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by garanda on 11/2/15.
 */
public class FixedTransformerViewPager extends ViewPager {

    PageTransformer pageTransformer;

    public FixedTransformerViewPager(final Context context) {
        super(context);
    }

    public FixedTransformerViewPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setPageTransformer(true, new ZoomOutPageTransformer());
    }

    @Override
    public void setPageTransformer(final boolean reverseDrawingOrder, final ViewPager.PageTransformer transformer) {
        this.pageTransformer = transformer;
    }

    @Override
    protected void onPageScrolled(final int position, final float offset, final int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);
        fixedPageScrolled();
    }

    protected void fixedPageScrolled() {

        final int clientWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

        if (pageTransformer != null) {
            final int scrollX = getScrollX();
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                final ViewPager.LayoutParams layoutParams = (ViewPager.LayoutParams) child.getLayoutParams();

                if (layoutParams.isDecor) {
                    continue;
                }
                //note the getPaddingLeft() that now exists
                final float transformPos = (float) (child.getLeft() - getPaddingLeft() - scrollX) / clientWidth;
                pageTransformer.transformPage(child, transformPos);
            }
        }
    }
}
