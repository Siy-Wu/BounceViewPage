package com.example.siy;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * 自定义ViewPager
 * <p>
 * 主要实现功能:<br/>
 * 1、背景随着页面滑动而滚动<br/>
 * 2、第一个page和最后一page的弹性滑动
 * <p>
 * Created by Siy on 2018/7/5.
 *
 * @author Siy
 */
public class BounceViewPager extends ViewPager {

    /**
     * maximum z distance to translate child view
     */
    final static int DEFAULT_OVERSCROLL_TRANSLATION = 150;

    /**
     * duration of overscroll animation in ms
     */
    final private static int DEFAULT_OVERSCROLL_ANIMATION_DURATION = 400;

    @SuppressWarnings("unused")
    private final static String DEBUG_TAG = ViewPager.class.getSimpleName();
    private final static int INVALID_POINTER_ID = -1;

    /**
     * @author renard, extended by Piotr Zawadzki
     */
    private class OverscrollEffect {
        private float mOverscroll;
        private Animator mAnimator;

        /**
         * @param deltaDistance [0..1] 0->no overscroll, 1>full overscroll
         */
        public void setPull(final float deltaDistance) {
            mOverscroll = deltaDistance;
            invalidateVisibleChilds(mLastPosition);
        }

        /**
         * called when finger is released. starts to animate back to default position
         */
        private void onRelease() {
            if (mAnimator != null && mAnimator.isRunning()) {
                mAnimator.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startAnimation(0);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });
                mAnimator.cancel();
            } else {
                startAnimation(0);
            }
        }

        private void startAnimation(final float target) {
            mAnimator = ObjectAnimator.ofFloat(this, "pull", mOverscroll, target);
            mAnimator.setInterpolator(new DecelerateInterpolator());
            final float scale = Math.abs(target - mOverscroll);
            mAnimator.setDuration((long) (mOverscrollAnimationDuration * scale));
            mAnimator.start();
        }

        private boolean isOverscrolling() {
            if (mScrollPosition == 0 && mOverscroll < 0) {
                return true;
            }
            final boolean isLast = (getAdapter().getCount() - 1) == mScrollPosition;
            return isLast && mOverscroll > 0;
        }

    }

    final private OverscrollEffect mOverscrollEffect = new OverscrollEffect();
    final private Camera mCamera = new Camera();

    private OnPageChangeListener mScrollListener;
    private float mLastMotionX;
    private int mActivePointerId;
    private int mScrollPosition;
    private float mScrollPositionOffset;
    final private int mTouchSlop;

    private float mOverscrollTranslation;
    private int mOverscrollAnimationDuration;

    public BounceViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setStaticTransformationsEnabled(true);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        super.setOnPageChangeListener(new MyOnPageChangeListener());
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BounceViewPager);
        mOverscrollTranslation = a.getDimension(R.styleable.BounceViewPager_overscroll_translation, DEFAULT_OVERSCROLL_TRANSLATION);
        mOverscrollAnimationDuration = a.getInt(R.styleable.BounceViewPager_overscroll_animation_duration, DEFAULT_OVERSCROLL_ANIMATION_DURATION);
        a.recycle();
    }

    public int getOverscrollAnimationDuration() {
        return mOverscrollAnimationDuration;
    }

    public void setOverscrollAnimationDuration(int mOverscrollAnimationDuration) {
        this.mOverscrollAnimationDuration = mOverscrollAnimationDuration;
    }

    public float getOverscrollTranslation() {
        return mOverscrollTranslation;
    }

    public void setOverscrollTranslation(int mOverscrollTranslation) {
        this.mOverscrollTranslation = mOverscrollTranslation;
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mScrollListener = listener;
    }

    ;

    private void invalidateVisibleChilds(final int position) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).invalidate();

        }
    }

    private int mLastPosition = 0;

    private class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mScrollListener != null) {
                mScrollListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
            mScrollPosition = position;
            mScrollPositionOffset = positionOffset;
            mLastPosition = position;
            invalidateVisibleChilds(position);
        }

        @Override
        public void onPageSelected(int position) {

            if (mScrollListener != null) {
                mScrollListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(final int state) {

            if (mScrollListener != null) {
                mScrollListener.onPageScrollStateChanged(state);
            }
            if (state == SCROLL_STATE_IDLE) {
                mScrollPositionOffset = 0;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    mLastMotionX = ev.getX();
                    mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                    break;
                }
                case MotionEventCompat.ACTION_POINTER_DOWN: {
                    final int index = MotionEventCompat.getActionIndex(ev);
                    final float x = MotionEventCompat.getX(ev, index);
                    mLastMotionX = x;
                    mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                    break;
                }
                default:
            }
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean callSuper = false;

        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                callSuper = true;
                mLastMotionX = ev.getX();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                callSuper = true;
                final int index = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, index);
                mLastMotionX = x;
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId != INVALID_POINTER_ID) {
                    // Scroll to follow the motion event
                    final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                    final float x = MotionEventCompat.getX(ev, activePointerIndex);
                    final float deltaX = mLastMotionX - x;
                    final float oldScrollX = getScrollX();
                    final int width = getWidth();
                    final int widthWithMargin = width + getPageMargin();
                    final int lastItemIndex = getAdapter().getCount() - 1;
                    final int currentItemIndex = getCurrentItem();
                    final float leftBound = Math.max(0, (currentItemIndex - 1) * widthWithMargin);
                    final float rightBound = Math.min(currentItemIndex + 1, lastItemIndex) * widthWithMargin;
                    final float scrollX = oldScrollX + deltaX;
                    if (mScrollPositionOffset == 0) {
                        if (scrollX < leftBound) {
                            if (leftBound == 0) {
                                final float over = deltaX + mTouchSlop;
                                mOverscrollEffect.setPull(over / width);
                            }
                        } else if (scrollX > rightBound) {
                            if (rightBound == lastItemIndex * widthWithMargin) {
                                final float over = scrollX - rightBound - mTouchSlop;
                                mOverscrollEffect.setPull(over / width);
                            }
                        }
                    } else {
                        mLastMotionX = x;
                    }
                } else {
                    mOverscrollEffect.onRelease();
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                callSuper = true;
                mActivePointerId = INVALID_POINTER_ID;
                mOverscrollEffect.onRelease();
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastMotionX = ev.getX(newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                    callSuper = true;
                }
                break;
            }
            default:
        }

        if (mOverscrollEffect.isOverscrolling() && !callSuper) {
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        if (child.getWidth() == 0) {
            return false;
        }
        final int position = child.getLeft() / child.getWidth();
        final boolean isFirstOrLast = position == 0 || (position == getAdapter().getCount() - 1);
        if (mOverscrollEffect.isOverscrolling() && isFirstOrLast) {
            final float dx = getWidth() / 2;
            final int dy = getHeight() / 2;
            t.getMatrix().reset();
            final float translateX = (float) mOverscrollTranslation * (mOverscrollEffect.mOverscroll > 0 ? Math.min(mOverscrollEffect.mOverscroll, 1) : Math.max(mOverscrollEffect.mOverscroll, -1));
            mCamera.save();
            mCamera.translate(-translateX, 0, 0);
            mCamera.getMatrix(t.getMatrix());
            mCamera.restore();
            t.getMatrix().preTranslate(-dx, -dy);
            t.getMatrix().postTranslate(dx, dy);

            if (bounceListener != null) {
                bounceListener.onBounce(child, translateX);
            }

            if (getChildCount() == 1) {
                this.invalidate();
            } else {
                child.invalidate();
            }
            return true;
        }
        return false;
    }

    private BounceListener bounceListener;

    public void setBounceListener(BounceListener bounceListener) {
        this.bounceListener = bounceListener;
    }

    /**
     * 弹性滑动的监听
     */
    public interface BounceListener {
        /**
         * @param view
         * @param translaeX
         */
        void onBounce(View view, float translaeX);
    }


//----------------------------------- 上面代码是实现第一页和最后一页X轴的弹性滑动

    private Bitmap bg;
    private Paint b = new Paint(Paint.ANTI_ALIAS_FLAG );


    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (this.bg != null) {
            int width = this.bg.getWidth();
            int height = this.bg.getHeight();
            int count = getAdapter().getCount();
            int x = getScrollX();
            // 子View中背景图片需要显示的宽度，放大背景图或缩小背景图。
            int n = height * getWidth() / getHeight();

            /**
             * (width - n) / (count - 1)表示除去显示第一个ViewPager页面用去的背景宽度，剩余的ViewPager需要显示的背景图片的宽度。
             * getWidth()等于ViewPager一个页面的宽度，即手机屏幕宽度。在该计算中可以理解为滑动一个ViewPager页面需要滑动的像素值。
             * ((width - n) / (count - 1)) /getWidth()也就表示ViewPager滑动一个像素时，背景图片滑动的宽度。
             * x * ((width - n) / (count - 1)) /  getWidth()也就表示ViewPager滑动x个像素时，背景图片滑动的宽度。
             * 背景图片滑动的宽度的宽度可以理解为背景图片滑动到达的位置。
             */
            int w = x * ((width - n) / (count - 1)) / getWidth();
            canvas.drawBitmap(this.bg, new Rect(w, 0, n + w, height), new Rect(x, 0, x + getWidth(), getHeight()), this.b);
        }
        super.dispatchDraw(canvas);
    }

    public void setBackGroud(Bitmap paramBitmap) {
        this.bg = paramBitmap;
        this.b.setFilterBitmap(true);
    }

    //-------------------------------------------------------------------上面代码是为了背景随着页面滑动

    public static class FixedSpeedScroller extends Scroller {

        //滑动的时间
        private int mDuration = 1500;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        /**
         * 获取滑动时间间隔
         *
         * @return
         */
        public int getmDuration() {
            return mDuration;
        }

        /**
         * 设置滑动时间间隔
         *
         * @param duration
         */
        public void setmDuration(int duration) {
            this.mDuration = duration;
        }
    }

    /**
     * 用来保存初始的{@link #mScroller}
     */
    private Scroller cacheMScroller;

    /**
     * 自己设置切换速度
     */
    private int mCustomDuration = -1;

    /**
     * 主要是在轮播时修改切换速度
     *
     * @param duration
     */
    public void setDuration(int duration) {
        if (duration != mCustomDuration && duration > 0) {
            mCustomDuration = duration;
            Log.e("siy", "setDuration");
            FixedSpeedScroller scroller = new FixedSpeedScroller(getContext(), new AccelerateInterpolator());
            scroller.setmDuration(duration);
            setDuration(scroller);
        }
    }

    private void setDuration(Scroller scroller) {
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            //在设置之前保存一下原来的scroller
            if (cacheMScroller == null) {
                //只有当cacheMScroller是null的时候才保存
                cacheMScroller = (Scroller) field.get(this);
            }
            field.set(this, scroller);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 还原之前的{@link #mScroller}
     */
    public void reSetDuration() {
        if (cacheMScroller != null && mCustomDuration != -1) {
            Log.e("siy", "reSetDuration");
            mCustomDuration = -1;
            setDuration(cacheMScroller);
        }
    }

    //----------------------------------------------上面的代码是为了自定义滑动速度-----------------------------------
}