package com.sotosmen.socialnetworkapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolderFriendRequestEmpty extends RecyclerView.ViewHolder {
    TextView friendRequestEmptyTitle;
    public MyViewHolderFriendRequestEmpty(@NonNull View view) {
        super(view);
        friendRequestEmptyTitle = view.findViewById(R.id.friendRequestTitleEmpty);
    }
}
