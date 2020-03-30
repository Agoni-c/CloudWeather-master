package com.example.cyk.cloudweather;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.cyk.cloudweather.service.UpdateService;


public class WidgetProvider extends AppWidgetProvider {

    private static final String TAG = "WEATHER_WIDGET";
    private Intent intent;

    //  在更新 widget 时，被执行
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate: ");
        intent = new Intent(context, UpdateService.class);
        context.startService(intent);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive: ");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        context.stopService(intent);
        super.onDeleted(context, appWidgetIds);
        Log.d(TAG, "onDeleted: ");
    }

    // 最后一个widget被删除时调用
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    // 第一个widget被创建时调用
    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled: ");
//        // 在第一个 widget 被创建时，开启服务
//          做法：可以在manifest中
//        <service android:name=".ExampleAppWidgetService" >
//            <intent-filter>
//                <action android:name="android.appwidget.action.EXAMPLE_APP_WIDGET_SERVICE" />
//            </intent-filter>
//        </service>
//        context.startService(EXAMPLE_SERVICE_INTENT);
        super.onEnabled(context);
    }

}
