package com.sotosmen.socialnetworkapp;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolderFriend extends RecyclerView.ViewHolder {
    TextView usernameTxt;
    TextView firstLastNameTxt;
    TextView profPicTxt;
    Button removeBtn;
    ConstraintLayout constraintLayout;
    public MyViewHolderFriend(@NonNull View view) {
        super(view);
        usernameTxt = view.findViewById(R.id.usernameUserItemFriend);
        firstLastNameTxt = view.findViewById(R.id.firstLastNameUserItemFriend);
        profPicTxt = view.findViewById(R.id.profPicUserItemFriend);
        removeBtn = view.findViewById(R.id.removeBtnFriendItem);
        constraintLayout = view.findViewById(R.id.constraintLayoutFriendItem);
    }
}
