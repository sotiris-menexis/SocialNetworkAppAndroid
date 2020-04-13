package com.sotosmen.socialnetworkapp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class User {
    private String username;
    private String password;
    private String email;
    private String type;
    private String regNum;
    private String firstName;
    private String lastName;
    private Date timestamp;
}