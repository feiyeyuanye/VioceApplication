package com.example.voice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.voice.kotlin.activity.AudioActivity;
import com.example.voice.record.activity.VioceActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnJava,btnKotlin;

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
        btnJava = findViewById(R.id.btnJava);
        btnKotlin = findViewById(R.id.btnKotlin);

        btnJava.setOnClickListener(this);
        btnKotlin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnJava:
                VioceActivity.actionStart(MainActivity.this);
                break;
            case R.id.btnKotlin:
                AudioActivity.Companion.actionStart(this);
                break;
            default:
                break;
        }
    }
}
