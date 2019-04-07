package com.allcn.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.LiveAct;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.EXVAL;
import com.allcn.views.HorChalListV;
import com.allcn.views.focus.FocusBorder;
import com.datas.LiveKindObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

public class KindsAdapter extends RecyclerView.Adapter<KindsAdapter.MVHolder> {

    private static final String TAG = KindsAdapter.class.getSimpleName();
    private LayoutInflater layoutInflater;
    private RecyclerView rv;
    private OnRvAdapterListener onAdapterItemListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private LiveAct act;
    private HorChalListV horChalListV;

    private String[] kinds = new String[]{
            "全部", "H265", "港澳", "大陆", "台湾", "国际1", "国际2", "越南", "体育", "少儿", "菲律宾"
    };
    private String[] kindsId = new String[]{
            "全部", "520", "228", "229", "231", "230", "521", "232", "234", "522", "501"
    };
    public KindsAdapter(LiveAct act, HorChalListV horChalListV) {
        this.act = act;
        this.horChalListV = horChalListV;
        layoutInflater = LayoutInflater.from(AppMain.ctx());
        mOnFocusChangeListener = new MOnFocusChangeListener(this);
        mOnClickListener = new MOnClickListener(this);
        mOnKeyListener = new MOnKeyListener(this);
    }

    public void setRecyclerView(RecyclerView rv) {
        this.rv = rv;
    }



    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MVHolder(layoutInflater.inflate(R.layout.live_kind_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        TextView textView = (TextView) holder.itemView;
        textView.setText(kinds[position]);
        holder.itemView.setOnFocusChangeListener(mOnFocusChangeListener);
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.itemView.setOnKeyListener(mOnKeyListener);
    }

    @Override
    public int getItemCount() {
        return kinds.length;
    }

    public void release() {
        kinds = null;
        rv = null;
        layoutInflater = null;
        onAdapterItemListener = null;
        act = null;
        horChalListV = null;
        if (mOnKeyListener != null) {
            mOnKeyListener.relase();
        }
        if (mOnClickListener != null) {
            mOnClickListener.relase();
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.relase();
        }
        mOnKeyListener = null;
        mOnClickListener = null;
        mOnFocusChangeListener = null;
    }


    public static class MVHolder extends RecyclerView.ViewHolder {

        public MVHolder(View itemView) {
            super(itemView);
        }
    }


    public void marketSel(final int position) {//标记选中项
        MLog.d(EXVAL.TAG, "KindAdapter marketSel " + position);
        final MVHolder mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(position);
        if (mvHolder != null) {
            mvHolder.itemView.setBackgroundResource(R.drawable.live_kind_sel_nof);
//            ((TextView) mvHolder.itemView).setTextColor(AppMain.res().getColor(R.color.list_item_nofocus_color));
        } else {
            MLog.e(EXVAL.TAG, "KindAdapter marketSel mvHolder null");
            rv.scrollToPosition(position);
            rv.post(new Runnable() {
                @Override
                public void run() {
                    final MVHolder mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(position);
                    if (mvHolder != null) {
                        MLog.d(EXVAL.TAG, "KindAdapter marketSel mvHolder");
                        mvHolder.itemView.setBackgroundResource(R.drawable.live_kind_sel_nof);
//                        ((TextView) mvHolder.itemView).setTextColor(AppMain.res().getColor(R.color.list_item_nofocus_color));
                    }
                }
            });
        }
    }










/*    public int getItemPosInList(LiveKindObj kind) {
        MLog.d(TAG, String.format("getItemPosInList %s", kind));
        String colName = kind.getColName();
        for (int i = 0; i < kinds.length; i++) {
            if (colName.equals(kinds[i])){
                return i;
            }
        }
        return -1;
    }*/


    public void resumeSel(final int position) {
        MLog.d(EXVAL.TAG, "KindAdapter resumeSel " + position);
        final MVHolder mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(position);
        if (mvHolder != null) {
            mvHolder.itemView.setBackgroundResource(0);
//            ((TextView) mvHolder.itemView).setTextColor(Color.WHITE);
        } else {
            MLog.e(EXVAL.TAG, "KindAdapter resumeSel mvHolder null");
            rv.scrollToPosition(position);
            rv.post(new Runnable() {
                @Override
                public void run() {
                    final MVHolder mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(position);
                    if (mvHolder != null) {
                        MLog.d(EXVAL.TAG, "KindAdapter resumeSel mvHolder");
                        mvHolder.itemView.setBackgroundResource(0);
//                        ((TextView) mvHolder.itemView).setTextColor(Color.WHITE);
                    }
                }
            });
        }
    }


    public void setOnAdapterItemListener(OnRvAdapterListener onAdapterItemListener) {
        this.onAdapterItemListener = onAdapterItemListener;
    }


    public int getFirstVisiblePosition(int cCount) {
        return cCount == 0 ? 0 : rv.getChildAdapterPosition(rv.getChildAt(0));
    }

    public void setSelection(final FocusBorder focusBorder, final int position) {
        MLog.d(EXVAL.TAG, "KindAdapter setSelection " + position);
        final MVHolder mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(position);
        if (mvHolder != null) {
            MLog.d(EXVAL.TAG, "KindAdapter setSelection mvHolder");
//            ((TextView) mvHolder.itemView).setTextColor(Color.WHITE);
            mvHolder.itemView.setBackgroundResource(0);
            Utils.focusV(mvHolder.itemView, true);
            horChalListV.handleKindItemFocus(mvHolder.itemView);
        } else {
            MLog.e(EXVAL.TAG, "KindAdapter setSelection mvHolder null");
            rv.scrollToPosition(position);
            rv.post(new Runnable() {
                @Override
                public void run() {
                    final MVHolder mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(position);
                    if (mvHolder != null) {
                        MLog.d(EXVAL.TAG, "KindAdapter setSelection mvHolder");
//                        ((TextView) mvHolder.itemView).setTextColor(Color.WHITE);
                        mvHolder.itemView.setBackgroundResource(0);
                        Utils.focusV(mvHolder.itemView, true);
                        horChalListV.handleKindItemFocus(mvHolder.itemView);
                    }
                }
            });
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private KindsAdapter hostCls;

        public MOnFocusChangeListener(KindsAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                if (hostCls != null && hostCls.onAdapterItemListener != null) {
                    int curPos = hostCls.rv.getChildLayoutPosition(v);
                    hostCls.onAdapterItemListener.onItemSelected(v, curPos);
                }
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private KindsAdapter hostCls;

        public MOnClickListener(KindsAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (hostCls != null && hostCls.onAdapterItemListener != null) {
                int curPos = hostCls.rv.getChildLayoutPosition(v);
                hostCls.onAdapterItemListener.onItemClick(v, curPos);
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private KindsAdapter hostCls;

        public MOnKeyListener(KindsAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (hostCls != null && hostCls.onAdapterItemListener != null) {
                    int curPos = hostCls.rv.getChildLayoutPosition(v);
                    hostCls.onAdapterItemListener.onItemKey(v, curPos, event, keyCode);
                }
            }
            return false;
        }
    }
}
