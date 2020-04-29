package com.example.cyk.cloudweather.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.db.DBManager;
import com.example.cyk.cloudweather.gson.Weather;
import com.example.cyk.cloudweather.json.WeatherJson;
import com.example.cyk.cloudweather.weather.CameraActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import androidx.viewpager.widget.ViewPager;

/**
 * 分享图片
 * Created by chen 2020.4.21
 */
public class ShareImageUtil {
    public static int h = 0;

    /**
     * 因为天气页面是可以滑动 的所以截取
     * 截取scrollview的屏幕
     **/
    public static void getBitmapByView(Context mContext, final ScrollView scrollView,String city) {
        // 获取listView实际高度
        h = 0;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        String s = DBManager.queryInfoByCity(city);
        Weather weather = WeatherJson.getWeatherResponse(s);
        String weatherInfoCode = weather.now.code;
        //获取当前时间
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour > 6 && hour < 19) {
            scrollView.setBackgroundResource(IconUtil.getDayBack(weatherInfoCode));
        } else {
            scrollView.setBackgroundResource(IconUtil.getNightBack(weatherInfoCode));
        }
        // 创建对应大小的bitmap
        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);


        Bitmap head = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.loading_image);
        Bitmap foot = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.loads);
        Bitmap v = toConformBitmap(head, bitmap, foot);


        try {
            FileOutputStream outStream = null;
            String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "testPhoto";
            String fileName = filePath + File.separator + String.valueOf(System.currentTimeMillis()) + ".jpg";
            File file = new File(fileName);
            if (!file.exists()) file.getParentFile().mkdirs();
            outStream = new FileOutputStream(fileName);
            v.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            if (outStream != null) outStream.close();
            // 最后通知图库更新
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
            Toast.makeText(mContext, "图片已保存至:" + fileName, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 合并图片
     *
     * @param head
     * @param kebiao
     * @param san
     * @return
     */
    public static Bitmap toConformBitmap(Bitmap head, Bitmap kebiao, Bitmap san) {
        if (head == null) {
            return null;
        }
        int headWidth = head.getWidth();
        int kebianwidth = kebiao.getWidth();
        int fotwid = san.getWidth();

        int headHeight = head.getHeight();
        int kebiaoheight = kebiao.getHeight();
        int footerheight = san.getHeight();
        //生成三个图片合并大小的Bitmap
        Bitmap newbmp = Bitmap.createBitmap(kebianwidth, headHeight + kebiaoheight + footerheight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        cv.drawBitmap(head, 0, 0, null);// 在 0，0坐标开始画入headBitmap

        //因为手机不同图片的大小的可能小了 就绘制白色的界面填充剩下的界面
        if (headWidth < kebianwidth) {
            System.out.println("绘制头");
            Bitmap ne = Bitmap.createBitmap(kebianwidth - headWidth, headHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(ne);
            canvas.drawColor(Color.WHITE);
            cv.drawBitmap(ne, headWidth, 0, null);
        }
        cv.drawBitmap(kebiao, 0, headHeight, null);// 在 0，headHeight坐标开始填充课表的Bitmap
        cv.drawBitmap(san, 0, headHeight + kebiaoheight, null);// 在 0，headHeight + kebiaoheight坐标开始填充课表的Bitmap
        //因为手机不同图片的大小的可能小了 就绘制白色的界面填充剩下的界面
        if (fotwid < kebianwidth) {
            System.out.println("绘制");
            Bitmap ne = Bitmap.createBitmap(kebianwidth - fotwid, footerheight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(ne);
            canvas.drawColor(Color.YELLOW);
            cv.drawBitmap(ne, fotwid, headHeight + kebiaoheight, null);
        }
        cv.save();// 保存
        cv.restore();// 存储
        //回收
        head.recycle();
        kebiao.recycle();
        san.recycle();
        return newbmp;
    }



}
