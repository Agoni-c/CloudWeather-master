package com.example.cyk.cloudweather.city_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.db.DatabaseBean;
import com.example.cyk.cloudweather.gson.Weather;
import com.example.cyk.cloudweather.json.WeatherJson;

import java.util.List;

public class CityManagerAdapter extends BaseAdapter{
    Context context;
    List<DatabaseBean> mDatas;

    public CityManagerAdapter(Context context,List<DatabaseBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.city_manager_item,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        DatabaseBean databaseBean = mDatas.get(i);
        viewHolder.cityTv.setText(databaseBean.getCity());
        final Weather weather = WeatherJson.getWeatherResponse(databaseBean.getContent());
        //获取今日天气情况
        String temperature = weather.now.temperature+"℃";
        String weatherInfo = weather.now.txt;
        String wind = weather.now.wind_direction+weather.now.wind_strength;
        String todayMax = weather.forecastList.get(0).max;
        String todayMin = weather.forecastList.get(0).min;
        String minandmax = todayMin + "~" + todayMax +"℃";
        viewHolder.tempTv.setText(temperature);
        viewHolder.conditionTv.setText(weatherInfo);
        viewHolder.windTv.setText(wind);
        viewHolder.tempRangeTv.setText(minandmax);
        return view;
    }

    class ViewHolder{
        TextView cityTv,conditionTv,tempTv,windTv,tempRangeTv;
        public ViewHolder(View itemView){
            cityTv = itemView.findViewById(R.id.city_item_tv_city);
            conditionTv = itemView.findViewById(R.id.city_item_tv_condition);
            tempTv = itemView.findViewById(R.id.city_item_tv_temp);
            windTv = itemView.findViewById(R.id.city_item_tv_wind);
            tempRangeTv = itemView.findViewById(R.id.city_item_tv_temprange);
        }
    }
}
