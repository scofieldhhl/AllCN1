package com.datas;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Level {

    @Id
    private Long id;
    private int levelId, isDefault, price, status, movieId;
    private String name;
    private String cid, movieName;

    @Generated(hash = 2052006133)
    public Level(Long id, int levelId, int isDefault, int price, int status, int movieId,
            String name, String cid, String movieName) {
        this.id = id;
        this.levelId = levelId;
        this.isDefault = isDefault;
        this.price = price;
        this.status = status;
        this.movieId = movieId;
        this.name = name;
        this.cid = cid;
        this.movieName = movieName;
    }

    @Generated(hash = 723561372)
    public Level() {
    }

    @Override
    public String toString() {
        return String.format("Level->[id=%d isDefault=%d price=%d status=%d name=%s]",
                id, isDefault, price, status, name);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLevelId() {
        return this.levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public int getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMovieId() {
        return this.movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
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

    public String getMovieName() {
        return this.movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }
}
