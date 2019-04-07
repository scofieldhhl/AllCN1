package com.allcn.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.BaseActivity;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.EXVAL;
import com.allcn.views.FocusKeepRecyclerView;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

public class DetailsJSAdapter extends RecyclerView.Adapter<DetailsJSAdapter.MVHolder> {

    private static String TAG = DetailsJSAdapter.class.getSimpleName();
    private FocusKeepRecyclerView rv;
    private int size, startNum, marketPos, exW, exH;
    private View headerV;
    private OnRvAdapterListener onRvAdapterListener;
    private MOnClickListener mOnClickListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnKeyListener mOnKeyListener;
    private MVHolder marketedHolder;
    private BaseActivity activity;

    public DetailsJSAdapter(BaseActivity activity, View headerV) {
        this.activity = activity;
        this.headerV = headerV;
        this.mOnClickListener = new MOnClickListener(this);
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);
        this.exW = (int) AppMain.res().getDimension(R.dimen.details_js_item_ex_width);
        this.exH = (int) AppMain.res().getDimension(R.dimen.details_js_item_ex_height);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.rv = (FocusKeepRecyclerView) recyclerView;
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MVHolder(AppMain.getView(R.layout.details_js_item, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder mvHolder, int i) {
        int data = startNum + i;
        boolean marketF = data == marketPos;
        mvHolder.setNum(data + 1);
        mvHolder.market(marketF);
        if (marketF) {
            this.marketedHolder = mvHolder;
            headerV.setActivated(true);
        }
        mvHolder.initAdapterListener(this);
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void release() {
        this.rv = null;
        this.activity = null;
        this.headerV = null;
        this.marketedHolder = null;
        this.onRvAdapterListener = null;
        if (this.mOnKeyListener != null) {
            this.mOnKeyListener.release();
            this.mOnKeyListener = null;
        }
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.release();
            this.mOnFocusChangeListener = null;
        }
        if (this.mOnClickListener != null) {
            this.mOnClickListener.release();
            this.mOnClickListener = null;
        }
    }

    public void refreshData(int startNum, int totalNum) {
        this.startNum = startNum;
        this.size = totalNum - this.startNum;
        if (this.size > EXVAL.COLLECTION_NUM_IN_LINE) {
            this.size = EXVAL.COLLECTION_NUM_IN_LINE;
        }
        notifyDataSetChanged();
    }

    public void setMarketPos(int marketPos) {
        this.marketPos = marketPos;
    }

    public void marketCacheHolder(boolean hasFocus) {
        if (this.marketedHolder != null) {
            this.marketedHolder.market(hasFocus);
        }
    }

    public void handleCacheHolderfocus(boolean hasFocus) {
        if (this.marketedHolder != null) {
            this.marketedHolder.handleFocus(hasFocus);
        }
    }

    private boolean handleHodlerFocusForSelPos(int selPos, boolean hasFocus) {
        MVHolder mvHolder = this.rv == null ? null :
                (MVHolder) this.rv.findViewHolderForAdapterPosition(selPos);
        if (mvHolder != null) {
            mvHolder.handleFocus(hasFocus);
        }
        return mvHolder != null;
    }

    public void setSelection(final int selPos) {
        if (!handleHodlerFocusForSelPos(selPos, true)) {
            this.rv.scrollToPosition(selPos);
            this.rv.post(new Runnable() {
                @Override
                public void run() {
                    DetailsJSAdapter.this.handleHodlerFocusForSelPos(selPos, true);
                }
            });
        }
    }

    public void setOnRvAdapterListener(OnRvAdapterListener onRvAdapterListener) {
        this.onRvAdapterListener = onRvAdapterListener;
    }

    protected static class MVHolder extends RecyclerView.ViewHolder {

        public MVHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setNum(int num) {
            ((TextView) this.itemView).setText(String.valueOf(num));
        }

        public void market(boolean market) {
            this.itemView.setActivated(market);
        }

        public void handleFocus(boolean hasFocus) {
            if (hasFocus) {
                this.itemView.setActivated(false);
                Utils.focusV(this.itemView, true);
            }
        }

        public void initAdapterListener(DetailsJSAdapter hostCls) {
            if (hostCls != null) {
                this.itemView.setOnClickListener(hostCls.mOnClickListener);
                this.itemView.setOnFocusChangeListener(hostCls.mOnFocusChangeListener);
                this.itemView.setOnKeyListener(hostCls.mOnKeyListener);
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private DetailsJSAdapter hostCls;

        public MOnClickListener(DetailsJSAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {

            if (this.hostCls == null) {
                return;
            }

            if (this.hostCls.onRvAdapterListener != null) {
                this.hostCls.onRvAdapterListener.onItemClick(v, this.hostCls.getSelVIndex(v));
            }
        }
    }

    public void setIgoreFocus(boolean igoreFocus) {
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.setIgoreFocus(igoreFocus);
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private DetailsJSAdapter hostCls;
        private boolean igoreFocus;

        public MOnFocusChangeListener(DetailsJSAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            MLog.d(TAG, String.format("onFocusChange hasFocus=%b igoreFocus=%b",
                    hasFocus, this.igoreFocus));
            if (this.hostCls == null || this.igoreFocus) {
                return;
            }

            if (hasFocus) {
                this.hostCls.rv.setCacheHasFocus(true);
                v.setActivated(false);
                this.hostCls.activity.focusBorderForV(v, 1.5f, 1.5f,
                        this.hostCls.exW, this.hostCls.exH);
                if (this.hostCls.onRvAdapterListener != null) {
                    this.hostCls.onRvAdapterListener.onItemSelected(v,
                            this.hostCls.getSelVIndex(v));
                }
            }
        }

        public void setIgoreFocus(boolean igoreFocus) {
            this.igoreFocus = igoreFocus;
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private DetailsJSAdapter hostCls;

        public MOnKeyListener(DetailsJSAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (this.hostCls != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (this.hostCls.onRvAdapterListener != null) {
                    this.hostCls.onRvAdapterListener.onItemKey(v, this.hostCls.getSelVIndex(v),
                            event, keyCode);
                }
            }

            return false;
        }
    }

    private int getSelVIndex(View view) {
        return this.rv == null ? -1 : this.rv.getChildAdapterPosition(view);
    }

    public int getSelNum(int selPos) {
        return startNum + selPos;
    }
}
