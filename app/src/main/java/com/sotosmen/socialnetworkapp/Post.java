package com.sotosmen.socialnetworkapp;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Post {
    private Long id;
    private String text;
    private Date timestamp;
    private String type;
    private String ownerThread;
    private String creatorUser;

}
