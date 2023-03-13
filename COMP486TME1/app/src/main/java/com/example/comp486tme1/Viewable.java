package com.example.comp486tme1;

import android.content.Context;
import android.view.SurfaceView;
import android.view.View;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Viewable (abstract; extends SurfaceView implements Runnable, View.OnTouchListener)
    Description: An abstract class which allows for multiple patterns of screens to be switched
    between by the GameView class. Everything here is self explanitory.

 */
public abstract class Viewable extends SurfaceView implements Runnable, View.OnTouchListener {
    public Viewable(Context context) {
        super(context);
    }
    public abstract void pause();
    public abstract void resume();
}
