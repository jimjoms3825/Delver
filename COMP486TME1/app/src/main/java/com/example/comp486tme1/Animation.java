package com.example.comp486tme1;

import android.graphics.Bitmap;

import java.util.ArrayList;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Animation
    Description: A class which handles a single animation and the parameters needed to construct
    and run the animation consistently.

 */
public class Animation {

    public final String NAME; // For access.
    private int numberOfFrames; // Total Frames in provided Bitmap.
    public ArrayList<Bitmap> frames; // Frames in animation.
    public final int FRAME_SPEED; // How many update cycles before next frame is played.

    public int nextFrame = 0; // Index of next frame.
    private int frameTimer = 0; // Index of elapsed updates since last frame change.

    public boolean oneShot = false; // Will only play the animation once.
    public boolean finished = false; // Bool flag for telling when a one-shot is done.

    /*
    Inputs: String name - Sets the name of the animation.
    Bitmap image - The source spriteSheet.
    int start - The first frame (not 0-indexed).
    int finish - The final frame of the animation.
    int totalFrames - The total number of frames in the spritesheet (for slicing).
    int frameSpeed - The number of updates before animation will change frames.

    Outputs: None, Creates an animation from the provided bitmap with the specified settings.

    Called by: Enemies, Player, Weapon, Bosses, LoadingView, CoinPickup, WizardSpell.

    Calls: None
     */
    public Animation(String name, Bitmap image, int start, int finish, int totalFrames, int frameSpeed){
        NAME = name;
        numberOfFrames = finish - start + 1;
        FRAME_SPEED = frameSpeed;
        frames = new ArrayList<Bitmap>();
        int frameWidth = image.getWidth() / totalFrames;
        int frameHeight = image.getHeight();
        for(int i = start - 1; i < finish ; i++){
            Bitmap newBitmap = Bitmap.createBitmap(image, i * frameWidth, 0, frameWidth, frameHeight);
            frames.add(newBitmap);
        }
    }

    /*
    Inputs: String name - Sets the name of the animation.
    Bitmap image - The source spriteSheet.
    int start - The first frame (not 0-indexed).
    int finish - The final frame of the animation.
    int totalFrames - The total number of frames in the spritesheet (for slicing).
    int frameSpeed - The number of updates before animation will change frames.
    boolean oneShot - whether the animation is a oneshot animation.

    Outputs: None. Creates a one-shot animation from the provided bitmap with the specified settings.

    Called by: Mimic, Orc, Projectile, Tile, WizardSpell.

    Calls: None
     */
    public Animation(String name, Bitmap image, int start, int finish, int totalFrames, int frameSpeed, boolean oneShot){
        NAME = name;
        numberOfFrames = finish - start + 1;
        FRAME_SPEED = frameSpeed;
        frames = new ArrayList<Bitmap>();
        int frameWidth = image.getWidth() / totalFrames;
        int frameHeight = image.getHeight();
        for(int i = start - 1; i < finish ; i++){
            Bitmap newBitmap = Bitmap.createBitmap(image, i * frameWidth, 0, frameWidth, frameHeight);
            frames.add(newBitmap);
        }
        this.oneShot = oneShot;
    }

    // Resets the animation.
    /*
    Inputs: None

    Outputs: None. Resets the current animation to its first frame.

    Called by: Animator.

    Calls: None
     */
    public void release(){
        nextFrame = 0;
    }

    /*
    Inputs: None

    Outputs:  Returns the next appropriate frame based on animation parameters as a Bitmap.

    Called by: Animator.

    Calls: Bitmap.size()
     */
    public Bitmap getNextFrame(){
        Bitmap returnMap = frames.get(nextFrame);
        if(frameTimer++ >= FRAME_SPEED){
            nextFrame++;
            frameTimer = 0;
            if(nextFrame == numberOfFrames && !oneShot) {
                nextFrame = 0;
                //next if stops the animation if it is a one shot.
            } else if(nextFrame == numberOfFrames && oneShot){
                nextFrame = frames.size() - 1;
                finished = true;
            }
        }
        return returnMap;
    }
}
