package com.example.punchcard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.punchcard.record.activity.VioceActivity;
import com.example.punchcard.utils.RequestPermission;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

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
