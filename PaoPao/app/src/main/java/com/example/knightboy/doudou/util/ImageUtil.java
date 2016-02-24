package com.example.knightboy.doudou.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by knightBoy on 2015/9/2.
 */
public class ImageUtil {
    public static final float DISPLAY_WIDTH = 300;
    public static final float DISPLAY_HEIGHT = 400;

    /**
     * 从path中获取图片信息
     * @param path
     * @return
     */
    public static Bitmap decodeBitmap(String path){
        BitmapFactory.Options op = new BitmapFactory.Options();
        //inJustDecodeBounds
        //If set to true, the decoder will return null (no bitmap), but the out…
        op.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path, op); //获取尺寸信息
        //获取比例大小
        int wRatio = (int)Math.ceil(op.outWidth/DISPLAY_WIDTH);
        int hRatio = (int)Math.ceil(op.outHeight/DISPLAY_HEIGHT);
        //如果超出指定大小，则缩小相应的比例
        if(wRatio > 1 && hRatio > 1){
            if(wRatio > hRatio){
                op.inSampleSize = wRatio;
            }else{
                op.inSampleSize = hRatio;
            }
        }
        op.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(path, op);
        return bmp;
    }
}
