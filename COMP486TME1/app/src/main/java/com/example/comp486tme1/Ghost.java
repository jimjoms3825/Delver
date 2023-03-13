package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Ghost (Extends Enemy)
    Description: A Ghost enemy which can phase through walls, is transparent, and follows the player.

 */
public class Ghost extends Enemy {

    private Animator animator;
    private float attackScale = 0; // The current aggression of the ghost (based on player distance).
    private final int DETECTION_DISTANCE = 500; // How far the player should be before the ghost reacts.
    private final int DETECTION_MAXED_DISTANCE = 100; // the offset between no attack visibility and some visibility.
    private final int MAX_OPACITY = 150; // the maximum opacity of the ghost (x/265).

    /*
    Inputs: None.

    Outputs: None. Initializes the enemy stats and animations.

    Called by: Constructor.

    Calls: Super.Initialize()
    */
    @Override
    public void initialize(){
        speed = 4;
        maximumMoveSpeed = 8;
        strength = 10;
        maxHealth = 15;
        animator = new Animator();
        animator.scale = 1;
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.chort);
        animator.addAnimation(new Animation("Idle", spriteSheet, 1, 4, 4, 8));
        isTrigger = true; // No need for collision, just triggering.
        collisionDetection = true;
        drawOrder = 1;
        knockBackStrength = 25;
        health = maxHealth;
        this.lootOnDeath = 10;
        super.initialize();
        canCollide = false;
    }

    /*
    Inputs: None.

    Outputs: None. Provides the update logic for the enemy.

    Called by: GameView loop

    Calls: Move().
    */
    @Override
    public void update() {
        if(Player.instance == null) { return; }
        attackScale = (DETECTION_DISTANCE - DETECTION_MAXED_DISTANCE) / Vector2.getDistance(position, Player.instance.position);
        if(attackScale > 1) { attackScale = 1; } // Clamp attack scale.
        Vector2 newVel = new Vector2();
        newVel.x = Player.instance.getCenter().x - getCenter().x;
        newVel.y = Player.instance.getCenter().y - getCenter().y;
        newVel.normalize();
        newVel.scale(speed * attackScale); // Adjust speed by attack scale.
        velocity.Lerp(newVel, 0.02f);
        move();
    }

    /*
    Inputs: None.

    Outputs: Returns a flipped and transparency adjusted Bitmap.

    Called by: GameView loop

    Calls: Bitmap, canvas, and matrix mehods for creating the desired effect.
    */
    @Override
    public Bitmap getDrawable() {
        //Handle flip.
        Matrix matrix = new Matrix();
        Bitmap source = animator.getBitmap();
        if(velocity.x < 0){
            matrix.preScale(-1, 1);
        } else{
            matrix.preScale(1, 1);
        }
        Bitmap tempMap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
        //Handle Transparency.
        Bitmap returnMap = Bitmap.createBitmap(tempMap.getWidth(), tempMap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(returnMap);
        tempCanvas.drawARGB(0, 0, 0, 0);
        Paint paint = new Paint();
        int newAlpha = (int)(attackScale * MAX_OPACITY);
        if(newAlpha > MAX_OPACITY) { newAlpha = MAX_OPACITY; }
        if(newAlpha < 0) { newAlpha = 0; }
        paint.setAlpha(newAlpha);
        tempCanvas.drawBitmap(tempMap, 0, 0, paint);
        lastDrawable = tempMap;
        return returnMap;
    }

    @Override
    protected void ensureInBounds() {
        //Ghost does not need this method.
    }
}
