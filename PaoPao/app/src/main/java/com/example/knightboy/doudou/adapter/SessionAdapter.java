package com.example.knightboy.doudou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.bean.Session;
import com.example.knightboy.doudou.util.TimeUtil;

import java.util.List;

/**
 * Created by knightBoy on 2015/9/1.
 */
public class SessionAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Session> sessions;
    private Context context;

    public SessionAdapter(Context aContext, List<Session> aSessions){
        this.sessions = aSessions;
        this.inflater = LayoutInflater.from(aContext);
        this.context = aContext;
    }

    @Override
    public int getCount() {
        return sessions.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = this.inflater.inflate(R.layout.fragment_message_item,null);
            viewHolder = new ViewHolder();
            //获得控件
            viewHolder.iv_user_avatar = (ImageView)convertView.findViewById(R.id.iv_user_avatar);
            viewHolder.tv_user_name = (TextView)convertView.findViewById(R.id.tv_user_name);
            viewHolder.tv_new_message = (TextView)convertView.findViewById(R.id.tv_latest_message);
            viewHolder.tv_session_time = (TextView)convertView.findViewById(R.id.tv_session_time);
            viewHolder.tv_msg_num = (TextView)convertView.findViewById(R.id.tv_msg_num);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        //获得数据
        Session session = sessions.get(position);
        String userId = session.getFrom();
        String userName = session.getFrom_user();
        String newMsg = session.getContent();
        String time = session.getTime();
        String msgNum = session.getNotReadCount();

        //设置数据
        viewHolder.tv_user_name.setText(userName);
        viewHolder.tv_new_message.setText(newMsg);
        viewHolder.tv_session_time.setText(TimeUtil.getSessionTime(time));
        if(Integer.parseInt(msgNum) != 0){
            viewHolder.tv_msg_num.setText(msgNum);
        }else {
            viewHolder.tv_msg_num.setVisibility(View.GONE);
        }

        //用户头像设置另外
        viewHolder.iv_user_avatar.setImageResource(R.drawable.user);

        return convertView;
    }

    private final class ViewHolder{
        ImageView iv_user_avatar;
        TextView tv_user_name;
        TextView tv_new_message;
        TextView tv_session_time;
        TextView tv_msg_num;
    }
}
