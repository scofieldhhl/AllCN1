package com.allcn.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.datas.EpgObj;
import com.allcn.R;
import com.allcn.activities.PlayBackMainAct;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.views.FocusKeepRecyclerView;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

public class PBLeftAdapter extends RecyclerView.Adapter<PBLeftAdapter.ViewHolder> {

    private static final String TAG = "PBLeftAdapter";
    private final PlayBackMainAct mContext;
    private final List<EpgObj> mPlaybackEpgs;
    private View.OnClickListener mOnClickListener;
    private View.OnFocusChangeListener mOnFocusChangeListener;
    private View.OnKeyListener mOnKeyListener;
    private OnRvAdapterListener onRvAdapterListener;

    private int marketPos, //当前标记的item位置
            exW, exH, freeY;
    private ViewHolder mViewHolder;

    private boolean needMarket;
    private boolean locked;
    private int curSelIndex;
    private FocusKeepRecyclerView rv;

    public PBLeftAdapter(PlayBackMainAct playBackMainAct, List<EpgObj> playBackEpgs) {
        mContext = playBackMainAct;
        mPlaybackEpgs = playBackEpgs;

        mOnFocusChangeListener = new MOnFocusChangeListener(this);
        mOnClickListener = new MOnClickListener(this);
        mOnKeyListener = new MOnKeyListener(this);

        exW = (int) AppMain.res().getDimension(R.dimen.playback_left_ex_width);
        exH = (int) AppMain.res().getDimension(R.dimen.playback_left_ex_height);
        freeY = (int) AppMain.res().getDimension(R.dimen.playback_left_free_y);
    }
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        rv = (FocusKeepRecyclerView) recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(AppMain.getView(R.layout.playback_left_item, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        EpgObj epgObj = mPlaybackEpgs.get(i);
        viewHolder.setText(String.valueOf(i + 1), epgObj.getName());
        viewHolder.initAdapterListener(this);
        if (needMarket) {
            if (i == marketPos) {
                needMarket = false;
                mViewHolder = viewHolder;
                marketForFocusVH(mViewHolder, false, true, true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPlaybackEpgs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView idV, nameV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idV = itemView.findViewById(R.id.playback_left_id_v);
            nameV = itemView.findViewById(R.id.playback_left_name_v);
        }

        public void setText(String id, String text) {
            idV.setText(id);
            if (text.contains("(粤语)")) text = text.replace("(粤语)", "");
            if (text.contains("(综合)")) text = text.replace("(综合)", "");
            nameV.setText(text);
            if (ExUtils.isOverFlowed(nameV)) {//判断内容是否超过可显示的宽度
                nameV.setMarqueeRepeatLimit(-1);
                nameV.setEllipsize(TextUtils.TruncateAt.MARQUEE);//设置跑马灯效果
            } else {
                nameV.setMarqueeRepeatLimit(0);
                nameV.setEllipsize(TextUtils.TruncateAt.END);
            }
        }

        public void scrollContent(boolean scroll) {
            nameV.setSelected(scroll);
        }

        public void initAdapterListener(PBLeftAdapter hostCls) {
            if (itemView != null) {
                itemView.setOnClickListener(hostCls.mOnClickListener);//设置item点击的监听器
                itemView.setOnFocusChangeListener(hostCls.mOnFocusChangeListener);//设置item焦点改变的监听器
                itemView.setOnKeyListener(hostCls.mOnKeyListener);
            }
        }

        public void marketForFocus(boolean hasFocus) {
            MLog.d(TAG, String.format("marketForFocus hasFocus=%b", hasFocus));
            nameV.setSelected(hasFocus);
            if (hasFocus) {
                itemView.setBackgroundResource(0);
            } else {
                itemView.setBackgroundResource(R.drawable.playback_left_item_sel_nof);
            }
            if (hasFocus) {
                Utils.focusV(itemView, true);
            }
        }
    }


    public void marketCacheForFocus(boolean hasFocus) {
        MLog.d(TAG, String.format("marketCacheForFocus hasFocus=%b", hasFocus));
        if (this.rv != null) {
            this.rv.setCanFocusOutVertical(!hasFocus);
            this.rv.setCanFocusOutHorizontal(!hasFocus);
        }
        this.marketForFocusVH(mViewHolder, hasFocus, false, !hasFocus);
    }
    public void marketForFocusVH(PBLeftAdapter.ViewHolder mvHolder, boolean hasFocus, boolean forceEffect,
                                 boolean ignore) {
        MLog.d(TAG, String.format("marketForFocusVH hasFocus=%b, forceEffect=%b ignore=%b",
                hasFocus, forceEffect, ignore));
        if (mvHolder != null) {
            mvHolder.marketForFocus(hasFocus);
            if (hasFocus || forceEffect) {
                execFocusVEffect(mvHolder.itemView, ignore);
            }
        }
    }

    public void execFocusVEffect(View v, boolean ignore) {
        MLog.d(TAG, String.format("execFocusVEffect v=%s ignore=%b", v, ignore));
        if (v != null && mContext != null) {
            if (ignore) {
                mContext.focusBorderForVIgnoreOldWHXY(v, exW, exH, 0, freeY);
//                this.act.focusBorderForVIgnoreOld(v, this.exW, this.exH);
            } else {
                mContext.focusBorderForVWHXY(v, exW, exH, 0, freeY);
//                this.act.focusBorderForV(v, this.exW, this.exH);
            }
        }
    }

    public void setSel(View view) {
        MLog.d(TAG, "setSel " + view);
        if (view == null) {
            this.curSelIndex = -1;
            this.mViewHolder= null;
        } else {
            this.curSelIndex = rv.getChildLayoutPosition(view);
            this.mViewHolder = (ViewHolder) rv.findViewHolderForAdapterPosition(this.curSelIndex);
        }
    }

    public void lock() {
        this.locked = true;
    }

    public void unLock() {
        this.locked = false;
    }


    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {//item焦点改变的监听器

        private PBLeftAdapter hostCls;

        public MOnFocusChangeListener(PBLeftAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            MLog.d(TAG, String.format("onFocusChange v=%s hasFocus=%b", v, hasFocus));
            if (hostCls == null || hostCls.locked) {
                return;
            }
            if (hasFocus) {
                if (hostCls != null) {
                    hostCls.setSel(v);
                    hostCls.execFocusVEffect(v, false);
                    if (hostCls.onRvAdapterListener != null) {
                        hostCls.onRvAdapterListener.onItemSelected(v, hostCls.curSelIndex);
                    }
                }
            }
            if (hostCls.mViewHolder != null) {
                hostCls.mViewHolder.scrollContent(hasFocus);
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {//item点击事件

        private PBLeftAdapter hostCls;

        public MOnClickListener(PBLeftAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (hostCls != null && hostCls.mContext!= null) {
                if (hostCls.locked) {
                    return;
                }
                hostCls.setSel(v);
                if (hostCls.onRvAdapterListener != null) {
                    hostCls.onRvAdapterListener.onItemClick(v, hostCls.curSelIndex);
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private PBLeftAdapter hostCls;

        public MOnKeyListener(PBLeftAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (hostCls != null) {
                    if (hostCls.locked) {
                        return false;
                    }
                    if (hostCls.onRvAdapterListener != null) {
                        hostCls.onRvAdapterListener.onItemKey(v,
                                hostCls.curSelIndex, event, keyCode);
                    }
                }
            }
            return false;
        }
    }

    public void setOnRvListener(OnRvAdapterListener onRvAdapterListener) {//设置item监听器
        this.onRvAdapterListener = onRvAdapterListener;
    }

    public void setMarketPos(int marketPos) {
        this.marketPos = marketPos;
        this.curSelIndex = marketPos;
    }
    public int getCurSelIndex(){
        return curSelIndex;
    }
    public void setNeedMarket(boolean needMarket) {
        this.needMarket = needMarket;
    }

}
