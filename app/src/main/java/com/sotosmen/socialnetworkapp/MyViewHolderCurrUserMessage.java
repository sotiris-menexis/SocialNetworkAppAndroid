package com.sotosmen.socialnetworkapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolderCurrUserMessage extends RecyclerView.ViewHolder {
    TextView messageText;
    TextView timestamp;
    public MyViewHolderCurrUserMessage(@NonNull View view) {
        super(view);
        messageText = view.findViewById(R.id.messengerCurrUserText);
        timestamp = view.findViewById(R.id.timestampMessengerCurrUser);
    }
}
