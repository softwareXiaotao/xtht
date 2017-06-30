package com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.ui.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceStatusListener;
import com.inuker.bluetooth.library.Constants;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.LogUtils;
import com.jess.arms.utils.UiUtils;
import com.walnutin.xtht.bracelet.R;
import com.walnutin.xtht.bracelet.app.MyApplication;
import com.walnutin.xtht.bracelet.mvp.ui.activity.di.component.DaggerRunningOutsideComponent;
import com.walnutin.xtht.bracelet.mvp.ui.activity.di.module.RunningOutsideModule;
import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.contract.RunningOutsideContract;
import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.presenter.RunningOutsidePresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class RunningOutsideActivity extends BaseActivity<RunningOutsidePresenter> implements RunningOutsideContract.View {

    @BindView(R.id.map)
    MapView mapView;
    private AMap aMap;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    Boolean isFirstLatLng = true;
    //以前的定位点
    private LatLng oldLatLng;
    LBSTraceClient lbsTraceClient;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerRunningOutsideComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .runningOutsideModule(new RunningOutsideModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_running_outside; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        //初始化定位
        initLocation();

    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");

                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
                    // 设置当前地图显示为当前位置
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 29));
                    //定位完成的时间
                    sb.append("定位时间: ");
                  /*  myLocationStyle = new MyLocationStyle();
                    myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
                    myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色*/
                    //aMap.setMyLocationStyle(myLocationStyle);
                    LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (isFirstLatLng) {
                        //记录第一次的定位信息
                        oldLatLng = newLatLng;
                        isFirstLatLng = false;
                    }
                    //位置有变化
                    if (oldLatLng != newLatLng) {
                        //setUpMap(oldLatLng, newLatLng);
                        oldLatLng = newLatLng;
                    }
                    //set_beginmark();
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                }

                //解析定位结果，
                String result = sb.toString();
                //LogUtils.debugInfo(result);
            } else {
            }
        }
    };

    List<LatLng> ceshi = new ArrayList<>();
    TraceStatusListener traceListener = new TraceStatusListener() {
        @Override
        public void onTraceStatus(List<TraceLocation> list, List<LatLng> list1, String s) {
            ceshi.addAll(list1);
            List<LatLng> latLngs = DataHelper.getDataList(MyApplication.getAppContext(), "latlng");
            latLngs.addAll(list1);
            DataHelper.setDataList(MyApplication.getAppContext(), "latlng", latLngs);
            LogUtils.debugInfo("怎么回事" + DataHelper.getDataList(MyApplication.getAppContext(), "latlng").size());
            setUpMap();
            LogUtils.debugInfo("获取到的数据" + latLngs.size() + "详情" + latLngs.toString());
        }
    };


    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);

        startLocation();
    }

    /**
     * 默认的定位参数
     *
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(1000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
        aMap.setMyLocationEnabled(true);
        lbsTraceClient = LBSTraceClient.getInstance(this);
    }


    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
        lbsTraceClient.startTrace(traceListener); //开始采集,需要传入一个状态回调监听。
    }


    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
        lbsTraceClient.stopTrace();//在不需要轨迹纠偏时（如行程结束），可调用此接口结束纠偏
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
        destroyLocation();
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        UiUtils.SnackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        UiUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }


    /**
     * 绘制两个坐标点之间的线段,从以前位置到现在位置
     */
    private void setUpMap() {
        // 绘制一个大地曲线
       /* aMap.addPolyline((new PolylineOptions())
                .add(oldData, newData)
                .geodesic(true).color(Color.GREEN));*/

        List<LatLng> latLngs = DataHelper.getDataList(MyApplication.getAppContext(), "latlng");

        aMap.addPolyline(new PolylineOptions() //setCustomTextureList(bitmapDescriptors)
//				.setCustomTextureIndex(texIndexList)
                .addAll(ceshi)
                .useGradient(true)
                .width(18).color(Color.BLUE));
        LogUtils.debugInfo("获取到的数据第二" + latLngs.size() + "详情" + latLngs.toString());
    }

    public void set_beginmark() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(oldLatLng);
        markerOptions.title("当前位置");
        markerOptions.visible(true);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.read_circle));
        markerOptions.icon(bitmapDescriptor);
        aMap.addMarker(markerOptions);

    }


}