package com.example.cyk.cloudweather;


import android.app.AlertDialog;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.cyk.cloudweather.db.DBManager;
import com.example.cyk.cloudweather.gson.Forecast;
import com.example.cyk.cloudweather.gson.Forecast_hourly;
import com.example.cyk.cloudweather.gson.Weather;
import com.example.cyk.cloudweather.http.MyCallBack;
import com.example.cyk.cloudweather.http.MyHttp;
import com.example.cyk.cloudweather.http.OkHttp;
import com.example.cyk.cloudweather.json.WeatherJson;
import com.example.cyk.cloudweather.util.HttpUtil;
import com.example.cyk.cloudweather.util.IconUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.litepal.util.LogUtil;

import java.io.IOException;
import java.util.ArrayList;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class CityWeatherFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "CityWeatherFragment";
    //刷新界面的控件
    public SwipeRefreshLayout swipeRefresh;
    //当前城市天气ID，实现下拉刷新功能
    private String city;
    //定位成功Toast提示msg
    private final int TOAST_LOCATION_SUCCEED = 1;

    //地图
    private MapView mMapView = null;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;
//    //创建驾车路线规划检索实例
//    private RoutePlanSearch routePlanSearch;


    //定位返回指针
    BaiduLocation baiduLocation;

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


    /**
     * 天气的整体信息
     */
    private ImageView weatherInfoImage;
    private FloatingActionButton floatingActionButton;

    private ScrollView weatherLayout;
    private MapContainer map_container;

    private TextView title_update_time;//时间
    private TextView temperatureText;//温度
    private TextView minandmaxText;//当日最低温和最高温
    private TextView weatherInfoText;//天气信息
    private TextView weatherWindText;//风向及风力
    private ImageView weatherMap,weatherNormal;

    //今日详情
    private ImageView humImage;
    private ImageView presImage;
    private ImageView visImage;
    private ImageView windImage;
    private TextView humInfo;
    private TextView visInfo;
    private TextView presInfo;
    private TextView windsc;
    private TextView winddir;

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
    //生活指数
    private LinearLayout comf,cw,drsg,sport,trav,spi;

    public CityWeatherFragment() {
        // Required empty public constructor
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
        initView(view);
        //可以通过activity传值获取当前fragment加载的城市的天气情况
        Bundle bundle = getArguments();
        city = bundle.getString("city");
        weatherLayout.setVisibility(View.INVISIBLE);
        weatherLayout = (ScrollView) view.findViewById(R.id.weather_layout);

        map_container = (MapContainer) view.findViewById(R.id.map_container);
        map_container.setScrollView(weatherLayout);
        //获取地图控件引用
        mMapView = (MapView) view.findViewById(R.id.mapView);
        //通过设置enable为true或false 选择是否显示比例尺
        mMapView.showScaleControl(true);
        baiduMap = mMapView.getMap();
        baiduMap.setMyLocationEnabled(true);
//        routePlanSearch.setOnGetRoutePlanResultListener(listener);
//        PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", "西二旗地铁站");
//        PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", "百度科技园");
//        routePlanSearch.drivingSearch((new DrivingRoutePlanOption())
//                .from(stNode)
//                .to(enNode));
//        routePlanSearch.destroy();

        requestWeather(city);
        //下拉刷新界面
        swipeRefresh = view.findViewById(R.id.refreshLayout);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(city);
                swipeRefresh.setRefreshing(false);
            }
        });
        return view;
    }

//    //创建路线规划检索结果监听器
//    OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
//        @Override
//        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
//            //创建DrivingRouteOverlay实例
//            DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap);
//            if (drivingRouteResult.getRouteLines().size() > 0) {
//                //获取路径规划数据,(以返回的第一条路线为例）
//                //为DrivingRouteOverlay实例设置数据
//                overlay.setData(drivingRouteResult.getRouteLines().get(0));
//                //在地图上绘制DrivingRouteOverlay
//                overlay.addToMap();
//            }
//        }
//
//        @Override
//        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
//
//        }
//
//        @Override
//        public void onGetWalkingRouteResult(WalkingRouteResult result) {
//
//        }
//
//        @Override
//        public void onGetTransitRouteResult(TransitRouteResult result) {
//
//        }
//
//        @Override
//        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
//
//        }
//
//        @Override
//        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
//
//        }
//    };


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
     * 百度定位结果回调，重写onReceiveLocation方法
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            LogUtil.d(TAG, "onReceiveLocation: start");
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                Log.v(TAG, "latitude " + location.getLatitude() + "  longitude:" + location.getLongitude());
                navigateTo(location);
        }}
    };


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
    }

    private void initView(View view){
        //初始化各控件
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_button);
        weatherInfoImage = (ImageView) view.findViewById(R.id.weather_info_image) ;
        weatherLayout = (ScrollView) view.findViewById(R.id.weather_layout);
        title_update_time = (TextView) view.findViewById(R.id.title_update_time);
        temperatureText = (TextView) view.findViewById(R.id.temperature_text);
        weatherInfoText = (TextView) view.findViewById(R.id.weather_info_text);
        weatherWindText = (TextView) view.findViewById(R.id.weather_wind);
        minandmaxText = (TextView) view.findViewById(R.id.minandmax_text);
        weatherMap = (ImageView) view.findViewById(R.id.weather_map);
        weatherNormal = (ImageView) view.findViewById(R.id.weather_normal);

        humImage = (ImageView) view.findViewById(R.id.hum_image);
        presImage = (ImageView) view.findViewById(R.id.pres_image);
        visImage = (ImageView) view.findViewById(R.id.vis_image);
        windImage = (ImageView) view.findViewById(R.id.wind_mage);
        humInfo = (TextView) view.findViewById(R.id.hum_info);
        visInfo = (TextView) view.findViewById(R.id.vis_info);
        presInfo = (TextView) view.findViewById(R.id.pres_info);
        windsc = (TextView) view.findViewById(R.id.wind_sc);
        winddir = (TextView) view.findViewById(R.id.wind_dir);

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
        floatingActionButton.setOnClickListener(this);
        weatherMap.setOnClickListener(this);
        weatherNormal.setOnClickListener(this);
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

    @Override
    public void onClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String s = DBManager.queryInfoByCity(city);
        Weather weather = WeatherJson.getWeatherResponse(s);
        String msg;
        switch (view.getId()){
            case R.id.weather_map:
                //开启定位功能，异步
                baiduLocation = new BaiduLocation(getContext());
                //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
                baiduLocation.registerListener(mListener);
                //注册监听
                baiduLocation.setLocationOption(baiduLocation.getDefaultLocationClientOption());
                baiduLocation.start();
                mMapView.setVisibility(View.VISIBLE);
                weatherMap.setVisibility(View.GONE);
                weatherNormal.setVisibility(View.VISIBLE);
                swipeRefresh.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.VISIBLE);
                break;
            case R.id.weather_normal:
                mMapView.setVisibility(View.GONE);
                weatherMap.setVisibility(View.VISIBLE);
                weatherNormal.setVisibility(View.GONE);
                swipeRefresh.setVisibility(View.VISIBLE);
                break;
            case R.id.floating_button:
                mMapView.setVisibility(View.GONE);
                weatherMap.setVisibility(View.VISIBLE);
                weatherNormal.setVisibility(View.GONE);
                swipeRefresh.setVisibility(View.VISIBLE);
                floatingActionButton.setVisibility(View.GONE);
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
            default:
                break;
        }
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
     * 处理并显示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        String temperature = weather.now.temperature+"℃";
        String weatherInfo = weather.now.txt+" | ";
        String wind = weather.now.wind_direction+weather.now.wind_strength+"级 | ";
        String weatherInfoCode = weather.now.code;

        String todayMax = weather.forecastList.get(0).max;
        String todayMin = weather.forecastList.get(0).min;
        String minandmax = todayMin + "~" + todayMax +"℃";

        int weatherInfoImageId = IconUtil.getDayIcon(weatherInfoCode);

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
        weatherInfoImage.setImageResource(weatherInfoImageId);
        title_update_time.setText(time);
        temperatureText.setText(temperature);
        weatherInfoText.setText(weatherInfo);
        weatherWindText.setText(wind);
        minandmaxText.setText(minandmax);

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

}
