package com.allcn.utils.parsers;

import android.text.TextUtils;

import com.allcn.R;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.datas.ChalObj;
import com.datas.EpgObj;
import com.db.cls.DBMgr;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.mast.lib.utils.MLog;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChalParser {

    private static final String TAG = ChalParser.class.getSimpleName();
    private int epgId;
    private List<String> dates;
    private DataCenter hostCls;
    private DateSort dateSort;
    private SimpleDateFormat dateSdf;
    private SimpleDateFormat weekSdf;
    private EpgObj epgObj;

    public ChalParser(DataCenter hostCls, EpgObj epgObj) {
        this.hostCls = hostCls;
        this.epgObj = epgObj;
        this.dateSort = new DateSort();
        this.dateSdf = new SimpleDateFormat("yyyy-MM-dd");
        this.weekSdf = new SimpleDateFormat(AppMain.res().getString(R.string.playback_week_fmt));
    }

    public void release() {
        this.hostCls = null;
        this.dateSort = null;
    }

    public void parse(String jsonStr) {

        this.dates = new ArrayList<>();

        if (TextUtils.isEmpty(jsonStr)) {
            return;
        }

        StringReader stringReader = new StringReader(jsonStr);
        JsonReader jsonReader = new JsonReader(stringReader);
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                JsonToken token = jsonReader.peek();
                MLog.d(TAG, "token " + token);
                if (JsonToken.NAME.equals(token)) {
                    String key = jsonReader.nextName();
                    if (key.equals("data")) {
                        parseData(jsonReader);
                    } else {
                        jsonReader.skipValue();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                jsonReader.endObject();
            } catch (Exception e) {
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
    }

    private void parseData(JsonReader jsonReader) {
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                parseDataObj(jsonReader);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                jsonReader.endArray();
            } catch (Exception e) {
            }
        }

        Collections.sort(dates, dateSort);

        MLog.d(TAG, String.format("epgId=%d dateNum=%d", epgId, dates.size()));

//        int dateNum = dates.size();
//
//        for (int i = 0; i < dateNum; i++) {
//            String dateStr = dates.get(i);
//            Date date = null;
//            try {
//                date = this.dateSdf.parse(dateStr);
//            } catch (ParseException e) {
//            }
//            String weekStr = this.weekSdf.format(date);
//            dates.set(i, weekStr);
//        }

        this.epgObj.setDates(dates);
        EpgObj epgObj = DBMgr.Ins().getEpg(epgId);
        if (epgObj != null) {
            MLog.d(TAG, "update " + epgObj.toString());
            epgObj.setDates(dates);
            DBMgr.Ins().updateEpg(epgObj);
        }
    }

    private void parseDataObj(JsonReader jsonReader) {
        String name = null;
        List<ChalObj> chals = null;
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                JsonToken token = jsonReader.peek();
                if (JsonToken.NAME.equals(token)) {
                    String key = jsonReader.nextName();
                    if (key.equals("id")) {
                        epgId = jsonReader.nextInt();
                    } else if (key.equals("name")) {
                        name = jsonReader.nextString();
                        if (!TextUtils.isEmpty(name)) {
                            dates.add(name);
                        }
                    } else if (key.equals("programs")) {
                        chals = parsePrograms(jsonReader, name);
                    } else {
                        jsonReader.skipValue();
                    }
                } else {
                    jsonReader.skipValue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                jsonReader.endObject();
            } catch (Exception e) {
            }
        }

        if (chals != null) {
            DBMgr.Ins().insertPlayBackEpgChals(chals, chals.size());
        }
    }

    private List<ChalObj> parsePrograms(JsonReader jsonReader, String date) {

        List<ChalObj> chals = new ArrayList<>();
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                ChalObj chalObj = parseProgramItem(jsonReader);
                if (chalObj != null) {
//                    chalObj.setEpgId(epgId);
                    chalObj.setEpgId(epgObj.getEpgId());
                    chalObj.setDate(date);
                    chals.add(chalObj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                jsonReader.endArray();
            } catch (Exception e) {
            }
        }

        return chals;
    }

    private ChalObj parseProgramItem(JsonReader jsonReader) {
        ChalObj chalObj = new ChalObj();
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                JsonToken token = jsonReader.peek();
                if (JsonToken.NAME.equals(token)) {
                    String key = jsonReader.nextName();
                    if (key.equals("beginTime")) {
                        chalObj.setBeginTime(jsonReader.nextString());
                    } else if (key.equals("endTime")) {
                        chalObj.setEndTime(jsonReader.nextString());
                    } else if (key.equals("name")) {
                        chalObj.setName(jsonReader.nextString());
                    } else if (key.equals("video")) {
                        parseVideoObj(jsonReader, chalObj);
                    } else {
                        jsonReader.skipValue();
                    }
                } else {
                    jsonReader.skipValue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            chalObj = null;
        } finally {
            try {
                jsonReader.endObject();
            } catch (Exception e) {
            }
        }

        if (chalObj != null && (TextUtils.isEmpty(chalObj.getFilmId()) ||
                TextUtils.isEmpty(chalObj.getName()))) {
            chalObj = null;
        }

        return chalObj;
    }

    private void parseVideoObj(JsonReader jsonReader, ChalObj chalObj) {

        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                JsonToken token = jsonReader.peek();
                if (JsonToken.NAME.equals(token)) {
                    String key = jsonReader.nextName();
                    if (key.equals("filmId")) {
                        chalObj.setFilmId(jsonReader.nextString());
                    } else if (key.equals("id")) {
                        chalObj.setVideoId(jsonReader.nextInt());
                    } else {
                        jsonReader.skipValue();
                    }
                } else {
                    jsonReader.skipValue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                jsonReader.endObject();
            } catch (Exception e) {
            }
        }
    }

    private static class DateSort implements Comparator<String> {

        @Override
        public int compare(String s, String t1) {
            return t1.compareTo(s);
        }
    }
}
