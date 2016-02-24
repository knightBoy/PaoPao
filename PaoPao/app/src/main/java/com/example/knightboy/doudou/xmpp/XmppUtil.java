package com.example.knightboy.doudou.xmpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.util.PreferencesUtils;

/**
 * XMPP的一些基本操作,如注册，设置在线状态，添加好友之类的
 */

public class XmppUtil {

    /**
     * 登录
     *
     * @param account 登录帐号
     * @param pwd 登录密码
     * @return
     */
    public boolean login(XMPPConnection mXMPPConnection,String account, String pwd) {
        try {
            if (mXMPPConnection == null)
                return false;
            /** 登录 */
            mXMPPConnection.login(account, pwd);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 注册 
     *
     * @param account 注册帐号
     * @param password 注册密码
     * @return 1、注册成功 0、服务器没有返回结果 2、这个账号已经存在 3、注册失败
     */
    public static int register(XMPPConnection mXMPPConnection,String account, String password) {
        Registration reg = new Registration();
        reg.setType(IQ.Type.SET);
        reg.setTo(mXMPPConnection.getServiceName());
        // 注意这里createAccount注册时，参数是UserName，不是jid，是"@"前面的部分。  
        reg.setUsername(account);
        reg.setPassword(password);
        // 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！  
        reg.addAttribute("android", "geolo_createUser_android");
        PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));
        PacketCollector collector =mXMPPConnection.createPacketCollector(filter);
        mXMPPConnection.sendPacket(reg);
        IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        // Stop queuing results停止请求results（是否成功的结果）  
        collector.cancel();
        if (result == null) {
            return 0;
        } else if (result.getType() == IQ.Type.RESULT) {
            return 1;
        } else {
            if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
                return 2;
            } else {
                return 3;
            }
        }
    }

    /**
     * 更改用户状态 ，只设置在线、离线
     */
    public static void setPresence(Context context,XMPPConnection con,int code) {
        if (con == null)
            return;
        Presence presence=null;
        switch (code) {
            case 0:
                presence = new Presence(Presence.Type.available);     //在线
                break;
            case 1:
                presence = new Presence(Presence.Type.unavailable);   //离线
                break;
            default:
                break;
        }
        if(presence!=null){
            con.sendPacket(presence);
        }
    }

    /**
     * 删除当前用户
     * @param connection
     * @return
     */
    public static boolean deleteAccount(XMPPConnection connection)
    {
        try {
            connection.getAccountManager().deleteAccount();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 修改密码
     * @param connection
     * @return
     */
    public static boolean changePassword(XMPPConnection connection,String pwd)
    {
        try {
            connection.getAccountManager().changePassword(pwd);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 添加好友 无分组
     *
     * @param roster
     * @param userName
     * @param name
     * @return
     */
    public static boolean addUser(Roster roster, String userName, String name) {
        try {
            roster.createEntry(userName, name, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除一个好友
     * @param roster
     * @param userJid
     * @return
     */
    public static boolean removeUser(Roster roster,String userJid)
    {
        try {
            RosterEntry entry = roster.getEntry(userJid);
            roster.removeEntry(entry);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建一个组 
     */
    public static boolean addGroup(Roster roster,String groupName)
    {
        try {
            roster.createGroup(groupName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加一个好友到分组
     * @param roster
     * @param userName
     * @param name
     * @return
     */
    public static boolean addUsers(Roster roster,String userName,String name,String groupName)
    {
        try {
            roster.createEntry(userName, name, new String[]{groupName});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 把一个好友添加到一个组中
     * @param userJid
     * @param groupName
     */
    public static void addUserToGroup(final String userJid, final String groupName,
                                      final XMPPConnection connection) {
        RosterGroup group = connection.getRoster().getGroup(groupName);
        // 这个组已经存在就添加到这个组，不存在创建一个组
        RosterEntry entry = connection.getRoster().getEntry(userJid);
        try {
            if (group != null) {
                if (entry != null)
                    group.addEntry(entry);
            } else {
                RosterGroup newGroup = connection.getRoster().createGroup("我的好友");
                if (entry != null)
                    newGroup.addEntry(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 把一个好友从组中删除
     * @param userJid
     * @param groupName
     */
    public static void removeUserFromGroup(final String userJid,final String groupName, final XMPPConnection connection) {
        RosterGroup group = connection.getRoster().getGroup(groupName);
        if (group != null) {
            try {
                RosterEntry entry = connection.getRoster().getEntry(userJid);
                if (entry != null)
                    group.removeEntry(entry);
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修改签名
     */
    public static void changeSign(XMPPConnection connection,int code , String content){
        Presence presence = getOnlineStatus(code);
        presence.setStatus(content);
        connection.sendPacket(presence);
    }


    /**
     * 发送消息
     * @param content
     * @param touser
     * @throws XMPPException
     */
    public static void sendMessage(XMPPConnection mXMPPConnection,String content,String touser) throws XMPPException {
        if(mXMPPConnection==null||!mXMPPConnection.isConnected()){
            throw new XMPPException();
        }
        ChatManager chatmanager = mXMPPConnection.getChatManager();
        Chat chat =chatmanager.createChat(touser + "@" + Cont.XMPP_HOST, null);
        if (chat != null) {
            chat.sendMessage(content);
        }
    }

    /**
     * 获得对应在线状态的Presence
     * @param code
     * @return
     */
    public static Presence getOnlineStatus(int code){
        Presence presence=null;
        switch (code) {
            case 0:
                presence = new Presence(Presence.Type.available);  //在线
                break;
            case 1:
                presence = new Presence(Presence.Type.unavailable);  //离线
                break;
            default:
                break;
        }
        return presence;
    }

}
