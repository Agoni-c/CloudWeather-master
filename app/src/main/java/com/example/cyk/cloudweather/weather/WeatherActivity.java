package com.example.cyk.cloudweather.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.example.cyk.cloudweather.baidu.BaiduLocation;
import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.city_manager.CityManagerActivity;
import com.example.cyk.cloudweather.db.DBManager;
import com.example.cyk.cloudweather.gson.Weather;
import com.example.cyk.cloudweather.http.MyCallBack;
import com.example.cyk.cloudweather.http.MyHttp;
import com.example.cyk.cloudweather.json.WeatherJson;
import com.example.cyk.cloudweather.util.IconUtil;
import com.google.android.material.navigation.NavigationView;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.litepal.util.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity implements IUiListener,View.OnClickListener {
    private static final String TAG = "WeatherActivity";
    private static final int CITY_MANAGER = 0;
    //判断百度是否完成调用，因为百度定位是异步实现的，
    // 主线程用不能停下来等他，所以这个暂时没有处理好解决
    public static boolean isBDLocationOk = false;
    //是否第一次进入onReceiveLocation函数
    private boolean isFirstonReceiveLocation = true;
    //定位返回指针
    BaiduLocation baiduLocation;
    //定位城市的天气Id和城市名
    public static String locationCountyWeatherId;
    public static String locationCountyWeatherName = null;
    //定位成功Toast提示msg
    private final int TOAST_LOCATION_SUCCEED = 1;
    //ui切换的提示，进度加载
    private ProgressDialog progressDialog;
    private PopupWindow mPopWindow;
    //qq分享
    private Tencent mTencent;
    private String APP_ID = "1109733371";
    //相机相册
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private Uri imageUri;


//    ImageView addIv,moreIv;
    private ImageView backgroundImage;
    private TextView toolbar_title;
    LinearLayout pointLayout;
    ViewPager mainVp;
    PagerAdapter adapter;
    //Viewpager数据源
    List<Fragment> fragmentList;
    //需要显示的城市集合
    List<String> cityList;
    //ViewPager的页数指示器显示集合
    List<ImageView> imageViewList;
    private DrawerLayout drawLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        mTencent = Tencent.createInstance(APP_ID, getApplicationContext());
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //禁止手势滑动
        drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//让导航按钮显示出来
            actionBar.setHomeAsUpIndicator(R.mipmap.add);//设置导航按钮图标
        }

        //先申请定位需要用到的权限
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        //没有权限则申请
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            //有权限开启定位功能，异步
            baiduLocation = new BaiduLocation(getApplicationContext());
            //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
            baiduLocation.registerListener(mListener);
            //注册监听
            baiduLocation.setLocationOption(baiduLocation.getDefaultLocationClientOption());
            baiduLocation.start();
            //显示进度条
            LogUtil.d(TAG, "initView: baiduLocation worked");
        }
//        //防止界面无响应，显示进度圈
//        showProgressDialog();

//        addIv = findViewById(R.id.main_iv_add);
//        moreIv = findViewById(R.id.main_iv_more);
        backgroundImage = findViewById(R.id.background_img);
        toolbar_title = findViewById(R.id.toolbar_title);
        pointLayout = findViewById(R.id.main_layout_point);
        mainVp = findViewById(R.id.main_vp);

//        addIv.setOnClickListener(this);
//        moreIv.setOnClickListener(this);

        fragmentList = new ArrayList<>();

        //获取数据库包含的城市列表信息
        cityList = DBManager.queryALLCityName();
        imageViewList = new ArrayList<>();

        if (cityList.size() == 0){
            cityList.add("惠安");
        }
        /*搜索界面点击跳转此界面，会传值，此处获取*/
        Intent intent = getIntent();
        String city = intent.getStringExtra("city");
        if (!cityList.contains(city)&&!TextUtils.isEmpty(city)){
            cityList.add(city);
        }
        //初始化ViewPager页面的方法
        initPager();
        adapter = new CityFragmentPagerAdapter(getSupportFragmentManager(),fragmentList);
        mainVp.setAdapter(adapter);
        //创建小圆点
        initPoint();
        //设置第一个城市信息
        mainVp.setCurrentItem(0);
        toolbar_title.setText(cityList.get(0));
        String s = DBManager.queryInfoByCity(cityList.get(0));
        Weather weather = WeatherJson.getWeatherResponse(s);
        String weatherInfoCode = weather.now.code;
        int backgroundImageId = IconUtil.getDayBack(weatherInfoCode);
        backgroundImage.setImageResource(backgroundImageId);
        //设置ViewPager页面监听
        setPagerListener();

        navView.setCheckedItem(R.id.nav_setting);//将Call菜单项设置为默认选中
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_setting:
                        drawLayout.closeDrawers();//关闭滑动菜单
                        Toast.makeText(WeatherActivity.this,"You clicked setting" , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_about:
                        Intent intent = new Intent(WeatherActivity.this, AboutActivity.class);
                        startActivity(intent);
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
    }

    private void clearCache(){
        /*清除缓存的函数*/
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("确定删除所有缓存记录么？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DBManager.deleteALLInfo();
                Toast.makeText(WeatherActivity.this,"已清除全部缓存！",Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("取消",null);
        builder.create().show();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.share:
                break;
            case R.id.more:
                drawLayout.openDrawer(GravityCompat.END);
                break;
            case android.R.id.home:
                Intent intent = new Intent(this,CityManagerActivity.class);
                startActivityForResult(intent,CITY_MANAGER);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Android原生分享功能
     * 默认选取手机所有可以分享的APP
     */
    public void allShare(){
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("text/plain");//设置分享内容的类型
        share_intent.putExtra(Intent.EXTRA_SUBJECT, "share");//添加分享内容标题
        share_intent.putExtra(Intent.EXTRA_TEXT, "Hi,云烟成雨");//添加分享内容
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, "share");
        startActivity(share_intent);
    }

    public void shareToQQ() {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "Hi,云烟成雨");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "欢迎使用云烟成雨");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://blog.csdn.net/new_one_object");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://avatar.csdn.net/B/0/1/1_new_one_object.jpg");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "云烟成雨");
        mTencent.shareToQQ(this, params, this);
    }

    public void shareToQZone() {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "Hi,云烟成雨");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "欢迎使用云烟成雨");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://blog.csdn.net/new_one_object");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://avatar.csdn.net/B/0/1/1_new_one_object.jpg");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "云烟成雨");
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        mTencent.shareToQQ(this, params, this);
    }

    private void showPopupWindow() {
        //设置contentView
        View contentView = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.popuplayout, null);
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
        View rootview = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.activity_weather, null);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);

    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.photo:{
                /*
                第一个参数是文件目录
                第二个参数是文件的名称
                应用的关联的缓存目录：就是SD卡中专门用于存放当前应用缓存数据的位置。
                getExternalCacheDir()就可以获取到这个目录
                /sdcard/Android/data/<package name>/cache
                Android6.0系统开始，读写SD卡被列为危险权限如果你要使用其他的SD
                卡储存位置都需要进行运行时权限处理才行，使用这个目录则可以跳过这个操作
                 */
                //创建一个文件对象，用于储存拍照后的图片
                File outputImage = new File(getExternalCacheDir(),"output_image.jpg");

                try {
                    if(outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.canExecute();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //对运行的设备系统进行判断如果低于Android 7.0
                // 就调用Uri。fromFile()将file独享转化为uri对象
//                if (Build.VERSION.SDK_INT >= 24){
//                    //将file转化为封装过得Uri对象
//                    /*
//                    getUriFroFile()
//                    第一个参数是content
//                    第二个参数是任何唯一的字符串
//                    第三个参数是File对象
//                    Android7.0开始意识到直接使用真实地址是不安全的
//                    FileProvider是文件内容提供器，
//                    使用提供与内容提供器相似的机制来实现输几局的保存。
//                    可以选择性的将封装过得Uri共享给外部。
//                     */
//                    imageUri = FileProvider.getUriForFile(WeatherActivity.this,
//                            "com.example.cyk.cloudweather.fileProvider",
//                            outputImage);
//                }else {
                    imageUri = Uri.fromFile(outputImage);
//                }

                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                //指定图片的输出地址
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
                mPopWindow.dismiss();
                drawLayout.closeDrawers();//关闭滑动菜单
            }
            break;
            case R.id.choose_picture:{
                if (ContextCompat.checkSelfPermission(WeatherActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(WeatherActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
                mPopWindow.dismiss();
                drawLayout.closeDrawers();//关闭滑动菜单
            }
            break;
            case R.id.cancle:{
//                Toast.makeText(this,"clicked cancle",Toast.LENGTH_SHORT).show();
                mPopWindow.dismiss();
                drawLayout.closeDrawers();//关闭滑动菜单
            }
            break;
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("images/*");
        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                }else {
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将图片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().
                                openInputStream(imageUri));
                        backgroundImage.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机的版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上的系统使用的这个方法处理图片
                        handleImageOnKitKat(data);
                    }else {
                        //4.4以下的系统使用的图片处理方法
                        handleImageBeforeKitKat(data);
                    }
                }
            default:
                break;
        }

    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
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
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
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
            backgroundImage.setImageBitmap(bitmap);
        }else {
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    private void initPager(){
        //创建Fragment对象，添加到ViewPager数据源中
        for (int i = 0; i < cityList.size(); i++){
            CityWeatherFragment cityWeatherFragment = new CityWeatherFragment();
            Bundle bundle = new Bundle();
            bundle.putString("city",cityList.get(i));
            cityWeatherFragment.setArguments(bundle);
            fragmentList.add(cityWeatherFragment);
        }

    }

    private void setPagerListener(){
        //设置监听事件
        mainVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < imageViewList.size(); i++){
                    imageViewList.get(i).setImageResource(R.mipmap.point1);
                }
                imageViewList.get(position).setImageResource(R.mipmap.point2);
                toolbar_title.setText(cityList.get(position));
                String s = DBManager.queryInfoByCity(cityList.get(position));
                Weather weather = WeatherJson.getWeatherResponse(s);
                String weatherInfoCode = weather.now.code;
                int backgroundImageId = IconUtil.getDayBack(weatherInfoCode);
                backgroundImage.setImageResource(backgroundImageId);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initPoint(){
        //创建小圆点ViewPager页面指示器的函数
        final int imgSize = 20;
        for (int i = 0; i < fragmentList.size(); i++){
            ImageView pointIv = new ImageView(this);
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

    //页面重新加载时调用函数，这个函数是在页面重新获取焦点之前进行调用，此处完成ViewPager的页面更新
    @Override
    protected void onRestart() {
        super.onRestart();
        //获取数据库中剩下的城市列表
        List<String> list = DBManager.queryALLCityName();
        if (list.size() == 0){
            list.add("惠安");
        }
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


    /**
     * 百度定位结果回调，重写onReceiveLocation方法
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            LogUtil.d(TAG, "onReceiveLocation: start");
            if (isFirstonReceiveLocation) {
                isFirstonReceiveLocation = false;
            } else {
                return;
            }
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                Log.v(TAG, "latitude " + location.getLatitude() + "  longitude:" + location.getLongitude());
                StringBuilder currentPosition = new StringBuilder();
                currentPosition.append(location.getLatitude()).append(","); // 经度
                currentPosition.append(location.getLongitude());// 纬度
                locationCountyWeatherId = currentPosition.toString();
                String weatherUrl = "https://api.heweather.net/s6/weather?location="+locationCountyWeatherId+"&key=88796e0ed0b045c0aba50fd9c2239a35";
                MyHttp.sendRequestOkHttpForGet(weatherUrl, new MyCallBack() {
                    @Override
                    public void onFailure(IOException e) {
                        LogUtil.d(TAG, "onFailure: netWork error");
                    }
                    @Override
                    public void onResponse(String response) throws IOException {
                        final String responseText = response;
                        LogUtil.d(TAG, "onResponse: responseText: " + responseText);
                        final Weather weather = WeatherJson.getWeatherResponse(responseText);
                        if (weather != null && "ok".equals(weather.status)) {
                            //获取的地理位置信息有效，保存定位结果，等下次Oncreate的时候直接调用
                            locationCountyWeatherName = weather.basic.countyName;
                            int i = DBManager.updateInfoByCity(locationCountyWeatherName,responseText);
                            if (i <= 0){
                                //更新数据库失败，无此城市，增加该城市信息
                                DBManager.addCityInfo(locationCountyWeatherName,responseText);
                            }
                            Message msg = new Message();
                            msg.what = TOAST_LOCATION_SUCCEED;
                            myHandler.sendMessage(msg);
                        } else {
                            //定位的城市信息无效，保存为空
                            locationCountyWeatherId = null;
                            locationCountyWeatherName = null;
                            int i = DBManager.updateInfoByCity(locationCountyWeatherName,responseText);
                            if (i <= 0){
                                //更新数据库失败，无此城市，增加该城市信息
                                DBManager.addCityInfo(locationCountyWeatherName,responseText);
                            }
                        }
                    }
                });
                Log.d(TAG, "locationCountyWeatherId: " + locationCountyWeatherId);

                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                Log.d(TAG, "onReceiveLocation: " + sb.toString() );
            } else {
                //网络定位失败时把数据置零
                LogUtil.d(TAG, "网络定位失败时把数据置零");
            }
        }

        public void onConnectHotSpotMessage(String s, int i){
        }
    };
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TOAST_LOCATION_SUCCEED) {
                Toast.makeText(WeatherActivity.this, "城市定位成功", Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        baiduLocation.unregisterListener(mListener); //注销掉监听
        baiduLocation.stop(); //停止定位服务

        super.onStop();
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
                            Log.e("123", "weather");
                        }
                    }
                });

            }
        });


    }
    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onComplete(Object o) {
        Toast.makeText(this, o.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(UiError uiError) {
        Toast.makeText(this, uiError.errorMessage + "--" + uiError.errorCode + "---" + uiError.errorDetail, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel() {
        Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show();
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (mTencent != null) {
//            Tencent.onActivityResultData(requestCode, resultCode, data, this);
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
}
