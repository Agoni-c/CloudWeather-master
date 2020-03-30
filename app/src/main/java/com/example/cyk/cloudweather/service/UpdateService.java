package com.example.cyk.cloudweather.service;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.example.cyk.cloudweather.BaiduLocation;
import com.example.cyk.cloudweather.WeatherActivity;
import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.WidgetProvider;
import com.example.cyk.cloudweather.db.DBManager;
import com.example.cyk.cloudweather.gson.Weather;
import com.example.cyk.cloudweather.json.WeatherJson;
import com.example.cyk.cloudweather.util.IconUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateService extends Service {
    private static final String TAG = "UpdateService";
    public static final String REFRESH_WIDGET = "REFRESH_WIDGET";
    private static final int UPDATE = 0x123;
    private RemoteViews remoteViews;

    BaiduLocation baiduLocation;
    //定位城市的天气Id和城市名
    public static String locationCountyWeatherName = null;
//    private Weather weather;
    private Weather weather = WeatherJson.getWeatherResponse(DBManager.queryInfoByCity(DBManager.queryALLCityName().get(0)));

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:
                    // 更新天气
                    updateTime();
//                    updateWeather();
                    break;
            }
        }
    };
    // 广播接收者去接收系统每分钟的提示广播，来更新时间
    private BroadcastReceiver mTimePickerBroadcast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateTime();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        remoteViews = new RemoteViews(getApplication().getPackageName(),
                R.layout.widget_provider);// 实例化RemoteViews
        // 点击天气图片，进入MainActivity
        Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),
                0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_weather, pi);

//        //有权限开启定位功能，异步
//        baiduLocation = new BaiduLocation(getApplicationContext());
//        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
//        baiduLocation.registerListener(mListener);
//        //注册监听
//        baiduLocation.setLocationOption(baiduLocation.getDefaultLocationClientOption());
//        baiduLocation.start();
//        // 设置响应 “按钮(widget_refresh)” 的intent
//        Intent btIntent = new Intent().setAction(REFRESH_WIDGET);
//        PendingIntent btPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, btIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteViews.setOnClickPendingIntent(R.id.widget_refresh, btPendingIntent);

        updateTime();// 第一次运行时先更新一下时间和天气
        updateWeather();
        // 定义一个定时器去更新天气。实际开发中更新时间间隔可以由用户设置，
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                msg.what = UPDATE;
                handler.sendMessage(msg);
            }
        }, 1, 3600 * 1000);// 每小时更新一次天气
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
                locationCountyWeatherName = currentPosition.toString();
            }
        }
    };

    private void updateTime() {
          Date date = new Date();
        // 定义SimpleDateFormat对象
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        // 将当前时间格式化成HHmm的形式
        String timeStr = df.format(date);
        String[] weekday = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        remoteViews.setTextViewText(R.id.widget_time, timeStr);
        remoteViews.setTextViewText(R.id.widget_week, weekday[date.getDay()]);
        remoteViews.setTextViewText(R.id.widget_date, (date.getMonth() + 1)
                + "/" + date.getDate());
        ComponentName componentName = new ComponentName(getApplication(),
                WidgetProvider.class);
        AppWidgetManager.getInstance(getApplication()).updateAppWidget(
                componentName, remoteViews);
    }


    private void updateWeather() {
//        if (isNetworkAvailable()) {
//            String weatherUrl = "https://api.heweather.net/s6/weather?location=" + locationCountyWeatherName + "&key=88796e0ed0b045c0aba50fd9c2239a35";
//            MyHttp.sendRequestOkHttpForGet(weatherUrl, new MyCallBack() {
//                @Override
//                public void onFailure(IOException e) {
//                    LogUtil.d(TAG, "onFailure: netWork error");
//                }
//
//                @Override
//                public void onResponse(String response) throws IOException {
//                    final String responseText = response;
//                    LogUtil.d(TAG, "onResponse: responseText: " + responseText);
//                    weather = WeatherJson.getWeatherResponse(responseText);
//                }
//            });
//        } else {
//            toast();
//        }
        remoteViews.setTextViewText(R.id.widget_county, weather.basic.countyName);
        String weatherInfoCode = weather.now.code;
        int weatherInfoImageId = IconUtil.getDayIcon(weatherInfoCode);
        remoteViews.setImageViewResource(R.id.widget_weather,
                weatherInfoImageId);
        remoteViews.setTextViewText(R.id.widget_temperature, weather.now.temperature + "℃" + weather.now.txt);
        // 执行更新
        ComponentName componentName = new ComponentName(
                getApplicationContext(), WidgetProvider.class);
        AppWidgetManager.getInstance(getApplicationContext()).updateAppWidget(
                componentName, remoteViews);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // 注册系统每分钟提醒广播（注意：这个广播只能在代码中注册）
        IntentFilter updateIntent = new IntentFilter();
        updateIntent.addAction("android.intent.action.TIME_TICK");
        registerReceiver(mTimePickerBroadcast, updateIntent);
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        // 注销系统的这个广播
        unregisterReceiver(mTimePickerBroadcast);
        //被系统干掉后，服务重启,做一次流氓软件,哈哈
        Intent intent = new Intent(getApplicationContext(), UpdateService.class);
        getApplication().startService(intent);
        super.onDestroy();
    }

    /**
     * 判断手机网络是否可用
     *
     * @param
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager mgr = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    private void toast() {
        new AlertDialog.Builder(getApplicationContext())
                .setTitle("提示")
                .setMessage("网络连接未打开")
                .setPositiveButton("前往打开",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent intent = new Intent(
                                        android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(intent);
                            }
                        }).setNegativeButton("取消", null).create().show();
    }
}
