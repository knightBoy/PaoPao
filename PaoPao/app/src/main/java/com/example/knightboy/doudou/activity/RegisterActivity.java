package com.example.knightboy.doudou.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.http.AsyncHttpPost;
import com.example.knightboy.doudou.listener.HttpJsonDataListener;
import com.example.knightboy.doudou.util.NetUtil;
import com.example.knightboy.doudou.util.PreferencesUtils;
import com.example.knightboy.doudou.util.TimeUtil;
import com.example.knightboy.doudou.util.ToastUtil;
import com.example.knightboy.doudou.view.KBLoadingDialog;
import com.example.knightboy.doudou.xmpp.XmppConnectionManager;
import com.example.knightboy.doudou.xmpp.XmppUtil;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by knightBoy on 2015/8/23.
 */
public class RegisterActivity extends Activity implements View.OnClickListener {
    //控件
    private EditText et_userName, et_pwd, et_conf_pwd, et_birthday;
    private TextView tv_boy, tv_girl, tv_register_legal, tv_register;
    //上下文对象
    private Context context;
    //进度框
    private Dialog dialog;
    //表示用户选择性别的字段
    private int userSex = -1;
    //xmpp对象
    private XmppConnectionManager xmppConnectionManager;
    //处理注册结果的Handler对象
    private final Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dialog.dismiss();
            switch (msg.what){
                case 1:
                    PreferencesUtils.putSharePre(context, Cont.IS_REMEMBERED, true);
                    ToastUtil.showShortToast(context, "注册成功");
                    //跳转界面
                    forward(UserInfoActivity.class);
                    RegisterActivity.this.finish();
                    break;
                default:
                    ToastUtil.showShortToast(context,"注册失败");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = this;
        //获得实例
        xmppConnectionManager=XmppConnectionManager.getInstance();

        //获得控件对象
        et_userName = (EditText)findViewById(R.id.et_nicName);
        et_pwd = (EditText)findViewById(R.id.et_psw);
        et_conf_pwd = (EditText)findViewById(R.id.et_confirepsw);
        et_birthday = (EditText)findViewById(R.id.et_age);
        tv_boy = (TextView)findViewById(R.id.tv_boy);
        tv_girl = (TextView)findViewById(R.id.tv_girl);
        tv_register_legal = (TextView)findViewById(R.id.tv_register_legal);
        tv_register = (TextView)findViewById(R.id.tv_register_button);

        //绑定监听器
        tv_boy.setOnClickListener(this);
        tv_girl.setOnClickListener(this);
        tv_register_legal.setOnClickListener(this);
        tv_register.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_boy:
                userSex = 1;
                tv_boy.setBackgroundColor(Color.RED);
                tv_girl.setBackgroundColor(getResources().getColor(R.color.send_button_bg));
                break;
            case R.id.tv_girl:
                userSex = 0;
                tv_girl.setBackgroundColor(Color.RED);
                tv_boy.setBackgroundColor(getResources().getColor(R.color.send_button_bg));
                break;
            case R.id.tv_register_legal:
                break;
            case R.id.tv_register_button:
                if(NetUtil.isNetConnected(context) == true){
                    checkEditText();
                }else{
                    ToastUtil.showShortToast(context,"网络异常");
                }
                break;
        }
    }

    /**
     * 检查用户输入
     * @return
     */
    private boolean checkEditText(){
        String nickName = et_userName.getText().toString().trim();
        String pwd = et_pwd.getText().toString().trim();
        String confPwd = et_conf_pwd.getText().toString().trim();
        String birthday = et_birthday.getText().toString().trim();
        Boolean empty = TextUtils.isEmpty(nickName) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(confPwd) ||TextUtils.isEmpty(birthday);
        if(pwd.equals(confPwd)){
            if(!empty){
                if(userSex == -1){
                    ToastUtil.showShortToast(context,"请选择性别");
                    return false;
                }
                register(nickName,pwd,birthday,userSex);
                return true;
            }
            ToastUtil.showShortToast(context,"内容不能为空");
            return false;
        }
        ToastUtil.showShortToast(context, "两次密码不一致");
        return false;
    }

    /**
     * 注册函数
     * @param nickName
     * @param pwd
     * @param birthday
     * @param sex
     */
    private void register(final String nickName, final String pwd, final String birthday, final int sex){
        dialog = KBLoadingDialog.createLoadingDialog(context, "正在注册……");
        dialog.show();
        //发送注册请求
        AsyncHttpPost asyncHttpPost = new AsyncHttpPost(Cont.REGISTER_URL);
        asyncHttpPost.setHttpJsonDataListener(new HttpJsonDataListener(){
            @Override
            public void onDataUpdate(final String jsonResult) {
                if(jsonResult != null){
                    //保存用户信息
                    PreferencesUtils.putSharePre(context,Cont.USER_ID,Integer.parseInt(jsonResult));   //保存为字符串类型
                    PreferencesUtils.putSharePre(context,Cont.USER_NAME,nickName);
                    PreferencesUtils.putSharePre(context,Cont.PASSWORD,pwd);
                    PreferencesUtils.putSharePre(context, Cont.BIRTHDAY, birthday);
                    PreferencesUtils.putSharePre(context, Cont.USER_SEX, sex);
                    PreferencesUtils.putSharePre(context,Cont.REGISTER_TIME, TimeUtil.getformatNow());

                    //openfire服务器注册
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            XMPPConnection mXMPPConnection=xmppConnectionManager.init();
                            try {
                                mXMPPConnection.connect();
                                int result= XmppUtil.register(mXMPPConnection, jsonResult, pwd);
                                mhandler.sendEmptyMessage(result);
                            } catch (XMPPException e) {
                                e.printStackTrace();
                                mhandler.sendEmptyMessage(4);
                            }
                        }
                    }).start();

                }else {
                    ToastUtil.showShortToast(context,"注册失败");
                }
                super.onDataUpdate(jsonResult);
            }
        });
        //生成请求参数
        Map map = new HashMap<String,String>();
        map.put("userName",nickName);
        map.put("pwd",pwd);
        map.put("birth",birthday);
        map.put("sex",String.valueOf(sex));
        asyncHttpPost.execute(map);
    }

    private void forward(Class<?> T){
        Intent intent = new Intent(context,T);
        context.startActivity(intent);
    }
}
