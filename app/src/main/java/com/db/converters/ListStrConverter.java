package com.db.converters;

import android.text.TextUtils;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.List;

public class ListStrConverter implements PropertyConverter<List<String>, String> {

    @Override
    public List<String> convertToEntityProperty(String databaseValue) {
        List<String> list = null;
        if (!TextUtils.isEmpty(databaseValue)) {
            String[] dataArr = databaseValue.split("[;]");
            int dataNum = dataArr == null ? 0 : dataArr.length;
            list = new ArrayList<>(dataNum);
            for (int i = 0; i < dataNum; i++) {
                list.add(dataArr[i]);
            }
        }
        return list;
    }

    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        StringBuilder stringBuilder = new StringBuilder();
        int dataNum = entityProperty == null ? 0 : entityProperty.size();
        int loopNum = dataNum - 1;
        for (int i = 0; i < loopNum; i++) {
            stringBuilder.append(entityProperty.get(i)).append(";");
        }
        if (loopNum >= 0) {
            stringBuilder.append(entityProperty.get(loopNum));
        }
        return stringBuilder.toString();
    }
}
