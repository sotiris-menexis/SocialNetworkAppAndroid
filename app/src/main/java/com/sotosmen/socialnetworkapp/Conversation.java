package com.sotosmen.socialnetworkapp;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Conversation {
    private Long id;
    private String creatorUser;
    private String receiverUser;
}
