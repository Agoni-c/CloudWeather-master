package com.example.cyk.cloudweather.city_manager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.db.DBManager;

import java.util.ArrayList;
import java.util.List;

public class DeleteCityActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView errorIv,rightIv;
    ListView deleteLv;
    List<String> mDatas;//listview的数据源
    List<String> deleteCitys;
    private DeleteCityAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_city);
        errorIv = findViewById(R.id.delete_iv_error);
        rightIv = findViewById(R.id.delete_iv_below);
        deleteLv = findViewById(R.id.delete_lv);
        mDatas = new ArrayList<>();
        errorIv.setOnClickListener(this);
        rightIv.setOnClickListener(this);
        //存储删除的城市信息
        deleteCitys = new ArrayList<>();
        adapter = new DeleteCityAdapter(this,mDatas,deleteCitys);
        deleteLv.setAdapter(adapter);
    }


    @Override
    protected void onResume(){
        super.onResume();
        List<String> ciryList = DBManager.queryALLCityName();
        mDatas.addAll(ciryList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.delete_iv_error:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示")
                        .setMessage("确定放弃更改")
                        .setPositiveButton("放弃更改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                builder.setNegativeButton("取消",null);
                builder.create().show();
                break;
            case R.id.delete_iv_below:
                for(int i = 0; i < deleteCitys.size(); i++){
                    String city = deleteCitys.get(i);
                    //调用删除城市的函数
                    DBManager.deleteInfoByCity(city);
                }
                //删除成功返回上一级
                finish();
                break;
            default:
                break;
        }
    }
}
