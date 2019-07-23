package com.jyx.barrageview;

import android.view.View;

public abstract class BarrageAdapter<VH> {

    protected abstract View createView();

    public abstract VH onCreateViewHolder(VH viewHolder);

    public void createViewHolder(){

    }

}
