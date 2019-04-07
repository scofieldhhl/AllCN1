package com.datas;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class CacheKindObj {

    @Id(autoincrement = true)
    private Long keyL;
    @NotNull
    @Index(unique = true)
    private String colName;
    @Transient
    private String colId;
    @Transient
    private int chalNum, pageNum, cKNum, pType;

    @Generated(hash = 864024572)
    public CacheKindObj(Long keyL, @NotNull String colName) {
        this.keyL = keyL;
        this.colName = colName;
    }

    @Generated(hash = 350990861)
    public CacheKindObj() {
    }

    @Override
    public String toString() {
        return String.format("CacheKind->{colName:%s}", colName);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null && (obj.getClass() == CacheKindObj.class)) {
            CacheKindObj kindObj = (CacheKindObj) obj;
            if (!TextUtils.isEmpty(kindObj.getColName()) && !TextUtils.isEmpty(colName) &&
                    kindObj.getColName().equals(colName)) {
                return true;
            }
        }

        return super.equals(obj);
    }

    public Long getKeyL() {
        return this.keyL;
    }

    public void setKeyL(Long keyL) {
        this.keyL = keyL;
    }

    public String getColName() {
        return this.colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColId() {
        return colId;
    }

    public void setColId(String colId) {
        this.colId = colId;
    }

    public int getChalNum() {
        return chalNum;
    }

    public void setChalNum(int chalNum) {
        this.chalNum = chalNum;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getcKNum() {
        return cKNum;
    }

    public void setcKNum(int cKNum) {
        this.cKNum = cKNum;
    }
}
