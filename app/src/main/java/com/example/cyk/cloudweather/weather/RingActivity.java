package com.example.cyk.cloudweather.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.cyk.cloudweather.R;

import java.io.IOException;
import java.util.Calendar;

public class RingActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String PREFERENCES_NAME = "RingActivity";
    private Switch weather_switch;
    private TextView day_tv,night_tv;
    private ImageView ring_back;
    private boolean weather;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        weather_switch = (Switch) findViewById(R.id.weather_switch);
        day_tv = (TextView) findViewById(R.id.day_tv);
        night_tv = (TextView) findViewById(R.id.night_tv);
        ring_back = (ImageView) findViewById(R.id.ring_back);
        ring_back.setOnClickListener(this);
        day_tv.setOnClickListener(RingActivity.this);
        night_tv.setOnClickListener(RingActivity.this);

        //先获取对应的Share
        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE);
        //根据key取出对应的值
        weather = sharedPreferences.getBoolean("weather", false);
        String dayTime = sharedPreferences.getString("dayTime",null);
        String nightTime = sharedPreferences.getString("nightTime",null);
        if (dayTime!=null){
            day_tv.setText(dayTime);
        }
        if (nightTime!=null){
            night_tv.setText(nightTime);
        }
        weather_switch.setChecked(weather);
        if (weather){
            day_tv.setClickable(true);
            night_tv.setClickable(true);
        }else {
            day_tv.setClickable(false);
            night_tv.setClickable(false);
        }


        weather_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //获取Editor对象，这个对象用于写入，可理解为编辑
            SharedPreferences.Editor editor = sharedPreferences.edit();
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //Todo
                    //Editor对象有几个方法需要注：clear()，commit()，putXXX(),clear()为清空Share文件中的内容，
                    // commit()为提交，editor在put值以后，需要调用commit方法才能被真正写入到Share文件中
                    editor.putBoolean("weather", true).commit();
                    day_tv.setClickable(true);
                    night_tv.setClickable(true);
                }else {
                    //Todo
                    editor.putBoolean("weather", false).commit();
                    day_tv.setClickable(false);
                    night_tv.setClickable(false);
                    cancleAlarm();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ring_back:
                finish();
                break;
            case R.id.day_tv:
                setAlarm();
                break;
            case R.id.night_tv:
                setAlarmCycle();
                break;
            default:
                break;
        }
    }

    /**
     * 设置闹钟
     */
    public void setAlarm(){

        //获取当前系统的时间
        Calendar calendar= Calendar.getInstance();
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);


        //弹出时间对话框
        TimePickerDialog timePickerDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY,hour);
                c.set(Calendar.MINUTE,minute);
                c.set(Calendar.SECOND,0);
                String s;
                if (minute < 10){
                    s = hour+":0"+minute;
                }else{
                    s = hour+":"+minute;
                }
                day_tv.setText(s);
                //获取Editor对象，这个对象用于写入，可理解为编辑
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("dayTime",s).commit();
                Intent intent = new Intent(RingActivity.this, RingReceived.class);
                intent.setAction("com.example.cyk.cloudweather.weather.RING");

                //将来时态的跳转 ang eng ing ong
                PendingIntent pendingIntent = PendingIntent.getBroadcast(RingActivity.this,0x101,intent,0);
                // 获取系统的闹钟服务
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                //设置闹钟
//                alarmManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
                //设置周期闹钟
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),1000*60*60*24, pendingIntent);
                //时间一到，发送广播（闹钟响了）
                //广播接受者中（跳转Activity）
                // 跳转Activity，在这个Activity中播放音乐
            }
        },hour,minute,true);

        timePickerDialog.show();
    }

    /**
     * 取消闹钟
     */
    private void cancleAlarm(){
        Intent intent = new Intent(RingActivity.this,RingReceived.class);
        intent.setAction("com.example.cyk.cloudweather.weather.RING");
        PendingIntent sender = PendingIntent.getBroadcast(RingActivity.this, 0, intent, 0);
        // And cancel the alarm.
        // 获取系统的闹钟服务
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(sender);//取消闹钟
    }


    /**
     * 设置周期闹钟
     */
    public void setAlarmCycle(){

        //获取当前系统的时间
        Calendar calendar=Calendar.getInstance();
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);


        //弹出时间对话框
        TimePickerDialog timePickerDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                Calendar c=Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY,i);
                c.set(Calendar.MINUTE,i1);
                c.set(Calendar.SECOND,0);
                String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
                String minute = String.valueOf(c.get(Calendar.MINUTE));
                String s;
                if (i1 < 10){
                    s = hour+":0"+minute;
                }else{
                    s = hour+":"+minute;
                }
                night_tv.setText(s);
                //获取Editor对象，这个对象用于写入，可理解为编辑
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("nightTime",s).commit();
                Intent intent=new Intent(RingActivity.this, RingReceived.class);
                intent.setAction("com.example.cyk.cloudweather.weather.RING");

                //将来时态的跳转 ang eng ing ong
                PendingIntent pendingIntent = PendingIntent.getBroadcast(RingActivity.this,0x102,intent,0);

                // 获取系统的闹钟服务
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                //设置周期闹钟
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),1000*60*60*24, pendingIntent);

                //时间一到，发送广播（闹钟响了）
                //广播接受者中（跳转Activity）
                // 跳转Activity，在这个Activity中播放音乐

            }
        },hour,minute,true);

        timePickerDialog.show();
    }

}
