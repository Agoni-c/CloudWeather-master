package com.example.cyk.cloudweather.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.cyk.cloudweather.MainActivity;
import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.UniteApp;
import com.example.cyk.cloudweather.util.ConstantGlobal;
import com.example.cyk.cloudweather.util.MultiLanguageUtil;
import com.example.cyk.cloudweather.util.SpUtil;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String PREFERENCES_NAME = "LanguageActivity";

    private ImageView language_back,cn_tick,tw_tick,us_tick,system_tick;
    private RelativeLayout language_cn,language_tw,language_us,language_system;
    private Boolean system,cn,tw,us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        SharedPreferences sp= getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        //根据key取出对应的值
        system = sp.getBoolean("system", true);//第二个参数为默认值，即当从Share中取不到时，返回这个值
        cn = sp.getBoolean("cn", false);
        tw = sp.getBoolean("tw", false);
        us = sp.getBoolean("us", false);
        language_back = (ImageView) findViewById(R.id.language_back);
        cn_tick = (ImageView) findViewById(R.id.cn_tick);
        tw_tick = (ImageView) findViewById(R.id.tw_tick);
        us_tick = (ImageView) findViewById(R.id.us_tick);
        system_tick = (ImageView) findViewById(R.id.system_tick);
        if (system){
            system_tick.setVisibility(View.VISIBLE);
        }else {
            system_tick.setVisibility(View.GONE);
        }
        if (cn){
            cn_tick.setVisibility(View.VISIBLE);
        }else {
            cn_tick.setVisibility(View.GONE);
        }
        if (tw){
            tw_tick.setVisibility(View.VISIBLE);
        }else {
            tw_tick.setVisibility(View.GONE);
        }
        if (us){
            us_tick.setVisibility(View.VISIBLE);
        }else {
            us_tick.setVisibility(View.GONE);
        }
        language_cn = (RelativeLayout) findViewById(R.id.language_cn);
        language_tw = (RelativeLayout) findViewById(R.id.language_tw);
        language_us = (RelativeLayout) findViewById(R.id.language_us);
        language_system = (RelativeLayout) findViewById(R.id.language_system);
        language_back.setOnClickListener(this);
        language_cn.setOnClickListener(this);
        language_tw.setOnClickListener(this);
        language_us.setOnClickListener(this);
        language_system.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        SharedPreferences sp = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        //获取Editor对象，这个对象用于写入，可理解为编辑
        SharedPreferences.Editor editor = sp.edit();
        switch (view.getId()){
            case R.id.language_back:
                finish();
                break;
            case R.id.language_system:
                editor.putBoolean("system", true).commit();
                editor.putBoolean("cn", false).commit();
                editor.putBoolean("tw", false).commit();
                editor.putBoolean("us", false).commit();
                system_tick.setVisibility(View.VISIBLE);
                cn_tick.setVisibility(View.GONE);
                tw_tick.setVisibility(View.GONE);
                us_tick.setVisibility(View.GONE);
                changeLanguage("", "");
                break;
            case R.id.language_cn:
                editor.putBoolean("system", false).commit();
                editor.putBoolean("cn", true).commit();
                editor.putBoolean("tw", false).commit();
                editor.putBoolean("us", false).commit();
                system_tick.setVisibility(View.GONE);
                cn_tick.setVisibility(View.VISIBLE);
                tw_tick.setVisibility(View.GONE);
                us_tick.setVisibility(View.GONE);
                changeLanguage("zh", "CN");
                break;
            case R.id.language_tw:
                editor.putBoolean("system", false).commit();
                editor.putBoolean("cn", false).commit();
                editor.putBoolean("tw", true).commit();
                editor.putBoolean("us", false).commit();
                system_tick.setVisibility(View.GONE);
                cn_tick.setVisibility(View.GONE);
                tw_tick.setVisibility(View.VISIBLE);
                us_tick.setVisibility(View.GONE);
                changeLanguage("zh", "TW");
                break;
            case R.id.language_us:
                editor.putBoolean("system", false).commit();
                editor.putBoolean("cn", false).commit();
                editor.putBoolean("tw", false).commit();
                editor.putBoolean("us", true).commit();
                system_tick.setVisibility(View.GONE);
                cn_tick.setVisibility(View.GONE);
                tw_tick.setVisibility(View.GONE);
                us_tick.setVisibility(View.VISIBLE);
                changeLanguage("en", "US");
                break;
            default:
                break;
        }
    }

    //修改应用内语言设置
    private void changeLanguage(String language, String area) {
        if (TextUtils.isEmpty(language) && TextUtils.isEmpty(area)) {
            //如果语言和地区都是空，那么跟随系统
            SpUtil.saveString(ConstantGlobal.LOCALE_LANGUAGE, "");
            SpUtil.saveString(ConstantGlobal.LOCALE_COUNTRY, "");
        } else {
            //不为空，那么修改app语言，并true是把语言信息保存到sp中，false是不保存到sp中
            Locale newLocale = new Locale(language, area);
            MultiLanguageUtil.changeAppLanguage(LanguageActivity.this, newLocale, true);
            MultiLanguageUtil.changeAppLanguage(UniteApp.getContext(), newLocale, true);
        }
        //重启app,这一步一定要加上，如果不重启app，可能打开新的页面显示的语言会不正确
        Intent intent = new Intent(UniteApp.getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        UniteApp.getContext().startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
