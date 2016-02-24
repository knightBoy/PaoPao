package com.example.knightboy.doudou.application;

import android.graphics.Bitmap;

import com.example.knightboy.doudou.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by knightBoy on 2015/8/25.
 */
public class CustomImageOption {
    private static DisplayImageOptions options;
    private static DisplayImageOptions userOptions;

    public static DisplayImageOptions getOptions(){
        if(options ==null){
            new CustomImageOption();
        }
        return options;
    }

    public static DisplayImageOptions getUserOptions(){
        if(userOptions == null){
            userImageOption();
        }
        return userOptions;
    }

    /**
     * 一般图片的配置
     */
    public CustomImageOption(){
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.content_image_loading) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.image_loading_failed) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.image_loading_failed) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
//                .displayer(new RoundedBitmapDisplayer(0)) // 设置成圆角图片
                .build(); // 构建完成
    }


    /**
     * 人物头像的配置
     */
    private static void userImageOption(){
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        userOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.default_avatar) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.default_avatar) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build(); // 构建完成
    }
}
