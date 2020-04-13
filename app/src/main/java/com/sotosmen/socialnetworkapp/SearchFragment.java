package com.sotosmen.socialnetworkapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {
    View view;
    RadioGroup searchOptionsRadioGroup;
    RadioButton userBtnSearch;
    RadioButton threadBtnSearch;
    RadioButton postBtnSearch;
    Button searchBtn;
    EditText searchTxt;
    RecyclerView recyclerViewSearch;
    static String searchType="User";
    static SearchObject searchList = new SearchObject();
    CustomRecyclerAdapterSearch cra;
    public static int position =-1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_layout,container,false);
        initializeView();
        setUpListeners();
        return view;
    }
    public void initializeView(){
        searchOptionsRadioGroup = view.findViewById(R.id.radioGroupSearch);
        userBtnSearch = view.findViewById(R.id.userRadioBtnSearch);
        threadBtnSearch = view.findViewById(R.id.threadRadioBtnSearch);
        postBtnSearch = view.findViewById(R.id.postRadioBtnSearch);
        searchBtn = view.findViewById(R.id.searchBtn);
        searchTxt = view.findViewById(R.id.searchEditText);
        if(position != -1){
            recyclerViewSearch.scrollToPosition(position);
        }
    }
    public void setUpListeners(){
        searchOptionsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.userRadioBtnSearch){
                    searchType = "User";
                    Log.d("SearchType value",searchType);
                }else if(checkedId == R.id.threadRadioBtnSearch){
                    searchType = "Thread";
                    Log.d("SearchType value",searchType);
                }else if(checkedId == R.id.postRadioBtnSearch){
                    searchType = "Post";
                    Log.d("SearchType value",searchType);
                }
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBtn.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.search_btn_anim));
                new searchTask().execute(searchTxt.getText().toString().trim());
            }
        });
    }
    public boolean searchFieldNotEmpty(){
        if (searchTxt.getText().toString().trim()==""){
            return false;
        }else{
            return true;
        }
    }

    private class searchTask extends AsyncTask<String,Void,Void>{
        private String searchType = SearchFragment.searchType;
        private String url="";
        private String type = MainActivity.currentUser.getType();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchList.clearAllLists();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            cra = new CustomRecyclerAdapterSearch(searchList);
            recyclerViewSearch = view.findViewById(R.id.recyclerViewSearch);
            recyclerViewSearch.setHasFixedSize(true);
            recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerViewSearch.setItemAnimator(new DefaultItemAnimator());
            recyclerViewSearch.setAdapter(cra);
        }

        @Override
        protected Void doInBackground(String... strings) {
            if(searchType == "User"){
                Log.d("SearchType value",searchType);
                url = "http://192.168.1.3:8080/search/users/"+strings[0]+"/"+type;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                try{
                    ResponseEntity<User[]> result = restTemplate.getForEntity(url,User[].class);
                    User[] users = result.getBody();
                    for(User u:users){
                        if(!u.getUsername().equals(MainActivity.currentUser.getUsername())) {
                            boolean flag = true;
                            for(Friend f:HomeScreenFragment.currentUserFriends){
                                if(u.getUsername().equals(f.getFriendUser1())|| u.getUsername().equals(f.getFriendUser2())){
                                    flag = false;
                                }
                            }
                            if(flag) {
                                searchList.getUsers().add(u);
                            }
                        }
                    }
                }catch (HttpClientErrorException e){

                }catch (Exception e){

                }
            }else if(searchType == "Thread"){
                Log.d("SearchType value",searchType);
                url = "http://192.168.1.3:8080/search/threads/"+strings[0]+"/"+type;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                try{
                    ResponseEntity<Thread[]> result = restTemplate.getForEntity(url,Thread[].class);
                    Thread[] threads = result.getBody();
                    for(Thread t:threads){
                        searchList.getThreads().add(t);
                    }
                }catch (HttpClientErrorException e){

                }catch (Exception e){

                }
                Log.d("ThreadsList",searchList.threads.toString());
            }else {
                Log.d("SearchType value",searchType);
                url = "http://192.168.1.3:8080/search/posts/"+strings[0]+"/"+type;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                try{
                    ResponseEntity<Post[]> result = restTemplate.getForEntity(url,Post[].class);
                    Post[] posts = result.getBody();
                    for(Post p:posts){
                        searchList.getPosts().add(p);
                    }
                }catch (HttpClientErrorException e){

                }catch (Exception e){

                }
            }
            return null;
        }
    }

}
