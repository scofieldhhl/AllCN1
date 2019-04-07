package com.datas;

import android.support.annotation.Nullable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class ChalObj {

    @Id(autoincrement = true)
    private Long id;
    private int chalId, videoId, epgId;
    private String name, beginTime, endTime, filmId, date;

    @Generated(hash = 1404857657)
    public ChalObj(Long id, int chalId, int videoId, int epgId, String name, String beginTime,
                   String endTime, String filmId, String date) {
        this.id = id;
        this.chalId = chalId;
        this.videoId = videoId;
        this.epgId = epgId;
        this.name = name;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.filmId = filmId;
        this.date = date;
    }

    @Generated(hash = 1045827306)
    public ChalObj() {
    }

    public int getChalId() {
        return chalId;
    }

    public void setChalId(int chalId) {
        this.chalId = chalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFilmId() {
        return filmId;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    @Override
    public String toString() {
        return String.format("[Chal: chalId=%d name=%s beginTime=%s endTime=%s filmId=%s videoId=%d]",
                chalId, name, beginTime, endTime, filmId, videoId);
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj == this) {
//            MLog.d("ChalObj", "is small Obj");
            return true;
        }

        if (obj == null || (obj.getClass() != ChalObj.class)) {
//            MLog.e("ChalObj", "obj is err");
            return false;
        }

//        MLog.e("ChalObj", String.format("self videoId=%d obj videoId=%d", getVideoId(),
//                ((ChalObj) obj).getVideoId()));

        return getVideoId() == ((ChalObj) obj).getVideoId() ? true : super.equals(obj);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getEpgId() {
        return this.epgId;
    }

    public void setEpgId(int epgId) {
        this.epgId = epgId;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
