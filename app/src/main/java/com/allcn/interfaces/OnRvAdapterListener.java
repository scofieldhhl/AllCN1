package com.allcn.interfaces;

import android.view.KeyEvent;
import android.view.View;

public interface OnRvAdapterListener {
    void onItemSelected(View view, int position);

    void onItemKey(View view, int position, KeyEvent event, int keyCode);

    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);

    void release();
}
