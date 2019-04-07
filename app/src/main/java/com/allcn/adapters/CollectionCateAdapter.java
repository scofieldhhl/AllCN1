package com.allcn.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

public class CollectionCateAdapter extends RecyclerView.Adapter<CollectionCateAdapter.MVHolder> {

    private static final String TAG = CollectionCateAdapter.class.getSimpleName();
    private int size;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private SparseArray<String> cateArr;
    private RecyclerView rv;
    private LinearLayout rootV;
    private TextView textView;
    private View lineV;
    private int focusColor, selPos;
    private MVHolder mvHolder;
    private OnRvAdapterListener onAdapterItemListener;

    public CollectionCateAdapter() {
        mOnClickListener = new MOnClickListener(this);
        mOnKeyListener = new MOnKeyListener(this);
        mOnFocusChangeListener = new MOnFocusChangeListener(this);
        focusColor = AppMain.res().getColor(R.color.details_focus_color);
    }

    public void setOnAdapterItemListener(OnRvAdapterListener onAdapterItemListener) {
        this.onAdapterItemListener = onAdapterItemListener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (this.rv == null) {
            this.rv = recyclerView;
        }
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MVHolder(LayoutInflater.from(AppMain.ctx()).inflate(R.layout.collection_cate_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder holder, int position) {
        if (cateArr != null && size != 0) {
            String cateStr = cateArr.get(position);
            MLog.d(TAG, "cateStr = " + cateStr);
            holder.textView.setText(cateStr);
//            holder.textView.setTypeface(AppMain.getJdfquFont());
        }
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.itemView.setOnKeyListener(mOnKeyListener);
        holder.itemView.setOnFocusChangeListener(mOnFocusChangeListener);
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void clearDatas() {
        if (cateArr != null) {
            cateArr.clear();
            cateArr = null;
        }
    }

    public void setDatas(SparseArray<String> datas) {
        clearDatas();
        cateArr = datas;
        size = cateArr == null ? 0 : cateArr.size();
        notifyDataSetChanged();
    }

    public void markV() {
        mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(selPos);
        if (mvHolder == null) {
            rv.scrollToPosition(selPos);
            rv.post(new Runnable() {
                @Override
                public void run() {
                    mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(selPos);
                    if (mvHolder != null) {
                        mvHolder.textView.setTextColor(focusColor);
                        mvHolder.view.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            mvHolder.textView.setTextColor(focusColor);
            mvHolder.view.setVisibility(View.VISIBLE);
        }
    }

    public void resumeMarkV() {
        if (mvHolder != null) {
            mvHolder.textView.setTextColor(AppMain.res().getColor(R.color.collection_cate_item_textcolor));
            mvHolder.view.setVisibility(View.GONE);
            mvHolder = null;
        }
    }

    public void setSelPos(int selPos) {
        MLog.d(TAG, "setSelPos " + selPos);
        this.selPos = selPos;
    }

    public int getSelPos() {
        return selPos;
    }

    public void setSelection() {
        MLog.d(TAG, "setSelection " + selPos);
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

    public static class MVHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public View view;

        public MVHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.collection_cate_text_v);
            view = itemView.findViewById(R.id.collection_cate_line_v);
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private CollectionCateAdapter hostCls;

        public MOnClickListener(CollectionCateAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {

            if (hostCls != null && hostCls.rv != null) {
                int pos = hostCls.rv.getChildAdapterPosition(v);
                hostCls.onAdapterItemListener.onItemClick(v, pos);
            }
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private CollectionCateAdapter hostCls;

        public MOnFocusChangeListener(CollectionCateAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (hasFocus) {
                if (hostCls != null && hostCls.rv != null) {
                    int pos = hostCls.rv.getChildAdapterPosition(v);
                    hostCls.onAdapterItemListener.onItemSelected(v, pos);
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private CollectionCateAdapter hostCls;

        public MOnKeyListener(CollectionCateAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (hostCls != null && hostCls.rv != null) {
                    int pos = hostCls.rv.getChildAdapterPosition(v);
                    hostCls.onAdapterItemListener.onItemKey(v, pos, event, keyCode);
                }
            }
            return false;
        }
    }

    public void release() {
        this.onAdapterItemListener = null;
        if (this.mOnClickListener != null) {
            this.mOnClickListener.release();
            this.mOnClickListener = null;
        }
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.release();
            this.mOnFocusChangeListener = null;
        }
        if (this.mOnKeyListener != null) {
            this.mOnKeyListener.release();
            this.mOnKeyListener = null;
        }
        if (this.cateArr != null) {
            this.cateArr.clear();
            this.cateArr = null;
        }
        this.rv = null;
        this.mvHolder = null;
    }
}
