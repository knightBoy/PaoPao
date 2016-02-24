package com.example.knightboy.doudou.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.application.CustomImageOption;
import com.example.knightboy.doudou.bean.Cont;
import com.example.knightboy.doudou.bean.Msg;
import com.example.knightboy.doudou.http.AsyncHttpGet;
import com.example.knightboy.doudou.listener.HttpJsonDataListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by knightBoy on 2015/8/31.
 */
public class ChatAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Msg> msgs;
    private Context context;

    public ChatAdapter(Context aContext, List<Msg> aMsgs){
        this.msgs = aMsgs;
        this.inflater = LayoutInflater.from(aContext);
        this.context = aContext;
    }

    @Override
    public int getCount() {
        return msgs.size();
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
        Msg aMsg = msgs.get(position);
        if(aMsg.getIsComing() == 0){    //是接收的消息
            convertView = this.inflater.inflate(R.layout.activity_chat_you_item,null);
        }
        else {                          //发送的消息
            convertView = this.inflater.inflate(R.layout.activity_chat_me_item,null);
        }
        //获取控件
        TextView tv_chat_time = (TextView)convertView.findViewById(R.id.chat_time);
        final ImageView userPhoto = (ImageView)convertView.findViewById(R.id.chat_icon);
        TextView content = (TextView)convertView.findViewById(R.id.chat_content);
        content.setText(aMsg.getContent());
        if(position % 10 == 0){
            tv_chat_time.setText(aMsg.getDate());
        }
        //设置头像
        String userId = aMsg.getFromUser();
        AsyncHttpGet asyncHttpGet = new AsyncHttpGet();
        asyncHttpGet.setHttpJsonDataListener(new HttpJsonDataListener() {
            @Override
            public void onDataUpdate(String jsonResult) {
                super.onDataUpdate(jsonResult);
                if (jsonResult != null) {
                    String[] infos = jsonResult.split(Cont.USER_SPLIT);
                    ImageLoader.getInstance().displayImage(infos[6],userPhoto, CustomImageOption.getUserOptions());
                }
            }
        });
        String url = Cont.GET_USER_INFO_URL + "?userID=" + userId;
        asyncHttpGet.execute(url);

        return convertView;
    }
}
