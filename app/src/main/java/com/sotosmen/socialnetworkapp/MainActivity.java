package com.sotosmen.socialnetworkapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;


public class MainActivity extends AppCompatActivity {
    public static User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(savedInstanceState == null) {
            fragmentManager.
                    beginTransaction().
                    replace(R.id.startContainer
                            ,new LogInFragment()
                            ,FragmentsNames.logInFrag).commit();
        }
    }

    @Override
    public void onBackPressed(){

    }

}
