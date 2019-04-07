package com.allcn.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.datas.ChalDatsObj;
import com.allcn.R;
import com.allcn.activities.PlayBackMainAct;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.EXVAL;
import com.allcn.views.FocusKeepRecyclerView;
import com.mast.lib.utils.Utils;
import com.umeng.commonsdk.statistics.common.MLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PBWeekAdapter extends RecyclerView.Adapter<PBWeekAdapter.ViewHolder> {

    private static final String TAG = "PBWeekAdapter";
    private PlayBackMainAct mContext;
    private List<ChalDatsObj.DataBean> mDatas;
    private final SimpleDateFormat dateSdf;
    private final SimpleDateFormat weekSdf;

    private final MOnFocusChangeListener mOnFocusChangeListener;//星期item的焦点改变监听器
    private final MOnClickListener mOnClickListener;//星期的item的点击事件监听器
    private final MOnKeyListener mOnKeyListener;

    private ViewHolder mViewHolder;
    private final int textSmallSize;//字体的最小字号
    private final int textBigSize;//字体的最大字号
    private boolean needMarket;
    private int marketPos;
    private FocusKeepRecyclerView rv;

    private OnRvAdapterListener mOnRvAdapterListener;
    private int curSelIndex;

    public PBWeekAdapter(PlayBackMainAct act, List<ChalDatsObj.DataBean> datas) {

        textSmallSize = (int) AppMain.res().getDimension(R.dimen.playback_week_text_small_size);
        textBigSize = (int) AppMain.res().getDimension(R.dimen.playback_week_text_big_size);
        dateSdf = new SimpleDateFormat("yyyy-MM-dd");
        weekSdf = new SimpleDateFormat(AppMain.res().getString(R.string.playback_week_fmt));
        mContext = act;
        mDatas = datas;

        mOnFocusChangeListener = new MOnFocusChangeListener(this);
        mOnClickListener = new MOnClickListener(this);
        mOnKeyListener = new MOnKeyListener(this);
    }


    @NonNull
    @Override
    public PBWeekAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PBWeekAdapter.ViewHolder(AppMain.getView(R.layout.playback_week_item, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull PBWeekAdapter.ViewHolder mvHolder, int i) {
        ChalDatsObj.DataBean dateStr = mDatas.get(i);//倒序
        Date date = null;
        try {
            date = this.dateSdf.parse(dateStr.getName());
        } catch (ParseException e) {
        }
        mvHolder.setText(this.weekSdf.format(date), this.textSmallSize, this.textBigSize);
        mvHolder.initAdapterListener(this);
        if (mContext.focusShowState != PlayBackMainAct.FOCUS_SHOW_WEEK)
            if (i == marketPos){
                mvHolder.itemView.setBackgroundResource(R.drawable.play_title_sel_no_focus);
            }
        //if (needMarket) {
        //    if (i == marketPos) {
        //        needMarket = false;
        //        mViewHolder = mvHolder;
        //        marketForFocusVH(mvHolder, false, true, true);
        //    }
        //} else {
        //    if (i == 0) {
        //        mViewHolder = mvHolder;
        //    }
        //}
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.rv = (FocusKeepRecyclerView) recyclerView;
    }

    public void reset() {
        //this.size = 0;
        this.marketPos = 1;
        this.curSelIndex = 1;
    }

    public void setDatas(List<ChalDatsObj.DataBean> datas) {
        mDatas = datas;
        //this.size = this.datas == null ? 0 : this.datas.size();
        setInitPosition();
        this.curSelIndex = 0;
        notifyDataSetChanged();
        if (mContext.focusShowState != PlayBackMainAct.FOCUS_SHOW_WEEK&&mViewHolder!=null){
            mViewHolder.itemView.setBackgroundResource(R.drawable.play_title_sel_no_focus);
        }
    }

    public void release() {
        mContext = null;
        rv = null;
        mDatas= null;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setText(String text, int textSmallSize, int textBigSize) {
            SpannableString ss = new SpannableString(text);
            int index = text.indexOf("\n");
            ss.setSpan(new AbsoluteSizeSpan(textSmallSize), 0, index,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            ss.setSpan(new AbsoluteSizeSpan(textBigSize), index + 1, text.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            ((TextView) itemView).setText(ss);
        }

        public void marketForFocus(boolean hasFocus, RecyclerView rv) {
//            MLog.d(TAG, String.format("marketForFocus hasFocus=%b", hasFocus));
            if (this.itemView != null) {
                this.itemView.setActivated(!hasFocus);
                if (hasFocus) {
                    Utils.focusV(this.itemView, true);
                }
            }
        }

        public void clearFocus(PBWeekAdapter hostCls) {
            if (itemView != null) {
                itemView.setSelected(false);
                itemView.setActivated(false);
            }
            if (hostCls != null) {
                itemView.setBackgroundResource(R.drawable.playback_title_bg);
                hostCls.execFocusVEffect(itemView, true, 1.0f, 1.0f);
            }
        }

        public void initAdapterListener(PBWeekAdapter hostCls) {
            if (itemView != null) {
                itemView.setOnClickListener(hostCls.mOnClickListener);
                itemView.setOnFocusChangeListener(hostCls.mOnFocusChangeListener);
                itemView.setOnKeyListener(hostCls.mOnKeyListener);
            }
        }
    }

    public void setSelection(int position) {
        MLog.d(TAG, String.format("setSelection %d", position));
        marketCacheForFocus(false);
        if (position >= mDatas.size()) {
            position = 0;
        }
        setBackResource(R.drawable.playback_title_bg);

        this.mViewHolder = (PBWeekAdapter.ViewHolder) this.rv.findViewHolderForAdapterPosition(position);
        if (this.mViewHolder == null) {
            this.rv.scrollToPosition(position);
            final int finalPosition = position;
            this.rv.post(new Runnable() {
                @Override
                public void run() {
                    mViewHolder = (PBWeekAdapter.ViewHolder) rv.findViewHolderForAdapterPosition(finalPosition);
                    if (mViewHolder != null) {
                        marketForFocusVH(mViewHolder, true, false, false);
                    }
                }
            });
        } else {
            marketForFocusVH(mViewHolder, true, false, false);
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private PBWeekAdapter hostCls;

        public MOnFocusChangeListener(PBWeekAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hostCls == null || hostCls.rv == null || !hostCls.rv.hasFocus()) {
                return;
            }
            com.mast.lib.utils.MLog.d(hostCls.TAG, String.format("onFocusChange v=%s hasFocus=%b", v, hasFocus));
            if (hasFocus) {
                hostCls.setSel(v);
                hostCls.execFocusVEffect(v, false, EXVAL.SEDM_MOVIE_ITEM_SCALE,
                        EXVAL.SEDM_MOVIE_ITEM_SCALE);
                if (hostCls.mOnRvAdapterListener != null) {
                    hostCls.mOnRvAdapterListener.onItemSelected(v, hostCls.curSelIndex);
                }
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private PBWeekAdapter hostCls;

        public MOnClickListener(PBWeekAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (hostCls != null) {
                this.hostCls.setSel(v);
                if (this.hostCls.mOnRvAdapterListener != null) {
                    this.hostCls.mOnRvAdapterListener.onItemClick(v, this.hostCls.curSelIndex);
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private PBWeekAdapter hostCls;

        public MOnKeyListener(PBWeekAdapter hostCls) {
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
//            MLog.d(hostCls.TAG, "onKey");
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                this.hostCls.setSel(v);
                if (this.hostCls.mOnRvAdapterListener != null) {
                    this.hostCls.mOnRvAdapterListener.onItemKey(v, this.hostCls.curSelIndex,
                            event, keyCode);
                }
            }
            return false;
        }
    }

    public void setSel(View view) {
        com.mast.lib.utils.MLog.d(TAG, "setSel " + view);
        if (view == null) {
            curSelIndex = 0;
            mViewHolder = null;
        } else {
            curSelIndex = this.rv.getChildAdapterPosition(view);
            mViewHolder = (PBWeekAdapter.ViewHolder) this.rv.findViewHolderForAdapterPosition(curSelIndex);
        }
    }

    public int getCurSelIndex() {
        return curSelIndex;
    }

    public void marketForFocusVH(PBWeekAdapter.ViewHolder mvHolder, boolean hasFocus, boolean forceEffect,
                                 boolean ignore) {
        if (mvHolder != null) {
            mvHolder.marketForFocus(hasFocus, this.rv);
            if (hasFocus || forceEffect) {
                //execFocusVEffect(mvHolder.itemView, ignore, EXVAL.SEDM_MOVIE_ITEM_SCALE,
                //        EXVAL.SEDM_MOVIE_ITEM_SCALE);
                execFocusVEffect(mvHolder.itemView, ignore, 1.3f,
                        1.3f);
            }
        }
    }

    public void marketCacheForFocus(boolean hasFocus) {//true为有交点的标记
        if (this.rv != null) {
            this.rv.setCanFocusOutVertical(!hasFocus);
            this.rv.setCanFocusOutHorizontal(!hasFocus);
        }
        this.marketForFocusVH(mViewHolder, hasFocus, false, !hasFocus);
    }

    public void clearCacheFocus() {//
        if (this.mViewHolder != null) {
            this.mViewHolder.clearFocus(this);
        }
    }

    public ChalDatsObj.DataBean getDataForItem(int position) {
        return position >= 0 && position < mDatas.size() ? mDatas.get(position) : null;
    }

    public void setMarketPos(int marketPos) {
        setInitPosition();
        this.marketPos = marketPos;
        this.curSelIndex = marketPos;
    }

    public void setNeedMarket(boolean needMarket) {
        this.needMarket = needMarket;
    }
    public boolean getNeedMarket() {
        return needMarket;
    }

    private void execFocusVEffect(View v, boolean ignore, float scaleX, float scaleY) {
        MLog.d(TAG, String.format("execFocusVEffect v=%s ignore=%b", v, ignore));
        if (v != null && mContext != null) {
            if (ignore) {

                //v.setBackgroundResource(R.drawable.play_title_sel_no_focus);
                mContext.focusBorderForVIgnoreOld(v, scaleX, scaleY);
            } else {
                //v.setBackgroundResource(R.drawable.playback_title_bg);
                mContext.focusBorderForV(v, scaleX, scaleY);
            }
        }
    }

    public void setOnRvListener(OnRvAdapterListener onRvListener) {
        mOnRvAdapterListener = onRvListener;
    }

    public void setBackResource(int resource){
        if (mViewHolder!=null){
            mViewHolder.itemView.setBackgroundResource(resource);
        }
    }

    public void setInitPosition() {
        if (curSelIndex!=0&&mViewHolder!=null) {
            mViewHolder.itemView.setBackgroundResource(R.drawable.playback_title_bg);
            clearCacheFocus();
        }
    }
}
