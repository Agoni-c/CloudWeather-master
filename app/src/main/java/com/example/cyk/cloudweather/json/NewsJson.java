package com.example.cyk.cloudweather.json;

import android.text.TextUtils;

import com.example.cyk.cloudweather.bean.HeadlineNewsBean;
import com.example.cyk.cloudweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.util.LogUtil;

public class NewsJson {

    private static final String TAG = "NewsJson";
    public static HeadlineNewsBean getNewsResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                String NewsContent = jsonObject1.toString();
                return new Gson().fromJson(NewsContent, HeadlineNewsBean.class);
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.d(TAG, "getNewsResponse: ");
            }
        }
        return null;
    }
}
