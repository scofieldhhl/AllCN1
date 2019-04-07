package com.allcn.interfaces;

import java.util.List;

public interface OnSearchListener {
    void onInitSearchResult(List<Object> datas, int searchTotalNum);

    void onSearchResultForPage(List<Object> datas);
}
