package com.example.knightboy.doudou.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.knightboy.doudou.application.ApplicationBase;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.listener.CheckConnectionListener;
import com.example.knightboy.doudou.listener.FriendsPacketListener;
import com.example.knightboy.doudou.listener.MsgListener;
import com.example.knightboy.doudou.util.PreferencesUtils;
import com.example.knightboy.doudou.xmpp.XmppConnectionManager;
import com.example.knightboy.doudou.xmpp.XmppUtil;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Presence;

/**
 * Created by knightBoy on 2015/8/29.
 * 添加了用于监听消息的MsgListener(MessageListener)
 * 用于监听连接状态的checkConnectionListener(ConnectionListener)
 * 用于监听好友状态的friendsPacketListener（PacketListener）
 */
public class MsgService extends Service {
    private static MsgService mInstance = null;

    private NotificationManager mNotificationManager;

    private String mUserId, mPassword;
    private XmppConnectionManager mXmppConnectionManager;
    private XMPPConnection mXMPPConnection;

    private CheckConnectionListener checkConnectionListener;
    private FriendsPacketListener friendsPacketListener;

    private final IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        public MsgService getService() {
            return MsgService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        //取得用户ID，密码
        mUserId = String.valueOf(PreferencesUtils.getSharePreInt(this, Cont.USER_ID));
        mPassword = PreferencesUtils.getSharePreStr(this, Cont.PASSWORD);

        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);         // 通知
        mXmppConnectionManager = XmppConnectionManager.getInstance();
        //初始化XMPP任务
        initXMPPTask();
    }

    public static MsgService getInstance() {
        return mInstance;
    }


    /**
     * 初始化xmpp和完成后台登录
     */
    private void initXMPPTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    initXMPP();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化XMPP
     */
    void initXMPP() {
        mXMPPConnection = mXmppConnectionManager.init();						//初始化XMPPConnection
        loginXMPP();															//登录XMPP
        ChatManager chatmanager = mXMPPConnection.getChatManager();
        chatmanager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat arg0, boolean arg1) {
                arg0.addMessageListener(new MsgListener(MsgService.this, mNotificationManager));
            }
        });
    }

    /**
     * 登录XMPP
     */
    void loginXMPP() {
        try {
            mXMPPConnection.connect();
            try{
                if(checkConnectionListener!=null){
                    mXMPPConnection.removeConnectionListener(checkConnectionListener);
                    checkConnectionListener=null;
                }
            }catch(Exception e){

            }
            mXMPPConnection.login(mUserId, mPassword);
            if(mXMPPConnection.isAuthenticated()){         //登录成功
                ApplicationBase.xmppConnection=mXMPPConnection;
                sendLoginBroadcast(true);

                //添加xmpp连接监听
                checkConnectionListener=new CheckConnectionListener(this);
                mXMPPConnection.addConnectionListener(checkConnectionListener);

                // 注册好友状态更新监听
                friendsPacketListener=new FriendsPacketListener(this);
                PacketFilter filter = new AndFilter(new PacketTypeFilter(Presence.class));
                mXMPPConnection.addPacketListener(friendsPacketListener, filter);
                XmppUtil.setPresence(this, mXMPPConnection, 0);//设置在线状态
            }else{
                sendLoginBroadcast(false);
                stopSelf();                  //如果登录失败，自动销毁Service
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendLoginBroadcast(false);
            stopSelf();
        }
    }

    /**
     * 发送登录状态广播
     * @param isLoginSuccess
     */
    void sendLoginBroadcast(boolean isLoginSuccess){
        Intent intent =new Intent(Cont.ACTION_IS_LOGIN_SUCCESS);
        intent.putExtra(Cont.IS_LOGIN_SUCCESS, isLoginSuccess);
        sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        if(mNotificationManager!=null){

        }
        try {
            if (mXMPPConnection != null) {
                mXMPPConnection.disconnect();
                mXMPPConnection = null;
            }
            if(mXmppConnectionManager!=null){
                mXmppConnectionManager = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
