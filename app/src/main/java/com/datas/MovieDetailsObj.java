package com.datas;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity(indexes = {
        @Index(value = "cid, movieId, name", unique = true)
})
public class MovieDetailsObj {

    @Id(autoincrement = true)
    private Long id;
    private boolean bought;
    private String cid, name, imgUrl, casts, category, countries, directors, summary, year,
            rate, label, duration;
    private int movieId, filmIdNum, filmIdPageNum;

    @Generated(hash = 227960576)
    public MovieDetailsObj(Long id, boolean bought, String cid, String name, String imgUrl, String casts, String category,
            String countries, String directors, String summary, String year, String rate, String label, String duration, int movieId,
            int filmIdNum, int filmIdPageNum) {
        this.id = id;
        this.bought = bought;
        this.cid = cid;
        this.name = name;
        this.imgUrl = imgUrl;
        this.casts = casts;
        this.category = category;
        this.countries = countries;
        this.directors = directors;
        this.summary = summary;
        this.year = year;
        this.rate = rate;
        this.label = label;
        this.duration = duration;
        this.movieId = movieId;
        this.filmIdNum = filmIdNum;
        this.filmIdPageNum = filmIdPageNum;
    }

    @Generated(hash = 2037649182)
    public MovieDetailsObj() {
    }

    @Override
    public String toString() {
        return String.format("MovieDetails->[bought=%b casts=%s category=%s cid=%s countries=%s directors=%s id=%d years=%s]",
                bought, casts, category, cid, countries, directors, id, year);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getBought() {
        return this.bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public String getCid() {
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getFilmIdNum() {
        return this.filmIdNum;
    }

    public void setFilmIdNum(int filmIdNum) {
        this.filmIdNum = filmIdNum;
    }

    public String getRate() {
        return this.rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getFilmIdPageNum() {
        return this.filmIdPageNum;
    }

    public void setFilmIdPageNum(int filmIdPageNum) {
        this.filmIdPageNum = filmIdPageNum;
    }
}
