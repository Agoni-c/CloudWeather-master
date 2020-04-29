package com.example.cyk.cloudweather.weather;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cyk.cloudweather.MainActivity;
import com.example.cyk.cloudweather.UniteApp;
import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.camera.WaterCameraActivity;
import com.example.cyk.cloudweather.city_manager.CityManagerActivity;
import com.example.cyk.cloudweather.db.DBManager;
import com.example.cyk.cloudweather.gson.Weather;
import com.example.cyk.cloudweather.json.WeatherJson;
import com.example.cyk.cloudweather.util.IconUtil;
import com.google.android.material.navigation.NavigationView;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class WeatherFragment extends Fragment implements IUiListener,View.OnClickListener{
    private static final String PREFERENCES_NAME = "SettingActivity";
    private static final String LOCATION_NAME = "Location";
    private static final String TAG = "WeatherFragment";
    //相机相册
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    //定位城市的天气Id和城市名
    public int pagePosition;
    //当前页面城市
    private String city;
    //弹出拍照窗口
    private PopupWindow mPopWindow;
    //背景图
    private ImageView backgroundImage;
    //标题栏
    private androidx.appcompat.widget.Toolbar toolbar;
    private TextView toolbar_title;
    //控件
    private DrawerLayout drawLayout;
    LinearLayout pointLayout;
    ViewPager mainVp;
    PagerAdapter adapter;
    //Viewpager数据源
    List<CityWeatherFragment> cityWeatherFragmentList= new ArrayList<>();
    List<Fragment> fragmentList = new ArrayList<>();
    //需要显示的城市集合
    List<String> cityList;
    //ViewPager的页数指示器显示集合
    List<ImageView> imageViewList = new ArrayList<>();
    //数据库，保存设置信息
    private SharedPreferences sharedPreferences;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //找到主界面view后，就可以进行UI的操作了。
        //注意：因为主界面现在是view，所以在找寻控件时要用view.findViewById
        View view=inflater.inflate(R.layout.fragment_weather,container,false);
        //标题栏
        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar);
        //导航控件
        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//让导航按钮显示出来
            actionBar.setHomeAsUpIndicator(R.mipmap.add);//设置导航按钮图标
        }
        setHasOptionsMenu(true);

        //控件加载
        backgroundImage = view.findViewById(R.id.background_img);
        toolbar_title = view.findViewById(R.id.toolbar_title);
        pointLayout = view.findViewById(R.id.main_layout_point);
        mainVp = view.findViewById(R.id.main_vp);
        toolbar = view.findViewById(R.id.toolbar);

        //获取数据库包含的城市列表信息
        cityList = DBManager.queryALLCityName();

        //初始化ViewPager页面的方法
        initPager();
        adapter = new CityFragmentPagerAdapter(getChildFragmentManager(),fragmentList);
        mainVp.setAdapter(adapter);
        //创建小圆点
        initPoint();

        //设置第一个城市信息
        mainVp.setCurrentItem(0);
        //获取数据库中存储的定位城市
        SharedPreferences sp= getContext().getSharedPreferences(LOCATION_NAME, Context.MODE_PRIVATE);
        //根据key取出对应的值
        String location = sp.getString("location", "");
        toolbar_title.setText(location);
        String s = DBManager.queryInfoByCity(location);
        Weather weather = WeatherJson.getWeatherResponse(s);
        String weatherInfoCode = weather.now.code;
        //获取当前时间
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour > 6 && hour < 19) {
            backgroundImage.setImageResource(IconUtil.getDayBack(weatherInfoCode));
        } else {
            backgroundImage.setImageResource(IconUtil.getNightBack(weatherInfoCode));
        }

        //设置ViewPager页面监听
        setPagerListener();

        //滑动菜单
        drawLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        //禁止手势滑动
        drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        NavigationView navView = (NavigationView) view.findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_setting);//将Call菜单项设置为默认选中
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_setting:
                        Intent intent = new Intent(getContext(), SettingActivity.class);
                        startActivity(intent);
                        drawLayout.closeDrawers();//关闭滑动菜单
                        break;
                    case R.id.nav_about:
                        Intent intent1 = new Intent(getContext(), AboutActivity.class);
                        startActivity(intent1);
                        drawLayout.closeDrawers();//关闭滑动菜单
                        break;
                    case R.id.nav_clean:
                        clearCache();
                        drawLayout.closeDrawers();//关闭滑动菜单
                        break;
                    case R.id.nav_png:
                        showPopupWindow();
                        break;
                }
                return true;
            }
        });
        return view;
    }

    @Override
    public void  onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.toolbar, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    /**
     * 初始化Viewpager
     */
    private void initPager(){
        //创建Fragment对象，添加到ViewPager数据源中
        for (int i = 0; i < cityList.size(); i++){
            CityWeatherFragment cityWeatherFragment = new CityWeatherFragment();
            Bundle bundle = new Bundle();
            bundle.putString("city",cityList.get(i));
            cityWeatherFragment.setArguments(bundle);
            fragmentList.add(cityWeatherFragment);
            cityWeatherFragmentList.add(cityWeatherFragment);
        }
    }

    /**
     * 设置页面监听
     */
    private void setPagerListener(){
        //设置监听事件
        mainVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                pagePosition = position;
                for (int i = 0; i < imageViewList.size(); i++){
                    imageViewList.get(i).setImageResource(R.mipmap.point1);
                }
                imageViewList.get(position).setImageResource(R.mipmap.point2);
                toolbar_title.setText(cityList.get(position));
                String s = DBManager.queryInfoByCity(cityList.get(position));
                Weather weather = WeatherJson.getWeatherResponse(s);
                String weatherInfoCode = weather.now.code;
                //获取当前时间
                Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
                int hour = c.get(Calendar.HOUR_OF_DAY);
                if (hour > 6 && hour < 19) {
                    backgroundImage.setImageResource(IconUtil.getDayBack(weatherInfoCode));
                } else {
                    backgroundImage.setImageResource(IconUtil.getNightBack(weatherInfoCode));
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * ViewPager页面指示器，小圆点
     */
    private void initPoint(){
        //创建小圆点ViewPager页面指示器的函数
        final int imgSize = 20;
        for (int i = 0; i < fragmentList.size(); i++){
            ImageView pointIv = new ImageView(getActivity());
            pointIv.setImageResource(R.mipmap.point1);
            pointIv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) pointIv.getLayoutParams();
            //给小圆点一个默认大小
            layoutParams.height = imgSize;
            layoutParams.width = imgSize;
            layoutParams.setMargins(0,0,20,0);
            imageViewList.add(pointIv);
            pointLayout.addView(pointIv);
        }
        imageViewList.get(0).setImageResource(R.mipmap.point2);

    }

    /**
     * 页面重新加载时调用函数，这个函数是在页面重新获取焦点之前进行调用，此处完成ViewPager的页面更新
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "onHiddenChanged:1 " );
        //获取数据库中剩下的城市列表
        List<String> list = DBManager.queryALLCityName();
        //重新加载之前，清空原本数据源
        cityList.clear();
        cityList.addAll(list);
        //剩下城市也要创建对应的Fragment页面
        fragmentList.clear();
        initPager();
        adapter.notifyDataSetChanged();
        //页面数量发生改变，指示器的数量也会发生变化，我们需要重新添加指示器
        imageViewList.clear();
        //将布局中所有元素全部移除
        pointLayout.removeAllViews();
        initPoint();
        mainVp.setCurrentItem(fragmentList.size()-1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.share:
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.putExtra("city",cityList.get(pagePosition));
                startActivity(intent);
                break;
            case R.id.more:
                drawLayout.openDrawer(GravityCompat.END);
                break;
            case android.R.id.home:
                Intent intent1 = new Intent(getActivity(), CityManagerActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.photo:{
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                city = toolbar_title.getText().toString();
                intent.putExtra("city",city);
                startActivity(intent);
                mPopWindow.dismiss();
                drawLayout.closeDrawers();//关闭滑动菜单
            }
            break;
            case R.id.choose_picture:{
                Intent intent = new Intent(getActivity(), WaterCameraActivity.class);
                city = toolbar_title.getText().toString();
                intent.putExtra("city",city);
                startActivity(intent);
                mPopWindow.dismiss();
                drawLayout.closeDrawers();//关闭滑动菜单
//                if (ContextCompat.checkSelfPermission(getActivity(),
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                        PackageManager.PERMISSION_GRANTED) {
//
//                    ActivityCompat.requestPermissions(getActivity(),
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//                }else {
//                    openAlbum();
//                }
//                mPopWindow.dismiss();
//                drawLayout.closeDrawers();//关闭滑动菜单
            }
            break;
            case R.id.cancle:{
                mPopWindow.dismiss();
                drawLayout.closeDrawers();//关闭滑动菜单
            }
            break;
        }
    }

    /**
     * 打开相册
     */
    public void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");//选择照片后毁掉onActivityResult方法
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    //判断手机的版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上的系统使用的这个方法处理图片
                        handleImageOnKitKat(data);
                    }else {
                        //4.4以下的系统使用的图片处理方法
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }

    }

    /**
     * 质量压缩方法
     * 照片
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 98;
        while (baos.toByteArray().length / 1024 > 3072) { // 循环判断如果压缩后图片是否大于 3Mb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 2;// 每次都减少2
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 添加水印
     */
    private Bitmap addWater(Bitmap mBitmap) {
        android.graphics.Bitmap.Config bitmapConfig =
                mBitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        //获取原始图片与水印图片的宽与高
        int mBitmapWidth = mBitmap.getWidth();
        int mBitmapHeight = mBitmap.getHeight();

        DisplayMetrics dm = getResources().getDisplayMetrics();
        float screenWidth = dm.widthPixels;//1080
        float mBitmapWidthF = mBitmapWidth;

        Bitmap mNewBitmap = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, bitmapConfig);
        Canvas canvas = new Canvas(mNewBitmap);
        //向位图中开始画入MBitmap原始图片
        canvas.drawBitmap(mBitmap, 0, 0, null);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setDither(true); //获取跟清晰的图像采样
        paint.setFilterBitmap(true);//过滤一些
        String text = "云烟成语";
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        float textW = paint.measureText(text);
        float x = (mBitmapWidth / 2) - (textW / 2);
        float textH = -paint.ascent() + paint.descent();
        canvas.drawText(text, x, (mBitmapHeight * 3 / 4), paint);//mBitmapWidth=3024

//        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.save();
        return mNewBitmap;

    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(getContext(),uri)){
            //如果是document类型的Uri，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);//根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri,String selection){
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.
                        Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if (imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            bitmap = compressImage(bitmap);
            bitmap = addWater(bitmap);
            savePicture(bitmap);
        }else {
            Toast.makeText(getActivity(),"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 保存图片
     */
    private void savePicture(Bitmap bitmap) {
        try {
            FileOutputStream outStream = null;
            String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "testPhoto";
            String fileName = filePath + File.separator + String.valueOf(System.currentTimeMillis()) + ".jpg";
            File file = new File(fileName);
            if (!file.exists()) file.getParentFile().mkdirs();
            outStream = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            if (outStream != null) outStream.close();
            // 最后通知图库更新
            getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
            Toast.makeText(getContext(), "图片已保存至:" + fileName, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 弹出窗口，天气水印拍照
     */
    private void showPopupWindow() {
        //设置contentView
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.popuplayout, null);
        mPopWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        //设置各个控件的点击响应
        TextView photo = (TextView)contentView.findViewById(R.id.photo);
        TextView choose_picture = (TextView)contentView.findViewById(R.id.choose_picture);
        TextView cancle = (TextView)contentView.findViewById(R.id.cancle);
        photo.setOnClickListener(this);
        choose_picture.setOnClickListener(this);
        cancle.setOnClickListener(this);
        //显示PopupWindow
        View rootview = LayoutInflater.from(getActivity()).inflate(R.layout.activity_weather, null);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);

    }

    /**
     * 清除缓存信息
     */
    private void clearCache(){
        /*清除缓存的函数*/
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("提示信息").setMessage("确定删除所有缓存记录么？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //先获取对应的Share
                sharedPreferences = getContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
                //获取Editor对象，这个对象用于写入，可理解为编辑
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("map", false).apply();
                editor.putBoolean("warn_message", false).apply();
                editor.putBoolean("notice_message", false).apply();
                DBManager.deleteALLInfo();
                //重启app
                Intent intent = new Intent(UniteApp.getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                UniteApp.getContext().startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
                Toast.makeText(getContext(),"已清除全部缓存！",Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("取消",null);
        builder.create().show();
    }


    /**
     * 改变标题栏
     */
    public void changeTitle(boolean b) {
        if (b) {
            toolbar.setVisibility(View.GONE);
        }else {
            toolbar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onComplete(Object o) {
        Toast.makeText(getContext(), o.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(UiError uiError) {
        Toast.makeText(getContext(), uiError.errorMessage + "--" + uiError.errorCode + "---" + uiError.errorDetail, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel() {
        Toast.makeText(getContext(), "取消", Toast.LENGTH_SHORT).show();
    }


}
