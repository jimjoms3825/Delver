package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.util.ArrayList;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 3)
    Name: James Bombardier
    Date: October 7, 2022

    Class: Ogre (extends Enemy)
    Description: A large enemy that moves with LERP calls.

 */
public class Ogre extends Enemy{
    Animator animator;

    /*
    Inputs: None

    Outputs: None. Initializes the enemies stats and animator.

    Called by: constructor.

    Calls: super.initialize()
    */
    @Override
    public void initialize(){
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.ogre);
        strength = 20;
        maximumMoveSpeed = 6;
        speed = 6;
        maxHealth = 40;
        knockBackStrength = 100;
        animator = new Animator();
        animator.addAnimation(new Animation("Idle", spriteSheet, 5, 8, 8, 12));
        animator.addAnimation(new Animation("Run", spriteSheet, 1, 4, 8, 5));
        health = maxHealth;
        this.lootOnDeath = 30;
        super.initialize();
    }

    /*
    Inputs: None.

    Outputs: None. Handles the update logic.

    Called by: GameView loop.

    Calls: think(), move()
    */
    @Override
    public void update() {
        think();
        move();
    }

    /*
    Inputs: None.

    Outputs: Returns a flipped Bitmap based on velocity.

    Called by: GameView loop.

    Calls: none.
    */
    @Override
    public Bitmap getDrawable() {
        Matrix matrix = new Matrix();
        Bitmap source = animator.getBitmap();
        if(velocity.x < 0){
            matrix.preScale(-1, 1);
        }
        lastDrawable = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
        return lastDrawable;
    }

    /*
    Inputs: None.

    Outputs: None. Simple following mechanic with a linear interpolation twist to make the enemy feel more sluggish.

    Called by: update()

    Calls: Vector logic.
    */
    private void think(){
        if(Player.instance == null) { return; }
        Vector2 newVel = Player.instance.getCenter();
        newVel.subtract(getCenter());
        newVel.normalize();
        newVel.scale(maximumMoveSpeed);
        velocity.Lerp(newVel, 0.015f);
    }
}
