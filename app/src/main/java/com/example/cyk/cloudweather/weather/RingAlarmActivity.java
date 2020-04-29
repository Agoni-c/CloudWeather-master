package com.example.cyk.cloudweather.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.db.DBManager;
import com.example.cyk.cloudweather.gson.Weather;
import com.example.cyk.cloudweather.json.WeatherJson;
import com.example.cyk.cloudweather.util.IconUtil;

import java.io.IOException;

public class RingAlarmActivity extends AppCompatActivity {
    private static final String LOCATION_NAME = "Location";
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_alarm);
        mediaPlayer = new MediaPlayer();
        AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.music);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                    file.getLength());
            mediaPlayer.prepare();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setVolume(0.5f, 0.5f);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        alarmOialog();
    }


    public void alarmOialog() {
        //获取数据库中存储的定位城市
        SharedPreferences sp= getSharedPreferences(LOCATION_NAME, Context.MODE_PRIVATE);
        //根据key取出对应的值
        String location = sp.getString("location", "");
        String s = DBManager.queryInfoByCity(location);
        Weather weather = WeatherJson.getWeatherResponse(s);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("云烟成雨");
        builder.setMessage(location+" "+weather.now.txt+" "+weather.now.temperature+"℃"+" "+weather.now.wind_direction+weather.now.wind_strength+"级");
        builder.setIcon(R.mipmap.app_logo);
        builder.setPositiveButton("稍后提醒（十分钟）",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alarm();
                        mediaPlayer.stop();
                        finish();
                    }
                });

        builder.setNegativeButton("停止", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancleAlarm();
                mediaPlayer.stop();
                finish();
            }
        });
        builder.show().setCanceledOnTouchOutside(false);
        ;
    }

    private void alarm() {
        // 触发闹钟的时间（毫秒）
        long triggerTime = System.currentTimeMillis() + 1000*60*10;
        Intent intent = new Intent(this, RingReceived.class);
        intent.setAction("com.example.cyk.cloudweather.weather.RING");
        PendingIntent op = PendingIntent.getBroadcast(this, 0, intent, 0);
        // 启动一次只会执行一次的闹钟
        // 获取系统的闹钟服务
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, triggerTime, op);
        // 指定时间重复执行闹钟
        // am.setRepeating(AlarmManager.RTC,triggerTime,2000,op);
    }

    /**
     * 取消闹钟
     */
    private void cancleAlarm(){
        Intent intent = new Intent(RingAlarmActivity.this,RingReceived.class);
        intent.setAction("com.example.cyk.cloudweather.weather.RING");
        PendingIntent sender = PendingIntent.getBroadcast(RingAlarmActivity.this, 0, intent, 0);
        // And cancel the alarm.
        // 获取系统的闹钟服务
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(sender);//取消闹钟
    }

    @Override
    protected void onResume(){
        super.onResume();
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }
}
