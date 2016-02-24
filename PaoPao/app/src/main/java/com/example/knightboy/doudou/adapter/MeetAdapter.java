package com.example.knightboy.doudou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.knightboy.doudou.bean.MeetBean;

import java.util.ArrayList;

/**
 * Created by knightBoy on 2015/9/6.
 */
public class MeetAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<MeetBean> meetBeans;

    public MeetAdapter(Context aContext,ArrayList<MeetBean> aMeetBeans) {
        context = aContext;
        inflater = LayoutInflater.from(context);
        meetBeans = aMeetBeans;
    }

    @Override
    public int getCount() {
        return meetBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return meetBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
