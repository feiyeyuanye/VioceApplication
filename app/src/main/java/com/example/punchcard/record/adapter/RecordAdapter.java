package com.example.punchcard.record.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.punchcard.R;
import com.example.punchcard.bean.RecordBean;
import com.example.punchcard.record.activity.RecordActivity;
import com.example.punchcard.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder>{

       private List<RecordBean> mList;
       private Context mContext;

        static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvFileName,tvFileTime;

            private ViewHolder(View view) {
                super(view);
                tvFileName = view.findViewById(R.id.tv_fileName);
                tvFileTime = view.findViewById(R.id.tv_fileTime);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_record, parent, false);
            final ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            final RecordBean recordBean = mList.get(position);

            String[] fileMes = recordBean.getFileName().split("_");
            final String path = recordBean.getFilePath();//获得文件的地址

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play(path);
                    Toast.makeText(mContext,path,Toast.LENGTH_SHORT).show();
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    fileDeleteDialog(position,path);
                    return false;
                }
            });
            holder.tvFileName.setText(fileMes[1]);
            holder.tvFileTime.setText(fileMes[0]);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public RecordAdapter(List<RecordBean> mList, Context mContext) {
            this.mList = mList;
            this.mContext = mContext;
        }

        public void setData(List<RecordBean> mList){
            this.mList = mList;
        }

    private void fileDeleteDialog(final int index, final String filePath){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("注意");
        builder.setMessage("删除此文件？\n"+filePath);
        final AlertDialog dialog;
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(filePath);
                if (file.isFile() && file.exists()) {
                    file.delete();
                    mList.remove(index);
                    notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        dialog = builder.create();
        dialog.show();

    }
    private void play(String path) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            //        重置音频文件，防止多次点击会报错
            mediaPlayer.reset();
//        调用方法传进播放地址
            mediaPlayer.setDataSource(path);
//            异步准备资源，防止卡顿
            mediaPlayer.prepareAsync();
//            调用音频的监听方法，音频准备完毕后响应该方法进行音乐播放
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
