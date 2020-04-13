package com.sotosmen.socialnetworkapp;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

public class FriendRequestFrag extends Fragment {
    public static View view;
    public static TextView appTitleTxt;
    public static TextView fragTitleTxt;
    public static Button backBtn;
    public static RecyclerView recyclerView;
    public static CustomRecyclerAdapterFriendRequest cra;
    public static SwipeRefreshLayout swipeRefreshLayout;
    public static ImageView logOutBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.friend_request_layout,container,false);
        new setUpFriendRequestsTask().execute();
        initializeView();
        setUpListeners();
        return view;
    }
    public void initializeView(){
        logOutBtn = view.findViewById(R.id.logOuBtnFriendRequestLayout);
        appTitleTxt = view.findViewById(R.id.appTitleFriendRequest);
        fragTitleTxt = view.findViewById(R.id.friendRequestTitle);
        backBtn = view.findViewById(R.id.backBtnFriendRequest);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshFriendRequest);
    }
    public void setUpListeners(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new refreshTask().execute();
            }
        });
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutBtn.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.log_out_btn_anim));
                new AlertDialog.Builder(getContext())
                        .setTitle("Title")
                        .setMessage("Are you sure you want to log out ?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                getFragmentManager().beginTransaction().replace(R.id.startContainer,new LogInFragment(),FragmentsNames.logInFrag).commit();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeScreenFragment.prevFrag = FragmentsNames.friendFrag;
                getFragmentManager().beginTransaction()
                        .replace(R.id.startContainer,new HomeScreenFragment(),FragmentsNames.homeScreenFrag).commit();
            }
        });
    }
    private class setUpFriendRequestsTask extends AsyncTask<Void,Void,Void>{
        private String result="";
        private List<FriendRequest> friendRequestsList = new ArrayList<>();
        @Override
        protected Void doInBackground(Void... voids) {
            String url = "http://192.168.1.3:8080/users/"+MainActivity.currentUser.getUsername()+"/friendrequest";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(3000);
            rf.setConnectTimeout(3000);
            try{
                ResponseEntity<FriendRequest[]> entity = restTemplate.getForEntity(url,FriendRequest[].class);
                FriendRequest[] friendRequests = entity.getBody();
                for(FriendRequest fr:friendRequests){
                    friendRequestsList.add(fr);
                }
                result = "Successful";
            }catch (HttpClientErrorException e){
                if(e.getStatusCode().equals("404") || e.getStatusCode().equals("403")) {
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
            cra = new CustomRecyclerAdapterFriendRequest(friendRequestsList);
            initializeView();
            recyclerView = view.findViewById(R.id.recyclerViewFriendRequest);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(cra);
            setUpListeners();
        }
    }

    private class refreshTask extends AsyncTask<Void,Void,Void>{
        private String result="";
        private List<FriendRequest> friendRequestsList = new ArrayList<>();
        @Override
        protected Void doInBackground(Void... voids) {
            String url = "http://192.168.1.3:8080/users/"+MainActivity.currentUser.getUsername()+"/friendrequest";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(3000);
            rf.setConnectTimeout(3000);
            try{
                ResponseEntity<FriendRequest[]> entity = restTemplate.getForEntity(url,FriendRequest[].class);
                FriendRequest[] friendRequests = entity.getBody();
                for(FriendRequest fr:friendRequests){
                    friendRequestsList.add(fr);
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
                cra.updateData(friendRequestsList);
            }
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
        }
    }

}
