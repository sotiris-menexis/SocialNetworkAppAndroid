package com.sotosmen.socialnetworkapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ForumFragment extends Fragment {
    public static View view;
    public static RecyclerView recyclerView;
    public static int position =-1;
    public static SwipeRefreshLayout swipeRefreshLayout;
    public static TextView addThreadBtn;
    @Getter @Setter
    CustomRecyclerAdapterForum cra;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.forum_layout,container,false);
        initializeView();
        listeners();
        return view;
    }
    public void initializeView(){
        addThreadBtn = view.findViewById(R.id.addThreadTextForum);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshForum);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        recyclerView = view.findViewById(R.id.recyclerViewForum);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        cra = new CustomRecyclerAdapterForum(HomeScreenFragment.userPermittedThreads,getContext());
        recyclerView.setAdapter(cra);
        recyclerView.setVisibility(View.VISIBLE);
        if(position != -1){
            recyclerView.scrollToPosition(position);
        }
    }

    public void listeners(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new refreshTask().execute();
            }
        });
        addThreadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.startContainer,new AddThreadFrag(FragmentsNames.forumFrag),FragmentsNames.addThreadFrag).commit();
            }
        });
    }

    private class refreshTask extends AsyncTask<Void,Void,Void> {
        private String result = "";
        private List<Thread> tempList = new ArrayList<>();
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("RefreshTask","Running doInBackground");
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(3000);
            rf.setConnectTimeout(3000);
            String url = "http://192.168.1.3:8080/threads";
            try {
                ResponseEntity<Thread[]> responseEntity = restTemplate.getForEntity(url, Thread[].class);
                Thread[] allThreads;
                allThreads = responseEntity.getBody();
                for(int i=0;i<allThreads.length;i++){
                    if(MainActivity.currentUser.getType().equals(allThreads[i].getType())||allThreads[i].getType().equals("All")){
                        Thread temp = allThreads[i];
                        tempList.add(temp);
                        Log.d("Threads List",String.valueOf(tempList.size()));
                    }
                }
                Log.d("Refresh","Refreshing RecyclerView");
                result = "Successful";
            } catch (HttpClientErrorException e) {
                result  = e.getResponseBodyAsString();
                Log.d("Error_Forum",result);
            } catch (Exception e){
                result  = "Something went wrong try again";
                Log.d("Exception",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(result =="Successful"){
                cra.clearData();
                cra.updateData(tempList);
            }
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
        }
    }
}
