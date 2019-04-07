package com.allcn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.adapters.HotReAdaper;
import com.allcn.adapters.KeybordAdapter;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.interfaces.OnSearchListener;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.views.decorations.KeyboardItemDecoration;
import com.allcn.views.focus.FocusBorder;
import com.datas.CKindObj;
import com.datas.MovieObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class SearchAct extends BaseActivity {

    private static final String TAG = SearchAct.class.getSimpleName();
    private RecyclerView keybordRV, hotReRV;
    private KeybordAdapter keybordAdapter;
    private GridLayoutManager keybordLayoutMgr, hotReLayoutMgr;
    private HotReAdaper hotReAdaper;
    private MOnSearchListener mOnSearchListener;
    private MOnAdapterListener mOnAdapterListener;
    private ImageButton delAllV, delV;
    private MOnClickListener mOnClickListener;
    private KeyboardItemDecoration keyboardItemDecoration;
    private EditText showV;
    private MOnAdapterItemListener mOnAdapterItemListener;
    private int pageNum, totalNum, selPageIndex, selPos;
    private TextView pageV;
    private RelativeLayout searchHotRootV;
    private LinearLayout searchKeyboardRootV;
    private boolean isCkind, backupIsCkind, disableBackKey, backupDisableBackKey;
    private String pCid, backupPCid;
    private int backupSelPageIndex, backupPageNum, backupTotalNum, backupSelPos;
    private boolean execStoped, execDestroyed;
    private String[] ckindCidArr = new String[]{
            "92",
            "137",
            "138",
            "117",
            "136",
            "55",
            "77",
            "56",
            "96",
            "97",
            "98",
            "99",
            "100",
            "101",
            "102",
            "103",
            "104",
            "105",
            "106",
            "107",
            "108",
            "109",
            "110",
            "111",
            "112",
            "113",
            "114",
            "115",
    };
    private int ckindCidNum = ckindCidArr.length;
    private MTextWatcher mTextWatcher;
    private MOnKeyListener mOnKeyListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorV = getWindow().getDecorView();

        Intent intent = getIntent();
        this.pCid = intent.getStringExtra(EXVAL.MOVIE_PID);

        this.backupIsCkind = this.isCkind;
        this.backupPCid = this.pCid;
        this.backupSelPageIndex = -1;

        decorV.setBackgroundResource(R.drawable.topics_bg);

        this.keybordRV = findViewById(R.id.search_keyboard);
        this.hotReRV = findViewById(R.id.search_hot_re_list_v);
        this.delAllV = findViewById(R.id.search_del_all_v);
        this.delV = findViewById(R.id.search_del_v);
        this.showV = findViewById(R.id.search_edit_text_v);
        this.pageV = findViewById(R.id.search_page_v);
        this.searchHotRootV = findViewById(R.id.search_hot_root_v);
        this.searchKeyboardRootV = findViewById(R.id.search_keyboard_root_v);

        this.mOnClickListener = new MOnClickListener(this);
        this.delAllV.setOnClickListener(this.mOnClickListener);
        this.delV.setOnClickListener(this.mOnClickListener);

        this.mOnKeyListener = new MOnKeyListener(this);
        this.delV.setOnKeyListener(this.mOnKeyListener);
        this.showV.setOnKeyListener(this.mOnKeyListener);

        this.keybordRV.setLayoutManager(this.keybordLayoutMgr = new GridLayoutManager(this, 6));
        this.keybordRV.setAdapter(this.keybordAdapter = new KeybordAdapter());
        this.keybordRV.addItemDecoration(this.keyboardItemDecoration = new KeyboardItemDecoration());

        this.hotReRV.setLayoutManager(this.hotReLayoutMgr = new GridLayoutManager(this, 4));
        this.hotReRV.setAdapter(this.hotReAdaper = new HotReAdaper());

        this.mOnAdapterListener = new MOnAdapterListener(this);
        this.hotReAdaper.setOnAdapterListener(this.mOnAdapterListener);

        this.mOnAdapterItemListener = new MOnAdapterItemListener(this);
        this.keybordAdapter.setOnAdapterItemListener(this.mOnAdapterItemListener);

        this.mTextWatcher = new MTextWatcher(this);
        this.showV.addTextChangedListener(this.mTextWatcher);

        this.mOnSearchListener = new MOnSearchListener(this);
        DataCenter.Ins().addSearchListener(this.mOnSearchListener);
        DataCenter.Ins().initSearchMovies(null, this.isCkind, this.backupIsCkind, this.pCid);

        this.keybordRV.post(new Runnable() {
            @Override
            public void run() {
                try {
                    SearchAct.this.keybordAdapter.setSelection(0);
                } catch (Exception e) {
                }
            }
        });

//        hotReRV.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
//            @Override
//            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
//                MLog.d(TAG, String.format("oldFocus=%s\nnewFocus=%s", oldFocus, newFocus));
//            }
//        });
    }

    @Override
    protected void eventView() {

    }

    @Override
    protected void findView() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        DataCenter.Ins().addSearchListener(this.mOnSearchListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        this.execStoped = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MLog.d(TAG, "execStop");
        execStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MLog.d(TAG, "onDestroy");
        execDestroy();
    }

    @Override
    protected void stopAct() {
        DataCenter.Ins().delSearchListener(this.mOnSearchListener);
    }

    @Override
    protected void destroyAct() {
        DataCenter.Ins().stopInitSearchMoviesTask();
        this.delAllV.setOnClickListener(null);
        this.delV.setOnClickListener(null);
        if (this.mOnAdapterItemListener != null) {
            this.mOnAdapterItemListener.release();
            this.mOnAdapterItemListener = null;
        }
        if (this.mOnSearchListener != null) {
            this.mOnSearchListener.release();
            this.mOnSearchListener = null;
        }
        if (this.mOnAdapterListener != null) {
            this.mOnAdapterListener.release();
            this.mOnAdapterListener = null;
        }
        if (this.mOnClickListener != null) {
            this.mOnClickListener.release();
            this.mOnClickListener = null;
        }
        if (this.mOnKeyListener != null) {
            this.mOnKeyListener.release();
            this.mOnKeyListener = null;
        }
        if (this.hotReAdaper != null) {
            this.hotReAdaper.release();
            this.hotReAdaper = null;
        }
        if (this.keybordAdapter != null) {
            this.keybordAdapter.release();
            this.keybordAdapter = null;
        }
        this.keybordRV.setAdapter(null);
        this.keybordRV.setLayoutManager(null);
        this.hotReRV.setAdapter(null);
        this.hotReRV.setLayoutManager(null);
        this.delAllV = null;
        this.delV = null;
        this.keybordLayoutMgr = null;
        this.hotReLayoutMgr = null;
        this.keyboardItemDecoration = null;
        this.keybordRV = null;
        this.hotReRV = null;
        this.keyboardItemDecoration = null;
        this.searchHotRootV = null;
        this.searchKeyboardRootV = null;
        this.showV = null;
        this.pageV = null;
        this.pCid = null;
        this.backupPCid = null;
    }

    @Override
    FocusBorder createFocusBorder() {
        return null;
    }

    @Override
    int layoutId() {
        return R.layout.search_layout;
    }

    private static class MOnSearchListener implements OnSearchListener {

        private SearchAct hostCls;

        public MOnSearchListener(SearchAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onInitSearchResult(final List<Object> datas, int searchTotalNum) {
            if (hostCls == null) {
                return;
            }
            hostCls.totalNum = searchTotalNum;
            hostCls.pageNum = searchTotalNum / EXVAL.SEARCH_NUM_IN_PAGE;
            if (searchTotalNum % EXVAL.SEARCH_NUM_IN_PAGE != 0) {
                hostCls.pageNum++;
            }
            hostCls.selPageIndex = 0;
            hostCls.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (hostCls.totalNum > 0) {
                            hostCls.pageV.setText(String.format("%d/%d", 1, hostCls.totalNum));
                        } else {
                            hostCls.pageV.setText("0/0");
                        }
                        hostCls.hotReAdaper.setDatas(datas);
                        hostCls.hotReRV.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (!hostCls.isCkind && hostCls.backupIsCkind) {
                                        hostCls.hotReAdaper.setSelection(0);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        });
                    } catch (Exception e) {
                    }
                }
            });
        }

        @Override
        public void onSearchResultForPage(final List<Object> datas) {
            if (hostCls == null) {
                return;
            }
            hostCls.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        hostCls.hotReAdaper.setDatas(datas);
                        hostCls.hotReRV.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    hostCls.hotReAdaper.setSelection(0);
                                } catch (Exception e) {
                                }
                            }
                        });
                    } catch (Exception e) {
                    }
                }
            });
        }
    }

    private static class MOnAdapterListener implements HotReAdaper.MOnAdapterListener {

        private SearchAct hostCls;

        public MOnAdapterListener(SearchAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            hostCls = null;
        }

        @Override
        public void onItemClick(View v, Object data, int position) {

            if (hostCls == null) {
                return;
            }

            if (data instanceof CKindObj) {
                CKindObj ckind = (CKindObj) data;
                hostCls.disableBackKey = true;
                hostCls.backupDisableBackKey = hostCls.disableBackKey;
                hostCls.isCkind = false;
                hostCls.pCid = ckind.getCid();
                hostCls.backupSelPageIndex = hostCls.selPageIndex;
                hostCls.backupPageNum = hostCls.pageNum;
                hostCls.backupTotalNum = hostCls.totalNum;
                hostCls.backupSelPos = position;
                hostCls.hotReAdaper.backupDatas();
//                DataCenter.Ins().initSearchMovies(EXVAL.Mtv_CID, hostCls.isCkind,
//                        hostCls.backupIsCkind, hostCls.pCid);
            } else if (data instanceof MovieObj) {
                MovieObj movie = (MovieObj) data;
                boolean iskt = false;
                for (int i = 0; i < this.hostCls.ckindCidNum; i++) {
                    if (this.hostCls.ckindCidArr[i].equals(movie.getCid())) {
                        iskt = true;
                        break;
                    }
                }

                Intent intent = new Intent();
                intent.setClass(hostCls, iskt ? SEDMMovieDetailsAct.class : DetailsAct.class);
                intent.putExtra(EXVAL.MOVIE_OBJ, movie);
                hostCls.startActivity(intent);
            }
        }

        @Override
        public void onItemKey(View v, int position, int keyCode, KeyEvent event) {
            MLog.d(TAG, "onItemKey " + position);

            if (hostCls == null) {
                return;
            }

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP: {
                    if (position >= 0 && position < 4) {
                        hostCls.selPageIndex--;
                        if (hostCls.selPageIndex < 0) {
                            hostCls.selPageIndex = 0;
                        } else {
                            hostCls.selPos = 0;
                            Editable editable = hostCls.showV.getText();
                            if (editable != null) {
                                DataCenter.Ins().searchMoviesForPage(editable.toString(),
                                        hostCls.pCid, hostCls.selPageIndex, hostCls.isCkind,
                                        hostCls.backupIsCkind);
                            }
                        }
                    }
                    break;
                }
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (position >= 4 && position < 8) {
                        hostCls.selPageIndex++;
                        if (hostCls.selPageIndex >= hostCls.pageNum) {
                            hostCls.selPageIndex = hostCls.pageNum - 1;
                        } else {
                            hostCls.selPos = 0;
                            Editable editable = hostCls.showV.getText();
                            if (editable != null) {
                                DataCenter.Ins().searchMoviesForPage(editable.toString(),
                                        hostCls.pCid, hostCls.selPageIndex, hostCls.isCkind,
                                        hostCls.backupIsCkind);
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (position % EXVAL.SEARCH_NUM_IN_ROW == 0) {
                        hostCls.disableBackKey = false;
                        Utils.noFocus(hostCls.searchHotRootV);
                        Utils.noFocus(hostCls.hotReRV);
                        hostCls.searchHotRootV.setDescendantFocusability(
                                ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                        Utils.focusV(hostCls.searchKeyboardRootV, false);
                        hostCls.searchKeyboardRootV.setDescendantFocusability(
                                ViewGroup.FOCUS_AFTER_DESCENDANTS);
                        Utils.focusV(hostCls.keybordRV, true);
                    }
                    break;
                case KeyEvent.KEYCODE_BACK:
                    try {
                        hostCls.isCkind = hostCls.backupIsCkind;
                        hostCls.pCid = hostCls.backupPCid;
                        hostCls.totalNum = hostCls.backupTotalNum;
                        hostCls.selPos = hostCls.backupSelPos;
                        hostCls.hotReAdaper.resumeDatas();
                        hostCls.pageV.setText(String.format("%d/%d", hostCls.selPos + 1,
                                hostCls.totalNum));
                        hostCls.hotReRV.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    hostCls.hotReAdaper.setSelection(hostCls.selPos);
                                } catch (Exception e) {
                                }
                            }
                        });
                    } catch (Exception e) {
                    }
                    break;
            }
        }

        @Override
        public void onItemSelected(View v, Object data, int position) {

            if (hostCls == null) {
                return;
            }

            if (hostCls != null) {
                hostCls.pageV.setText(String.format("%d/%d",
                        hostCls.selPageIndex * EXVAL.SEARCH_NUM_IN_PAGE + position + 1,
                        hostCls.totalNum));
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private SearchAct hostCls;

        public MOnClickListener(SearchAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            hostCls = null;
        }

        @Override
        public void onClick(View v) {

            if (hostCls == null) {
                return;
            }

            if (v == hostCls.delAllV) {
                if (hostCls != null && hostCls.showV != null) {
                    hostCls.showV.setText("");
                    hostCls.showV.setBackgroundResource(R.drawable.search_frame_f);
                    DataCenter.Ins().initSearchMovies("",
                            hostCls.isCkind, hostCls.backupIsCkind, hostCls.pCid);
                }
            } else if (v == hostCls.delV) {
                if (hostCls != null && hostCls.showV != null) {
                    int index = this.hostCls.showV.getSelectionStart();
                    Editable editable = this.hostCls.showV.getText();
                    if (editable != null) {
                        int keyNum = editable.length();
                        if (keyNum > 0) {
                            editable.delete(index - 1, index);
                        }
                        DataCenter.Ins().initSearchMovies(editable.toString(),
                                hostCls.isCkind, hostCls.backupIsCkind, hostCls.pCid);
                    }
                }
            }
        }
    }

    private static class MOnAdapterItemListener implements OnRvAdapterListener {

        private SearchAct hostCls;

        public MOnAdapterItemListener(SearchAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onItemSelected(View view, int position) {

        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (hostCls == null) {
                return;
            }

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if ((position + 1) % 6 == 0 && hostCls.hotReAdaper.getItemCount() > 0) {
                        hostCls.disableBackKey = hostCls.backupDisableBackKey;
                        hostCls.searchKeyboardRootV.setDescendantFocusability(
                                ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                        Utils.noFocus(hostCls.searchKeyboardRootV);
                        Utils.noFocus(hostCls.keybordRV);
                        Utils.focusV(hostCls.searchHotRootV, false);
                        hostCls.searchHotRootV.setDescendantFocusability(
                                ViewGroup.FOCUS_AFTER_DESCENDANTS);
                        if (hostCls.hotReRV.getChildCount() > 0) {
                            hostCls.hotReRV.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        hostCls.hotReAdaper.setSelection(0);
                                    } catch (Exception e) {
                                    }
                                }
                            });
                        }
                    }
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int position) {
//            if (hostCls != null && hostCls.keyBuilder != null) {
//                if (!hostCls.isCkind && hostCls.backupIsCkind) {
//                    hostCls.isCkind = hostCls.backupIsCkind;
//                    hostCls.pCid = hostCls.backupPCid;
//                    hostCls.totalNum = 0;
//                    hostCls.selPos = 0;
//                }
//                hostCls.keyBuilder.append(view.getTag(R.id.tag_data));
//                hostCls.showV.setText(hostCls.keyBuilder);
//                hostCls.showV.setBackgroundResource(R.drawable.search_frame);
//            }
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private SearchAct hostCls;

        public MOnKeyListener(SearchAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (this.hostCls == null || (event.getAction() == KeyEvent.ACTION_UP)) {
                return false;
            }

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                    if (v == this.hostCls.showV || v == this.hostCls.delV) {
                        if (hostCls.hotReAdaper.getItemCount() > 0) {
                            hostCls.disableBackKey = hostCls.backupDisableBackKey;
                            hostCls.searchKeyboardRootV.setDescendantFocusability(
                                    ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                            Utils.noFocus(hostCls.searchKeyboardRootV);
                            Utils.noFocus(hostCls.keybordRV);
                            Utils.focusV(hostCls.searchHotRootV, false);
                            hostCls.searchHotRootV.setDescendantFocusability(
                                    ViewGroup.FOCUS_AFTER_DESCENDANTS);
                            if (hostCls.hotReRV.getChildCount() > 0) {
                                hostCls.hotReRV.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            hostCls.hotReAdaper.setSelection(0);
                                        } catch (Exception e) {
                                        }
                                    }
                                });
                            }
                        }
                    }
                    break;
                }
            }

            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (this.disableBackKey) {
                this.disableBackKey = false;
                this.backupDisableBackKey = this.disableBackKey;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SearchAct.this.execStop();
        SearchAct.this.execDestroy();
    }

    private static class MTextWatcher implements TextWatcher {

        private SearchAct hostCls;

        public MTextWatcher(SearchAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!hostCls.isCkind && hostCls.backupIsCkind) {
                hostCls.isCkind = hostCls.backupIsCkind;
                hostCls.pCid = hostCls.backupPCid;
                hostCls.totalNum = 0;
                hostCls.selPos = 0;
            }
            hostCls.showV.setBackgroundResource(count == 0 ? R.drawable.search_frame_f :
                    R.drawable.search_frame);
            DataCenter.Ins().initSearchMovies(s.toString(),
                    hostCls.isCkind, hostCls.backupIsCkind, hostCls.pCid);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
