package com.allcn.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.interfaces.OnDataListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.views.focus.FocusBorder;
import com.datas.HomeMovie;
import com.datas.LiveChalObj;
import com.mast.lib.parsers.MarqueeParser;
import com.mast.lib.utils.Utils;

import java.io.File;

public class HotReAct extends BaseActivity {

    private static final String TAG = HotReAct.class.getSimpleName();
    private TextView dyV, jjV, zyV, ztV;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private int exWidth;
    private TextView timeV;
    private ImageView netV;
    private MOnDataListener mOnDataListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(android.R.id.content).setBackgroundResource(R.drawable.hot_re_bg);

        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);

        this.timeV = findViewById(R.id.hot_re_time_v);
        this.netV = findViewById(R.id.hot_re_net_v);
        this.dyV = findViewById(R.id.hot_re_dy_v);
        this.jjV = findViewById(R.id.hot_re_jj_v);
        this.zyV = findViewById(R.id.hot_re_zy_v);
        this.ztV = findViewById(R.id.hot_re_zt_v);

        this.dyV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.jjV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.zyV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.ztV.setOnFocusChangeListener(this.mOnFocusChangeListener);

        this.dyV.setOnClickListener(this.mOnClickListener);
        this.jjV.setOnClickListener(this.mOnClickListener);
        this.zyV.setOnClickListener(this.mOnClickListener);
        this.ztV.setOnClickListener(this.mOnClickListener);

        int bigTextSize = (int) AppMain.res().getDimension(R.dimen.hot_re_name_big_text_size);
        int smallTextSize = (int) AppMain.res().getDimension(R.dimen.hot_re_name_small_text_size);

        String mainTitleStr = AppMain.res().getString(R.string.movie);
        String hotReStr = AppMain.res().getString(R.string.hot_re);
        String[] hotReArr = hotReStr.split(" ");
        hotReStr = hotReArr.length == 2 ? hotReArr[1] : hotReStr;

        String titleStr = String.format("%s\n%s", mainTitleStr, hotReStr);
        int titleLen = titleStr.length();
        int bigLen = mainTitleStr.length();

        StyleSpan boldItalicSpan = new StyleSpan(Typeface.BOLD_ITALIC);
        StyleSpan italicSpan = new StyleSpan(Typeface.ITALIC);
        AbsoluteSizeSpan bigAbsSizeSpan = new AbsoluteSizeSpan(bigTextSize);
        AbsoluteSizeSpan smallAbsSizeSpan = new AbsoluteSizeSpan(smallTextSize);

        SpannableString spannableString = new SpannableString(titleStr);
        spannableString.setSpan(boldItalicSpan, 0, bigLen, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(italicSpan, bigLen, titleLen, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(bigAbsSizeSpan, 0, bigLen,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(smallAbsSizeSpan, bigLen, titleLen,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        dyV.setText(spannableString);

        mainTitleStr = AppMain.res().getString(R.string.jj);
        titleStr = String.format("%s\n%s", mainTitleStr, hotReStr);
        titleLen = titleStr.length();
        bigLen = mainTitleStr.length();

        spannableString = new SpannableString(titleStr);
        spannableString.setSpan(boldItalicSpan, 0, bigLen, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(italicSpan, bigLen, titleLen, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(bigAbsSizeSpan, 0, bigLen,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(smallAbsSizeSpan, bigLen, titleLen,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        jjV.setText(spannableString);

        mainTitleStr = AppMain.res().getString(R.string.zy);
        titleStr = String.format("%s\n%s", mainTitleStr, hotReStr);
        titleLen = titleStr.length();
        bigLen = mainTitleStr.length();

        spannableString = new SpannableString(titleStr);
        spannableString.setSpan(boldItalicSpan, 0, bigLen, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(italicSpan, bigLen, titleLen, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(bigAbsSizeSpan, 0, bigLen,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(smallAbsSizeSpan, bigLen, titleLen,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        this.zyV.setText(spannableString);

        mainTitleStr = AppMain.res().getString(R.string.zt);
        titleStr = String.format("%s\n%s", mainTitleStr, hotReStr);
        titleLen = titleStr.length();
        bigLen = mainTitleStr.length();

        spannableString = new SpannableString(titleStr);
        spannableString.setSpan(boldItalicSpan, 0, bigLen, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(italicSpan, bigLen, titleLen, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(bigAbsSizeSpan, 0, bigLen,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(smallAbsSizeSpan, bigLen, titleLen,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        this.ztV.setText(spannableString);

        this.exWidth = (int) AppMain.res().getDimension(R.dimen.hot_re_ex_width);

        this.dyV.post(new Runnable() {
            @Override
            public void run() {
                Utils.focusV(dyV, true);
            }
        });

        this.mOnDataListener = new MOnDataListener(this);
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
        DataCenter.Ins().addDataListener(this.mOnDataListener);
        DataCenter.Ins().scanTime();
        DataCenter.Ins().regReceiver(AppMain.ctx());
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
        DataCenter.Ins().unregReceiver(AppMain.ctx());
        DataCenter.Ins().delDataListener(this.mOnDataListener);
    }

    @Override
    protected void destroyAct() {
        if (this.mOnDataListener != null) {
            this.mOnDataListener.release();
            this.mOnDataListener = null;
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

    @Override
    FocusBorder createFocusBorder() {
        return new FocusBorder.Builder()
                .asDrawable()
                .borderResId(R.drawable.kind_item_f)
                .build(this);
    }

    @Override
    int layoutId() {
        return R.layout.hot_re_layout;
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private HotReAct hostCls;

        public MOnFocusChangeListener(HotReAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                if (this.hostCls != null) {
                    this.hostCls.focusBorderForV(v, 1.3f, 1.3f,
                            this.hostCls.exWidth, this.hostCls.exWidth);
                }
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private HotReAct hostCls;

        public MOnClickListener(HotReAct hostCls) {
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

            Intent intent = new Intent(this.hostCls, HotReDetailsAct.class);

            switch (v.getId()) {
                case R.id.hot_re_dy_v:
                    intent.putExtra(EXVAL.CID, EXVAL.DY_HOT_RE_CID);
                    break;
                case R.id.hot_re_jj_v:
                    intent.putExtra(EXVAL.CID, EXVAL.JJ_HOT_RE_CID);
                    break;
                case R.id.hot_re_zy_v:
                    intent.putExtra(EXVAL.CID, EXVAL.ZY_HOT_RE_CID);
                    break;
                case R.id.hot_re_zt_v:
                    intent.putExtra(EXVAL.CID, EXVAL.ZT_HOT_RE_CID);
                    break;
            }
            intent.putExtra(EXVAL.TITLE_TEXT, ((TextView) v).getText().toString().trim());
            this.hostCls.startActivity(intent);
        }
    }

    private static class MOnDataListener implements OnDataListener {

        private HotReAct hostCls;

        public MOnDataListener(HotReAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onLoadInitDatas(final SparseArray<HomeMovie> homeMovies) {

        }

        @Override
        public void onNetState(int netType, boolean isConnected) {
            if (this.hostCls == null) {
                return;
            }
            if (isConnected) {
                this.hostCls.netV.setVisibility(View.VISIBLE);
                switch (netType) {
                    case ConnectivityManager.TYPE_WIFI:
                        this.hostCls.netV.setBackgroundResource(R.drawable.wifi_ok);
                        break;
                    default:
                        this.hostCls.netV.setBackgroundResource(R.drawable.eth_f);
                        break;
                }
            } else {
                this.hostCls.netV.setVisibility(View.GONE);
            }
        }

        @Override
        public void onTimeDate(final String time) {
            if (this.hostCls == null) {
                return;
            }
            this.hostCls.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MOnDataListener.this.hostCls.timeV.setText(time);
                }
            });
        }

        @Override
        public void onUpdateApp(final File apkF, final String desStr) {
        }

        @Override
        public void onMarquee(final MarqueeParser marqueeParser) {

        }

        @Override
        public void onForceTv() {
        }

        @Override
        public void onGetDataOver() {

        }

        @Override
        public void onInitMediaOver() {
        }

        @Override
        public void onFavUpdate() {

        }

        @Override
        public void onChalList(LiveChalObj chal, boolean liveDataDBOK) {

        }

        @Override
        public void onLogin(String loginStr, boolean dialogCancelable) {

        }

        @Override
        public void onToken() {

        }

        @Override
        public void onPay(final boolean b, final String s) {

        }
    }
}
