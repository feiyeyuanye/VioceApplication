package com.example.voice.record.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import androidx.viewpager2.widget.ViewPager2;

import com.example.voice.R;
import com.example.voice.record.fragment.ReadAloudFragment;
import com.example.voice.record.fragment.RecordFragment;
import com.example.voice.utils.RequestPermission;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class VioceActivity extends AppCompatActivity implements View.OnClickListener, ReadAloudFragment.OnReadFragmentListener, RecordFragment.OnRecordFragmentListener {

    private ViewPager2 mViewPager2;
    private TabLayout mTabLayout;
    private ViewPagerFragmentStateAdapter mAdapter;
    private TextView tvRecycle,tvBack,tvTitle;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, VioceActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vioce);

        initView();
        init();
        initData();
    }

    private void initData() {
        List<Fragment> mFragments = new ArrayList<>();
        RecordFragment recordFragment = new RecordFragment();
        ReadAloudFragment readAloudFragment = new ReadAloudFragment();
        recordFragment.setOnRecordFragmentListener(this);
        readAloudFragment.setOnRecordFragmentListener(this);
        mFragments .add(recordFragment);
        mFragments .add(readAloudFragment);
        mAdapter = new ViewPagerFragmentStateAdapter(VioceActivity.this, mFragments);
        mViewPager2.setAdapter(mAdapter);

        mTabLayout.addTab(mTabLayout.newTab().setText("录音"));
        mTabLayout.addTab(mTabLayout.newTab().setText("朗读"));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mTabLayout.setScrollPosition(position,0,false);
            }
        });
    }

    private void init() {
        // 运行时权限
        RequestPermission requestPermission = new RequestPermission();
        requestPermission.RequestPermission(this);
    }

    private void initView() {
        mTabLayout = findViewById(R.id.tablayout);
        mViewPager2 = findViewById(R.id.viewpager2);
        tvRecycle = findViewById(R.id.tv_recycle);
        tvBack = findViewById(R.id.tv_back);
        tvTitle = findViewById(R.id.tv_title);

        tvRecycle.setOnClickListener(this);
        tvBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_recycle:
                // 回收站
                showToast("开发中");
                break;
            case  R.id.tv_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void showToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    /**
     * 回调
     * @param str
     */
    @Override
    public void onReadFragmentListener(String str) {
        tvTitle.setText(str);
    }

    /**
     * 回调
     * @param str
     */
    @Override
    public void onRecordFragmentListener(String str) {
        tvTitle.setText(str);
    }


    public class ViewPagerFragmentStateAdapter extends FragmentStateAdapter {

        private List<Fragment> mFragments;

        public ViewPagerFragmentStateAdapter(@NonNull VioceActivity fragmentActivity, List<Fragment> fragments) {
            super(fragmentActivity);
            this.mFragments = fragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getItemCount() {
            return mFragments.size();
        }
    }


}
