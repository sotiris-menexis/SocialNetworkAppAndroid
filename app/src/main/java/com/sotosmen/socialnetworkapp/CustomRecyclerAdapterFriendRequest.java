package com.sotosmen.socialnetworkapp;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerAdapterFriendRequest extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private View view;
    private ViewGroup parent;
    private List<FriendRequest> friendRequests = new ArrayList<>();
    private static final int VIEW_TYPE_FRIENDS_REQUESTS_ITEM_EMPTY = 0;
    private static final int VIEW_TYPE_FRIENDS_REQUESTS_ITEM = 1;
    private List<MyViewHolderFriendRequest> myViewHolderFriendRequestList = new ArrayList<>();

    public CustomRecyclerAdapterFriendRequest(List<FriendRequest> friendRequests){
        this.friendRequests.addAll(friendRequests);
    }

    @Override
    public int getItemViewType(int position) {
        if(friendRequests.isEmpty()){
            return VIEW_TYPE_FRIENDS_REQUESTS_ITEM_EMPTY;
        }else {
            return VIEW_TYPE_FRIENDS_REQUESTS_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        if(viewType == VIEW_TYPE_FRIENDS_REQUESTS_ITEM_EMPTY){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_item_empty,parent,false);
            MyViewHolderFriendRequestEmpty myViewHolderFriendRequestEmpty = new MyViewHolderFriendRequestEmpty(view);
            return myViewHolderFriendRequestEmpty;
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_item,parent,false);
            MyViewHolderFriendRequest myViewHolderFriendRequest = new MyViewHolderFriendRequest(view);
            myViewHolderFriendRequestList.add(myViewHolderFriendRequest);
            return myViewHolderFriendRequest;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder,final int position) {
        if(holder.getItemViewType() == VIEW_TYPE_FRIENDS_REQUESTS_ITEM_EMPTY){
            MyViewHolderFriendRequestEmpty myViewHolderFriendRequestEmpty = (MyViewHolderFriendRequestEmpty) holder;
        }else {
            final MyViewHolderFriendRequest myViewHolderFriendRequest = (MyViewHolderFriendRequest) holder;
            String username = "Username: "+friendRequests.get(position).getFromUserId();
            myViewHolderFriendRequest.usernameTxt.setText(username);
            myViewHolderFriendRequest.profPicTxt.setText(friendRequests.get(position).getFromUserId().substring(0,1));
            myViewHolderFriendRequest.acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myViewHolderFriendRequest.declineBtn.setClickable(false);
                    new createFriendTask().execute(Integer.valueOf(position));
                }
            });
            myViewHolderFriendRequest.declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myViewHolderFriendRequest.acceptBtn.setClickable(false);
                    new removeFriendRequestTask().execute(Integer.valueOf(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(friendRequests.isEmpty()){
            return 1;
        }else {
            return friendRequests.size();
        }
    }

    public void clearData(){
        friendRequests.clear();
        notifyDataSetChanged();
    }

    public void updateData(List<FriendRequest>friendRequestList){
        friendRequests.addAll(friendRequestList);
        notifyDataSetChanged();
    }

    private class createFriendTask extends AsyncTask<Integer,Void,Void>{
        private int position;
        private String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            String url = "http://192.168.1.3:8080/friends";
            String url1 = "http://192.168.1.3:8080/friendrequest/"+friendRequests.get(position).getFriend_request_id();
            position = integers[0];
            Friend friend = new Friend();
            friend.setFriendUser2(friendRequests.get(position).getToUserId());
            friend.setFriendUser1(friendRequests.get(position).getFromUserId());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(3000);
            rf.setConnectTimeout(3000);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Friend> entity = new HttpEntity<>(friend,headers);
            try{
                restTemplate.exchange(url, HttpMethod.POST,entity,Friend.class);
                restTemplate.delete(url1);
                result = "Successful";
            }catch (HttpClientErrorException e){
                result = "Something went wrong try again";
            }catch (Exception e){
                result = "Something went wrong try again";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(result == "Successful"){
                friendRequests.remove(position);
                notifyItemRemoved(position);
            }else {
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }
            myViewHolderFriendRequestList.get(position).declineBtn.setClickable(true);
        }
    }

    private class removeFriendRequestTask extends AsyncTask<Integer,Void,Void>{
        private int position;
        private String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            String url = "http://192.168.1.3:8080/friendrequest/"+friendRequests.get(position).getFriend_request_id();
            Log.d("Friend_Req_Id",String.valueOf(friendRequests.get(position).getFriend_request_id()));
            position = integers[0];
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
                result = "Something went wrong try again";
            }catch (Exception e){
                result = "Something went wrong try again";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(result == "Successful"){
                friendRequests.remove(position);
                notifyItemRemoved(position);
            }else {
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }
            myViewHolderFriendRequestList.get(position).acceptBtn.setClickable(true);
        }
    }

}
