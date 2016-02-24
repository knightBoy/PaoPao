package com.example.knightboy.doudou.bean;

import java.io.Serializable;

/**
 * Created by knightBoy on 2015/8/25.
 */
public class StateBean implements Serializable {
    /**
     * 一条动态包括的所有属性
     */
    private int serialNum;         //1.数据库编号，查找索引
    private String avatarUrl;      //2.头像url地址
    private String userName;       //3.用户名
    private int sex;               //4.性别，1表示男，0表示女
    private String position;       //5.位置
    private String Time;           //6.发表时间
    private String content;        //7.动态主要内容
    private String pictureUrl;     //8.图片url地址
    private int    goodNum;        //9.点赞数目
    private int userId;            //10.用户Id

    /**
     * 下面是上面属性的set，get方法
     */

    public int getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public int getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(int goodNum) {
        this.goodNum = goodNum;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
