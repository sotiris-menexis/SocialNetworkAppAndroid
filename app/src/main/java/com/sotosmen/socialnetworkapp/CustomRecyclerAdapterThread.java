package com.sotosmen.socialnetworkapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class CustomRecyclerAdapterThread extends RecyclerView.Adapter<ViewHolder> {
    private List<Post> posts = new ArrayList<>();
    private static final int POSTS_VIEW_IS_EMPTY=0;
    private static final int POSTS_VIEW_HAS_ITEMS=1;
    Context context;
    private int threadPos;
    View view;
    ViewGroup parent;
    @Getter @Setter
    List<MyViewHolderPost> myViewHoldersThreads = new ArrayList<>();

    public CustomRecyclerAdapterThread(List<Post> posts, Context context, int threadPos){
        this.posts.addAll(posts);
        Log.d("Posts size adapter",String.valueOf(posts.size()));
        this.threadPos = threadPos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        if(viewType==0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_empty, parent, false);
            return new MyViewHolderPostEmpty(view);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
            MyViewHolderPost myViewHolderPost = new MyViewHolderPost(view,parent.getContext());
            myViewHoldersThreads.add(myViewHolderPost);
            return myViewHolderPost;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if(viewType==POSTS_VIEW_IS_EMPTY){
            MyViewHolderPostEmpty myViewHolderPostEmpty = (MyViewHolderPostEmpty) holder;
        }else {
            final MyViewHolderPost myViewHolderPost = (MyViewHolderPost) holder;
            myViewHolderPost.profileImgPost.setText(posts.get(position).getCreatorUser().substring(0,1));
            myViewHolderPost.textPost.setText(posts.get(position).getText());
            myViewHolderPost.timestampPost.setText(posts.get(position).getTimestamp().toString());
            myViewHolderPost.creatorUserPost.setText("POSTED BY: "+posts.get(position).getCreatorUser());
            myViewHolderPost.saveBtn.setVisibility(View.INVISIBLE);
            myViewHolderPost.deleteBtn.setVisibility(View.INVISIBLE);
            Log.d("Posts","Things go right");
            if(MainActivity.currentUser.getUsername().equals(posts.get(position).getCreatorUser())){
                myViewHolderPost.editBtn.setVisibility(View.VISIBLE);
                myViewHolderPost.textPost.setClickable(false);
                myViewHolderPost.textPost.setFocusable(false);
                myViewHolderPost.textPost.setFocusableInTouchMode(false);
                myViewHolderPost.editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myViewHolderPost.textPost.requestFocus();
                        myViewHolderPost.textPost.setClickable(true);
                        myViewHolderPost.textPost.setFocusable(true);
                        myViewHolderPost.textPost.setFocusableInTouchMode(true);
                        myViewHolderPost.textPost.requestFocus();
                        myViewHolderPost.saveBtn.setVisibility(View.VISIBLE);
                        myViewHolderPost.deleteBtn.setVisibility(View.VISIBLE);
                        myViewHolderPost.editBtn.setVisibility(View.INVISIBLE);
                    }
                });
                myViewHolderPost.saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(myViewHolderPost.textPost.getText().toString().trim().equals("")){
                            Toast.makeText(parent.getContext(),"Text cant be empty",Toast.LENGTH_SHORT).show();
                            myViewHolderPost.textPost.requestFocus();
                        }else {
                            posts.get(position).setText(myViewHolderPost.textPost.getText().toString().trim());
                            new updatePostTask().execute(Integer.valueOf(position));
                        }
                    }
                });
                myViewHolderPost.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(parent.getContext())
                                .setTitle("Title")
                                .setMessage("Do you really want to delete the Post ?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        new deletePostTask().execute(Integer.valueOf(position));
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                });
            }else{
                myViewHolderPost.editBtn.setVisibility(View.INVISIBLE);
                myViewHolderPost.textPost.setFocusable(false);
                myViewHolderPost.textPost.setFocusableInTouchMode(false);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(posts.isEmpty()){
            return POSTS_VIEW_IS_EMPTY;
        }else{
            return POSTS_VIEW_HAS_ITEMS;
        }
    }

    @Override
    public int getItemCount() {
        if(posts.isEmpty()){
            return 1;
        }else {
            return posts.size();
        }
    }

    public void clearData(){
        posts.clear();
        notifyDataSetChanged();
    }
    public void updateData(List<Post> newPosts){
        posts.addAll(newPosts);
        notifyDataSetChanged();
    }

    private class updatePostTask extends AsyncTask<Integer,Void,Integer>{
        private String urlPost ="";
        private String result="";
        private Post[] newPosts;
        @Override
        protected Integer doInBackground(Integer... integers) {
            urlPost = "http://192.168.1.3:8080/posts";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Post> entity = new HttpEntity<>(posts.get(integers[0]),headers);
            try{
                Log.d("Posts",entity.toString());
                restTemplate.exchange(urlPost, HttpMethod.PUT,entity,Post.class);
                result = "Successful";
            }catch (HttpClientErrorException e){
                Log.d("Posts","Error 1 :" +e.getResponseBodyAsString());
                result = e.getResponseBodyAsString();
            }catch (Exception e){
                Log.d("Posts","Error 1 :" +e.getMessage());
                result = "Something went wrong try again";
            }
            if(result == "Successful"){
                urlPost = "http://192.168.1.3:8080/threads/"+HomeScreenFragment.userPermittedThreads.get(threadPos).getThreadName()+"/posts";
                RestTemplate restTemplate1 = new RestTemplate();
                restTemplate1.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                try {
                    ResponseEntity<Post[]> responseEntity = restTemplate1.getForEntity(urlPost, Post[].class);
                    newPosts = responseEntity.getBody();
                    result = "Successful";
                }catch (HttpClientErrorException e){
                    Log.d("Posts","Error 2 :" +e.getResponseBodyAsString());
                    result = e.getResponseBodyAsString();
                }catch (Exception e){
                    Log.d("Posts","Error 2 :" +e.getMessage());
                    result = "Something went wrong try again";
                }

            }
            return integers[0];
        }

        @Override
        protected void onPostExecute(Integer position) {
            super.onPostExecute(position);
            if(result == "Successful"){
                clearData();
                List<Post> temp = new ArrayList<>();
                temp.addAll(Arrays.asList(newPosts));
                posts.addAll(temp);
                notifyItemChanged(position);
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }
            myViewHoldersThreads.get(position).saveBtn.setVisibility(View.INVISIBLE);
            myViewHoldersThreads.get(position).deleteBtn.setVisibility(View.INVISIBLE);
            myViewHoldersThreads.get(position).editBtn.setVisibility(View.VISIBLE);
            myViewHoldersThreads.get(position).textPost.setClickable(false);
            myViewHoldersThreads.get(position).textPost.setFocusable(false);
            myViewHoldersThreads.get(position).textPost.setFocusableInTouchMode(false);
            myViewHoldersThreads.get(position).textPost.clearFocus();
            myViewHoldersThreads.get(position).constraintLayout.requestFocus();
        }
    }
    private class deletePostTask extends AsyncTask<Integer,Void,Integer>{
        private String urlPost="";
        private String urlThread="";
        private String result="";
        private Post[] newPosts;
        private boolean noPostsFlag = false;
        @Override
        protected Integer doInBackground(Integer... integers) {
            urlPost = "http://192.168.1.3:8080/post/"+posts.get(integers[0]).getId();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            try{
                restTemplate.delete(urlPost);
                result = "Successful";
                Log.d("Success posts",result);
            }catch (HttpClientErrorException e){
                Log.d("Posts","Error 1_1 :" +e.getResponseBodyAsString());
                result = e.getResponseBodyAsString();
            }catch (Exception e){
                Log.d("Posts","Error 1_2 :" +e.getMessage());
                result = "Something went wrong try again";
            }
            if(result == "Successful"){
                urlPost = "http://192.168.1.3:8080/threads/"+HomeScreenFragment.userPermittedThreads.get(threadPos).getThreadName()+"/posts";
                RestTemplate restTemplate1 = new RestTemplate();
                restTemplate1.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                try {
                    ResponseEntity<Post[]> responseEntity = restTemplate1.getForEntity(urlPost, Post[].class);
                    newPosts = responseEntity.getBody();
                    result = "Successful";
                }catch (HttpClientErrorException e){
                    Log.d("Posts","Error 2 :" +e.getResponseBodyAsString());
                    noPostsFlag = true;
                    result = e.getResponseBodyAsString();
                }catch (Exception e){
                    Log.d("Posts","Error 2 :" +e.getMessage());
                    result = "Something went wrong try again";
                }

            }
            return integers[0];
        }

        @Override
        protected void onPostExecute(Integer position) {
            super.onPostExecute(position);
            if(result == "Successful"){
                clearData();
                List<Post> temp = new ArrayList<>();
                temp.addAll(Arrays.asList(newPosts));
                updateData(temp);
                myViewHoldersThreads.get(position).saveBtn.setVisibility(View.INVISIBLE);
                myViewHoldersThreads.get(position).deleteBtn.setVisibility(View.INVISIBLE);
                myViewHoldersThreads.get(position).editBtn.setVisibility(View.VISIBLE);
                myViewHoldersThreads.get(position).textPost.setClickable(false);
                myViewHoldersThreads.get(position).textPost.setFocusable(false);
                myViewHoldersThreads.get(position).textPost.setTextIsSelectable(false);
                myViewHoldersThreads.get(position).textPost.setSelectAllOnFocus(false);
                myViewHoldersThreads.get(position).textPost.setEnabled(false);
                myViewHoldersThreads.get(position).textPost.setFocusableInTouchMode(false);
                myViewHoldersThreads.get(position).textPost.clearFocus();
                myViewHoldersThreads.get(position).constraintLayout.requestFocus();
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }else{
                if(noPostsFlag){
                    clearData();
                }else{
                    myViewHoldersThreads.get(position).saveBtn.setVisibility(View.INVISIBLE);
                    myViewHoldersThreads.get(position).deleteBtn.setVisibility(View.INVISIBLE);
                    myViewHoldersThreads.get(position).editBtn.setVisibility(View.VISIBLE);
                    myViewHoldersThreads.get(position).textPost.setClickable(false);
                    myViewHoldersThreads.get(position).textPost.setFocusable(false);
                    myViewHoldersThreads.get(position).textPost.setTextIsSelectable(false);
                    myViewHoldersThreads.get(position).textPost.setSelectAllOnFocus(false);
                    myViewHoldersThreads.get(position).textPost.setEnabled(false);
                    myViewHoldersThreads.get(position).textPost.setFocusableInTouchMode(false);
                    myViewHoldersThreads.get(position).textPost.clearFocus();
                    myViewHoldersThreads.get(position).constraintLayout.requestFocus();
                }
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
