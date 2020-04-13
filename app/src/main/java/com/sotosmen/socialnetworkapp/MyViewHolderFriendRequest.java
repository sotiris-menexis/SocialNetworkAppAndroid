package com.sotosmen.socialnetworkapp;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolderFriendRequest extends RecyclerView.ViewHolder {
    TextView usernameTxt;
    TextView profPicTxt;
    Button acceptBtn;
    Button declineBtn;

    public MyViewHolderFriendRequest(@NonNull View view) {
        super(view);
        usernameTxt = view.findViewById(R.id.usernameUserItemFriendReq);
        profPicTxt = view.findViewById(R.id.profPicUserItemFriendReq);
        acceptBtn = view.findViewById(R.id.acceptBtnFriendReq);
        declineBtn = view.findViewById(R.id.declineBtnFriendReq);
    }
}
