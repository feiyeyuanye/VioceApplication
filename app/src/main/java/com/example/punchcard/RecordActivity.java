package com.example.punchcard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnStart,btnStop;
    private TextView tvBack;

    private MediaRecorder mMediaRecorder;
    private String fileName,filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        tvBack = findViewById(R.id.tv_back);

        tvBack.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);


    }



    private void startRecord(String str) {
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        try {
            /* ②setAudioSource/setVedioSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            fileName = DateFormat.format("yyyy-MM-dd HH-mm-ss", Calendar.getInstance(Locale.CHINA)) + "_" + str + ".m4a";
            File destDir = new File(Environment.getExternalStorageDirectory() + "/jhr/record/");
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            filePath = Environment.getExternalStorageDirectory() + "/jhr/record/" + fileName;

            /* ③准备 */
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.prepare();
            /* ④开始 */
            mMediaRecorder.start();
            Toast.makeText(this,"录音开始",Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            Log.e("failed!", e.getMessage());
        } catch (IOException e) {
            Log.e("failed!", e.getMessage());
        }
    }

    private void fileNameDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
        View view = LayoutInflater.from(RecordActivity.this).inflate(R.layout.dialog_recoed_filename,null,false);
        builder.setView(view);
        builder.setTitle("文件名称");
        // 创建dialog
         final Dialog dialog = builder.create();
        // 初始化控件，注意这里是通过view.findViewById
         final EditText edt = (EditText) view.findViewById(R.id.edt);
        Button confirm = (Button) view.findViewById(R.id.confirm);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        // 设置button的点击事件及获取editview中的文本内容
        confirm.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String str = edt.getText() == null ? "" : edt.getText()
                        .toString();

                startRecord(str);
                dialog.dismiss();
            }
        });
        // 取消按钮
        cancel.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void stopRecord() {
        try {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
            Toast.makeText(this,"录音结束",Toast.LENGTH_SHORT).show();

        } catch (RuntimeException e) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;

            File file = new File(filePath);
            if (file.exists())
                file.delete();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                fileNameDialog();
                break;
            case  R.id.btn_stop:
                stopRecord();
                break;
            case R.id.tv_back:
                finish();
                break;
            default:
                break;
        }
    }

    public static void actionStart(Context mContext){
        Intent intent = new Intent(mContext,RecordActivity.class);
        mContext.startActivity(intent);
    }


}
