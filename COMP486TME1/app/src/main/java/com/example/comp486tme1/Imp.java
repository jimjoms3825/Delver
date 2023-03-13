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

    Class: Imp (extends Enemy)
    Description: A rather weak early game enemy that uses A* pathfinding.

 */

public class Imp extends Enemy{

    Animator animator;
    private ArrayList<Vector2> pathList;

    /*
    Inputs: None.

    Outputs: None. Initializes the enemies stats and animator.

    Called by: Constructor

    Calls: Super.initialize.
    */
    @Override
    public void initialize(){
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.imp);
        strength = 10;
        maximumMoveSpeed = 4;
        speed = 4;
        maxHealth = 15;
        knockBackStrength = 10;
        animator = new Animator();
        animator.scale = 0.7f;
        animator.addAnimation(new Animation("Idle", spriteSheet, 5, 7, 7, 12));
        animator.addAnimation(new Animation("Run", spriteSheet, 1, 4, 7, 5));
        health = maxHealth;
        pathList = new ArrayList<Vector2>();
        this.lootOnDeath = 20;
        super.initialize();
    }

    /*
    Inputs: None.

    Outputs: None. Controls the update logic of the enemy.

    Called by: GameView main loop

    Calls: think(), move()
    */
    @Override
    public void update() {
        think();
        move();
    }

    /*
    Inputs: None.

    Outputs: Returns a Bitmap from the Animator flipped based on velocity.

    Called by: GameView loop

    Calls: Animator.getBitmap()
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

    Outputs: None. Handles the call to the main pathfinding system.

    Called by: update()

    Calls: Pathfinder.getPath()
    */
    private void think(){
        if(Player.instance == null) { return; }
        //Follows the player in a straight line if they are very close.
        if(Vector2.getDistance(position, Player.instance.position) < 400){
            velocity = Player.instance.getCenter();
            velocity.subtract(getCenter());
            velocity.normalize();
            velocity.scale(maximumMoveSpeed);
            return;
        }
        //else uses A* pathfinding.
        pathList = Pathfinder.getPath(getCenter(), Player.instance.getCenter(), 25);
        if(pathList != null && pathList.size() > 1){
            Vector2 targetPosition = pathList.get(1);
            targetPosition.add(new Vector2(GameView.level.scaledTileWidth / 2, GameView.level.scaledTileWidth / 2));
            velocity = targetPosition;
            velocity.subtract(getCenter());
            velocity.normalize();
            velocity.scale(maximumMoveSpeed);
        }
    }
}
