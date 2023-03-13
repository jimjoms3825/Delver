package com.example.comp486tme1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: GameActivity
    Description: An Activity class which allows for the instantiation of one of the Viewable classes.
    Functionally speaking, this class provides the framework that the custom game windows utilize.

 */
public class GameActivity extends Activity {

    private Viewable currentViewable;

    /*
    Inputs: SavedInstanceState (Activity required.)

    Outputs: None. Creates a view based on the value of MainActivity.

    Called by: Android system

    Calls: Creates the relevant view. Sets the activity.
    */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.VIEWS targetView = MainActivity.currentView;
        switch (targetView){
            case GameView:
                if(GameView.instance == null){
                    currentViewable = new GameView(this);
                }
                else{
                    currentViewable = GameView.instance;
                    ((ViewGroup)GameView.instance.getParent()).removeView(currentViewable);
                    GameView.instance.addPlayer();
                }
                break;
            case GameOverView:
                currentViewable = new EndView(this, false);
                break;
            case GameWonView:
                currentViewable = new EndView(this, true);
                break;
            case ControlsView:
                currentViewable = new ControlsView(this);
                break;
            case LoadingView:
                currentViewable = new LoadingView(this);
                break;
            case ShopView:
                currentViewable = new ShopView(this);
                break;
            case MainMenu:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
        }
        setContentView(currentViewable);
    }

    /*
    Inputs: None.

    Outputs: None. Pauses the current viewable.

    Called by: Android System

    Calls: currentViewable.Pause, Super.Pause.
    */
    @Override
    protected void onPause() {
        super.onPause();
        currentViewable.pause();
    }

    /*
    Inputs: None.

    Outputs: None. Resumes execution

    Called by: Android System

    Calls: Super.resume, currentViewable.resume.
    */
    @Override
    protected void onResume() {
        super.onResume();
        currentViewable.resume();
    }

}
