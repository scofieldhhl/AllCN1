package com.allcn.views;

import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.allcn.R;
import com.allcn.activities.SEDMMovieDetailsAct;
import com.allcn.adapters.CollectionCateAdapter;
import com.allcn.adapters.CollectionItemAdapter;
import com.allcn.interfaces.ChalListWindow;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.EXVAL;
import com.allcn.views.decorations.CollectionCateDecoration;
import com.allcn.views.decorations.CollectionItemDecoration;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.ShareAdapter;
import com.mast.lib.utils.Utils;

public class SelectionWindow implements ChalListWindow {

    private static final String TAG = SelectionWindow.class.getSimpleName();
    private PopupWindow window;
    private RecyclerView rv, cateRv;
    private CollectionItemAdapter rvAdapter;
    private CollectionCateAdapter cateRvAdapter;
    private CollectionItemDecoration rvDecoration;
    private CollectionCateDecoration cateRvDecoration;
    private LinearLayoutManager cateRvLayoutMgr;
    private GridLayoutManager rvLayoutMgr;
    private SEDMMovieDetailsAct act;
    private FrameLayout rvRootV, cateRvRootV;
    private int totalUrlPageNum, totalUrlNum, cateReverse, playPos;
    private MOnDismissListener mOnDismissListener;
    private boolean release;
    private MOnAdapterItemListener rvAdapterListener;
    private MCateOnAdapterItemListener cateRvAdapterListener;

    public SelectionWindow(SEDMMovieDetailsAct activity) {

        this.act = activity;
        this.cateReverse = ShareAdapter.Ins(AppMain.ctx()).getI(EXVAL.REVERSE_KEY);

        if (this.cateReverse <= 0) {
            this.cateReverse = EXVAL.IS_REVERSE;
        }

        View rootV = LayoutInflater.from(AppMain.ctx()).inflate(R.layout.selection_window_layout, null);
        this.rv = rootV.findViewById(R.id.selection_list_v);
        this.cateRv = rootV.findViewById(R.id.selection_cates_v);
        this.rvRootV = rootV.findViewById(R.id.selection_list_root_v);
        this.cateRvRootV = rootV.findViewById(R.id.selection_cates_root_v);

        this.rv.setLayoutManager(this.rvLayoutMgr = new GridLayoutManager(activity,
                EXVAL.SEDM_COLLECTION_NUM_IN_LINE, LinearLayoutManager.VERTICAL, false));
        this.rv.addItemDecoration(this.rvDecoration = new CollectionItemDecoration());
        this.rv.setAdapter(this.rvAdapter = new CollectionItemAdapter(this.act));

        this.cateRv.setLayoutManager(this.cateRvLayoutMgr = new LinearLayoutManager(
                activity, LinearLayoutManager.HORIZONTAL,
                this.cateReverse == EXVAL.IS_REVERSE));
        this.cateRv.addItemDecoration(this.cateRvDecoration = new CollectionCateDecoration());
        this.cateRv.setAdapter(this.cateRvAdapter = new CollectionCateAdapter());

        this.rvAdapter.setOnAdapterItemListener(this.rvAdapterListener =
                new MOnAdapterItemListener(this));
        this.cateRvAdapter.setOnAdapterItemListener(this.cateRvAdapterListener =
                new MCateOnAdapterItemListener(this));

        this.window = new PopupWindow(rootV, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.window.setFocusable(true);
        this.window.setTouchable(true);
        this.window.setOutsideTouchable(true);
        this.window.setBackgroundDrawable(new BitmapDrawable());
        this.window.setOnDismissListener(
                this.mOnDismissListener = new MOnDismissListener(this));

//        this.rv.getViewTreeObserver().addOnGlobalFocusChangeListener(
//                new ViewTreeObserver.OnGlobalFocusChangeListener() {
//            @Override
//            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
//                MLog.d(TAG, String.format("oldFocus=%s\nnewFocus=%s", oldFocus, newFocus));
//            }
//        });
    }

    @Override
    public void show(View rootV) {
        int tmpPagePos = 0, posInUI;
        if (this.playPos < 0) {
            posInUI = this.cateReverse == EXVAL.IS_REVERSE ?
                    this.totalUrlNum : 1;
        } else {
            posInUI = this.playPos + 1;
        }
        tmpPagePos = posInUI / EXVAL.SEDM_COLLECTION_NUM_IN_PAGE;
        if (posInUI % EXVAL.SEDM_COLLECTION_NUM_IN_PAGE == 0) {
            tmpPagePos--;
        }
        MLog.d(TAG, String.format("initDatas playPos=%d", playPos));
        cateRvAdapter.setSelPos(tmpPagePos);
        rvAdapter.setPlayPos(playPos);
        rvAdapter.initDatas(totalUrlPageNum, totalUrlNum);
        rvAdapter.setReverse(this.cateReverse == EXVAL.IS_REVERSE);
        final SparseArray<String> cateArr = new SparseArray<>();
        for (int i = 0; i < totalUrlPageNum; i++) {
            int start = i * EXVAL.SEDM_COLLECTION_NUM_IN_PAGE + 1;
            int end = start + EXVAL.SEDM_COLLECTION_NUM_IN_PAGE - 1;
            if (end > totalUrlNum) {
                end = totalUrlNum;
            }
            cateArr.put(i, String.format("%d-%d", start, end));
        }
        rvAdapter.changePage(tmpPagePos);
        cateRvAdapter.setDatas(cateArr);
        cateRv.post(new Runnable() {
            @Override
            public void run() {
                try {
                    cateRvAdapter.markV();
                } catch (Exception e) {
                }
            }
        });
        Utils.noFocus(SelectionWindow.this.cateRvRootV);
        SelectionWindow.this.cateRvRootV.
                setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        Utils.focusV(SelectionWindow.this.rvRootV, false);
        SelectionWindow.this.rvRootV.
                setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        window.showAtLocation(rootV, Gravity.CENTER, 0, 0);
        this.cateRvAdapter.markV();

        final int posInPage = this.rvAdapter.getPosInUI(String.format("%02d", posInUI));
        this.rv.post(new Runnable() {
            @Override
            public void run() {
                try {
                    SelectionWindow.this.rvAdapter.setSelPos(posInPage);
                    SelectionWindow.this.rvAdapter.setSelection();
                } catch (Exception e) {
                }
            }
        });
    }

    public boolean isRelease() {
        return release;
    }

    @Override
    public void dismiss() {

    }

    @Override
    public void relase() {
        MLog.d(TAG, "relase");
        this.release = true;
        this.act = null;
        if (this.rv != null) {
            this.rv.setAdapter(null);
            this.rv.removeItemDecoration(this.rvDecoration);
            this.rv.setLayoutManager(null);
            this.rv = null;
        }
        this.rvDecoration = null;
        this.rvLayoutMgr = null;
        if (this.cateRv != null) {
            this.cateRv.setAdapter(null);
            this.cateRv.removeItemDecoration(this.cateRvDecoration);
            this.cateRv.setLayoutManager(null);
            this.cateRv = null;
        }
        this.cateRvDecoration = null;
        this.cateRvLayoutMgr = null;
        if (this.rvAdapter != null) {
            this.rvAdapter.release();
            this.rvAdapter = null;
        }
        if (this.cateRvAdapter != null) {
            this.cateRvAdapter.release();
            this.cateRvAdapter = null;
        }
        if (this.rvAdapterListener != null) {
            this.rvAdapterListener.release();
            this.rvAdapterListener = null;
        }
        if (this.cateRvAdapterListener != null) {
            this.cateRvAdapterListener.release();
            this.cateRvAdapterListener = null;
        }
        this.rvRootV = this.cateRvRootV = null;
        this.mOnDismissListener = null;
    }

    public void initDatas(int totalUrlPageNum, int totalUrlNum, int playPos) {
        this.totalUrlPageNum = totalUrlPageNum;
        this.totalUrlNum = totalUrlNum;
        this.playPos = playPos;
    }

    private static class MOnAdapterItemListener implements OnRvAdapterListener {

        private SelectionWindow hostCls;

        public MOnAdapterItemListener(SelectionWindow hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onItemSelected(View view, int position) {
            MLog.d(TAG, "Item onItemSelected " + position);
            if (this.hostCls == null || this.hostCls.rvRootV.getDescendantFocusability()
                    == ViewGroup.FOCUS_BLOCK_DESCENDANTS) {
                return;
            }
            try {
                this.hostCls.rvAdapter.setSelPos(position);
            } catch (Exception e) {
            }
        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            MLog.d(TAG, "item onItemKeyEvent " + event);
            if (this.hostCls == null) {
                return;
            }

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (position % EXVAL.SEDM_COLLECTION_NUM_IN_LINE == 0) {
                        final int selPos = this.hostCls.rvAdapter.getSelPos() +
                                EXVAL.SEDM_COLLECTION_NUM_IN_LINE - 1;
                        this.hostCls.rvDecoration.reset();
                        int pageIndex = this.hostCls.cateReverse == EXVAL.IS_REVERSE ?
                                this.hostCls.rvAdapter.nextPage() : hostCls.rvAdapter.prevPage();
                        this.hostCls.cateRvAdapter.setSelPos(pageIndex);
                        this.hostCls.cateRvAdapter.resumeMarkV();
                        this.hostCls.cateRvAdapter.markV();
                        this.hostCls.rvRootV.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                        this.hostCls.rv.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    int newSelPos = selPos;
                                    if (newSelPos > hostCls.rvAdapter.getItemCount() - 1) {
                                        newSelPos = hostCls.rvAdapter.getItemCount() - 1;
                                    }
                                    hostCls.rvRootV.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                                    hostCls.rvAdapter.setSelPos(newSelPos);
                                    hostCls.rvAdapter.setSelection();
                                } catch (Exception e) {
                                }
                            }
                        });
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (((position + 1) % EXVAL.SEDM_COLLECTION_NUM_IN_LINE) == 0 ||
                            position == hostCls.rvAdapter.getItemCount() - 1) {
                        final int selPos = hostCls.rvAdapter.getSelPos() -
                                EXVAL.SEDM_COLLECTION_NUM_IN_LINE + 1;
                        hostCls.rvDecoration.reset();
                        int pageIndex = hostCls.cateReverse == EXVAL.IS_REVERSE ?
                                hostCls.rvAdapter.prevPage() : hostCls.rvAdapter.nextPage();
                        hostCls.cateRvAdapter.setSelPos(pageIndex);
                        hostCls.cateRvAdapter.resumeMarkV();
                        hostCls.cateRvAdapter.markV();
                        hostCls.rvRootV.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                        hostCls.rv.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    int newSelPos = selPos;
                                    if (newSelPos < 0 ||
                                            newSelPos > hostCls.rvAdapter.getItemCount() - 1) {
                                        newSelPos = 0;
                                    }
                                    hostCls.rvRootV.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                                    hostCls.rvAdapter.setSelPos(newSelPos);
                                    hostCls.rvAdapter.setSelection();
                                } catch (Exception e) {
                                }
                            }
                        });
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (position >= 0 && position < EXVAL.SEDM_COLLECTION_NUM_IN_LINE) {
                        Utils.noFocus(hostCls.rvRootV);
                        hostCls.rvRootV.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                        Utils.focusV(hostCls.cateRvRootV, false);
                        hostCls.cateRv.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    hostCls.act.focusBorderVisible(false);
                                    hostCls.cateRvAdapter.setSelection();
                                    hostCls.cateRvRootV.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                                } catch (Exception e) {
                                }
                            }
                        });
                    }
                    break;
                case KeyEvent.KEYCODE_MENU:
                    hostCls.changeReverse();
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int position) {

            if (hostCls == null || hostCls.act == null) {
                return;
            }

            hostCls.act.startPlay(hostCls.rvAdapter.getPlayPosInUI());
//            hostCls.window.dismiss();
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    }

    private static class MCateOnAdapterItemListener implements OnRvAdapterListener {

        private SelectionWindow hostCls;

        public MCateOnAdapterItemListener(SelectionWindow hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onItemSelected(View view, int position) {
            MLog.d(TAG, "Cate onItemSelected " + position);
            if (hostCls == null || hostCls.cateRvAdapter == null ||
                    hostCls.rvAdapter == null || hostCls.cateRvRootV.getDescendantFocusability()
                    == ViewGroup.FOCUS_BLOCK_DESCENDANTS) {
                return;
            }

            if (hostCls.cateRvAdapter.getSelPos() != position) {
                hostCls.rvDecoration.reset();
                hostCls.cateRvAdapter.setSelPos(position);
                hostCls.rvAdapter.setSelPos(0);
                hostCls.cateRvAdapter.resumeMarkV();
                hostCls.cateRvAdapter.markV();
                hostCls.rvAdapter.changePage(position);
            }
        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            MLog.d(TAG, "cate onItemKeyEvent " + event);
            if (hostCls == null || hostCls.cateRvAdapter == null ||
                    hostCls.rvAdapter == null) {
                return;
            }

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    hostCls.cateRvRootV.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                    Utils.noFocus(hostCls.cateRvRootV);
                    hostCls.cateRvAdapter.resumeMarkV();
                    hostCls.cateRvAdapter.markV();
                    Utils.focusV(hostCls.rvRootV, false);
                    hostCls.rv.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                hostCls.rvRootV.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                                hostCls.rvAdapter.setSelPos(0);
                                hostCls.rvAdapter.setSelection();
                            } catch (Exception e) {
                            }
                        }
                    });
                    break;
                case KeyEvent.KEYCODE_MENU:
                    hostCls.changeReverse();
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int position) {

        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    }

    private static class MOnDismissListener implements PopupWindow.OnDismissListener {

        private SelectionWindow hostCls;

        public MOnDismissListener(SelectionWindow hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void onDismiss() {
            this.hostCls.relase();
            this.hostCls = null;
        }
    }

    private void changeReverse() {
        this.cateReverse = this.cateReverse == EXVAL.IS_REVERSE ?
                EXVAL.NO_REVERSE : EXVAL.IS_REVERSE;
        ShareAdapter.Ins(AppMain.ctx()).setI(EXVAL.REVERSE_KEY, this.cateReverse);
        rvDecoration.reset();
        rvAdapter.setReverse(this.cateReverse == EXVAL.IS_REVERSE);
        cateRvLayoutMgr.setReverseLayout(this.cateReverse == EXVAL.IS_REVERSE);
        cateRvAdapter.resumeMarkV();
        int pagePos = this.cateReverse == EXVAL.IS_REVERSE ? cateRvAdapter.getItemCount() - 1 : 0;
        cateRvAdapter.setSelPos(pagePos);
        cateRvAdapter.markV();
        rvAdapter.changePage(pagePos);
        if (rvRootV.getDescendantFocusability() == ViewGroup.FOCUS_AFTER_DESCENDANTS) {
            rv.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        rvAdapter.setSelPos(0);
                        rvAdapter.setSelection();
                    } catch (Exception e) {
                    }
                }
            });
        } else if (cateRvRootV.getDescendantFocusability() == ViewGroup.FOCUS_AFTER_DESCENDANTS) {
            cateRv.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        cateRvAdapter.setSelection();
                    } catch (Exception e) {
                    }
                }
            });
        }
    }

    public void refresh(int playPos) {
        if (this.playPos == playPos) {
            return;
        }
        this.playPos = playPos;
        rvAdapter.setPlayPos(this.playPos);
        playPos += 1;
        int pagePos = playPos / EXVAL.SEDM_COLLECTION_NUM_IN_PAGE;
        if (playPos % EXVAL.SEDM_COLLECTION_NUM_IN_PAGE == 0) {
            pagePos--;
        }
        final String posStr = String.format("%02d", playPos);
        rvDecoration.reset();
        rvAdapter.changePage(pagePos);
        cateRvAdapter.setSelPos(pagePos);
        cateRvAdapter.resumeMarkV();
        cateRvAdapter.markV();
        this.rv.post(new Runnable() {
            @Override
            public void run() {
                try {
                    int posInPage = SelectionWindow.this.rvAdapter.getPosInUI(posStr);
                    SelectionWindow.this.rvAdapter.setSelPos(posInPage);
                    SelectionWindow.this.rvAdapter.setSelection();
                } catch (Exception e) {
                }
            }
        });
    }
}
