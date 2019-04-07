package com.allcn.interfaces;

import android.view.KeyEvent;
import android.view.View;

public interface OnPlayBackAdapterItemListener {
    void onItemSelected(View view, int position, int type);
    void onItemClicked(View view, int position, int type);
    void onItemKey(View view, int position, int keyCode, KeyEvent event, int type);
}
