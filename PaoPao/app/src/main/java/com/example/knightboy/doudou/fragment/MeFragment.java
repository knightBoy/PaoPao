package com.example.knightboy.doudou.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.activity.UserInfoActivity;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.util.PreferencesUtils;

/**
 * Created by knightBoy on 2015/8/23.
 */
public class MeFragment extends Fragment implements View.OnClickListener{
    LinearLayout ll_user_info;
    //上下文对象
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_me,container,false);
        //获得上下文对象
        context = getActivity();
        //获得控件引用
        ll_user_info = (LinearLayout)rootview.findViewById(R.id.ll_user_info);
        //设置监听器
        ll_user_info.setOnClickListener(this);

        return rootview;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_user_info:
                forward(UserInfoActivity.class);
                break;
        }
    }

    /**
     * 实现用户页面到详细页面的跳转
     * @param T
     */
    private void forward(Class<?> T){
        Intent intent = new Intent(context,T);
        intent.putExtra("userId", PreferencesUtils.getSharePreInt(context, Cont.USER_ID));
        context.startActivity(intent);
    }
}
