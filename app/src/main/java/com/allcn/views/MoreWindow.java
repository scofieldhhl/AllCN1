package com.allcn.views;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.interfaces.ChalListWindow;
import com.allcn.utils.AppMain;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MoreWindow implements ChalListWindow {

    private TextView moreV;
    private PopupWindow window;

    public MoreWindow(boolean isSedm) {
        View rootV = LayoutInflater.from(AppMain.ctx()).inflate(R.layout.more_layout, null);
        rootV.setBackgroundResource(isSedm ? R.drawable.sedm_main_bg : R.drawable.topics_bg);
        moreV = rootV.findViewById(R.id.more_v);
        if (isSedm) {
            moreV.setTextColor(AppMain.res().getColor(R.color.sedm_movie_details_text_red_color));
        }
        window = new PopupWindow(rootV, MATCH_PARENT, MATCH_PARENT);
        window.setTouchable(true);
        window.setFocusable(true);
    }

    public void loadMoreText(String text) {
        moreV.setText(text);
    }

    @Override
    public void show(View rootV) {
        window.showAtLocation(rootV, Gravity.CENTER, 0, 0);
    }

    @Override
    public void dismiss() {
        window.dismiss();
    }

    @Override
    public void relase() {
        window = null;
        moreV = null;
    }
}
