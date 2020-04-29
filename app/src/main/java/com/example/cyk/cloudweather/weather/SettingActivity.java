package com.example.cyk.cloudweather.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import com.example.cyk.cloudweather.MainActivity;
import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.UniteApp;
import com.example.cyk.cloudweather.db.DBManager;
import com.example.cyk.cloudweather.gson.Weather;
import com.example.cyk.cloudweather.json.WeatherJson;
import com.example.cyk.cloudweather.util.ConstantGlobal;
import com.example.cyk.cloudweather.util.IconUtil;
import com.example.cyk.cloudweather.util.MultiLanguageUtil;
import com.example.cyk.cloudweather.util.SpUtil;

import java.util.Calendar;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String PREFERENCES_NAME = "SettingActivity";
    private static final String LOCATION_NAME = "Location";
    /**
     * SharedPreferences sp = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
     参数有两个，第一个表示Share文件的名称，不同的名称对应这不同的Share文件，其中的内容也是不同；
     第二个参数表示操作模式，操作模式有两种：MODE_PRIVATE和MODE_MULTI_PRIVATE
     MODE_PRIVATE：默认操作模式，直接在把第二个参数写0就是默认使用这种操作模式，
     这种模式表示只有当前的应用程序才可以对当前这个SharedPreferences文件进行读写。
     MODE_MULTI_PRIVATE：用于多个进程共同操作一个SharedPreferences文件。
     */

    private Switch map_switch,warn_switch,notice_switch;
    private ImageView setting_back,setting_ring_iv;
    private RelativeLayout setting_language;

    private boolean map,warn_message,notice_message;
    private AlarmManager alarmManager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //先获取对应的Share
        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE);
        //根据key取出对应的值
        map = sharedPreferences.getBoolean("map", false);//第二个参数为默认值，即当从Share中取不到时，返回这个值
        warn_message = sharedPreferences.getBoolean("warn_message", false);
        notice_message = sharedPreferences.getBoolean("notice_message", false);

        //获取闹钟管理者
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //获取界面布局控件
        map_switch = (Switch) findViewById(R.id.map_switch);
        setting_ring_iv = (ImageView) findViewById(R.id.setting_ring_iv);
        warn_switch = (Switch) findViewById(R.id.warn_switch);
        notice_switch = (Switch) findViewById(R.id.notice_switch);
        setting_back = (ImageView) findViewById(R.id.setting_back);
        setting_language = (RelativeLayout) findViewById(R.id.setting_language);

        map_switch.setChecked(map);
        warn_switch.setChecked(warn_message);
        notice_switch.setChecked(notice_message);
        setting_ring_iv.setOnClickListener(this);
        setting_back.setOnClickListener(this);
        setting_language.setOnClickListener(this);

        //获取Editor对象，这个对象用于写入，可理解为编辑
        SharedPreferences.Editor editor = sharedPreferences.edit();
        map_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //Todo
                    //Editor对象有几个方法需要注：clear()，commit()，putXXX(),clear()为清空Share文件中的内容，
                    // commit()为提交，editor在put值以后，需要调用commit方法才能被真正写入到Share文件中
                    editor.putBoolean("map", true).apply();
                }else {
                    //Todo
                    editor.putBoolean("map", false).apply();
                }
            }
        });


        warn_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //Todo
                    //Editor对象有几个方法需要注：clear()，commit()，putXXX(),clear()为清空Share文件中的内容，
                    // commit()为提交，editor在put值以后，需要调用commit方法才能被真正写入到Share文件中
                    editor.putBoolean("warn_message", true).apply();
                }else {
                    //Todo
                    editor.putBoolean("warn_message", false).apply();
                }
            }
        });

        notice_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //Todo
                    //Editor对象有几个方法需要注：clear()，commit()，putXXX(),clear()为清空Share文件中的内容，
                    // commit()为提交，editor在put值以后，需要调用commit方法才能被真正写入到Share文件中
                    editor.putBoolean("notice_message", true).apply();
                    setNotification();
                }else {
                    //Todo
                    editor.putBoolean("notice_message", false).apply();
                    cancelNotification();
                }
            }
        });


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.setting_back:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_language:
                Intent intent1 = new Intent(this, LanguageActivity.class);
                startActivity(intent1);
                break;
            case R.id.setting_ring_iv:
                Intent intent2 = new Intent(this, RingActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }


    //添加常驻通知
    public void setNotification(){
        //获取数据库中存储的定位城市
        SharedPreferences sp= getSharedPreferences(LOCATION_NAME, Context.MODE_PRIVATE);
        //根据key取出对应的值
        String location = sp.getString("location", "");
        String s = DBManager.queryInfoByCity(location);
        Weather weather = WeatherJson.getWeatherResponse(s);
        //天气详情
        String weatherInfoCode = weather.now.code;
        int weatherInfoImageId = IconUtil.getDayIcon(weatherInfoCode);
        //获取当前时间
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int hour = c.get(Calendar.HOUR_OF_DAY);
        //自定义通知栏视图初始化
        RemoteViews remoteViews =
                new RemoteViews(getPackageName(),R.layout.notification);
        remoteViews.setTextViewText(R.id.notification_city,location);
        remoteViews.setTextViewText(R.id.notification_weather,weather.now.txt);
        remoteViews.setTextViewText(R.id.notification_temp,weather.now.temperature+"℃");
        remoteViews.setTextViewText(R.id.notification_wind,weather.now.wind_direction+weather.now.wind_strength+"级");
        if (hour > 6 && hour < 19) {
            remoteViews.setImageViewResource(R.id.notification_iv,weatherInfoImageId);
        } else {
            remoteViews.setImageViewResource(R.id.notification_iv, IconUtil.getNightIconDark(weatherInfoCode));
        }
        //自定义通知也是在Android N之后才出现的，所以要加上版本号判断
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            Notification.Builder builder = new Notification.Builder(this)
                    .setAutoCancel(false)//是否自动取消，设置为true，点击通知栏 ，移除通知
                    //设置通知的图标
                    .setSmallIcon(R.mipmap.weather_cloudy)
                    //设置通知内容的标题
                    .setContentTitle("云烟成雨");
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_ONGOING_EVENT;// 设置常驻 Flag
            //PendingIntent即将要发生的意图，可以被取消、更新
            Intent intent = new Intent(this,MainActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.now_map_ll,pendingIntent);
            //绑定自定义视图
            builder.setCustomContentView(remoteViews);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(R.string.app_name,notification);
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent contextIntent = PendingIntent.getActivity(this,0,intent,0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    //设置通知的图标
                    .setSmallIcon(R.mipmap.weather_cloudy)
                    //设置通知内容的标题
                    .setContentTitle(location)
                    //设置通知内容的内容
                    .setContentText(weather.now.txt+weather.now.temperature+"℃");
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            builder.setContentIntent(contextIntent);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(R.string.app_name,notification);
        }
    }

    //取消通知
    public void cancelNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(R.string.app_name);
    }
}
