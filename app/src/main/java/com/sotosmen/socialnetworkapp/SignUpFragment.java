package com.sotosmen.socialnetworkapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.SneakyThrows;


public class SignUpFragment extends Fragment {
    public static View view;
    public static ProgressBar progressBar;
    public static TextView signup_title;
    public static EditText username;
    public static EditText password;
    public static EditText repeatPassword;
    public static EditText firstName;
    public static EditText lastName;
    public static EditText regNum;
    public static EditText email;
    public static RadioGroup typeRadioGroup;
    public static Button signupBtn;
    public static Button loginBtn;
    public static ScrollView scrollView;
    public static String type="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signuplayout,container,false);
        initializeView();
        setUpListeners();
        return view;
    }
    public void initializeView(){
        scrollView = (ScrollView) view.findViewById(R.id.scrollViewSignUp);
        username = (EditText) view.findViewById(R.id.username_txt_sign);
        password = (EditText) view.findViewById(R.id.password_txt_sign);
        repeatPassword = (EditText) view.findViewById(R.id.repeat_password_txt);
        firstName = (EditText) view.findViewById(R.id.firstname_txt);
        lastName = (EditText) view.findViewById(R.id.lastname_txt);
        regNum = (EditText) view.findViewById(R.id.registration_num_txt);
        email = (EditText) view.findViewById(R.id.email_txt);
        typeRadioGroup = (RadioGroup) view.findViewById(R.id.type_radio_group);
        signupBtn = (Button) view.findViewById(R.id.signup_btn_signup);
        loginBtn = (Button) view.findViewById(R.id.login_btn_signup);
        signup_title = (TextView) view.findViewById(R.id.app_title_signup);
        progressBar = (ProgressBar) view.findViewById(R.id.signup_progBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void setUpListeners(){
        typeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.type_student:
                        type = "Student";
                        Log.d("Type", type);
                        break;
                    case R.id.type_professor:
                        type = "Professor";
                        Log.d("Type", type);
                        break;
                }
            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignUpTask().execute();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.startContainer,new LogInFragment(),FragmentsNames.logInFrag).commit();
            }
        });
    }
    public void showUI(){
        scrollView.setVisibility(View.VISIBLE);
        username.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        repeatPassword.setVisibility(View.VISIBLE);
        firstName.setVisibility(View.VISIBLE);
        lastName.setVisibility(View.VISIBLE);
        email.setVisibility(View.VISIBLE);
        regNum.setVisibility(View.VISIBLE);
        typeRadioGroup.setVisibility(View.VISIBLE);
        signupBtn.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
    public void hideUI(){
        scrollView.setVisibility(View.INVISIBLE);
        username.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);
        repeatPassword.setVisibility(View.INVISIBLE);
        firstName.setVisibility(View.INVISIBLE);
        lastName.setVisibility(View.INVISIBLE);
        email.setVisibility(View.INVISIBLE);
        regNum.setVisibility(View.INVISIBLE);
        typeRadioGroup.setVisibility(View.INVISIBLE);
        signupBtn.setVisibility(View.INVISIBLE);
        loginBtn.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private class SignUpTask extends AsyncTask<Void,Void,Void>{
        String username = SignUpFragment.username.getText().toString().trim();
        String password = SignUpFragment.password.getText().toString().trim();
        String repeatPassword = SignUpFragment.repeatPassword.getText().toString().trim();
        String firstName = SignUpFragment.firstName.getText().toString().trim();
        String lastName = SignUpFragment.lastName.getText().toString().trim();
        String email = SignUpFragment.email.getText().toString().trim();
        String regNum = SignUpFragment.regNum.getText().toString().trim();
        String type = SignUpFragment.type;
        String error = "Successful Sign Up";
        int error_num = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hideUI();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            showUI();
            if(error == "Successful Sign Up") {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.startContainer, new LogInFragment(), FragmentsNames.homeScreenFrag).commit();
            }
        }

        @SneakyThrows
        @Override
        protected Void doInBackground(Void... voids) {
            if(checkEmptyFields() && checkPassMatch()){
                SimpleDateFormat formatter=new SimpleDateFormat("E, MMM dd yyyy HH:mm:ss");
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.setType(type);
                user.setRegNum(regNum);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                Date timestamp = new Date();
                user.setTimestamp(timestamp);
                Log.d("User",user.toString());
                String urlPostUsers = "http://192.168.1.3:8080/users/";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<User> entity = new HttpEntity<User>(user,headers);
                Log.d("User json",entity.toString());
                ResponseEntity<String> responseEntity = null;
                try {
                    responseEntity = restTemplate.exchange(urlPostUsers, HttpMethod.POST, entity, String.class);
                    JSONObject jsonObject = new JSONObject(responseEntity.getBody());
                    User result = new User();
                    result.setUsername(jsonObject.getString("username"));
                    result.setPassword(jsonObject.getString("password"));
                    result.setEmail(jsonObject.getString("email"));
                    result.setType(jsonObject.getString("type"));
                    result.setRegNum(jsonObject.getString("regNum"));
                    result.setFirstName(jsonObject.getString("firstName"));
                    result.setLastName(jsonObject.getString("lastName"));
                    result.setTimestamp(formatter.parse(jsonObject.getString("timestamp")));
                }catch (HttpClientErrorException e) {
                    try {
                        JSONObject jsonObject = new JSONObject(e.getResponseBodyAsString());
                        JSONArray errors = jsonObject.getJSONArray("errors");
                        for (int i = 0; i < errors.length(); i++) {
                            JSONObject jsonObject1 = errors.getJSONObject(i);
                            if (!jsonObject1.getString("defaultMessage").isEmpty()) {
                                error = jsonObject1.getString("defaultMessage");
                                break;
                            }
                        }
                    }catch (Exception error){
                        error.getStackTrace();
                    }
                }catch (Exception e){

                }
            }
            return null;
        }

        private boolean checkEmptyFields(){
            boolean result = false;
            if(username.isEmpty()){
                error = "Username is Empty";
                error_num = 1;
            }else if(password.isEmpty()){
                error = "Password is Empty";
                error_num = 1;
            }else if(repeatPassword.isEmpty()){
                error = "Repeat Password is Empty";
                error_num = 2;
            }else if(email.isEmpty()){
                error = "Email is Empty";
                error_num = 3;
            }else if(firstName.isEmpty()){
                error = "First Name is Empty";
                error_num = 4;
            }else if(lastName.isEmpty()){
                error = "Last Name is Empty";
                error_num = 5;
            }else if(regNum.isEmpty()){
                error = "Registration Number is Empty";
                error_num = 6;
            }else if(type.isEmpty()){
                error = "No type is selected";
                error_num = 7;
            }else {
                result = true;
            }
            return result;
        }
        private boolean checkPassMatch(){
            boolean result = false;
            if(!password.equals(repeatPassword)){
                error = "Passwords don't match";
                error_num = 8;
            }else {
                result = true;
            }
            return result;
        }
    }
}
