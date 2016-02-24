package com.example.knightboy.doudou.bean;

import java.io.Serializable;

/**
 * Created by knightBoy on 2015/8/25.
 */
public class BrainBean implements Serializable {
    /**
     * 一条脑洞包括的所有属性
     */
    private int serialNum;         //1.数据库编号，查找索引
    private String userName;       //2.用户名
    private String type;           //3.脑洞类型
    private String content;        //4.动态主要内容
    private int    goodNum;        //5.点赞数目
    private int userId;            //6.用户ID

    private int bgDrawableId;      //文字背景图片Id

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(int goodNum) {
        this.goodNum = goodNum;
    }

    public int getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBgDrawableId() {
        return bgDrawableId;
    }

    public void setBgDrawableId(int bgDrawableId) {
        this.bgDrawableId = bgDrawableId;
    }
}
