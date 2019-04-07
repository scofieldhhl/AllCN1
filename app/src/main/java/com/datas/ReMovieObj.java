package com.datas;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity(indexes = {
        @Index(value = "name, cid, movieId", unique = true)
})
public class ReMovieObj {

    @Id(autoincrement = true)
    private Long id;
    private String cid;
    private String casts, category, countries, directors, summary, year;
    private int movieId;
    private String imgUrl;
    private String name;

    @Generated(hash = 1751578579)
    public ReMovieObj(Long id, String cid, String casts, String category, String countries, String directors, String summary, String year,
                      int movieId, String imgUrl, String name) {
        this.id = id;
        this.cid = cid;
        this.casts = casts;
        this.category = category;
        this.countries = countries;
        this.directors = directors;
        this.summary = summary;
        this.year = year;
        this.movieId = movieId;
        this.imgUrl = imgUrl;
        this.name = name;
    }

    @Generated(hash = 43637404)
    public ReMovieObj() {
    }

    @Override
    public String toString() {
        return String.format("ReMovieObj->[name=%s imgUrl=%s casts=%s category=%s colId=%s countries=%s directors=%s movieId=%d years=%s]",
                name, imgUrl, casts, category, cid, countries, directors, movieId, year);
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

    public String getCasts() {
        return this.casts;
    }

    public void setCasts(String casts) {
        this.casts = casts;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCountries() {
        return this.countries;
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }

    public String getDirectors() {
        return this.directors;
    }

    public void setDirectors(String directors) {
        this.directors = directors;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getMovieId() {
        return this.movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
