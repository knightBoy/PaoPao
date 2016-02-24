package com.example.knightboy.doudou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.bean.BrainBean;

import java.util.ArrayList;

/**
 * Created by knightBoy on 2015/8/25.
 */
public class BrainAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<BrainBean> brainBeans;
    private Context context;

    public BrainAdapter(Context aContext, ArrayList<BrainBean> aBrainBeans){
        this.brainBeans = aBrainBeans;
        this.inflater = LayoutInflater.from(aContext);
        this.context = aContext;
    }

    @Override
    public int getCount() {
        return brainBeans.size();
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
            convertView=this.inflater.inflate(R.layout.fragment_brain_listitem,null);
            viewHolder = new ViewHolder();
            //获取控件
            viewHolder.tv_userName = (TextView)convertView.findViewById(R.id.tv_user_name);
            viewHolder.tv_brainType = (TextView)convertView.findViewById(R.id.tv_brain_type);
            viewHolder.tv_userBrain = (TextView)convertView.findViewById(R.id.tv_user_brain);
            viewHolder.tv_goodNum = (TextView)convertView.findViewById(R.id.tv_goodNum);
            //获取操作区
            viewHolder.rl_sendMessage = (RelativeLayout)convertView.findViewById(R.id.rl_brain_sendMessage);
            viewHolder.rl_reply = (RelativeLayout)convertView.findViewById(R.id.rl_brain_reply);
            viewHolder. rl_goodOne = (RelativeLayout)convertView.findViewById(R.id.rl_brain_goodOne);
            //获取背景区
            viewHolder.ll_brain_bg = (LinearLayout)convertView.findViewById(R.id.ll_brain_bg);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        //获取数据
        BrainBean brainBean = brainBeans.get(position);
        String userName = brainBean.getUserName();
        String brainType = brainBean.getType();
        String userBrain = brainBean.getContent();
        int goodNum = brainBean.getGoodNum();
        int bgDrawable = brainBean.getBgDrawableId();
        //设置数据
        viewHolder.tv_userName.setText(userName);
        viewHolder.tv_brainType.setText(brainType);
        viewHolder.tv_userBrain.setText(userBrain);
        viewHolder.tv_goodNum.setText(goodNum+"");
        viewHolder.ll_brain_bg.setBackgroundResource(bgDrawable);

        //设置监听器

        return convertView;
    }

    private final class ViewHolder{
        TextView tv_userName;
        TextView tv_brainType;
        TextView tv_userBrain;
        TextView tv_goodNum;
        //获取操作区
        RelativeLayout rl_sendMessage;
        RelativeLayout rl_reply;
        RelativeLayout rl_goodOne;
        //获取背景区
        LinearLayout ll_brain_bg;
    }
}
