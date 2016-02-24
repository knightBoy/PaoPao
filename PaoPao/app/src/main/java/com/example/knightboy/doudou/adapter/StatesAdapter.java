package com.example.knightboy.doudou.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.activity.ChatActivity;
import com.example.knightboy.doudou.activity.CommentActivity;
import com.example.knightboy.doudou.application.CustomImageOption;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.bean.StateBean;
import com.example.knightboy.doudou.util.TimeUtil;
import com.example.knightboy.doudou.view.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import static android.graphics.Color.RED;

/**
 * Created by knightBoy on 2015/8/25.
 */
public class StatesAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<StateBean> stateBeans;
    private Context context;
    private XListView xListView;

    public StatesAdapter(Context aContext, ArrayList<StateBean> aStateBeans){
        this.stateBeans = aStateBeans;
        this.inflater = LayoutInflater.from(aContext);
        this.context = aContext;
    }

    public void setxListView(XListView xListView) {
        this.xListView = xListView;
    }

    @Override
    public int getCount() {
        return stateBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView=this.inflater.inflate(R.layout.fragment_state_listitem,null);
            //获得控件
            viewHolder.iv_userPhoto = (ImageView)convertView.findViewById(R.id.iv_user_photo);
            viewHolder.tv_userName = (TextView)convertView.findViewById(R.id.tv_user_name);
            viewHolder.iv_userSex = (ImageView)convertView.findViewById(R.id.iv_user_sex);
            viewHolder.tv_userLocation = (TextView)convertView.findViewById(R.id.tv_user_location);
            viewHolder.tv_userTime = (TextView)convertView.findViewById(R.id.tv_user_time);
            viewHolder.tv_content = (TextView)convertView.findViewById(R.id.tv_user_state);
            viewHolder.iv_picture = (ImageView)convertView.findViewById(R.id.iv_picture);
            viewHolder.tv_goodNum = (TextView)convertView.findViewById(R.id.tv_goodNum);
            //获得操作布局
            viewHolder.rl_sendMessage = (RelativeLayout)convertView.findViewById(R.id.rl_sendMessage);
            viewHolder.rl_reply = (RelativeLayout)convertView.findViewById(R.id.rl_reply);
            viewHolder.rl_goodOne = (RelativeLayout)convertView.findViewById(R.id.rl_goodOne);
            viewHolder.iv_icon_good = (ImageView)convertView.findViewById(R.id.iv_icon_good);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        //获得各项数据
        final StateBean state = stateBeans.get(position);
        final int id = state.getUserId();
        String photoUrl = state.getAvatarUrl();       //用户头像
        final String name = state.getUserName();
        int userSex = state.getSex();
        String userLocation = state.getPosition();
        String userTime = TimeUtil.getDistanceTime(state.getTime());  //发表时间
        final String content = state.getContent();
        String pictureUrl = state.getPictureUrl();    //发表图片
        int goodNum = state.getGoodNum();

        //设置数据
        //获得ImageLoader实例
        ImageLoader imageLoader = ImageLoader.getInstance();
        viewHolder.iv_userPhoto.setImageResource(R.drawable.user);
        viewHolder.tv_userName.setText(name);
        if(userSex == Cont.SEX_BOY){
            viewHolder.iv_userSex.setImageResource(R.drawable.icon_boy);
        }else{
            viewHolder.iv_userSex.setImageResource(R.drawable.icon_girl);
        }
        viewHolder.tv_userLocation.setText(userLocation);
        viewHolder.tv_userTime.setText(userTime);
        viewHolder.tv_content.setText(content);
        //设置动态图片
        imageLoader.displayImage(pictureUrl,viewHolder.iv_picture,CustomImageOption.getOptions());
        viewHolder.tv_goodNum.setText(goodNum+"");

        //设置监听器
        //评论回复
        viewHolder.rl_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(context, CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("state",state);
                intent2.putExtra("state",bundle);
                context.startActivity(intent2);
            }
        });
        //发送消息
        viewHolder.rl_sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 =new Intent(context, ChatActivity.class);
                intent1.putExtra("userName",name);
                intent1.putExtra("userId",String.valueOf(id));
                context.startActivity(intent1);
            }
        });

        viewHolder.rl_goodOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.iv_icon_good.setImageResource(R.drawable.icon_good_over);
            }
        });

        return convertView;
    }

    private final class ViewHolder {
        ImageView iv_userPhoto;
        TextView tv_userName;
        ImageView iv_userSex;
        TextView tv_userLocation;
        TextView tv_userTime;
        TextView tv_content;
        ImageView iv_picture;
        TextView tv_goodNum;
        RelativeLayout rl_sendMessage;
        RelativeLayout rl_reply;
        RelativeLayout rl_goodOne;
        ImageView iv_icon_good;
    }
}
