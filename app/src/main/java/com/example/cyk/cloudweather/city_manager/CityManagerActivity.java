package com.example.cyk.cloudweather.city_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.cyk.cloudweather.MainActivity;
import com.example.cyk.cloudweather.gson.Weather;
import com.example.cyk.cloudweather.http.MyCallBack;
import com.example.cyk.cloudweather.http.MyHttp;
import com.example.cyk.cloudweather.json.WeatherJson;
import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.db.DBManager;
import com.example.cyk.cloudweather.db.DatabaseBean;
import com.zaaach.citypicker.CityPickerActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CityManagerActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_CODE_PICK_CITY = 0;
    ImageView addIv,backIv,deleteIv;
    //需要显示的城市集合
    List<String> cityList;
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
                if(cityCount < 5 ){
                    startActivityForResult(new Intent(CityManagerActivity.this, CityPickerActivity.class),
                            REQUEST_CODE_PICK_CITY);
                }else {
                    Toast.makeText(this,"存储城市数量已达上限",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.city_iv_back:
                Intent intent = new Intent(CityManagerActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.city_iv_delete:
                Intent intent1 = new Intent(CityManagerActivity.this,DeleteCityActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    //返回选中城市
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_CODE_PICK_CITY && resultCode == RESULT_OK){
            if (data != null){
                String city = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
                //获取数据库包含的城市列表信息
                cityList = DBManager.queryALLCityName();
                if (!cityList.contains(city)&&!TextUtils.isEmpty(city)){
                    requestWeather(city);
                }
            }
        } else {

        }
    }

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
                            //更新城市信息
                            int i = DBManager.updateInfoByCity(weatherId,responseText);
                            if (i <= 0){
                                //更新数据库失败，无此城市，增加该城市信息
                                DBManager.addCityInfo(weatherId,responseText);
                            }
                            List<DatabaseBean> list = DBManager.queryALLInfo();
                            mDatas.clear();
                            mDatas.addAll(list);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

            }
        });
    }
}
