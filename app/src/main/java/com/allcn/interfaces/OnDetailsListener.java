package com.allcn.interfaces;

import com.datas.MovieDetailsObj;
import com.datas.MovieObj;

import java.util.List;

public interface OnDetailsListener {
    void onDetails(MovieDetailsObj movieDetailsObj, List<MovieObj> reMovieObjs, boolean isFav);
}
