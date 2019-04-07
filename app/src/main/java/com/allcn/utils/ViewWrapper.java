package com.allcn.utils;

import android.view.View;
import android.view.ViewGroup;

public class ViewWrapper {

    private View view;

    public ViewWrapper() {}

    public ViewWrapper(View view) {
        this.view = view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public View getView() {
        return this.view;
    }

    public void setWidth(int toWidth) {
        ViewGroup.LayoutParams layoutParams = this.view == null ? null : this.view.getLayoutParams();
        if (layoutParams != null) {
            this.view.getLayoutParams().width = toWidth;
        }
    }

    public int getWidth() {
        return this.view == null ? 0 : this.view.getLayoutParams().width;
    }

    public void release() {
        this.view = null;
    }
}
