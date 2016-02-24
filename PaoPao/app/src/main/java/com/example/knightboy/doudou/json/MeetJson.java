package com.example.knightboy.doudou.json;

import com.example.knightboy.doudou.bean.MeetBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by knightBoy on 2015/9/6.
 */
public class MeetJson {

    public static ArrayList<MeetBean> jsonToMeets(String jsonString){
        ArrayList<MeetBean> meetBeans = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            //生成新的动态链表
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //生成一条新的评论
                MeetBean meetBean = jsonObject2Meet(jsonObject);
                meetBeans.add(meetBean);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return meetBeans;
    }

    /**
     * 将一个JsonObject对象转换成一条遇见
     * @param jsonObject
     * @return
     */
    private static MeetBean jsonObject2Meet(JSONObject jsonObject){
        MeetBean meetBean = new MeetBean();
        try {
            meetBean.setUserId(jsonObject.getString("userId"));
            meetBean.setUserName(jsonObject.getString("userName"));
            meetBean.setAvatarUrl(jsonObject.getString("avatarUrl"));
            meetBean.setSex(Integer.parseInt(jsonObject.getString("sex")));
            meetBean.setBirthday(jsonObject.getString("birthday"));
            meetBean.setSigne(jsonObject.getString("signe"));
            meetBean.setLongtitude(jsonObject.getDouble("longtitude"));
            meetBean.setLatitude(jsonObject.getDouble("latitude"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return meetBean;
    }
}
