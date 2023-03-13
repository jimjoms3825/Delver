package com.example.comp486tme1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Animator
    Description: A class which handles multiple Animation objects and provides a convienent
    interface for animation control.

 */
public class Animator {

    private Bitmap currentBitmap; //The current displayed sprite.
    private ArrayList<Animation> animations; // The list of all added animations.
    private Animation currentAnimation; // The currently playing animation.
    public float scale = 1; // The scale of all bitmaps being output.

    /*
    Inputs:

    Outputs:

    Called by:

    Calls:
     */
    public Animator (){
        animations = new ArrayList<Animation>();
    }

    /*
    Inputs: None

    Outputs: Adds an animation into the animation list. Sets it to current anim if one is not present.

    Called by: Animator.

    Calls: Bitmap.size()
     */
    public void addAnimation(Animation anim){
        animations.add(anim);
        if(currentAnimation == null){
            currentAnimation = anim;
        }
    }

    //Plays the animation with the specified name.
        /*
    Inputs: None

    Outputs: Adds an animation into the animation list. Sets it to current anim if one is not present.

    Called by: Animator.

    Calls: Bitmap.size()
     */
    public void playAnimation(String name){
        if(currentAnimation.NAME.equals(name) && !currentAnimation.oneShot){ return; } // Dont do anything if the animation is the same.
        for(Animation anim: animations){
            if (anim.NAME.equals(name)) {
                currentAnimation.release();
                currentAnimation = anim;
                return;
            }
        }
    }

    //Gets the bitmap from the current animation.
        /*
    Inputs: None

    Outputs: Adds an animation into the animation list. Sets it to current anim if one is not present.

    Called by: Animator.

    Calls: Bitmap.size()
     */
    public Bitmap getBitmap() {
        if(currentAnimation == null) { return null; }
        currentBitmap = currentAnimation.getNextFrame();
        return Bitmap.createScaledBitmap(currentBitmap, (int)(currentBitmap.getWidth() * scale),
                (int)(currentBitmap.getHeight() * scale), false);
    }

    //Returns the current animation.
        /*
    Inputs: None

    Outputs: Adds an animation into the animation list. Sets it to current anim if one is not present.

    Called by: Animator.

    Calls: Bitmap.size()
     */
    public Animation getAnimation(){
        return currentAnimation;
    }
}
