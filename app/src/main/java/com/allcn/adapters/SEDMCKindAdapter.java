package com.allcn.adapters;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.SEDMKindListAct;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.EXVAL;
import com.allcn.views.FocusKeepRecyclerView;
import com.datas.CKindObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

public class SEDMCKindAdapter extends RecyclerView.Adapter<SEDMCKindAdapter.MVHolder> {

    private String TAG;
    private int size, marketPos, curSelIndex, cidNum;
    private List<? extends Object> datas;
    private FocusKeepRecyclerView rv;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private MVHolder mvHolder;
    private boolean needMarket;
    private SEDMKindListAct activity;
    private OnRvAdapterListener onRvAdapterListener;
    private String[] cids;

    public SEDMCKindAdapter(SEDMKindListAct activity, String tag) {
        this.TAG = tag;
        this.activity = activity;
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);
    }

    public void reset() {
        this.size = 0;
        this.marketPos = -1;
        this.curSelIndex = -1;
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
        return new MVHolder(AppMain.getView(R.layout.sedm_pkind_item, viewGroup));
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
//        mvHolder.setTypeFace(AppMain.getJdjquFont());
        mvHolder.initAdapterListener(this);
        if (this.needMarket) {
            if (i == marketPos) {
                this.needMarket = false;
                this.mvHolder = mvHolder;
                marketForFocusVH(mvHolder, false, true, true);
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
        this.datas = datas;
        this.size = this.datas == null ? 0 : this.datas.size();
        notifyDataSetChanged();
    }

    public void setDatas(List<? extends Object> datas, String cids) {
        MLog.d(TAG, "setDatas");
        clearDatas();
        this.datas = datas;
        if (TextUtils.isEmpty(cids)) {
            this.cids = null;
            this.cidNum = 0;
        } else {
            this.cids = cids.split(";");
            this.cidNum = this.cids.length;
        }
        this.size = this.datas == null ? 0 : this.datas.size();
        MLog.d(TAG, String.format("setDatas size=%d", this.size));
        notifyDataSetChanged();
    }

    public void setSelection(int position) {
        MLog.d(TAG, String.format("setSelection %d", position));
        marketCacheForFocus(false);
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
                        marketForFocusVH(mvHolder, true, false, false);
                    }
                }
            });
        } else {
            marketForFocusVH(mvHolder, true, false, false);
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private SEDMCKindAdapter hostCls;

        public MOnFocusChangeListener(SEDMCKindAdapter hostCls) {
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
                this.hostCls.execFocusVEffect(v, false, EXVAL.SEDM_MOVIE_ITEM_SCALE,
                        EXVAL.SEDM_MOVIE_ITEM_SCALE);
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

        private SEDMCKindAdapter hostCls;

        public MOnClickListener(SEDMCKindAdapter hostCls) {
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

        private SEDMCKindAdapter hostCls;

        public MOnKeyListener(SEDMCKindAdapter hostCls) {
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
        MLog.d(TAG, "setSel " + ((TextView) view).getText());
        if (view == null) {
            this.curSelIndex = -1;
            this.mvHolder = null;
        } else {
            this.curSelIndex = this.rv.getChildAdapterPosition(view);
            this.mvHolder = (MVHolder) this.rv.findViewHolderForAdapterPosition(this.curSelIndex);
        }
    }

    public void setCurSelIndex(int curSelIndex) {
        this.curSelIndex = curSelIndex;
    }

    public int getCurSelIndex() {
        return this.curSelIndex;
    }

    public void marketForFocusVH(MVHolder mvHolder, boolean hasFocus, boolean forceEffect,
                                 boolean ignore) {
        if (mvHolder != null) {
            mvHolder.marketForFocus(hasFocus);
            if (hasFocus || forceEffect) {
                float scale = hasFocus ? EXVAL.SEDM_MOVIE_ITEM_SCALE : 1.0f;
                this.execFocusVEffect(mvHolder.itemView, ignore, scale, scale);
            }
        }
    }

    public void marketCacheForFocus(boolean hasFocus) {
        if (this.rv != null) {
            this.rv.setCanFocusOutVertical(!hasFocus);
            this.rv.setCanFocusOutHorizontal(!hasFocus);
        }
        this.marketForFocusVH(mvHolder, hasFocus, true, !hasFocus);
    }

    public void clearCacheFocus() {
        if (this.mvHolder != null) {
            this.mvHolder.clearFocus(this);
        }
    }

    public Object getDataForItem(int position) {
        return position >= 0 && position < this.size ? this.datas.get(position) : null;
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
//            MLog.d(TAG, String.format("marketForFocus hasFocus=%b", hasFocus));
            if (itemView != null) {
                itemView.setSelected(hasFocus);
                itemView.setActivated(!hasFocus);
                if (hasFocus) {
                    Utils.focusV(itemView, true);
                }
            }
        }

        public void clearFocus(SEDMCKindAdapter hostCls) {
            if (itemView != null) {
                itemView.setSelected(false);
                itemView.setActivated(false);
            }
            if (hostCls != null) {
                hostCls.execFocusVEffect(itemView, true, 1.0f, 1.0f);
            }
        }

        public void setTypeFace(Typeface typeFace) {
            if (itemView != null && typeFace != null) {
                ((TextView) itemView).setTypeface(typeFace);
            }
        }

        public void initAdapterListener(SEDMCKindAdapter hostCls) {
            if (itemView != null) {
                itemView.setOnClickListener(hostCls.mOnClickListener);
                itemView.setOnFocusChangeListener(hostCls.mOnFocusChangeListener);
                itemView.setOnKeyListener(hostCls.mOnKeyListener);
            }
        }
    }

    public void setMarketPos(int marketPos) {
        this.marketPos = marketPos;
        this.curSelIndex = marketPos;
    }

    public void setNeedMarket(boolean needMarket) {
        this.needMarket = needMarket;
    }

    public String getCid(int position) {
        return this.cidNum == 0 || position >= this.cidNum || position < 0 ?
                "" : this.cids[position];
    }

    private void execFocusVEffect(View v, boolean ignore, float scaleX, float scaleY) {
        MLog.d(TAG, String.format("execFocusVEffect v=%s ignore=%b", v, ignore));
        if (v != null && this.activity != null) {
            if (ignore) {
                this.activity.focusBorderForVIgnoreOld(v, scaleX, scaleY);
            } else {
                this.activity.focusBorderForV(v, scaleX, scaleY);
            }
            v.bringToFront();
        }
    }

    public void release() {
        this.clearDatas();
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
