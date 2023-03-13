package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.util.ArrayList;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Demon (Extends Enemy)
    Description: An enemy which can follow the player using A* pathfinding.

 */
public class Demon extends Enemy{

    Animator animator; // Animates the enemy.
    private ArrayList<Vector2> pathList; // Stores the current path to the player.

    /*
    Inputs: None

    Outputs: None. Initializes the stats of the enemy.

    Called by: Enemy constructor.

    Calls: Animator methods.
    */
    @Override
    public void initialize(){
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.bigdemon);
        strength = 20;
        maximumMoveSpeed = 3;
        speed = 3;
        maxHealth = 20;
        knockBackStrength = 50;
        animator = new Animator();
        animator.scale = 0.7f;
        animator.addAnimation(new Animation("Idle", spriteSheet, 5, 7, 7, 12));
        animator.addAnimation(new Animation("Run", spriteSheet, 1, 4, 7, 5));
        health = maxHealth;
        pathList = new ArrayList<Vector2>();
        this.lootOnDeath = 12;
        super.initialize();
    }

    /*
    Inputs: None.

    Outputs: None. Controls the enemy behavior.

    Called by: GameView main loop.

    Calls: Think, Move
    */
    @Override
    public void update() {
        think();
        move();
    }

    /*
    Inputs: None.

    Outputs: Returns a bitmap which is rotated based on velocity

    Called by: GameView main thread.

    Calls: Bitmap methods.
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

    //Handles the call to the main pathfinding system.
    /*
    Inputs: None.

    Outputs: None. Handles the call to the main pathfinding system. Assigns the pathlist variable.

    Called by: Update.

    Calls: Vector2 methods, Pathfinder.getPath.
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
