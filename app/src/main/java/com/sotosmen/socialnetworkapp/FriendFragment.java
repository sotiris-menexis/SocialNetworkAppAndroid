package com.sotosmen.socialnetworkapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment {
    public static View view;
    public static RecyclerView recyclerView;
    public static TextView friendRequestsBtn;
    public static CustomRecyclerAdapterFriend cra;
    public static SwipeRefreshLayout swipeRefreshLayout;
    private int position = -1;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.friend_layout,container,false);
        new setUpFriendsTask().execute();
        initializeView();
        setUpListeners();
        return view;
    }
    public void initializeView(){
        friendRequestsBtn = view.findViewById(R.id.friendRequestsBtnFriendFrag);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshFriend);
    }
    public void setUpListeners(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new refreshTask().execute();
            }
        });
        friendRequestsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.startContainer,new FriendRequestFrag(),FragmentsNames.friendReqFrag).commit();
            }
        });
    }

    private class setUpFriendsTask extends AsyncTask<Void,Void,Void>{
        private String result = "";
        private List<Friend> friendList = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... voids) {
            String url = "http://192.168.1.3:8080/friends/"+MainActivity.currentUser.getUsername();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(3000);
            rf.setConnectTimeout(3000);
            try{
                ResponseEntity<Friend[]> entity = restTemplate.getForEntity(url,Friend[].class);
                Friend[] friends = entity.getBody();
                for(Friend f:friends){
                    friendList.add(f);
                }
                result = "Successful";
            }catch (HttpClientErrorException e){
                if(e.getStatusCode().equals("400") || e.getStatusCode().equals("403")) {
                    result = e.getResponseBodyAsString();
                }else{
                    result = e.getMessage();
                }
            }catch (Exception e){
                result = "Something went wrong please try again";
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            cra = new CustomRecyclerAdapterFriend(friendList);
            recyclerView = view.findViewById(R.id.recyclerViewFriend);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(cra);
            if(position!=-1){
                recyclerView.scrollToPosition(position);
            }
            Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
        }
    }

    private class refreshTask extends AsyncTask<Void,Void,Void>{
        private String result = "";
        private List<Friend> friendList = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... voids) {
            String url = "http://192.168.1.3:8080/friends/"+MainActivity.currentUser.getUsername();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(3000);
            rf.setConnectTimeout(3000);
            try{
                ResponseEntity<Friend[]> entity = restTemplate.getForEntity(url,Friend[].class);
                Friend[] friends = entity.getBody();
                for(Friend f:friends){
                    friendList.add(f);
                }
                result = "Successful";
            }catch (HttpClientErrorException e){
                if(e.getStatusCode().equals("400") || e.getStatusCode().equals("403")) {
                    result = e.getResponseBodyAsString();
                }else{
                    result = e.getMessage();
                }
            }catch (Exception e){
                result = "Something went wrong please try again";
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(result == "Successful"){
                cra.clearData();
                cra.updateData(friendList);
            }
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
        }
    }
}
