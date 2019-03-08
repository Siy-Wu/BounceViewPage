package com.example.siy;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 用来控制LinearLayout的显示，只能用于水平布局
 * <p>
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
        //获取LinearLayout的个数
        int childCount = getChildCount();

        if (childCount == 2) {
            //如果只有2个子View就进行下面的操作

            //获取第二个子View
            View alwaysClidView = getChildAt(1);
            ViewGroup.LayoutParams layoutParams = alwaysClidView.getLayoutParams();

            //获取第二个布局的水平布局的margin
            int alwaysClidViewMargin = 0;
            if (layoutParams instanceof MarginLayoutParams) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) layoutParams;
                alwaysClidViewMargin = marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            }

            //用来记录第一个子View的宽度，保存起来
            View childView = getChildAt(0);
            int width = childView.getMeasuredWidth();

            ViewGroup.LayoutParams layoutParams1 = childView.getLayoutParams();

            //记录第一个子View的水平margin
            int childViewMargin = 0;
            if (layoutParams1 instanceof MarginLayoutParams) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) layoutParams1;
                childViewMargin = marginLayoutParams.rightMargin + marginLayoutParams.leftMargin;

            }

            //将第一个子view的宽度设置成0，让出位置给alwaysClidView，以便于测量alwaysClidView的宽度
            layoutParams1.width = 0;
            childView.setLayoutParams(layoutParams1);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);


            //记录2个子view的margin和alwaysChildView的宽度
            int marginAndWithd = alwaysClidViewMargin + childViewMargin+ alwaysClidView.getMeasuredWidth();


            if (width >= getMeasuredWidth() - marginAndWithd) {
                //如果第一个子View的宽度大于等于LinearLayout的宽度减出marginAndWithd，就表示第一个子View把alwaysChildView挤出去了，需要调整一下不把alwaysChildView挤出去
                layoutParams1.width = getMeasuredWidth() - marginAndWithd;
                childView.setLayoutParams(layoutParams1);
            } else {
                layoutParams1.width = width;
                childView.setLayoutParams(layoutParams1);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


}
