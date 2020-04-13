package com.sotosmen.socialnetworkapp;

import com.fasterxml.jackson.annotation.JsonIgnore;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Thread {
    private String threadName;
    private String creatorUser;
    private String description;
    private Date timestamp;
    private long votes;
    private String type;
}
