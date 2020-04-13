package com.sotosmen.socialnetworkapp;


import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Message {
    private Long id;
    private String text;
    private String senderUser;
    private String receiverUser;
    private Long ownerConversation;
    private Date timestamp;

    public String json(){
        return new Gson().toJson(this);
    }
}