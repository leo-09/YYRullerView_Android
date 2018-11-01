package liyy.hrg.com.yyrullerview.RullerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * 竖直的 自定义时间轴/可滑动标尺
 * */
public class VerticalScaleScrollView extends BaseScaleView {

    public VerticalScaleScrollView(Context context) {
        super(context);
    }

    public VerticalScaleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalScaleScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerticalScaleScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initVar() {
        mRectWidth = (maxSecond - minSecond) * secondScaleMargin;
        mRectHeight = mScaleHeight * 8;
        mScaleMaxHeight = mScaleHeight * 2;

        // 设置layoutParams
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams((int)mRectWidth, (int)mRectHeight);
        this.setLayoutParams(lp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.makeMeasureSpec((int)mRectHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);

        mScaleScrollViewRange = getMeasuredWidth();
        mTempScale = mScaleScrollViewRange / secondScaleMargin / 2 + minSecond;
        mMidCountScale = mScaleScrollViewRange / secondScaleMargin / 2 + minSecond;
    }

    @Override
    protected void onDrawLine(Canvas canvas, Paint paint) {
        canvas.drawLine(0, 0, 0, (int)mRectHeight, paint);
    }

    @Override
    protected void drawTimeRange(Canvas canvas, Paint paint) {

    }

    @Override
    protected void onDrawScale(Canvas canvas, Paint paint) {
        paint.setTextSize((float)mRectWidth / 4);

        for (int i = 0, k = minSecond; i <= maxSecond - minSecond; i++) {
            if (i % 10 == 0) { // 整值
                canvas.drawLine(0, (float)(i * secondScaleMargin), (float) mScaleMaxHeight, (float)(i * secondScaleMargin), paint);
                // 整值文字
                canvas.drawText(String.valueOf(k), (float)(mScaleMaxHeight + 40), (float)(i * secondScaleMargin + paint.getTextSize() / 3), paint);
                k += 10;
            } else {
                canvas.drawLine(0, (float)(i * secondScaleMargin), (float) mScaleHeight, (float)(i * secondScaleMargin), paint);
            }
        }
    }

    @Override
    protected void onDrawPointer(Canvas canvas, Paint paint) {

        paint.setColor(Color.RED);

        // 每一屏幕刻度的个数/2
        double countScale = mScaleScrollViewRange / secondScaleMargin / 2;
        // 根据滑动的距离，计算指针的位置【指针始终位于屏幕中间】
        int finalY = mScroller.getFinalY();
        // 滑动的刻度（四舍五入取整）
        int tmpCountScale = (int) Math.rint((double) finalY / (double) secondScaleMargin);
        // 总刻度
        mCountScale = tmpCountScale + countScale + minSecond;
        if (mScrollListener != null) { // 回调方法
            mScrollListener.onScaleScroll(mCountScale);
        }

        canvas.drawLine(0,
                (float)(countScale * secondScaleMargin + finalY),
                (float)(mScaleMaxHeight + mScaleHeight),
                (float)(countScale * secondScaleMargin + finalY), paint);
    }

    @Override
    public void scrollToScale(int second) {
        if (second < minSecond || second > maxSecond) {
            return;
        }

        double dy = (second - mCountScale) * secondScaleMargin;
        smoothScrollBy(0, dy);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastX = y;

                if (mScrollListener != null) { // 回调方法
                    mScrollListener.touchBegin();
                }

                return true;
            case MotionEvent.ACTION_MOVE:
                double dataY = mScrollLastX - y;
                if (mCountScale - mTempScale < 0) {         // 向下边滑动
                    if (mCountScale <= minSecond && dataY <= 0)  // 禁止继续向下滑动
                        return super.onTouchEvent(event);
                } else if (mCountScale - mTempScale > 0) {  // 向上边滑动
                    if (mCountScale >= maxSecond && dataY >= 0)  // 禁止继续向上滑动
                        return super.onTouchEvent(event);
                }
                smoothScrollBy(0, dataY);
                mScrollLastX = y;
                postInvalidate();
                mTempScale = mCountScale;
                return true;
            case MotionEvent.ACTION_UP:
                if (mCountScale < minSecond) mCountScale = minSecond;
                if (mCountScale > maxSecond) mCountScale = maxSecond;

                double finalY = (mCountScale - mMidCountScale) * secondScaleMargin;
                mScroller.setFinalY((int)finalY); // 纠正指针位置
                postInvalidate();

                if (mScrollListener != null) { // 回调方法
                    mScrollListener.touchEnd();
                }

                return true;
        }

        return super.onTouchEvent(event);
    }
}
