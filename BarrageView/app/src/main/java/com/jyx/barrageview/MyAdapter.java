package com.jyx.barrageview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

public class MyAdapter extends BarrageView.Adapter<MyAdapter.ViewHolder> {


    private List<String> list;
    private Context context;

    public MyAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<String> list){
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_barrage,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int index) {
        holder.tvBarrage.setText(list.get(index));
    }

    @Override
    public int getBarrageCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends BarrageView.ViewHolder{
        private TextView tvBarrage;
        protected ViewHolder(@Nullable View itemView) {
            super(itemView);
            tvBarrage = itemView.findViewById(R.id.tv_barrage);
        }
    }
}
