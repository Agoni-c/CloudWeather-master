package com.example.cyk.cloudweather.weather;


import android.app.AlertDialog;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.cyk.cloudweather.MainActivity;
import com.example.cyk.cloudweather.baidu.BaiduLocation;
import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.db.DBManager;
import com.example.cyk.cloudweather.gson.Air;
import com.example.cyk.cloudweather.gson.Alarm;
import com.example.cyk.cloudweather.gson.Forecast_hourly;
import com.example.cyk.cloudweather.gson.Rain;
import com.example.cyk.cloudweather.gson.Weather;
import com.example.cyk.cloudweather.http.MyCallBack;
import com.example.cyk.cloudweather.http.MyHttp;
import com.example.cyk.cloudweather.json.WeatherJson;
import com.example.cyk.cloudweather.overlayutil.DrivingRouteOverlay;
import com.example.cyk.cloudweather.overlayutil.PoiOverlay;
import com.example.cyk.cloudweather.util.IconUtil;

import org.litepal.util.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class CityWeatherFragment extends Fragment implements View.OnClickListener{
    /**
     * 多天预报日期1
     */
    private TextView mDaysForecastTvDay1;

    /**
     * 多天预报日期2
     */
    private TextView mDaysForecastTvDay2;

    /**
     * 多天预报日期3
     */
    private TextView mDaysForecastTvDay3;

    /**
     * 多天预报日期4
     */
    private TextView mDaysForecastTvDay4;

    /**
     * 多天预报日期5
     */
    private TextView mDaysForecastTvDay5;

    /**
     * 多天预报日期6
     */
    private TextView mDaysForecastTvDay6;

    /**
     * 多天预报日期7
     */
    private TextView mDaysForecastTvDay7;

    /**
     * 多天预报白天天气类型图片1
     */
    private ImageView mDaysForecastWeaTypeDayIv1;

    /**
     * 多天预报白天天气类型图片2
     */
    private ImageView mDaysForecastWeaTypeDayIv2;

    /**
     * 多天预报白天天气类型图片3
     */
    private ImageView mDaysForecastWeaTypeDayIv3;

    /**
     * 多天预报白天天气类型图片4
     */
    private ImageView mDaysForecastWeaTypeDayIv4;

    /**
     * 多天预报白天天气类型图片5
     */
    private ImageView mDaysForecastWeaTypeDayIv5;

    /**
     * 多天预报白天天气类型图片6
     */
    private ImageView mDaysForecastWeaTypeDayIv6;

    /**
     * 多天预报白天天气类型图片7
     */
    private ImageView mDaysForecastWeaTypeDayIv7;


    /**
     * 多天预报白天天气类型文字1
     */
    private TextView mDaysForecastWeaTypeDayTv1;

    /**
     * 多天预报白天天气类型文字2
     */
    private TextView mDaysForecastWeaTypeDayTv2;

    /**
     * 多天预报白天天气类型文字3
     */
    private TextView mDaysForecastWeaTypeDayTv3;

    /**
     * 多天预报白天天气类型文字4
     */
    private TextView mDaysForecastWeaTypeDayTv4;

    /**
     * 多天预报白天天气类型文字5
     */
    private TextView mDaysForecastWeaTypeDayTv5;

    /**
     * 多天预报白天天气类型文字6
     */
    private TextView mDaysForecastWeaTypeDayTv6;

    /**
     * 多天预报白天天气类型文字7
     */
    private TextView mDaysForecastWeaTypeDayTv7;


    /**
     * 温度曲线
     */
    private WeatherChartView chartView;


    /**
     * 多天预报夜间天气类型图片1
     */
    private ImageView mDaysForecastWeaTypeNightIv1;

    /**
     * 多天预报夜间天气类型图片2
     */
    private ImageView mDaysForecastWeaTypeNightIv2;

    /**
     * 多天预报夜间天气类型图片3
     */
    private ImageView mDaysForecastWeaTypeNightIv3;

    /**
     * 多天预报夜间天气类型图片4
     */
    private ImageView mDaysForecastWeaTypeNightIv4;

    /**
     * 多天预报夜间天气类型图片5
     */
    private ImageView mDaysForecastWeaTypeNightIv5;

    /**
     * 多天预报夜间天气类型图片6
     */
    private ImageView mDaysForecastWeaTypeNightIv6;

    /**
     * 多天预报夜间天气类型图片7
     */
    private ImageView mDaysForecastWeaTypeNightIv7;

    /**
     * 多天预报夜间天气类型文字1
     */
    private TextView mDaysForecastWeaTypeNightTv1;

    /**
     * 多天预报夜间天气类型文字2
     */
    private TextView mDaysForecastWeaTypeNightTv2;

    /**
     * 多天预报夜间天气类型文字3
     */
    private TextView mDaysForecastWeaTypeNightTv3;

    /**
     * 多天预报夜间天气类型文字4
     */
    private TextView mDaysForecastWeaTypeNightTv4;

    /**
     * 多天预报夜间天气类型文字5
     */
    private TextView mDaysForecastWeaTypeNightTv5;

    /**
     * 多天预报夜间天气类型文字6
     */
    private TextView mDaysForecastWeaTypeNightTv6;

    /**
     * 多天预报夜间天气类型文字7
     */
    private TextView mDaysForecastWeaTypeNightTv7;

    /**
     * 多天预报风向1
     */
    private TextView mDaysForecastWindDirectionTv1;

    /**
     * 多天预报风向2
     */
    private TextView mDaysForecastWindDirectionTv2;

    /**
     * 多天预报风向3
     */
    private TextView mDaysForecastWindDirectionTv3;

    /**
     * 多天预报风向4
     */
    private TextView mDaysForecastWindDirectionTv4;

    /**
     * 多天预报风向5
     */
    private TextView mDaysForecastWindDirectionTv5;

    /**
     * 多天预报风向6
     */
    private TextView mDaysForecastWindDirectionTv6;

    /**
     * 多天预报风向7
     */
    private TextView mDaysForecastWindDirectionTv7;


    /**
     * 多天预报风力1
     */
    private TextView mDaysForecastWindPowerTv1;

    /**
     * 多天预报风力2
     */
    private TextView mDaysForecastWindPowerTv2;

    /**
     * 多天预报风力3
     */
    private TextView mDaysForecastWindPowerTv3;

    /**
     * 多天预报风力4
     */
    private TextView mDaysForecastWindPowerTv4;

    /**
     * 多天预报风力5
     */
    private TextView mDaysForecastWindPowerTv5;

    /**
     * 多天预报风力6
     */
    private TextView mDaysForecastWindPowerTv6;

    /**
     * 多天预报风力7
     */
    private TextView mDaysForecastWindPowerTv7;

    private static final String TAG = "CityWeatherFragment";
    private static final String PREFERENCES_NAME = "SettingActivity";
    private static final String LOCATION_NAME = "Location";
    //ui切换的提示，进度加载
    private ProgressDialog progressDialog;
    //空气质量
    private Air air;
    //降雨信息
    private Rain rain;
    //灾害预警信息
    private Alarm alarm;
    private boolean warn_message;
    //通知栏
    private boolean notice_message;
    //界面数据刷新变量
    private boolean isGetData = false;
    //刷新界面的控件
    public SwipeRefreshLayout swipeRefresh;
    //当前城市，实现下拉刷新功能
    private String city;
    private LatLng cityLatLng;
    //定位城市
    private String location;
    //地图
    private boolean map = true;
    private TextureMapView mMapView;
    private AutoCompleteTextView autoCompleteTextView = null;
    private AutoCompleteTextView autoCompleteTextViewSt = null;
    private LatLng latLng;
    private Button btnSeek;
    private ImageView map_center;
    private BaiduMap baiduMap;
    //创建POI检索
    private PoiSearch poiSearch;
    //创建路线规划检索
    private RoutePlanSearch routePlanSearch;
    private LatLng stLatLng,enLatLng;
    //创建联想检索
    private SuggestionSearch suggestionSearch;
    private SuggestionSearch suggestionSearchSt;
    //定位返回指针
    BaiduLocation baiduLocation;
    private boolean isFirstLocate = true;
    private BitmapDescriptor bitmap;
    double centerLatitude;
    double centerLongitude;
    private String centerDistrict;
    private RelativeLayout map_ll;
    //地图 Marker 覆盖物
    private Weather markerWeather;

    /**
     * 天气的整体信息
     */
    private ImageView weatherInfoImage;
    private ScrollView weatherLayout;
    private MapContainer map_container;

    private TextView title_update_time;//时间
    private TextView temperatureText;//温度
    private TextView minandmaxText;//当日最低温和最高温
    private TextView weatherInfoText;//天气信息
    private TextView weatherWindText;//风向及风力
    private TextView weather_aqi_qlty;//空气质量
    private ImageView weatherMap,weatherNormal;

    //今日详情
    private LinearLayout now_ll;
    private ImageView humImage;
    private ImageView presImage;
    private ImageView visImage;
    private ImageView windImage;
    private TextView humInfo;
    private TextView visInfo;
    private TextView presInfo;
    private TextView windsc;
    private TextView winddir;

    //今日详情-地图
    private LinearLayout now_map_ll;
    private TextView now_map_city,now_map_weather,now_map_temp,now_map_air,now_map_wind;
    private ImageView now_map_iv;

    //灾害预警信息
    private ImageView horn;
    private TextView warn_text,tv_today_alarm;
    private LinearLayout alarm_ll;
    private RelativeLayout rl_alarm;

    //生活指数
    private ImageView comfImage;
    private ImageView cwImage;
    private ImageView drsgImage;
    private ImageView spiImage;
    private ImageView travImage;
    private ImageView sportImage;
    private TextView comfInfo;
    private TextView cwInfo;
    private TextView sportInfo;
    private TextView spiInfo;
    private TextView travInfo;
    private TextView drsgInfo;

    //AQi，空气质量，PM2.5
    private TextView aqiText;
    private TextView qltyText;
    private TextView pm25Text;

    //生活建议
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    //未来7日天气预报
    private RelativeLayout forecastRelativeLayout;
    private LinearLayout forecastLayout;
    //24小时天气预报
    private LinearLayout forecastHourlyLayout;
    private TextView forecast_hourly_city;
    //生活指数
    private LinearLayout comf,cw,drsg,sport,trav,spi;


    public CityWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        //   进入当前Fragment
        if (enter && !isGetData) {
            isGetData = true;
            //   这里可以做网络请求或者需要的数据刷新操作
            if (map){
                now_map_ll.setVisibility(View.VISIBLE);
                map_ll.setVisibility(View.VISIBLE);
                mMapView.setVisibility(View.VISIBLE);
                weatherMap.setVisibility(View.GONE);
                weatherNormal.setVisibility(View.VISIBLE);
            }else {
                now_map_ll.setVisibility(View.GONE);
                map_ll.setVisibility(View.GONE);
                mMapView.setVisibility(View.GONE);
                weatherMap.setVisibility(View.VISIBLE);
                weatherNormal.setVisibility(View.GONE);
            }
            if (warn_message){
                alarm_ll.setVisibility(View.VISIBLE);
            }else {
                alarm_ll.setVisibility(View.GONE);
            }
        } else {
            isGetData = false;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().
                detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
                detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        super.onCreate(savedInstanceState);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_city_weather, container, false);
        //加载控件
        initView(view);
        //可以通过activity传值获取当前fragment加载的城市的天气情况
        Bundle bundle = getArguments();
        city = bundle.getString("city");

        //获取数据库中存储的灾害预警和降雨信息开关
        SharedPreferences sp= getContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        //根据key取出对应的值
        map = sp.getBoolean("map", false);//第二个参数为默认值，即当从Share中取不到时，返回这个值
        warn_message = sp.getBoolean("warn_message", false);
        notice_message = sp.getBoolean("notice_message", false);

        weatherLayout = (ScrollView) view.findViewById(R.id.weather_layout);
        weatherLayout.setVisibility(View.VISIBLE);

        map_container = (MapContainer) view.findViewById(R.id.map_container);
        map_container.setScrollView(weatherLayout);

        //获取地图控件引用
        map_ll = (RelativeLayout) view.findViewById(R.id.map_ll);
        mMapView = (TextureMapView) view.findViewById(R.id.mapView);
        weatherMap = (ImageView) view.findViewById(R.id.weather_map);
        weatherNormal = (ImageView) view.findViewById(R.id.weather_normal);
        btnSeek = (Button) view.findViewById(R.id.btnSeek);
        map_center = view.findViewById(R.id.map_center);
        autoCompleteTextView = view.findViewById(R.id.editText);
        autoCompleteTextViewSt = view.findViewById(R.id.editText_st);

        baiduMap = mMapView.getMap();
        //通过设置enable为true或false 选择是否显示比例尺
        mMapView.showScaleControl(true);
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        //设置地图显示类型   普通/卫星/空白
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启定位功能，异步
        baiduLocation = new BaiduLocation(getContext());
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        baiduLocation.registerListener(mListener);
        //注册监听
        baiduLocation.setLocationOption(baiduLocation.getDefaultLocationClientOption());
        baiduLocation.start();
        //生成maker点图标
        bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_marker);
        //设置地图单击事件监听
        baiduMap.setOnMapClickListener(listener);
        //设置地图覆盖物点击事件
        baiduMap.setOnMarkerClickListener(markerClickListener);
        //POI搜索模块
        poiSearch = PoiSearch.newInstance();
        //增加监听：POI搜索结果
        poiSearch.setOnGetPoiSearchResultListener(new PoiSearchResultListener());
        // 模糊搜索，初始化建议搜索模块，注册建议搜索事件监听
        suggestionSearch = SuggestionSearch.newInstance();
        suggestionSearchSt = SuggestionSearch.newInstance();
        suggestionSearch.setOnGetSuggestionResultListener(sugListener);
        suggestionSearchSt.setOnGetSuggestionResultListener(sugListenerSt);
        //POI关键词检索的方法调用
        // 初始化view
        autoCompleteTextView.setThreshold(1);//设置输入几个字符开始提示
        setEdit();
        autoCompleteTextViewSt.setThreshold(1);//设置输入几个字符开始提示
        setEditSn();


        if (map){
            now_map_ll.setVisibility(View.VISIBLE);
            mMapView.setVisibility(View.VISIBLE);
            weatherMap.setVisibility(View.GONE);
            weatherNormal.setVisibility(View.VISIBLE);
        }else {
            now_map_ll.setVisibility(View.GONE);
            mMapView.setVisibility(View.GONE);
            weatherMap.setVisibility(View.VISIBLE);
            weatherNormal.setVisibility(View.GONE);
        }
        if (warn_message){
            alarm_ll.setVisibility(View.VISIBLE);
        }else {
            alarm_ll.setVisibility(View.GONE);
        }
        if (notice_message){
            setNotification();
        }else {
            cancelNotification();
        }

        weatherMap.setOnClickListener(this);
        weatherNormal.setOnClickListener(this);
        //通过设置enable为true或false 选择是否显示比例尺
        mMapView.showScaleControl(true);
        baiduMap = mMapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        btnSeek.setOnClickListener(this);
        map_center.setOnClickListener(this);

        //根据城市名获取定位经纬度
        getLatAndLng(city);
        //请求天气信息
        requestAir(city);
        requestRain(cityLatLng);
        requestAlarm(city);
        requestWeather(city);

        //下拉刷新界面
        swipeRefresh = view.findViewById(R.id.refreshLayout);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(city);
                //        showProgressDialog();
                swipeRefresh.setRefreshing(false);
            }
        });
        return view;
    }


    /**
     * 反地址编码，获取经纬度
     */
    public void getLatAndLng(String cityName) {

        Geocoder geocoder = new Geocoder(getContext(), Locale.CHINA);
        try {
            List<Address> addressList = geocoder.getFromLocationName(cityName,5);
            if (addressList.size()> 0) {
                Address address = addressList.get(0);
                double latitude = address.getLatitude();//纬度
                double longitude = address.getLongitude();//精度
                cityLatLng = new LatLng(latitude,longitude);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 百度定位结果回调，重写onReceiveLocation方法
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            LogUtil.d(TAG, "onReceiveLocation: start");
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                Log.v(TAG, "latitude " + location.getLatitude() + "  longitude:" + location.getLongitude());
                centerLatitude = location.getLatitude();
                centerLongitude = location.getLongitude();
                centerDistrict =  location.getDistrict();
                stLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                navigateTo(location);
            }}
    };

    /**
     * 地图移动至当前位置
     */
    private void navigateTo(BDLocation location){
        if (isFirstLocate){
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(12.0f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(location.getLatitude());
        builder.longitude(location.getLongitude());
        MyLocationData locationData = builder.build();
        baiduMap.setMyLocationData(locationData);
    }

    /**
     * POI搜索
     */
    private class PoiSearchResultListener implements OnGetPoiSearchResultListener {

        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            //获取POI检索结果
            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                baiduMap.clear();

                //创建PoiOverlay对象
                PoiOverlay poiOverlay = new PoiOverlay(baiduMap);

                //设置Poi检索数据
                poiOverlay.setData(poiResult);

                //将poiOverlay添加至地图并缩放至合适级别
                poiOverlay.addToMap();
                poiOverlay.zoomToSpan();
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            if (poiDetailResult == null
                    || poiDetailResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                Toast.makeText(getContext(), "未找到结果", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            if (poiDetailResult.error == SearchResult.ERRORNO.NO_ERROR) {
                //清除图层
                baiduMap.clear();
                //搜索到POI
                baiduMap.addOverlay(
                        new MarkerOptions()
                                .position(poiDetailResult.location)                                     //坐标位置
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_marker))
                                .title(poiDetailResult.getAddress())                                         //标题
                );
                //将该POI点设置为地图中心
                String weatherId = poiDetailResult.location.latitude+","+poiDetailResult.location.longitude;
                requestWeatherOnly(weatherId);
                LatLng latLng = new LatLng(poiDetailResult.location.latitude,poiDetailResult.location.longitude);
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
                baiduMap.animateMapStatus(update);
                update = MapStatusUpdateFactory.zoomTo(16.0f);
                baiduMap.animateMapStatus(update);
                baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(poiDetailResult.location));
                return;
            }
            if (poiDetailResult.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
            }

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }
    }

    /**
     * 地图单击事件监听
     */
    BaiduMap.OnMapClickListener listener = new BaiduMap.OnMapClickListener() {
        /**
         * 地图单击事件回调函数
         *
         * @param latLng 点击的地理坐标
         */
        @Override
        public void onMapClick(LatLng latLng) {
            //获取经纬度
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            //清除图层
            baiduMap.clear();
            // 定义Maker坐标点
            LatLng point = new LatLng(latitude, longitude);
            //定义options设置maker属性，构建MarkerOption，用于在地图上添加Marker
            OverlayOptions options = new MarkerOptions().position(point).icon(bitmap).draggable(true);
            //将maker添加到地图
            baiduMap.addOverlay(options);
            //实例化一个地理编码查询对象
            GeoCoder geoCoder = GeoCoder.newInstance();
            //设置反地理编码位置坐标
            ReverseGeoCodeOption op = new ReverseGeoCodeOption();
            op.location(point);
            //发起反地理编码请求(经纬度->地址信息)
            geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                @Override
                public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                    if (geoCodeResult == null
                            || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                        Toast.makeText(getContext(), "没有检测到结果", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                    if (reverseGeoCodeResult == null
                            || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                        Toast.makeText(getContext(), "没有检测到结果", Toast.LENGTH_SHORT).show();
                    } else {
                        //获取点击的坐标地址
                        String address = reverseGeoCodeResult.getAddressDetail().countryName
                                + reverseGeoCodeResult.getAddressDetail().province
                                + reverseGeoCodeResult.getAddressDetail().city
                                + reverseGeoCodeResult.getAddressDetail().district
                                + reverseGeoCodeResult.getAddressDetail().street
                                + reverseGeoCodeResult.getAddressDetail().town;
                        requestWeatherOnly(reverseGeoCodeResult.getAddressDetail().district);
//                            Toast.makeText(MainActivity.this, "位置：" + address, Toast.LENGTH_LONG).show();
                    }
                }
            });
            geoCoder.reverseGeoCode(op);
            // 释放实例
            geoCoder.destroy();
        }

        /**
         * 地图内 Poi 单击事件回调函数
         *
         * @param mapPoi 点击的 poi 信息
         */
        @Override
        public void onMapPoiClick(MapPoi mapPoi) {
            baiduMap.clear();
            String POIName = mapPoi.getName();//POI点名称
            LatLng POIPosition = mapPoi.getPosition();//POI点坐标
            String weatherId = POIPosition.latitude+","+POIPosition.longitude;
            requestWeatherOnly(weatherId);
            //将该POI点设置为地图中心
            baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(POIPosition));

        }
    };

    /**
     * 地图 Marker 覆盖物点击事件监听接口
     */
    BaiduMap.OnMarkerClickListener markerClickListener = new BaiduMap.OnMarkerClickListener() {
        /**
         * 地图 Marker 覆盖物点击事件监听函数
         * @param marker 被点击的 marker
         */
        public boolean onMarkerClick(Marker marker){
            LatLng latLng = marker.getPosition();
            String markerLatlng = latLng.latitude+","+latLng.longitude;
            requestWeatherMarker(markerLatlng);
            Button button = new Button(getContext());
            button.setBackgroundResource(R.mipmap.qipao);
            button.setTextColor(getResources().getColor(R.color.colorPrimary));
            button.setTextSize(10);
            if (markerWeather != null){
                button.setText(markerWeather.basic.countyName+markerWeather.now.txt+markerWeather.now.temperature+"℃");
            }
            // 定义用于显示该InfoWindow的坐标点
            // 创建InfoWindow的点击事件监听者
            InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
                public void onInfoWindowClick() {
                    showWeatherInfo(markerWeather);
                }};
            InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory
                    .fromView(button), marker.getPosition(), 0, listener);//i:Y 轴偏移量
            baiduMap.showInfoWindow(mInfoWindow);
            return true;

        }
    };

    /**
     * 路线POI检索监听
     */
    OnGetRoutePlanResultListener routeListener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            if (drivingRouteResult == null || drivingRouteResult.error !=
                    SearchResult.ERRORNO.NO_ERROR) {
                LogUtil.d(TAG, "检索无结果");
            }
            if (drivingRouteResult.error ==
                    SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (drivingRouteResult.error ==
                    SearchResult.ERRORNO.NO_ERROR) {
                baiduMap.clear();
                //驾车路线的规划,有很多节点（拐点）图标
                //创建DrivingRouteOverlay实例
                DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap);
                if (drivingRouteResult.getRouteLines().size() > 0) {
                    //获取路径规划数据,(以返回的第一条路线为例）
                    //为DrivingRouteOverlay实例设置数据
                    overlay.setData(drivingRouteResult.getRouteLines().get(0));
                    //在地图上绘制DrivingRouteOverlay
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }

//                //如何只画线(不要图标),
//                test(drivingRouteResult.getRouteLines().get(0));
            }

        }
        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitResult) {

        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }

        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingResult) {

        }

    };

    /**
     * Sug检索，联想搜索，出发地
     */
    OnGetSuggestionResultListener sugListenerSt = new OnGetSuggestionResultListener() {
        @Override
        public void onGetSuggestionResult(SuggestionResult suggestionResult) {
            //处理sug检索结果
            if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                return;
            }
            List<HashMap<String, String>> suggest = new ArrayList<>();
            for (SuggestionResult.SuggestionInfo suggestionInfo : suggestionResult.getAllSuggestions()) {
                if (suggestionInfo.key != null) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("key",suggestionInfo.getKey());
                    map.put("city",suggestionInfo.getCity());
                    map.put("dis",suggestionInfo.getDistrict());
                    suggest.add(map);
                }
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(),
                    suggest,
                    R.layout.item_layout,
                    new String[]{"key", "city","dis"},
                    new int[]{R.id.sug_key, R.id.sug_city, R.id.sug_dis});
            autoCompleteTextViewSt.setAdapter(simpleAdapter);
            autoCompleteTextViewSt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SuggestionResult.SuggestionInfo info = suggestionResult.getAllSuggestions().get(position);
                    stLatLng = info.getPt();
                    poiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(info.uid));
                    autoCompleteTextViewSt.setText(info.key);
                    //隐藏软键盘
                    InputMethodManager imm= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });
            simpleAdapter.notifyDataSetChanged();

        }
    };

    /**
     * Sug检索，联想搜索，目的地
     */
    OnGetSuggestionResultListener sugListener = new OnGetSuggestionResultListener() {
        @Override
        public void onGetSuggestionResult(SuggestionResult suggestionResult) {
            //处理sug检索结果
            if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                return;
            }
            List<HashMap<String, String>> suggest = new ArrayList<>();
            for (SuggestionResult.SuggestionInfo suggestionInfo : suggestionResult.getAllSuggestions()) {
                if (suggestionInfo.key != null) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("key",suggestionInfo.getKey());
                    map.put("city",suggestionInfo.getCity());
                    map.put("dis",suggestionInfo.getDistrict());
                    suggest.add(map);
                }
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(),
                    suggest,
                    R.layout.item_layout,
                    new String[]{"key", "city","dis"},
                    new int[]{R.id.sug_key, R.id.sug_city, R.id.sug_dis});
            autoCompleteTextView.setAdapter(simpleAdapter);
            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SuggestionResult.SuggestionInfo info = suggestionResult.getAllSuggestions().get(position);
                    enLatLng = info.getPt();
                    poiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(info.uid));
                    autoCompleteTextView.setText(info.key);
                    //隐藏软键盘
                    InputMethodManager imm= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });
            simpleAdapter.notifyDataSetChanged();

        }
    };

    /**
     * 监听输入框的输入，出发地
     */
    public void setEditSn() {
        //点击就自动提示
        autoCompleteTextViewSt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextViewSt.showDropDown();
            }
        });

        autoCompleteTextViewSt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                suggestionSearchSt.requestSuggestion((new SuggestionSearchOption())
                        .keyword(charSequence.toString()) //关键字
                        .city(autoCompleteTextViewSt.getText().toString())); //城市
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * 监听输入框的输入，目的地
     */
    public void setEdit() {
        //点击就自动提示
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.showDropDown();
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                suggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(charSequence.toString()) //关键字
                        .city(autoCompleteTextView.getText().toString())); //城市
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * 控件初始化
     */
    private void initView(View view){
        //初始化各控件
        forecast_hourly_city = (TextView) view.findViewById(R.id.forecast_hourly_city);
        horn = (ImageView) view.findViewById(R.id.horn);
        alarm_ll = (LinearLayout) view.findViewById(R.id.alarm_ll);
        warn_text = (TextView) view.findViewById(R.id.warn_text);
        tv_today_alarm = (TextView) view.findViewById(R.id.tv_today_alarm);
        rl_alarm = (RelativeLayout) view.findViewById(R.id.rl_alarm);

        weatherInfoImage = (ImageView) view.findViewById(R.id.weather_info_image) ;
        weatherLayout = (ScrollView) view.findViewById(R.id.weather_layout);
        title_update_time = (TextView) view.findViewById(R.id.title_update_time);
        temperatureText = (TextView) view.findViewById(R.id.temperature_text);
        weatherInfoText = (TextView) view.findViewById(R.id.weather_info_text);
        weatherWindText = (TextView) view.findViewById(R.id.weather_wind);
        weather_aqi_qlty = (TextView) view.findViewById(R.id.weather_aqi_qlty);
        minandmaxText = (TextView) view.findViewById(R.id.minandmax_text);

        now_ll = (LinearLayout) view.findViewById(R.id.now_ll);
        humImage = (ImageView) view.findViewById(R.id.hum_image);
        presImage = (ImageView) view.findViewById(R.id.pres_image);
        visImage = (ImageView) view.findViewById(R.id.vis_image);
        windImage = (ImageView) view.findViewById(R.id.wind_mage);
        humInfo = (TextView) view.findViewById(R.id.hum_info);
        visInfo = (TextView) view.findViewById(R.id.vis_info);
        presInfo = (TextView) view.findViewById(R.id.pres_info);
        windsc = (TextView) view.findViewById(R.id.wind_sc);
        winddir = (TextView) view.findViewById(R.id.wind_dir);

        //今日详情-地图
        now_map_ll = (LinearLayout) view.findViewById(R.id.now_map_ll);
        now_map_iv = (ImageView) view.findViewById(R.id.now_map_iv);
        now_map_city = (TextView) view.findViewById(R.id.now_map_city);
        now_map_weather = (TextView) view.findViewById(R.id.now_map_weather);
        now_map_temp = (TextView) view.findViewById(R.id.now_map_temp);
        now_map_air = (TextView) view.findViewById(R.id.now_map_air);
        now_map_wind = (TextView) view.findViewById(R.id.now_map_wind);

        comfImage = (ImageView) view.findViewById(R.id.comf_image);
        cwImage = (ImageView) view.findViewById(R.id.cw_image);
        travImage = (ImageView) view.findViewById(R.id.trav_image);
        spiImage = (ImageView) view.findViewById(R.id.spi_image);
        drsgImage = (ImageView) view.findViewById(R.id.drsg_image);
        sportImage = (ImageView) view.findViewById(R.id.sport_image);
        comfInfo = (TextView) view.findViewById(R.id.comf_info);
        cwInfo = (TextView) view.findViewById(R.id.cw_info);
        sportInfo = (TextView) view.findViewById(R.id.sport_info);
        spiInfo = (TextView) view.findViewById(R.id.spi_info);
        travInfo = (TextView) view.findViewById(R.id.trav_info);
        drsgInfo = (TextView) view.findViewById(R.id.drsg_Info);
        comf = (LinearLayout) view.findViewById(R.id.comf);
        cw = (LinearLayout) view.findViewById(R.id.cw);
        drsg = (LinearLayout) view.findViewById(R.id.drsg);
        spi = (LinearLayout) view.findViewById(R.id.spi);
        trav = (LinearLayout) view.findViewById(R.id.trav);
        sport = (LinearLayout) view.findViewById(R.id.sport);
        //设置监听事件
        rl_alarm.setOnClickListener(this);
        rl_alarm.setClickable(false);
        comf.setOnClickListener(this);
        cw.setOnClickListener(this);
        sport.setOnClickListener(this);
        spi.setOnClickListener(this);
        trav.setOnClickListener(this);
        drsg.setOnClickListener(this);

        forecastRelativeLayout = (RelativeLayout) view.findViewById(R.id.forecast_relative_layout);
        forecastLayout = (LinearLayout) view.findViewById(R.id.forecast_layout);
        forecastHourlyLayout = (LinearLayout) view.findViewById(R.id.forecast_hourly_layout);

        qltyText = (TextView) view.findViewById(R.id.aqi_qlty);
        aqiText = (TextView) view.findViewById(R.id.aqi_aqi) ;
        pm25Text = (TextView) view.findViewById(R.id.aqi_pm25);
        comfortText = (TextView) view.findViewById(R.id.comfort_txt);
        sportText = (TextView) view.findViewById(R.id.sport_txt);
        carWashText = (TextView) view.findViewById(R.id.carWash_txt);
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String s = DBManager.queryInfoByCity(city);
        Weather weather = WeatherJson.getWeatherResponse(s);
        String msg;
        switch (view.getId()){
            case R.id.weather_map:
                //清除图层
                baiduMap.clear();
                LatLng latLng = new LatLng(centerLatitude, centerLongitude);
                requestWeatherOnly(centerDistrict);
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
                baiduMap.animateMapStatus(update);
                update = MapStatusUpdateFactory.zoomTo(12.0f);
                baiduMap.animateMapStatus(update);
                //把定位点再次显现出来
                baiduMap.animateMapStatus(update);
                swipeRefresh.setEnabled(false);
                now_map_city.setText(city);
                ((WeatherFragment) (CityWeatherFragment.this.getParentFragment())).changeTitle(true);
                now_map_ll.setVisibility(View.VISIBLE);
                map_center.setVisibility(View.VISIBLE);
                now_ll.setVisibility(View.GONE);
                map_ll.setVisibility(View.VISIBLE);
                mMapView.setVisibility(View.VISIBLE);
                weatherMap.setVisibility(View.GONE);
                weatherNormal.setVisibility(View.VISIBLE);
                break;
            case R.id.weather_normal:
                swipeRefresh.setEnabled(true);
                ((WeatherFragment) (CityWeatherFragment.this.getParentFragment())).changeTitle(false);
                now_map_ll.setVisibility(View.GONE);
                map_center.setVisibility(View.GONE);
                now_ll.setVisibility(View.VISIBLE);
                alarm_ll.setVisibility(View.VISIBLE);
                map_ll.setVisibility(View.GONE);
                mMapView.setVisibility(View.GONE);
                weatherMap.setVisibility(View.VISIBLE);
                weatherNormal.setVisibility(View.GONE);
                break;
            case R.id.rl_alarm:
                Intent intent = new Intent(getContext(), AlarmActivity.class);
                intent.putExtra("alarm_title",alarm.Alarm_item.get(0).title.split("布")[1]);
                intent.putExtra("alarm_content",alarm.Alarm_item.get(0).txt);
                startActivity(intent);
                break;
            case R.id.comf:
                builder.setTitle("舒适度指数");
                msg = weather.lifestyleList.get(0).text;
                builder.setMessage(msg);
                builder.setPositiveButton("确定",null);
                builder.create().show(); //构建AlertDialog并显示
                break;
            case R.id.cw:
                builder.setTitle("洗车指数");
                msg = weather.lifestyleList.get(6).text;
                builder.setMessage(msg);
                builder.setPositiveButton("确定",null);
                builder.create().show(); //构建AlertDialog并显示
                break;
            case R.id.sport:
                builder.setTitle("运动指数");
                msg = weather.lifestyleList.get(3).text;
                builder.setMessage(msg);
                builder.setPositiveButton("确定",null);
                builder.create().show(); //构建AlertDialog并显示
                break;
            case R.id.spi:
                builder.setTitle("紫外线指数");
                msg = weather.lifestyleList.get(15).text;
                builder.setMessage(msg);
                builder.setPositiveButton("确定",null);
                builder.create().show(); //构建AlertDialog并显示
                break;
            case R.id.trav:
                builder.setTitle("旅游指数");
                msg = weather.lifestyleList.get(4).text;
                builder.setMessage(msg);
                builder.setPositiveButton("确定",null);
                builder.create().show(); //构建AlertDialog并显示
                break;
            case R.id.drsg:
                builder.setTitle("穿衣指数");
                msg = weather.lifestyleList.get(1).text;
                builder.setMessage(msg);
                builder.setPositiveButton("确定",null);
                builder.create().show(); //构建AlertDialog并显示
                break;
            case R.id.map_center:
                //清除图层
                baiduMap.clear();
                LatLng point = new LatLng(centerLatitude, centerLongitude);
                requestWeatherOnly(centerDistrict);
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(point);
                baiduMap.animateMapStatus(mapStatusUpdate);
                mapStatusUpdate = MapStatusUpdateFactory.zoomTo(12.0f);
                baiduMap.animateMapStatus(mapStatusUpdate);
                //把定位点再次显现出来
                baiduMap.animateMapStatus(mapStatusUpdate);
                break;
            case R.id.btnSeek:
                // 创建路线检索实例
                routePlanSearch = RoutePlanSearch.newInstance();
                // 设置路线检索监听者；
                routePlanSearch.setOnGetRoutePlanResultListener(routeListener);
                //设置途径点
                PlanNode stNode = PlanNode.withLocation(stLatLng);
                PlanNode enNode = PlanNode.withLocation(enLatLng);
                // 发起驾车线路规划检索
                routePlanSearch.drivingSearch((new DrivingRoutePlanOption())
                        .from(stNode)
                        .to(enNode));
                break;
            default:
                break;
        }
    }

    /**
     * 根据天气id请求城市天气信息,并保存之数据库
     */
    public void requestWeather(final String weatherId){
        String weatherUrl = "https://api.heweather.net/s6/weather?location="+weatherId+"&key=88796e0ed0b045c0aba50fd9c2239a35";

        MyHttp.sendRequestOkHttpForGet(weatherUrl, new MyCallBack() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //数据库查找上一次信息显示再fragment中
                        String s = DBManager.queryInfoByCity(weatherId);
                        if(!TextUtils.isEmpty(s)){
                            showWeatherInfo( WeatherJson.getWeatherResponse(s));
                        }else{
                            Toast.makeText(getActivity(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onResponse(String response) throws IOException {
                final String responseText = response;
                Weather weather = WeatherJson.getWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            //更新城市信息
                            int i = DBManager.updateInfoByCity(weatherId,responseText);
                            if (i <= 0){
                                //更新数据库失败，无此城市，增加该城市信息
                                DBManager.addCityInfo(weatherId,responseText);
                            }
                            city = weather.basic.countyName;
                            showWeatherInfo(weather);
                            Log.e("123", "weather");
                        }
                    }
                });

            }
        });
    }

    /**
     * 根据天气id请求城市天气信息,地图marker
     */
    public void requestWeatherMarker(final String weatherId){
        String weatherUrl = "https://api.heweather.net/s6/weather?location="+weatherId+"&key=88796e0ed0b045c0aba50fd9c2239a35";

        MyHttp.sendRequestOkHttpForGet(weatherUrl, new MyCallBack() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(String response) throws IOException {
                final String responseText = response;
                markerWeather = WeatherJson.getWeatherResponse(responseText);
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeatherOnly(final String weatherId){
        String weatherUrl = "https://api.heweather.net/s6/weather?location="+weatherId+"&key=88796e0ed0b045c0aba50fd9c2239a35";

        MyHttp.sendRequestOkHttpForGet(weatherUrl, new MyCallBack() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(String response) throws IOException {
                final String responseText = response;
                Weather weather = WeatherJson.getWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            showWeatherInfo(weather);
                            Log.e("123", "weather");
                        }
                    }
                });

            }
        });
    }

    /**
     * 根据天气id请求城市灾害预警信息
     */
    public void requestAlarm(final String weatherId){
        String weatherUrl = "https://api.heweather.net/s6/alarm?location="+weatherId+"&key=b3f12f4deb964e7289fa38050148e5b5";

        MyHttp.sendRequestOkHttpForGet(weatherUrl, new MyCallBack() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response) throws IOException {
                final String responseText = response;
                alarm = WeatherJson.getAlarmResponse(responseText);
                if (getActivity() == null){
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (alarm != null && "ok".equals(alarm.status)){
                            showAlarmInfo(alarm);
                            rl_alarm.setClickable(true);
                        }
                    }
                });
            }
        });
    }

    /**
     * 根据城市定位请求城市降水信息
     */
    public void requestRain(final LatLng latLng){
        String latandlng = latLng.longitude + "," + latLng.latitude;
        String weatherUrl = "https://api.heweather.net/s6/weather/grid-minute?location="+latandlng+"&key=b3f12f4deb964e7289fa38050148e5b5";

        MyHttp.sendRequestOkHttpForGet(weatherUrl, new MyCallBack() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response) throws IOException {
                final String responseText = response;
                rain = WeatherJson.getRainResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (rain != null && "ok".equals(rain.status)){
                            showRainInfo(rain);
                        }
                    }
                });
            }
        });
    }

    /**
     * 根据城市定位请求城市降水信息
     */
    public void requestAir(final String weatherId){
        String weatherUrl = "https://api.heweather.net/s6/air/now?location="+weatherId+"&key=b3f12f4deb964e7289fa38050148e5b5";

        MyHttp.sendRequestOkHttpForGet(weatherUrl, new MyCallBack() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response) throws IOException {
                final String responseText = response;
                air = WeatherJson.getAirResponse (responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (air != null && "ok".equals(air.status)){
                            showAirInfo(air);
                        }
                    }
                });
            }
        });
    }

    /**
     * 处理并显示Alarm实体类中的数据
     */
    private void showAlarmInfo(Alarm malarm){
        tv_today_alarm.setText(malarm.Alarm_item.get(0).title.split("布")[1]);

    }

    /**
     * 处理并显示Rain实体类中的数据
     */
    private void showRainInfo(Rain rain){
        warn_text.setText(rain.grid_minute_forecast.txt);
    }

    /**
     * 处理并显示Air实体类中的数据
     */
    private void showAirInfo(Air air){
        weather_aqi_qlty.setText("空气质量"+air.air_now_city.qlty);
        now_map_air.setText(air.air_now_city.aqi+air.air_now_city.qlty);
    }

    /**
     * 处理并显示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        String temperature = weather.now.temperature+"℃";
        String weatherInfo = weather.now.txt+" | ";
        String wind = weather.now.wind_direction+weather.now.wind_strength+"级 | ";

        String todayMax = weather.forecastList.get(0).max;
        String todayMin = weather.forecastList.get(0).min;
        String minandmax = todayMin + "~" + todayMax +"℃";

        String county = weather.basic.countyName;
        String time = weather.update.updateTime.split(" ")[1];



        //湿度、能见度、风力、压力
        humImage.setImageResource(R.mipmap.hum);
        visImage.setImageResource(R.mipmap.vis);
        presImage.setImageResource(R.mipmap.pres);
        windImage.setImageResource(R.mipmap.wind);
        humInfo.setText(weather.now.humidity+"%");
        visInfo.setText(weather.now.vis+"千米");
        presInfo.setText(weather.now.pres+"百帕");
        winddir.setText(weather.now.wind_direction);
        windsc.setText(weather.now.wind_strength+"级");

        //天气详情
        String weatherInfoCode = weather.now.code;
        int weatherInfoImageId = IconUtil.getDayIcon(weatherInfoCode);
        //获取当前时间
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour > 6 && hour < 19) {
            weatherInfoImage.setImageResource(IconUtil.getDayIcon(weatherInfoCode));
            now_map_iv.setImageResource(weatherInfoImageId);
        } else {
            weatherInfoImage.setImageResource(IconUtil.getNightIconDark(weatherInfoCode));
            now_map_iv.setImageResource(IconUtil.getNightIconDark(weatherInfoCode));
        }
        title_update_time.setText(time);
        temperatureText.setText(temperature);
        weatherInfoText.setText(weatherInfo);
        weatherWindText.setText(wind);
        minandmaxText.setText(minandmax);

        //天气详情-地图
        now_map_city.setText(county);
        now_map_weather.setText(weatherInfo);
        now_map_temp.setText(temperature);
        now_map_wind.setText(weather.now.wind_direction+weather.now.wind_strength+"级");

        //生活指数
        cwImage.setImageResource(R.mipmap.cw);
        comfImage.setImageResource(R.mipmap.comf);
        spiImage.setImageResource(R.mipmap.spi);
        sportImage.setImageResource(R.mipmap.sport);
        drsgImage.setImageResource(R.mipmap.drsg);
        travImage.setImageResource(R.mipmap.trav);
        cwInfo.setText(weather.lifestyleList.get(6).brf);
        comfInfo.setText(weather.lifestyleList.get(0).brf);
        sportInfo.setText(weather.lifestyleList.get(3).brf);
        spiInfo.setText(weather.lifestyleList.get(15).brf);
        travInfo.setText(weather.lifestyleList.get(4).brf);
        drsgInfo.setText(weather.lifestyleList.get(1).brf);

        //设置多天天气预报
        forecastLayout.removeAllViews();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.forecast_item,forecastLayout,false);
        mDaysForecastTvDay1 = (TextView) view.findViewById(R.id.wea_days_forecast_day1);
        mDaysForecastTvDay2 = (TextView) view.findViewById(R.id.wea_days_forecast_day2);
        mDaysForecastTvDay3 = (TextView) view.findViewById(R.id.wea_days_forecast_day3);
        mDaysForecastTvDay4 = (TextView) view.findViewById(R.id.wea_days_forecast_day4);
        mDaysForecastTvDay5 = (TextView) view.findViewById(R.id.wea_days_forecast_day5);
        mDaysForecastTvDay6 = (TextView) view.findViewById(R.id.wea_days_forecast_day6);
        mDaysForecastTvDay6 = (TextView) view.findViewById(R.id.wea_days_forecast_day6);
        mDaysForecastTvDay7 = (TextView) view.findViewById(R.id.wea_days_forecast_day7);

        mDaysForecastWeaTypeDayIv1 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_day_iv1);
        mDaysForecastWeaTypeDayIv2 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_day_iv2);
        mDaysForecastWeaTypeDayIv3 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_day_iv3);
        mDaysForecastWeaTypeDayIv4 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_day_iv4);
        mDaysForecastWeaTypeDayIv5 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_day_iv5);
        mDaysForecastWeaTypeDayIv6 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_day_iv6);
        mDaysForecastWeaTypeDayIv7 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_day_iv7);

        mDaysForecastWeaTypeDayTv1 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_day_tv1);
        mDaysForecastWeaTypeDayTv2 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_day_tv2);
        mDaysForecastWeaTypeDayTv3 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_day_tv3);
        mDaysForecastWeaTypeDayTv4 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_day_tv4);
        mDaysForecastWeaTypeDayTv5 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_day_tv5);
        mDaysForecastWeaTypeDayTv6 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_day_tv6);
        mDaysForecastWeaTypeDayTv7 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_day_tv7);

        chartView = (WeatherChartView) view.findViewById(R.id.line_char);

        mDaysForecastWeaTypeNightIv1 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_night_iv1);
        mDaysForecastWeaTypeNightIv2 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_night_iv2);
        mDaysForecastWeaTypeNightIv3 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_night_iv3);
        mDaysForecastWeaTypeNightIv4 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_night_iv4);
        mDaysForecastWeaTypeNightIv5 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_night_iv5);
        mDaysForecastWeaTypeNightIv6 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_night_iv6);
        mDaysForecastWeaTypeNightIv7 = (ImageView) view.findViewById(R.id.wea_days_forecast_weather_night_iv7);

        mDaysForecastWeaTypeNightTv1 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_night_tv1);
        mDaysForecastWeaTypeNightTv2 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_night_tv2);
        mDaysForecastWeaTypeNightTv3 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_night_tv3);
        mDaysForecastWeaTypeNightTv4 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_night_tv4);
        mDaysForecastWeaTypeNightTv5 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_night_tv5);
        mDaysForecastWeaTypeNightTv6 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_night_tv6);
        mDaysForecastWeaTypeNightTv7 = (TextView) view.findViewById(R.id.wea_days_forecast_weather_night_tv7);

        mDaysForecastWindDirectionTv1 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_direction_tv1);
        mDaysForecastWindDirectionTv2 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_direction_tv2);
        mDaysForecastWindDirectionTv3 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_direction_tv3);
        mDaysForecastWindDirectionTv4 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_direction_tv4);
        mDaysForecastWindDirectionTv5 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_direction_tv5);
        mDaysForecastWindDirectionTv6 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_direction_tv6);
        mDaysForecastWindDirectionTv7 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_direction_tv7);

        mDaysForecastWindPowerTv1 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_power_tv1);
        mDaysForecastWindPowerTv2 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_power_tv2);
        mDaysForecastWindPowerTv3 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_power_tv3);
        mDaysForecastWindPowerTv4 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_power_tv4);
        mDaysForecastWindPowerTv5 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_power_tv5);
        mDaysForecastWindPowerTv6 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_power_tv6);
        mDaysForecastWindPowerTv7 = (TextView) view.findViewById(R.id.wea_days_forecast_wind_power_tv7);


        mDaysForecastTvDay1.setText(weather.forecastList.get(0).date.split("-")[1]+"-"+weather.forecastList.get(0).date.split("-")[2]);
        mDaysForecastTvDay2.setText(weather.forecastList.get(1).date.split("-")[1]+"-"+weather.forecastList.get(1).date.split("-")[2]);
        mDaysForecastTvDay3.setText(weather.forecastList.get(2).date.split("-")[1]+"-"+weather.forecastList.get(2).date.split("-")[2]);
        mDaysForecastTvDay4.setText(weather.forecastList.get(3).date.split("-")[1]+"-"+weather.forecastList.get(3).date.split("-")[2]);
        mDaysForecastTvDay5.setText(weather.forecastList.get(4).date.split("-")[1]+"-"+weather.forecastList.get(4).date.split("-")[2]);
        mDaysForecastTvDay6.setText(weather.forecastList.get(5).date.split("-")[1]+"-"+weather.forecastList.get(5).date.split("-")[2]);
        mDaysForecastTvDay7.setText(weather.forecastList.get(6).date.split("-")[1]+"-"+weather.forecastList.get(6).date.split("-")[2]);
        // 取得白天天气类型图片id
        String forecastImageD1 = weather.forecastList.get(0).cond_code_d;
        String forecastImageD2 = weather.forecastList.get(1).cond_code_d;
        String forecastImageD3 = weather.forecastList.get(2).cond_code_d;
        String forecastImageD4 = weather.forecastList.get(3).cond_code_d;
        String forecastImageD5 = weather.forecastList.get(4).cond_code_d;
        String forecastImageD6 = weather.forecastList.get(5).cond_code_d;
        String forecastImageD7 = weather.forecastList.get(6).cond_code_d;
        int weatherDayId1 = IconUtil.getDayIcon(forecastImageD1);
        int weatherDayId2 = IconUtil.getDayIcon(forecastImageD2);
        int weatherDayId3 = IconUtil.getDayIcon(forecastImageD3);
        int weatherDayId4 = IconUtil.getDayIcon(forecastImageD4);
        int weatherDayId5 = IconUtil.getDayIcon(forecastImageD5);
        int weatherDayId6 = IconUtil.getDayIcon(forecastImageD6);
        int weatherDayId7 = IconUtil.getDayIcon(forecastImageD7);

        //设置白天天气类型图片
        mDaysForecastWeaTypeDayIv1.setImageResource(weatherDayId1);
        mDaysForecastWeaTypeDayIv2.setImageResource(weatherDayId2);
        mDaysForecastWeaTypeDayIv3.setImageResource(weatherDayId3);
        mDaysForecastWeaTypeDayIv4.setImageResource(weatherDayId4);
        mDaysForecastWeaTypeDayIv5.setImageResource(weatherDayId5);
        mDaysForecastWeaTypeDayIv6.setImageResource(weatherDayId6);
        mDaysForecastWeaTypeDayIv7.setImageResource(weatherDayId7);

        // 设置白天天气类型文字
        mDaysForecastWeaTypeDayTv1.setText(weather.forecastList.get(0).cond_txt_d);
        mDaysForecastWeaTypeDayTv2.setText(weather.forecastList.get(1).cond_txt_d);
        mDaysForecastWeaTypeDayTv3.setText(weather.forecastList.get(2).cond_txt_d);
        mDaysForecastWeaTypeDayTv4.setText(weather.forecastList.get(3).cond_txt_d);
        mDaysForecastWeaTypeDayTv5.setText(weather.forecastList.get(4).cond_txt_d);
        mDaysForecastWeaTypeDayTv6.setText(weather.forecastList.get(5).cond_txt_d);
        mDaysForecastWeaTypeDayTv7.setText(weather.forecastList.get(6).cond_txt_d);

        int tempDays[] = new int[7];
        int tempNights[] = new int[7];
        for(int i = 0; i < weather.forecastList.size(); i++){
            tempDays[i] = Integer.parseInt(weather.forecastList.get(i).max);
            tempNights[i] = Integer.parseInt(weather.forecastList.get(i).min);
        }
        // 设置白天温度曲线
        chartView .setTempDay(tempDays);
        // 设置夜间温度曲线
        chartView .setTempNight(tempNights);
        chartView .invalidate();

        // 设置夜间天气类型文字
        mDaysForecastWeaTypeNightTv1.setText(weather.forecastList.get(0).cond_txt_n);
        mDaysForecastWeaTypeNightTv2.setText(weather.forecastList.get(1).cond_txt_n);
        mDaysForecastWeaTypeNightTv3.setText(weather.forecastList.get(2).cond_txt_n);
        mDaysForecastWeaTypeNightTv4.setText(weather.forecastList.get(3).cond_txt_n);
        mDaysForecastWeaTypeNightTv5.setText(weather.forecastList.get(4).cond_txt_n);
        mDaysForecastWeaTypeNightTv6.setText(weather.forecastList.get(5).cond_txt_n);
        mDaysForecastWeaTypeNightTv7.setText(weather.forecastList.get(6).cond_txt_n);

        // 取得夜间天气类型图片id
        String forecastImageN1 = weather.forecastList.get(0).cond_code_n;
        String forecastImageN2 = weather.forecastList.get(1).cond_code_n;
        String forecastImageN3 = weather.forecastList.get(2).cond_code_n;
        String forecastImageN4 = weather.forecastList.get(3).cond_code_n;
        String forecastImageN5 = weather.forecastList.get(4).cond_code_n;
        String forecastImageN6 = weather.forecastList.get(5).cond_code_n;
        String forecastImageN7 = weather.forecastList.get(6).cond_code_n;
        int weatherNightId1 = IconUtil.getDayIcon(forecastImageN1);
        int weatherNightId2 = IconUtil.getDayIcon(forecastImageN2);
        int weatherNightId3 = IconUtil.getDayIcon(forecastImageN3);
        int weatherNightId4 = IconUtil.getDayIcon(forecastImageN4);
        int weatherNightId5 = IconUtil.getDayIcon(forecastImageN5);
        int weatherNightId6 = IconUtil.getDayIcon(forecastImageN6);
        int weatherNightId7 = IconUtil.getDayIcon(forecastImageN7);

        //设置夜间天气类型图片
        mDaysForecastWeaTypeNightIv1.setImageResource(weatherNightId1);
        mDaysForecastWeaTypeNightIv2.setImageResource(weatherNightId2);
        mDaysForecastWeaTypeNightIv3.setImageResource(weatherNightId3);
        mDaysForecastWeaTypeNightIv4.setImageResource(weatherNightId4);
        mDaysForecastWeaTypeNightIv5.setImageResource(weatherNightId5);
        mDaysForecastWeaTypeNightIv6.setImageResource(weatherNightId6);
        mDaysForecastWeaTypeNightIv7.setImageResource(weatherNightId7);

        // 设置风向
        mDaysForecastWindDirectionTv1.setText(weather.forecastList.get(0).wind_dir);
        mDaysForecastWindDirectionTv2.setText(weather.forecastList.get(1).wind_dir);
        mDaysForecastWindDirectionTv3.setText(weather.forecastList.get(2).wind_dir);
        mDaysForecastWindDirectionTv4.setText(weather.forecastList.get(3).wind_dir);
        mDaysForecastWindDirectionTv5.setText(weather.forecastList.get(4).wind_dir);
        mDaysForecastWindDirectionTv6.setText(weather.forecastList.get(5).wind_dir);
        mDaysForecastWindDirectionTv7.setText(weather.forecastList.get(6).wind_dir);

        // 设置风力
        mDaysForecastWindPowerTv1.setText(weather.forecastList.get(0).wind_sc+"级");
        mDaysForecastWindPowerTv2.setText(weather.forecastList.get(1).wind_sc+"级");
        mDaysForecastWindPowerTv3.setText(weather.forecastList.get(2).wind_sc+"级");
        mDaysForecastWindPowerTv4.setText(weather.forecastList.get(3).wind_sc+"级");
        mDaysForecastWindPowerTv5.setText(weather.forecastList.get(4).wind_sc+"级");
        mDaysForecastWindPowerTv6.setText(weather.forecastList.get(5).wind_sc+"级");
        mDaysForecastWindPowerTv7.setText(weather.forecastList.get(6).wind_sc+"级");
        forecastLayout.addView(view);

        //24小时天气
        forecastHourlyLayout.removeAllViews();
        for(Forecast_hourly forecast_hourly:weather.forecast_hourlyList){
            String hourlyInfoCode = forecast_hourly.code;
            int hourlyInfoImageId = IconUtil.getDayIcon(hourlyInfoCode);
            View hourlyView = LayoutInflater.from(getActivity()).inflate(R.layout.forecast_hourly_item,forecastHourlyLayout,false);
            TextView hourlyInfoText = (TextView) hourlyView.findViewById(R.id.hourlyInfo_text);
            TextView hourlyTimeText = (TextView) hourlyView.findViewById(R.id.hourlyTime_text);
            TextView hourlyTmpText = (TextView) hourlyView.findViewById(R.id.hourlyTmp_text);
            ImageView hourlyInfoImage = (ImageView) hourlyView.findViewById(R.id.hourlyInfo_image);
            hourlyInfoImage.setImageResource(hourlyInfoImageId);
            hourlyInfoText.setText(forecast_hourly.hourlyInfo);
            hourlyTimeText.setText(forecast_hourly.time.split(" ")[1]);
            hourlyTmpText.setText(forecast_hourly.tmp+"℃");
            forecastHourlyLayout.addView(hourlyView);
        }
        weatherLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
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

    /**
     * 添加常驻通知
     */
    private void setNotification(){
        //获取数据库中存储的定位城市
        SharedPreferences sp= getContext().getSharedPreferences(LOCATION_NAME, Context.MODE_PRIVATE);
        //根据key取出对应的值
        String location = sp.getString("location", "");
        String s = DBManager.queryInfoByCity(location);
        Weather weather = WeatherJson.getWeatherResponse(s);
        //天气详情
        String weatherInfoCode = weather.now.code;
        int weatherInfoImageId = IconUtil.getDayIcon(weatherInfoCode);
        //获取当前时间
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int hour = c.get(Calendar.HOUR_OF_DAY);
        //自定义通知栏视图初始化
        RemoteViews remoteViews =
                new RemoteViews(getActivity().getPackageName(),R.layout.notification);
        remoteViews.setTextViewText(R.id.notification_city,location);
        remoteViews.setTextViewText(R.id.notification_weather,weather.now.txt);
        remoteViews.setTextViewText(R.id.notification_temp,weather.now.temperature+"℃");
        remoteViews.setTextViewText(R.id.notification_wind,weather.now.wind_direction+weather.now.wind_strength+"级");
        if (hour > 6 && hour < 19) {
            remoteViews.setImageViewResource(R.id.notification_iv,weatherInfoImageId);
        } else {
            remoteViews.setImageViewResource(R.id.notification_iv,IconUtil.getNightIconDark(weatherInfoCode));
        }
        //自定义通知也是在Android N之后才出现的，所以要加上版本号判断
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            Notification.Builder builder = new Notification.Builder(getContext())
                    .setAutoCancel(false)//是否自动取消，设置为true，点击通知栏 ，移除通知
                    //设置通知的图标
                    .setSmallIcon(R.mipmap.weather_cloudy)
                    //设置通知内容的标题
                    .setContentTitle("云烟成雨");
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_ONGOING_EVENT;// 设置常驻 Flag
            //PendingIntent即将要发生的意图，可以被取消、更新
            Intent intent = new Intent(getContext(),MainActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(getContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.now_map_ll,pendingIntent);
            //绑定自定义视图
            builder.setCustomContentView(remoteViews);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(R.string.app_name,notification);
        }else {
            Intent intent = new Intent(getContext(), MainActivity.class);
            PendingIntent contextIntent = PendingIntent.getActivity(getContext(),0,intent,0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                    //设置通知的图标
                    .setSmallIcon(R.mipmap.weather_cloudy)
                    //设置通知内容的标题
                    .setContentTitle(location)
                    //设置通知内容的内容
                    .setContentText(weather.now.txt+weather.now.temperature+"℃");
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            builder.setContentIntent(contextIntent);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(R.string.app_name,notification);
        }
    }

    /**
     * 取消常驻通知
     */
    private void cancelNotification(){
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(R.string.app_name);
    }


    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume: ");
        //在执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause: ");
        //在执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        isGetData = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop: ");
        //keyForFirstIn = true;
    }
    @Override
    public  void onDestroy(){
        super.onDestroy();
        //在执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        poiSearch.destroy();
        suggestionSearch.destroy();
    }

}
