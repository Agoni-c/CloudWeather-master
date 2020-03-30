package com.example.cyk.cloudweather.json;

import android.text.TextUtils;

import com.example.cyk.cloudweather.db.City;
import com.example.cyk.cloudweather.db.County;
import com.example.cyk.cloudweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.util.LogUtil;

/*解析和处理服务器返回的省市县数据*/
public class AreaJson {

    private static final String TAG = "AreaJson";

    /**
     * 获取省数据
     */
    public static boolean getProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvince = new JSONArray(response);
                for (int i = 0; i < allProvince.length(); i++){
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
            }catch (JSONException e) {
                e.printStackTrace();
                LogUtil.d(TAG, "getProvinceResponse: ");
            }
            return true;
        }
        return false;
    }

    /**
     * 获取市数据
     */
    public static boolean getCityJson(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
            } catch (JSONException e){
                e.printStackTrace();
                LogUtil.d(TAG, "getCityJson: error");
            }
            return true;
        }
        return false;
    }

    /**
     * 获取县数据
     */
    public static boolean getCountyJson(String response, int cityId) {
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
            }catch (JSONException e) {
                e.printStackTrace();
                LogUtil.d(TAG, "getCountyJson: error");
            }
            return true;
        }
        return false;
    }
}
