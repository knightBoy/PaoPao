package com.example.knightboy.doudou.fragment.subfragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.adapter.BrainAdapter;
import com.example.knightboy.doudou.bean.BrainBean;
import com.example.knightboy.doudou.view.XListView;

import java.util.ArrayList;

/**
 * Created by knightBoy on 2015/8/25.
 */
public class BestBrainFragment extends Fragment implements XListView.IXListViewListener {
    private XListView xListView;
    //存储动态的链表
    private ArrayList<BrainBean> brainItems = new ArrayList<>();
    //提供数据的adapter
    private BrainAdapter brainAdapter;
    //上下文对象
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_brain_fragment_best,container,false);
        //获得控件
        xListView = (XListView)rootview.findViewById(R.id.xlistview_brain_best);
        //必要操作
        xListView.setPullLoadEnable(true);
        context = getActivity();

        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        //初始化数据
        initData();
        brainAdapter = new BrainAdapter(context,brainItems);
        xListView.setAdapter(brainAdapter);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    private void initData(){
        for(int i = 0;i < 15; i++){
            BrainBean brainBean = new BrainBean();
            brainBean.setSerialNum(1);
            brainBean.setUserName("往事随风");
            brainBean.setType("冷笑话");
            brainBean.setContent(getResources().getString(R.string.brain_string3));
            brainBean.setGoodNum(30);
            brainBean.setUserId(1);
            brainBean.setBgDrawableId(R.drawable.bg_3);
            brainItems.add(brainBean);
        }
    }
}
