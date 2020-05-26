package com.example.myhelper.record.fragment;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhelper.R;
import com.example.myhelper.bean.RecordBean;
import com.example.myhelper.record.adapter.RecordAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RecordFragment extends Fragment implements View.OnClickListener, RecordAdapter.OnAdapterListener {

    private Button btnStart;
    private RecyclerView rv;
    private RecordAdapter adapter;

    private MediaRecorder mMediaRecorder;
    private String fileName,filePath;

    private List<RecordBean> mList = new ArrayList<>();
    private int type;
    private Context mContext;

    private MediaPlayer mediaPlayer;
    private int indexPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        init();
        initData();
    }

    private void init() {
        mContext = getContext();
        mediaPlayer = new MediaPlayer();

    }

    private void initView(View view) {
        btnStart = view.findViewById(R.id.btn_start);
        rv = view.findViewById(R.id.rv);

        btnStart.setOnClickListener(this);

    }

    private void initData() {
        getFileName(Environment.getExternalStorageDirectory() + "/jhr/record");

        LinearLayoutManager layoutManager= new LinearLayoutManager(mContext);
        rv.setLayoutManager(layoutManager);
        adapter = new RecordAdapter(mList,mContext);
        adapter.setOnAdapterListener(this);
        rv.setAdapter(adapter);
    }

    public void getFileName(String fileAbsolutePath) {
        File file = new File(fileAbsolutePath);
        if (file.listFiles() != null){
            File[] subFile = file.listFiles();
            if (mList.size()>0)
                mList.clear();

            for (int i = 0; i < subFile.length; i++) {
                // 判断是否为文件夹
                if (!subFile[i].isDirectory()) {
                    String[] fileMes = subFile[i].getName().split("_");
                    RecordBean recordBean = new RecordBean(subFile[i].getName(),fileMes[1],fileMes[0],subFile[i].getAbsolutePath());
                    mList.add(recordBean);

                }
            }
        }

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

            if ("".equals(str)){
                str = String.valueOf(DateFormat.format("yyyyMMdd HHmmss", Calendar.getInstance(Locale.CHINA)));
            }
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
            setBtnText(1);
            showToast("录音开始");
        } catch (IllegalStateException e) {
            Log.e("failed!", e.getMessage());
        } catch (IOException e) {
            Log.e("failed!", e.getMessage());
        }
    }

    private void fileNameDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_recoed_filename,null,false);
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
                String str = edt.getText() == null ? "" : edt.getText().toString();

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
            showToast("录音结束");
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

    /**
     * type:
     * 0，开始录音
     * 1，正在录音中，单击结束录音...
     * 2，正在播放中，单击结束播放...
     * @param v
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                if (type == 0){
                    // 开始录音
                    fileNameDialog();
                }else if (type == 1){
                    // 停止录音
                    stopRecord();
                    setBtnText(0);
                }else if (type == 2){
                    // 停止播放
                    mediaPlayer.stop();
                    recordFragmentListener.onRecordFragmentListener("音频");
                    setBtnText(0);
                }
                break;
            case R.id.tv_recycle:
                // 回收站
                showToast("开发中");
                break;
            default:
                break;
        }
    }


    private void showToast(String str){
        Toast.makeText(mContext,str,Toast.LENGTH_SHORT).show();
    }

    /**
     * 更改按钮显示
     * @param type
     */
    private void setBtnText(int type){
        this.type = type;
        String str ;
        if (type == 0){
            str = "开始录音";
        }else if (type == 1){
            str = "正在录音中，单击结束录音...";
        }else if (type == 2){
            str = "正在播放中，单击结束播放...";
        }else {
            str = "出现错误";
        }
        btnStart.setText(str);

    }

    /**
     * Adapter 的回调
     */

    @Override
    public void onAdapter(int position) {
        play(position);
    }



    private void play(final int index) {
        indexPath = index;
        nextPlay();
        // 监听，自动播放下一个。
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                indexPath++;
                if (indexPath<=mList.size()-1){
                    showToast("播放下一首");
                    nextPlay();
                }else {
                    showToast("播放结束");
                    recordFragmentListener.onRecordFragmentListener("音频");
                    setBtnText(0);
                }
            }
        });
    }

    private void nextPlay(){
        try {
            //        重置音频文件，防止多次点击会报错
            mediaPlayer.reset();
//        调用方法传进播放地址
            mediaPlayer.setDataSource(mList.get(indexPath).getFilePath());
//            异步准备资源，防止卡顿
            mediaPlayer.prepareAsync();
//            调用音频的监听方法，音频准备完毕后响应该方法进行音乐播放
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    setBtnText(2);
                    recordFragmentListener.onRecordFragmentListener(mList.get(indexPath).getFileName());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
    }


    /**
     * 回调
     */
    private OnRecordFragmentListener recordFragmentListener;

    public void setOnRecordFragmentListener(OnRecordFragmentListener recordFragmentListener) {
        this.recordFragmentListener = recordFragmentListener;
    }

    public interface OnRecordFragmentListener {
        void onRecordFragmentListener(String str);
    }


}
