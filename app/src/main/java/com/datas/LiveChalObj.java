package com.datas;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

@Entity(indexes = {
        @Index(value = "name, vodId, colName, colId", unique = true)
})
public class LiveChalObj {

    @Id(autoincrement = true)
    private Long keyL;
    private int listPos, pType;
    private String uiPos;
    @NotNull
    private String name, filmId, vodId, colName, colId;
    private boolean isFav;

    @Generated(hash = 998719280)
    public LiveChalObj(Long keyL, int listPos, int pType, String uiPos, @NotNull String name, @NotNull String filmId,
            @NotNull String vodId, @NotNull String colName, @NotNull String colId, boolean isFav) {
        this.keyL = keyL;
        this.listPos = listPos;
        this.pType = pType;
        this.uiPos = uiPos;
        this.name = name;
        this.filmId = filmId;
        this.vodId = vodId;
        this.colName = colName;
        this.colId = colId;
        this.isFav = isFav;
    }

    @Generated(hash = 1334634826)
    public LiveChalObj() {
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null) {
            LiveChalObj chalObj = (LiveChalObj) obj;
            if (!TextUtils.isEmpty(chalObj.getName()) && !TextUtils.isEmpty(name) &&
                    chalObj.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("LiveChal->{uiPos=%s listPos=%d name=%s filmId=%s vodId=%s colName=%s colId=%s isFav=%b pType=%d}",
                uiPos, listPos, name, filmId, vodId, colName, colId, isFav, pType);
    }

    public Long getKeyL() {
        return this.keyL;
    }

    public void setKeyL(Long keyL) {
        this.keyL = keyL;
    }

    public int getListPos() {
        return this.listPos;
    }

    public void setListPos(int listPos) {
        this.listPos = listPos;
    }

    public String getUiPos() {
        return this.uiPos;
    }

    public void setUiPos(String uiPos) {
        this.uiPos = uiPos;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilmId() {
        return this.filmId;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }

    public String getVodId() {
        return this.vodId;
    }

    public void setVodId(String vodId) {
        this.vodId = vodId;
    }

    public String getColName() {
        return this.colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColId() {
        return this.colId;
    }

    public void setColId(String colId) {
        this.colId = colId;
    }

    public boolean getIsFav() {
        return this.isFav;
    }

    public void setIsFav(boolean isFav) {
        this.isFav = isFav;
    }

    public int getPType() {
        return this.pType;
    }

    public void setPType(int pType) {
        this.pType = pType;
    }
}
