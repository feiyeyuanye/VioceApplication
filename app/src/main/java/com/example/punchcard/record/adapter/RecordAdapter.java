package com.example.punchcard.record.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.punchcard.R;
import com.example.punchcard.bean.RecordBean;

import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder>{

       private List<RecordBean> mList;

        static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvFileName;

            private ViewHolder(View view) {
                super(view);
                tvFileName = view.findViewById(R.id.tv_fileName);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_record, parent, false);
            final ViewHolder holder = new ViewHolder(view);
//            int position = holder.getAdapterPosition();
//            RecordBean recordBean = mList.get(position);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RecordBean recordBean = mList.get(position);

            holder.tvFileName.setText(recordBean.getFileName());
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public RecordAdapter(List<RecordBean> list) {
            mList = list;
        }

}
