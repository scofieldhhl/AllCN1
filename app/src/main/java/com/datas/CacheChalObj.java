package com.datas;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

@Entity(indexes = {
        @Index(value = "name, colName, colId", unique = true)
})
public class CacheChalObj {

    @Id(autoincrement = true)
    private Long keyL;
    @NotNull
    private String name, colName, colId;

    @Generated(hash = 1110912141)
    public CacheChalObj(Long keyL, @NotNull String name, @NotNull String colName,
            @NotNull String colId) {
        this.keyL = keyL;
        this.name = name;
        this.colName = colName;
        this.colId = colId;
    }

    @Generated(hash = 235198941)
    public CacheChalObj() {
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null) {
            CacheChalObj chalObj = (CacheChalObj) obj;
            if (!TextUtils.isEmpty(chalObj.getName()) && !TextUtils.isEmpty(name) &&
                    chalObj.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("CacheChal->{name=%s colName=%s colId=%s}", name, colName, colId);
    }

    public Long getKeyL() {
        return this.keyL;
    }

    public void setKeyL(Long keyL) {
        this.keyL = keyL;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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
}
