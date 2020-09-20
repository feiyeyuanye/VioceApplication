package com.example.myhelper.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.myhelper.R;
import com.example.myhelper.record.activity.VioceActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initData() {
        System.out.println();
    }

    private void initView() {
        tvRecord = findViewById(R.id.tv_record);

        tvRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_record:
                VioceActivity.actionStart(MainActivity.this);
                break;
            default:
                break;
        }
    }


}
