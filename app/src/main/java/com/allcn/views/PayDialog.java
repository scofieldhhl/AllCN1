package com.allcn.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.allcn.R;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.MSG;
import com.mast.lib.utils.Utils;

public class PayDialog extends Dialog {

    private static final String TAG = PayDialog.class.getSimpleName();
    private EditText idV, psV;
    private Button okV, exitV;
    private MOnClickListener mOnClickListener;
    private Handler handler;

    public PayDialog(@NonNull Context context) {
        this(context, R.style.MenuDialog);
    }

    public PayDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_layout);

        mOnClickListener = new MOnClickListener(this);

        idV = findViewById(R.id.pay_id_edit_v);
        psV = findViewById(R.id.pay_ps_edit_v);
        okV = findViewById(R.id.pay_ok_v);
        exitV = findViewById(R.id.pay_exit_v);

        okV.setOnClickListener(mOnClickListener);
        exitV.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mOnClickListener != null) {
            mOnClickListener.release();
            mOnClickListener = null;
        }
        handler = null;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    private static class MOnClickListener implements View.OnClickListener {

        private PayDialog hostCls;

        public MOnClickListener(PayDialog hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.pay_ok_v:
                    String idStr = hostCls.idV.getText().toString().trim();
                    String psStr = hostCls.psV.getText().toString().trim();
                    DataCenter.Ins().pay(idStr, psStr);
                    AppMain.showToastTips(AppMain.res().getString(R.string.paying));
                    break;
                case R.id.pay_exit_v:
                    Utils.sendMsg(hostCls.handler, MSG.EXIT_APP);
                    hostCls.dismiss();
                    break;
            }
        }
    }

    public static PayDialog showPay(Context context, Handler handler) {
        PayDialog payDialog = new PayDialog(context);
        payDialog.setCancelable(false);
        payDialog.setHandler(handler);
        payDialog.show();
        payDialog.getWindow().setLayout(
                (int) AppMain.res().getDimension(R.dimen.pay_dialog_w),
                (int) AppMain.res().getDimension(R.dimen.pay_dialog_h));
        return payDialog;
    }
}
