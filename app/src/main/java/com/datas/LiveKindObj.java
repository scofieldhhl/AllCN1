package com.datas;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

@Entity(indexes = {
        @Index(value = "colId, colName", unique = true)
})
public class LiveKindObj {

    @Id(autoincrement = true)
    private Long keyL;
    @NotNull
    private String colName, colId;
    private boolean haveChildren;
    private int chalNum, pageNum, cKNum, pType;

    @Generated(hash = 90004245)
    public LiveKindObj(Long keyL, @NotNull String colName, @NotNull String colId, boolean haveChildren, int chalNum,
            int pageNum, int cKNum, int pType) {
        this.keyL = keyL;
        this.colName = colName;
        this.colId = colId;
        this.haveChildren = haveChildren;
        this.chalNum = chalNum;
        this.pageNum = pageNum;
        this.cKNum = cKNum;
        this.pType = pType;
    }

    @Generated(hash = 503742060)
    public LiveKindObj() {
    }

    @Override
    public String toString() {
        return String.format("LiveKind->{colId:%s, colName:%s haveChildren=%b chalNum=%d pageNum=%d cKNum=%d pType=%d}",
                colId, colName, haveChildren, chalNum, pageNum, cKNum, pType);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null && (obj.getClass() == LiveKindObj.class)) {
            LiveKindObj kindObj = (LiveKindObj) obj;
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

    public boolean getHaveChildren() {
        return this.haveChildren;
    }

    public void setHaveChildren(boolean haveChildren) {
        this.haveChildren = haveChildren;
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

    public int getCKNum() {
        return this.cKNum;
    }

    public void setCKNum(int cKNum) {
        this.cKNum = cKNum;
    }

    public int getPType() {
        return this.pType;
    }

    public void setPType(int pType) {
        this.pType = pType;
    }
}
