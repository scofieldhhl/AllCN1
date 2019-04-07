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

public class KeybordAdapter extends RecyclerView.Adapter<KeybordAdapter.MVHolder> {

    private String[] keybordArr;
    private int size;
    private RecyclerView rv;
    private MOnClickListener mOnClickListener;
    private OnRvAdapterListener onAdapterItemListener;
    private MVHolder mvHolder;
    private MOnKeyListener mOnKeyListener;

    public KeybordAdapter() {
        keybordArr = AppMain.res().getStringArray(R.array.keybord_arr);
        size = keybordArr.length;
        mOnClickListener = new MOnClickListener(this);
        mOnKeyListener = new MOnKeyListener(this);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.rv = recyclerView;
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MVHolder(LayoutInflater.from(AppMain.ctx()).inflate(
                R.layout.keybord_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder holder, int position) {
        String str = keybordArr[position];
        ((TextView) holder.itemView).setText(str);
        holder.itemView.setTag(R.id.tag_data, str);
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.itemView.setOnKeyListener(mOnKeyListener);
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void release() {
        mvHolder = null;
        keybordArr = null;
        if (mOnClickListener != null) {
            mOnClickListener.release();
            mOnClickListener = null;
        }
        if (mOnKeyListener != null) {
            mOnKeyListener.release();
            mOnKeyListener = null;
        }
        rv = null;
        onAdapterItemListener = null;
    }

    public static class MVHolder extends RecyclerView.ViewHolder {

        public MVHolder(View itemView) {
            super(itemView);
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private KeybordAdapter hostCls;

        public MOnClickListener(KeybordAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (hostCls != null && hostCls.onAdapterItemListener != null) {
                int position = hostCls.rv.getChildAdapterPosition(v);
                hostCls.onAdapterItemListener.onItemClick(v, position);
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private KeybordAdapter hostCls;

        public MOnKeyListener(KeybordAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (hostCls != null && hostCls.onAdapterItemListener != null) {
                    int position = hostCls.rv.getChildAdapterPosition(v);
                    hostCls.onAdapterItemListener.onItemKey(v, position, event, keyCode);
                }
            }
            return false;
        }
    }

    public void setSelection(final int pos) {
        try {
            mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(pos);
            if (mvHolder == null) {
                rv.scrollToPosition(pos);
                rv.post(new Runnable() {
                    @Override
                    public void run() {
                        mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(pos);
                        if (mvHolder != null) {
                            Utils.focusV(mvHolder.itemView, true);
                        }
                    }
                });
            } else {
                Utils.focusV(mvHolder.itemView, true);
            }
        } catch (Exception e) {}
    }

    public void setOnAdapterItemListener(OnRvAdapterListener listener) {
        this.onAdapterItemListener = listener;
    }
}
