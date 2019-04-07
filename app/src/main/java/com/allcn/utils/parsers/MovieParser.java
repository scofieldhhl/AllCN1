package com.allcn.utils.parsers;

import android.text.TextUtils;
import android.util.Log;

import com.datas.FilmID;
import com.datas.Level;
import com.datas.MovieDetailsObj;
import com.datas.MovieObj;
import com.datas.ReMovieObj;
import com.db.cls.DBMgr;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.mast.lib.interfaces.Parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MovieParser implements Parser {

    private static final String TAG = MovieParser.class.getSimpleName();
    private MovieDetailsObj movieDetails;
    private String imgFmt, imgFmt1, hostStr;
    private MovieObj movieObj;
    private int filmIdNum/*, filmIdPageNum*/;
    private List<ReMovieObj> reMovieObjs;

    public MovieParser(String imgFmt, String imgFmt1, String hostStr, MovieObj movieObj) {
        this.imgFmt = imgFmt;
        this.imgFmt1 = imgFmt1;
        this.hostStr = hostStr;
        this.movieObj = movieObj;
        movieDetails = new MovieDetailsObj();
    }

    public MovieDetailsObj getMovieDetails() {
        return movieDetails;
    }

    @Override
    public void parse(String jsonStr) {

        if (TextUtils.isEmpty(jsonStr)) {
            movieDetails = null;
            return;
        }

        boolean isEnd = false;
        long sL = System.currentTimeMillis();
        StringReader stringReader = new StringReader(jsonStr);
        JsonReader jsonReader = new JsonReader(stringReader);
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                JsonToken token = jsonReader.peek();
                if (JsonToken.NAME.equals(token)) {
                    String key = jsonReader.nextName();
                    if (key.equals("data")) {
                        movieDetails = parseDataObj(jsonReader);
                        if (movieDetails != null) {
                            movieDetails.setImgUrl(movieObj.getImgUrl());
                            movieDetails.setName(movieObj.getName());
                            movieDetails.setRate(movieObj.getRate());
                            movieDetails.setLabel(movieObj.getLabel());
                            movieDetails.setDuration(movieObj.getDuration());
                            movieDetails.setMovieId(movieObj.getMovieId());
                            movieDetails.setFilmIdNum(filmIdNum);
//                            movieDetails.setFilmIdPageNum(filmIdPageNum);
//                            MLog.d(TAG, String.format("filmIdNum = %d filmIdPageNum = %d", filmIdNum, filmIdPageNum));
                        }
                    } else {
                        jsonReader.skipValue();
                    }
                } else if (JsonToken.END_OBJECT.equals(token)) {
                    isEnd = true;
                    jsonReader.endObject();
                }
            }
            DBMgr.Ins().insertMovieDetails(movieDetails);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!isEnd) {
                try {
                    jsonReader.endObject();
                } catch (Exception e) {
                }
            }
            try {
                jsonReader.close();
            } catch (Exception e) {
            }
            try {
                stringReader.close();
            } catch (Exception e) {
            }
        }
        long eL = System.currentTimeMillis();
        Log.e(TAG, "parse: "+(sL - eL) );
    }

    private MovieDetailsObj parseDataObj(JsonReader jsonReader) {

        boolean isEnd = false;
        MovieDetailsObj movieDetailsObj = new MovieDetailsObj();
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                JsonToken token = jsonReader.peek();
                if (JsonToken.NAME.equals(token)) {
                    String key = jsonReader.nextName();
                    if (key.equals("bought")) {
                        movieDetailsObj.setBought(jsonReader.nextBoolean());
                    } else if (key.equals("casts")) {
                        movieDetailsObj.setCasts(jsonReader.nextString());
                    } else if (key.equals("category")) {
                        movieDetailsObj.setCategory(jsonReader.nextString());
                    } else if (key.equals("colId")) {
                        movieDetailsObj.setCid(String.valueOf(jsonReader.nextInt()));
                    } else if (key.equals("countries")) {
                        movieDetailsObj.setCountries(jsonReader.nextString());
                    } else if (key.equals("directors")) {
                        movieDetailsObj.setDirectors(jsonReader.nextString());
                    }/* else if (key.equals("id")) {
                        movieDetailsObj.setMovieId(jsonReader.nextInt());
                    }*/ else if (key.equals("level")) {
                        parseLevelObj(jsonReader);
                    }/* else if (key.equals("recommend")) {
                        parseRecommendArr(jsonReader);
                    }*/ else if (key.equals("summary")) {
                        movieDetailsObj.setSummary(jsonReader.nextString());
                    } else if (key.equals("video")) {
                        parseVideoArr(jsonReader);
                    } else if (key.equals("years")) {
                        movieDetailsObj.setYear(jsonReader.nextString());
                    } else {
                        jsonReader.skipValue();
                    }
                } else if (JsonToken.END_OBJECT.equals(token)) {
                    jsonReader.endObject();
                    isEnd = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            movieDetailsObj = null;
        } finally {
            if (!isEnd) {
                try {
                    jsonReader.endObject();
                } catch (Exception e) {
                }
            }
        }
        return movieDetailsObj;
    }

    private void parseRecommendArr(JsonReader jsonReader) {

        boolean isEnd = false;
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                JsonToken token = jsonReader.peek();
                if (JsonToken.BEGIN_OBJECT.equals(token)) {
                    ReMovieObj reMovieObj = parseRecommendItemObj(jsonReader);
                    if (reMovieObj != null) {
                        reMovieObjs.add(reMovieObj);
                    }
                } else if (JsonToken.END_ARRAY.equals(token)) {
                    jsonReader.endArray();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!isEnd) {
                try {
                    jsonReader.endArray();
                } catch (IOException e) {}
            }
        }

    }

    private ReMovieObj parseRecommendItemObj(JsonReader jsonReader) {

        boolean isEnd = false;
        ReMovieObj reMovieObj = new ReMovieObj();
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                JsonToken token = jsonReader.peek();
                if (JsonToken.NAME.equals(token)) {
                    String key = jsonReader.nextName();
                    if (key.equals("colId")) {
                        reMovieObj.setCid(String.valueOf(jsonReader.nextInt()));
                    } else if (key.equals("id")) {
                        reMovieObj.setMovieId(jsonReader.nextInt());
                    } else if (key.equals("img")) {
                        String img = jsonReader.nextString();
                        if (!TextUtils.isEmpty(img)) {
                            if (img.startsWith(imgFmt1)) {
                                img = String.format(imgFmt, hostStr, "", img);
                            } else {
                                img = String.format(imgFmt, hostStr, imgFmt1, img);
                            }
                            reMovieObj.setImgUrl(img);
                        }
                    } else if (key.equals("name")) {
                        reMovieObj.setName(jsonReader.nextString());
                    } else {
                        jsonReader.skipValue();
                    }
                } else if (JsonToken.END_OBJECT.equals(token)) {
                    jsonReader.endObject();
                    isEnd = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            reMovieObj = null;
        } finally {
            if (!isEnd) {
                try {
                    jsonReader.endObject();
                } catch (Exception e) {}
            }
        }
        return reMovieObj;
    }

    private void parseVideoArr(JsonReader jsonReader) {

        boolean isEnd = false;
        List<FilmID> filmIds = new ArrayList<>();
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                JsonToken token = jsonReader.peek();
                if (JsonToken.BEGIN_OBJECT.equals(token)) {
                    FilmID filmID = parseVideoObj(jsonReader);
                    if (filmID != null) {
                        filmID.setPos(filmIdNum++);
                        filmIds.add(filmID);
                    }
                } else if (JsonToken.END_ARRAY.equals(token)) {
                    isEnd = true;
                    jsonReader.endArray();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            filmIds = null;
        } finally {
            if (!isEnd) {
                try {
                    jsonReader.endArray();
                } catch (Exception e) {
                }
            }
        }

//        filmIdNum = filmIds.size();
//        MLog.d(TAG, String.format("filmid num = %d", filmIdNum));
        if (filmIds != null && filmIdNum > 0) {
//            filmIdPageNum = filmIdNum / EXVAL.COLLECTION_NUM_IN_LINE;
//            if (filmIdNum % EXVAL.COLLECTION_NUM_IN_LINE != 0) {
//                filmIdPageNum++;
//            }
            DBMgr.Ins().insertFilmIds(filmIds, filmIdNum);
        }
    }

    private FilmID parseVideoObj(JsonReader jsonReader) {

        boolean isEnd = false;
        FilmID filmID = new FilmID();
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                JsonToken token = jsonReader.peek();
                if (JsonToken.NAME.equals(token)) {
                    String key = jsonReader.nextName();
                    if (key.equals("filmId")) {
                        filmID.setCid(movieObj.getCid());
                        filmID.setFilmId(jsonReader.nextString());
                        filmID.setMovieId(movieObj.getMovieId());
                        filmID.setName(movieObj.getName());
                    } else {
                        jsonReader.skipValue();
                    }
                } else if (JsonToken.END_OBJECT.equals(token)) {
                    isEnd = true;
                    jsonReader.endObject();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            filmID = null;
        } finally {
            if (!isEnd) {
                try {
                    jsonReader.endObject();
                } catch (Exception e) {
                }
            }
        }
        return filmID;
    }

    private void parseLevelObj(JsonReader jsonReader) {

        boolean isEnd = false;
        Level level = new Level();
        level.setCid(movieObj.getCid());
        level.setMovieId(movieObj.getMovieId());
        level.setMovieName(movieObj.getName());
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                JsonToken token = jsonReader.peek();
                if (JsonToken.NAME.equals(token)) {
                    String key = jsonReader.nextName();
                    if (key.equals("id")) {
                        level.setLevelId(jsonReader.nextInt());
                    } else if (key.equals("isDefault")) {
                        level.setIsDefault(jsonReader.nextInt());
                    } else if (key.equals("name")) {
                        level.setName(jsonReader.nextString());
                    } else if (key.equals("price")) {
                        level.setPrice(jsonReader.nextInt());
                    } else if (key.equals("status")) {
                        level.setStatus(jsonReader.nextInt());
                    } else {
                        jsonReader.skipValue();
                    }
                } else if (JsonToken.END_OBJECT.equals(token)) {
                    isEnd = true;
                    jsonReader.endObject();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            level = null;
        } finally {
            if (!isEnd) {
                try {
                    jsonReader.endObject();
                } catch (Exception e) {
                }
            }
        }
        if (level != null) {
            DBMgr.Ins().insertLevel(level);
        }
    }

    @Override
    public void release() {
        movieDetails = null;
    }

    public List<ReMovieObj> getReMovieObjs() {
        return reMovieObjs;
    }
}
