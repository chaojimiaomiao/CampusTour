package com.example.zhaibingjie.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {//

    private MenuItem[] miSwitch = null;

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private double lat;
    private double lon;
    private float centerScale;
    private String[] arrSchools;
    private String[][] arrLatLons;
    private String[] arrZooms;
    private int mOrder;

    private Button showAreaBtn, locateCenterBtn;
    private Button showInfoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main1);

        arrSchools = getResources().getStringArray(R.array.arr_schools);

        mMapView = (MapView) findViewById(R.id.bmapView1);
        mBaiduMap = mMapView.getMap();

        parseInfos();
        //initBtns();
        initShowBtns();

        lat = Double.parseDouble(arrLatLons[0][0]);
        lon = Double.parseDouble(arrLatLons[0][1]);
        centerScale = 17.6f;
        setUserMapCenter(centerScale);
    }

    private void parseInfos() {
        String[] latlons = getResources().getStringArray(R.array.arr_latlon);
        arrLatLons = new String[latlons.length][2];
        for (int i= 0; i< latlons.length; i++) {
            String[] tmpS = latlons[i].split(",");
            arrLatLons[i][0] = tmpS[0];
            arrLatLons[i][1] = tmpS[1];
        }

        arrZooms = getResources().getStringArray(R.array.arr_zoom);
    }

    private void initShowBtns() {
        showInfoBtn = (Button)findViewById(R.id.show_info);
        showInfoBtn.setOnClickListener(this);
        showAreaBtn = (Button)findViewById(R.id.show_area);
        locateCenterBtn = (Button)findViewById(R.id.show_center);
        showAreaBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (showAreaBtn.getText().toString().contains("显示")) {
                    if (mOrder == 0) {
                        setUpGroundOverlay(R.mipmap.groudoverlay);
                    } else if (mOrder == 1) {
                        north = 31.041343000000012; east = 121.45706499999997;
                        south = 31.022763999999995; west = 121.42948899999998;
                        setUpGroundOverlay(R.mipmap.jiaotong_overlay1);
                    } else if (mOrder == 4) {
                        north = 31.04508699999999; east = 121.47047200000002;
                        south = 31.02850399999997; west = 121.45184699999963;
                        setUpGroundOverlay(R.mipmap.huashi_overlay);
                    } else if (mOrder == 5) {
                        north = 30.842956000000036; east = 121.51899600000017;
                        south = 30.830042;          west = 121.50206299999972;
                        setUpGroundOverlay(R.mipmap.huadongligong_overlay);
                    } else if (mOrder == 6) {
                        north = 31.299497000000034; east = 121.23025;
                        south = 31.28345899999999;  west = 121.2115;
                        setUpGroundOverlay(R.mipmap.tongji_overlay);
                    }
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
                setUserMapCenter(centerScale);
            }
        });
    }

    /**
     * 设置中心点
     */
    private void setUserMapCenter(float scale) {
        setUserMapCenter(scale, 0);
    }

    private void setUserMapCenter(float scale, float rotate) {
        Log.v("pcw","setUserMapCenter : lat : "+ lat+" lon : " + lon);
        LatLng cenpt = new LatLng(lat,lon);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(scale)
                .rotate(rotate)//-20
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

        //改变地图状态
        mBaiduMap.setMaxAndMinZoomLevel(scale + 1.4f, scale - 0.4f);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        //mBaiduMap.getUiSettings().setScrollGesturesEnabled(false);
        //此处只能setBounds,因为放大后还是要拖动校园的
    }

    //这是交大徐汇的贴图结果
    double north = 31.208350000000003, east = 121.44303999999998, south = 31.202850000000005, west = 121.43529100000006;
    private void setUpGroundOverlay(int resourceId) {
        LatLng northeast = new LatLng(north, east);//右上角31.2081750000,121.4424570000
        LatLng southwest = new LatLng(south, west);//左下角31.2028100000,121.4351810000
        LatLngBounds bounds = new LatLngBounds.Builder().include(northeast)
                .include(southwest).build();

        BitmapDescriptor bdGround = BitmapDescriptorFactory
                .fromResource(resourceId);
        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds).image(bdGround).transparency(0.6f);
        mBaiduMap.clear();
        mBaiduMap.addOverlay(ooGround);
    }

    private void showInfo() {
        LinearLayout popLL = (LinearLayout) getLayoutInflater().inflate(R.layout.popup_text, null);

        mBaiduMap.showInfoWindow(new InfoWindow(popLL, new LatLng(lat,lon), -40));
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
        mOrder = selectedOrder;
        lat = Double.parseDouble(arrLatLons[selectedOrder][0]);
        lon = Double.parseDouble(arrLatLons[selectedOrder][1]);
        String zoomS = arrZooms[selectedOrder];
        centerScale = new Float(zoomS);
        /*if (selectedOrder == 1) {//交大(闵行校区)
            rotate = -20;
        } else {
            rotate = 0;
        }
        setUserMapCenter(centerScale);*/
        if (selectedOrder == 1) {//交大(闵行校区)
            setUserMapCenter(centerScale, 0);//-20
        } else {
            setUserMapCenter(centerScale);
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        showInfo();
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
