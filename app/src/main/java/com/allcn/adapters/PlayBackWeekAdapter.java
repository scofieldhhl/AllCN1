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

import com.allcn.R;
import com.allcn.activities.PlayBackAct;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.EXVAL;
import com.allcn.views.FocusKeepRecyclerView;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PlayBackWeekAdapter extends RecyclerView.Adapter<PlayBackWeekAdapter.MVHolder> {

    private static final String TAG = PlayBackWeekAdapter.class.getSimpleName();
    private List<String> datas;
    private int size, textSmallSize, textBigSize, curSelIndex, marketPos;
    private PlayBackAct act;
    private FocusKeepRecyclerView rv;
    private SimpleDateFormat dateSdf;
    private SimpleDateFormat weekSdf;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private MVHolder mvHolder;
    private boolean needMarket;
    private OnRvAdapterListener onRvAdapterListener;

    public PlayBackWeekAdapter(PlayBackAct act) {
        this.act = act;
        this.textSmallSize = (int) AppMain.res().getDimension(R.dimen.playback_week_text_small_size);
        this.textBigSize = (int) AppMain.res().getDimension(R.dimen.playback_week_text_big_size);
        this.dateSdf = new SimpleDateFormat("yyyy-MM-dd");
        this.weekSdf = new SimpleDateFormat(AppMain.res().getString(R.string.playback_week_fmt));
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);
        this.reset();
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MVHolder(AppMain.getView(R.layout.playback_week_item, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder mvHolder, int i) {
        String dateStr = datas.get(i);
        Date date = null;
        try {
            date = this.dateSdf.parse(dateStr);
        } catch (ParseException e) {
        }
        mvHolder.setText(this.weekSdf.format(date), this.textSmallSize, this.textBigSize);
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
        return this.size;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.rv = (FocusKeepRecyclerView) recyclerView;
    }

    public void reset() {
        this.size = 0;
        this.marketPos = -1;
        this.curSelIndex = -1;
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
        this.size = this.datas == null ? 0 : this.datas.size();
        this.curSelIndex = 0;
        notifyDataSetChanged();
    }

    public void release() {
        this.act = null;
        this.rv = null;
        this.datas = null;
    }

    public static class MVHolder extends RecyclerView.ViewHolder {

        public MVHolder(@NonNull View itemView) {
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

        public void clearFocus(PlayBackWeekAdapter hostCls) {
            if (itemView != null) {
                itemView.setSelected(false);
                itemView.setActivated(false);
            }
            if (hostCls != null) {
                hostCls.execFocusVEffect(itemView, true, 1.0f, 1.0f);
            }
        }

        public void initAdapterListener(PlayBackWeekAdapter hostCls) {
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
        if (position >= this.size) {
            position = 0;
        }
        this.mvHolder = (MVHolder) this.rv.findViewHolderForAdapterPosition(position);
        if (this.mvHolder == null) {
            this.rv.scrollToPosition(position);
            final int finalPosition = position;
            this.rv.post(new Runnable() {
                @Override
                public void run() {
                    mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(finalPosition);
                    if (mvHolder != null) {
                        marketForFocusVH(mvHolder, true, false, false);
                    }
                }
            });
        } else {
            marketForFocusVH(this.mvHolder, true, false, false);
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private PlayBackWeekAdapter hostCls;

        public MOnFocusChangeListener(PlayBackWeekAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (this.hostCls == null || hostCls.rv == null || !hostCls.rv.hasFocus()) {
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
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private PlayBackWeekAdapter hostCls;

        public MOnClickListener(PlayBackWeekAdapter hostCls) {
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

        private PlayBackWeekAdapter hostCls;

        public MOnKeyListener(PlayBackWeekAdapter hostCls) {
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
                if (this.hostCls.onRvAdapterListener != null) {
                    this.hostCls.onRvAdapterListener.onItemKey(v, this.hostCls.curSelIndex,
                            event, keyCode);
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
            this.curSelIndex = this.rv.getChildAdapterPosition(view);
            this.mvHolder = (MVHolder) this.rv.findViewHolderForAdapterPosition(this.curSelIndex);
        }
    }

    public int getCurSelIndex() {
        return this.curSelIndex;
    }

    public void marketForFocusVH(MVHolder mvHolder, boolean hasFocus, boolean forceEffect,
                                 boolean ignore) {
        if (mvHolder != null) {
            mvHolder.marketForFocus(hasFocus, this.rv);
            if (hasFocus || forceEffect) {
                this.execFocusVEffect(mvHolder.itemView, ignore, EXVAL.SEDM_MOVIE_ITEM_SCALE,
                        EXVAL.SEDM_MOVIE_ITEM_SCALE);
            }
        }
    }

    public void marketCacheForFocus(boolean hasFocus) {
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

    public String getDataForItem(int position) {
        return position >= 0 && position < this.size ? this.datas.get(position) : null;
    }

    public void setMarketPos(int marketPos) {
        this.marketPos = marketPos;
        this.curSelIndex = marketPos;
    }

    public void setNeedMarket(boolean needMarket) {
        this.needMarket = needMarket;
    }

    private void execFocusVEffect(View v, boolean ignore, float scaleX, float scaleY) {
        MLog.d(TAG, String.format("execFocusVEffect v=%s ignore=%b", v, ignore));
        if (v != null && this.act != null) {
            if (ignore) {
                this.act.focusBorderForVIgnoreOld(v, scaleX, scaleY);
            } else {
                this.act.focusBorderForV(v, scaleX, scaleY);
            }
        }
    }

    public void setOnRvListener(OnRvAdapterListener onRvListener) {
        this.onRvAdapterListener = onRvListener;
    }
}
