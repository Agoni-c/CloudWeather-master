package com.example.cyk.cloudweather.city_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cyk.cloudweather.R;

import java.util.List;

public class DeleteCityAdapter extends BaseAdapter {

    Context context;
    List<String> mDatas;
    List<String> deleteCitys;

    public DeleteCityAdapter(Context context, List<String> mDatas,List<String> deleteCitys) {
        this.context = context;
        this.mDatas = mDatas;
        this.deleteCitys = deleteCitys;
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.citydelete_item,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final String city = mDatas.get(i);
        viewHolder.tv.setText(city);
        viewHolder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatas.remove(city);
                deleteCitys.add(city);
                notifyDataSetChanged();//删除了提示适配器更新

            }
        });
        return view;
    }

    class ViewHolder{
        TextView tv;
        ImageView iv;
        public ViewHolder(View itemView){
            tv = itemView.findViewById(R.id.delete_item_tv);
            iv = itemView.findViewById(R.id.delete_item_iv);
        }
    }
}
