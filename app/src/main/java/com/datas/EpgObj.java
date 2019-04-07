package com.datas;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.db.converters.ListStrConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.List;

@Entity(indexes = {
        @Index(value = "epgId, name", unique = true)
})
public class EpgObj implements Parcelable {

    @Id(autoincrement = true)
    private Long id;
    private int epgId;
    private String name;
    @Convert(converter = ListStrConverter.class, columnType = String.class)
    private List<String> dates;

    public EpgObj() {
    }

    public EpgObj(int epgId, String name) {
        this.epgId = epgId;
        this.name = name;
    }

    @Generated(hash = 1974744890)
    public EpgObj(Long id, int epgId, String name, List<String> dates) {
        this.id = id;
        this.epgId = epgId;
        this.name = name;
        this.dates = dates;
    }

    protected EpgObj(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        epgId = in.readInt();
        name = in.readString();
        dates = in.createStringArrayList();
    }

    public static final Creator<EpgObj> CREATOR = new Creator<EpgObj>() {
        @Override
        public EpgObj createFromParcel(Parcel in) {
            return new EpgObj(in);
        }

        @Override
        public EpgObj[] newArray(int size) {
            return new EpgObj[size];
        }
    };

    public int getEpgId() {
        return epgId;
    }

    public void setEpgId(int epgId) {
        this.epgId = epgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("[Epg: epgId=%d name=%s]", epgId, name);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getDates() {
        return this.dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeInt(epgId);
        dest.writeString(name);
        dest.writeStringList(dates);
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj == this) {
            return true;
        }

        if (obj == null || (obj.getClass() != EpgObj.class)) {
            return false;
        }

        return getEpgId() == ((EpgObj) obj).getEpgId() ? true : super.equals(obj);
    }

    public static class ProgremPlayerInfo {
        private String colName;
        private String url;
        private int position;
    }
}
