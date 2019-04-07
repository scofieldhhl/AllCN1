package com.allcn.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.SEDMMovieDetailsAct;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.EXVAL;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CollectionItemAdapter extends RecyclerView.Adapter<CollectionItemAdapter.MVHolder> {

    private static final String TAG = CollectionItemAdapter.class.getSimpleName();
    private int size, pageIndex, totalPageNum, totalNum;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private RecyclerView rv;
    private int focusColor;
    private List<String> numArr;
    private int selPos, playPos;
    private MVHolder mvHolder;
    private boolean reverse;
    private OnRvAdapterListener onAdapterItemListener;
    private SEDMMovieDetailsAct activity;

    public CollectionItemAdapter(SEDMMovieDetailsAct activity) {
        this.activity = activity;
        mOnClickListener = new MOnClickListener(this);
        mOnKeyListener = new MOnKeyListener(this);
        mOnFocusChangeListener = new MOnFocusChangeListener(this);
        pageIndex = -1;
        playPos = -1;
        focusColor = Color.parseColor("#FD6232");
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.rv = recyclerView;
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MVHolder(AppMain.getView(R.layout.collection_item, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        textView.setText(numArr.get(position));
//        textView.setTypeface(AppMain.getJdfquFont());
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.itemView.setOnKeyListener(mOnKeyListener);
        holder.itemView.setOnFocusChangeListener(mOnFocusChangeListener);
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void setOnAdapterItemListener(OnRvAdapterListener onAdapterItemListener) {
        this.onAdapterItemListener = onAdapterItemListener;
    }

    public void initDatas(int totalPageNum, int totalNum) {
        this.totalPageNum = totalPageNum;
        this.totalNum = totalNum;
        this.pageIndex = -1;
    }

    private void clearDatas() {
        if (numArr != null) {
            numArr.clear();
            numArr = null;
        }
    }

    public int getPosInUI(String posStr) {
        return TextUtils.isEmpty(posStr) ? -1 : numArr == null ? -1 : numArr.indexOf(posStr);
    }

    private void fillDatas() {
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.reset();
        }
        int start = pageIndex * EXVAL.SEDM_COLLECTION_NUM_IN_PAGE;
        numArr = new ArrayList<>(size);
        if (this.reverse) {
            for (int i = size; i > 0; i--) {
                numArr.add(String.format("%02d", start + i));
            }
        } else {
            for (int i = 0; i < size; i++) {
                numArr.add(String.format("%02d", start + i + 1));
            }
        }
    }

    public int prevPage() {
        if (this.totalNum <= 0) {
            return 0;
        }
        int tmpPageIndex = this.pageIndex;
        tmpPageIndex--;
        if (tmpPageIndex < 0) {
            tmpPageIndex = this.totalPageNum - 1;
        }
        if (tmpPageIndex != this.pageIndex) {
            this.pageIndex = tmpPageIndex;
            int start = this.pageIndex * EXVAL.SEDM_COLLECTION_NUM_IN_PAGE;
            int end = start + EXVAL.SEDM_COLLECTION_NUM_IN_PAGE;
            if (end > this.totalNum) {
                end = this.totalNum;
            }
            this.size = end - start;
            clearDatas();
            fillDatas();
            notifyDataSetChanged();
        }
        return this.pageIndex;
    }

    public int nextPage() {
        if (this.totalNum <= 0) {
            return 0;
        }
        int tmpPageIndex = this.pageIndex;
        tmpPageIndex++;
        if (tmpPageIndex >= this.totalPageNum) {
            tmpPageIndex = 0;
        }
        if (tmpPageIndex != this.pageIndex) {
            this.pageIndex = tmpPageIndex;
            int start = this.pageIndex * EXVAL.SEDM_COLLECTION_NUM_IN_PAGE;
            int end = start + EXVAL.SEDM_COLLECTION_NUM_IN_PAGE;
            if (end > this.totalNum) {
                end = this.totalNum;
            }
            this.size = end - start;
            clearDatas();
            fillDatas();
            notifyDataSetChanged();
        }
        return this.pageIndex;
    }

    public int changePage(int pageIndex) {
        if (pageIndex < 0 || pageIndex >= this.totalPageNum) {
            return -1;
        }
//        if (pageIndex != this.pageIndex) {
        this.pageIndex = pageIndex;
        int start = this.pageIndex * EXVAL.SEDM_COLLECTION_NUM_IN_PAGE;
        int end = start + EXVAL.SEDM_COLLECTION_NUM_IN_PAGE;
        if (end > this.totalNum) {
            end = this.totalNum;
        }
        this.size = end - start;
        clearDatas();
        fillDatas();
        notifyDataSetChanged();
//        }
        return this.pageIndex;
    }

    public void setSelection() {
        MLog.d(TAG, "setSelection " + selPos);
        if (selPos < 0) {
            return;
        }
        mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(selPos);
        if (mvHolder == null) {
            rv.scrollToPosition(selPos);
            rv.post(new Runnable() {
                @Override
                public void run() {
                    if (mvHolder == null) {
                        mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(selPos);
                    }
                    if (mvHolder != null) {
                        mvHolder.itemView.setSelected(true);
                        Utils.focusV(mvHolder.itemView, true);
                    }
                }
            });
        } else {
            mvHolder.itemView.setSelected(true);
            Utils.focusV(mvHolder.itemView, true);
        }
    }

    public void setSelPos(int selPos) {
        this.selPos = selPos;
    }

    public int getSelPos() {
        return selPos;
    }

    public int getPos() {
        return pageIndex * EXVAL.SEDM_COLLECTION_NUM_IN_PAGE + selPos;
    }

    public int getPlayPosInUI() {
        String posStr = numArr == null || selPos < 0 ? "" : numArr.get(selPos);
        MLog.d(TAG, String.format("getPlayPosInUI posStr=%s selPos=%d", posStr, selPos));
        return TextUtils.isEmpty(posStr) ? -1 : Integer.valueOf(posStr) - 1;
    }

    public int getPlayPosInUI(int selPos) {
        String posStr = numArr == null || selPos < 0 ? "" : numArr.get(selPos);
        MLog.d(TAG, String.format("getPlayPosInUI posStr=%s selPos=%d", posStr, selPos));
        return TextUtils.isEmpty(posStr) ? -1 : Integer.valueOf(posStr) - 1;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public static class MVHolder extends RecyclerView.ViewHolder {

        public MVHolder(View itemView) {
            super(itemView);
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private CollectionItemAdapter hostCls;

        public MOnClickListener(CollectionItemAdapter hostCls) {
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

        private CollectionItemAdapter hostCls;
        private View oldV;

        public MOnFocusChangeListener(CollectionItemAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.oldV = null;
            this.hostCls = null;
        }

        public void reset() {
            if (oldV != null) {
                oldV.setActivated(false);
                oldV = null;
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (hostCls != null && hostCls.rv != null) {
                int pos = hostCls.rv.getChildAdapterPosition(v);
                int posInUI = hostCls.getPlayPosInUI(pos);
                MLog.d(TAG, String.format("onFocusChange posInUI=%d playPos=%d", posInUI, hostCls.playPos));
                if (hasFocus) {
                    if ((posInUI >= 0) && (posInUI == hostCls.playPos)) {
                        v.setActivated(false);
                    }
                    this.hostCls.activity.focusBorderForV(v, 1.2f, 1.2f);
                    if (hostCls.onAdapterItemListener != null) {
                        hostCls.onAdapterItemListener.onItemSelected(v, pos);
                    }
                } else {
                    if ((posInUI >= 0) && (posInUI == hostCls.playPos)) {
                        oldV = v;
                        v.setActivated(true);
                    }
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private CollectionItemAdapter hostCls;

        public MOnKeyListener(CollectionItemAdapter hostCls) {
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

    public void setPlayPos(int playPos) {
        this.playPos = playPos;
    }

    public void release() {
        this.activity = null;
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
        if (this.numArr != null) {
            this.numArr.clear();
            this.numArr = null;
        }
        this.rv = null;
        this.mvHolder = null;
    }
}
