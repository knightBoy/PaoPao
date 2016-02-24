package com.example.knightboy.doudou.json;


import android.util.Log;

import com.example.knightboy.doudou.bean.StateBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by knightBoy on 2015/8/27.
 */
public class StateJson {

    /**
     * 下拉刷新
     * @param stateItems
     * @return
     */
    public static ArrayList<StateBean> refreshStateJson(ArrayList<StateBean> stateItems,String jsonString){
        //先暂存初始数据
        ArrayList<StateBean> stateBeans = stateItems;
        //清空所有数据
        stateItems.clear();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            //生成新的动态链表
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //生成一条新的状态
                StateBean stateBean = jsonObject2StateBean(jsonObject);
                stateItems.add(stateBean);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(stateItems.size() > 0){
            return stateItems;
        }else{
            return stateBeans;
        }
    }

    public static ArrayList<StateBean> loadMoreStateJson(ArrayList<StateBean> stateItems,String jsonString){
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            //在原有链表上追加
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //生成一条新的状态
                StateBean stateBean = jsonObject2StateBean(jsonObject);
                stateItems.add(stateBean);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return stateItems;
    }

    /**
     * 将一个JsonObject对象转换成一条动态
     * @param jsonObject
     * @return
     */
    private static StateBean jsonObject2StateBean(JSONObject jsonObject){
        StateBean stateBean = new StateBean();
        try {
            stateBean.setSerialNum(jsonObject.getInt("serialNum"));
            stateBean.setAvatarUrl(jsonObject.getString("avatarUrl"));
            stateBean.setUserName(jsonObject.getString("userName"));
            stateBean.setSex(jsonObject.getInt("sex"));
            stateBean.setPosition(jsonObject.getString("position"));
            stateBean.setTime(jsonObject.getString("time"));
            stateBean.setContent(jsonObject.getString("content"));
            stateBean.setPictureUrl(jsonObject.getString("pictureUrl"));
            stateBean.setGoodNum(jsonObject.getInt("goodNum"));
            stateBean.setUserId(jsonObject.getInt("userID"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return stateBean;
    }
}
