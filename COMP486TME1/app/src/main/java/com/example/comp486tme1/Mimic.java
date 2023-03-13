package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 3)
    Name: James Bombardier
    Date: October 7, 2022

    Class: Mimic (extends Enemy)
    Description: An enemy that looks like a chest and activates when the player gets close.

 */

public class Mimic extends Enemy {

    Animator animator;
    public boolean attacking = false;

    /*
    Inputs: None.

    Outputs: None. initializes the enemy stats.

    Called by: constructor.

    Calls: super. initialize()
    */
    @Override
    public void initialize(){
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.chest_mimic);
        strength = 20;
        maximumMoveSpeed = 3;
        speed = 3;
        maxHealth = 50;
        knockBackStrength = 50;
        animator = new Animator();
        animator.addAnimation(new Animation("Idle", spriteSheet, 1, 1, 3, 12, true));
        animator.addAnimation(new Animation("Run", spriteSheet, 1, 3, 3, 8));
        health = maxHealth;
        this.lootOnDeath = 12;
        super.initialize();
        canCollide = false;
        isTrigger = false;
        collisionDetection = false;
    }

    /*
    Inputs:None.

    Outputs: None. Handles update logic.

    Called by: GameView loop

    Calls: Move(), checkForPlyaer()
    */
    @Override
    public void update() {
        if(!attacking){
            checkForPlayer();
        } else{
            velocity = Player.instance.getCenter();
            velocity.subtract(getCenter());
            velocity.normalize();
            velocity.scale(maximumMoveSpeed);
        }
        move();
    }

    /*
    Inputs: None.

    Outputs: Returns a Bitmap flipped based on velocity.

    Called by: GameView loop

    Calls: None.
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

    Outputs: None. Checks if the player is in range to trigger the enemy attacking.

    Called by: update().

    Calls: Animator.playAnimation()
    */
    private void checkForPlayer(){
        if(Player.instance == null) { return; }
        //Follows the player in a straight line if they are very close.
        if(Vector2.getDistance(position, Player.instance.position) < 400){
            attacking = true;
            canCollide = true;
            collisionDetection = true;
            animator.playAnimation("Run");
        }
    }

    /*
    Inputs: Float of damage taken. Vector2 of knockback to be applied.

    Outputs: None. Deals damage to the mimic. Drops coin relative to how much damage was recieved.

    Called by: GameView main loop

    Calls: Super.hit()
    */
    @Override
    public void hit(float damage, Vector2 knockBack) {
        if(!attacking) {return;}
        super.hit(damage, knockBack);
        int coinsToDrop = (int)(damage / (maxHealth / 50)); //will drop a max of 50 coins before death.
        if(coinsToDrop < 1 && Math.random() < damage / (maxHealth / 50)){ // proportional chance to drop coin if under damage threshold
            coinsToDrop = 1;
        }
        for(int i = 0; i < coinsToDrop; i++){
            GameObject toDrop;
            float roll = (float)Math.random();
            if(roll < 0.98){
                toDrop = new CoinPickup();
            }else{
                toDrop = new SpellPickup();
            }
            toDrop.position = position.clone();
            toDrop.position.add(new Vector2(getDrawable().getWidth() / 2, getDrawable().getHeight() / 2));
            toDrop.velocity = new Vector2((1 - (float) Math.random() * 2), (1 - (float) Math.random() * 2));
            toDrop.velocity.normalize();
            toDrop.velocity.scale(5 + (float)Math.random() * 5);
        }
    }
}
