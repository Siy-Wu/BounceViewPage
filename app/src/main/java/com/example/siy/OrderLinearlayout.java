package com.example.siy;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;


/**
 * 用來控制LinearLayout的绘制顺序,Lineaylaoyt从右往左进行绘制
 * <p>
 * Created by Siy on 2019/01/08.
 *
 * @author Siy
 */
public class OrderLinearlayout extends LinearLayout {
    public OrderLinearlayout(Context context) {
        super(context);
        setChildrenDrawingOrderEnabled(true);
    }

    public OrderLinearlayout(Context context, @android.support.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
    }

    public OrderLinearlayout(Context context, @android.support.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        return childCount - 1 - i;
    }
}
