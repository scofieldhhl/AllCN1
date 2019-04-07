package com.allcn.views;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.interfaces.ChalListWindow;
import com.allcn.utils.AppMain;
import com.datas.LiveChalObj;
import com.mast.lib.utils.MLog;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class LiveBottomEpgWindow implements ChalListWindow {

    private static final String TAG = LiveBottomEpgWindow.class.getSimpleName();
    private PopupWindow window;
    private boolean isShown;
    private RelativeLayout rootV;
    private TextView nameV, curEpgV, nextEpgV;
    private ImageView[] idImgVArr;
    private int[] idImgResIdArr = new int[]{
            R.drawable.live0,
            R.drawable.live1,
            R.drawable.live2,
            R.drawable.live3,
            R.drawable.live4,
            R.drawable.live5,
            R.drawable.live6,
            R.drawable.live7,
            R.drawable.live8,
            R.drawable.live9,
    };

    public LiveBottomEpgWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(AppMain.ctx());
        rootV = (RelativeLayout) layoutInflater.inflate(R.layout.live_bottom_epg, null,
                false);
        nameV = rootV.findViewById(R.id.live_epg_chal_name);
        curEpgV = rootV.findViewById(R.id.live_epg_cur_epg);
        nextEpgV = rootV.findViewById(R.id.live_epg_next_epg);
        this.idImgVArr = new ImageView[4];
        this.idImgVArr[0] = rootV.findViewById(R.id.live_id1);
        this.idImgVArr[1] = rootV.findViewById(R.id.live_id2);
        this.idImgVArr[2] = rootV.findViewById(R.id.live_id3);
        this.idImgVArr[3] = rootV.findViewById(R.id.live_id4);
//        nameV.setTypeface(Typeface.createFromAsset(AppMain.ctx().getAssets(), "fonts/SWZ721H.TTF"));
//        idV.setTypeface(Typeface.createFromAsset(AppMain.ctx().getAssets(), "fonts/SWZ721HI.TTF"));

        window = new PopupWindow(rootV, MATCH_PARENT,
                (int) AppMain.res().getDimension(R.dimen.live_epg_root_h));
        window.setTouchable(false);
        window.setFocusable(false);
    }

    public void loadData(LiveChalObj liveChalObj) {
        char[] chars = liveChalObj.getUiPos().toCharArray();
        int charNum = chars.length, i = 0;
        if (charNum == 1) {
            this.idImgVArr[0].setBackgroundResource(idImgResIdArr[0]);
            this.idImgVArr[1].setBackgroundResource(idImgResIdArr[chars[0] - 48]);
            this.idImgVArr[0].setVisibility(View.VISIBLE);
            this.idImgVArr[1].setVisibility(View.VISIBLE);
            i = 2;
        } else {
            for (; i < charNum; i++) {
                MLog.d(TAG, String.format("chars[%d]=%c", i, chars[i]));
                this.idImgVArr[i].setBackgroundResource(idImgResIdArr[chars[i] - 48]);
                this.idImgVArr[i].setVisibility(View.VISIBLE);
            }
        }
        for (; i < 4; i++) {
            this.idImgVArr[i].setVisibility(View.GONE);
        }
        if (this.nameV != null) {
            this.nameV.setText(liveChalObj == null ? "" : liveChalObj.getName());
        }
    }

    @Override
    public void show(View rootV) {
        if (window != null && !isShown) {
            isShown = true;
            window.showAtLocation(rootV, Gravity.BOTTOM, 0, 0);
            nameV.post(new Runnable() {
                @Override
                public void run() {
                    nameV.setSelected(true);
                    curEpgV.setSelected(true);
                    nextEpgV.setSelected(true);
                }
            });
        }
    }

    @Override
    public void dismiss() {
        if (window != null && window.isShowing()) {
            isShown = false;
            window.dismiss();
        }
    }

    public boolean isShown() {
        return isShown;
    }

    @Override
    public void relase() {
        dismiss();
        window = null;
        rootV = null;
        nameV = null;
        nextEpgV = null;
        curEpgV = null;
        this.idImgVArr = null;
        this.idImgResIdArr = null;
    }
}
