package com.example.knightboy.doudou.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knightboy.doudou.R;

/**
 * Created by knightBoy on 2015/8/26.
 */
public class ToastUtil {
    public static void showShortToast(Context context,String message){
        Toast toast = makeToast(context,message,Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showLongToast(Context context,String message){
        Toast toast = makeToast(context,message,Toast.LENGTH_LONG);
        toast.show();
    }

    private static Toast makeToast(Context context,String message,int timeType){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.view_toast_dialog, null);
        TextView tv_tip = (TextView)view.findViewById(R.id.tipTextView);
        tv_tip.setText(message);
        //获得Toast
        Toast toast=new Toast(context);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.setDuration(timeType);
        toast.setView(view);
        return toast;
    }
}
