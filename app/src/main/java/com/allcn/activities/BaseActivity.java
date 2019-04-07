package com.allcn.activities;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allcn.R;
import com.allcn.views.focus.FocusBorder;
import com.blankj.utilcode.util.AdaptScreenUtils;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    protected boolean execDestroyed, execStoped;
    private FocusBorder mFocusBorder;//

    public static final long CHECK_BREAK_VALID_TIME = 2000;

    protected long checkBackTime;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId());
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        mFocusBorder = createFocusBorder();
        findView();
        eventView();
    }

    protected abstract void eventView();

    protected abstract void findView();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        execStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        execDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged");
    }

    @Override
    public Resources getResources() {
        return AdaptScreenUtils.adaptWidth(super.getResources(), 1280);
    }

    abstract int layoutId();

    protected abstract void stopAct();

    protected abstract void destroyAct();

    protected void execStop() {
        if (this.execStoped) {
            return;
        }
        this.execStoped = true;
        stopAct();
    }

    protected void execDestroy() {
        if (this.execDestroyed) {
            return;
        }
        this.execDestroyed = true;
        MobclickAgent.onKillProcess(this);
        this.mFocusBorder = null;
        destroyAct();
    }

    abstract FocusBorder createFocusBorder();


    public void focusBorderForV(View view) {
        if (this.mFocusBorder != null) {
            this.mFocusBorder.onFocus(view, FocusBorder.OptionsFactory.get(), false);
        }
    }

    public void focusBorderForV(View view, float scaleX, float scaleY, int exWidth, int exHeight) {
        if (this.mFocusBorder != null) {
            this.mFocusBorder.onFocus(view, FocusBorder.OptionsFactory.get(scaleX, scaleY,
                    exWidth, exHeight), false);
        }
    }

    public void focusBorderForV(View view, float scaleX, float scaleY, int exWidth, int exHeight,
                                int freeWidth, int freeHeight) {
        if (this.mFocusBorder != null) {
            this.mFocusBorder.onFocus(view, FocusBorder.OptionsFactory.get(scaleX, scaleY,
                    exWidth, exHeight, freeWidth, freeHeight), false);
        }
    }

    public void focusBorderForV(View view, float scaleX, float scaleY) {
        if (this.mFocusBorder != null) {
            this.mFocusBorder.onFocus(view, FocusBorder.OptionsFactory.get(scaleX, scaleY),
                    false);
        }
    }

    public void focusBorderForV(View view, int exWidth, int exHeight) {
        if (this.mFocusBorder != null) {
            this.mFocusBorder.onFocus(view, FocusBorder.OptionsFactory.get(exWidth, exHeight),
                    false);
        }
    }

    public void focusBorderForVWHXY(View view, int exWidth, int exHeight, int freeX, int freeY) {
        if (this.mFocusBorder != null) {
            this.mFocusBorder.onFocus(view, FocusBorder.OptionsFactory.get(exWidth, exHeight, freeX,
                    freeY), false);
        }
    }

    public void focusBorderForV(View view, int exWidth, int exHeight, int freeWidth, int freeHeight) {
        if (this.mFocusBorder != null) {
            this.mFocusBorder.onFocus(view, FocusBorder.OptionsFactory.get(exWidth, exHeight,
                    freeWidth, freeHeight), false);
        }
    }

    public void focusBorderForVIgnoreOld(View view) {
        if (this.mFocusBorder != null) {
            this.mFocusBorder.onFocus(view, FocusBorder.OptionsFactory.get(), true);
        }
    }

    public void focusBorderForVIgnoreOld(View view, float scaleX, float scaleY, int exWidth,
                                         int exHeight) {
        if (this.mFocusBorder != null) {
            this.mFocusBorder.onFocus(view, FocusBorder.OptionsFactory.get(scaleX, scaleY,
                    exWidth, exHeight), true);
        }
    }

    public void focusBorderForVIgnoreOld(View view, float scaleX, float scaleY) {
        if (this.mFocusBorder != null) {
            this.mFocusBorder.onFocus(view, FocusBorder.OptionsFactory.get(scaleX, scaleY),
                    true);
        }
    }

    public void focusBorderForVIgnoreOld(View view, int exWidth, int exHeight) {
        if (this.mFocusBorder != null) {
            this.mFocusBorder.onFocus(view, FocusBorder.OptionsFactory.get(exWidth, exHeight),
                    true);
        }
    }

    public void focusBorderForVIgnoreOldWHXY(View view, int exWidth, int exHeight, int freeX, int freeY) {
        if (this.mFocusBorder != null) {
            this.mFocusBorder.onFocus(view, FocusBorder.OptionsFactory.getWHXY(exWidth, exHeight,
                    freeX, freeY), true);
        }
    }

    public void focusBorderVisible(boolean visible) {
        MLog.d(TAG, String.format("focusBorderVisible visible=%b", visible));
        if (this.mFocusBorder != null) {
            this.mFocusBorder.setVisible(visible);
        }
    }

    public void resetFocusBorder() {
        if (this.mFocusBorder != null) {
            this.mFocusBorder.reset();
        }
    }

    public void changeFocusBorder(FocusBorder focusBorder) {
        this.mFocusBorder = focusBorder;
    }

    /**
     * 将Toast封装在一个方法中，在其它地方使用时直接输入要弹出的内容即可
     */
     public void toastMessage(/*String titles, */String messages) {
         //LayoutInflater的作用：对于一个没有被载入或者想要动态载入的界面，都需要LayoutInflater.inflate()来载入，LayoutInflater是用来找res/layout/下的xml布局文件，并且实例化
         LayoutInflater inflater = getLayoutInflater();//调用Activity的getLayoutInflater()
         View view = inflater.inflate(R.layout.layout_toast, null);//加載layout下的布局
         //ImageView iv = view.findViewById(R.id.tvImageToast);
         //iv.setImageResource(R.mipmap.atm);//显示的图片
         //TextView title = view.findViewById(R.id.tvTitleToast);
         //title.setText(titles); //toast的标题
         TextView text = view.findViewById(R.id.msg_v);
         text.setText(messages); //toast内容
         Toast toast = new Toast(getApplicationContext());
         toast.setGravity(Gravity.CENTER, 12, 20);//setGravity用来设置Toast显示的位置，相当于xml中的android:gravity或android:layout_gravity
         toast.setDuration(Toast.LENGTH_SHORT);//setDuration方法：设置持续时间，以毫秒为单位。该方法是设置补间动画时间长度的主要方法
         toast.setView(view); //添加视图文件
         toast.show();
     }

}
