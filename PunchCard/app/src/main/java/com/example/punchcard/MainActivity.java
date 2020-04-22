package com.example.punchcard;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvRecord;
    private EditText input;
    private Button speech, record;

    private TextToSpeech textToSpeech;
    private String fileName,filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        // 运行时权限
        RequestPermission requestPermission = new RequestPermission();
        requestPermission.RequestPermission(this);

        initData();

    }

    private void initData() {

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == textToSpeech.SUCCESS) {
                    //int result = textToSpeech.setLanguage(new Locale("th", "TH"));
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE){
                        Toast.makeText(MainActivity.this, "TTS暂时不支持这种语音的朗读！",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        input = (EditText) findViewById(R.id.input_text);
        speech = (Button) findViewById(R.id.speech);
        record = (Button) findViewById(R.id.record);

        speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(input.getText().toString(),
                        TextToSpeech.QUEUE_ADD, null);
            }
        });

        fileName = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA)) + "sound.wav";
        File destDir = new File(Environment.getExternalStorageDirectory() + "/jhr/tts/");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        filePath = Environment.getExternalStorageDirectory() + "/jhr/tts/" + fileName;

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputText = input.getText().toString();
                HashMap<String, String> myHashRender = new HashMap<>();
                myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, inputText);
                textToSpeech.synthesizeToFile(inputText, myHashRender,
                        filePath);
                Toast.makeText(MainActivity.this, "声音记录成功啦", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        tvRecord = findViewById(R.id.tv_record);
        initListener();
    }

    private void initListener() {
        tvRecord.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_record:
                RecordActivity.actionStart(MainActivity.this);
                break;
                default:
                    break;
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null)
            textToSpeech.shutdown();
        super.onDestroy();
    }
}
