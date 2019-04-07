package com.datas;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity(indexes = {
        @Index(value = "cid, movieId, name", unique = true)
})
public class MovieObj implements Parcelable {

    @Id(autoincrement = true)
    private Long id;
    private String cid;
    private int movieId;
    private String duration;
    private String imgUrl;
    private String label;
    private String name;
    private String rate;
    private int year;
    private int type;
    private int playPos;
    private String js;

    @Generated(hash = 683408678)
    public MovieObj(Long id, String cid, int movieId, String duration, String imgUrl, String label, String name,
                    String rate, int year, int type, int playPos, String js) {
        this.id = id;
        this.cid = cid;
        this.movieId = movieId;
        this.duration = duration;
        this.imgUrl = imgUrl;
        this.label = label;
        this.name = name;
        this.rate = rate;
        this.year = year;
        this.type = type;
        this.playPos = playPos;
        this.js = js;
    }

    @Generated(hash = 948215210)
    public MovieObj() {
    }

    protected MovieObj(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        cid = in.readString();
        movieId = in.readInt();
        duration = in.readString();
        imgUrl = in.readString();
        label = in.readString();
        name = in.readString();
        rate = in.readString();
        year = in.readInt();
        type = in.readInt();
        playPos = in.readInt();
        js = in.readString();
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
        dest.writeInt(movieId);
        dest.writeString(duration);
        dest.writeString(imgUrl);
        dest.writeString(label);
        dest.writeString(name);
        dest.writeString(rate);
        dest.writeInt(year);
        dest.writeInt(type);
        dest.writeInt(playPos);
        dest.writeString(js);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieObj> CREATOR = new Creator<MovieObj>() {
        @Override
        public MovieObj createFromParcel(Parcel in) {
            return new MovieObj(in);
        }

        @Override
        public MovieObj[] newArray(int size) {
            return new MovieObj[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == MovieObj.class) {
            MovieObj movieObj = (MovieObj) obj;
            if (!TextUtils.isEmpty(name) && name.equals(movieObj.getName()) &&
                    (movieId == movieObj.getMovieId()) && (cid.equals(movieObj.getCid()))) {
                return true;
            }
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return String.format("Movie->[cid=%s movieId=%d duration=%s imgUrl=%s label=%s name=%s rate=%s year=%s playPos=%d]",
                cid, movieId, duration, imgUrl, label, name, rate, year, playPos);
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

    public int getMovieId() {
        return this.movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRate() {
        return this.rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPlayPos() {
        return this.playPos;
    }

    public void setPlayPos(int playPos) {
        this.playPos = playPos;
    }

    public String getJs() {
        return this.js;
    }

    public void setJs(String js) {
        this.js = js;
    }
}
