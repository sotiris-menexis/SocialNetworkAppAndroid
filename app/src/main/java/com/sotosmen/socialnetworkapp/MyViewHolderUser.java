package com.sotosmen.socialnetworkapp;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolderUser extends RecyclerView.ViewHolder {
    TextView usernameTxt;
    TextView firstLastNameTxt;
    TextView profPicTxt;
    Button addFriendBtn;

    public MyViewHolderUser(@NonNull View view) {
        super(view);
        usernameTxt = view.findViewById(R.id.usernameUserItem);
        firstLastNameTxt = view.findViewById(R.id.firstLastNameUserItem);
        profPicTxt = view.findViewById(R.id.profPicUserItem);
        addFriendBtn = view.findViewById(R.id.addFriendBtnUserItem);
    }
}
