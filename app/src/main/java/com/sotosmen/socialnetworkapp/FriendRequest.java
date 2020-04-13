package com.sotosmen.socialnetworkapp;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FriendRequest {
    private Long friend_request_id;
    private String fromUserId;
    private String toUserId;
}
