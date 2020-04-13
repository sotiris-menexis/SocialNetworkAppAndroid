package com.sotosmen.socialnetworkapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolderFriendEmpty extends RecyclerView.ViewHolder {
    TextView friendEmptyTxt;

    public MyViewHolderFriendEmpty(@NonNull View view) {
        super(view);
        friendEmptyTxt = view.findViewById(R.id.friendTitleEmpty);
    }
}
