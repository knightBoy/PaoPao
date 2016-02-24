package com.example.knightboy.doudou.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.knightboy.doudou.R;
import com.example.knightboy.doudou.activity.EditBrainActivity;
import com.example.knightboy.doudou.fragment.subfragment.BestBrainFragment;
import com.example.knightboy.doudou.fragment.subfragment.NewBrainFragment;
import com.example.knightboy.doudou.fragment.subfragment.OldBrainFragment;

import java.util.ArrayList;

/**
 * Created by knightBoy on 2015/8/23.
 */
public class BrainFragment extends Fragment implements View.OnClickListener {
    private RadioGroup radioGroup;
    private RadioButton radioButton_new, radioButton_old, radioButton_best;
    private ImageView iv_edit_brain;
    private ViewPager viewPager;
    private Fragment newBrainFragment, oldBrainFragment, bestBrainFragment;
    private ArrayList<Fragment> subFragments = new ArrayList<>();
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_brain,container,false);
        context = getActivity();
        //获得控件
        radioGroup = (RadioGroup)rootview.findViewById(R.id.rg_brain);
        radioButton_new = (RadioButton)rootview.findViewById(R.id.rt_brain1);
        radioButton_old = (RadioButton)rootview.findViewById(R.id.rt_brain2);
        radioButton_best = (RadioButton)rootview.findViewById(R.id.rt_brain3);
        viewPager = (ViewPager)rootview.findViewById(R.id.viewPager);
        iv_edit_brain = (ImageView)rootview.findViewById(R.id.iv_edit_brain);
        iv_edit_brain.setOnClickListener(this);
        //生成子ragment对象
        newBrainFragment = new NewBrainFragment();
        oldBrainFragment = new OldBrainFragment();
        bestBrainFragment = new BestBrainFragment();
        //加入到链表中
        subFragments.add(newBrainFragment);
        subFragments.add(oldBrainFragment);
        subFragments.add(bestBrainFragment);

        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();

        //给viewPager设置适配器
        viewPager.setAdapter(new MyViewPagerAdapter(getChildFragmentManager(), subFragments));

        //设置当前页
        viewPager.setCurrentItem(0);
        radioGroup.check(R.id.rt_brain1);

        //设置页面变化监听器
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        //给按钮设置监听器
        radioGroup.setOnCheckedChangeListener(new MyOnCheckChangeListener());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_edit_brain:
                Intent intent = new Intent(context, EditBrainActivity.class);
                startActivity(intent);
                break;
        }
    }

    //**********************ViewPager的数据来源*******************************************************
    class MyViewPagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragmentlist;

        public MyViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments){
            super(fm);
            this.fragmentlist = fragments;
        }

        @Override
        public int getCount() {
            return fragmentlist.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentlist.get(position);
        }
    }
    //**********************实现ViewPager+RadioButton效果所需的内部监听器*******************************
    //ViewPager的切换监听器类
    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    radioGroup.check(R.id.rt_brain1);
                    break;
                case 1:
                    radioGroup.check(R.id.rt_brain2);
                    break;
                case 2:
                    radioGroup.check(R.id.rt_brain3);
                    break;
                default:
                    break;
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    //RadioButton的监听器类
    class MyOnCheckChangeListener implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.rt_brain1:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.rt_brain2:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.rt_brain3:
                    viewPager.setCurrentItem(2);
                    break;
                default:
                    break;
            }
        }
    }
    //*******************实现ViewPager+RadioButton效果所需的内部监听器结束*****************************
}
