package com.example.cyk.cloudweather.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cyk.cloudweather.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView about_logo,about_back;
    private TextView about_version,about_intelligence;
    private Button about_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        about_back = findViewById(R.id.about_back);
        about_logo = findViewById(R.id.about_logo);
        about_version = findViewById(R.id.about_version);
        about_intelligence = findViewById(R.id.about_intelligence);
        about_check = findViewById(R.id.about_check);
        about_back.setOnClickListener(this);
        about_check.setOnClickListener(this);
        about_intelligence.setOnClickListener(this);
        String versionName = getVersionName();
        about_version.setText("V"+versionName);
    }

    public String getVersionName(){
        /*获取应用版本信息*/
        PackageManager manager = getPackageManager();
        String versionName = null;
        try{
            PackageInfo info = manager.getPackageInfo(getPackageName(),0);
            versionName = info.versionName;
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return versionName;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.about_back:
                finish();
                break;
            case R.id.about_check:
                break;
            case R.id.about_intelligence:
                break;
            default:
                break;
        }
    }
}
