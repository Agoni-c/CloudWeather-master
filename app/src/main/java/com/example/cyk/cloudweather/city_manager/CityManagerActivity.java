package com.example.cyk.cloudweather.city_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.cyk.cloudweather.WeatherActivity;
import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.db.DBManager;
import com.example.cyk.cloudweather.db.DatabaseBean;

import java.util.ArrayList;
import java.util.List;

public class CityManagerActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView addIv,backIv,deleteIv;
    ListView cityLv;
    //显示列表数据源
    List<DatabaseBean> mDatas;
    private CityManagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manager);
        addIv = findViewById(R.id.city_iv_add);
        backIv = findViewById(R.id.city_iv_back);
        deleteIv = findViewById(R.id.city_iv_delete);
        cityLv = findViewById(R.id.city_lv);
        mDatas = new ArrayList<>();
        //添加点击事件
        addIv.setOnClickListener(this);
        backIv.setOnClickListener(this);
        deleteIv.setOnClickListener(this);

        //设置适配器
        adapter = new CityManagerAdapter(this,mDatas);
        cityLv.setAdapter(adapter);
        //添加ListView的点击事件动作
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String city = mDatas.get(i).getCity();
                Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("city",city);
                startActivity(intent);
            }
        };
        cityLv.setOnItemClickListener(itemClickListener);
    }

    /*获取数据库中真实数据源，添加到原有数据源当中，提示适配器更新*/
    @Override
    protected void onResume() {
        super.onResume();
        List<DatabaseBean> list = DBManager.queryALLInfo();
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.city_iv_add:
                int cityCount = DBManager.getCityCount();
                if(cityCount < 10 ){
                    Intent intent = new Intent(CityManagerActivity.this,SearchCityActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(this,"存储城市数量已达上限",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.city_iv_back:
                finish();
                break;
            case R.id.city_iv_delete:
                Intent intent = new Intent(CityManagerActivity.this,DeleteCityActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
