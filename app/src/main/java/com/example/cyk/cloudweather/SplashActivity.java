package com.example.cyk.cloudweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.example.cyk.cloudweather.baidu.BaiduLocation;
import com.example.cyk.cloudweather.db.DBManager;
import com.example.cyk.cloudweather.gson.Weather;
import com.example.cyk.cloudweather.http.MyCallBack;
import com.example.cyk.cloudweather.http.MyHttp;
import com.example.cyk.cloudweather.json.WeatherJson;
import com.example.cyk.cloudweather.weather.WeatherActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final String LOCATION_NAME = "Location";
    private SharedPreferences sharedPreferences;
    //定位返回指针
    BaiduLocation baiduLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //申请定位需要用到的权限
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(SplashActivity.this,Manifest.
                permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(SplashActivity.this,Manifest.
                permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //没有权限则申请
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
            return;
        } else {
            //有权限开启定位功能，异步
            baiduLocation = new BaiduLocation(this);
            //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
            baiduLocation.registerListener(mListener);
            //注册监听
            baiduLocation.setLocationOption(baiduLocation.getDefaultLocationClientOption());
            baiduLocation.start();
        }
    }

    /**
     * 百度定位结果回调，重写onReceiveLocation方法
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuilder currentPosition = new StringBuilder();
                currentPosition.append(location.getLatitude()).append(","); // 经度
                currentPosition.append(location.getLongitude());// 纬度
                requestWeather(currentPosition.toString());
            }
        }
    };

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId){
        String weatherUrl = "https://api.heweather.net/s6/weather?location="+weatherId+"&key=88796e0ed0b045c0aba50fd9c2239a35";

        MyHttp.sendRequestOkHttpForGet(weatherUrl, new MyCallBack() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response) throws IOException {
                final String responseText = response;
                Weather weather = WeatherJson.getWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            String countyName = weather.basic.countyName;
                            //先获取对应的Share
                            sharedPreferences = getApplicationContext().getSharedPreferences(LOCATION_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("location", countyName).apply();
                            //更新城市信息
                            int i = DBManager.updateInfoByCity(countyName,responseText);
                            if (i <= 0){
                                //更新数据库失败，无此城市，增加该城市信息
                                DBManager.addCityInfo(countyName,responseText);
                            }
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        baiduLocation.unregisterListener(mListener); //注销掉监听
        baiduLocation.stop(); //停止定位服务
    }
}
