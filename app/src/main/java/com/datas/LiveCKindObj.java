package com.datas;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

@Entity(indexes = {
        @Index(value = "colId, colName, pColId, pColName", unique = true)
})
public class LiveCKindObj {

    @Id(autoincrement = true)
    private Long keyL;
    @NotNull
    private String colName, colId, pColName, pColId;
    private int chalNum, pageNum, pType;

    @Generated(hash = 1070399473)
    public LiveCKindObj(Long keyL, @NotNull String colName, @NotNull String colId, @NotNull String pColName,
            @NotNull String pColId, int chalNum, int pageNum, int pType) {
        this.keyL = keyL;
        this.colName = colName;
        this.colId = colId;
        this.pColName = pColName;
        this.pColId = pColId;
        this.chalNum = chalNum;
        this.pageNum = pageNum;
        this.pType = pType;
    }

    @Generated(hash = 56197026)
    public LiveCKindObj() {
    }

    @Override
    public String toString() {
        return String.format("LiveCKind->{colId:%s, colName:%s, pColId=%s, pColName=%s chalNum=%d pageNum=%d pType=%d}",
                colId, colName, pColId, pColName, chalNum, pageNum, pType);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null && (obj.getClass() == LiveCKindObj.class)) {
            LiveCKindObj kindObj = (LiveCKindObj) obj;
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
        return this.colId;
    }

    public void setColId(String colId) {
        this.colId = colId;
    }

    public String getPColName() {
        return this.pColName;
    }

    public void setPColName(String pColName) {
        this.pColName = pColName;
    }

    public String getPColId() {
        return this.pColId;
    }

    public void setPColId(String pColId) {
        this.pColId = pColId;
    }

    public int getChalNum() {
        return this.chalNum;
    }

    public void setChalNum(int chalNum) {
        this.chalNum = chalNum;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPType() {
        return this.pType;
    }

    public void setPType(int pType) {
        this.pType = pType;
    }
}
