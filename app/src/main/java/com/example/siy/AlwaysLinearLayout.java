package com.example.siy;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Siy on 2019/03/07.
 *
 * @author Siy
 */
public class AlwaysLinearLayout extends LinearLayout {
    public AlwaysLinearLayout(Context context) {
        super(context);
    }

    public AlwaysLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlwaysLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        adjust();
    }

    private void adjust() {
        if (getChildCount() > 2) {
            throw new RuntimeException("child must <=2");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childCount = getChildCount();
        if (childCount == 2) {
            View alwaysClidView = getChildAt(1);
            ViewGroup.LayoutParams layoutParams = alwaysClidView.getLayoutParams();
            int alwaysClidViewMargin = 0;
            if (layoutParams instanceof MarginLayoutParams) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) layoutParams;
                alwaysClidViewMargin = marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            }

            View childView = getChildAt(0);
            int width = childView.getMeasuredWidth();

            ViewGroup.LayoutParams layoutParams1 = childView.getLayoutParams();
            int childViewMargin = 0;

            int leftMar = 0;
            if (layoutParams1 instanceof MarginLayoutParams) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) layoutParams1;
                childViewMargin = marginLayoutParams.rightMargin;
                leftMar = marginLayoutParams.leftMargin;
            }

            int marginAndWithd = alwaysClidViewMargin + childViewMargin;
            layoutParams1.width = 0;
            childView.setLayoutParams(layoutParams1);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            marginAndWithd = marginAndWithd + alwaysClidView.getMeasuredWidth();
            if (width >= getMeasuredWidth() - marginAndWithd - leftMar) {
                layoutParams1.width = getMeasuredWidth() - marginAndWithd - leftMar;
                childView.setLayoutParams(layoutParams1);
            } else {
                layoutParams1.width = width;
                childView.setLayoutParams(layoutParams1);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }


    }


}
