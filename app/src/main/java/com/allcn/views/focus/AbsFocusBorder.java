package com.allcn.views.focus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.allcn.activities.MainActivity;
import com.mast.lib.utils.MLog;
import com.owen.tvrecyclerview.utils.Loger;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by owen on 2017/7/20.
 */

public abstract class AbsFocusBorder extends View implements FocusBorder, ViewTreeObserver.OnGlobalFocusChangeListener {
    private static final String TAG = AbsFocusBorder.class.getSimpleName();
    private static final long DEFAULT_ANIM_DURATION_TIME = 200;
    private static final long DEFAULT_SHIMMER_DURATION_TIME = 2000;

    protected long mAnimDuration = DEFAULT_ANIM_DURATION_TIME;
    protected long mShimmerDuration = DEFAULT_SHIMMER_DURATION_TIME;
    protected RectF mFrameRectF = new RectF();
    protected RectF mPaddingRectF = new RectF();
    protected RectF mPaddingOfsetRectF = new RectF();
    protected RectF mTempRectF = new RectF();

    private LinearGradient mShimmerLinearGradient;
    private Matrix mShimmerGradientMatrix;
    private Paint mShimmerPaint;
    private int mShimmerColor = 0x66FFFFFF;
    private float mShimmerTranslate = 0;
    private boolean mShimmerAnimating = false;
    private boolean mIsShimmerAnim = true;
    private boolean mReAnim = false; //修复RecyclerView焦点临时标记

    private ObjectAnimator mTranslationXAnimator;
    private ObjectAnimator mTranslationYAnimator;
    private ObjectAnimator mWidthAnimator;
    private ObjectAnimator mHeightAnimator;
    private ObjectAnimator mShimmerAnimator;
    private AnimatorSet mAnimatorSet;

    private RecyclerViewScrollListener mRecyclerViewScrollListener;
    private WeakReference<RecyclerView> mWeakRecyclerView;
    private WeakReference<View> mOldFocusView;
    private OnFocusCallback mOnFocusCallback;
    private boolean mIsVisible = false, mInitVisible;

    private float mScaleX;
    private float mScaleY;

    private int mExWidth, mExHeight, mFreeWidth, mFreeHeight, mFreeX, mFreeY;

    private MAnimatorListener mAnimatorListener;

    private boolean needDuplicateIdleStatus;

    protected AbsFocusBorder(Context context, int shimmerColor, long shimmerDuration,
                             boolean isShimmerAnim, long animDuration, RectF paddingOfsetRectF,
                             boolean needDuplicateIdleStatus) {
        super(context);

        this.needDuplicateIdleStatus = needDuplicateIdleStatus;
        this.mInitVisible = false;
        this.mShimmerColor = shimmerColor;
        this.mShimmerDuration = shimmerDuration;
        this.mIsShimmerAnim = isShimmerAnim;
        this.mAnimDuration = animDuration;
        if (null != paddingOfsetRectF)
            this.mPaddingOfsetRectF.set(paddingOfsetRectF);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null); //关闭硬件加速
        setVisibility(INVISIBLE);

        mShimmerPaint = new Paint();
        mShimmerGradientMatrix = new Matrix();

        mAnimatorListener = new MAnimatorListener(this);
    }

    public void stopAnim() {
        if (mAnimatorSet != null) {
            mAnimatorSet.end();
        }
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    /**
     * 绘制闪光
     *
     * @param canvas
     */
    protected void onDrawShimmer(Canvas canvas) {
        if (this.mShimmerAnimating) {
            canvas.save();
            this.mTempRectF.set(this.mFrameRectF);
            this.mTempRectF.left += this.mPaddingOfsetRectF.left;
            this.mTempRectF.top += this.mPaddingOfsetRectF.top;
            this.mTempRectF.right -= this.mPaddingOfsetRectF.right;
            this.mTempRectF.bottom -= this.mPaddingOfsetRectF.bottom;
            float shimmerTranslateX = this.mTempRectF.width() * this.mShimmerTranslate;
            float shimmerTranslateY = this.mTempRectF.height() * this.mShimmerTranslate;
            this.mShimmerGradientMatrix.setTranslate(shimmerTranslateX, shimmerTranslateY);
            this.mShimmerLinearGradient.setLocalMatrix(this.mShimmerGradientMatrix);
            canvas.drawRoundRect(this.mTempRectF, getRoundRadius(), getRoundRadius(),
                    this.mShimmerPaint);
            canvas.restore();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            this.mFrameRectF.set(this.mPaddingRectF.left, this.mPaddingRectF.top,
                    w - this.mPaddingRectF.right, h - this.mPaddingRectF.bottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        onDrawShimmer(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        unBoundGlobalFocusListener();
        super.onDetachedFromWindow();
    }

    private void setShimmerAnimating(boolean shimmerAnimating) {
        this.mShimmerAnimating = shimmerAnimating;
        if (this.mShimmerAnimating) {
            this.mTempRectF.set(this.mFrameRectF);
            this.mTempRectF.left += this.mPaddingOfsetRectF.left;
            this.mTempRectF.top += this.mPaddingOfsetRectF.top;
            this.mTempRectF.right -= this.mPaddingOfsetRectF.right;
            this.mTempRectF.bottom -= this.mPaddingOfsetRectF.bottom;
            this.mShimmerLinearGradient = new LinearGradient(
                    0, 0, this.mTempRectF.width(), this.mTempRectF.height(),
                    new int[]{0x00FFFFFF, 0x1AFFFFFF, this.mShimmerColor, 0x1AFFFFFF, 0x00FFFFFF},
                    new float[]{0f, 0.2f, 0.5f, 0.8f, 1f}, Shader.TileMode.CLAMP);
            this.mShimmerPaint.setShader(this.mShimmerLinearGradient);
        }
    }

    protected void setShimmerTranslate(float shimmerTranslate) {
        if (this.mIsShimmerAnim && this.mShimmerTranslate != shimmerTranslate) {
            this.mShimmerTranslate = shimmerTranslate;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    protected float getShimmerTranslate() {
        return this.mShimmerTranslate;
    }

    protected void setWidth(int width) {
        if (getLayoutParams().width != width) {
            getLayoutParams().width = width;
            requestLayout();
        }
    }

    protected void setHeight(int height) {
        if (getLayoutParams().height != height) {
            getLayoutParams().height = height;
            requestLayout();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (this.mIsVisible != visible || !this.mInitVisible) {
            this.mIsVisible = visible;
            this.mInitVisible = true;
            setVisibility(visible ? VISIBLE : INVISIBLE);

            if (!visible && null != this.mOldFocusView && null != this.mOldFocusView.get()) {
                runFocusScaleAnimation(this.mOldFocusView.get(), 1f, 1f);
                this.mOldFocusView.clear();
                this.mOldFocusView = null;
            }
        }
    }

    @Override
    public boolean isVisible() {
        return this.mIsVisible;
    }

    private void registerScrollListener(RecyclerView recyclerView) {
        if (null != mWeakRecyclerView && mWeakRecyclerView.get() == recyclerView) {
            return;
        }

        if (null == mRecyclerViewScrollListener) {
            mRecyclerViewScrollListener = new RecyclerViewScrollListener(this);
        }

        if (null != mWeakRecyclerView && null != mWeakRecyclerView.get()) {
            mWeakRecyclerView.get().removeOnScrollListener(mRecyclerViewScrollListener);
            mWeakRecyclerView.clear();
        }

        recyclerView.removeOnScrollListener(mRecyclerViewScrollListener);
        mRecyclerViewScrollListener.resetCacheState();
        recyclerView.addOnScrollListener(mRecyclerViewScrollListener);
        mWeakRecyclerView = new WeakReference<>(recyclerView);
    }

    protected Rect findLocationWithView(View view) {
        /*ViewGroup root = (ViewGroup) getParent();
        Rect rect = new Rect();
        root.offsetDescendantRectToMyCoords(view, rect);*/
        return findOffsetDescendantRectToMyCoords(view);
    }

    protected Rect findOffsetDescendantRectToMyCoords(View descendant) {
        final ViewGroup root = (ViewGroup) getParent();
        final Rect rect = new Rect();
        mReAnim = false;
        if (descendant == root) {
            return rect;
        }

        final View srcDescendant = descendant;

        ViewParent theParent = descendant.getParent();
        Object tag;
        Point point;

        // search and offset up to the parent
        while ((theParent != null)
                && (theParent instanceof View)
                && (theParent != root)) {

            rect.offset(descendant.getLeft(), descendant.getTop());

            //兼容TvRecyclerView
            if (theParent instanceof RecyclerView) {
                final RecyclerView rv = (RecyclerView) theParent;
                registerScrollListener(rv);
                tag = rv.getTag();
                if (null != tag && tag instanceof Point) {
                    point = (Point) tag;
                    rect.offset(-point.x, -point.y);
//                    Log.i("@!@!", "point.x="+point.x+" point.y="+point.y);
                }
                if (null == tag && rv.getScrollState() != RecyclerView.SCROLL_STATE_IDLE
                        && (mRecyclerViewScrollListener.mScrolledX != 0 || mRecyclerViewScrollListener.mScrolledY != 0)) {
                    mReAnim = true;
                }
            }

            descendant = (View) theParent;
            theParent = descendant.getParent();
        }

        // now that we are up to this view, need to offset one more time
        // to get into our coordinate space
        if (theParent == root) {
            rect.offset(descendant.getLeft(), descendant.getTop());
        }

        rect.right = rect.left + srcDescendant.getMeasuredWidth();
        rect.bottom = rect.top + srcDescendant.getMeasuredHeight();

        return rect;
    }

    @Override
    public void onFocus(@NonNull View focusView, FocusBorder.Options options, boolean ignoreOld) {
        if (null != mOldFocusView && null != mOldFocusView.get() && !ignoreOld) {
            runFocusScaleAnimation(mOldFocusView.get(), 1f, 1f);
            mOldFocusView.clear();
        } else {
            Loger.e("mOldFocusView is null !!!!!");
        }

        if (focusView == null) {
            MLog.e(TAG, "focusView null");
            return;
        }

        if (options instanceof Options) {
            final Options baseOptions = (Options) options;
            if (baseOptions.isScale() && !ignoreOld) {
                mOldFocusView = new WeakReference<>(focusView);
            }
            runFocusAnimation(focusView, baseOptions, ignoreOld);
        }
    }

    @Override
    public void boundGlobalFocusListener(@NonNull OnFocusCallback callback) {
        mOnFocusCallback = callback;
        getViewTreeObserver().addOnGlobalFocusChangeListener(this);
    }

    @Override
    public void unBoundGlobalFocusListener() {
        if (null != mOnFocusCallback) {
            mOnFocusCallback = null;
            getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
        }
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        runFocusScaleAnimation(oldFocus, 1f, 1f);

        final Options options = null != mOnFocusCallback ? (Options) mOnFocusCallback.onFocus(oldFocus, newFocus) : null;
        if (null != options) {
            runFocusAnimation(newFocus, options, false);
        }
    }

    private void runFocusAnimation(View focusView, Options options, boolean igoreOld) {
        setVisible(true);
        this.mScaleX = options.scaleX;
        this.mScaleY = options.scaleY;
        this.mExWidth = options.exWidth;
        this.mExHeight = options.exHeight;
        this.mFreeWidth = options.freeWidth;
        this.mFreeHeight = options.freeHeight;
        this.mFreeX = options.freeX;
        this.mFreeY = options.freeY;
        runFocusScaleAnimation(focusView, this.mScaleX, this.mScaleY); // 焦点缩放动画
        if (this.mRecyclerViewScrollListener != null) {
            MLog.d(TAG, String.format("runFocusAnimation CacheState=%d",
                    mRecyclerViewScrollListener.getCacheState()));
        }

        if (igoreOld) {
            return;
        }

        if (this.mRecyclerViewScrollListener == null ||
                this.mRecyclerViewScrollListener.getCacheState() == RecyclerView.SCROLL_STATE_IDLE) {
            runBorderAnimation(focusView, options); // 移动边框的动画。
        }
    }

    public void reset() {
        this.mOldFocusView = null;
    }

    protected void runBorderAnimation(View focusView, Options options) {
        if (null == focusView)
            return;

        if (null != mAnimatorSet) {
            mAnimatorSet.cancel();
        }

        createBorderAnimation(focusView, options);

        mAnimatorSet.start();
    }

    /**
     * 焦点VIEW缩放动画
     *
     * @param oldOrNewFocusView
     * @param
     */
    protected void runFocusScaleAnimation(@Nullable final View oldOrNewFocusView, final float scaleX, final float scaleY) {
        if (null == oldOrNewFocusView)
            return;
        MLog.d(TAG, String.format("runFocusScaleAnimation scaleX=%f scaleY=%f", scaleX, scaleY));
        oldOrNewFocusView.animate().scaleX(scaleX).scaleY(scaleY).setDuration(mAnimDuration).start();
    }

    protected void createBorderAnimation(View focusView, Options options) {

        MLog.d(TAG, String.format("createBorderAnimation focusView=%s", focusView));

//        if (focusView instanceof TextView) {
//            MLog.d(TAG, String.format("createBorderAnimation focusView text=%s",
//                    ((TextView) focusView).getText()));
//        }

        final float paddingWidth = mPaddingRectF.left + mPaddingRectF.right + mPaddingOfsetRectF.left + mPaddingOfsetRectF.right;
        final float paddingHeight = mPaddingRectF.top + mPaddingRectF.bottom + mPaddingOfsetRectF.top + mPaddingOfsetRectF.bottom;
        final int ofsetWidth = (int) ((focusView.getMeasuredWidth() - options.freeWidth) * (options.scaleX - 1f) + paddingWidth);
        final int ofsetHeight = (int) ((focusView.getMeasuredHeight() - options.freeHeight) * (options.scaleY - 1f) + paddingHeight);

        final Rect fromRect = findLocationWithView(this);
        final Rect toRect = findLocationWithView(focusView);
        toRect.inset(-ofsetWidth / 2, -ofsetHeight / 2);

        ViewGroup parent = (ViewGroup) focusView.getParent();
        int layerNum = 1;

        /**
         * 这里主要是为了矫正FocusBorder的位置在RecyclerView发生位置动画，位置偏移后会导致FocusBorder的位置
         * 并没有在准确的RecyclerView的Item上
         * 这里tag里面记录了需要获取的第几层父View的tranX，tranY等的值
         * 在xml布局中，tag为1表示只需要获取当前focusView的第一层父View
         * */
        if (parent != null) {
            Object tag = parent.getTag();
            layerNum = tag == null ? 1 : Integer.valueOf(tag.toString());
        }

        MLog.d(TAG, "layerNum=" + layerNum);

        for (int layerIndex = 1; parent != null && layerIndex < layerNum; layerIndex++) {
            parent = (ViewGroup) parent.getParent();
        }

        final int tranX = parent == null ? 0 : (int) parent.getTranslationX();
        final int tranY = parent == null ? 0 : (int) parent.getTranslationY();

        final int newWidth = toRect.width() + options.exWidth - options.freeWidth;
        final int newHeight = toRect.height() + options.exHeight - options.freeHeight;
        final int newX = toRect.left - fromRect.left - (options.exWidth / 2) + tranX - options.freeX;
        final int newY = toRect.top - fromRect.top - (options.exHeight / 2) + tranY - options.freeY;

        MLog.d(TAG, String.format("tranX=%d tranY=%d newX=%d newY=%d", tranX, tranY, newX, newY));

        if (xywhObjAnim == null) {
            xywhObjAnim = ObjectAnimator.ofPropertyValuesHolder(this,
                    getProperytyValX(newX),
                    getProperytyValY(newY),
                    getProperytyValW(newWidth),
                    getProperytyValH(newHeight)
            );
            xywhObjAnim.setDuration(mAnimDuration);
        } else {
            xywhObjAnim.setValues(
                    getProperytyValX(newX),
                    getProperytyValY(newY),
                    getProperytyValW(newWidth),
                    getProperytyValH(newHeight));
        }

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator(1));
        mAnimatorSet.playTogether(xywhObjAnim, getShimmerAnimator());
        mAnimatorSet.addListener(this.mAnimatorListener);
    }

    private ObjectAnimator xywhObjAnim;
    private PropertyValuesHolder pvhX;
    private PropertyValuesHolder pvhY;
    private PropertyValuesHolder pvhW;
    private PropertyValuesHolder pvhH;

    private PropertyValuesHolder getProperytyValX(float x) {
        if (null == pvhX) {
            pvhX = PropertyValuesHolder.ofFloat("translationX", x);
        } else {
            pvhX.setFloatValues(x);
        }
        return pvhX;
    }

    private PropertyValuesHolder getProperytyValY(float y) {
        if (null == pvhY) {
            pvhY = PropertyValuesHolder.ofFloat("translationY", y);
        } else {
            pvhY.setFloatValues(y);
        }
        return pvhY;
    }

    private PropertyValuesHolder getProperytyValW(int w) {
        if (null == pvhW) {
            pvhW = PropertyValuesHolder.ofInt("width", getMeasuredWidth(), w);
        } else {
            pvhW.setIntValues(getMeasuredWidth(), w);
        }
        return pvhW;
    }

    private PropertyValuesHolder getProperytyValH(int h) {
        if (null == pvhH) {
            pvhH = PropertyValuesHolder.ofInt("height", getMeasuredHeight(), h);
        } else {
            pvhH.setIntValues(getMeasuredHeight(), h);
        }
        return pvhH;
    }

    private ObjectAnimator getTranslationXAnimator(float x) {
//        Log.d(TAG, "mTranslationXAnimator is null " + (mTranslationXAnimator == null));
        if (null == mTranslationXAnimator) {
            mTranslationXAnimator = ObjectAnimator.ofFloat(this, "translationX", x)
                    .setDuration(mAnimDuration);
        } else {
            mTranslationXAnimator.setFloatValues(x);
        }
        return mTranslationXAnimator;
    }

    private ObjectAnimator getTranslationYAnimator(float y) {
        if (null == mTranslationYAnimator) {
            mTranslationYAnimator = ObjectAnimator.ofFloat(this, "translationY", y)
                    .setDuration(mAnimDuration);
        } else {
            mTranslationYAnimator.setFloatValues(y);
        }
        return mTranslationYAnimator;
    }

    private ObjectAnimator getHeightAnimator(int height) {
        if (null == mHeightAnimator) {
            mHeightAnimator = ObjectAnimator.ofInt(this, "height", getMeasuredHeight(), height)
                    .setDuration(mAnimDuration);
        } else {
            mHeightAnimator.setIntValues(getMeasuredHeight(), height);
        }
        return mHeightAnimator;
    }

    private ObjectAnimator getWidthAnimator(int width) {
        if (null == mWidthAnimator) {
            mWidthAnimator = ObjectAnimator.ofInt(this, "width", getMeasuredWidth(), width)
                    .setDuration(mAnimDuration);
        } else {
            mWidthAnimator.setIntValues(getMeasuredWidth(), width);
        }
        return mWidthAnimator;
    }

    private ObjectAnimator getShimmerAnimator() {
        if (null == mShimmerAnimator) {
            mShimmerAnimator = ObjectAnimator.ofFloat(this, "shimmerTranslate", -1f, 1f);
            mShimmerAnimator.setInterpolator(new LinearInterpolator());
            mShimmerAnimator.setDuration(mShimmerDuration);
            mShimmerAnimator.setStartDelay(400);
            mShimmerAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setShimmerAnimating(true);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setShimmerAnimating(false);
                }
            });
        }
        return mShimmerAnimator;
    }

    abstract float getRoundRadius();

    abstract List<Animator> getTogetherAnimators(float newX, float newY, int newWidth, int newHeight, Options options);

    abstract List<Animator> getSequentiallyAnimators(float newX, float newY, int newWidth, int newHeight, Options options);

    private static class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        private WeakReference<AbsFocusBorder> mFocusBorder;
        private int mScrolledX = 0, mScrolledY = 0;
        private int cacheState;

        public RecyclerViewScrollListener(AbsFocusBorder border) {
            mFocusBorder = new WeakReference<>(border);
            this.resetCacheState();
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mScrolledX = Math.abs(dx) == 1 ? 0 : dx;
            mScrolledY = Math.abs(dy) == 1 ? 0 : dy;
            Log.i("@!@!", "onScrolled...dx=" + dx + " dy=" + dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            this.cacheState = newState;
            final AbsFocusBorder border = mFocusBorder.get();
            if (newState == RecyclerView.SCROLL_STATE_IDLE && ((mScrolledX != 0 || mScrolledY != 0)
                    || border.needDuplicateIdleStatus)) {
                Log.i("@!@!", "onScrollStateChanged...IDLE");
                final View focused = recyclerView.getFocusedChild();
//                Log.i("@!@!", "onScrollStateChanged...border is null = " + (null == border));
//                Log.i("@!@!", "onScrollStateChanged...focused is null = " + (null == focused));
                if (null != border && null != focused) {
//                    Log.i("@!@!", "onScrollStateChanged...mReAnim  = " + border.mReAnim);
//                    Log.i("@!@!", "onScrollStateChanged...mScrolledX  = " + mScrolledX);
//                    Log.i("@!@!", "onScrollStateChanged...mScrolledY  = " + mScrolledY);
                    for (ViewParent parent = focused.getParent(); parent != null;
                         parent = parent.getParent()) {
                        if (parent instanceof RecyclerView) {
                            border.mReAnim = true;
                            break;
                        }
                    }
                    if (border.mReAnim || mScrolledX != 0 || mScrolledY != 0) {
                        Log.i("@!@!", String.format("onScrollStateChanged...scleX=%f scleY=%f mExWidth=%d mExHeight=%d mFreeWidth=%d mFreeHeight=%d mFreeX=%d mFreeY=%d",
                                border.mScaleX, border.mScaleY, border.mExWidth, border.mExHeight,
                                border.mFreeWidth, border.mFreeHeight, border.mFreeX, border.mFreeY));
                        border.runBorderAnimation(focused, Options.get(border.mScaleX,
                                border.mScaleY, border.mExWidth, border.mExHeight,
                                border.mFreeWidth, border.mFreeHeight, border.mFreeX, border.mFreeY));
                    }
                }
                mScrolledX = mScrolledY = 0;
            }
        }

        public int getCacheState() {
            return cacheState;
        }

        public void resetCacheState() {
            this.cacheState = RecyclerView.SCROLL_STATE_IDLE;
        }
    }

    public static class Options extends FocusBorder.Options {
        protected float scaleX = 1f, scaleY = 1f;
        protected int exWidth, exHeight, freeWidth, freeHeight, freeX, freeY;

        Options() {
        }

        private static class OptionsHolder {
            private static final Options INSTANCE = new Options();
        }

        public static Options get() {
            OptionsHolder.INSTANCE.scaleX = 1f;
            OptionsHolder.INSTANCE.scaleY = 1f;
            OptionsHolder.INSTANCE.exWidth = 0;
            OptionsHolder.INSTANCE.exHeight = 0;
            OptionsHolder.INSTANCE.freeWidth = 0;
            OptionsHolder.INSTANCE.freeHeight = 0;
            OptionsHolder.INSTANCE.freeX = 0;
            OptionsHolder.INSTANCE.freeY = 0;
            return OptionsHolder.INSTANCE;
        }

        public static Options get(float scaleX, float scaleY) {
            OptionsHolder.INSTANCE.scaleX = scaleX;
            OptionsHolder.INSTANCE.scaleY = scaleY;
            OptionsHolder.INSTANCE.exWidth = 0;
            OptionsHolder.INSTANCE.exHeight = 0;
            OptionsHolder.INSTANCE.freeWidth = 0;
            OptionsHolder.INSTANCE.freeHeight = 0;
            OptionsHolder.INSTANCE.freeX = 0;
            OptionsHolder.INSTANCE.freeY = 0;
            return OptionsHolder.INSTANCE;
        }

        public static Options get(float scaleX, float scaleY, int exWidth, int exHeight) {
            OptionsHolder.INSTANCE.scaleX = scaleX;
            OptionsHolder.INSTANCE.scaleY = scaleY;
            OptionsHolder.INSTANCE.exWidth = exWidth;
            OptionsHolder.INSTANCE.exHeight = exHeight;
            OptionsHolder.INSTANCE.freeWidth = 0;
            OptionsHolder.INSTANCE.freeHeight = 0;
            OptionsHolder.INSTANCE.freeX = 0;
            OptionsHolder.INSTANCE.freeY = 0;
            return OptionsHolder.INSTANCE;
        }

        public static Options get(float scaleX, float scaleY, int exWidth, int exHeight,
                                  int freeWidth, int freeHeight) {
            OptionsHolder.INSTANCE.scaleX = scaleX;
            OptionsHolder.INSTANCE.scaleY = scaleY;
            OptionsHolder.INSTANCE.exWidth = exWidth;
            OptionsHolder.INSTANCE.exHeight = exHeight;
            OptionsHolder.INSTANCE.freeWidth = freeWidth;
            OptionsHolder.INSTANCE.freeHeight = freeHeight;
            OptionsHolder.INSTANCE.freeX = 0;
            OptionsHolder.INSTANCE.freeY = 0;
            return OptionsHolder.INSTANCE;
        }

        public static Options get(float scaleX, float scaleY, int exWidth, int exHeight,
                                  int freeWidth, int freeHeight, int freeX, int freeY) {
            OptionsHolder.INSTANCE.scaleX = scaleX;
            OptionsHolder.INSTANCE.scaleY = scaleY;
            OptionsHolder.INSTANCE.exWidth = exWidth;
            OptionsHolder.INSTANCE.exHeight = exHeight;
            OptionsHolder.INSTANCE.freeWidth = freeWidth;
            OptionsHolder.INSTANCE.freeHeight = freeHeight;
            OptionsHolder.INSTANCE.freeX = freeX;
            OptionsHolder.INSTANCE.freeY = freeY;
            return OptionsHolder.INSTANCE;
        }

        public static Options get(int exWidth, int exHeight) {
            OptionsHolder.INSTANCE.scaleX = 1f;
            OptionsHolder.INSTANCE.scaleY = 1f;
            OptionsHolder.INSTANCE.exWidth = exWidth;
            OptionsHolder.INSTANCE.exHeight = exHeight;
            OptionsHolder.INSTANCE.freeWidth = 0;
            OptionsHolder.INSTANCE.freeHeight = 0;
            OptionsHolder.INSTANCE.freeX = 0;
            OptionsHolder.INSTANCE.freeY = 0;
            return OptionsHolder.INSTANCE;
        }

        public static Options get(int exWidth, int exHeight, int freeWidth, int freeHeight) {
            OptionsHolder.INSTANCE.scaleX = 1f;
            OptionsHolder.INSTANCE.scaleY = 1f;
            OptionsHolder.INSTANCE.exWidth = exWidth;
            OptionsHolder.INSTANCE.exHeight = exHeight;
            OptionsHolder.INSTANCE.freeWidth = freeWidth;
            OptionsHolder.INSTANCE.freeHeight = freeHeight;
            OptionsHolder.INSTANCE.freeX = 0;
            OptionsHolder.INSTANCE.freeY = 0;
            return OptionsHolder.INSTANCE;
        }

        public static Options getWHXY(int exWidth, int exHeight, int freeX, int freeY) {
            OptionsHolder.INSTANCE.scaleX = 1f;
            OptionsHolder.INSTANCE.scaleY = 1f;
            OptionsHolder.INSTANCE.exWidth = exWidth;
            OptionsHolder.INSTANCE.exHeight = exHeight;
            OptionsHolder.INSTANCE.freeWidth = 0;
            OptionsHolder.INSTANCE.freeHeight = 0;
            OptionsHolder.INSTANCE.freeX = freeX;
            OptionsHolder.INSTANCE.freeY = freeY;
            return OptionsHolder.INSTANCE;
        }

        public boolean isScale() {
            return scaleX != 1f || scaleY != 1f;
        }
    }

    public static abstract class Builder {
        protected int mShimmerColor = 0x66FFFFFF;
        protected boolean mIsShimmerAnim = true;
        protected long mAnimDuration = AbsFocusBorder.DEFAULT_ANIM_DURATION_TIME;
        protected long mShimmerDuration = AbsFocusBorder.DEFAULT_SHIMMER_DURATION_TIME;
        protected RectF mPaddingOfsetRectF = new RectF();
        protected boolean needDuplicateIdleStatus;

        public Builder shimmerColor(int color) {
            this.mShimmerColor = color;
            return this;
        }

        public Builder shimmerDuration(long duration) {
            this.mShimmerDuration = duration;
            return this;
        }

        public Builder noShimmer() {
            this.mIsShimmerAnim = false;
            return this;
        }

        public Builder animDuration(long duration) {
            this.mAnimDuration = duration;
            return this;
        }

        public Builder padding(float padding) {
            return padding(padding, padding, padding, padding);
        }

        public Builder padding(float left, float top, float right, float bottom) {
            this.mPaddingOfsetRectF.left = left;
            this.mPaddingOfsetRectF.top = top;
            this.mPaddingOfsetRectF.right = right;
            this.mPaddingOfsetRectF.bottom = bottom;
            return this;
        }

        public Builder needDuplicateIdleStatus(boolean needDuplicateIdleStatus) {
            this.needDuplicateIdleStatus = needDuplicateIdleStatus;
            return this;
        }

        public abstract FocusBorder build(Activity activity);

        public abstract FocusBorder build(ViewGroup viewGroup);
    }

    private static class MAnimatorListener implements Animator.AnimatorListener {

        private AbsFocusBorder hostCls;

        public MAnimatorListener(AbsFocusBorder hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (this.hostCls != null) {
                Context context = this.hostCls.getContext();
                if (context instanceof MainActivity) {
//                    ((MainActivity) context).changeKindInfo();
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
