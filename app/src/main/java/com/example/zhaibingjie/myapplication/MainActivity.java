package com.example.zhaibingjie.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MenuItem[] miSwitch = null;

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient = null;
    private TextView showText;
    private double lat;
    private double lon;
    private String[] arrSchools;
    private String[][] arrLatLons;

    private Button showAreaBtn, locateCenterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main1);

        arrSchools = getResources().getStringArray(R.array.arr_schools);

        mMapView = (MapView) findViewById(R.id.bmapView1);
        mBaiduMap = mMapView.getMap();

        parseInfos();
        initBtns();
        initShowBtns();

        lat = Double.parseDouble(arrLatLons[0][0]);
        lon = Double.parseDouble(arrLatLons[0][1]);
        setUserMapCenter();
    }

    private void parseInfos() {
        String[] latlons = getResources().getStringArray(R.array.arr_latlon);
        arrLatLons = new String[latlons.length][2];
        for (int i= 0; i< latlons.length; i++) {
            String[] tmpS = latlons[i].split(",");
            arrLatLons[i][0] = tmpS[0];
            arrLatLons[i][1] = tmpS[1];
        }
    }

    private void initShowBtns() {
        showAreaBtn = (Button)findViewById(R.id.show_area);
        locateCenterBtn = (Button)findViewById(R.id.show_center);
        showAreaBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (showAreaBtn.getText().toString().contains("显示")) {
                    setUpGroundOverlay();
                    showAreaBtn.setText("隐藏区域划分");
                } else {
                    mBaiduMap.clear();
                    showAreaBtn.setText("显示区域划分");
                }
            }
        });
        locateCenterBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                setUserMapCenter();
            }
        });
    }

    /**
     * 设置中心点
     */
    private void setUserMapCenter() {
        Log.v("pcw","setUserMapCenter : lat : "+ lat+" lon : " + lon);
        LatLng cenpt = new LatLng(lat,lon);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(17.6f)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        mBaiduMap.setMaxAndMinZoomLevel(19f, 17.2f);
        //mBaiduMap.getUiSettings().setScrollGesturesEnabled(false);
        //此处只能setBounds,因为放大后还是要拖动校园的
    }

    //最终能结果→ north:31.208350000000003, east：121.44303999999998, south：31.202850000000005, west：121.43529100000006
    double north = 31.208350000000003, east = 121.44303999999998, south = 31.202850000000005, west = 121.43529100000006;
    private void setUpGroundOverlay() {
        LatLng northeast = new LatLng(north, east);//右上角31.2081750000,121.4424570000
        LatLng southwest = new LatLng(south, west);//左下角31.2028100000,121.4351810000
        LatLngBounds bounds = new LatLngBounds.Builder().include(northeast)
                .include(southwest).build();

        BitmapDescriptor bdGround = BitmapDescriptorFactory
                .fromResource(R.mipmap.groudoverlay);
        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds).image(bdGround).transparency(0.6f);
        mBaiduMap.clear();
        mBaiduMap.addOverlay(ooGround);
        showText.setText("north:" + north + ", east：" + east + ", south：" + south + ", west：" + west);
        Log.e("", "north:" + north + ", east：" + east + ", south：" + south + ", west：" + west);
    }

    Button jiaNBtn, jianNBtn, jianEBtn, jiaEBtn, jiaSBtn, jianSBtn, jiaWBtn, jianWBtn;
    private void initBtns() {
        jiaNBtn = (Button) findViewById(R.id.control_north_jia);
        jianNBtn = (Button) findViewById(R.id.control_north_jian);
        jiaEBtn = (Button) findViewById(R.id.control_east_jia);
        jianEBtn = (Button) findViewById(R.id.control_east_jian);
        jiaSBtn = (Button) findViewById(R.id.control_south_jia);
        jianSBtn = (Button) findViewById(R.id.control_south_jian);
        jiaWBtn = (Button) findViewById(R.id.control_west_jia);
        jianWBtn = (Button) findViewById(R.id.control_west_jian);

        showText = (TextView) findViewById(R.id.txtShow);
        jiaNBtn.setOnClickListener(this);
        jianNBtn.setOnClickListener(this);
        jiaEBtn.setOnClickListener(this);
        jianEBtn.setOnClickListener(this);
        jiaSBtn.setOnClickListener(this);
        jianSBtn.setOnClickListener(this);
        jiaWBtn.setOnClickListener(this);
        jianWBtn.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.control_north_jia:
                north += 0.0000500000;
                break;
            case R.id.control_north_jian:
                north -= 0.0000500000;
                break;
            case R.id.control_east_jia:
                east += 0.0000100000;
                break;
            case R.id.control_east_jian:
                east -= 0.0000100000;
                break;
            case R.id.control_south_jia:
                south += 0.0000500000;
                break;
            case R.id.control_south_jian:
                south -= 0.0000500000;
                break;
            case R.id.control_west_jia:
                west += 0.0000100000;
                break;
            case R.id.control_west_jian:
                west -= 0.0000100000;
                break;
        }
        setUpGroundOverlay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        miSwitch = new MenuItem[arrSchools.length];
        for (int i=0; i<arrSchools.length; i++) {
            miSwitch[i] = menu.add(Menu.NONE, Menu.NONE, i, arrSchools[i]);
            miSwitch[i].setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedOrder = item.getOrder();
        lat = Double.parseDouble(arrLatLons[selectedOrder][0]);
        lon = Double.parseDouble(arrLatLons[selectedOrder][1]);
        setUserMapCenter();

        return true;
    }

    //MapStatusUpdateFactory.newLatLngZoom(cenpt, 17.6f);
    /*UiSettings settings = mBaiduMap.getUiSettings();
        settings.setAllGesturesEnabled(false);
        settings.setOverlookingGesturesEnabled(false);//屏蔽双指下拉时变成3D地图
        settings.setRotationGesturesEnabled(false);//屏蔽旋转
        settings.setZoomGesturesEnabled(false);//是否允许缩放手势
        LatLng northeast = new LatLng(31.21, 121.45);
        LatLng southwest = new LatLng(31.2, 121.44);
        mBaiduMap.setMapStatusLimits(new LatLngBounds.Builder().include(northeast).include(southwest).build());*/
}
