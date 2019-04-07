package com.datas;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity(indexes = {
        @Index(value = "name, cid, movieId, filmId, pos", unique = true)
})
public class FilmID {

    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String cid;
    private int movieId;
    private String filmId;
    private int pos;

    @Generated(hash = 1040345224)
    public FilmID(Long id, String name, String cid, int movieId, String filmId,
            int pos) {
        this.id = id;
        this.name = name;
        this.cid = cid;
        this.movieId = movieId;
        this.filmId = filmId;
        this.pos = pos;
    }

    @Generated(hash = 1490010067)
    public FilmID() {
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

    public int getMovieId() {
        return this.movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getFilmId() {
        return this.filmId;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPos() {
        return this.pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

}
