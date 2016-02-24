package com.example.knightboy.doudou.listener;

import com.example.knightboy.doudou.service.MsgService;
import com.example.knightboy.doudou.util.ToastUtil;

import org.jivesoftware.smack.ConnectionListener;

/**
 * Created by knightBoy on 2015/8/30.
 */
public class CheckConnectionListener implements ConnectionListener {
    private MsgService context;

    public CheckConnectionListener(MsgService context){
        this.context=context;
    }

    @Override
    public void connectionClosed() {

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        if (e.getMessage().equals("stream:error (conflict)")) {
            ToastUtil.showLongToast(context, "您的账号在异地登录");
        }
    }

    @Override
    public void reconnectingIn(int i) {

    }

    @Override
    public void reconnectionSuccessful() {

    }

    @Override
    public void reconnectionFailed(Exception e) {

    }
}
