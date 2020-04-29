package com.example.cyk.cloudweather.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cyk.cloudweather.R;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView alarm_back;
    private TextView alarm_tv,alarm_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        //提取数据
        Intent intent=getIntent();
        String title =intent.getStringExtra("alarm_title");
        String content =intent.getStringExtra("alarm_content");
        alarm_back = (ImageView) findViewById(R.id.alarm_back);
        alarm_tv = (TextView) findViewById(R.id.alarm_tv);
        alarm_content = (TextView) findViewById(R.id.alarm_content);
        alarm_tv.setText(title);
        alarm_content.setText(content);

        alarm_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.alarm_back:
                finish();
                break;
            default:
                break;
        }
    }
}
