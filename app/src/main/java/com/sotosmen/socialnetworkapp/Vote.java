package com.sotosmen.socialnetworkapp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Vote {
    private Long id;
    private String user;
    private String thread;
}
