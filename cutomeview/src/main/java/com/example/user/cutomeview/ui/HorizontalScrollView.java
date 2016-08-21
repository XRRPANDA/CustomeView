package com.example.user.cutomeview.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Copyright  : 2015-2033 Beijing Startimes Communication & Network Technology Co.Ltd
 * <p/>
 * Created by xiongl on 2016/8/20..
 * ClassName  :
 * Description  :
 */
public class HorizontalScrollView extends ViewGroup {
    Scroller scroller;
    VelocityTracker velocityTracker;  //控制滑动速度

    int mLastX;
    int mLastY;
    int mLastInterceptX;
    int mLastInterceptY;
    int childIndex; //当前子View的索引
    int childSize;  //子View的数量
    int childWidth;

    public HorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        if (scroller == null) {
            scroller = new Scroller(this.getContext());
            velocityTracker = VelocityTracker.obtain();
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                intercept = false;
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                    intercept = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int distanceX = Math.abs(x - mLastInterceptX);
                int distanceY = Math.abs(y - mLastInterceptY);
                if(distanceX > distanceY){
                     intercept = true;
                }else{
                     intercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        mLastInterceptX = x;
        mLastInterceptY = y;
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        velocityTracker.addMovement(event); //对MotionEvent进行测速
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!scroller.isFinished()){
                    scroller.abortAnimation(); //如果动画没有停止,立刻让动画执行完毕
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int distaceX = x - mLastX;
                int distanceY = y - mLastY;
                scrollBy(-distaceX, 0); //边移动,边滑动
                break;
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();  //获取滑动的距离
                velocityTracker.computeCurrentVelocity(1000); //测量滑动速度
                float velocityX = velocityTracker.getXVelocity(); //获取X方向的速度

                if(Math.abs(velocityX) > 50){
                      childIndex = velocityX > 0 ?childIndex -1: childIndex +1;
                }else{
                      childIndex = (scrollX + childWidth/2) / childWidth;  //貌似只对第一页内的滑动有效
                }
                childIndex = Math.max(0,Math.min(childIndex,childSize -1)); //避免index操作索引
                int dx = childIndex * childWidth - scrollX; //滑动距离超过索引时,会有弹性回退，并且滑动距离超过一半时,完成整页切换
                smoothScrollBy(dx, 0);
                velocityTracker.clear();  //重置
                break;
            default:
                break;
        }

        mLastY = y;
        mLastX = x;
        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

    //getScrollX()返回的View的左边缘减去view的内容的左边缘的间距, scroller.getCurrX()获取根据时间比例计算的当前需要滑动的距离加上startX的值。

    public void smoothScrollBy(int dx, int dy) {
        scroller.startScroll(getScrollX(),0,dx,dy,500);
        invalidate();
    }

    /***
     * 按照规范来,需要处理其padding以及子View的margin,并且这里假设子元素的宽高都是相等
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureHeight = 0;
        int measureWidth = 0;
        childSize = getChildCount();
        measureChildren(widthMeasureSpec, heightMeasureSpec); //测量所有的子View


        int widthSpaceSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpaceSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        if (childSize == 0) {
            setMeasuredDimension(0, 0);
        } else if (widthSpecMode == MeasureSpec.AT_MOST && heightMeasureSpec == MeasureSpec.AT_MOST) {
            View childView = getChildAt(0);
            measureWidth = childView.getMeasuredWidth() * childSize;
            measureHeight = childView.getMeasuredHeight();
            setMeasuredDimension(measureWidth, measureHeight);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            View childView = getChildAt(0);
            measureHeight = childView.getMeasuredHeight();
            measureWidth = widthSpaceSize;
            setMeasuredDimension(measureWidth, measureHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            View childView = getChildAt(0);
            measureWidth = childView.getMeasuredWidth() * childSize;
            measureHeight = heightSpaceSize;
            setMeasuredDimension(measureWidth, measureHeight);
        }

    }

    /**
     * 给每个子View布局
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int marginLeft = 0;
        for (int i = 0; i < childSize; i++) {
            View childView = getChildAt(i);
            //对于gone的View不会measure和layout
            if (childView.getVisibility() != GONE) {
                childView.layout(marginLeft, 0, marginLeft + childView.getMeasuredWidth(), childView.getMeasuredHeight());
                childWidth = childView.getMeasuredWidth();
                marginLeft += childView.getMeasuredWidth();
            }
        }
    }

}
