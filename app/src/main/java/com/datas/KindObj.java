package com.datas;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity(indexes = {
        @Index(value = "cid, name", unique = true)
})
public class KindObj {

    @Id(autoincrement = true)
    private Long id;
    private String cid;
    private String name;
    private int type;

    @Generated(hash = 88688192)
    public KindObj(Long id, String cid, String name, int type) {
        this.id = id;
        this.cid = cid;
        this.name = name;
        this.type = type;
    }

    @Generated(hash = 1021536231)
    public KindObj() {
    }

    @Override
    public String toString() {
        return String.format("Kind->[cid:%s, name:%s]", cid, name);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null && (obj.getClass() == KindObj.class)) {
            KindObj kindObj = (KindObj) obj;
            if (!TextUtils.isEmpty(kindObj.getName()) && !TextUtils.isEmpty(name) &&
                    kindObj.getName().equals(name)) {
                return true;
            }
        }

        return super.equals(obj);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCid() {
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
