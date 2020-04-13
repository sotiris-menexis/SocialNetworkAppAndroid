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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

public class EditThreadFrag extends Fragment {
    public static View view;
    private static int threadPos;
    private static String type = "";
    public static TextView appTitle;
    public static EditText threadDesc;
    public static Button editThreadBtn;
    public static Button cancelThreadBtn;
    public static RadioGroup threadTypeGroup;
    public static RadioButton currentUserTypeRadionButton;
    private String calledFromFrag="";
    public static ImageView logOutBtn;

    public EditThreadFrag(int threadPos,String calledFromFrag){
        this.threadPos = threadPos;
        this.calledFromFrag = calledFromFrag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_thread_layout,container,false);
        initializeView();
        setUpListeners();
        return view;
    }

    public void initializeView(){
        logOutBtn = view.findViewById(R.id.logOuBtnEditThread);
        appTitle = view.findViewById(R.id.app_title_editThread);
        currentUserTypeRadionButton = view.findViewById(R.id.threadTypeStudentProfEdit);
        currentUserTypeRadionButton.setText(MainActivity.currentUser.getType());
        threadDesc = view.findViewById(R.id.threadDescEditThreadText);
        editThreadBtn = view.findViewById(R.id.editBtnEditThread);
        cancelThreadBtn = view.findViewById(R.id.cancelBtnEditThread);
        threadTypeGroup = view.findViewById(R.id.threadTypeRadioGroupEdit);
    }
    public void setUpListeners(){
        threadTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.threadTypeStudentProfEdit:
                        type = MainActivity.currentUser.getType();
                        Log.d("Type", type);
                        break;
                    case R.id.threadTypeAllEdit:
                        type = "All";
                        Log.d("Type", type);
                        break;
                }
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
        editThreadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new createThreadTask().execute();
            }
        });
        cancelThreadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeScreenFragment.prevFrag = calledFromFrag;
                cancelThreadBtn.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.create_cancel_anim));
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.startContainer, new HomeScreenFragment(),FragmentsNames.homeScreenFrag).commit();
            }
        });
    }

    private class createThreadTask extends AsyncTask<Void,Void,Void> {
        private String url = "";
        private String result = "";
        private String threadDesc = EditThreadFrag.threadDesc.getText().toString().trim();
        private String type = EditThreadFrag.type;
        @Override
        protected Void doInBackground(Void... voids) {
            if (checkFieldEmpty()) {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
                SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                        .getRequestFactory();
                rf.setReadTimeout(3000);
                rf.setConnectTimeout(3000);
                CustomRecyclerAdapterForum.threads.get(threadPos).setDescription(threadDesc);
                CustomRecyclerAdapterForum.threads.get(threadPos).setType(type);
                url = "http://192.168.1.3:8080/threads";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Thread> entity = new HttpEntity<Thread>(CustomRecyclerAdapterForum.threads.get(threadPos),headers);
                try{
                    Log.d("Thread obj",CustomRecyclerAdapterForum.threads.get(threadPos).toString());
                    restTemplate.exchange(url, HttpMethod.PUT,entity,Thread.class);
                    result = "Successful";
                }catch (HttpClientErrorException e){
                    result = e.getResponseBodyAsString();
                }catch (Exception e){
                    result = "Something went wrong try again";
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
            editThreadBtn.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.create_cancel_anim));
            if(result == "Successful"){
                HomeScreenFragment.prevFrag = calledFromFrag;
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.startContainer,
                        new HomeScreenFragment(),FragmentsNames.homeScreenFrag).commit();
            }
        }

        private boolean checkFieldEmpty(){
            if(threadDesc.equals("")){
                result = "Thread Description cannot be empty";
                return false;
            }else {
                return true;
            }
        }

    }

}
