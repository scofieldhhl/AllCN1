package com.allcn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.adapters.TopicsDetailsMovieAdapter;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.utils.GlideUtils;
import com.allcn.views.FocusKeepRecyclerView;
import com.allcn.views.decorations.TopicsDetailsMovieDecoration;
import com.allcn.views.focus.FocusBorder;
import com.datas.CKindObj;
import com.datas.MovieDetailsObj;
import com.datas.MovieObj;
import com.db.cls.DBMgr;
import com.mast.lib.utils.Utils;

import java.util.List;

public class TopicsDetailsAct extends BaseActivity {

    private static final String TAG = TopicsDetailsAct.class.getSimpleName();

    private TextView titleV, introV;
    private ImageView favV;
    private FocusKeepRecyclerView rv;
    private TopicsDetailsMovieAdapter adapter;
    private MOnRvAdapterListener mOnRvAdapterListener;
    private FrameLayout rootV;
    private CKindObj cKindObj;
    private boolean isFav, initIsFav;
    private MOnKeyListener mOnKeyListener;
    private MOnClickListener mOnClickListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        this.cKindObj = intent.getParcelableExtra(EXVAL.CKIND_OBJ);

        this.rootV = findViewById(R.id.topics_details_root_v);
        this.titleV = findViewById(R.id.topics_details_title_v);
        this.introV = findViewById(R.id.topics_details_intro_v);
        this.favV = findViewById(R.id.topics_details_img_v);
        this.rv = findViewById(R.id.topics_details_rv);

        this.mOnKeyListener = new MOnKeyListener(this);
        this.mOnClickListener = new MOnClickListener(this);

        this.favV.setOnClickListener(this.mOnClickListener);
        this.favV.setOnKeyListener(this.mOnKeyListener);

        GlideUtils.Ins().loadUrlImg(this, this.cKindObj.getBgUrl(), this.rootV);
        this.titleV.setText(this.cKindObj.getName());
        if (this.isFav = DBMgr.Ins().ckindInFavDB(this.cKindObj)) {
            this.favV.setActivated(true);
        }
        this.initIsFav = this.isFav;

        this.rv.setAdapter(this.adapter = new TopicsDetailsMovieAdapter(this));
        this.rv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        this.rv.addItemDecoration(new TopicsDetailsMovieDecoration());
        this.adapter.setOnRvListener(this.mOnRvAdapterListener = new MOnRvAdapterListener(this));

        DataCenter.Ins().scanAllMovies(this, this.cKindObj.getCid());
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
        this.execStoped = false;
        DataCenter.Ins().setActivityType(EXVAL.TYPE_ACTIVITY_OTH);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void stopAct() {

    }

    @Override
    protected void destroyAct() {
        DataCenter.Ins().stopScanMovieDetails();
        if (this.initIsFav != this.isFav) {
            DataCenter.Ins().handleFavMovie(this.cKindObj, this.isFav);
        }
        if (this.adapter != null) {
            this.adapter.release();
            this.adapter = null;
        }
        if (this.mOnKeyListener != null) {
            this.mOnKeyListener.release();
            this.mOnKeyListener = null;
        }
        if (this.mOnClickListener != null) {
            this.mOnClickListener.release();
            this.mOnClickListener = null;
        }
        if (this.mOnRvAdapterListener != null) {
            this.mOnRvAdapterListener.release();
            this.mOnRvAdapterListener = null;
        }
        if (this.initIsFav != this.isFav) {
            Intent intent = new Intent();
            intent.putExtra(EXVAL.FAV_CHANGED, true);
            this.setResult(RESULT_OK, intent);
        }
    }

    @Override
    public FocusBorder createFocusBorder() {
        return new FocusBorder.Builder()
                .asDrawable()
                .borderResId(R.drawable.kind_item_f)
                .needDuplicateIdleStatus(true)
                .build(this);
    }

    @Override
    public int layoutId() {
        return R.layout.topics_details_act;
    }

    @Override
    public void onBackPressed() {
        stopAct();
        destroyAct();
        super.onBackPressed();
    }

    public void loadMovieDatas(final List<MovieObj> movieObjs, final int movieNum) {
        TopicsDetailsAct.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TopicsDetailsAct.this.adapter.setDatas(movieObjs, movieNum);
                TopicsDetailsAct.this.adapter.setSelection(0);
            }
        });
    }

    public void loadDetails(final MovieDetailsObj movieDetails) {
        TopicsDetailsAct.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TopicsDetailsAct.this.introV.setText(
                        AppMain.res().getString(R.string.intro_nospace, movieDetails.getSummary()));
            }
        });
    }

    private static class MOnRvAdapterListener implements OnRvAdapterListener {

        private TopicsDetailsAct hostCls;

        public MOnRvAdapterListener(TopicsDetailsAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void onItemSelected(View view, int position) {
            if (this.hostCls != null) {
                if (this.hostCls.favV.isFocusable()) {
                    Utils.noFocus(this.hostCls.favV);
                }
                DataCenter.Ins().scanMovieDetails(this.hostCls,
                        this.hostCls.adapter.getItemData(position));
            }
        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (this.hostCls != null) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                        this.hostCls.adapter.marketCacheFocus(false);
                        Utils.focusV(this.hostCls.favV, true);
                        break;
                }
            }
        }

        @Override
        public void onItemClick(View view, int position) {
            if (this.hostCls != null) {
                Intent intent = new Intent(this.hostCls, DetailsAct.class);
                intent.putExtra(EXVAL.MOVIE_OBJ, this.hostCls.adapter.getItemData(position));
                this.hostCls.startActivityForResult(intent, EXVAL.DETAILS_REQ_CODE);
            }
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }

        @Override
        public void release() {
            this.hostCls = null;
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private TopicsDetailsAct hostCls;

        public MOnKeyListener(TopicsDetailsAct hostCls) {
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
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    switch (v.getId()) {
                        case R.id.topics_details_img_v:
                            this.hostCls.rv.post(new Runnable() {
                                @Override
                                public void run() {
                                    MOnKeyListener.this.hostCls.adapter.
                                            marketCacheFocus(true);
                                }
                            });
                            break;
                    }
                    break;
            }

            return false;
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private TopicsDetailsAct hostCls;

        public MOnClickListener(TopicsDetailsAct hostCls) {
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
            switch (v.getId()) {
                case R.id.topics_details_img_v: {
                    this.hostCls.isFav = !this.hostCls.isFav;
                    this.hostCls.favV.setActivated(this.hostCls.isFav);
                    break;
                }
            }
        }
    }
}
