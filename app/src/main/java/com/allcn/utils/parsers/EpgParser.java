package com.allcn.utils.parsers;

import android.text.TextUtils;
import android.util.Log;

import com.datas.EpgObj;
import com.db.cls.DBMgr;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class EpgParser {

    private List<EpgObj> yyEpgs, zhEpgs;
    private int yyEpgNum, zhEpgNum;

    public EpgParser() {
        yyEpgs = new ArrayList<>();
        zhEpgs = new ArrayList<>();
    }

    public void release() {
        clearDatas();
    }

    private void clearDatas() {
        yyEpgs = null;
        zhEpgs = null;
        yyEpgNum = zhEpgNum = 0;
    }

    public void parse(String jsonStr) {//解析頻道信息

        clearDatas();

//        epgNum = 20;
//
//        for (int i = 0; i < epgNum; i++) {
//            EpgObj epgObj = new EpgObj();
//            epgObj.setEpgId(i);
//            epgObj.setName("北京卫视");
//            epgs.add(epgObj);
//        }

//        if (TextUtils.isEmpty(jsonStr)) {
//            return;
//        }
//
        long sL = System.currentTimeMillis();
        StringReader stringReader = new StringReader(jsonStr);
        JsonReader jsonReader = new JsonReader(stringReader);
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                //JsonToken token = jsonReader.peek();
                //if (JsonToken.NAME.equals(token)) {
                    String key = jsonReader.nextName();
                    if (key.equals("data")) {
                        parseData(jsonReader);
                    } else {
                        jsonReader.skipValue();
                    }
                //}
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
        DBMgr.Ins().clearPlayBackEpg();

        long eL = System.currentTimeMillis();
        Log.e(TAG, "parse: "+(eL - sL ));
        DBMgr.Ins().insertPlayBackEpgs(yyEpgs, yyEpgNum);//粵語頻道數據存入數據庫
        DBMgr.Ins().insertPlayBackEpgs(zhEpgs, zhEpgNum);//綜合頻道存入數據庫
    }

    private static final String TAG = "EpgParser";
    private void parseData(JsonReader jsonReader) {//將獲取到的數據分類
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                EpgObj epgObj = parseEpgObj(jsonReader);
                if (epgObj != null) {
                    String name = epgObj.getName();
                    String showName = name.substring(0, name.indexOf("("));
                    epgObj.setName(showName);
                    if (name.contains("综合")) {
                        zhEpgs.add(zhEpgNum++, epgObj);
                    } else if (name.contains("粤语")) {
                        yyEpgs.add(yyEpgNum++, epgObj);
                    }
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
    }


    private EpgObj parseEpgObj(JsonReader jsonReader) {
        int id = -1;
        String name = null;
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                JsonToken token = jsonReader.peek();
                if (JsonToken.NAME.equals(token)) {
                    String key = jsonReader.nextName();
                    if (key.equals("id")) {
                        id = jsonReader.nextInt();
                    } else if (key.equals("name")) {
                        name = jsonReader.nextString();
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
        }
        return TextUtils.isEmpty(name) || id == -1 ? null : new EpgObj(id, name);
    }



    public List<EpgObj> getYyEpgs(List<EpgObj> epgObjs) {
        yyEpgs.clear();
        for (EpgObj epgObj : epgObjs) {
            if (epgObj.getName().contains("粤语")){
                yyEpgs.add(epgObj);
            }
        }
        return yyEpgs;
    }

    public List<EpgObj> getZhEpgs(List<EpgObj> epgObjs) {
        zhEpgs.clear();
        for (EpgObj epgObj : epgObjs) {
            if (epgObj.getName().contains("综合")){
                zhEpgs.add(epgObj);
            }
        }
        return zhEpgs;
    }


    public List<EpgObj> getYyEpgs() {
        return yyEpgs;
    }

    public List<EpgObj> getZhEpgs() {
        return zhEpgs;
    }

    public int getYyEpgNum() {
        return this.yyEpgNum;
    }

    public int getZhEpgNum() {
        return this.zhEpgNum;
    }

    public EpgObj getYyEpg(int position) {
        return position < this.yyEpgNum ? this.yyEpgs.get(position) : null;
    }

    public EpgObj getZhEpg(int position) {
        return position < this.zhEpgNum ? this.zhEpgs.get(position) : null;
    }
}
