package com.example.cyk.cloudweather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class WeatherFragment extends Fragment {


    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //该View表示该碎片的主界面,最后要返回该view
        View view=inflater.inflate(R.layout.fragment_weather,container,false);
        //找到主界面view后，就可以进行UI的操作了。
        //注意：因为主界面现在是view，所以在找寻控件时要用view.findViewById
        TextView textView=view.findViewById(R.id.tv_weather);
        //textView.setText("");
        return view;

    }

}
