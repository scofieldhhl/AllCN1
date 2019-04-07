package com.allcn.views.decorations;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allcn.R;
import com.allcn.utils.AppMain;

public class PlayBackChalDecorations extends RecyclerView.ItemDecoration {

    private int bottomOffset;

    public PlayBackChalDecorations() {
        this.bottomOffset = (int) AppMain.res().getDimension(R.dimen.playback_chal_bottom_offset);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom += this.bottomOffset;
    }
}
