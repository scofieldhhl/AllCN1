package com.allcn.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.KindMovieAct;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.views.FocusKeepRecyclerView;
import com.datas.CKindObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

public class CKindAdapter extends RecyclerView.Adapter<CKindAdapter.MVHolder> {

    private static String TAG;
    private int size, marketPos, curSelIndex;
    private List<? extends Object> datas;
    private FocusKeepRecyclerView rv;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private MVHolder mvHolder;
    private boolean needMarket;
    private KindMovieAct activity;
    private OnRvAdapterListener onRvAdapterListener;
    private Object mLock = new Object();

    public CKindAdapter(KindMovieAct activity, String tag) {
        this.TAG = tag;
        this.activity = activity;
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);
    }

    public void reset() {
        this.size = 0;
        this.marketPos = 0;
        this.curSelIndex = 0;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (this.rv == null) {
            this.rv = (FocusKeepRecyclerView) recyclerView;
        }
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MVHolder(AppMain.getView(R.layout.pkind_item, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder mvHolder, int i) {
        MLog.d(TAG, "onBindViewHolder");
        Object data = datas.get(i);
        if (data instanceof String) {
            mvHolder.setContent(data.toString());
        } else if (data instanceof CKindObj) {
            mvHolder.setContent(((CKindObj) data).getName());
        }
        mvHolder.initAdapterListener(this);
        if (this.needMarket) {
            if (i == marketPos) {
                this.needMarket = false;
                this.mvHolder = mvHolder;
                marketForFocusVH(mvHolder, false);
            }
        } else {
            if (i == 0) {
                this.mvHolder = mvHolder;
            }
        }
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void setOnRvListener(OnRvAdapterListener onRvListener) {
        this.onRvAdapterListener = onRvListener;
    }

    private void clearDatas() {
        try {
            if (this.datas != null) {
                this.datas.clear();
                this.datas = null;
            }
        } catch (Exception e) {
        }
    }

    public void setDatas(List<? extends Object> datas) {
        MLog.d(TAG, "setDatas");
        clearDatas();
        needMarket = true;
        this.datas = datas;
        this.size = this.datas == null ? 0 : this.datas.size();
        if (this.mvHolder != null) {
            this.mvHolder.clearBg();
        }
        notifyDataSetChanged();
    }

    public void setSelection(int position) {
        MLog.d(TAG, String.format("setSelection %d", position));
        marketCacheForFocus(false, false);
        if (position >= getItemCount()) {
            position = 0;
        }
        mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(position);
        if (mvHolder == null) {
            rv.scrollToPosition(position);
            final int finalPosition = position;
            rv.post(new Runnable() {
                @Override
                public void run() {
                    mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(finalPosition);
                    if (mvHolder != null) {
                        marketForFocusVH(mvHolder, true);
                    }
                }
            });
        } else {
            marketForFocusVH(mvHolder, true);
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private CKindAdapter hostCls;

        public MOnFocusChangeListener(CKindAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (this.hostCls == null || hostCls.rv == null || (hostCls.rv.getWidth() == 0)) {
                return;
            }
            MLog.d(hostCls.TAG, String.format("onFocusChange v=%s hasFocus=%b", v, hasFocus));
            if (hasFocus) {
                this.hostCls.setSel(v);
                if (this.hostCls.activity != null) {
                    //this.hostCls.activity.focusBorderForV(v);
                    hostCls.activity.focusBorderForV(v);
                }
                if (this.hostCls.onRvAdapterListener != null) {
                    this.hostCls.onRvAdapterListener.onItemSelected(v, this.hostCls.curSelIndex);
                }
            }
            if (this.hostCls.mvHolder != null) {
                this.hostCls.mvHolder.scrollContent(hasFocus);
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private CKindAdapter hostCls;

        public MOnClickListener(CKindAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (hostCls != null) {
                this.hostCls.setSel(v);
                if (this.hostCls.onRvAdapterListener != null) {
                    this.hostCls.onRvAdapterListener.onItemClick(v, this.hostCls.curSelIndex);
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private CKindAdapter hostCls;

        public MOnKeyListener(CKindAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (hostCls == null) {
                return false;
            }
            MLog.d(hostCls.TAG, "onKey");
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                this.hostCls.setSel(v);
                if (this.hostCls.onRvAdapterListener != null) {
                    this.hostCls.onRvAdapterListener.onItemKey(v, this.hostCls.curSelIndex,
                            event, keyCode);
                }
            }
            return false;
        }
    }

    public void setSel(View view) {
        synchronized (mLock) {
            MLog.d(TAG, "setSel " + ((TextView) view).getText());
            if (view == null) {
                this.curSelIndex = 0;
                this.mvHolder = null;
            } else {
                this.curSelIndex = this.rv.getChildAdapterPosition(view);
                this.mvHolder = (MVHolder) this.rv.findViewHolderForAdapterPosition(this.curSelIndex);
            }
        }
    }

    public void setCurSelIndex(int curSelIndex) {
        this.curSelIndex = curSelIndex;
    }

    public int getCurSelIndex() {
        return this.curSelIndex;
    }

    public void marketForFocusVH(MVHolder mvHolder, boolean hasFocus) {
        if (mvHolder != null) {
            mvHolder.marketForFocus(hasFocus);
        }
    }

    public void marketCurSelFocusVH(boolean hasFocus) {
        this.marketForFocusVH(this.mvHolder, hasFocus);
    }


    public void marketCurSelFocusVH(int position, boolean hasFocus) {
        if (position >= getItemCount()) {
            position = 0;
        }
        mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(position);
        marketForFocusVH(this.mvHolder, hasFocus);
    }



    public void marketCacheForFocus(boolean hasFocus, boolean forceEffect) {
        if (this.rv != null) {
            this.rv.setCanFocusOutVertical(!hasFocus);
            this.rv.setCanFocusOutHorizontal(!hasFocus);
        }
        if (mvHolder != null) {
            MLog.d(TAG, "marketCacheForFocus " + ((TextView) mvHolder.itemView).getText());
        } else {
            return;
        }
        marketForFocusVH(mvHolder, hasFocus);
        if (forceEffect) {
            this.activity.focusBorderForV(mvHolder.itemView);
        }
    }

    public Object getDataForItem(int position) {
        return position >= 0 && position < size ? datas.get(position) : null;
    }

    protected static class MVHolder extends RecyclerView.ViewHolder {

        public MVHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void scrollContent(boolean scroll) {
            if (itemView != null) {
                itemView.setSelected(scroll);
            }
        }

        public void setContent(String content) {
            if (itemView != null) {
                ((TextView) itemView).setText(content);
            }
        }

        public void marketForFocus(boolean hasFocus) {
            MLog.d(TAG, String.format("marketForFocus hasFocus=%b", hasFocus));
            if (itemView != null) {
                itemView.setSelected(hasFocus);
                if (hasFocus) {
                    itemView.setBackgroundResource(0);
                    Utils.focusV(itemView, true);
                } else {
                    itemView.setBackgroundResource(R.drawable.kind_item_sel_nof);
                }
            }
        }

        public void initAdapterListener(CKindAdapter hostCls) {
            if (itemView != null) {
                itemView.setOnClickListener(hostCls.mOnClickListener);
                itemView.setOnFocusChangeListener(hostCls.mOnFocusChangeListener);
                itemView.setOnKeyListener(hostCls.mOnKeyListener);
            }
        }

        public void clearBg() {
            if (itemView != null) {
                itemView.setBackgroundResource(0);
            }
        }
    }

    public RecyclerView getRelateRv() {
        return this.rv;
    }

    public int getMarketPos() {
        return this.marketPos;
    }

    public void setMarketPos(int marketPos) {
        this.marketPos = marketPos;
        this.curSelIndex = marketPos;
    }

    public void setNeedMarket(boolean needMarket) {
        this.needMarket = needMarket;
    }

    public void release() {
        clearDatas();
        this.rv = null;
        this.mvHolder = null;
        this.onRvAdapterListener = null;
        if (this.mOnKeyListener != null) {
            this.mOnKeyListener.relase();
            this.mOnKeyListener = null;
        }
        if (this.mOnClickListener != null) {
            this.mOnClickListener.relase();
            this.mOnClickListener = null;
        }
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.relase();
            this.mOnFocusChangeListener = null;
        }
    }
}
