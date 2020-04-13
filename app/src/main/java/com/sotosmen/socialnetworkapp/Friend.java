package com.sotosmen.socialnetworkapp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Friend {
    private Long id;
    private String friendUser1;
    private String friendUser2;
    @JsonIgnore
    private User friendUserObj;
}
