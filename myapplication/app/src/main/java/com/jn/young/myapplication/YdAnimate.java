package com.jn.young.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/**
 * Created by zhengjinyang on 16/8/3.
 * 从1.变为三的过程，以及刷新过程中的动画
 */
public class YdAnimate extends Drawable {
    private final int DEFAULT_MAX_PULL_LEN = 600;
    private final int MAX_PULL_REFRESHINGLEN = 600;
    private final float MIN_PULL_REFRESHINGLEN = 1.0f;//一个刷新预置，超过这个阈值才进行刷新的重绘
    private final int REFRESHING_DURATION = 200; //刷新过程中的动画周期为1s
    private final int MIN_BORDER_RADIUS = 6;//外围边框的最小圆角
    private final int ROUNDCORNER_RADIUS = 2;//边角倒角的设计值
    private Animation mAnimation;
    private Animation mTipAnimation;
    private Paint mPaintCircle;//外围border的paint
    private Paint mPaintRect;//内部三个矩形的pain
    private Paint mPaintTip;//提示的背景

    private float mPaintTipHalfWidth;//提示背景的半宽
    private Matrix mMatrix;
    private int mColor;
    private Context mContext;
    private int mMaxPullLength;//下拉的最大长度，
    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();//线性比例放大
    private static final Interpolator DECELERATEINTERPOLATOR = new DecelerateInterpolator();//减速放大

    private float mLength;

    private float mScale;//刷新过程中采用差值动画

    private boolean mIsRefreshing;
    private boolean mIsReset;

    private int mCenterX;
    private int mCenterY;

    private View mParent;

    public YdAnimate(Context context, View parent, int color) {
        mContext = context;
        mColor = color;
        mMatrix = new Matrix();
        mPaintCircle = new Paint();
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setColor(color);
        mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setStrokeWidth(2.0f);
        mPaintRect = new Paint();
        mPaintRect.setStrokeCap(Paint.Cap.ROUND);
        mPaintRect.setAntiAlias(true);
        mPaintRect.setStyle(Paint.Style.FILL);
        mPaintRect.setColor(mColor);
        mPaintRect.setStrokeWidth(getPxForDifferentScreen(4));

        mPaintTip = new Paint();
        mPaintTip.setStyle(Paint.Style.FILL);
        mPaintTip.setStrokeWidth(1.0f);

        mParent = parent;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        mCenterX = screenSize.x/2;
        mCenterY = mParent.getHeight() / 2;

        mPaintTipHalfWidth = getPxForDifferentScreen(48)/2;

//        mMaxPullLength = maxPullLength < mParent.getWidth() ? mParent.getWidth() : maxPullLength;//选取屏幕宽度和设定最大长度的较小值
        mMaxPullLength = 6;
        initAnimation();
    }


    private void initAnimation() {
        mAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                setScale(interpolatedTime);
            }
        };
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setDuration(REFRESHING_DURATION);
        mAnimation.setInterpolator(DECELERATEINTERPOLATOR);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        mTipAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (mPaintTipHalfWidth < display.getWidth()) {
                    mPaintTipHalfWidth *= 1.1;
                }
            }
        };

        mTipAnimation.setDuration(REFRESHING_DURATION);
        mTipAnimation.setRepeatCount(Animation.INFINITE);
        mTipAnimation.setRepeatMode(Animation.REVERSE);
        mTipAnimation.setInterpolator(LINEAR_INTERPOLATOR);

    }

    /**
     * 刷新过程中的三条线的变化控制
     * @param len
     */
    private void setScale(float len) {
        mScale = len;
        invalidateSelf();
        mParent.invalidate();
    }

    /**
     * 设置Paint的颜色
     * @param color
     */
    public void setColor(int color) {
        mColor = color;
        if (mPaintCircle != null) {
            mPaintCircle.setColor(color);
        }
        if (mPaintRect != null) {
            mPaintRect.setColor(color);
        }
        if (mPaintTip != null) {
            mPaintTip.setColor(getAlphaColor(color));
        }
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 由于在rect2中进行了旋转画布的操作，所以先后顺序不能变化
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        if (mIsReset){
            mParent.startAnimation(mTipAnimation);
            drawTipBackGround(canvas);
        }else {
            drawCircle(canvas);
            drawRect3(canvas);
            drawRect1(canvas);
            drawRect2(canvas);
        }

    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        //noinspection WrongConstant
        return 0;
    }


    /**
     * 绘制外围的circle，需要控制外部的矩形的圆角
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        RectF rectF;
        if (mIsRefreshing) {
            rectF = new RectF(mCenterX - getPxForDifferentScreen(19), mCenterY - getPxForDifferentScreen(21), mCenterX + getPxForDifferentScreen(19), mCenterY + getPxForDifferentScreen(21));
            canvas.drawRoundRect(rectF, getPxForDifferentScreen(6), getPxForDifferentScreen(6), mPaintCircle);

        } else {
            rectF = new RectF(mCenterX - getBorderWidth(mLength), mCenterY - getBorderHeight(mLength), mCenterX + getBorderWidth(mLength), mCenterY + getBorderHeight(mLength));
            canvas.drawRoundRect(rectF, getBorderCornerRadius(mLength), getBorderCornerRadius(mLength), mPaintCircle);

        }

    }

    /**
     * 外部矩形的宽度
     * @param length
     * @return
     */
    private float getBorderCornerRadius(float length) {
        float calcRadius = 22 - (length / mMaxPullLength) * 75;//因为变化的太小，所以乘一个系数
        return calcRadius < MIN_BORDER_RADIUS ? getPxForDifferentScreen(MIN_BORDER_RADIUS) : getPxForDifferentScreen(calcRadius);//不能小于6
    }


    /**
     * 外部矩形的长度
     * @param length
     * @return
     */
    private float getBorderWidth(float length){
        float calcWidth = 22 - length*2;
        float minWidth = 20;
        return calcWidth > minWidth ? getPxForDifferentScreen(calcWidth): getPxForDifferentScreen(minWidth);
    }

    private float getBorderHeight(float length){
        float calcWidth = 22 + length*2;
        float maxWidth = 24;
        return calcWidth < maxWidth ? getPxForDifferentScreen(calcWidth): getPxForDifferentScreen(maxWidth);
    }

    /**
     * 绘制第一条线
     * @param canvas
     */
    private void drawRect1(Canvas canvas) {
        RectF rectF;
        if (mIsRefreshing) {
            rectF = new RectF((float) (mCenterX - 11 - (0.5 - mScale) * 2 * getPxForDifferentScreen(2)), mCenterY - getPxForDifferentScreen(10) - getPxForDifferentScreen(2), (float) (mCenterX + 11 + (0.5 - mScale) * 2 * getPxForDifferentScreen(2)), mCenterY + getPxForDifferentScreen(2) - getPxForDifferentScreen(10));
            canvas.drawRoundRect(rectF, getPxForDifferentScreen(ROUNDCORNER_RADIUS), getPxForDifferentScreen(ROUNDCORNER_RADIUS), mPaintRect);
        } else {
//            rectF = new RectF(mCenterX - getPxForDifferentScreen(11), mCenterY - getPxForDifferentScreen(12), getTopRectEndX(mLength), mCenterY - getPxForDifferentScreen(8));
//            canvas.drawRoundRect(rectF,getPxForDifferentScreen(3), getPxForDifferentScreen(3), mPaintRect);
            canvas.drawLine(getTopRectStartX(mLength), getTopRectStartY(mLength), getTopRectEndX(mLength), getTopRectEndY(mLength),mPaintRect);
        }
    }


    private float getTopRectStartX(float length){
        float calpos = mCenterX - getPxForDifferentScreen(6) - length*8;
        float minPos = mCenterX - getPxForDifferentScreen(9);
        return calpos > minPos ? calpos : minPos;
    }

    private float getTopRectStartY(float length){
        float calpos = mCenterY - getPxForDifferentScreen(8) - length*8;
        float minPos = mCenterY - getPxForDifferentScreen(10);
        return calpos > minPos ? calpos : minPos;
    }
    private float getTopRectEndX(float length){
        float calPos = mCenterX + length * 12;
        float maxPos = mCenterX + getPxForDifferentScreen(9);
        return calPos < maxPos ? calPos : maxPos;
    }

    private float getTopRectEndY(float length){
        float calPos = mCenterY - getPxForDifferentScreen(10) + length * 12;
        float minPos = mCenterY - getPxForDifferentScreen(10);
        return calPos < minPos ? calPos : minPos;
    }

    /**
     * 1的那个斜边的转动
     * @param length
     * @return
     */
    private float getUpRectRotateDegree(float length){
        float calDegree = 60 - length *15;
        return calDegree >0 ? calDegree : 0;
    }

    /**
     * 画中间那一竖
     * @param canvas
     */
    private void drawRect2(Canvas canvas) {
        RectF rectF;
        if (mIsRefreshing) {
            rectF = new RectF((float) (mCenterX - 11 - (mScale - 0.5) * 2 * getPxForDifferentScreen(2)), mCenterY - getPxForDifferentScreen(2), (float) (mCenterX + 11 + (mScale - 0.5) * 2 * getPxForDifferentScreen(2)), mCenterY + getPxForDifferentScreen(2));
            canvas.drawRoundRect(rectF, getPxForDifferentScreen(ROUNDCORNER_RADIUS), getPxForDifferentScreen(ROUNDCORNER_RADIUS), mPaintRect);

        } else {
            Matrix matrix = mMatrix;
            matrix.reset();
//            matrix.setTranslate(mCenterX, mCenterY);
//            matrix.setRotate(getMidRectRotateDegree(mLength));
            rectF = new RectF(mCenterX - getMidRectHalfLen(mLength), mCenterY + getPxForDifferentScreen(-2), mCenterX + getMidRectHalfLen(mLength),mCenterY +  getPxForDifferentScreen(2));
            canvas.rotate(getMidRectRotateDegree(mLength), mCenterX, mCenterY);
            canvas.drawRoundRect(rectF,getPxForDifferentScreen(3), getPxForDifferentScreen(3), mPaintRect);
            canvas.setMatrix(matrix);
        }
    }


    /**
     *从下拉的长度计算出中间的那个矩形的旋转角度
     *
     */
    private float getMidRectRotateDegree(float length){
        float calDegree = 90 - length * 80;//转动速度太慢，乘一个系数
        return calDegree > 0? calDegree : 0; //不能小于0
    }

    /**
     * 中间那条线在旋转过程中长度会变化，计算半长
     * @param length
     * @return
     */
    private float getMidRectHalfLen(float length){
        float calLength = getPxForDifferentScreen(12) - length * 3;
        float minLength = getPxForDifferentScreen(11);
        return calLength > minLength ? calLength : minLength;
    }

    /**
     * 绘制第三条刷新线
     * @param canvas
     */
    private void drawRect3(Canvas canvas) {
        RectF rectF;
        if (mIsRefreshing) {
            rectF = new RectF((float) (mCenterX - 11 - (0.5 - mScale) * 2 * getPxForDifferentScreen(2)), mCenterY + getPxForDifferentScreen(10) - getPxForDifferentScreen(2), (float) (mCenterX + 11 + (0.5 - mScale) * 2 * getPxForDifferentScreen(2)), mCenterY + getPxForDifferentScreen(2) + getPxForDifferentScreen(10));
            canvas.drawRoundRect(rectF, getPxForDifferentScreen(ROUNDCORNER_RADIUS), getPxForDifferentScreen(ROUNDCORNER_RADIUS), mPaintRect);

        } else {
            rectF = new RectF(getBottomLineLeftSide(mLength),mCenterY - getBottomLineMovePos(mLength)/3 + getPxForDifferentScreen(10) - getBottomeLineWidth(mLength), mCenterX + getBottomLineMovePos(mLength) + getPxForDifferentScreen(11), mCenterY - getBottomLineMovePos(mLength)/3 + getPxForDifferentScreen(10) + getBottomeLineWidth(mLength));
            canvas.drawRoundRect(rectF,getPxForDifferentScreen(3), getPxForDifferentScreen(3), mPaintRect);
        }
    }

    /**
     * 第三条线的最左边的位置
     * @param length
     * @return
     */
    private float getBottomLineLeftSide(float length){
        float calPos = mCenterX + getBottomLineMovePos(length) + getPxForDifferentScreen(5) - length * 30;
        float minPos = mCenterX - getPxForDifferentScreen(11);
        return calPos > minPos ? calPos: minPos; //不能超过左边的那一部分；
    }

    /**
     * 下边的那个点位置需要从右向左移动
     * @param length
     * @return
     */

    private float getBottomLineMovePos(float length){
        float calPos = 2 - length;
        return calPos > 0 ? getPxForDifferentScreen(calPos) : 0;

    }

    private float getBottomeLineWidth(float length){
        float calWidth = 3 - length;
        float minWidth = 2;
        return calWidth > minWidth? getPxForDifferentScreen(calWidth):getPxForDifferentScreen(minWidth);
    }

    /**
     * 显示刷新提示的
     * @param canvas
     */
    private void drawTipBackGround(Canvas canvas){
        RectF rectF = new RectF(mCenterX-mPaintTipHalfWidth, 0, mCenterX + mPaintTipHalfWidth, 2*mCenterY);
        canvas.drawRoundRect(rectF, getPxForDifferentScreen(ROUNDCORNER_RADIUS), getPxForDifferentScreen(ROUNDCORNER_RADIUS), mPaintTip);
    }


    public void setPullToRefresh() {
        mIsRefreshing = false;
    }


    /**
     * 刷新过程中实现
     */
    public void setRefreshing() {
        if (mAnimation == null) {
            initAnimation();
        }
        mIsRefreshing = true;
        mIsReset = false;
        mPaintTipHalfWidth = getPxForDifferentScreen(24);//提示背景在刷新过程中需要重置为原始大小,x=y,即一个正方形
        mCenterY = mParent.getHeight() / 2;
        mParent.startAnimation(mAnimation);

    }

    /**
     * 完成刷新之后进行的一些列操作，包括停止动画
     */
    public void setReset() {
        if (mAnimation != null) {
            mAnimation.cancel();
        }

        mIsRefreshing = false;
        mIsReset = true;
    }


    /**
     * 传入下拉的长度
     * @param length
     */
    public void setLength(float length) {
        mIsReset = false;
        if (length < MIN_PULL_REFRESHINGLEN){
            length = MIN_PULL_REFRESHINGLEN;
            mLength = 0;
        }else {
            mLength = (length - MIN_PULL_REFRESHINGLEN)*2.4f;//因为要下拉一段时间才能开始动画
        }
        if (mAnimation != null) {
            mAnimation.reset();
        }
        mCenterY = (int) (mParent.getHeight() / 2 + length);
        invalidateSelf();
        mParent.invalidate();
    }


    /**
     * 从设计尺寸转化为显示尺寸
     * @param designSize
     * @return
     */
    public float getPxForDifferentScreen(float designSize) {
        WindowManager wmg = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wmg.getDefaultDisplay().getMetrics(dm);
        return designSize / 2 * dm.density;
    }


    /**
     * 显示刷新结果时的半透明背景
     * @param color
     * @return
     */
    private int getAlphaColor(int color){
        return 0x19000000|(0x00FFFFFF&color);
    }

}

