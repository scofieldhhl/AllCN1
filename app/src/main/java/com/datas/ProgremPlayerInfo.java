package com.datas;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ProgremPlayerInfo {
    @Index(unique = true)
    private String colName;
    @NonNull
    @Index(unique = true)
    private String url;
    private int position;
    @Generated(hash = 198626549)
    public ProgremPlayerInfo(String colName, @NonNull String url, int position) {
        this.colName = colName;
        this.url = url;
        this.position = position;
    }
    @Generated(hash = 2059481129)
    public ProgremPlayerInfo() {
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public int getPosition() {
        return this.position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public String getColName() {
        return this.colName;
    }
    public void setColName(String colName) {
        this.colName = colName;
    }
}
