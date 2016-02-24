package com.example.knightboy.doudou.http;

import android.os.AsyncTask;
import android.util.Log;

import com.example.knightboy.doudou.listener.HttpJsonDataListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by knightBoy on 2015/8/27.
 * 获得的是json字符串
 */
public class AsyncHttpGet extends AsyncTask<String, Void, String> {
    private HttpJsonDataListener httpJsonDataListener;

    //设置取得数据后的监听器
    public void setHttpJsonDataListener(HttpJsonDataListener listViewDataListener) {
        this.httpJsonDataListener = listViewDataListener;
    }

    @Override
    protected String doInBackground(String... params) {
        return requestByGet(params[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(this.httpJsonDataListener != null){
            httpJsonDataListener.onDataUpdate(s);
        }
    }

    /**
     * get请求，返回字符串
     * @param aurl
     * @return result
     */
    public String requestByGet(String aurl) {
        String result = null;
        try {
            URL url = new URL(aurl);
            HttpURLConnection hc = (HttpURLConnection) url.openConnection();
            hc.setRequestMethod("GET");
            hc.setReadTimeout(3000);
            InputStream content = hc.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(content);
            byte[] b = new byte[1024];
            int len = bis.read(b);
            while (len != -1) {
                String str = new String(b, 0, len);
                result += str;
                len = bis.read(b);
            }
            bis.close();
            content.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
