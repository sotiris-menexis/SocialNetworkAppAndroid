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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.SneakyThrows;

public class LogInFragment extends Fragment {
    public static View view;
    public static ProgressBar progressBar;
    public static TextView login_title;
    public static EditText username;
    public static EditText password;
    public static Button login;
    public static Button signup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.loginlayout,container,false);
        initializeView();
        setUpListeners();

        return view;
    }

    public void initializeView(){
        login = (Button) view.findViewById(R.id.login_btn_signup);
        signup = (Button) view.findViewById(R.id.signup_btn_signup);
        username = (EditText) view.findViewById(R.id.username_txt_sign);
        password = (EditText) view.findViewById(R.id.password_txt_sign);
        login_title = (TextView) view.findViewById(R.id.app_title_signup);
        progressBar = (ProgressBar) view.findViewById(R.id.login_progBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void setUpListeners(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.startscreen_btn_anim));
                new LogInTask().execute();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.startscreen_btn_anim));
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.startContainer,new SignUpFragment(),FragmentsNames.signUpFrag).commit();

            }
        });
    }

    public void showUI(){
        login.setVisibility(View.VISIBLE);
        signup.setVisibility(View.VISIBLE);
        username.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        login_title.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void hideUI() {
        login.setVisibility(View.INVISIBLE);
        signup.setVisibility(View.INVISIBLE);
        username.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);
        login_title.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private class LogInTask extends AsyncTask<Void,Void,Void> {
        String username = LogInFragment.username.getText().toString().trim();
        String password = LogInFragment.password.getText().toString().trim();
        String error = "";
        boolean result = false;
        @SneakyThrows
        @Override
        protected Void doInBackground(Void... voids) {

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(3000);
            rf.setConnectTimeout(3000);
            if(checkIfFiendEmpty()) {
                String url = "http://192.168.1.3:8080/login/"+username+"/"+password;
                Log.d("Url",url);
                try {
                    User user = restTemplate.getForObject(url,User.class);
                    if(user != null){
                        result = true;
                    }
                    MainActivity.currentUser = user;
                } catch (HttpClientErrorException e) {
                    error = e.getResponseBodyAsString();
                } catch (Exception e){
                    error = "Failed to Connect try again";
                    Log.d("Shit",e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(result==true){
                Toast.makeText(getContext(),"Sucessful.",Toast.LENGTH_SHORT).show();
                getFragmentManager().beginTransaction()
                        .replace(R.id.startContainer,new HomeScreenFragment(), FragmentsNames.homeScreenFrag).commit();
            }else
                Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
        }
        private boolean checkIfFiendEmpty(){
            if(username.isEmpty()){
                error = "Username is Empty";
                return false;
            }else if(password.isEmpty()){
                error = "Password is Empty";
                return false;
            }
            return true;
        }
    }

}