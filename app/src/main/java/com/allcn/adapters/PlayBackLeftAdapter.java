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
import com.datas.EpgObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

public class PlayBackLeftAdapter extends RecyclerView.Adapter<PlayBackLeftAdapter.MVHolder> {

    private static final String TAG = PlayBackLeftAdapter.class.getSimpleName();
    private int size, curSelIndex, exW, exH, marketPos, freeY, yyEpgNum, zhEpgNum, vId;
    private List<EpgObj> datas;
    private List<EpgObj> yyDatas, zhDatas;
    private OnRvAdapterListener onRvAdapterListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private FocusKeepRecyclerView rv;
    private PlayBackAct act;
    private MVHolder mvHolder;
    private boolean needMarket;

    public PlayBackLeftAdapter(PlayBackAct act) {
        this.vId = -1;
        this.act = act;
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);
        this.exW = (int) AppMain.res().getDimension(R.dimen.playback_left_ex_width);
        this.exH = (int) AppMain.res().getDimension(R.dimen.playback_left_ex_height);
        this.freeY = (int) AppMain.res().getDimension(R.dimen.playback_left_free_y);
        this.reset();
    }

    public void release() {
        this.rv = null;
        this.act = null;
        this.mvHolder = null;
        this.onRvAdapterListener = null;
        this.datas = null;
        if (this.yyDatas != null) {
            this.yyDatas.clear();
            this.yyDatas = null;
        }
        if (this.zhDatas != null) {
            this.zhDatas.clear();
            this.zhDatas = null;
        }
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

    public void reset() {
        this.marketPos = -1;
        this.curSelIndex = -1;
        this.size = 0;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.rv = (FocusKeepRecyclerView) recyclerView;
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MVHolder(AppMain.getView(R.layout.playback_left_item, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder mvHolder, int i) {
        MLog.d(TAG, String.format("onBindViewHolder needMarket=%b marketPos=%d i=%d",
                this.needMarket, this.marketPos, i));
        EpgObj epgObj = datas.get(i);
        mvHolder.setText(String.valueOf(i + 1), epgObj.getName());
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

    public void changeEpgList(int vId) {
        if (this.vId == vId) {
            return;
        }
        this.vId = vId;
        this.datas = vId == R.id.playback_yy_title_v ? yyDatas : zhDatas;
        this.size = this.datas == null ? 0 : this.datas.size();
        this.curSelIndex = 0;
        if (this.size > 0) {
            this.act.changeRelateDatasForEpg(this.datas.get(0));
        }
        notifyDataSetChanged();
    }

    public void setSelection(int position) {
//        MLog.d(TAG, String.format("setSelection %d", position));
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
                    PlayBackLeftAdapter.this.mvHolder = (MVHolder)
                            PlayBackLeftAdapter.this.rv.findViewHolderForAdapterPosition(finalPosition);
                    if (PlayBackLeftAdapter.this.mvHolder != null) {
                        marketForFocusVH(PlayBackLeftAdapter.this.mvHolder,
                                true, false, false);
                    }
                }
            });
        } else {
            marketForFocusVH(this.mvHolder, true, false, false);
        }
    }

    public void initDatas(List<EpgObj> yyEpgObjs, int yyEpgNum, List<EpgObj> zhEpgObjs,
                          int zhEpgNum) {
        this.yyDatas = yyEpgObjs;
        this.yyEpgNum = yyEpgNum;
        this.zhDatas = zhEpgObjs;
        this.zhEpgNum = zhEpgNum;
    }

    public static class MVHolder extends RecyclerView.ViewHolder {

        private TextView idV, nameV;

        public MVHolder(@NonNull View itemView) {
            super(itemView);
            this.idV = itemView.findViewById(R.id.playback_left_id_v);
            this.nameV = itemView.findViewById(R.id.playback_left_name_v);
        }

        public void setText(String id, String text) {
            idV.setText(id);
            nameV.setText(text);
            if (ExUtils.isOverFlowed(nameV)) {
                nameV.setMarqueeRepeatLimit(-1);
                nameV.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            } else {
                nameV.setMarqueeRepeatLimit(0);
                nameV.setEllipsize(TextUtils.TruncateAt.END);
            }
        }

        public void scrollContent(boolean scroll) {
            this.nameV.setSelected(scroll);
        }

        public void marketForFocus(boolean hasFocus) {
            MLog.d(TAG, String.format("marketForFocus hasFocus=%b", hasFocus));
            nameV.setSelected(hasFocus);
            if (hasFocus) {
                this.itemView.setBackgroundResource(0);
            } else {
                this.itemView.setBackgroundResource(R.drawable.playback_left_item_sel_nof);
            }
            if (hasFocus) {
                Utils.focusV(itemView, true);
            }
        }

        public void clearFocus(PlayBackLeftAdapter hostCls) {
            nameV.setSelected(false);
            if (hostCls != null) {
                hostCls.execFocusVEffect(itemView, true);
            }
        }

        public void initAdapterListener(PlayBackLeftAdapter hostCls) {
            if (this.itemView != null) {
                this.itemView.setOnClickListener(hostCls.mOnClickListener);
                this.itemView.setOnFocusChangeListener(hostCls.mOnFocusChangeListener);
                this.itemView.setOnKeyListener(hostCls.mOnKeyListener);
            }
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private PlayBackLeftAdapter hostCls;

        public MOnFocusChangeListener(PlayBackLeftAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
//            MLog.d(TAG, String.format("onFocusChange v=%s hasFocus=%b", v, hasFocus));
            if (this.hostCls == null || this.hostCls.locked) {
                return;
            }
            if (hasFocus) {
                if (this.hostCls != null) {
                    this.hostCls.setSel(v);
                    this.hostCls.execFocusVEffect(v, false);
                    if (this.hostCls.onRvAdapterListener != null) {
                        this.hostCls.onRvAdapterListener.onItemSelected(v, this.hostCls.curSelIndex);
                    }
                }
            }
            if (this.hostCls.mvHolder != null) {
                this.hostCls.mvHolder.scrollContent(hasFocus);
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private PlayBackLeftAdapter hostCls;

        public MOnClickListener(PlayBackLeftAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (this.hostCls != null && this.hostCls.act != null) {
                if (this.hostCls.locked) {
                    return;
                }
                this.hostCls.setSel(v);
                if (this.hostCls.onRvAdapterListener != null) {
                    this.hostCls.onRvAdapterListener.onItemClick(v, this.hostCls.curSelIndex);
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private PlayBackLeftAdapter hostCls;

        public MOnKeyListener(PlayBackLeftAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (this.hostCls != null) {
                    if (this.hostCls.locked) {
                        return false;
                    }
                    if (this.hostCls.onRvAdapterListener != null) {
                        this.hostCls.onRvAdapterListener.onItemKey(v,
                                this.hostCls.curSelIndex, event, keyCode);
                    }
                }
            }
            return false;
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

    public EpgObj getDataForItem(int position) {
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

    public void clearCacheFocus() {
        if (this.mvHolder != null) {
            this.mvHolder.clearFocus(this);
        }
    }

    private void execFocusVEffect(View v, boolean ignore) {
        MLog.d(TAG, String.format("execFocusVEffect v=%s ignore=%b", v, ignore));
        if (v != null && this.act != null) {
            if (ignore) {
                this.act.focusBorderForVIgnoreOldWHXY(v, this.exW, this.exH, 0, this.freeY);
//                this.act.focusBorderForVIgnoreOld(v, this.exW, this.exH);
            } else {
                this.act.focusBorderForVWHXY(v, this.exW, this.exH, 0, this.freeY);
//                this.act.focusBorderForV(v, this.exW, this.exH);
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

    private boolean locked;

    public void lock() {
        this.locked = true;
    }

    public void unLock() {
        this.locked = false;
    }
}
