package com.example.cyk.cloudweather.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cyk.cloudweather.MainActivity;
import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.db.DBManager;
import com.example.cyk.cloudweather.gson.Weather;
import com.example.cyk.cloudweather.json.WeatherJson;
import com.example.cyk.cloudweather.util.IconUtil;
import com.example.cyk.cloudweather.util.ShareImageUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView weather_info_image,share_location,share_back;
    private TextView weather_info_text,weather_wind,weather_humidity,temperature_text,share_date,share_city;
    private Button share_button;
    private LinearLayout share_ll,share_activity;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        //提取数据
        Intent intent = getIntent();
        city = intent.getStringExtra("city");
        share_activity = (LinearLayout) findViewById(R.id.share_activity);
        share_ll = (LinearLayout) findViewById(R.id.share_ll);
        weather_info_image = (ImageView) findViewById(R.id.weather_info_image);
        share_location = (ImageView) findViewById(R.id.share_location);
        share_back = (ImageView) findViewById(R.id.share_back);
        weather_info_text = (TextView) findViewById(R.id.weather_info_text);
        weather_wind = (TextView) findViewById(R.id.weather_wind);
        temperature_text = (TextView) findViewById(R.id.temperature_text);
        share_date = (TextView) findViewById(R.id.share_date);
        share_city = (TextView) findViewById(R.id.share_city);
        weather_humidity = (TextView) findViewById(R.id.weather_humidity);
        share_button = (Button) findViewById(R.id.share_button);
        share_back.setOnClickListener(this);
        share_button.setOnClickListener(this);
        initData();
    }

    private void initData(){
        String s = DBManager.queryInfoByCity(city);
        Weather weather = WeatherJson.getWeatherResponse(s);
        weather_info_text.setText(weather.now.txt+"|");
        weather_wind.setText(weather.now.wind_direction+weather.now.wind_strength+"级|");
        weather_humidity.setText("湿度"+weather.now.humidity+"%");
        share_city.setText(weather.basic.countyName);
        String weatherInfoCode = weather.now.code;
        //获取当前时间
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        share_date.setText(month+"月"+day+"日");
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour > 6 && hour < 19) {
            share_activity.setBackgroundResource(IconUtil.getDayBack(weatherInfoCode));
            share_ll.setBackgroundResource(IconUtil.getDayBack(weatherInfoCode));
            weather_info_image.setImageResource(IconUtil.getDayIcon(weatherInfoCode));
        } else {
            share_activity.setBackgroundResource(IconUtil.getDayBack(weatherInfoCode));
            share_ll.setBackgroundResource(IconUtil.getNightBack(weatherInfoCode));
            weather_info_image.setImageResource(IconUtil.getNightIconDark(weatherInfoCode));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.share_back:
                finish();
                break;
            case R.id.share_button:
                Bitmap b = getViewBitmap(share_ll);
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), b, null,null));
                allShare(uri);
                break;
            default:
                break;
        }
    }
    /**
     * Android原生分享功能
     * 默认选取手机所有可以分享的APP
     */
    public void allShare(Uri uri){
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("image/*");//设置分享内容的类型
        share_intent.putExtra(Intent.EXTRA_SUBJECT, "share");//添加分享内容标题
        share_intent.putExtra(Intent.EXTRA_STREAM, uri);//添加分享内容
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, "share");
        startActivity(share_intent);
    }


    private Bitmap getViewBitmap(LinearLayout linearLayout){
            linearLayout.clearFocus();
            linearLayout.setPressed(false);
            boolean willNotCache = linearLayout.willNotCacheDrawing();
            linearLayout.setWillNotCacheDrawing(false);
            int color = linearLayout.getDrawingCacheBackgroundColor();
            linearLayout.setDrawingCacheBackgroundColor(0);
            if (color != 0) {
                linearLayout.destroyDrawingCache();
            }
            linearLayout.buildDrawingCache();
            Bitmap cacheBitmap = linearLayout.getDrawingCache();
            if (cacheBitmap == null) {
                return null;
            }
            Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
            linearLayout.destroyDrawingCache();
            linearLayout.setWillNotCacheDrawing(willNotCache);
            linearLayout.setDrawingCacheBackgroundColor(color);
            return bitmap;
        }

}
