package com.example.knightboy.doudou.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.application.CustomImageOption;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.http.AsyncHttpGet;
import com.example.knightboy.doudou.http.AsyncHttpPost;
import com.example.knightboy.doudou.listener.HttpJsonDataListener;
import com.example.knightboy.doudou.util.PreferencesUtils;
import com.example.knightboy.doudou.util.TimeUtil;
import com.example.knightboy.doudou.util.ToastUtil;
import com.example.knightboy.doudou.util.UploadUtil;
import com.example.knightboy.doudou.util.UriUtil;
import com.example.knightboy.doudou.view.CircleImageView;
import com.example.knightboy.doudou.view.KBLoadingDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by knightBoy on 2015/8/29.
 */
public class UserInfoActivity extends Activity implements View.OnClickListener,UploadUtil.OnUploadProcessListener{
    CircleImageView user_photo;
    ImageView iv_userSex,iv_userInfo_back, iv_more_info,iv_update_signe;
    TextView tv_userName, tv_age, tv_userSex, tv_userId, tv_birthday, tv_register_time, tv_save;
    TextView et_userSigne, et_userPosition;

    private Context context;
    private int userId;

    private final int PHOTO_REQUEST_GALLERY = 1;  // 从相册中选择
    private final int PHOTO_REQUEST_CAMERA = 2;      // 拍照获取
    private final int PHOTO_REQUEST_CUT = 3;      // 剪切结果
    //头像处理结果
    private String photoPath;
    private boolean photoHasChanged = false;
    private File photoFile;

    //签名部分
    private AlertDialog selfdialog;
    private String newSigne;

    //是否修改的标志值
    private boolean infoHasChanged = false;
    //保存进度框
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        context = this;
        //获得控件
        findView();
        //获得传递的userId，判断是否查看的是本机用户
        userId = getIntent().getIntExtra("userId",0);
        if(userId == PreferencesUtils.getSharePreInt(context,Cont.USER_ID)){
            initMyselfData();
        }else {
            tv_save.setVisibility(View.GONE);
            iv_more_info.setVisibility(View.VISIBLE);
            iv_update_signe.setVisibility(View.GONE);
            initOthersData(userId);
        }

    }

    private void findView(){
        user_photo = (CircleImageView)findViewById(R.id.civ_user_photo);
        iv_userSex = (ImageView)findViewById(R.id.iv_user_sex);
        iv_userInfo_back = (ImageView)findViewById(R.id.iv_userinfo_back);
        iv_userInfo_back.setOnClickListener(this);
        iv_more_info = (ImageView)findViewById(R.id.iv_more_info);
        iv_more_info.setOnClickListener(this);
        tv_userName = (TextView)findViewById(R.id.tv_user_name);
        tv_age = (TextView)findViewById(R.id.tv_age);
        tv_userSex = (TextView)findViewById(R.id.tv_user_sex);
        tv_userId = (TextView)findViewById(R.id.tv_user_id);
        tv_birthday = (TextView)findViewById(R.id.tv_user_birthday);
        tv_register_time = (TextView)findViewById(R.id.tv_user_register_time);
        tv_save = (TextView)findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);
        et_userPosition = (TextView)findViewById(R.id.et_user_position);
        et_userSigne = (TextView)findViewById(R.id.et_user_signe);
        iv_update_signe = (ImageView)findViewById(R.id.iv_update_info);
        iv_update_signe.setOnClickListener(this);
    }

    /**
     * 初始化用户数据,本用户
     */
    private void initMyselfData(){
        tv_userName.setText(PreferencesUtils.getSharePreStr(context, Cont.USER_NAME));
        if(PreferencesUtils.getSharePreInt(context,Cont.USER_SEX) == Cont.SEX_BOY){
            iv_userSex.setImageResource(R.drawable.icon_boy);
            tv_userSex.setText("男");
        }else{
            iv_userSex.setImageResource(R.drawable.icon_girl);
            tv_userSex.setText("女");
        }
        tv_userId.setText(String.valueOf(PreferencesUtils.getSharePreInt(context,Cont.USER_ID)));
        //设置生日，年龄
        String birthday = PreferencesUtils.getSharePreStr(context, Cont.BIRTHDAY);
        tv_age.setText(TimeUtil.getAge(birthday) + "岁");
        tv_birthday.setText(TimeUtil.getformatBirthday(birthday));
        tv_register_time.setText(PreferencesUtils.getSharePreStr(context, Cont.REGISTER_TIME));
        //设置个人签名及地址
        String signe = PreferencesUtils.getSharePreStr(context,Cont.SIGNE);
        if(signe.equals("")){
            et_userSigne.setText("你还没有签名");
        }else {
            et_userSigne.setText(signe);
        }
        et_userPosition.setText(PreferencesUtils.getSharePreStr(context, Cont.USER_POSITION));

        //设置点击上传头像监听器
        user_photo.setOnClickListener(this);
        //请求用户头像
        AsyncHttpGet asyncHttpGet = new AsyncHttpGet();
        asyncHttpGet.setHttpJsonDataListener(new HttpJsonDataListener() {
            @Override
            public void onDataUpdate(String jsonResult) {
                super.onDataUpdate(jsonResult);
                if (jsonResult != null) {
                    String[] infos = jsonResult.split(Cont.USER_SPLIT);
                    Log.e("info",jsonResult);
                    ImageLoader.getInstance().displayImage(infos[6],user_photo, CustomImageOption.getUserOptions());
                }
            }
        });
        String url = Cont.GET_USER_INFO_URL + "?userID=" + userId;
        asyncHttpGet.execute(url);
    }

    /**
     * 加载别人的数据
     * @param userId
     */
    private void initOthersData(int userId){
        AsyncHttpGet asyncHttpGet = new AsyncHttpGet();
        asyncHttpGet.setHttpJsonDataListener(new HttpJsonDataListener() {
            @Override
            public void onDataUpdate(String jsonResult) {
                super.onDataUpdate(jsonResult);
                if (jsonResult != null) {
                    displayOthersData(jsonResult);
                } else {
                    ToastUtil.showShortToast(context, "获取用户信息失败");
                }
            }
        });
        String url = Cont.GET_USER_INFO_URL + "?userID=" + userId;
        asyncHttpGet.execute(url);
    }

    /**
     * 显示用户信息到界面
     * @param userInfo
     */
    private void displayOthersData(String userInfo){
        String[] infos = userInfo.split(Cont.USER_SPLIT);
        String userId = infos[1];
        String userName = infos[2];
        int sex = Integer.parseInt(infos[3]);
        String birthday = infos[4];
        String registerTime = infos[5];
        String avatar = infos[6];
        String signe = infos[7];
        //在界面上显示
        tv_userName.setText(userName);
        if(sex == Cont.SEX_BOY){
            iv_userSex.setImageResource(R.drawable.icon_boy);
            tv_userSex.setText("男");
        }else{
            iv_userSex.setImageResource(R.drawable.icon_girl);
            tv_userSex.setText("女");
        }
        tv_userId.setText(userId);
        //设置生日，年龄
        tv_age.setText(TimeUtil.getAge(birthday) + "岁");
        tv_birthday.setText(birthday);
        tv_register_time.setText(registerTime);
        //设置个人签名及地址
        if(signe.equals("")){
            et_userSigne.setText("还没有签名");
        }else {
            et_userSigne.setText(signe);
        }
        et_userPosition.setText(PreferencesUtils.getSharePreStr(context, Cont.USER_POSITION));  //**********

        //设置点击上传头像监听器
        user_photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.civ_user_photo:
                ShowPickDialog();
                break;
            case R.id.tv_save:
                savaUserInfo();
                break;
            case R.id.iv_more_info:
                break;
            case R.id.iv_userinfo_back:
                UserInfoActivity.this.finish();
                break;
            case R.id.iv_update_info:
                updateSigne();
                break;
        }
    }

    /**
     * 选择提示对话框
     */
    private void ShowPickDialog() {
        new AlertDialog.Builder(this)
                .setTitle("设置头像...")
                .setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                })
                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //下面这句指定调用相机拍照后的照片存储的路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), "avatar.png")));
                        startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 如果是直接从相册获取
            case PHOTO_REQUEST_GALLERY:
                startPhotoZoom(data.getData());
                break;
            // 如果是调用相机拍照时
            case PHOTO_REQUEST_CAMERA:
                photoFile = new File(Environment.getExternalStorageDirectory() + "/avatar.png");
                photoPath = Environment.getExternalStorageDirectory() + "/avatar.png";
                startPhotoZoom(Uri.fromFile(photoFile));
                break;
            // 取得裁剪后的图片
            case PHOTO_REQUEST_CUT:
                //非空判断大家一定要验证，如果不验证的话，在剪裁之后如果发现不满意，要重新裁剪，丢弃当前功能时，会报NullException
                if(data != null){
                    setPicToView(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 保存裁剪之后的图片数据
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            user_photo.setImageBitmap(photo);
            photoHasChanged = true;
            infoHasChanged = true;
            //将bitmap保存为文件
            if(photoFile == null){
                photoFile = new File(Environment.getExternalStorageDirectory() + "/avatar.png");
                photoPath = Environment.getExternalStorageDirectory() + "/avatar.png";
                FileOutputStream fos = null;
                try {
                    photoFile.createNewFile();
                    fos = new FileOutputStream(photoFile);
                    photo.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 提交保存用户信息,提交后记得删除图片
     */
    private void savaUserInfo(){
        if(infoHasChanged == false){
            ToastUtil.showShortToast(context,"你没有修改信息");
            return;
        }
        loadingDialog = KBLoadingDialog.createLoadingDialog(context,"正在保存……");
        infoHasChanged = false;
        if(photoHasChanged == true){
            photoHasChanged = false;
            String fileKey = "uploadedfile";
            UploadUtil uploadUtil = UploadUtil.getInstance();
            uploadUtil.setOnUploadProcessListener(this);  //设置监听器监听上传状态
            //上传参数设置
            Map<String, String> params = new HashMap<>();
            params.put("signe",newSigne);
            params.put("userID", String.valueOf(userId));
            //开始发送
            uploadUtil.uploadFile(photoPath, fileKey, Cont.SET_USER_INFO_URL, params);
            loadingDialog.show();
            return;
        }else {
            AsyncHttpPost asyncHttpPost = new AsyncHttpPost(Cont.SET_USER_INFO_URL);
            asyncHttpPost.setHttpJsonDataListener(new HttpJsonDataListener(){
                @Override
                public void onDataUpdate(String jsonResult) {
                    super.onDataUpdate(jsonResult);
                    ToastUtil.showShortToast(context,"保存成功");
                }
            });
            //上传参数设置
            Map<String, String> params = new HashMap<>();
            params.put("signe", newSigne);
            params.put("userID",String.valueOf(userId));
            asyncHttpPost.execute(params);
        }
    }

    /**
     * 修改个性签名
     */
    private void updateSigne(){
        //创建view
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_signe_dialog, null);

        final EditText et_signe = (EditText)view.findViewById(R.id.et_signe);
        AlertDialog.Builder ad =new AlertDialog.Builder(context);
        ad.setView(view);
        ad.setTitle("修改签名");
        selfdialog =ad.create();
        selfdialog.setButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获取输入框的签名
                newSigne = et_signe.getText().toString();
                //设置已修改标志
                infoHasChanged = true;
                mhandler.sendEmptyMessage(5);
            }
        });
        selfdialog.setButton2("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                selfdialog.cancel();
            }
        });
        selfdialog.show();
    }

    /**
     * 更新修改的个性签名
     */
    Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 5:
                    et_userSigne.setText(newSigne);
                    break;
                case 1:
                    ToastUtil.showShortToast(context,"保存成功");
                    break;
            }
        }
    };

    @Override
    public void onUploadDone(int responseCode, String message) {
        loadingDialog.dismiss();
        mhandler.sendEmptyMessage(responseCode);
        //上传图片，成功后删除图片
        if (photoFile != null){
            photoFile.delete();
        }
    }

    @Override
    public void onUploadProcess(int uploadSize) {

    }

    @Override
    public void initUpload(int fileSize) {

    }
}
