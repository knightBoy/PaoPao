package com.example.knightboy.doudou.listener;

import android.content.Intent;

import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.service.MsgService;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

/**
 * Created by knightBoy on 2015/8/30.
 * 好友状态的更新
 */
public class FriendsPacketListener implements PacketListener {
    MsgService context;
    public FriendsPacketListener(MsgService context){
        this.context=context;
    }

    @Override
    public void processPacket(Packet packet) {
        if(packet.getFrom().equals(packet.getTo())){
            return;
        }
        if (packet instanceof Presence) {
            Presence presence = (Presence) packet;
            final String from = presence.getFrom().split("@")[0];     //发送方
            String to = presence.getTo().split("@")[0];               //接收方
            if(from.equals(to)){
                return;
            }

            if (presence.getType().equals(Presence.Type.subscribe)) {
            // /好友申请
            } else if (presence.getType().equals(Presence.Type.subscribed)) {
            //同意添加好友
            } else if (presence.getType().equals(Presence.Type.unsubscribe)) {
            //拒绝添加好友  和  删除好友
            } else if (presence.getType().equals(Presence.Type.unsubscribed)){

            //好友下线   要更新好友列表，可以在这收到包后，发广播到指定页面   更新列表
            } else if (presence.getType().equals(Presence.Type.unavailable)) {
                Intent intent=new Intent(Cont.ACTION_FRIENDS_ONLINE_STATUS_CHANGE);
                intent.putExtra(Cont.PRESENCE_FROM, from);
                intent.putExtra(Cont.ON_LINE_STATUAS, 0);
                context.sendBroadcast(intent);

                //好友上线
            } else if(presence.getType().equals(Presence.Type.available)){
                Intent intent=new Intent(Cont.ACTION_FRIENDS_ONLINE_STATUS_CHANGE);
                intent.putExtra(Cont.PRESENCE_FROM, from);
                intent.putExtra(Cont.ON_LINE_STATUAS,1);
                context.sendBroadcast(intent);
            }  else{
            }
        }
    };
}
