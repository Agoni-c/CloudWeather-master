package com.example.cyk.cloudweather.json;

import android.text.TextUtils;

import com.example.cyk.cloudweather.gson.Air;
import com.example.cyk.cloudweather.gson.Alarm;
import com.example.cyk.cloudweather.gson.Rain;
import com.example.cyk.cloudweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.util.LogUtil;


/*将返回的JSON数据解析成Weather实体类*/
public class WeatherJson {
    private static final String TAG = "WeatherJson";
    public static Weather getWeatherResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                String weateherContent = jsonArray.getJSONObject(0).toString();
                return new Gson().fromJson(weateherContent, Weather.class);
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.d(TAG, "getWeatherResponse: ");
            }
        }
        return null;
    }

    public static Alarm getAlarmResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                String weateherContent = jsonArray.getJSONObject(0).toString();
                return new Gson().fromJson(weateherContent, Alarm.class);
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.d(TAG, "getAlarmResponse: ");
            }
        }
        return null;
    }

    public static Rain getRainResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                String weateherContent = jsonArray.getJSONObject(0).toString();
                return new Gson().fromJson(weateherContent, Rain.class);
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.d(TAG, "getRainResponse: ");
            }
        }
        return null;
    }

    public static Air getAirResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                String weateherContent = jsonArray.getJSONObject(0).toString();
                return new Gson().fromJson(weateherContent, Air.class);
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.d(TAG, "getRainResponse: ");
            }
        }
        return null;
    }
}
