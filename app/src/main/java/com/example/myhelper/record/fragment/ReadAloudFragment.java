package com.example.myhelper.record.fragment;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhelper.bean.RecordBean;
import com.example.myhelper.R;
import com.example.myhelper.record.adapter.RecordAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ReadAloudFragment extends Fragment implements RecordAdapter.OnAdapterListener {

    private EditText input;
    private Button speech, record;
    private String fileName,filePath;

    private Context mContext;

    /**
     * 语音朗读
     */
    private TextToSpeech textToSpeech;
    private RecyclerView rv;
    private RecordAdapter adapter;
    private List<RecordBean> mList = new ArrayList<>();

    /**
     * 语音播放
     */
    private MediaPlayer mediaPlayer;
    private int indexPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_read, container, false);
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
        input =  view.findViewById(R.id.input_text);
        speech =  view.findViewById(R.id.speech);
        record =  view.findViewById(R.id.record);
        rv = view.findViewById(R.id.rv);
    }

    private void initData() {
        showList();
        readList();
    }

    /**
     * 回去文件列表
     * @param fileAbsolutePath
     */
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

    private void showList(){
        // 展示
        getFileName(Environment.getExternalStorageDirectory() + "/jhr/tts");

        LinearLayoutManager layoutManager= new LinearLayoutManager(mContext);
        rv.setLayoutManager(layoutManager);
        adapter = new RecordAdapter(mList,mContext);
        adapter.setOnAdapterListener(this);
        rv.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void readList(){
        // 朗读
        textToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == textToSpeech.SUCCESS) {
                    //int result = textToSpeech.setLanguage(new Locale("th", "TH"));
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE){
                        Toast.makeText(mContext, "TTS暂时不支持这种语音的朗读！",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        // 设置音调
        textToSpeech.setPitch(1.0f);
        // 设置语速
        textToSpeech.setSpeechRate(1.0f);
        speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(input.getText().toString(),
                        TextToSpeech.QUEUE_ADD, null);
            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileNameDialog();
            }
        });
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
                String str = edt.getText() == null ? "" : edt.getText()
                        .toString();

                fileName = DateFormat.format("yyyy-M-dd HH-mm", Calendar.getInstance(Locale.CHINA)) + "_"+ str +".wav";
                File destDir = new File(Environment.getExternalStorageDirectory() + "/jhr/tts/");
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                filePath = Environment.getExternalStorageDirectory() + "/jhr/tts/" + fileName;


                String inputText = input.getText().toString();
                HashMap<String, String> myHashRender = new HashMap<>();
                myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, inputText);
                textToSpeech.synthesizeToFile(inputText, myHashRender,
                        filePath);

                Toast.makeText(mContext, "声音记录成功啦", Toast.LENGTH_SHORT).show();

                //刷新数据
                getFileName(Environment.getExternalStorageDirectory() + "/jhr/tts");
                adapter.setData(mList);
                adapter.notifyDataSetChanged();

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

    /**
     * 播放
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
                    readFragmentListener.onReadFragmentListener("音频");

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

                    readFragmentListener.onReadFragmentListener(mList.get(indexPath).getFileName());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showToast(String str){
        Toast.makeText(mContext,str,Toast.LENGTH_SHORT).show();
    }


    /**
     * 回调
     */
    private OnReadFragmentListener readFragmentListener;

    public void setOnRecordFragmentListener(OnReadFragmentListener readFragmentListener) {
        this.readFragmentListener = readFragmentListener;
    }



    public interface OnReadFragmentListener {
        void onReadFragmentListener(String str);
    }

    @Override
    public void onDestroyView() {
        if (textToSpeech != null)
            textToSpeech.shutdown();
        super.onDestroyView();
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
    }

}
