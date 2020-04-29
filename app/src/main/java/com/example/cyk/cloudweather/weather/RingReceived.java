package com.example.cyk.cloudweather.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RingReceived extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.example.cyk.cloudweather.weather.RING".equals(intent.getAction())){
            //跳转到Activity
            Intent intent1=new Intent(context,RingAlarmActivity.class);
            //给Intent设置标志位
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}
