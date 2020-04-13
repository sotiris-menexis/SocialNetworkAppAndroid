package com.sotosmen.socialnetworkapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeScreenFragment extends Fragment {
    public View view;
    private boolean firstTime=true;
    public static TextView title;
    public static List<Friend> currentUserFriends = new ArrayList<>();
    public static Thread[] allThreads;
    public static List<Thread> userPermittedThreads = new ArrayList<Thread>();
    ImageView logOutBtn;
    ViewPager viewPager;
    TabLayout tabLayout;
    ArrayList<Fragment> fragments = new ArrayList<>();
    public static String prevFrag="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_screen_layout,container,false);
        new getAllThreads().execute();
        new setUpFriendsTask().execute();
        return view;
    }

    public void initializeView(){
        logOutBtn = view.findViewById(R.id.logOuBtnHomeScreen);
        tabLayout = view.findViewById(R.id.tabLayoutHomeScreen);
        viewPager = view.findViewById(R.id.viewPagerHomeScreen);
        fragments.add(new ForumFragment());
        fragments.add(new SearchFragment());
        fragments.add(new FriendFragment());
        viewPager.setAdapter(new HomeScreenFragAdapter(getFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ,getContext(),fragments));
        tabLayout.getTabAt(1).setCustomView(R.layout.tabitem_search);
        tabLayout.setupWithViewPager(viewPager);
        if(prevFrag==FragmentsNames.forumFrag){
            viewPager.setCurrentItem(0);
        }else if(prevFrag==FragmentsNames.friendFrag){
            viewPager.setCurrentItem(2);
        }else if(prevFrag==FragmentsNames.searchFrag){
            viewPager.setCurrentItem(1);
        }

    }

    public void setUpListeners(){
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
    }

    private class getAllThreads extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(5000);
            rf.setConnectTimeout(5000);
            String url = "http://192.168.1.3:8080/threads";
            try {
                ResponseEntity<Thread[]> responseEntity = restTemplate.getForEntity(url, Thread[].class);
                allThreads = responseEntity.getBody();
                userPermittedThreads.clear();
                for(int i=0;i<allThreads.length;i++){
                    if(MainActivity.currentUser.getType().equals(allThreads[i].getType())||allThreads[i].getType().equals("All")){
                        Thread temp = allThreads[i];
                        userPermittedThreads.add(temp);
                        Log.d("Threads List",String.valueOf(userPermittedThreads.size()));
                    }
                }
            } catch (HttpClientErrorException e) {
                Log.d("Shit",e.getMessage());
            } catch (Exception e){
                Log.d("Shit",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initializeView();
            setUpListeners();
        }
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
            currentUserFriends.clear();
            currentUserFriends.addAll(friendList);
        }
    }

}
