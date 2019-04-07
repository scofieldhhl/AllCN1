package com.allcn.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.LiveAct;
import com.allcn.utils.EXVAL;
import com.allcn.utils.MSG;
import com.mast.lib.utils.Utils;

public class OpDialog extends Dialog {

    private static final String TAG = OpDialog.class.getSimpleName();
    private TextView appNameV;
    private Button changeV, deleteV;
    private MOnClickListener mOnClickListener;
    private HorChalListV horChalListV;
    private LiveAct liveAct;
    private View view;

    public OpDialog(@NonNull Context context) {
        this(context, R.style.MenuDialog);
    }

    public OpDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public void initDatas(HorChalListV horChalListV, LiveAct liveAct, View view) {
        this.horChalListV = horChalListV;
        this.liveAct = liveAct;
        this.view = view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.op_dialog);

        mOnClickListener = new MOnClickListener(this);
        appNameV = findViewById(R.id.op_app_name);
        changeV = findViewById(R.id.op_change_btn);
        deleteV = findViewById(R.id.op_delete_btn);

        if (liveAct != null) {
            appNameV.setText(liveAct.selLiveChal.getIsFav() ? R.string.unfav_chal_tips : R.string.fav_chal_tips);
            changeV.setText(liveAct.selLiveChal.getIsFav() ? R.string.unfav : R.string.fav);
        }

        changeV.setOnClickListener(mOnClickListener);
        deleteV.setOnClickListener(mOnClickListener);

        changeV.post(new Runnable() {
            @Override
            public void run() {
                Utils.focusV(changeV, true);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mOnClickListener.release();
        mOnClickListener = null;
        changeV.setOnClickListener(null);
        deleteV.setOnClickListener(null);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (liveAct != null) {
            Utils.sendMsg(liveAct.mHandler, MSG.HIDE_LIST, EXVAL.SHOW_LIST_TIMEOUT);
        }
        horChalListV = null;
        view = null;
        liveAct = null;
    }

    private static class MOnClickListener implements View.OnClickListener {

        private OpDialog hostCls;

        public MOnClickListener(OpDialog hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {

            if (hostCls == null || hostCls.horChalListV == null) {
                return;
            }

            if (v == hostCls.changeV) {
                hostCls.horChalListV.handleChalInFavWrapper(true, hostCls.view);
            } else if (v == hostCls.deleteV) {
                hostCls.horChalListV.handleChalInFavWrapper(false, hostCls.view);
            }
            hostCls.dismiss();
        }
    }
}
