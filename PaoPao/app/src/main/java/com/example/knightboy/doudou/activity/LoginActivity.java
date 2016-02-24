package com.example.knightboy.doudou.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.http.AsyncHttpPost;
import com.example.knightboy.doudou.listener.HttpJsonDataListener;
import com.example.knightboy.doudou.util.NetUtil;
import com.example.knightboy.doudou.util.PreferencesUtils;
import com.example.knightboy.doudou.util.ToastUtil;
import com.example.knightboy.doudou.view.KBLoadingDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by knightBoy on 2015/8/23.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    //控件
    private EditText et_account, et_password;
    private TextView tv_register, tv_login;
    //上下文对象
    private Context context;
    //账户，密码字符串
    private String account, password;
    //进度框
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //获得控件及上下文对象
        et_account = (EditText)findViewById(R.id.et_account);
        et_password = (EditText)findViewById(R.id.et_password);
        tv_register = (TextView)findViewById(R.id.tv_register);
        tv_login = (TextView)findViewById(R.id.tv_login);
        context = this;

        //绑定监听器
        tv_register.setOnClickListener(this);
        tv_login.setOnClickListener(this);

        //检查是否记住了用户
        hasRemembered();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_register:
                Intent intent1 = new Intent(context,RegisterActivity.class);
                context.startActivity(intent1);
                LoginActivity.this.finish();
                break;
            case R.id.tv_login:
                if(NetUtil.isNetConnected(context) == true){
                    if(checkEditText() == true){
                        login();
                    }
                }else {
                    ToastUtil.showShortToast(context,"网络异常");
                }
                break;
            default:break;
        }
    }

    /**
     * 检验账号密码框是否合法
     * @return
     */
    private boolean checkEditText(){
        String accountStr = et_account.getText().toString().trim();
        String pwdStr = et_password.getText().toString().trim();
        if(!TextUtils.isEmpty(accountStr) && !TextUtils.isEmpty(pwdStr)){
            account = accountStr;
            password = pwdStr;
            return true;
        }else{
            ToastUtil.showShortToast(context,"账号密码不能为空");
            return false;
        }
    }

    private void login(){
        dialog = KBLoadingDialog.createLoadingDialog(context,"正在登录……");
        dialog.show();
        //发送登录请求
        AsyncHttpPost asyncHttpPost = new AsyncHttpPost(Cont.LOGIN_URL);
        asyncHttpPost.setHttpJsonDataListener(new HttpJsonDataListener(){
            @Override
            public void onDataUpdate(String jsonResult) {
                dialog.dismiss();
                if(jsonResult != null && Integer.parseInt(jsonResult) == 1){
                    //下次不用登陆了
                    PreferencesUtils.putSharePre(context,Cont.IS_REMEMBERED,true);

                    Intent intent = new Intent(context,MainActivity.class);
                    context.startActivity(intent);
                    LoginActivity.this.finish();
                }else {
                    ToastUtil.showShortToast(context,"登录失败");
                }
                super.onDataUpdate(jsonResult);
            }
        });
        //生成请求参数
        Map map = new HashMap<String,String>();
        map.put("userName",account);
        map.put("pwd",password);
        asyncHttpPost.execute(map);
    }

    /**
     * 检验用户是否记住了密码，如果是，直接跳到主界面
     */
    private void hasRemembered(){
        boolean isRemembered = PreferencesUtils.getSharePreBoolean(context,Cont.IS_REMEMBERED);
        if(isRemembered){
            Intent intent = new Intent(context,MainActivity.class);
            context.startActivity(intent);
            LoginActivity.this.finish();
        }
    }
}
