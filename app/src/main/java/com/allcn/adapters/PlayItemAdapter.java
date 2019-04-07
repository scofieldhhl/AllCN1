package com.allcn.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.mast.lib.utils.Utils;

public class PlayItemAdapter extends RecyclerView.Adapter<PlayItemAdapter.MVHolder> {

    private static final String TAG = PlayItemAdapter.class.getSimpleName();
    private int totalNum;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private OnRvAdapterListener onAdapterItemListener;
    private RecyclerView rv;
    private int selPos;
    private MVHolder mvHolder;

    public PlayItemAdapter() {
        mOnClickListener = new MOnClickListener(this);
        mOnKeyListener = new MOnKeyListener(this);
        mOnFocusChangeListener = new MOnFocusChangeListener(this);
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MVHolder(LayoutInflater.from(AppMain.ctx()).inflate(R.layout.play_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder holder, int position) {
        ((TextView) holder.itemView).setText(String.valueOf(position + 1));
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.itemView.setOnKeyListener(mOnKeyListener);
        holder.itemView.setOnFocusChangeListener(mOnFocusChangeListener);
    }

    @Override
    public int getItemCount() {
        return totalNum;
    }

    public void initDatas(int totalNum) {
        this.totalNum = totalNum;
        notifyDataSetChanged();
    }

    public void attachRecylerView(RecyclerView cateRv) {
        rv = cateRv;
    }

    public void setSelection() {
        mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(selPos);
        if (mvHolder == null) {
            rv.scrollToPosition(selPos);
        }
        rv.post(new Runnable() {
            @Override
            public void run() {
                if (mvHolder == null) {
                    mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(selPos);
                }
                if (mvHolder != null) {
                    Utils.focusV(mvHolder.itemView, true);
                }
            }
        });
    }

    public void setSelPos(int selPos) {
        this.selPos = selPos;
    }

    public int getSelPos() {
        return selPos;
    }

    public void setOnAdapterItemListener(OnRvAdapterListener onAdapterItemListener) {
        this.onAdapterItemListener = onAdapterItemListener;
    }

    public static class MVHolder extends RecyclerView.ViewHolder {

        public MVHolder(View itemView) {
            super(itemView);
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private PlayItemAdapter hostCls;

        public MOnClickListener(PlayItemAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {

            if (hostCls != null && hostCls.onAdapterItemListener != null && hostCls.rv != null) {
                int pos = hostCls.rv.getChildAdapterPosition(v);
                hostCls.onAdapterItemListener.onItemClick(v, pos);
            }
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private PlayItemAdapter hostCls;

        public MOnFocusChangeListener(PlayItemAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (hasFocus) {
                if (hostCls != null && hostCls.onAdapterItemListener != null && hostCls.rv != null) {
                    int pos = hostCls.rv.getChildAdapterPosition(v);
                    hostCls.onAdapterItemListener.onItemSelected(v, pos);
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private PlayItemAdapter hostCls;

        public MOnKeyListener(PlayItemAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (hostCls != null && hostCls.onAdapterItemListener != null && hostCls.rv != null) {
                    int pos = hostCls.rv.getChildAdapterPosition(v);
                    hostCls.onAdapterItemListener.onItemKey(v, pos, event, keyCode);
                }
            }
            return false;
        }
    }

    public void release() {
        rv = null;
        mvHolder = null;
        if (mOnClickListener != null) {
            mOnClickListener.release();
            mOnClickListener = null;
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.release();
            mOnFocusChangeListener = null;
        }
        if (mOnKeyListener != null) {
            mOnKeyListener.release();
            mOnKeyListener = null;
        }
        onAdapterItemListener = null;
    }
}
