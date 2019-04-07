package com.allcn.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.PlayBackAct;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.views.FocusKeepRecyclerView;
import com.datas.ChalObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

public class PlayBackChalAdapter extends RecyclerView.Adapter<PlayBackChalAdapter.MVHolder> {

    private static final String TAG = PlayBackChalAdapter.class.getSimpleName();
    private List<ChalObj> datas;
    private int size, curSelIndex, exW, exH, marketPos;
    private PlayBackAct act;
    private FocusKeepRecyclerView rv;
    private MVHolder mvHolder;
    private OnRvAdapterListener onRvAdapterListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private boolean needMarket;

    public PlayBackChalAdapter(PlayBackAct act) {
        this.act = act;
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);
        this.exW = (int) AppMain.res().getDimension(R.dimen.playback_right_ex_width);
        this.exH = (int) AppMain.res().getDimension(R.dimen.playback_right_ex_height);
        this.reset();
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MVHolder(AppMain.getView(R.layout.playback_chal_item, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder mvHolder, int i) {
//        MLog.d(TAG, String.format("onBindViewHolder needMarket=%b marketPos=%d i=%d",
//                this.needMarket, this.marketPos, i));
        ChalObj chalObj = datas.get(i);
        mvHolder.setText(chalObj);
        mvHolder.initAdapterListener(this);
        if (this.needMarket) {
            if (i == this.marketPos) {
                this.needMarket = false;
                this.mvHolder = mvHolder;
                marketForFocusVH(mvHolder, false, true, true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.size;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.rv = (FocusKeepRecyclerView) recyclerView;
    }

    public void setDatas(List<ChalObj> datas) {
        clearDatas();
        this.datas = datas;
//        MLog.d(TAG, "setDatas " + this.datas);
        this.size = this.datas == null ? 0 : this.datas.size();
        this.curSelIndex = 0;
        notifyDataSetChanged();
    }

    public void release() {
        this.clearDatas();
        this.act = null;
        this.rv = null;
        this.mvHolder = null;
        this.onRvAdapterListener = null;
        if (this.mOnClickListener != null) {
            this.mOnClickListener.relase();
            this.mOnClickListener = null;
        }
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.relase();
            this.mOnFocusChangeListener = null;
        }
        if (this.mOnKeyListener != null) {
            this.mOnKeyListener.relase();
            this.mOnKeyListener = null;
        }
    }

    public static class MVHolder extends RecyclerView.ViewHolder {

        public MVHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setText(ChalObj chalObj) {
            TextView textView = (TextView) this.itemView;
            textView.setText(String.format("%s-%s     %s",
                    chalObj.getBeginTime(), chalObj.getEndTime(), chalObj.getName()));
            if (ExUtils.isOverFlowed(textView)) {
                textView.setMarqueeRepeatLimit(-1);
                textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            } else {
                textView.setMarqueeRepeatLimit(0);
                textView.setEllipsize(TextUtils.TruncateAt.END);
            }
        }

        public void marketForFocus(boolean hasFocus) {
            MLog.d(TAG, String.format("marketForFocus hasFocus=%b", hasFocus));
            if (hasFocus) {
                Utils.focusV(itemView, true);
            }
        }

        public void initAdapterListener(PlayBackChalAdapter hostCls) {
            if (this.itemView != null) {
                this.itemView.setOnClickListener(hostCls.mOnClickListener);
                this.itemView.setOnFocusChangeListener(hostCls.mOnFocusChangeListener);
                this.itemView.setOnKeyListener(hostCls.mOnKeyListener);
            }
        }
    }

    private void clearDatas() {
        if (this.datas != null) {
            this.datas.clear();
            this.datas = null;
        }
    }

    public void setSelection(int position) {
        MLog.d(TAG, String.format("setSelection %d", position));
        marketCacheForFocus(false);
        if (position >= getItemCount()) {
            position = 0;
        }
        this.mvHolder = (MVHolder) this.rv.findViewHolderForAdapterPosition(position);
        if (this.mvHolder == null) {
            this.rv.scrollToPosition(position);
            final int finalPosition = position;
            this.rv.post(new Runnable() {
                @Override
                public void run() {
                    PlayBackChalAdapter.this.mvHolder = (MVHolder)
                            PlayBackChalAdapter.this.rv.findViewHolderForAdapterPosition(finalPosition);
                    if (PlayBackChalAdapter.this.mvHolder != null) {
                        marketForFocusVH(PlayBackChalAdapter.this.mvHolder,
                                true, false, false);
                    }
                }
            });
        } else {
            marketForFocusVH(this.mvHolder, true, false, false);
        }
    }

    public void setSel(View view) {
        MLog.d(TAG, "setSel " + view);
        if (view == null) {
            this.curSelIndex = -1;
            this.mvHolder = null;
        } else {
            this.curSelIndex = this.rv.getChildLayoutPosition(view);
            this.mvHolder = (MVHolder) this.rv.findViewHolderForAdapterPosition(this.curSelIndex);
        }
    }

    public ChalObj getDataForItem(int position) {
        return position >= 0 && position < this.size ? this.datas.get(position) : null;
    }

    public void setOnRvListener(OnRvAdapterListener onRvAdapterListener) {
        this.onRvAdapterListener = onRvAdapterListener;
    }

    public void setCurSelIndex(int curSelIndex) {
        this.curSelIndex = curSelIndex;
    }

    public int getCurSelIndex() {
        return this.curSelIndex;
    }

    public void marketForFocusVH(MVHolder mvHolder, boolean hasFocus, boolean forceEffect,
                                 boolean ignore) {
        MLog.d(TAG, String.format("marketForFocusVH hasFocus=%b, forceEffect=%b ignore=%b",
                hasFocus, forceEffect, ignore));
        if (mvHolder != null) {
            mvHolder.marketForFocus(hasFocus);
            if (hasFocus || forceEffect) {
                this.execFocusVEffect(mvHolder.itemView, ignore);
            }
        }
    }

    public void marketCacheForFocus(boolean hasFocus) {
        MLog.d(TAG, String.format("marketCacheForFocus hasFocus=%b", hasFocus));
        if (this.rv != null) {
            this.rv.setCanFocusOutVertical(!hasFocus);
            this.rv.setCanFocusOutHorizontal(!hasFocus);
        }
        this.marketForFocusVH(mvHolder, hasFocus, false, !hasFocus);
    }

    private void execFocusVEffect(View v, boolean ignore) {
        MLog.d(TAG, String.format("execFocusVEffect v=%s ignore=%b", v, ignore));
        if (v != null && this.act != null) {
            if (ignore) {
                this.act.focusBorderForVIgnoreOld(v, exW, exH);
            } else {
                this.act.focusBorderForV(v, exW, exH);
            }
        }
    }

    public void reset() {
        this.curSelIndex = -1;
        this.size = 0;
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private PlayBackChalAdapter hostCls;

        public MOnFocusChangeListener(PlayBackChalAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            MLog.d(TAG, String.format("onFocusChange v=%s hasFocus=%b", v, hasFocus));
            if (hasFocus) {
                if (this.hostCls != null) {
                    this.hostCls.setSel(v);
                    this.hostCls.execFocusVEffect(v, false);
                    if (this.hostCls.onRvAdapterListener != null) {
                        this.hostCls.onRvAdapterListener.onItemSelected(v, this.hostCls.curSelIndex);
                    }
                }
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private PlayBackChalAdapter hostCls;

        public MOnClickListener(PlayBackChalAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (this.hostCls != null && this.hostCls.act != null) {
                this.hostCls.setSel(v);
                if (this.hostCls.onRvAdapterListener != null) {
                    this.hostCls.onRvAdapterListener.onItemClick(v, this.hostCls.curSelIndex);
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private PlayBackChalAdapter hostCls;

        public MOnKeyListener(PlayBackChalAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (this.hostCls != null) {
                    if (this.hostCls.onRvAdapterListener != null) {
                        this.hostCls.onRvAdapterListener.onItemKey(v,
                                this.hostCls.curSelIndex, event, keyCode);
                    }
                }
            }
            return false;
        }
    }

    public void setMarketPos(int marketPos) {
        this.marketPos = marketPos;
        this.curSelIndex = marketPos;
    }

    public void setNeedMarket(boolean needMarket) {
        this.needMarket = needMarket;
    }
}
