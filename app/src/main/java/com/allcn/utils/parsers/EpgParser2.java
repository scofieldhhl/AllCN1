package com.allcn.utils.parsers;

import android.text.TextUtils;
import android.util.Log;

import com.datas.EpgObj;
import com.db.cls.DBMgr;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EpgParser2 {

    private List yyEpgs;
    private List<EpgObj> zhEpgs;
    private int yyEpgNum, zhEpgNum;

    public EpgParser2() {
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
        List epgObjList = new ArrayList();


        if (zhEpgs == null) zhEpgs = new ArrayList();
        else zhEpgs.clear();
        if (yyEpgs == null) yyEpgs = new ArrayList();
        else yyEpgs.clear();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray data = jsonObject.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                EpgObj epgObj = new EpgObj();
                JSONObject jObject = (JSONObject) data.get(i);
                epgObj.setEpgId(jObject.getInt("id"));
                epgObj.setName(jObject.getString("name"));

                if (epgObj.getName().contains("粤语"))
                    yyEpgs.add(epgObj);
                else
                 zhEpgs.add(epgObj);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        long eL = System.currentTimeMillis();

        //yyEpgs = getYyEpgs(epgObjList);
        //zhEpgs = getZhEpgs(epgObjList);

        Log.e(TAG, "parse: "+(eL - sL ));
        try {
            DBMgr.Ins().clearPlayBackEpg();
        }catch (Exception e){}

        if (yyEpgs.size()>0)
            DBMgr.Ins().insertPlayBackEpgs(yyEpgs, yyEpgs.size());//粵語頻道數據存入數據庫
        if (zhEpgs.size()>0)
            DBMgr.Ins().insertPlayBackEpgs(zhEpgs, zhEpgs.size());//綜合頻道存入數據庫
    }

    private static final String TAG = "EpgParser2";
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
        if (yyEpgs == null) yyEpgs = new ArrayList();
        yyEpgs.clear();
        for (EpgObj epgObj : epgObjs) {
            if (epgObj.getName().contains("粤语")){
                yyEpgs.add(epgObj);
            }
        }
        return yyEpgs;
    }

    public List<EpgObj> getZhEpgs(List<EpgObj> epgObjs) {
        if (zhEpgs == null) zhEpgs = new ArrayList();
        zhEpgs.clear();
        for (EpgObj epgObj : epgObjs) {
            if (epgObj.getName().contains("综合")){
                zhEpgs.add(epgObj);
            }
        }
        return zhEpgs;
    }

     public List<EpgObj> getAllEpgs() {
         ArrayList<EpgObj> epgObjs = new ArrayList<>();
         if (yyEpgs != null)
             epgObjs.addAll(yyEpgs);
         if (zhEpgs!=null)
             epgObjs.addAll(zhEpgs);
        return epgObjs;
    }



    public List<EpgObj> getYyEpgs() {
        return yyEpgs;
    }

    public List<EpgObj> getZhEpgs() {
        return zhEpgs;
    }

    public int getYyEpgNum() {
        return yyEpgs.size();
    }

    public int getZhEpgNum() {
        return zhEpgs.size();
    }

    public Object getYyEpg(int position) {
        return position < this.yyEpgNum ? this.yyEpgs.get(position) : null;
    }

    public EpgObj getZhEpg(int position) {
        return position < this.zhEpgNum ? this.zhEpgs.get(position) : null;
    }
}
