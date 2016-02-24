package com.example.knightboy.doudou.http;

import android.os.AsyncTask;

import com.example.knightboy.doudou.listener.HttpJsonDataListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by knightBoy on 2015/8/27.
 */
public class AsyncHttpPost extends AsyncTask<Map<String,String>,Void,String> {
    private HttpJsonDataListener httpJsonDataListener;
    private String postUrl;

    public AsyncHttpPost(String url){
        this.postUrl = url;
    }

    //设置取得数据后的监听器
    public void setHttpJsonDataListener(HttpJsonDataListener listViewDataListener) {
        this.httpJsonDataListener = listViewDataListener;
    }

    @Override
    protected String doInBackground(Map<String,String>... params) {
        return requestByPost(postUrl,params[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        if(this.httpJsonDataListener != null){
            this.httpJsonDataListener.onDataUpdate(s);
        }
        super.onPostExecute(s);
    }

    public String requestByPost(String urlString,Map<String,String> map) {
        String resultStr = null;

        try {
            // 请求的地址
            String spec = urlString;
            // 根据地址创建URL对象
            URL url = new URL(spec);
            // 根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);

            // 传递的数据
            StringBuffer sb = new StringBuffer();
            for(String key : map.keySet()){
                sb.append(key + "=" + URLEncoder.encode(map.get(key),"UTF-8") + "&");
            }
            String originalStr = sb.toString();
            //去掉最后一个&
            String data = originalStr.substring(0,originalStr.length()-1);

            // 设置请求的头
            urlConnection.setRequestProperty("Connection", "keep-alive");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Length",
                    String.valueOf(data.getBytes().length));
            // 设置请求的头
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");

            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            //setDoInput的默认值就是true
            //获取输出流
            urlConnection.connect();
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();

            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                baos.close();
                resultStr = baos.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStr;
    }
}
