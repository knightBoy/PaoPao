package com.example.knightboy.doudou.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.adapter.CommentAdapter;
import com.example.knightboy.doudou.application.CustomImageOption;
import com.example.knightboy.doudou.bean.Comment;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.bean.StateBean;
import com.example.knightboy.doudou.http.AsyncHttpGet;
import com.example.knightboy.doudou.http.AsyncHttpPost;
import com.example.knightboy.doudou.json.CommentJson;
import com.example.knightboy.doudou.listener.HttpJsonDataListener;
import com.example.knightboy.doudou.util.PreferencesUtils;
import com.example.knightboy.doudou.util.TimeUtil;
import com.example.knightboy.doudou.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by knightBoy on 2015/8/28.
 */
public class CommentActivity extends Activity implements View.OnClickListener {
    private ImageView iv_comment_back, iv_user_photo, iv_user_sex, iv_picture;
    private TextView tv_user_name, tv_user_location, tv_user_time, tv_user_state, tv_no_comment;
    private ListView comment_lv;
    private ImageView iv_face;
    private EditText et_comment;
    private TextView tv_send_comment;

    //动态Id
    private int serialNum;
    //评论列表
    private ArrayList<Comment> comments = new ArrayList<>();
    //适配器
    private CommentAdapter commentAdapter;

    //上下文对象
    private Context context;
    //软键盘控制
    private InputMethodManager inputMethodManager;
    //intent传递的动态
    private StateBean stateBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        context = this;
        //获得软键盘管理服务
        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //获得控件引用
        findView();
        //获得传递过来的动态
        Bundle bundle = getIntent().getBundleExtra("state");
        stateBean = (StateBean) bundle.getSerializable("state");
        //显示动态
        initState(stateBean);
        //加载评论
        initComments();
    }

    private void findView(){
        iv_comment_back = (ImageView)findViewById(R.id.iv_comment_back);
        iv_user_photo = (ImageView)findViewById(R.id.iv_user_photo);
        iv_user_sex = (ImageView)findViewById(R.id.iv_user_sex);
        iv_picture = (ImageView)findViewById(R.id.iv_picture);
        tv_user_name = (TextView)findViewById(R.id.tv_user_name);
        tv_user_location = (TextView)findViewById(R.id.tv_user_location);
        tv_user_time = (TextView)findViewById(R.id.tv_user_time);
        tv_user_state = (TextView)findViewById(R.id.tv_user_state);
        comment_lv = (ListView)findViewById(R.id.comment_lv);
        iv_face = (ImageView)findViewById(R.id.iv_comment_emoji);
        et_comment = (EditText)findViewById(R.id.et_user_comment);
        tv_send_comment = (TextView)findViewById(R.id.tv_send_comment);
        tv_no_comment = (TextView)findViewById(R.id.tv_no_comment);

        //添加监听器
        iv_comment_back.setOnClickListener(this);
        tv_send_comment.setOnClickListener(this);
    }

    //生成动态
    private void initState(StateBean state){
        String avatarUrl = state.getAvatarUrl();
        String userName = state.getUserName();
        int sex = state.getSex();
        String position = state.getPosition();
        String time = state.getTime();
        String content = state.getContent();
        String pictureUrl = state.getPictureUrl();
        serialNum = state.getSerialNum();
        //设置数据
        iv_user_photo.setImageResource(R.drawable.user);
        tv_user_name.setText(userName);
        if(sex == Cont.SEX_BOY){
            iv_user_sex.setImageResource(R.drawable.icon_boy);
        }else{
            iv_user_sex.setImageResource(R.drawable.icon_girl);
        }
        tv_user_location.setText(position);
        tv_user_time.setText(TimeUtil.getDistanceTime(time));
        tv_user_state.setText(content);
        ImageLoader.getInstance().displayImage(pictureUrl, iv_picture, CustomImageOption.getOptions());
    }

    /**
     * 加载评论
     */
    private void initComments(){
        AsyncHttpGet asyncHttpGet = new AsyncHttpGet();
        asyncHttpGet.setHttpJsonDataListener(new HttpJsonDataListener() {
            @Override
            public void onDataUpdate(String jsonResult) {
                super.onDataUpdate(jsonResult);
                if(jsonResult != null){
                    String result = jsonResult.substring(4,jsonResult.length());
                    comments = CommentJson.jsonToComments(result);
                    commentAdapter = new CommentAdapter(context, comments);
                    comment_lv.setAdapter(commentAdapter);
                    hiddenSofa();
                }
            }
        });
        String url = Cont.GET_COMMENTS_URL + "?serialNum=" + serialNum;
        asyncHttpGet.execute(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_chat_back:
                CommentActivity.this.finish();
                break;
            case R.id.tv_send_comment:
                String commentStr = et_comment.getText().toString();
                if(TextUtils.isEmpty(commentStr)){
                    ToastUtil.showShortToast(context,"评论不能为空");
                }else {
                    doComment(commentStr);
                    inputMethodManager.hideSoftInputFromWindow(CommentActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    et_comment.setText(null);
                }
                break;
        }
    }

    /**
     * 发表评论
     * @param comment
     */
    private void doComment(String comment){
        Map<String,String> map = new HashMap<>();
        map.put("serialNum",String.valueOf(serialNum));
        map.put("userID",String.valueOf(PreferencesUtils.getSharePreInt(context,Cont.USER_ID)));
        map.put("contents", comment);

        //发送请求
        AsyncHttpPost asyncHttpPost = new AsyncHttpPost(Cont.EDIT_COMMENTS_URL);
        asyncHttpPost.execute(map);

        //更新视图
        Comment comment1 = new Comment();
        comment1.setUserId(PreferencesUtils.getSharePreInt(context,Cont.USER_ID));
        comment1.setContents(comment);
        comments.add(comment1);
        commentAdapter.notifyDataSetChanged();
        comment_lv.setSelection(comments.size());

        //隐藏抢沙发条目
        hiddenSofa();
    }

    /**
     * 控制抢沙发的出现
     */
    private void hiddenSofa(){
        if(comments.size() == 0){
            tv_no_comment.setVisibility(View.VISIBLE);
        }else {
            tv_no_comment.setVisibility(View.GONE);
        }
    }
}
