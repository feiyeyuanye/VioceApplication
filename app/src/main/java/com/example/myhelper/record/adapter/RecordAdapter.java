package com.example.myhelper.record.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhelper.bean.RecordBean;
import com.example.myhelper.R;

import java.io.File;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder>{

       private List<RecordBean> mList;
       private Context mContext;


    public RecordAdapter(List<RecordBean> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;

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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterListener.onAdapter(position);
                    Toast.makeText(mContext,recordBean.getFilePath(),Toast.LENGTH_SHORT).show();
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    fileDeleteDialog(position,recordBean.getFilePath());
                    return false;
                }
            });
            holder.tvFileName.setText(recordBean.getFileName());
            holder.tvFileTime.setText(recordBean.getFileTime());
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }


      class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFileName,tvFileTime;

        private ViewHolder(View view) {
            super(view);
            tvFileName = view.findViewById(R.id.tv_fileName);
            tvFileTime = view.findViewById(R.id.tv_fileTime);
        }
    }

    /**
     * 主界面更新数据
     * @param mList 更新集合数据
     */
    public void setData(List<RecordBean> mList){
            this.mList = mList;
        }

    /**
     * 弹框，删除文件
     * @param index 标示，删除集合中的对应项
     * @param filePath 文件地址
     */
    private void fileDeleteDialog(final int index, final String filePath){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("删除此文件？");
        builder.setMessage(mList.get(index).getFileName());
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


    /**
     * 回调
     */
    private OnAdapterListener adapterListener;

    public void setOnAdapterListener(OnAdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }

    public interface OnAdapterListener {
         void onAdapter(int position);
    }


}
