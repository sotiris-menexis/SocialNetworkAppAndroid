package com.sotosmen.socialnetworkapp;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolderThreadEmpty extends RecyclerView.ViewHolder {
    TextView threadTextEmpty;
    public MyViewHolderThreadEmpty(View view) {
        super(view);
        this.threadTextEmpty = view.findViewById(R.id.titleThreadEmpty);
    }
}
