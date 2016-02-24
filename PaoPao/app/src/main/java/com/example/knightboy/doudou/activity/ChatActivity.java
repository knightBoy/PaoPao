package com.example.knightboy.doudou.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.adapter.ChatAdapter;
import com.example.knightboy.doudou.application.ApplicationBase;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.bean.Msg;
import com.example.knightboy.doudou.bean.Session;
import com.example.knightboy.doudou.db.ChatMsgDao;
import com.example.knightboy.doudou.db.SessionDao;
import com.example.knightboy.doudou.util.PreferencesUtils;
import com.example.knightboy.doudou.util.ToastUtil;
import com.example.knightboy.doudou.view.DropdownListView;
import com.example.knightboy.doudou.xmpp.XmppUtil;

import org.jivesoftware.smack.XMPPException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by knightBoy on 2015/8/23.
 */
public class ChatActivity extends Activity implements View.OnClickListener, DropdownListView.OnRefreshListenerHeader {
    //头部控件
    ImageView iv_chat_back;
    TextView tv_user_name;
    ImageView iv_chat_add_friend;
    //列表
    DropdownListView dropdownListView;
    //底部
    ImageView iv_chat_emoji;
    EditText et_user_message;
    TextView tv_send_message;

    //消息列表
    private List<Msg> messages = new ArrayList<>();
    //适配器
    private ChatAdapter chatAdapter;
    //区分发送者和接受者
    private String me;
    private String you;
    private String toUserName;
    private String myName;
    //时间格式
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
    //上下文对象
    private Context context;
    //消息数据表中取数据的偏移量（最新的为0）
    private int offset;
    //广播接收器
    private BroadcastReceiver receiver;
    //数据库操纵类
    private SessionDao sessionDao;
    private ChatMsgDao chatMsgDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = this;
        //获得数据库操纵类
        sessionDao = new SessionDao(context);
        chatMsgDao = new ChatMsgDao(context);
        //获得控件
        findView();
        initListener();
        //获得传递的参数，即通讯的对象
        Intent intent = getIntent();
        me = String.valueOf(PreferencesUtils.getSharePreInt(context, Cont.USER_ID));
        you = intent.getStringExtra("userId");
        toUserName = intent.getStringExtra("userName");
        myName = PreferencesUtils.getSharePreStr(context,Cont.USER_NAME);
        tv_user_name.setText(toUserName);

        //初始化消息
        initMessage();
        //更新消息为已读
        updateMsgToReaded();
        //初始化监听新消息的广播接收器
        initReceiver();
    }

    private void findView(){
        iv_chat_back = (ImageView)findViewById(R.id.iv_chat_back);
        tv_user_name = (TextView)findViewById(R.id.tv_user_name);
        iv_chat_add_friend = (ImageView)findViewById(R.id.iv_chat_add_friend);
        dropdownListView = (DropdownListView)findViewById(R.id.message_chat_listview);
        iv_chat_emoji = (ImageView)findViewById(R.id.iv_chat_emoji);
        et_user_message = (EditText)findViewById(R.id.et_user_message);
        tv_send_message = (TextView)findViewById(R.id.tv_send_message);
    }

    private void initListener(){
        iv_chat_back.setOnClickListener(this);
        dropdownListView.setOnRefreshListenerHead(this);
        tv_send_message.setOnClickListener(this);
    }

    /**
     * 打开聊天界面后初始化显示的聊天记录
     */
    private void initMessage(){
        offset=0;
        messages=chatMsgDao.queryMsg(you,me,offset);
        offset=messages.size();
        chatAdapter = new ChatAdapter(context, messages);
        dropdownListView.setAdapter(chatAdapter);
        dropdownListView.setSelection(messages.size());
    }

    /**
     * 更新消息为已读
     */
    private void updateMsgToReaded() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long row = chatMsgDao.updateAllMsgToRead(you,me);
            }
        }).start();
    }

    /**
     * 发送文本消息
     */
    private void sendMessage(){
        final String content = et_user_message.getText().toString();
        Msg msg = getChatInfoTo(content,Cont.MSG_TYPE_TEXT);
        //存入数据库
        chatMsgDao.insert(msg);
        //更新列表
        messages.add(msg);
        chatAdapter.notifyDataSetChanged();
        //发送消息.接收者ID卍发送者ID卍消息类型卍消息内容卍发送时间卍发送人名字
        final String message=you+Cont.SPLIT+me+Cont.SPLIT+Cont.MSG_TYPE_TEXT
                +Cont.SPLIT+content+Cont.SPLIT+simpleDateFormat.format(new Date())+Cont.SPLIT+myName;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    XmppUtil.sendMessage(ApplicationBase.xmppConnection, message, you);
                    updateSession(Cont.MSG_TYPE_TEXT, content);
                } catch (XMPPException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    ToastUtil.showShortToast(ChatActivity.this, "发送失败");
                    Looper.loop();
                }
            }
        }).start();
    }

    /**
     * 发送的信息
     *  from为收到的消息，to为自己发送的消息
     * @return
     */
    private Msg getChatInfoTo(String message,String msgtype) {
        String time=simpleDateFormat.format(new Date());
        Msg msg = new Msg();
        msg.setFromUser(me);
        msg.setFromUserName(myName);
        msg.setToUser(you);
        msg.setIsComing(1);      //自己发送的
        msg.setType(msgtype);
        msg.setContent(message);
        msg.setDate(time);
        msg.setIsReaded("1");
        return msg;
    }

    /**
     * 发送完消息后要更新会话的内容，会话内容是最新的消息
     * @param type
     * @param content
     */
    void updateSession(String type,String content){
        Session session=new Session();
        session.setFrom(you);
        session.setTo(me);
        session.setNotReadCount("");//未读消息数量
        session.setContent(content);
        session.setTime(simpleDateFormat.format(new Date()));
        session.setType(type);
        session.setIsdispose("1");
        if(sessionDao.isContent(you, me)){
            sessionDao.updateSession(session);
        }else{
            //获取到对方的userName值
            session.setFrom_user(toUserName);
            sessionDao.insertSession(session);
        }
        Intent intent=new Intent(Cont.ACTION_NEW_MSG);    //发送广播，通知会话界面更新
        sendBroadcast(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_send_message:
                sendMessage();
                et_user_message.setText(null);
                break;
            case R.id.iv_chat_back:
                this.finish();
                break;
        }
    }

    /**
     * 下拉加载历史记录
     */
    @Override
    public void onRefresh() {
        List<Msg> list=chatMsgDao.queryMsg(you,me,offset);
        if(list.size()<=0){
            dropdownListView.setSelection(0);
            dropdownListView.onRefreshCompleteHeader();
            return;
        }
        messages.addAll(0,list);
        offset=messages.size();
        dropdownListView.onRefreshCompleteHeader();
        chatAdapter.notifyDataSetChanged();
        dropdownListView.setSelection(list.size());
    }

    /**
     * 初始化广播接收器,主要监听登录状态
     */
    private void initReceiver(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Cont.ACTION_CHAT_NEW_MSG)){
                    int msgId = intent.getIntExtra("msgId",0);
                    Bundle b=intent.getBundleExtra("msg");
                    Msg msg=(Msg) b.getSerializable("msg");    //获得了接收的消息,但不一定属于这个界面
                    if(you.equals(msg.getFromUser())){
                        messages.add(msg);
                        chatAdapter.notifyDataSetChanged();
                        //更新消息为已读
                        long raw = chatMsgDao.updateOneMsgToRead(msgId);
                    }
                }
            }
        };
        //注册广播接收者
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Cont.ACTION_CHAT_NEW_MSG);
        registerReceiver(receiver, mFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
