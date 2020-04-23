package com.example.punchcard.record.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.punchcard.R;
import com.example.punchcard.bean.RecordBean;
import com.example.punchcard.record.adapter.RecordAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnStart;
    private TextView tvBack;
    private RecyclerView rv;
    private RecordAdapter adapter;

    private MediaRecorder mMediaRecorder;
    private String fileName,filePath;

    private List<RecordBean> mList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        initView();
        initData();

    }

    private void initView() {
        btnStart = findViewById(R.id.btn_start);
//        btnStop = findViewById(R.id.btn_stop);
        tvBack = findViewById(R.id.tv_back);
        rv = findViewById(R.id.rv);

        tvBack.setOnClickListener(this);
        btnStart.setOnClickListener(this);
//        btnStop.setOnClickListener(this);
    }

    private void initData() {
        getFileName(Environment.getExternalStorageDirectory() + "/jhr/record");

        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        adapter = new RecordAdapter(mList,this);
        rv.setAdapter(adapter);
    }

    public Vector<String> getFileName(String fileAbsolutePath) {
        Vector<String> vecFile = new Vector<String>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();
        if (mList.size()>0)
            mList.clear();

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                RecordBean recordBean = new RecordBean(subFile[i].getName(),subFile[i].getAbsolutePath());
                mList.add(recordBean);

            }
        }
        return vecFile;
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

            fileName = DateFormat.format("yyyy-MM-dd HH-mm", Calendar.getInstance(Locale.CHINA)) + "_" + str + ".m4a";
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
            btnStart.setText("正在录音...");
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
            getFileName(Environment.getExternalStorageDirectory() + "/jhr/record");
            adapter.setData(mList);
            adapter.notifyDataSetChanged();
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
                if ("开始录音".equals(btnStart.getText())){
                    fileNameDialog();
                }else {
                    stopRecord();
                    btnStart.setText("开始录音");
                }
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
