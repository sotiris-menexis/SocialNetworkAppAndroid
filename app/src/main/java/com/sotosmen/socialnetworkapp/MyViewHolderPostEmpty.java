package com.sotosmen.socialnetworkapp;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolderPostEmpty extends RecyclerView.ViewHolder {
    TextView postTextEmpty;
    View view;
    public MyViewHolderPostEmpty(View view) {
        super(view);
        this.view = view;
        this.postTextEmpty = this.view.findViewById(R.id.postTextEmpty);
    }
}
