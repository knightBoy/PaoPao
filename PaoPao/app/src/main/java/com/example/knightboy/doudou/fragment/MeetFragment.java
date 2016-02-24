package com.example.knightboy.doudou.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.adapter.MeetAdapter;
import com.example.knightboy.doudou.bean.Comment;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.bean.MeetBean;
import com.example.knightboy.doudou.http.AsyncHttpGet;
import com.example.knightboy.doudou.json.MeetJson;
import com.example.knightboy.doudou.listener.HttpJsonDataListener;
import com.example.knightboy.doudou.view.KBLoadingDialog;
import com.example.knightboy.doudou.view.XListView;

import java.util.ArrayList;

/**
 * Created by knightBoy on 2015/8/23.
 */
public class MeetFragment extends Fragment {
    private XListView xListView;
    private ArrayList<MeetBean> meetBeans = new ArrayList<>();
    private MeetAdapter meetAdapter;
    private Dialog loadingDialog;

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_meet,container,false);
        context = getActivity();
        xListView = (XListView)rootview.findViewById(R.id.meetListView);
        //禁止下拉刷新
        xListView.setPullRefreshEnable(false);

        return rootview;
    }

    @Override
    public void onStart() {
        loadingDialog = KBLoadingDialog.createLoadingDialog(context,"正在加载……");
        loadingDialog.show();
        //发送请求
        AsyncHttpGet asyncHttpGet = new AsyncHttpGet();
        asyncHttpGet.setHttpJsonDataListener(new HttpJsonDataListener(){
            @Override
            public void onDataUpdate(String jsonResult) {
                super.onDataUpdate(jsonResult);
                if(jsonResult != null){
                    loadingDialog.dismiss();
                    meetBeans = MeetJson.jsonToMeets(jsonResult);
                    meetAdapter = new MeetAdapter(context,meetBeans);
                    xListView.setAdapter(meetAdapter);
                }
            }
        });
        asyncHttpGet.execute(Cont.GET_AROUND_USER_URL);
        super.onStart();
    }
}
