package com.allcn.adapters;

import android.graphics.Color;
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
import com.allcn.views.focus.FocusBorder;
import com.datas.LiveCKindObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

public class CKAdapter extends RecyclerView.Adapter<CKAdapter.MVHolder> {

    private static final String TAG = CKAdapter.class.getSimpleName();
    private LayoutInflater layoutInflater;
    private List<LiveCKindObj> cKinds;
    private RecyclerView rv;
    private int size;
    private OnRvAdapterListener onAdapterItemListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private MVHolder mvHolder;
    private LiveAct act;

    public CKAdapter(LiveAct act) {
        this.act = act;
        layoutInflater = LayoutInflater.from(AppMain.ctx());
        mOnFocusChangeListener = new MOnFocusChangeListener(this);
        mOnClickListener = new MOnClickListener(this);
        mOnKeyListener = new MOnKeyListener(this);
    }

    public void setRecyclerView(RecyclerView rv) {
        this.rv = rv;
    }

    public void setDatas(List<LiveCKindObj> kinds) {
        this.cKinds = kinds;
        size = this.cKinds == null ? 0 : this.cKinds.size();
        notifyDataSetChanged();
    }

    public void marketSel(int position) {
        MLog.d(EXVAL.TAG, "CKAdapter marketSel " + position);
        MVHolder mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(position);
        if (mvHolder != null) {
            ((TextView) mvHolder.itemView).setTextColor(AppMain.res().getColor(R.color.list_item_nofocus_color));
        }
    }

    public void setSelection(final FocusBorder focusBorder, int position) {
        MLog.d(EXVAL.TAG, "CKAdapter setSelection " + position);
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
                        ((TextView) mvHolder.itemView).setTextColor(Color.WHITE);
                        Utils.focusV(mvHolder.itemView, true);
                        act.focusBorderForV(mvHolder.itemView);
                    }
                }
            });
        } else {
            ((TextView) mvHolder.itemView).setTextColor(Color.WHITE);
            Utils.focusV(mvHolder.itemView, true);
            act.focusBorderForV(mvHolder.itemView);
        }
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MVHolder(layoutInflater.inflate(R.layout.live_ckind_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        TextView textView = (TextView) holder.itemView;
        textView.setText(cKinds.get(position).getColName());
        holder.itemView.setOnFocusChangeListener(mOnFocusChangeListener);
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.itemView.setOnKeyListener(mOnKeyListener);
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public LiveCKindObj getItemObject(int position) {
        return size > 0 ? cKinds.get(position) : null;
    }

    public int getItemPosInList(LiveCKindObj cKind) {
        MLog.d(TAG, String.format("getItemPosInList %s", cKind));
        return size > 0 ? cKinds.indexOf(cKind) : -1;
    }

    public void release() {
        clearDatas(true);
        cKinds = null;
        rv = null;
        layoutInflater = null;
        onAdapterItemListener = null;
        act = null;
        if (mOnKeyListener != null) {
            mOnKeyListener.relase();
            mOnKeyListener = null;
        }
        if (mOnClickListener != null) {
            mOnClickListener.relase();
            mOnClickListener = null;
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.relase();
            mOnFocusChangeListener = null;
        }
    }

    public void setOnAdapterItemListener(OnRvAdapterListener onAdapterItemListener) {
        this.onAdapterItemListener = onAdapterItemListener;
    }

    public static class MVHolder extends RecyclerView.ViewHolder {

        public MVHolder(View itemView) {
            super(itemView);
        }
    }

    public void clearDatas(boolean clear) {
        if (cKinds != null && clear) {
            cKinds.clear();
            cKinds = null;
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private CKAdapter hostCls;

        public MOnFocusChangeListener(CKAdapter hostCls) {
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

        private CKAdapter hostCls;

        public MOnClickListener(CKAdapter hostCls) {
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

        private CKAdapter hostCls;

        public MOnKeyListener(CKAdapter hostCls) {
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
