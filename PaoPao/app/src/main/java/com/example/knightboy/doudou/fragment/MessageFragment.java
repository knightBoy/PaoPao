package com.example.knightboy.doudou.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.activity.ChatActivity;
import com.example.knightboy.doudou.adapter.SessionAdapter;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.bean.Msg;
import com.example.knightboy.doudou.bean.Session;
import com.example.knightboy.doudou.db.SessionDao;
import com.example.knightboy.doudou.util.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by knightBoy on 2015/8/23.
 */
public class MessageFragment extends Fragment implements View.OnClickListener{
    private LinearLayout ll_no_message;
    //上下文对象
    private Context context;
    //session链表
    private List<Session> sessions = new ArrayList<>();
    //适配器
    private SessionAdapter sessionAdapter;
    private ListView sesListView;
    //数据库操作类
    private SessionDao sessionDao;
    //用户自身标志
    private String userId;
    //监听新消息的广播接收器
    private BroadcastReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_message,container,false);
        context = getActivity();
        //初始化操作
        userId = String.valueOf(PreferencesUtils.getSharePreInt(context, Cont.USER_ID));
        sessionDao = new SessionDao(context);
        //获得控件
        ll_no_message = (LinearLayout)rootview.findViewById(R.id.ll_no_message);
        sesListView = (ListView)rootview.findViewById(R.id.sessionListView);

        //初始化监听器
        initListener();
        //广播接收器
        initReceiver();

        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    //为控件绑定监听器
    private void initListener(){
        //设置点击事件，这里只处理聊天消息
        sesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Session session=sessions.get(position);
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("userId", session.getFrom());
                intent.putExtra("userName",session.getFrom_user());
                context.startActivity(intent);
            }
        });
        //设置长按事件
        sesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Session session = sessions.get(position);
                AlertDialog.Builder bd=new AlertDialog.Builder(context);
                bd.setItems(new String[] {"删除会话"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        sessionDao.deleteSession(session);
                        initData();
                    }
                }).create().show();
                return true;
            }
        });
    }

    /**
     * 初始化数据,数据变动后调用
     */
    private void initData() {
        sessions=sessionDao.queryAllSessions(userId);
        sessionAdapter = new SessionAdapter(context, sessions);
        sesListView.setAdapter(sessionAdapter);
        if(sessions.size()>0){
            ll_no_message.setVisibility(View.GONE);
        }else{
            ll_no_message.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化广播接收器,主要监听会话状态
     */
    private void initReceiver(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initData();
            }
        };
        //注册广播接收者
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Cont.ACTION_NEW_MSG);
        context.registerReceiver(receiver, mFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(receiver);
    }
}
