package com.allcn.adapters;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.datas.ChalDatsObj;
import com.allcn.R;
import com.allcn.activities.PlayBackMainAct;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.views.FocusKeepRecyclerView;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

public class PBChalAdapter extends RecyclerView.Adapter<PBChalAdapter.ViewHolder> {
    private static final String TAG = "PBChalAdapter";
    private final PlayBackMainAct mContext;

    private final int exW;
    private final int exH;

    private List<ChalDatsObj.DataBean.ProgramsBean> mProgramList;
    private ViewHolder mViewHolder;
    private boolean needMarket;//是否需要标记
    private int marketPos;
    private FocusKeepRecyclerView rv;

    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;

    private OnRvAdapterListener onRvAdapterListener;//item焦点变换的监听器

    private int curSelIndex;


    private int playId;


    public PBChalAdapter(PlayBackMainAct act, List<ChalDatsObj.DataBean.ProgramsBean> programList) {
        mContext = act;
        mProgramList = programList;

        mOnFocusChangeListener = new MOnFocusChangeListener(this);
        mOnClickListener = new MOnClickListener(this);
        mOnKeyListener = new MOnKeyListener(this);

        exW = (int) AppMain.res().getDimension(R.dimen.playback_right_ex_width);
        exH = (int) AppMain.res().getDimension(R.dimen.playback_right_ex_height);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(AppMain.getView(R.layout.playback_chal_item, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        synchronized (mLock) {
            MLog.d(TAG, String.format("onBindViewHolder needMarket=%b marketPos=%d i=%d", needMarket, marketPos, i));//
            ChalDatsObj.DataBean.ProgramsBean program = mProgramList.get(i);
            viewHolder.setText(program);
            viewHolder.initAdapterListener(this);
            if (program.getVideo().getId() == playId)
                viewHolder.imageView.setVisibility(View.VISIBLE);
            else viewHolder.imageView.setVisibility(View.GONE);
            //if (needMarket) {
            //    if (i == marketPos) {
            //        this.needMarket = false;
            //        mViewHolder = viewHolder;
            //        marketForFocusVH(viewHolder, false, true, true);
            //    }
            //}
        }
    }

    @Override
    public int getItemCount() {
        return mProgramList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        rv = (FocusKeepRecyclerView) recyclerView;
    }
    Object mLock = new Object();
    public void setDatas(List<ChalDatsObj.DataBean.ProgramsBean> programsBeans) {
        synchronized (mLock) {
            clearDatas();
            mProgramList.addAll(programsBeans);
            this.curSelIndex = 0;
            notifyDataSetChanged();
            MLog.d(TAG, "mProgramList " + mProgramList);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.program_name);
            imageView = itemView.findViewById(R.id.play_iv);
        }

        public void setText(ChalDatsObj.DataBean.ProgramsBean program) {
            TextView textView = itemView.findViewById(R.id.program_name);
            textView.setText(String.format("%s-%s     %s",
                    program.getBeginTime(), program.getEndTime(), program.getName()));
            if (ExUtils.isOverFlowed(textView)) {
                textView.setMarqueeRepeatLimit(-1);
                textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            } else {
                textView.setMarqueeRepeatLimit(0);
                textView.setEllipsize(TextUtils.TruncateAt.END);
            }
        }

        public void initAdapterListener(PBChalAdapter hostCls) {
            if (itemView != null) {
                itemView.setOnClickListener(hostCls.mOnClickListener);
                itemView.setOnFocusChangeListener(hostCls.mOnFocusChangeListener);
                itemView.setOnKeyListener(hostCls.mOnKeyListener);
            }
        }

        public void marketForFocus(boolean hasFocus) {
            MLog.d(TAG, String.format("marketForFocus hasFocus=%b", hasFocus));
            if (hasFocus) {
                Utils.focusV(itemView, true);
            }
        }
    }

    private void clearDatas() {
        if (mProgramList != null) {
            mProgramList.clear();
            //mProgramList = null;
        }
    }

    public void setSelection(int position) {
        MLog.d(TAG, String.format("setSelection %d", position));
        marketCacheForFocus(false);
        if (position >= getItemCount()) {
            position = 0;
        }
        mViewHolder = (PBChalAdapter.ViewHolder) rv.findViewHolderForAdapterPosition(position);
        if (mViewHolder == null) {
            rv.scrollToPosition(position);
            final int finalPosition = position;
            rv.post(new Runnable() {
                @Override
                public void run() {
                    mViewHolder = (PBChalAdapter.ViewHolder) rv.findViewHolderForAdapterPosition(finalPosition);

                    if (mViewHolder != null) {
                        marketForFocusVH(mViewHolder,
                                true, false, false);
                    }
                }
            });
        } else {
            marketForFocusVH(mViewHolder, true, false, false);
        }
    }

    public void setSel(View view) {
        MLog.d(TAG, "setSel " + view);
        if (view == null) {
            curSelIndex = -1;
            mViewHolder = null;
        } else {
            curSelIndex = this.rv.getChildLayoutPosition(view);
            mViewHolder = (ViewHolder) rv.findViewHolderForAdapterPosition(curSelIndex);
        }
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

    public void marketForFocusVH(PBChalAdapter.ViewHolder viewHolder, boolean hasFocus, boolean forceEffect,
                                 boolean ignore) {
        MLog.d(TAG, String.format("marketForFocusVH hasFocus=%b, forceEffect=%b ignore=%b",
                hasFocus, forceEffect, ignore));
        if (viewHolder != null) {
            viewHolder.marketForFocus(hasFocus);
            if (hasFocus || forceEffect) {
                this.execFocusVEffect(viewHolder.itemView, ignore);
            }
        }
    }

    public void marketCacheForFocus(boolean hasFocus) {
        MLog.d(TAG, String.format("marketCacheForFocus hasFocus=%b", hasFocus));
        if (rv != null) {
            rv.setCanFocusOutVertical(!hasFocus);
            rv.setCanFocusOutHorizontal(!hasFocus);
        }
        marketForFocusVH(mViewHolder, hasFocus, false, !hasFocus);
    }

    private void execFocusVEffect(View v, boolean ignore) {
        MLog.d(TAG, String.format("execFocusVEffect v=%s ignore=%b", v, ignore));
        if (v != null && mContext != null) {
            if (ignore) {
                mContext.focusBorderForVIgnoreOld(v, exW, exH);
            } else {
                mContext.focusBorderForV(v, exW, exH);
            }
        }
    }

    public void reset() {
        this.curSelIndex = -1;
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private PBChalAdapter hostCls;

        public MOnFocusChangeListener(PBChalAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            MLog.d(TAG, String.format("onFocusChange v=%s hasFocus=%b", v, hasFocus));
            if (hasFocus) {
                if (hostCls != null) {
                    hostCls.setSel(v);
                    hostCls.execFocusVEffect(v, false);
                    if (hostCls.onRvAdapterListener != null) {
                        hostCls.onRvAdapterListener.onItemSelected(v, hostCls.curSelIndex);
                    }
                }
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private PBChalAdapter hostCls;

        public MOnClickListener(PBChalAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (this.hostCls != null && this.hostCls.mContext != null) {
                hostCls.setSel(v);
                hostCls.playId = hostCls.mProgramList.get(hostCls.curSelIndex).getVideo().getId();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (hostCls.mViewHolder!=null){
                            hostCls.mViewHolder.imageView.setVisibility(View.VISIBLE);
                        }
                    }
                });
                if (this.hostCls.onRvAdapterListener != null) {
                    hostCls.onRvAdapterListener.onItemClick(v, this.hostCls.curSelIndex);

                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private PBChalAdapter hostCls;

        public MOnKeyListener(PBChalAdapter hostCls) {
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
    public void setPlayId(int playId) {
        this.playId = playId;
    }
}
