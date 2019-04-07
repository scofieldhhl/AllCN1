package com.allcn.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mast.lib.utils.MLog;

import java.util.ArrayList;

public class FocusKeepRecyclerView extends RecyclerView {

    private static final String TAG = FocusKeepRecyclerView.class.getSimpleName();
    //是否可以纵向移出
    private boolean mCanFocusOutVertical = true;
    //是否可以横向移出
    private boolean mCanFocusOutHorizontal = true;
    //焦点移出recyclerview的事件监听
    private FocusLostListener mFocusLostListener;
    //焦点移入recyclerview的事件监听
    private FocusGainListener mFocusGainListener;
    //默认第一次选中第一个位置
    private int mCurrentFocusPosition = 0;
    private String exTag;
    private boolean cacheHasFocus = true;

    public FocusKeepRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public FocusKeepRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusKeepRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setChildrenDrawingOrderEnabled(true);
        setItemAnimator(null);
    }

    @Override
    public View focusSearch(int direction) {
        return super.focusSearch(direction);
    }

    //覆写focusSearch寻焦策略
    @Override
    public View focusSearch(View focused, int direction) {
        MLog.d(TAG + exTag, String.format("focusSearch focused=%s direction=%d", focused, direction));
        MLog.d(TAG + exTag, String.format("focusSearch cacheHasFocus=%b", cacheHasFocus));
        if (!this.cacheHasFocus) {
            this.cacheHasFocus = true;
            if (focused != null) {
                return focused;
            }
        }
        View view = super.focusSearch(focused, direction);
        if (focused == null) {
            MLog.e(TAG, "super focusSearch focused not found");
            return view;
        }
        if (view != null) {
            MLog.d(TAG + exTag, String.format("super focusSearch view found %s", view));
            // 该方法返回焦点view所在的父view, 如果是在recyclerview之外，就会是null.
            // 所以根据是否是null,来判断是否是移出了recyclerview
            View nextFocusItemView = findContainingItemView(view);
            if (nextFocusItemView == null) {
                MLog.e(TAG + exTag, "nextFocusItemView not found");
                MLog.d(TAG + exTag, String.format("mCanFocusOutVertical=%b mCanFocusOutHorizontal=%b",
                        mCanFocusOutVertical, mCanFocusOutHorizontal));
                if (!mCanFocusOutVertical && (direction == View.FOCUS_DOWN ||
                        direction == View.FOCUS_UP)) {
                    //屏蔽焦点纵向移出recyclerview
                    return focused;
                }
                if (!mCanFocusOutHorizontal && (direction == View.FOCUS_LEFT ||
                        direction == View.FOCUS_RIGHT)) {
                    //屏蔽焦点横向移出recyclerview
                    return focused;
                }
                return view;
            }
            MLog.d(TAG + exTag, "nextFocusItemView found");
        } else {
            MLog.e(TAG + exTag, "super focusSearch view not found ");
        }

        return view == null ? focused : view;
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        this.cacheHasFocus = this.hasFocus();
        Log.i(TAG + exTag, String.format("requestChildFocus nextchild=%s focused=%s hasFocus=%b"
                , child, focused, this.cacheHasFocus));
//        if (!this.hasFocus()) {
        if (!this.cacheHasFocus) { // 由于逻辑修改，注释掉上一句，使用这句代码，避免一个方法内重复调用hasFocus
            // recyclerview 子view 重新获取焦点，调用移入焦点的事件监听
            if (this.mFocusGainListener != null) {
                this.mFocusGainListener.onFocusGain(child, focused);
            }
        }
        super.requestChildFocus(child, focused);//执行过super.requestChildFocus之后hasFocus会变成true
        this.mCurrentFocusPosition = getChildViewHolder(child).getAdapterPosition();
//        Log.i(TAG, "focusPos = " + mCurrentFocusPosition);
    }

    //实现焦点记忆的关键代码
    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        MLog.d(TAG + exTag, String.format("addFocusables mCurrentFocusPosition=%d this.hasFocus=%b",
                this.mCurrentFocusPosition, this.hasFocus()));
        View view = null;
        if (this.hasFocus() || this.mCurrentFocusPosition < 0 || (view =
                getLayoutManager() == null ? null : getLayoutManager().findViewByPosition(mCurrentFocusPosition)) == null) {
            MLog.d(TAG + exTag, String.format("addFocusables"));
            super.addFocusables(views, direction, focusableMode);
        } else if (view.isFocusable()) {
            MLog.d(TAG + exTag, "addFocusables add " + view);
            //将当前的view放到Focusable views列表中，再次移入焦点时会取到该view,实现焦点记忆功能
            views.add(view);
        } else {
            MLog.d(TAG + exTag, String.format("addFocusables 1"));
            super.addFocusables(views, direction, focusableMode);
        }
    }


    /**
     * 控制当前焦点最后绘制，防止焦点放大后被遮挡
     * 原顺序123456789，当4是focus时，绘制顺序变为123567894
     *
     * @param childCount
     * @param i
     * @return
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View focusedChild = getFocusedChild();
//        Log.i(TAG, "focusedChild =" + focusedChild);
        if (focusedChild == null) {
            return super.getChildDrawingOrder(childCount, i);
        } else {
            int index = indexOfChild(focusedChild);
//            Log.i(TAG, " index = " + index + ",i=" + i + ",count=" + childCount);
            if (i == childCount - 1) {
                return index;
            }
            if (i < index) {
                return i;
            }
            return i + 1;
        }
    }

    public void setFocusLostListener(FocusLostListener focusLostListener) {
        this.mFocusLostListener = focusLostListener;
    }

    public interface FocusLostListener {
        void onFocusLost(View lastFocusChild, int direction);
    }

    public void setGainFocusListener(FocusGainListener focusListener) {
        this.mFocusGainListener = focusListener;
    }

    public interface FocusGainListener {
        void onFocusGain(View child, View focued);
    }

    public void setTag(String exTag) {
        this.exTag = exTag;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        boolean hasFocus = this.hasFocus();
//        MLog.d(TAG + exTag, String.format("onLayout(%b-%b-%d,%d,%d,%d)",
//                changed, hasFocus, l, t, r, b));
//        int descendantFocusability = getDescendantFocusability();
//        if (r - l == 0) {
//            if (descendantFocusability != FOCUS_BLOCK_DESCENDANTS) {
//                lockFocus();
//            }
//        } else {
//            if (descendantFocusability == FOCUS_AFTER_DESCENDANTS) {
//                unlockFocus();
//            }
//        }
    }

    public void lockFocus() {
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
//        setFocusable(false);
//        setFocusableInTouchMode(false);
    }

    public void unlockFocus() {
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
//        setFocusable(true);
//        setFocusableInTouchMode(true);
    }

    public void setCanFocusOutVertical(boolean canFocusOutVertical) {
        this.mCanFocusOutVertical = canFocusOutVertical;
    }

    public void setCanFocusOutHorizontal(boolean canFocusOutHorizontal) {
        this.mCanFocusOutHorizontal = canFocusOutHorizontal;
    }

    public void setCacheHasFocus(boolean cacheHasFocus) {
        this.cacheHasFocus = cacheHasFocus;
    }
}
