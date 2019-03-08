package com.example.siy;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ViewPager切换动画，右上角进入，左上角退出，底部文字平移
 * Created by Siy on 2018/7/5.
 *
 * @author Siy
 */
public class TranslationPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(@NonNull View itemView, float position) {
        //这里考虑>=-1和<=1的情况
        if (position >= -1 && position <= 1) {
            View view = itemView.findViewById(R.id.iv1);
            if (position < 0) {
                //左进左出的情况
                float offsetY = view.getTop() * Math.abs(position);
                float offsetX = itemView.getWidth() * Math.abs(position);
                view.setTranslationY(-offsetY);
                view.setTranslationX(-offsetX);
            } else {
                //右进右出的情况
                float offsetY = view.getTop() * Math.abs(position);
                float offsetX = itemView.getWidth() * Math.abs(position);
                view.setTranslationY(-offsetY);
                view.setTranslationX(offsetX);
            }
        }
    }
}