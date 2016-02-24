package com.example.knightboy.doudou.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by knightBoy on 2015/8/28.
 */
public class TimeUtil {

    public static String getDistanceTime(String timeStr){
        String result = null;

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            java.util.Date date=sd.parse(timeStr);
            long diff = System.currentTimeMillis() - date.getTime();   //获得相差的毫秒数
            int year = (int) diff / (1000*3600*24*365);
            if(year != 0){
                result = year + "年前";
                return result;
            }
            int month = (int)diff/(1000*3600*24*30);
            if(month != 0){
                result = month + "月前";
                return result;
            }
            int day = (int)diff/(1000*3600*24);
            if(day != 0){
                result = day + "天前";
                return result;
            }
            int hour = (int)diff/(1000*3600);
            if(hour != 0){
                result = hour + "小时前";
                return result;
            }
            int minute = (int)diff/(1000*60);
            result = minute + "分钟前";
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据出生日期获取年龄
     * @param birthday
     * @return
     */
    public static int getAge(String birthday){
        int birthYear = Integer.parseInt(birthday.substring(0,4));
        //获取现在年份
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date curDate = new Date(System.currentTimeMillis());    //获取当前时间
        String str = formatter.format(curDate);
        int curYear = Integer.parseInt(str);
        return curYear - birthYear;
    }

    /**
     * 获得形如1995-08-05的日期
     * @param birthday
     * @return
     */
    public static String getformatBirthday(String birthday){
        return birthday.substring(0,4) + "-" + birthday.substring(4,6) + "-" + birthday.substring(6,8);
    }

    public static String getformatNow(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        Date curDate = new Date(System.currentTimeMillis());    //获取当前时间
        return formatter.format(curDate);
    }

    /**
     * 根据形如 09-05 11:30 的时间得到人性化的时间
     * @param time
     * @return
     */
    public static String getSessionTime(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
        String nowDay = simpleDateFormat.format(new Date());
        String timeDay = time.substring(0,5);
        if(nowDay.equals(timeDay)){
            return time.substring(6,time.length());
        }
        return time;
    }
}
