package com.example.knightboy.doudou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.bean.Comment;

import java.util.ArrayList;

/**
 * Created by knightBoy on 2015/9/2.
 */
public class CommentAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Comment> comments;
    private Context context;

    public CommentAdapter(Context aContext, ArrayList<Comment> aComments){
        this.comments = aComments;
        this.inflater = LayoutInflater.from(aContext);
        this.context = aContext;
    }

    @Override
    public int getCount() {
        return comments.size();
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
        if(convertView == null){
            convertView = this.inflater.inflate(R.layout.activity_comment_listitem,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_user_photo = (ImageView)convertView.findViewById(R.id.iv_user_photo);
            viewHolder.tv_user_name = (TextView)convertView.findViewById(R.id.tv_user_name);
            viewHolder.tv_comment_content = (TextView)convertView.findViewById(R.id.tv_comment_content);
            viewHolder.tv_serialNum = (TextView)convertView.findViewById(R.id.tv_serialNum);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        //获取数据
        Comment comment = comments.get(position);
        viewHolder.iv_user_photo.setImageResource(R.drawable.user);
        viewHolder.tv_user_name.setText("胡伟龙");
        viewHolder.tv_comment_content.setText(comment.getContents());
        viewHolder.tv_serialNum.setText((position+1) + "");

        return convertView;
    }

    private final class ViewHolder{
        ImageView iv_user_photo;
        TextView tv_user_name;
        TextView tv_comment_content;
        TextView tv_serialNum;
    }
}
