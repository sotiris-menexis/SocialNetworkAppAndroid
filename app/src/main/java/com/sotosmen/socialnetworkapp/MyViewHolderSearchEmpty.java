package com.sotosmen.socialnetworkapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolderSearchEmpty extends RecyclerView.ViewHolder {
    TextView textView;
    public MyViewHolderSearchEmpty(@NonNull View view) {
        super(view);
        textView = view.findViewById(R.id.noResultsFoundTxt);
    }
}
