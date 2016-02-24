package com.example.knightboy.doudou.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.util.ImageUtil;
import com.example.knightboy.doudou.util.PreferencesUtils;
import com.example.knightboy.doudou.util.ToastUtil;
import com.example.knightboy.doudou.util.UploadUtil;
import com.example.knightboy.doudou.util.UriUtil;
import com.example.knightboy.doudou.view.KBLoadingDialog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by knightBoy on 2015/8/26.
 */
public class EditStateActivity extends Activity implements View.OnClickListener,UploadUtil.OnUploadProcessListener {
    private EditText et_state;
    private ImageView iv_statePictrue;
    private LinearLayout ll_pickPhoto,ll_takePhoto,ll_sendState;

    //使用照相机拍照获取图片
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    //使用相册中的图片
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
    //相册获取到的图片路径
    private String picPath;
    //拍照保存图片的位置
    private String takePhotoPath;
    //图片的Uri
    private Uri photoUri;
    private File imageFile;
    //上下文对象
    private Context context;
    //发表进度对话框
    private Dialog loadingDialog;
    //数据上传返回码
    private static final int UPLOAD_SUCCESS_CODE = 1;
    private static final int UPLOAD_FILE_NOT_EXISTS_CODE = 2;
    private static final int UPLOAD_SERVER_ERROR_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editstate);
        context = this;
        loadingDialog = KBLoadingDialog.createLoadingDialog(context,"正在发表……");
        findView();
    }

    //处理上传状态的Handler类
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int Type = msg.what;
            if(Type == UPLOAD_SUCCESS_CODE){
                ToastUtil.showShortToast(context,"发表成功");
                setResult(0);
                EditStateActivity.this.finish();
            }else if(Type == UPLOAD_FILE_NOT_EXISTS_CODE){
                ToastUtil.showShortToast(context,"图片不存在");
            }else if(Type == UPLOAD_SERVER_ERROR_CODE){
                ToastUtil.showShortToast(context,"服务器异常");
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 获得控件引用
     */
    private void findView(){
        et_state = (EditText)findViewById(R.id.et_state_topunish);
        iv_statePictrue = (ImageView)findViewById(R.id.iv_statepicture);
        ll_pickPhoto = (LinearLayout)findViewById(R.id.ll_pickphoto);
        ll_takePhoto = (LinearLayout)findViewById(R.id.ll_takephoto);
        ll_sendState = (LinearLayout)findViewById(R.id.ll_state_send);
        //绑定监听器
        ll_pickPhoto.setOnClickListener(this);
        ll_takePhoto.setOnClickListener(this);
        ll_sendState.setOnClickListener(this);
    }

    /**
     * 点击事件处理
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //相册中取出照片
            case R.id.ll_pickphoto:
                pickPhoto();
                break;
            //拍照得到照片
            case R.id.ll_takephoto:
                takePhoto();
                break;
            //发表动态
            case R.id.ll_state_send:
                sendState();
                break;
            default:
                break;
        }
    }

    /**
     * 从相册中取得图片
     */
    private void pickPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }

    /**
     * 拍照取得图片
     */
    private void takePhoto(){
        //执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if(SDState.equals(Environment.MEDIA_MOUNTED))
        {   //设置图片的保存路径
            takePhotoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.jpg";
            imageFile = new File(takePhotoPath);       //通过路径创建保存文件
            Uri imageFileUri = Uri.fromFile(imageFile);    //获取文件的Uri
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);//告诉相机拍摄完毕输出图片到指定的Uri
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        }else{
            Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 发表动态
     */
    private void sendState(){
        //检查文字图片是否合法
        if(!checkEditText()){
            ToastUtil.showShortToast(context,"不能少于五个字");
            return;
        }else if(picPath == null){
            ToastUtil.showShortToast(context,"请选择图片");
            return;
        }else{
            String fileKey = "uploadedfile";
            UploadUtil uploadUtil = UploadUtil.getInstance();
            uploadUtil.setOnUploadProcessListener(this);  //设置监听器监听上传状态
            //获得上传参数,有些参数之前就存在SharedPreferences中了
            int userId = PreferencesUtils.getSharePreInt(context, Cont.USER_ID);
            String avatarUrl = PreferencesUtils.getSharePreStr(context, Cont.AVATAR_URL);
            String userName = PreferencesUtils.getSharePreStr(context,Cont.USER_NAME);
            int sex = PreferencesUtils.getSharePreInt(context, Cont.USER_SEX);
            String position = PreferencesUtils.getSharePreStr(context, Cont.USER_POSITION);
            String content = et_state.getText().toString();
            //上传参数设置
            Map<String, String> params = new HashMap<>();
            params.put("userID", String.valueOf(userId));
            params.put("avatarUrl", avatarUrl);
            params.put("userName", userName);
            params.put("sex", String.valueOf(sex));
            params.put("position", position);
            params.put("content", content);
            //开始发送
            uploadUtil.uploadFile(picPath, fileKey, Cont.EDIT_STATE_REQUEST_URL, params);
            loadingDialog.show();
        }
    }

    private boolean checkEditText(){
        String text = et_state.getText().toString();
        if(text.length() < 5){
            return false;
        }
        return true;
    }

    /**
     * 处理选择图片的返回结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_PIC_BY_PICK_PHOTO){
                photoUri = data.getData();
                picPath = UriUtil.getImageAbsolutePath(context, photoUri);
                Bitmap bm = ImageUtil.decodeBitmap(picPath);
                iv_statePictrue.setImageBitmap(bm);
            }else if(requestCode == SELECT_PIC_BY_TACK_PHOTO){
                picPath = takePhotoPath;
                Bitmap bm = ImageUtil.decodeBitmap(picPath);
                iv_statePictrue.setImageBitmap(bm);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onUploadDone(int responseCode, String message) {
        loadingDialog.dismiss();
        Message msg = Message.obtain();
        msg.what = responseCode;
        handler.sendMessage(msg);
        if(imageFile != null){
            imageFile.delete();
        }
    }

    @Override
    public void onUploadProcess(int uploadSize) {

    }

    @Override
    public void initUpload(int fileSize) {

    }
}
