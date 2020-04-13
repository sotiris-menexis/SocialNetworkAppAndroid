package com.sotosmen.socialnetworkapp;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolderPost extends RecyclerView.ViewHolder{
    ConstraintLayout constraintLayout;
    TextView profileImgPost;
    TextView creatorUserPost;
    TextView timestampPost;
    EditText textPost;
    Button editBtn;
    Button saveBtn;
    Button deleteBtn;
    Context context;
    View view;
    public MyViewHolderPost(View view, Context context){
        super(view);
        this.context = context;
        this.view = view;
        constraintLayout = this.view.findViewById(R.id.constraintLayoutPostItem);
        profileImgPost = this.view.findViewById(R.id.profPicPostItem);
        profileImgPost.setTextSize(28);
        profileImgPost.setTextColor(ContextCompat.getColor(this.context,R.color.colorTextParams));
        profileImgPost.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        creatorUserPost = this.view.findViewById(R.id.postCreator);
        timestampPost = this.view.findViewById(R.id.postTimestamp);
        textPost = this.view.findViewById(R.id.postText);
        editBtn = this.view.findViewById(R.id.editBtnPostItem);
        editBtn.setVisibility(View.INVISIBLE);
        saveBtn = this.view.findViewById(R.id.saveBtnPostItem);
        saveBtn.setVisibility(View.INVISIBLE);
        deleteBtn = this.view.findViewById(R.id.deleteBrnPostItem);
        deleteBtn.setVisibility(View.INVISIBLE);
    }

}
