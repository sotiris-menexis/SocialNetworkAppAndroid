package com.sotosmen.socialnetworkapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolderOtherUserMessage extends RecyclerView.ViewHolder {
    TextView profPicOtherUser;
    TextView messageText;
    TextView timestamp;

    public MyViewHolderOtherUserMessage(@NonNull View view) {
        super(view);
        profPicOtherUser = view.findViewById(R.id.profPicMessengerOtherUserItem);
        messageText = view.findViewById(R.id.messengerOtherUserTxt);
        timestamp = view.findViewById(R.id.timestampMessengerOtherUser);
    }
}
