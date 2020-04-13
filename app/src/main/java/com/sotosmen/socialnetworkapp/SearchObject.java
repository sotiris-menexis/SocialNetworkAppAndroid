package com.sotosmen.socialnetworkapp;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class SearchObject {
    List<User> users = new ArrayList<>();
    List<Thread> threads = new ArrayList<>();
    List<Post> posts = new ArrayList<>();

    public int listType(){
        if(!users.isEmpty()){
            return 1;
        }else if(!threads.isEmpty()){
            return 2;
        }else if(!posts.isEmpty()){
            return 3;
        }else{
            return 0;
        }
    }
    public void clearAllLists(){
        users.clear();
        threads.clear();
        posts.clear();
    }
    public void clearAList(int listType){
        if(listType==1){
            users.clear();
        }else if(listType==2){
            threads.clear();
        }else if(listType==3){
            posts.clear();
        }
    }

}
