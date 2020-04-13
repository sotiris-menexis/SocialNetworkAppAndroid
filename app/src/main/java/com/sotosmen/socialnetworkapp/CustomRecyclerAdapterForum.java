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
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

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

public class CustomRecyclerAdapterForum extends RecyclerView.Adapter<ViewHolder> {
    public static List<Thread> threads;
    private static final int THREADS_VIEW_IS_EMPTY=0;
    private static final int THREADS_VIEW_HAS_ITEMS=1;
    private int dropDownBtnPressed=0;
    Context context;
    ArrayList<MyViewHolderThread> myViewHolderThreads = new ArrayList<>();
    View view;
    ViewGroup parent;
    public CustomRecyclerAdapterForum(List<Thread> threads, Context context){
        this.threads = new ArrayList<>();
        this.threads.addAll(threads);
        Log.d("Threads size",""+this.threads.size());
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        if(viewType ==THREADS_VIEW_IS_EMPTY){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thread_item_empty,parent,false);
            MyViewHolderThreadEmpty myViewHolderThreadEmpty = new MyViewHolderThreadEmpty(view);
            return myViewHolderThreadEmpty;
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thread_item, parent, false);
            MyViewHolderThread myViewHolderThread = new MyViewHolderThread(view);
            myViewHolderThreads.add(myViewHolderThread);
            return myViewHolderThread;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if(viewType ==THREADS_VIEW_IS_EMPTY){
            MyViewHolderThreadEmpty myViewHolderThreadEmpty = (MyViewHolderThreadEmpty) holder;
        }else if(viewType ==THREADS_VIEW_HAS_ITEMS) {
            final MyViewHolderThread myViewHolderThread = (MyViewHolderThread) holder;
            myViewHolderThread.dropDownLayout.setVisibility(View.INVISIBLE);
            myViewHolderThread.dropDownBtn.setVisibility(View.INVISIBLE);
            myViewHolderThread.threadVotes.setText(String.valueOf(threads.get(position).getVotes()));
            myViewHolderThread.threadTitle.setText("Title: "+threads.get(position).getThreadName());
            myViewHolderThread.threadDesc.setText("Description: "+threads.get(position).getDescription());
            myViewHolderThread.threadCreatorUsername.setText("Created By: "+threads.get(position).getCreatorUser());
            myViewHolderThread.upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Position_1",String.valueOf(position));
                    Integer[] integers = {1,position};
                    new changeVotes().execute(integers);
                }
            });
            myViewHolderThread.downvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Position_1",String.valueOf(position));
                    Integer[] integers = {-1,position};
                    new changeVotes().execute(integers);

                }
            });
            myViewHolderThread.constraintLayoutThreadItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(threads.get(position).getType().equals("All")){
                        new checkThreadChangedTaskForum().execute(position);
                    }else {
                        ThreadLayoutFrag.currentThread = threads.get(position);
                        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.startContainer, new ThreadLayoutFrag(position, FragmentsNames.forumFrag), FragmentsNames.threadFrag).commit();
                    }
                }
            });
            if(threads.get(position).getCreatorUser().equals(MainActivity.currentUser.getUsername())) {
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
                                ,new EditThreadFrag(position,FragmentsNames.forumFrag),FragmentsNames.editThreadFrag).commit();
                    }
                });
                myViewHolderThread.deleteBtnDropDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(parent.getContext())
                                .setTitle("Title")
                                .setMessage("Do you really want to delete the Thread ?\n All posts related to the thread will\n also be deleted.")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        new deleteThreadTask().execute(Integer.valueOf(position));
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                });
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(threads.isEmpty()){
            Log.d("Wrong","call");
            return THREADS_VIEW_IS_EMPTY;
        }else {
            Log.d("Correct","call");
            return THREADS_VIEW_HAS_ITEMS;
        }
    }

    @Override
    public int getItemCount() {
        if(threads.isEmpty()){
            return 1;
        }else {
            return threads.size();
        }
    }

    private class changeVotes extends AsyncTask<Integer,Void,Integer>{
        String result="";
        boolean flag = false;
        @Override
        protected Integer doInBackground(Integer... integers) {
            if(integers[0] == 1){
                long votes = threads.get(integers[1]).getVotes()+1;
                Log.d("Votes",String.valueOf(votes));
                threads.get(integers[1]).setVotes(votes);
            }else if( integers[0] == -1){
                long votes = threads.get(integers[1]).getVotes()-1;
                Log.d("Votes",String.valueOf(votes));
                threads.get(integers[1]).setVotes(votes);
            }
            Vote vote = new Vote();
            vote.setThread(threads.get(integers[1]).getThreadName());
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
            RestTemplate restTemplate1 = new RestTemplate();
            restTemplate1.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate1.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf1 = (SimpleClientHttpRequestFactory) restTemplate1
                    .getRequestFactory();
            rf1.setReadTimeout(3000);
            rf1.setConnectTimeout(3000);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Thread> entity = new HttpEntity<Thread>(threads.get(integers[1]), headers);
            HttpEntity<Vote> entity1 = new HttpEntity<Vote>(vote,headers);
            Log.d("Thread", threads.get(integers[1]).getThreadName());
            try {
                restTemplate1.exchange(url1, HttpMethod.POST, entity1, Vote.class);
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
                myViewHolderThreads.get(position).threadVotes.setText(String.valueOf(threads.get(position).getVotes()));
            }else {
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class deleteThreadTask extends AsyncTask<Integer,Void,Void>{
        private int position;
        private String result="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(result=="Successful"){
                threads.remove(position);
                notifyItemRemoved(position);
                myViewHolderThreads.remove(position);
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            position = integers[0];
            String urlThread = "http://192.168.1.3:8080/threads/"+threads.get(position).getThreadName();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            try{
                restTemplate.delete(urlThread);
                result="Successful";
            }catch (HttpClientErrorException e){
                Log.d("Error_1_Del_Thread",e.getResponseBodyAsString());
                result="Something went wrong try again";
            }catch (Exception e){
                Log.d("Error_2_Del_Thread",e.getMessage());
                result="Something went wrong try again";
            }
            return null;
        }
    }

    private class checkThreadChangedTaskForum extends AsyncTask<Integer,Void,Integer>{
        private String result = "";
        @Override
        protected Integer doInBackground(Integer... integers) {
            String url = "http://192.168.1.3:8080/threads/"+threads.get(integers[0]).getThreadName();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(3000);
            rf.setConnectTimeout(3000);
            try{
                Thread thread = restTemplate.getForObject(url,Thread.class);
                if(thread.getType().equals(threads.get(integers[0]).getType())){
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
                ThreadLayoutFrag.currentThread = threads.get(integer);
                FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.startContainer,new ThreadLayoutFrag(integer,FragmentsNames.forumFrag),FragmentsNames.threadFrag).commit();
            }else if(result == "Changed"){
                result = "Action Forbidden";
                threads.remove(integer);
                notifyDataSetChanged();
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(parent.getContext(),result,Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void clearData(){
        threads.clear();
        notifyDataSetChanged();
    }
    public void updateData(List<Thread> newThreads){
        threads.addAll(newThreads);
        notifyDataSetChanged();
    }
}
