package com.example.comp486tme1;

import androidx.annotation.Nullable;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: MainActivity
    Description: Creates the GameActivities. The Bootstrap program.

 */
public class MainActivity extends Activity{

    public enum VIEWS {MainMenu, GameView, GameOverView, GameWonView, ControlsView, ShopView, LoadingView}
    public static VIEWS currentView = VIEWS.MainMenu;
    public static MainActivity instance;

    /*
    Inputs: Bundle (unused)

    Outputs: None. Creates the main menu view.

    Called by: Android system.

    Calls: setContentView(), SoundManager.create().
    */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_main);
        final Button playButton = (Button)findViewById(R.id.playButton);
        final Button controlsButton = (Button)findViewById(R.id.controlsButtoin);
        final Button tutorialButton = (Button)findViewById(R.id.tutorialButton);
        SoundManager.create(this);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundManager.instance.playSound(SoundManager.Sounds.Coin);
                setView(VIEWS.LoadingView);
            }
        });
        controlsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundManager.instance.playSound(SoundManager.Sounds.Coin);
                setView(VIEWS.ControlsView);
            }
        });
        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundManager.instance.playSound(SoundManager.Sounds.Coin);
                GameView.floorsCleared = -1;
                setView(VIEWS.LoadingView);
            }
        });
        instance = this;
    }

    /*
    Inputs: one of the VIEWS defined in MainActivity to specify what to load.

    Outputs: None. Changes to the requested view.

    Called by: ControlsView, EndView, GameView, LoadingView, ShopButton, this.onCreate()

    Calls: Android intent methods.
    */
    public void setView(VIEWS newView){
        currentView = newView;
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        finish();
    }
}