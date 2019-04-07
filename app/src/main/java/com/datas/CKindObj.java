package com.datas;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity(indexes = {
        @Index(value = "cid, pCid, name", unique = true)
})
public class CKindObj implements Parcelable {

    @Id(autoincrement = true)
    private Long id;
    private String cid;
    private String pCid;
    private String name;
    private String imgUrl;
    private String bgUrl;
    private int type;


    @Generated(hash = 284376442)
    public CKindObj(Long id, String cid, String pCid, String name, String imgUrl,
                    String bgUrl, int type) {
        this.id = id;
        this.cid = cid;
        this.pCid = pCid;
        this.name = name;
        this.imgUrl = imgUrl;
        this.bgUrl = bgUrl;
        this.type = type;
    }

    @Generated(hash = 1835223835)
    public CKindObj() {
    }


    protected CKindObj(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        cid = in.readString();
        pCid = in.readString();
        name = in.readString();
        imgUrl = in.readString();
        bgUrl = in.readString();
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(cid);
        dest.writeString(pCid);
        dest.writeString(name);
        dest.writeString(imgUrl);
        dest.writeString(bgUrl);
        dest.writeInt(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CKindObj> CREATOR = new Creator<CKindObj>() {
        @Override
        public CKindObj createFromParcel(Parcel in) {
            return new CKindObj(in);
        }

        @Override
        public CKindObj[] newArray(int size) {
            return new CKindObj[size];
        }
    };

    @Override
    public String toString() {
        return String.format("CKind->[cid:%s, pCid:%s, name:%s]",
                cid, pCid, name);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null && (obj.getClass() == CKindObj.class)) {
            CKindObj kindObj = (CKindObj) obj;
            if (!TextUtils.isEmpty(kindObj.getName()) && !TextUtils.isEmpty(name) &&
                    kindObj.getName().equals(name)) {
                return true;
            }
        }

        return super.equals(obj);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCid() {
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPCid() {
        return this.pCid;
    }

    public void setPCid(String pCid) {
        this.pCid = pCid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBgUrl() {
        return this.bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
