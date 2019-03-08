package com.example.siy;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private BounceViewPager mViewPager;

    /**
     * 引导ViewPager的展示图片
     */
    private int[] imgRes = {R.drawable.bj_login_j3, R.drawable.bj_login_j1, R.drawable.bj_login_j2, R.drawable.bj_login_j4};

    /**
     * 引导ViewPage的文字
     */
    private int[] strRes = {R.drawable.bj_login_z3, R.drawable.bj_login_z1, R.drawable.bj_login_z2, R.drawable.bj_login_z4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_xx);

        TextView t = findViewById(R.id.user_name);

        t.setText("姓名姓姓名姓姓名姓姓名姓姓名姓姓名姓姓名姓姓名姓姓名姓姓名姓姓名姓姓名姓姓名姓");

//        mViewPager = findViewById(R.id.bvp);
//        initViewViewPager();
    }




    private void initViewViewPager(){
        mViewPager.setBackgroundResource(R.drawable.bj_login_bg);

        mViewPager.setPageMargin(200);

//        mViewPager.getAdapter()

        mViewPager.setAdapter(new PagerAdapter() {

            @Override
            public int getCount() {
                return imgRes.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = getLayoutInflater().inflate(R.layout.vp_item_login_main, null, false);
                ImageView iv = view.findViewById(R.id.iv1);
                iv.setImageResource(imgRes[position]);
                ImageView tv = view.findViewById(R.id.iv2);
                tv.setImageResource(strRes[position]);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        });
    }
}
