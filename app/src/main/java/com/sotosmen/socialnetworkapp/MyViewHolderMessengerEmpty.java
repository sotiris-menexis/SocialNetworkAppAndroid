package com.sotosmen.socialnetworkapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolderMessengerEmpty extends RecyclerView.ViewHolder {
    TextView textEmpty;
    public MyViewHolderMessengerEmpty(@NonNull View view) {
        super(view);
        textEmpty = view.findViewById(R.id.messengerTextEmpty);
    }
}
