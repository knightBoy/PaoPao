package com.example.knightboy.doudou.listener;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.activity.ChatActivity;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.bean.Msg;
import com.example.knightboy.doudou.bean.Session;
import com.example.knightboy.doudou.db.ChatMsgDao;
import com.example.knightboy.doudou.db.SessionDao;
import com.example.knightboy.doudou.service.MsgService;
import com.example.knightboy.doudou.util.PreferencesUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by knightBoy on 2015/8/30.
 * 监听消息
 */
public class MsgListener implements MessageListener {
    private MsgService context;
    private NotificationManager mNotificationManager;

    private boolean isShowNotice=false;
    private Notification mNotification;

    private KeyguardManager mKeyguardManager = null;

    //操纵数据库的DAO
    private ChatMsgDao msgDao;
    private SessionDao sessionDao;

    public MsgListener(MsgService context,NotificationManager mNotificationManager){
        this.context=context;
        this.mNotificationManager=mNotificationManager;
        mKeyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);
        sessionDao=new SessionDao(context);
        msgDao=new ChatMsgDao(context);
    }

    /**
     * 监听对方发来的消息
     * @param arg0
     * @param message
     */
    @Override
    public void processMessage(Chat arg0, Message message) {
        try {
            String msgBody = message.getBody();
            if (TextUtils.isEmpty(msgBody))
                return;

            //接收者卍发送者卍消息类型卍消息内容卍发送时间卍发送者名字
            String[] msgs=msgBody.split(Cont.SPLIT);
            String to=msgs[0];         //接收者,当然是自己
            String from=msgs[1];       //发送者，谁给你发的消息
            String msgtype=msgs[2];    //消息类型
            String msgcontent=msgs[3]; //消息内容
            String msgtime=msgs[4];    //消息时间
            String fromName = msgs[5]; //发送者的名字

            //建立一个会话
            Session session=new Session();
            session.setFrom(from);
            session.setFrom_user(fromName);
            session.setTo(to);
            session.setNotReadCount("");   //未读消息数量
            session.setTime(msgtime);

            //1.....添加好友的请求
            if(msgtype.equals(Cont.MSG_TYPE_ADD_FRIEND)){
                session.setType(msgtype);
                session.setContent(msgcontent);
                session.setIsdispose("0");
                sessionDao.insertSession(session);

                //2.....对方同意添加好友的请求
            }else	if(msgtype.equals(Cont.MSG_TYPE_ADD_FRIEND_SUCCESS)){
                session.setType(Cont.MSG_TYPE_TEXT);
                session.setContent("我们已经是好友了，快来和我聊天吧！");
                sessionDao.insertSession(session);
                //发送广播更新好友列表
                Intent intent=new Intent(Cont.ACTION_ADDFRIEND_SUCCESS);
                context.sendBroadcast(intent);

                //3.....一般文本类型
            }else if(msgtype.equals(Cont.MSG_TYPE_TEXT)){
                Msg msg=new Msg();
                msg.setToUser(to);
                msg.setFromUser(from);
                msg.setFromUserName(fromName);
                msg.setIsComing(0);
                msg.setContent(msgcontent);
                msg.setDate(msgtime);
                msg.setIsReaded("0");
                msg.setType(msgtype);
                int msgId = msgDao.insert(msg);
                //发送消息
                sendNewMsg(msg,msgId);

                session.setIsdispose("0");
                session.setType(Cont.MSG_TYPE_TEXT);
                session.setContent(msgcontent);
                if(sessionDao.isContent(from, to)){     //判断最近联系人列表是否已存在记录
                    sessionDao.updateSession(session);
                }else{
                    sessionDao.insertSession(session);
                }

                //4.....图片类型
            }else if(msgtype.equals(Cont.MSG_TYPE_IMG)){
                Msg msg=new Msg();
                msg.setToUser(to);
                msg.setFromUser(from);
                msg.setIsComing(0);
                msg.setContent(msgcontent);
                msg.setDate(msgtime);
                msg.setIsReaded("0");
                msg.setType(msgtype);
                int msgId = msgDao.insert(msg);
                //发送消息
                sendNewMsg(msg,msgId);

                session.setType(Cont.MSG_TYPE_TEXT);
                session.setContent("[图片]");
                if(sessionDao.isContent(from, to)){
                    sessionDao.updateSession(session);
                }else{
                    sessionDao.insertSession(session);
                }
            }
            //发送广播，通知消息界面更新,就是会话界面
            Intent intent=new Intent(Cont.ACTION_NEW_MSG);
            context.sendBroadcast(intent);
            //显示通知,只显示是谁发来的
            showNotice(session.getFrom(),session.getFrom_user());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * //发送广播到聊天界面
     * @param msg
     */
    void sendNewMsg(Msg msg, int msgId){
        Intent intent=new Intent(Cont.ACTION_CHAT_NEW_MSG);
        Bundle b=new Bundle();
        b.putSerializable("msg", msg);
        intent.putExtra("msg", b);
        intent.putExtra("msgId",msgId);
        context.sendBroadcast(intent);
    }

    @SuppressWarnings("deprecation")
    public void showNotice(String userId,String userName) {
        // 更新通知栏
        CharSequence tickerText = "来自" + userName +"新消息";
        mNotification = new Notification(R.drawable.icon_logo, tickerText, System.currentTimeMillis());
        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        if(PreferencesUtils.getSharePreBoolean(context, Cont.MSG_IS_VOICE)){
            // 设置默认声音
            mNotification.defaults |= Notification.DEFAULT_SOUND;
        }
        if(PreferencesUtils.getSharePreBoolean(context, Cont.MSG_IS_VIBRATE)){
            // 设定震动(需加VIBRATE权限)
            mNotification.defaults |= Notification.DEFAULT_VIBRATE;
        }
        //设置点击通知后的跳转
        PendingIntent contentIntent = PendingIntent.getActivities(context,0,makeIntentStack(context,userId,userName),PendingIntent.FLAG_CANCEL_CURRENT);

        // LED灯
        mNotification.defaults |= Notification.DEFAULT_LIGHTS;
        mNotification.ledARGB = 0xff00ff00;
        mNotification.ledOnMS = 500;
        mNotification.ledOffMS = 1000;
        mNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
        mNotification.setLatestEventInfo(context, "泡泡", tickerText, contentIntent);
        mNotificationManager.notify(Cont.NOTIFY_ID, mNotification);// 通知
    }

    private Intent[] makeIntentStack(Context context,String from,String name) {
        Intent[] intents = new Intent[2];
        intents[0] = Intent.makeRestartActivityTask(new ComponentName(context, com.example.knightboy.doudou.activity.MainActivity.class));
        intents[1] = new Intent(context,  com.example.knightboy.doudou.activity.ChatActivity.class);
        intents[1].putExtra("userId",from);
        intents[1].putExtra("userName",name);
        return intents;
    }
}
