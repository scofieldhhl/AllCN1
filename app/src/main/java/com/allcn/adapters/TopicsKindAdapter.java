package com.allcn.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.TopicsMainAct;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.utils.GlideUtils;
import com.allcn.views.FocusKeepRecyclerView;
import com.datas.CKindObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

public class TopicsKindAdapter extends RecyclerView.Adapter<TopicsKindAdapter.MVHolder> {

    private static final String TAG = TopicsKindAdapter.class.getSimpleName();
    private List<CKindObj> cKindObjs;
    private int size, exWidth, exHeight, curSelIndex, ckTotalNum, ckTotalPageNum;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private FocusKeepRecyclerView rv;
    private TopicsMainAct act;
    private MVHolder mvHolder;
    private OnRvAdapterListener onRvAdapterListener;
    private String cid;

    public TopicsKindAdapter(TopicsMainAct act) {
        this.act = act;
        this.exWidth = (int) AppMain.res().getDimension(R.dimen.topics_kind_item_ex_w);
        this.exHeight = (int) AppMain.res().getDimension(R.dimen.topics_kind_item_ex_h);
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);
    }

    public void reset() {
        this.size = 0;
        this.ckTotalNum = 0;
        this.ckTotalPageNum = 0;
        this.curSelIndex = -1;
        this.curSelPageIndex = -1;
    }

    public void release() {
        this.rv = null;
        this.act = null;
        this.mvHolder = null;
        this.onRvAdapterListener = null;
        if (this.mOnKeyListener != null) {
            this.mOnKeyListener.relase();
            this.mOnKeyListener = null;
        }
        if (this.mOnClickListener != null) {
            this.mOnClickListener.relase();
            this.mOnClickListener = null;
        }
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.release();
            this.mOnFocusChangeListener = null;
        }
    }

    public void setOnRvListener(OnRvAdapterListener onRvAdapterListener) {
        this.onRvAdapterListener = onRvAdapterListener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (this.rv == null) {
            this.rv = (FocusKeepRecyclerView) recyclerView;
        }
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MVHolder(AppMain.getView(R.layout.topics_kind_item, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder mvHolder, int i) {
        MLog.d(TAG, "onBindViewHolder " + i);
        if(i>=cKindObjs.size())
            return;

        CKindObj cKindObj = cKindObjs.get(i);
        mvHolder.setName(cKindObj.getName());
        mvHolder.setBg(this.act, cKindObj.getImgUrl());
        mvHolder.initAdapterListener(this);
    }

    @Override
    public int getItemCount() {
        return cKindObjs==null?0:cKindObjs.size();
    }

    private void clearDatas() {
        if (this.cKindObjs != null) {
            this.cKindObjs.clear();
        }
    }

    public void setDatas(List<CKindObj> cKindObjs, int cKindObjNum, int ckTotalNum,
                         int ckTotalPageNum, int pageIndex) {
        MLog.d(TAG, "setDatas " + pageIndex);
        this.clearDatas();
        this.reset();
        this.cKindObjs = cKindObjs;
        this.size = cKindObjNum;
        this.ckTotalNum = ckTotalNum;
        this.ckTotalPageNum = ckTotalPageNum;
        this.curSelPageIndex = pageIndex;
        notifyDataSetChanged();
    }

    public void setSel(View view) {
        MLog.d(TAG, "setSel " + view);
        if (view == null) {
            this.curSelIndex = -1;
            this.mvHolder = null;
        } else {
            this.curSelIndex = this.rv.getChildLayoutPosition(view);
            this.mvHolder = (MVHolder) this.rv.findViewHolderForAdapterPosition(this.curSelIndex);
        }
    }

    public void setSelection(final int position) {
        if (this.size == 0) {
            return;
        }
        this.mvHolder = (MVHolder) this.rv.findViewHolderForAdapterPosition(position);
        if (this.mvHolder == null) {
            this.rv.scrollToPosition(position);
            this.rv.post(new Runnable() {
                @Override
                public void run() {
                    TopicsKindAdapter.this.mvHolder = (MVHolder)
                            TopicsKindAdapter.this.rv.findViewHolderForAdapterPosition(position);
                    if (TopicsKindAdapter.this.mvHolder != null) {
                        Utils.focusV(TopicsKindAdapter.this.mvHolder.itemView, true);
                    }
                }
            });
        } else {
            Utils.focusV(TopicsKindAdapter.this.mvHolder.itemView, true);
        }
    }

    public static class MVHolder extends RecyclerView.ViewHolder {

        private TextView nameV;
        private ImageView imgV;

        public MVHolder(@NonNull View itemView) {
            super(itemView);
            this.nameV = itemView.findViewById(R.id.topics_kind_item_name);
            this.imgV = itemView.findViewById(R.id.topics_kind_item_img);
        }

        public ImageView getImgV() {
            return imgV;
        }

        public void setName(String name) {
            if (this.nameV != null) {
                this.nameV.setText(name);
            }
        }

        public void scrollContent(boolean scroll) {
            if (this.nameV != null) {
                this.nameV.setSelected(scroll);
            }
        }

        public void setBg(Activity activity, String imgUrl) {
            if (this.imgV != null) {
                GlideUtils.Ins().loadUrlImg(activity, imgUrl, this.imgV);
            }
        }

        public void marketForFocus(boolean hasFocus) {
            MLog.d(TAG, String.format("marketForFocus hasFocus=%b", hasFocus));
            if (this.itemView != null) {
                this.itemView.setSelected(hasFocus);
                if (hasFocus) {
                    Utils.focusV(this.itemView, true);
                }
            }
        }

        public void initAdapterListener(TopicsKindAdapter hostCls) {
            if (this.itemView != null) {
                this.itemView.setOnClickListener(hostCls.mOnClickListener);
                this.itemView.setOnKeyListener(hostCls.mOnKeyListener);
                this.itemView.setOnFocusChangeListener(hostCls.mOnFocusChangeListener);
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private TopicsKindAdapter hostCls;

        public MOnClickListener(TopicsKindAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (this.hostCls != null) {
                this.hostCls.setSel(v);
                if (this.hostCls.onRvAdapterListener != null) {
                    this.hostCls.onRvAdapterListener.onItemClick(v, this.hostCls.curSelIndex);
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private TopicsKindAdapter hostCls;

        public MOnKeyListener(TopicsKindAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            MLog.d(TAG, String.format("onKey v=%s event=%s", v, event));
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (this.hostCls != null) {
                    this.hostCls.setSel(v);
                    if (this.hostCls.onRvAdapterListener != null) {
                        this.hostCls.onRvAdapterListener.onItemKey(v, this.hostCls.curSelIndex,
                                event, keyCode);
                    }
                }
            }
            return false;
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private TopicsKindAdapter hostCls;

        public MOnFocusChangeListener(TopicsKindAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (this.hostCls == null) {
                return;
            }

            if (hasFocus) {
                this.hostCls.setSel(v);
                if (this.hostCls.act != null) {
                    this.hostCls.act.focusBorderForV(v, 1.1f, 1.1f,
                            this.hostCls.exWidth, this.hostCls.exHeight);
                }
                if (this.hostCls.onRvAdapterListener != null) {
                    this.hostCls.onRvAdapterListener.onItemSelected(v, this.hostCls.curSelIndex);
                }
            }

            if (this.hostCls.mvHolder != null) {
                this.hostCls.mvHolder.scrollContent(hasFocus);
            }
        }
    }

    private int curSelPageIndex;

    public void loadPage(boolean isUP) {
        MLog.d(TAG, String.format("loadPage isUP=%b curSelPageIndex=%d ckTotalPageNum=%d",
                isUP, this.curSelPageIndex, this.ckTotalPageNum));
        int tmpCurSelPageIndex = this.curSelPageIndex;
        if (isUP) {
            if (--tmpCurSelPageIndex < 0) {
                return;
            }
        } else {
            if (++tmpCurSelPageIndex >= this.ckTotalPageNum) {
                return;
            }
        }
        DataCenter.Ins().scanCkindForPage(this.act, this.cid, tmpCurSelPageIndex,
                EXVAL.TOPICS_CKIND_NUM_IN_PAGE, true);
    }

    public int getCurSelPageIndex() {
        return this.curSelPageIndex;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public CKindObj getItemData(int pos) {
        return pos >= 0 && pos < this.size ? this.cKindObjs.get(pos) : null;
    }
}
