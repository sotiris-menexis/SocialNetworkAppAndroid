package com.sotosmen.socialnetworkapp;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerAdapterFriend extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_FRIENDS_ITEM_EMPTY=0;
    private static final int VIEW_TYPE_FRIENDS_ITEM=1;
    private List<Friend> friends = new ArrayList<>();
    private View view;
    private ViewGroup parent;

    public CustomRecyclerAdapterFriend(List<Friend> friends){
        this.friends.addAll(friends);
    }

    @Override
    public int getItemViewType(int position) {
        if(friends.isEmpty()){
            return VIEW_TYPE_FRIENDS_ITEM_EMPTY;
        }else {
            return VIEW_TYPE_FRIENDS_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        if(viewType == VIEW_TYPE_FRIENDS_ITEM_EMPTY){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_friend_empty,parent,false);
            MyViewHolderFriendEmpty myViewHolderFriendEmpty = new MyViewHolderFriendEmpty(view);
            return myViewHolderFriendEmpty;
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_friend,parent,false);
            MyViewHolderFriend myViewHolderFriend = new MyViewHolderFriend(view);

            return myViewHolderFriend;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder,final int position) {
        if(holder.getItemViewType()==VIEW_TYPE_FRIENDS_ITEM_EMPTY){
            MyViewHolderFriendEmpty myViewHolderFriendEmpty = (MyViewHolderFriendEmpty) holder;
        }else {
            final MyViewHolderFriend myViewHolderFriend = (MyViewHolderFriend) holder;
            final String usernameTxt;
            final String username;
            if(friends.get(position).getFriendUser1().equals(MainActivity.currentUser.getUsername())) {
                username = friends.get(position).getFriendUser2();
                usernameTxt = "Username: " + username;
            }else {
                username = friends.get(position).getFriendUser1();
                usernameTxt = "Username: " + username;
            }
            myViewHolderFriend.usernameTxt.setText(usernameTxt);
            /*String firstLastName = "FirstName: "+ friends.get(position).getFriendUserObj().getFirstName()+"\n"+
                    "LastName: "+ friends.get(position).getFriendUserObj().getLastName();
            myViewHolderFriend.firstLastNameTxt.setText(firstLastName);*/
            myViewHolderFriend.firstLastNameTxt.setVisibility(View.INVISIBLE);
            myViewHolderFriend.profPicTxt.setText(friends.get(position).getFriendUser2().substring(0,1));
            myViewHolderFriend.removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(parent.getContext())
                            .setTitle("Title")
                            .setMessage("Are you sure you want to delete this Friend ?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    new deleteFriendTask().execute(Integer.valueOf(position));
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });
            myViewHolderFriend.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Username",username);
                    FragmentManager fragmentManager = ((AppCompatActivity) parent.getContext()).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.startContainer,new MessengerFragment(position,username),FragmentsNames.messengerFrag).commit();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(friends.isEmpty()){
            return 1;
        }else {
            return friends.size();
        }
    }

    public void clearData(){
        friends.clear();
        notifyDataSetChanged();
    }

    public void updateData(List<Friend> friendsList){
        friends.addAll(friendsList);
        notifyDataSetChanged();
    }

    private class deleteFriendTask extends AsyncTask<Integer,Void,Void> {
        private String result="";
        private int position;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            position = integers[0];
            String url = "http://192.168.1.3:8080/friends/users/"+friends
                    .get(position).getFriendUser1()+"/"+friends.get(position).getFriendUser2();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(3000);
            rf.setConnectTimeout(3000);
            try{
                restTemplate.delete(url);
                result = "Successful";
            }catch (HttpClientErrorException e){
                Log.d("Error_1",e.getResponseBodyAsString());
                result = "Something went wrong try again";
            }catch (Exception e){
                Log.d("Error_2",e.getMessage());
                result = "Something went wrong try again";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(result == "Successful"){
                HomeScreenFragment.currentUserFriends.remove(friends.get(position));
                friends.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
