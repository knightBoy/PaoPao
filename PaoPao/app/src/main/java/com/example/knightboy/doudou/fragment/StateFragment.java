package com.example.knightboy.doudou.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.activity.EditStateActivity;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.bean.StateBean;
import com.example.knightboy.doudou.adapter.StatesAdapter;
import com.example.knightboy.doudou.http.AsyncHttpGet;
import com.example.knightboy.doudou.json.StateJson;
import com.example.knightboy.doudou.listener.HttpJsonDataListener;
import com.example.knightboy.doudou.util.NetUtil;
import com.example.knightboy.doudou.util.ToastUtil;
import com.example.knightboy.doudou.view.KBLoadingDialog;
import com.example.knightboy.doudou.view.XListView;

import java.util.ArrayList;

/**
 * Created by knightBoy on 2015/8/23.
 */
public class StateFragment extends Fragment implements XListView.IXListViewListener , View.OnClickListener {
    private XListView xListView;
    //存储动态的链表
    private ArrayList<StateBean> stateItems = new ArrayList<>();
    //提供数据的adapter
    private StatesAdapter statesAdapter;
    //上下文对象
    private Context context;
    //按钮对象
    private ImageView iv_editstate;
    //初始化加载进度框
    private Dialog dialog = null;
    //请求页数目
    private static int loadMoreCount = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_state,container,false);
        //获得控件
        xListView = (XListView)rootview.findViewById(R.id.xlistview_states);
        iv_editstate = (ImageView)rootview.findViewById(R.id.iv_editstate);
        //绑定监听器
        iv_editstate.setOnClickListener(this);
        xListView.setXListViewListener(this);

        //必要操作
        xListView.setPullLoadEnable(true);
        context = getActivity();

        //初始化数据，先判断网络状态
        if(NetUtil.isNetConnected(context) == true){
            //加载进度框
            if(dialog == null){
                dialog = KBLoadingDialog.createLoadingDialog(context,"正在加载……");
            }
            dialog.show();
            //取得数据
            onRefresh();
        }else {
            ToastUtil.showShortToast(context,"网络异常");
        }

        return rootview;
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        //更正下拉数
        loadMoreCount = 1;

        String url = Cont.GET_STATE_REQUEST_URL + "?action=0&pageCount=" + loadMoreCount;
        AsyncHttpGet asyncHttpGet = new AsyncHttpGet();
        asyncHttpGet.setHttpJsonDataListener(new HttpJsonDataListener() {
            @Override
            public void onDataUpdate(String jsonResult) {
                if (jsonResult != null) {
                    String newjsonString = jsonResult.substring(4, jsonResult.length());
                    //刷新所有数据
                    stateItems = StateJson.refreshStateJson(stateItems, newjsonString);
                    statesAdapter = new StatesAdapter(context, stateItems);
                    statesAdapter.setxListView(xListView);
                    xListView.setAdapter(statesAdapter);
                    onLoadOver();
                } else {
                    stopDialog();
                    ToastUtil.showShortToast(context, "服务器异常");
                }
                super.onDataUpdate(jsonResult);
            }
        });
        asyncHttpGet.execute(url);
    }

    //上拉加载
    @Override
    public void onLoadMore() {
        String url = Cont.GET_STATE_REQUEST_URL + "?action=1&pageCount=" + loadMoreCount;
		loadMoreCount++;
        AsyncHttpGet asyncHttpGet = new AsyncHttpGet();
        asyncHttpGet.setHttpJsonDataListener(new HttpJsonDataListener(){
            @Override
            public void onDataUpdate(String jsonResult) {
                if(jsonResult != null){
                    String newjsonString = jsonResult.substring(4, jsonResult.length());
                    //加载更多数据
                    int orignalSize = stateItems.size();
                    stateItems = StateJson.loadMoreStateJson(stateItems, newjsonString);
                    statesAdapter.notifyDataSetChanged();
                    onLoadOver();
                    //提示用户加载到底了
                    if(orignalSize == stateItems.size()){
                        ToastUtil.showShortToast(context,"加载到底了");
                    }
                }else {
                    stopDialog();
                    ToastUtil.showShortToast(context,"服务器异常");
                }
                super.onDataUpdate(jsonResult);
            }
        });
        asyncHttpGet.execute(url);
    }

    private void onLoadOver() {
        stopDialog();

        xListView.stopRefresh();
        xListView.stopLoadMore();
        xListView.setRefreshTime("刚刚");
    }

    private void stopDialog(){
        if(dialog != null){
            dialog.dismiss();
        }
    }

    //处理所有点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_editstate:
                Intent intent = new Intent(context, EditStateActivity.class);
                startActivityForResult(intent,0);
                break;
            default:
                break;
        }
    }

    //编辑动态界面结束后的回调函数
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dialog.show();
        //取得数据
        onRefresh();
    }
}
