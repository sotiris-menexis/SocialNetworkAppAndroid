package com.sotosmen.socialnetworkapp;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.functions.Action;
import lombok.SneakyThrows;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompMessage;

public class MessengerFragment extends Fragment {
    public static View view;
    StompClient stompClient;
    public static CustomRecyclerAdapterMessenger cra;
    public static RecyclerView recyclerViewMessenger;
    public static TextView textView;
    public static TextView userTxt;
    public static Button backBtn;
    public static Button sendBtn;
    public static ImageView logOutBtn;
    private int position;
    private String friendUsername;
    private Conversation currentConversation = new Conversation();

    public MessengerFragment(int position, String friendUsername){
        this.position = position;
        this.friendUsername = friendUsername;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.messenger_layout,container,false);
        new setUpConversationTask().execute();
        return view;
    }
    public void initializeView(){
        logOutBtn = view.findViewById(R.id.logOuBtnMessenger);
        backBtn  = view.findViewById(R.id.backBtnMessenger);
        textView = view.findViewById(R.id.edittext_chatbox);
        sendBtn = view.findViewById(R.id.button_chatbox_send);
        userTxt = view.findViewById(R.id.friendUsernameMessenger);
        userTxt.setText(friendUsername);
    }

    public void setUpListeners(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeScreenFragment.prevFrag = FragmentsNames.friendFrag;
                getFragmentManager().beginTransaction()
                        .replace(R.id.startContainer,new HomeScreenFragment(),FragmentsNames.homeScreenFrag).commit();
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
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBtn.setClickable(false);
                new sendMessageTask().execute();
            }
        });
    }

    private class setUpConversationTask extends AsyncTask<Void,Void,Void>{
        private Conversation conversation = new Conversation();
        private String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("FriendUsername",friendUsername);
            String url = "http://192.168.1.3:8080/conversations/"+MainActivity.currentUser.getUsername()+"/"+friendUsername;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            try{
                conversation = restTemplate.getForObject(url,Conversation.class);
                result = "Successful";
            }catch (HttpClientErrorException e){
                result = e.getResponseBodyAsString();
                Log.d("Error_try_1_1",result);
                Log.d("STATUSCODE",e.getStatusCode().toString());
                if(e.getStatusCode().toString().equals("404")){
                    url = "http://192.168.1.3:8080/conversations";
                    conversation.setCreatorUser(MainActivity.currentUser.getUsername());
                    conversation.setReceiverUser(friendUsername);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<Conversation> entity = new HttpEntity<>(conversation,headers);
                    try{
                        restTemplate.exchange(url, HttpMethod.POST,entity,Conversation.class);
                        try{
                            url = "http://192.168.1.3:8080/conversations/"+MainActivity.currentUser.getUsername()+"/"+friendUsername;
                            conversation = restTemplate.getForObject(url,Conversation.class);
                            result = "Successful";
                        }catch (HttpClientErrorException e1){
                            result = e1.getResponseBodyAsString();
                            Log.d("Error_try_3_1",result);
                        }catch (Exception e1){
                            result = e1.getMessage();
                            Log.d("Error_try_3_2",result);
                        }
                    }catch (HttpClientErrorException e2){
                        result = e2.getResponseBodyAsString();
                        Log.d("Error_try_2_1",result);
                    }catch (Exception e2){
                        result = e2.getMessage();
                        Log.d("Error_try_2_2",result);
                    }
                }
            }catch (Exception e){
                Log.d("Error_try_1_2",result);
                result = "Something went wrong try again";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            currentConversation = conversation;
            Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
            if(result=="Successful") {
                new messengerTask().execute();
            }
            initializeView();
            setUpListeners();
            super.onPostExecute(aVoid);
        }
    }

    private class messengerTask extends AsyncTask<Void,Void,Void>{
        private List<Message> tempMessageList = new ArrayList<>();
        @SuppressLint("CheckResult")
        @Override
        protected Void doInBackground(Void... voids) {
            String url1 = "http://192.168.1.3:8080/conversations/"+currentConversation.getId()+"/messages";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(3000);
            rf.setConnectTimeout(3000);
            try{
                ResponseEntity<Message[]> entity = restTemplate.getForEntity(url1,Message[].class);
                Message[] messages = entity.getBody();
                for(Message m:messages){
                    tempMessageList.add(m);
                }
                Log.d("Fine","Everything is going well.");
            }catch (HttpClientErrorException e){

            }catch (Exception e){

            }
            stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "http://192.168.1.3:8080/chat/websocket");
            stompClient.withServerHeartbeat(10000);
            stompClient.connect();
            String url = "/topic/" + currentConversation.getId().toString();
            stompClient.topic(url).subscribe(topicMessage -> {
                Log.d("MessageBack", topicMessage.getPayload());
                Message message = new Message();
                JSONObject jsonObject = new JSONObject(topicMessage.getPayload());
                try{
                    message.setSenderUser(jsonObject.getString("senderUser"));
                    message.setReceiverUser(jsonObject.getString("receiverUser"));
                    Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(jsonObject.getString("timestamp"));
                    message.setTimestamp(calendar.getTime());
                    message.setOwnerConversation(jsonObject.getLong("ownerConversation"));
                    message.setText(jsonObject.getString("text"));
                    Log.d("Timestamp",message.getTimestamp().toString());
                }catch (JSONException e){

                }catch (Exception e){

                }
                cra.addItem(message);
            });
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerViewMessenger = view.findViewById(R.id.recyclerViewMessenger);
            cra = new CustomRecyclerAdapterMessenger(tempMessageList);
            recyclerViewMessenger.setAdapter(cra);
            recyclerViewMessenger.setHasFixedSize(true);
            LinearLayoutManager mManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            mManager.setStackFromEnd(true);
            recyclerViewMessenger.setLayoutManager(mManager);
            recyclerViewMessenger.setItemAnimator(new DefaultItemAnimator());
            cra.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    mManager.smoothScrollToPosition(recyclerViewMessenger, null, cra.getItemCount());
                }
            });
        }
    }

    private class sendMessageTask extends AsyncTask<Void,Void,Void>{
        private String text = textView.getText().toString().trim();
        private String error = "Successful";
        @SneakyThrows
        @Override
        protected Void doInBackground(Void... voids) {
            if(!text.equals("")) {
                Message msg = new Message();
                msg.setText(text);
                msg.setReceiverUser(friendUsername);
                msg.setSenderUser(MainActivity.currentUser.getUsername());
                msg.setOwnerConversation(currentConversation.getId());
                msg.setTimestamp(new Date());
                stompClient.send("/app/messenger", msg.json()).subscribe();
            }else {
                error = "Message can't be empty";
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
            textView.setText("");
            sendBtn.setClickable(true);
        }
    }

}
