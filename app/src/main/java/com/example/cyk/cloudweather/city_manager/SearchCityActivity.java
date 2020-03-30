package com.example.cyk.cloudweather.city_manager;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cyk.cloudweather.WeatherActivity;
import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.gson.Weather;
import com.example.cyk.cloudweather.http.OkHttp;
import com.example.cyk.cloudweather.json.WeatherJson;

import java.io.IOException;

public class SearchCityActivity extends AppCompatActivity implements View.OnClickListener {
    EditText searchEd;
    ImageView submitIv;
    GridView searchGv;
    private ArrayAdapter<String> adapter;
    String[] hotCity = {"北京","上海","广州","深圳","佛山","南京","苏州","厦门","武汉","成都","长沙","珠海"};
    String city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);
        searchEd = findViewById(R.id.search_ed);
        submitIv = findViewById(R.id.search_iv_submit);
        searchGv = findViewById(R.id.search_gv);
        submitIv.setOnClickListener(this);
        //设置适配器
        adapter = new ArrayAdapter<>(this,R.layout.search_item,hotCity);
        searchGv.setAdapter(adapter);
        //设置监听
        setListener();

    }

    private void setListener(){
        searchGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                city = hotCity[i];
                Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("city",city);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.search_iv_submit:
                city = searchEd.getText().toString();
                if (!TextUtils.isEmpty(city)){
                    //判断是否能够找到这个城市
                    String weatherUrl = "https://api.heweather.net/s6/weather?location="+city+"&key=b3f12f4deb964e7289fa38050148e5b5";
                    OkHttp.sendRequestOkHttpForGet(weatherUrl, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(getApplicationContext(),"未查找到此城市天气信息",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseText = response.body().string();
                            Weather weather = WeatherJson.getWeatherResponse(responseText);
                            if (weather != null && "ok".equals(weather.status)){
                                Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("city",city);
                                startActivity(intent);
                            }else {
                                Toast.makeText(getApplicationContext(),"未查找到此城市天气信息",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(this,"输入内容不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
