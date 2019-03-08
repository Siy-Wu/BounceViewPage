package com.example.siy;

import android.content.Intent;
import android.graphics.BitmapFactory;
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
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.bvp);
        initViewViewPager();


        findViewById(R.id.to_next_page)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this,SecActivity.class));
                    }
                });
    }




    private void initViewViewPager(){
        mViewPager.setBackGroud(BitmapFactory.decodeResource(getResources(), R.drawable.bj_login_bg));

        mViewPager.setPageTransformer(true,new TranslationPageTransformer());

        mViewPager.setBounceListener(new BounceViewPager.BounceListener() {
            @Override
            public void onBounce(View child, float translaeX) {
                final int position = child.getLeft() / child.getWidth();
                //通过translateX给iv1一个Y轴上的位移
                View view = child.findViewById(R.id.iv1);
                if (view != null) {
                    if (position == mViewPager.getAdapter().getCount() - 1) {
                        //最后一个page
                        view.setTranslationY(-translaeX);
                    } else if (position == 0) {
                        //第一个page
                        view.setTranslationY(translaeX);
                    }
                }
            }
        });

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
