package com.sotosmen.socialnetworkapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class CustomRecyclerAdapterMessenger extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Getter @Setter
    List<Message> messagesList = new ArrayList<>();
    private ViewGroup parent;
    private View view;
    private static final int VIEW_TYPE_ITEM_CURR_USER=1;
    private static final int VIEW_TYPE_ITEM_OTHER_USER=2;
    private static final int VIEW_TYPE_ITEM_EMPTY=0;

    public CustomRecyclerAdapterMessenger(List<Message> messagesList){
        this.messagesList.addAll(messagesList);
    }

    @Override
    public int getItemViewType(int position) {
        if(messagesList.isEmpty()){
            return VIEW_TYPE_ITEM_EMPTY;
        }else {
            if(messagesList.get(position).getSenderUser().equals(MainActivity.currentUser.getUsername())){
                return VIEW_TYPE_ITEM_CURR_USER;
            }else{
                return VIEW_TYPE_ITEM_OTHER_USER;
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        if(viewType==VIEW_TYPE_ITEM_EMPTY){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messenger_item_empty,parent,false);
            MyViewHolderMessengerEmpty myViewHolderMessengerEmpty = new MyViewHolderMessengerEmpty(view);
            return myViewHolderMessengerEmpty;
        }else if(viewType==VIEW_TYPE_ITEM_CURR_USER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messenger_item_current_user,parent,false);
            MyViewHolderCurrUserMessage myViewHolderCurrUserMessage = new MyViewHolderCurrUserMessage(view);
            return myViewHolderCurrUserMessage;
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messenger_item_other_user,parent,false);
            MyViewHolderOtherUserMessage myViewHolderOtherUserMessage = new MyViewHolderOtherUserMessage(view);
            return myViewHolderOtherUserMessage;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if(holder.getItemViewType()==VIEW_TYPE_ITEM_EMPTY){
            MyViewHolderMessengerEmpty myViewHolderMessengerEmpty = (MyViewHolderMessengerEmpty) holder;
        }else if(holder.getItemViewType()==VIEW_TYPE_ITEM_CURR_USER){
            final MyViewHolderCurrUserMessage myViewHolderCurrUserMessage = (MyViewHolderCurrUserMessage) holder;
            myViewHolderCurrUserMessage.messageText.setText(messagesList.get(position).getText());
            myViewHolderCurrUserMessage.timestamp.setText(messagesList.get(position).getTimestamp().toString());
        }else {
            final MyViewHolderOtherUserMessage myViewHolderOtherUserMessage = (MyViewHolderOtherUserMessage) holder;
            myViewHolderOtherUserMessage.messageText.setText(messagesList.get(position).getText());
            myViewHolderOtherUserMessage.timestamp.setText(messagesList.get(position).getTimestamp().toString());
            myViewHolderOtherUserMessage.profPicOtherUser.setText(messagesList.get(position).getSenderUser().substring(0,1));
        }
    }

    @Override
    public int getItemCount() {
        if(messagesList.isEmpty()){
            return 1;
        }else{
            return messagesList.size();
        }
    }

    public void clearData(){
        messagesList.clear();
        notifyDataSetChanged();
    }

    public void addItem(Message message){
        messagesList.add(message);
        notifyItemInserted(getItemCount());
    }

    public void updateData(List<Message> messagesList){
        messagesList.addAll(messagesList);
        notifyDataSetChanged();
    }

}
