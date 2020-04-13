package com.sotosmen.socialnetworkapp;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import androidx.appcompat.app.AppCompatActivity;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ThreadLayoutFrag extends Fragment {
    private static int position;
    private List<Post> posts = new ArrayList<>();
    public static Thread currentThread;
    public static View view;
    public static TextView threadTitle;
    public static TextView threadCreator;
    public static RecyclerView recyclerView;
    public static TextView textArea;
    public static Button postBtn;
    public static Button backBtn;
    public static ImageView logOutBtn;
    public static SwipeRefreshLayout swipeRefreshLayout;
    CustomRecyclerAdapterThread cra;
    private String calledFromFrag="";

    public ThreadLayoutFrag(int position, String calledFromFrag){
        this.calledFromFrag = calledFromFrag;
        this.position = position;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.thread_layout,container,false);
        new setUpPostsForThreadTask().execute();
        return view;
    }

    public void initializeView(){
        logOutBtn = view.findViewById(R.id.logOuBtnThreadLayout);
        threadTitle = view.findViewById(R.id.threadNameTitle);
        threadCreator = view.findViewById(R.id.creatorUsernameThread);
        textArea = view.findViewById(R.id.textAreaThread);
        postBtn = view.findViewById(R.id.postBtnThread);
        backBtn = view.findViewById(R.id.backBtnThread);
        recyclerView = view.findViewById(R.id.recyclerViewThread);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.d("Post size",String.valueOf(posts.size()));
        cra = new CustomRecyclerAdapterThread(posts,getContext(),position);
        recyclerView.setAdapter(cra);
        recyclerView.setVisibility(View.VISIBLE);
        threadTitle.setText(currentThread.getThreadName());
        threadCreator.setText("Created By: "+currentThread.getCreatorUser());
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshThread);
    }
    public void setUpListeners(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeScreenFragment.prevFrag = calledFromFrag;
                backBtn.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.back_btn_anim));
                ForumFragment.position = position;
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.startContainer, new HomeScreenFragment(), FragmentsNames.homeScreenFrag).commit();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new refreshPostsTask().execute();
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
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postBtn.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.post_btn_anim));
                if(currentThread.getType().equals("All")) {
                    new checkThreadChangedTaskThread().execute();
                }else {
                    new createPostTask().execute();
                }
            }
        });
    }



    private class setUpPostsForThreadTask extends AsyncTask<Void,Void,Void>{
        private String result = "";
        private Post[] newPosts;
        private String url = "";
        @Override
        protected Void doInBackground(Void... voids) {
            url = "http://192.168.1.3:8080/threads/"+currentThread.getThreadName()+"/posts";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            try{
                ResponseEntity<Post[]> entity = restTemplate.getForEntity(url,Post[].class);
                newPosts = entity.getBody();
                Log.d("Response",newPosts[0].getText());
                for(int i=0;i<newPosts.length;i++) {
                    posts.add(newPosts[i]);
                }
                Log.d("Posts","Successful pull "+posts.size());
                result = "Successful posts pull";
            }catch (HttpClientErrorException e){
                result = e.getResponseBodyAsString();
            }catch (Exception e){
                result = "Something went wrong try again";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getContext(),result,Toast.LENGTH_SHORT);
            initializeView();
            setUpListeners();
        }
    }

    private class refreshPostsTask extends AsyncTask<Void,Void,Void>{
        private String result = "";
        private Post[] newPosts;
        private String url = "";
        @Override
        protected Void doInBackground(Void... voids) {
            url = "http://192.168.1.3:8080/threads/"+currentThread.getThreadName()+"/posts";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            try{
                ResponseEntity<Post[]> entity = restTemplate.getForEntity(url,Post[].class);
                newPosts = entity.getBody();
                posts.clear();
                posts.addAll(Arrays.asList(newPosts));
                result = "Successful posts pull";
            }catch (HttpClientErrorException e){
                result = e.getResponseBodyAsString();
            }catch (Exception e){
                result = "Something went wrong try again";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(result == "Successful posts pull"){
                cra.clearData();
                cra.updateData(posts);
            }else if(result.equals("No posts were found in this thread")){
                cra.clearData();
            }
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
        }
    }

    private class createPostTask extends AsyncTask<Void,Void,Void>{
        private String result="";
        private String url="";
        private String text="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            text = textArea.getText().toString().trim();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(text!="") {
                Post post = new Post();
                post.setCreatorUser(MainActivity.currentUser.getUsername());
                post.setOwnerThread(currentThread.getThreadName());
                post.setText(text);
                post.setTimestamp(new Date());
                post.setType(currentThread.getType());
                url = "http://192.168.1.3:8080/posts/users/" + MainActivity.currentUser.getUsername() + "/threads/"
                        + HomeScreenFragment.userPermittedThreads.get(position).getThreadName();
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Post> entity = new HttpEntity<Post>(post, headers);
                Log.d("Posts",entity.toString());
                try {
                    restTemplate.exchange(url, HttpMethod.POST, entity, Post.class);
                    result = "Successful";
                } catch (HttpClientErrorException e) {
                    result = e.getResponseBodyAsString();
                } catch (Exception e) {
                    Log.d("Posts",e.getMessage());
                    result = "Something went wrong try again";
                }
                if (result == "Successful") {
                    url = "http://192.168.1.3:8080/threads/" + currentThread.getThreadName() + "/posts";
                    Post[] newPosts;
                    RestTemplate restTemplate1 = new RestTemplate();
                    restTemplate1.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    try {
                        ResponseEntity<Post[]> responseEntity = restTemplate1.getForEntity(url, Post[].class);
                        newPosts = responseEntity.getBody();
                        Log.d("Response",responseEntity.getBody().toString());
                        posts.clear();
                        posts.addAll(Arrays.asList(newPosts));
                        result = "Successful";
                    } catch (HttpClientErrorException e) {
                        result = e.getResponseBodyAsString();
                    } catch (Exception e) {
                        Log.d("Posts",e.getMessage());
                        result = "Something went wrong try again";
                    }

                }
            }else {
                result = "Text can't be empty";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(result=="Successful"){
                cra.clearData();
                cra.updateData(posts);
                textArea.setText("");
                textArea.setHint("Write here your post.");
                textArea.clearFocus();
                view.requestFocus();
                Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class checkThreadChangedTaskThread extends AsyncTask<Void,Void,Void>{
        private String result = "";
        @Override
        protected Void doInBackground(Void... voids) {
            String url = "http://192.168.1.3:8080/threads/"+currentThread.getThreadName();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(3000);
            rf.setConnectTimeout(3000);
            try{
                Thread thread = restTemplate.getForObject(url,Thread.class);
                if(thread.getType().equals(currentThread.getType())){
                    result = "Unchanged";
                }else{
                    result = "Changed";
                }
            }catch (HttpClientErrorException e){
                result = e.getResponseBodyAsString();
            }catch (Exception e){
                result = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(result == "Unchanged"){
                new createPostTask().execute();
            }else if(result == "Changed"){
                result = "Action Forbidden";
                Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
            }
        }
    }

}
