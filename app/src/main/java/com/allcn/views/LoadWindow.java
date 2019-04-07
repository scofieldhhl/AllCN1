package com.allcn.views;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.LiveAct;
import com.allcn.activities.PlayAct;
import com.allcn.interfaces.ChalListWindow;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class LoadWindow implements ChalListWindow {//播放时缓存时的动画

    private PopupWindow window;
    private ImageView loadImgV;
    private TextView loadSpeedV, tipsV;
    private boolean isShown, isFullScreen;
    private AnimationDrawable anim;
    private Activity mAct;
    private int topOffset, leftOffset;

    public LoadWindow(Activity mAct) {
        this.isFullScreen = true;
        this.mAct = mAct;
        View view = LayoutInflater.from(AppMain.ctx()).
                inflate(R.layout.load_layout, null, false);
        loadImgV = view.findViewById(R.id.load_img);
        loadSpeedV = view.findViewById(R.id.load_speed);
        tipsV = view.findViewById(R.id.load_tips);

        topOffset = (int) -AppMain.res().getDimension(R.dimen.load_window_top_offset);
        leftOffset = (int) AppMain.res().getDimension(R.dimen.load_window_left_offset);

        anim = (AnimationDrawable) loadImgV.getDrawable();

        window = new PopupWindow(view, WRAP_CONTENT, WRAP_CONTENT);
        window.setTouchable(false);
        window.setFocusable(false);
    }

    public boolean isShown() {
        return isShown;
    }

    public void updateLoadSpeed(String speedStr) {
        this.loadSpeedV.setText(speedStr);
    }

    public void setFullScreen(boolean fullScreen) {
        this.isFullScreen = fullScreen;
    }

    @Override
    public void show(View rootV) {

        if (this.isShown) {
            return;
        }

        if (this.mAct instanceof PlayAct) {
            if (!((PlayAct) mAct).isShowBottomProgress()) {
                DataCenter.Ins().execNetSpeed();
            }
        }/* else if (mAct instanceof PlayBackPlayAct) {
            if (!((PlayBackPlayAct) mAct).isShowBottomProgress()) {
                DataCenter.Ins().execNetSpeed();
            }
        }*/ else if (this.mAct instanceof LiveAct) {
            //this.tipsV.setVisibility(View.VISIBLE);
            DataCenter.Ins().execNetSpeed();
        }

        this.isShown = true;
        this.anim.start();

        if (this.isFullScreen) {
            this.window.showAtLocation(rootV, Gravity.CENTER, 0, 0);
        } else {
            this.window.showAtLocation(rootV, Gravity.CENTER, leftOffset, topOffset);
        }
    }

    @Override
    public void dismiss() {
        if (this.anim != null && this.anim.isRunning()) {
            this.anim.stop();
        }
        if (this.isShown) {
            this.isShown = false;
            if (this.mAct instanceof PlayAct) {
                if (!((PlayAct) this.mAct).isShowBottomProgress()) {
                    DataCenter.Ins().execNetSpeed();
                }
            }
            /*else if (mAct instanceof PlayBackPlayAct) {
                if (!((PlayBackPlayAct) mAct).isShowBottomProgress()) {
                    DataCenter.Ins().execNetSpeed();
                }
            }*/
            this.window.dismiss();
        }
    }

    public void setLoadImgVisibity(int visibity){
        if (loadImgV!=null)
            loadImgV.setVisibility(visibity);
    }

    @Override
    public void relase() {
        dismiss();
        this.window.setOnDismissListener(null);
        this.window = null;
        this.anim = null;
    }
}
