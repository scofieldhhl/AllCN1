package com.allcn.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.allcn.utils.MSG;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

public class AutoScrollView extends ScrollView {

    private static final String TAG = AutoScrollView.class.getSimpleName();
    private boolean isScrolledToTop = true; //初始化的时候设置一下值
    private boolean isScrolledToBottom = false;
    private int paddingTop = 0;
    private boolean scrollAble = false; //是否能滑动

    //三个可设置的属性
    private boolean autoToScroll = true; //是否自动滚动
    private boolean scrollLoop = false; //是否循环滚动
    private int firstTimeScroll = 5000; //多少秒后开始滚动，默认5秒
    private int scrollRate = 50; //多少毫秒滚动一个像素点
    private Handler handler;
    private int childHeight;
    private boolean isRunning;
    private int emptyLineNum;
    private StringBuilder stringBuilder = new StringBuilder();

    public AutoScrollView(Context context) {
        this(context, null, 0);
    }

    public AutoScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEmptyLineNum(int emptyLineNum) {
        this.emptyLineNum = emptyLineNum;
    }

    public void attachHandler(Handler handler) {
        this.handler = handler;
    }

    private ISmartScrollChangedListener iSmartScrollChangedListener;

    public void setiSmartScrollChangedListener(ISmartScrollChangedListener iSmartScrollChangedListener) {
        this.iSmartScrollChangedListener = iSmartScrollChangedListener;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public interface ISmartScrollChangedListener {
        void onScrolledToBottom(); //滑动到底部
        void onScrolledToTop(); //滑动到顶部
    }

    /** ScrollView内的赎途进行滑动时的回调方法，据说是API 9后都是调用这个方法，但是我测试过并不准确 */
    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        MLog.d(TAG, "onOverScrolled");

        if (scrollY == 0) {
            isScrolledToTop = clampedY;
            isScrolledToBottom = false;
        } else {
            isScrolledToTop = false;
            isScrolledToBottom = clampedY; /** 系统回调告诉我们什么时候滑动到底部 */
        }

        notifyScrollChangedListeners();
    }

    /** ScrollView内的视图进行滑动时的回调方法，据说是API 9前都是调用这个方法，我新版的SDK也是或回调这个方法 */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
//        MLog.d(TAG, "onScrollChanged");

//        if (android.os.Build.VERSION.SDK_INT < 9) {  // API 9及之后走onOverScrolled方法监听，
        if (getScrollY() == 0) {
            isScrolledToTop = true;
            isScrolledToBottom = false;
        } else if (getScrollY() + getHeight() - getPaddingTop() - getPaddingBottom() == childHeight) {
            isScrolledToTop = false;
            isScrolledToBottom = true;
        } else {
            isScrolledToTop = false;
            isScrolledToBottom = false;
        }

        notifyScrollChangedListeners();
    }

    /** 获取子View和ScrollView的高度比较，决定是否能够滑动View */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void startScroll() {
        if (getChildCount() == 0) {
            return;
        }
        View child = getChildAt(0);
        childHeight = child.getMeasuredHeight();//获取子控件的高度
        int measuredHeight = getMeasuredHeight();//获取ScrollView的高度
        if (childHeight > measuredHeight) { //如果子控件的高度大于父控件才需要滚动
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                if (textView.getLineCount() - emptyLineNum <= 5) {
                    return;
                }
            }
            scrollAble = true;
            paddingTop = 0;
            isRunning = true;
            Utils.sendMsg(handler, MSG.MSG_SCROLL, firstTimeScroll);
        } else {
            isRunning = false;
            scrollAble = false;
        }
    }

    public void handleScroll() {
        if (scrollAble && autoToScroll) {
            scrollTo(0, paddingTop);
            paddingTop += 1;
            Utils.sendMsg(handler, MSG.MSG_SCROLL, scrollRate);
        }
    }

    public void stopScroll() {
        isRunning = false;
        Utils.removeMsg(handler, MSG.MSG_SCROLL);
        Utils.removeMsg(handler, MSG.MSG_SCROLL_LOOP);
        scrollTo(0, 0);
    }

    public void handleScrollLoop() {
        paddingTop = 0;
        autoToScroll = true;
        scrollTo(0, 0);
        Utils.sendMsg(handler, MSG.MSG_SCROLL, firstTimeScroll);
    }

    /** 判断是否滑动到底部或顶部 */
    private void notifyScrollChangedListeners() {
        if (isScrolledToTop) {
            if (iSmartScrollChangedListener != null) {
                iSmartScrollChangedListener.onScrolledToTop();
            }
        } else if (isScrolledToBottom) {
            Utils.removeMsg(handler, MSG.MSG_SCROLL);
            if (!scrollLoop) {
                scrollAble = false;
            }
            if (scrollAble) {
                Utils.sendMsg(handler, MSG.MSG_SCROLL_LOOP);
            }
            if (iSmartScrollChangedListener != null) {
                iSmartScrollChangedListener.onScrolledToBottom();
            }
        }
    }

    //设置是否自动滚动
    public void setAutoToScroll(boolean autoToScroll) {
        this.autoToScroll = autoToScroll;
    }

    //设置第一次开始滚动的时间
    public void setFistTimeScroll(int fistTimeScroll) {
        this.firstTimeScroll = fistTimeScroll;
        Utils.sendMsg(handler, MSG.MSG_SCROLL, firstTimeScroll);
    }

    //设置滚动的速率，多少毫秒滚动一个像素点
    public void setScrollRate(int scrollRate) {
        this.scrollRate = scrollRate;
    }

    //设置是否循环滚动
    public void setScrollLoop(boolean scrollLoop) {
        this.scrollLoop = scrollLoop;
    }

    public void release() {
        stopScroll();
        stringBuilder = null;
        handler = null;
    }
}
