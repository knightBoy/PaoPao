package com.example.knightboy.doudou.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.fragment.BrainFragment;
import com.example.knightboy.doudou.fragment.MeFragment;
import com.example.knightboy.doudou.fragment.MeetFragment;
import com.example.knightboy.doudou.fragment.MessageFragment;
import com.example.knightboy.doudou.fragment.StateFragment;
import com.example.knightboy.doudou.http.AsyncHttpPost;
import com.example.knightboy.doudou.listener.HttpJsonDataListener;
import com.example.knightboy.doudou.service.MsgService;
import com.example.knightboy.doudou.util.PreferencesUtils;
import com.example.knightboy.doudou.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by knightBoy on 2015/8/23.
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {
    //定义布局对象,可用用来改变布局背景之类的
    private RelativeLayout menu_message_rl, menu_state_rl, menu_meet_rl, menu_brain_rl, menu_me_rl;
    //id数组，为了将来操作方便
    private final int[] bottomBarId = new int[]{R.id.rl_message,R.id.rl_state,R.id.rl_meet,R.id.rl_brain,R.id.rl_me};
    //表示初始化操作的参数
    private final int DEFAULT_OPER = 0;
    //上下文对象
    private Context context;
    //广播接收器对象
    private BroadcastReceiver receiver;

    //Fragment对象，不要每次都重新创建，不然每次都刷新
    private Fragment messageFrament, stateFragment, meetFragment, brainFragment, meFragment;

    //百度定位
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //保存上下文对象
        context = this;
        //获得控件引用
        findView();
        //给菜单图标绑定监听器
        initListener();
        //默认选中消息菜单,这里传入一个0参数,进入switch的default选项
        replaceFragmentByClick(DEFAULT_OPER);

        //开启核心即时通讯服务
        Intent intent=new Intent(context,MsgService.class);
        startService(intent);
        //初始化广播接收器
        initReceiver();

        //初始化一些本该在注册时进行的用户数据操作
        initUserPosition();
    }

    private void findView(){
        menu_message_rl = (RelativeLayout)findViewById(R.id.rl_message);
        menu_state_rl = (RelativeLayout)findViewById(R.id.rl_state);
        menu_meet_rl = (RelativeLayout)findViewById(R.id.rl_meet);
        menu_brain_rl = (RelativeLayout)findViewById(R.id.rl_brain);
        menu_me_rl = (RelativeLayout)findViewById(R.id.rl_me);
        //获得fragment
        messageFrament = (MessageFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_message);
        stateFragment = (StateFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_state);
        meetFragment = (MeetFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_meet);
        brainFragment = (BrainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_brain);
        meFragment = (MeFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_me);
    }

    private void initListener(){
        menu_message_rl.setOnClickListener(this);
        menu_state_rl.setOnClickListener(this);
        menu_meet_rl.setOnClickListener(this);
        menu_brain_rl.setOnClickListener(this);
        menu_me_rl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        replaceFragmentByClick(v.getId());
    }

    //根据点击的菜单的id改变fragment
    private void replaceFragmentByClick(int id){
        //根据新的fragment替换之前的
        // 得到Fragment事务管理器
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        switch (id){
            case R.id.rl_message:
                ft.hide(stateFragment).hide(meetFragment).hide(brainFragment).hide(meFragment).show(messageFrament).commit();
                break;
            case R.id.rl_state:
                ft.hide(messageFrament).hide(meetFragment).hide(brainFragment).hide(meFragment).show(stateFragment).commit();
                break;
            case R.id.rl_meet:
                ft.hide(messageFrament).hide(stateFragment).hide(brainFragment).hide(meFragment).show(meetFragment).commit();
                break;
            case R.id.rl_brain:
                ft.hide(stateFragment).hide(meetFragment).hide(messageFrament).hide(meFragment).show(brainFragment).commit();
                break;
            case R.id.rl_me:
                ft.hide(stateFragment).hide(meetFragment).hide(brainFragment).hide(messageFrament).show(meFragment).commit();
                break;
            default:
                ft.hide(stateFragment).hide(meetFragment).hide(brainFragment).hide(meFragment).show(messageFrament).commit();
                //设置默认选中菜单的图片及字体颜色
                ImageView iv_message_default = (ImageView)findViewById(R.id.iv_message);
                TextView tv_message_default = (TextView)findViewById(R.id.tv_message);
                iv_message_default.setSelected(true);
                tv_message_default.setTextColor(getResources().getColor(R.color.menu_text_click));
                break;
        }

        //改变按钮的样式，在不是初始化操作的时候
        if(id != DEFAULT_OPER){
            for(int i = 0; i < 5; i++){
                //获取到菜单的RelativeLayout
                RelativeLayout barLayout = (RelativeLayout)findViewById(bottomBarId[i]);
                ImageView imageView;
                TextView textView;
                //判断是不是消息菜单项
                if(barLayout.getChildAt(0) instanceof LinearLayout){
                    LinearLayout linearLayout = (LinearLayout)barLayout.getChildAt(0);
                    imageView = (ImageView)linearLayout.getChildAt(0);
                    textView = (TextView)linearLayout.getChildAt(1);
                }else{
                    FrameLayout frameLayout = (FrameLayout)barLayout.getChildAt(0);
                    LinearLayout linearLayout = (LinearLayout)frameLayout.getChildAt(0);
                    imageView = (ImageView)linearLayout.getChildAt(0);
                    textView = (TextView)linearLayout.getChildAt(1);
                }
                //设置图片的状态
                if(id == bottomBarId[i]){
                    imageView.setSelected(true);
                    textView.setTextColor(getResources().getColor(R.color.menu_text_click));
                }else {
                    imageView.setSelected(false);
                    textView.setTextColor(getResources().getColor(R.color.menu_text_normal));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
        finish();
    }

    /**
     * 初始化用户位置
     */
    private void initUserPosition(){
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);            //注册监听函数
        initLocation();
        mLocationClient.start();
    }

    /**
     * 配置定位SDK参数
     */
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    /**
     * 初始化广播接收器,主要监听登录状态
     */
    private void initReceiver(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Cont.ACTION_IS_LOGIN_SUCCESS)){
                    boolean isLoginSuccess=intent.getBooleanExtra(Cont.IS_LOGIN_SUCCESS, false);
                    if(isLoginSuccess){//登录成功
                        ToastUtil.showShortToast(context, "聊天服务正常");
                        //默认开启声音和震动提醒
                        PreferencesUtils.putSharePre(context, Cont.MSG_IS_VOICE, true);
                        PreferencesUtils.putSharePre(context, Cont.MSG_IS_VIBRATE, true);
                    }else{
                        ToastUtil.showShortToast(context, "聊天服务异常");
                    }
                }
            }
        };
        //注册广播接收者
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Cont.ACTION_IS_LOGIN_SUCCESS);
        registerReceiver(receiver, mFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /**
     * 实现BDLocationListener接口
     */
    class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            int code = bdLocation.getLocType();
            if(code == 61 || code == 65 || code == 66 || code == 161){  //定位成功
                mLocationClient.stop();                         //停止定位服务
                String city = bdLocation.getCity();
                String cityCode = bdLocation.getCityCode();
                double latitude = bdLocation.getLatitude();     //纬度
                double longtitude = bdLocation.getLongitude();  //经度
                PreferencesUtils.putSharePre(context,Cont.USER_POSITION,city);
                //上传用户位置
                AsyncHttpPost asyncHttpPost = new AsyncHttpPost(Cont.SET_USER_POSITION);
                asyncHttpPost.setHttpJsonDataListener(new HttpJsonDataListener(){
                    @Override
                    public void onDataUpdate(String jsonResult) {
                        super.onDataUpdate(jsonResult);
                    }
                });
                Map<String,String> map = new HashMap<>();
                map.put("city",city);
                map.put("cityCode",cityCode);
                map.put("longtitude",String.valueOf(longtitude));
                map.put("latitude",String.valueOf(latitude));
                asyncHttpPost.execute(map);
            }
        }
    }
}
