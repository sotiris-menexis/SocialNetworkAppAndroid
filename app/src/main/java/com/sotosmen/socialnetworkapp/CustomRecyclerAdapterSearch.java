package com.sotosmen.socialnetworkapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class CustomRecyclerAdapterSearch extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Getter@Setter
    SearchObject searchObject;
    View view;
    ViewGroup parent;
    Context context;
    private static final int VIEW_TYPE_EMPTY=0;
    private static final int VIEW_TYPE_USER=1;
    private static final int VIEW_TYPE_THREAD=2;
    private static final int VIEW_TYPE_POSTS=3;
    List<MyViewHolderPost> myViewHoldersPosts = new ArrayList<>();
    List<MyViewHolderThread> myViewHolderThreads = new ArrayList<>();
    List<MyViewHolderUser> myViewHolderUsers = new ArrayList<>();
    private int dropDownBtnPressed=0;

    public CustomRecyclerAdapterSearch(SearchObject searchObject){
        if(searchObject!=null) {
            this.searchObject = searchObject;
        }else{
            this.searchObject = new SearchObject();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        this.context = parent.getContext();
        if(viewType == VIEW_TYPE_EMPTY){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_empty,parent,false);
            MyViewHolderSearchEmpty myViewHolderSearchEmpty = new MyViewHolderSearchEmpty(view);
            return myViewHolderSearchEmpty;
        }else if(viewType == VIEW_TYPE_USER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_search,parent,false);
            MyViewHolderUser myViewHolderUser = new MyViewHolderUser(view);
            myViewHolderUsers.add(myViewHolderUser);
            return myViewHolderUser;
        }else if(viewType == VIEW_TYPE_THREAD){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thread_item,parent,false);
            MyViewHolderThread myViewHolderThread = new MyViewHolderThread(view);
            myViewHolderThreads.add(myViewHolderThread);
            return myViewHolderThread;
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
            MyViewHolderPost myViewHolderPost = new MyViewHolderPost(view,parent.getContext());
            myViewHoldersPosts.add(myViewHolderPost);
            return myViewHolderPost;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder,final int position) {
        if(holder.getItemViewType()==VIEW_TYPE_EMPTY){
            MyViewHolderSearchEmpty myViewHolderSearchEmpty = (MyViewHolderSearchEmpty) holder;
        }else if(holder.getItemViewType()==VIEW_TYPE_USER){
            final MyViewHolderUser myViewHolderUser = (MyViewHolderUser) holder;
            String username = "Username: "+searchObject.getUsers().get(position).getUsername();
            myViewHolderUser.usernameTxt.setText(username);
            String firstLastName = "Firstname: "+searchObject.getUsers().get(position).getFirstName()+"\n"+
                    "Lastname: "+searchObject.getUsers().get(position).getLastName();
            myViewHolderUser.firstLastNameTxt.setText(firstLastName);
            myViewHolderUser.profPicTxt.setText(searchObject.getUsers().get(position).getUsername().substring(0,1));
            myViewHolderUser.addFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new addFriendTask().execute(Integer.valueOf(position));
                }
            });
        }else if(holder.getItemViewType()==VIEW_TYPE_THREAD){
            final MyViewHolderThread myViewHolderThread = (MyViewHolderThread) holder;
            myViewHolderThread.dropDownLayout.setVisibility(View.INVISIBLE);
            myViewHolderThread.dropDownBtn.setVisibility(View.INVISIBLE);
            myViewHolderThread.threadVotes.setText(String.valueOf(searchObject.threads.get(position).getVotes()));
            myViewHolderThread.threadTitle.setText("Title: "+searchObject.threads.get(position).getThreadName());
            myViewHolderThread.threadDesc.setText("Description: "+searchObject.threads.get(position).getDescription());
            myViewHolderThread.threadCreatorUsername.setText("Created By: "+searchObject.threads.get(position).getCreatorUser());
            myViewHolderThread.upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Position_1",String.valueOf(position));
                    Integer[] integers = {1,position};
                    new changeVotesSearch().execute(integers);
                }
            });
            myViewHolderThread.downvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Position_1",String.valueOf(position));
                    Integer[] integers = {-1,position};
                    new changeVotesSearch().execute(integers);

                }
            });
            myViewHolderThread.constraintLayoutThreadItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(searchObject.getThreads().get(position).getType().equals("All")){
                        new checkThreadChangedTaskSearch().execute(position);
                    }else {
                        ThreadLayoutFrag.currentThread = searchObject.getThreads().get(position);
                        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.startContainer, new ThreadLayoutFrag(position, FragmentsNames.forumFrag), FragmentsNames.threadFrag).commit();
                    }
                }
            });
            if(searchObject.threads.get(position).getCreatorUser().equals(MainActivity.currentUser.getUsername())) {
                myViewHolderThread.dropDownBtn.setVisibility(View.VISIBLE);
                myViewHolderThread.dropDownBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(dropDownBtnPressed ==0) {
                            myViewHolderThread.dropDownBtn
                                    .startAnimation(AnimationUtils.loadAnimation(parent.getContext(), R.anim.dropdownbtn_anim));
                            myViewHolderThread.dropDownLayout
                                    .startAnimation(AnimationUtils.loadAnimation(parent.getContext(), R.anim.slide_down_anim));
                            myViewHolderThread.dropDownLayout.setVisibility(View.VISIBLE);
                            dropDownBtnPressed = 1;
                        }else if(dropDownBtnPressed ==1){
                            myViewHolderThread.dropDownBtn
                                    .startAnimation(AnimationUtils.loadAnimation(parent.getContext(), R.anim.dropdownbtn_anim));
                            myViewHolderThread.dropDownLayout
                                    .startAnimation(AnimationUtils.loadAnimation(parent.getContext(),R.anim.slide_up_anim));
                            myViewHolderThread.dropDownLayout.setVisibility(View.INVISIBLE);
                            dropDownBtnPressed = 0;
                        }
                    }
                });
                myViewHolderThread.editBtnDropDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.startContainer
                                ,new EditThreadFrag(position,FragmentsNames.searchFrag),FragmentsNames.editThreadFrag).commit();
                    }
                });
            }
        }else{
            final MyViewHolderPost myViewHolderPost = (MyViewHolderPost) holder;
            myViewHolderPost.profileImgPost.setText(searchObject.posts.get(position).getCreatorUser().substring(0,1));
            myViewHolderPost.textPost.setText(searchObject.posts.get(position).getText());
            myViewHolderPost.timestampPost.setText(searchObject.posts.get(position).getTimestamp().toString());
            myViewHolderPost.creatorUserPost.setText("POSTED BY: "+searchObject.posts.get(position).getCreatorUser());
            myViewHolderPost.saveBtn.setVisibility(View.INVISIBLE);
            myViewHolderPost.deleteBtn.setVisibility(View.INVISIBLE);
            Log.d("Posts","Things go right");
            if(MainActivity.currentUser.getUsername().equals(searchObject.posts.get(position).getCreatorUser())){
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
                            searchObject.posts.get(position).setText(myViewHolderPost.textPost.getText().toString().trim());
                            new updatePostTaskSearch().execute(Integer.valueOf(position));
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
                                        new deletePostTaskSearch().execute(Integer.valueOf(position));
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
        if(searchObject.listType()==0){
            return VIEW_TYPE_EMPTY;
        }else if(searchObject.listType()==1){
            return VIEW_TYPE_USER;
        }else if(searchObject.listType()==2){
            return VIEW_TYPE_THREAD;
        }else{
            return VIEW_TYPE_POSTS;
        }
    }

    @Override
    public int getItemCount() {
        if(searchObject.listType()==0){
            return 1;
        }else if(searchObject.listType()==1){
            return searchObject.getUsers().size();
        }else if(searchObject.listType()==2){
            return searchObject.getThreads().size();
        }else{
            return searchObject.getPosts().size();
        }
    }
    public void clearData(){
        searchObject.clearAList(searchObject.listType());
        notifyDataSetChanged();
    }

    private class updatePostTaskSearch extends AsyncTask<Integer,Void,Integer> {
        private String url="";
        private String result="";
        private Post[] newPosts;
        @Override
        protected Integer doInBackground(Integer... integers) {
            url = "http://192.168.1.3:8080/posts";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Post> entity = new HttpEntity<>(searchObject.posts.get(integers[0]),headers);
            try{
                Log.d("Posts",entity.toString());
                restTemplate.exchange(url, HttpMethod.PUT,entity,Post.class);
                result = "Successful";
            }catch (HttpClientErrorException e){
                Log.d("Posts","Error 1 :" +e.getResponseBodyAsString());
                result = e.getResponseBodyAsString();
            }catch (Exception e){
                Log.d("Posts","Error 1 :" +e.getMessage());
                result = "Something went wrong try again";
            }
            if(result == "Successful"){
                url = "http://192.168.1.3:8080/threads/"+searchObject.posts.get(integers[0]).getOwnerThread()+"/posts";
                RestTemplate restTemplate1 = new RestTemplate();
                restTemplate1.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                try {
                    ResponseEntity<Post[]> responseEntity = restTemplate1.getForEntity(url, Post[].class);
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
                searchObject.posts.addAll(temp);
                notifyItemChanged(position);
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }
            myViewHoldersPosts.get(position).saveBtn.setVisibility(View.INVISIBLE);
            myViewHoldersPosts.get(position).deleteBtn.setVisibility(View.INVISIBLE);
            myViewHoldersPosts.get(position).editBtn.setVisibility(View.VISIBLE);
            myViewHoldersPosts.get(position).textPost.setClickable(false);
            myViewHoldersPosts.get(position).textPost.setFocusable(false);
            myViewHoldersPosts.get(position).textPost.setFocusableInTouchMode(false);
            myViewHoldersPosts.get(position).textPost.clearFocus();
            myViewHoldersPosts.get(position).constraintLayout.requestFocus();
        }
    }
    private class deletePostTaskSearch extends AsyncTask<Integer,Void,Integer>{
        private String url="";
        private String result="";
        private Post[] newPosts;
        private boolean noPostsFlag = false;
        @Override
        protected Integer doInBackground(Integer... integers) {
            url = "http://192.168.1.3:8080/post/"+searchObject.posts.get(integers[0]).getId();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Post> entity = new HttpEntity<>(searchObject.posts.get(integers[0]),headers);
            try{
                Log.d("Posts",entity.toString());
                restTemplate.delete(url);
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
                url = "http://192.168.1.3:8080/threads/"+searchObject.posts.get(integers[0]).getOwnerThread()+"/posts";
                RestTemplate restTemplate1 = new RestTemplate();
                restTemplate1.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                try {
                    ResponseEntity<Post[]> responseEntity = restTemplate1.getForEntity(url, Post[].class);
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
                searchObject.setPosts(temp);
                myViewHoldersPosts.get(position).saveBtn.setVisibility(View.INVISIBLE);
                myViewHoldersPosts.get(position).deleteBtn.setVisibility(View.INVISIBLE);
                myViewHoldersPosts.get(position).editBtn.setVisibility(View.VISIBLE);
                myViewHoldersPosts.get(position).textPost.setClickable(false);
                myViewHoldersPosts.get(position).textPost.setFocusable(false);
                myViewHoldersPosts.get(position).textPost.setTextIsSelectable(false);
                myViewHoldersPosts.get(position).textPost.setSelectAllOnFocus(false);
                myViewHoldersPosts.get(position).textPost.setEnabled(false);
                myViewHoldersPosts.get(position).textPost.setFocusableInTouchMode(false);
                myViewHoldersPosts.get(position).textPost.clearFocus();
                myViewHoldersPosts.get(position).constraintLayout.requestFocus();
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }else{
                if(noPostsFlag){
                    clearData();
                }else{
                    myViewHoldersPosts.get(position).saveBtn.setVisibility(View.INVISIBLE);
                    myViewHoldersPosts.get(position).deleteBtn.setVisibility(View.INVISIBLE);
                    myViewHoldersPosts.get(position).editBtn.setVisibility(View.VISIBLE);
                    myViewHoldersPosts.get(position).textPost.setClickable(false);
                    myViewHoldersPosts.get(position).textPost.setFocusable(false);
                    myViewHoldersPosts.get(position).textPost.setTextIsSelectable(false);
                    myViewHoldersPosts.get(position).textPost.setSelectAllOnFocus(false);
                    myViewHoldersPosts.get(position).textPost.setEnabled(false);
                    myViewHoldersPosts.get(position).textPost.setFocusableInTouchMode(false);
                    myViewHoldersPosts.get(position).textPost.clearFocus();
                    myViewHoldersPosts.get(position).constraintLayout.requestFocus();
                }
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class changeVotesSearch extends AsyncTask<Integer,Void,Integer>{
        String result="";
        boolean flag = false;
        @Override
        protected Integer doInBackground(Integer... integers) {
            if(integers[0] == 1){
                long votes = searchObject.threads.get(integers[1]).getVotes()+1;
                Log.d("Votes",String.valueOf(votes));
                searchObject.threads.get(integers[1]).setVotes(votes);
            }else if( integers[0] == -1){
                long votes = searchObject.threads.get(integers[1]).getVotes()-1;
                Log.d("Votes",String.valueOf(votes));
                searchObject.threads.get(integers[1]).setVotes(votes);
            }
            Vote vote = new Vote();
            vote.setThread(searchObject.threads.get(integers[1]).getThreadName());
            vote.setUser(MainActivity.currentUser.getUsername());
            String url = "http://192.168.1.3:8080/threads";
            String url1 = "http://192.168.1.3:8080/votes";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(3000);
            rf.setConnectTimeout(3000);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Thread> entity = new HttpEntity<Thread>(searchObject.threads.get(integers[1]), headers);
            HttpEntity<Vote> entity1 = new HttpEntity<Vote>(vote,headers);
            Log.d("Thread", searchObject.threads.get(integers[1]).getThreadName());
            try {
                restTemplate.exchange(url1, HttpMethod.POST, entity1, Vote.class);
                restTemplate.exchange(url, HttpMethod.PUT, entity, Thread.class);
                result = "Successful vote";
                flag = true;
            } catch (HttpClientErrorException e) {
                Log.d("Exception_1",e.getResponseBodyAsString());
                result = e.getResponseBodyAsString();
            } catch (Exception e) {
                Log.d("Exception_2",e.getMessage());
                result = "Something went wrong try again";
            }
            return integers[1];
        }

        @Override
        protected void onPostExecute(Integer position) {
            super.onPostExecute(position);
            if(flag){
                Log.d("Position",String.valueOf(position));
                myViewHolderThreads.get(position).threadVotes.setText(String.valueOf(searchObject.threads.get(position).getVotes()));
            }else {
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class addFriendTask extends AsyncTask<Integer,Void,Void>{
        private String result = "";
        private int position;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            position = integers[0];
            String url = "http://192.168.1.3:8080/friendrequest";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(3000);
            rf.setConnectTimeout(3000);
            HttpHeaders headers = new HttpHeaders();
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setFromUserId(MainActivity.currentUser.getUsername());
            friendRequest.setToUserId(searchObject.getUsers().get(position).getUsername());
            HttpEntity<FriendRequest> entity = new HttpEntity<>(friendRequest,headers);
            try{
                restTemplate.exchange(url,HttpMethod.POST,entity,FriendRequest.class);
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
                Friend friend = new Friend();
                friend.setFriendUser1(searchObject.getUsers().get(position).getUsername());
                HomeScreenFragment.currentUserFriends.add(friend);
                searchObject.getUsers().remove(position);
                notifyItemRemoved(position);
            }
            Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
        }
    }
    private class checkThreadChangedTaskSearch extends AsyncTask<Integer,Void,Integer>{
        private String result = "";
        @Override
        protected Integer doInBackground(Integer... integers) {
            String url = "http://192.168.1.3:8080/threads/"+searchObject.getThreads().get(integers[0]).getThreadName();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(3000);
            rf.setConnectTimeout(3000);
            try{
                Thread thread = restTemplate.getForObject(url,Thread.class);
                if(thread.getType().equals(searchObject.getThreads().get(integers[0]).getType())){
                    result = "Unchanged";
                }else{
                    result = "Changed";
                }
            }catch (HttpClientErrorException e){
                result = e.getResponseBodyAsString();
            }catch (Exception e){
                result = e.getMessage();
            }
            return integers[0];
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(result == "Unchanged"){
                ThreadLayoutFrag.currentThread = searchObject.getThreads().get(integer);
                FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.startContainer,new ThreadLayoutFrag(integer,FragmentsNames.forumFrag),FragmentsNames.threadFrag).commit();
            }else if(result == "Changed"){
                result = "Action Forbidden";
                searchObject.getThreads().remove(integer);
                notifyDataSetChanged();
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }
        }
    }

}
