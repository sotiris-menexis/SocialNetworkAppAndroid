package com.sotosmen.socialnetworkapp;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolderThread extends RecyclerView.ViewHolder {
    TextView threadVotes;
    TextView threadTitle;
    TextView threadDesc;
    TextView threadCreatorUsername;
    ConstraintLayout constraintLayoutThreadItem;
    ImageView upvote;
    ImageView downvote;
    ImageView dropDownBtn;
    ConstraintLayout dropDownLayout;
    Button editBtnDropDown;
    Button deleteBtnDropDown;
    public MyViewHolderThread(View view) {
        super(view);
        this.threadTitle = view.findViewById(R.id.titleThread);
        this.threadVotes = view.findViewById(R.id.votesThread);
        this.threadDesc = view.findViewById(R.id.descThread);
        this.threadCreatorUsername = view.findViewById(R.id.creatorUsername);
        this.upvote = view.findViewById(R.id.upvoteThread);
        this.downvote = view.findViewById(R.id.downvoteThread);
        this.upvote.setImageResource(R.drawable.upvote);
        this.downvote.setImageResource(R.drawable.downvote);
        this.dropDownBtn = view.findViewById(R.id.dropDownBtn);
        this.constraintLayoutThreadItem = view.findViewById(R.id.constraintLayoutThreadItem);
        this.dropDownLayout = view.findViewById(R.id.dropDownLayout);
        this.editBtnDropDown = view.findViewById(R.id.editBtnDropDown);
        this.deleteBtnDropDown = view.findViewById(R.id.deleteBtnDropDown);
    }
}
