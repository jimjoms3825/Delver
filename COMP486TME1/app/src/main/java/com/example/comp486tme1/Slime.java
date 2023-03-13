package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Slime (extends Enemy)
    Description: An enemy which aimlessly hops around and blindly follows the player.

 */
public class Slime extends Enemy {

    Animator animator; // Animator instance.

    private enum State {Idle, Wander, Chase}; // Simple finite state machine states.
    private State currentState = State.Idle; // Current state of the system.
    private int framesToThink = 0; // Counter for time between thinking updates.


    /*
    Inputs: None

    Outputs: None. initializes the enemy.

    Called by: constructor.

    Calls: super.initialize()
    */
    @Override
    public void initialize(){
        double typeOfSlime = Math.random();
        Bitmap slimeSheet;
        this.lootOnDeath = 3;

        if(typeOfSlime < 0.5){ // 50% chance of green slime.
            slimeSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                    R.drawable.slime_green);
            strength = 5;
            maximumMoveSpeed = 2;
            maxHealth = 10;
        }
        else if(typeOfSlime < 0.6){// 10% chance of white slime.
            slimeSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                    R.drawable.slime_white);
            strength = 2;
            maximumMoveSpeed = 4;
            maxHealth = 1;
        }
        else if(typeOfSlime < 0.8){// 20% chance of blue slime.
            slimeSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                    R.drawable.slime_blue);
            strength = 5;
            maximumMoveSpeed = 2;
            maxHealth = 35;
            this.lootOnDeath = 15;
        }
        else if(typeOfSlime < 0.9 || GameView.floorsCleared < 4){// 10% chance of black slime.
            slimeSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                    R.drawable.slime_black);
            strength = 30;
            maximumMoveSpeed = 1;
            maxHealth = 10;
            this.lootOnDeath = 10;
        }
        else{// 10% chance of red slime.
            slimeSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                    R.drawable.slime_red);
            strength = 20;
            maximumMoveSpeed = 4;
            maxHealth = 30;
            this.lootOnDeath = 25;
        }
        knockBackStrength = 25;
        animator = new Animator();
        animator.scale = 1.5f;
        animator.addAnimation(new Animation("Idle", slimeSheet, 1, 2, 7, 20));
        animator.addAnimation(new Animation("Jump", slimeSheet, 3, 6, 7, 10));
        health = maxHealth;
        super.initialize();
    }

    /*
    Inputs: None.

    Outputs: None. Controls the update logic.

    Called by: GameView loop

    Calls: think(), move()
    */
    @Override
    public void update() {
        think();
        move();
    }

    /*
    Inputs: None.

    Outputs: Returns animator next frame.

    Called by: GameView loop.

    Calls: None.
    */
    @Override
    public Bitmap getDrawable() {
        lastDrawable = animator.getBitmap();
        return lastDrawable;
    }

    /*
    Inputs: None.

    Outputs: None. Provides a simple FSM for slime logic.

    Called by: update()

    Calls: Vector, AABB, Animator methods.
    */
    private void think(){
        if(Player.instance == null) { return; }
        if(Vector2.getDistance(position, Player.instance.position) < 500){
            //Sets state to chase if player is close enough.
            currentState = State.Chase;
        }
        switch (currentState){
            case Idle:
                //Changes to wander after 200 frames.
                if(framesToThink-- <= 0){
                    framesToThink = 200;
                    currentState = State.Wander;
                    velocity = new Vector2(1 - (float) Math.random() * 2, 1 - (float) Math.random() * 2);
                    velocity.normalize();
                    velocity.scale(maximumMoveSpeed);
                    animator.playAnimation("Jump");
                }
                break;
            case Wander:
                //Changes to idle after 200 frames.
                if(framesToThink-- <= 0){
                    framesToThink = 200;
                    currentState = State.Idle;
                    animator.playAnimation("Idle");
                }
                break;
            case Chase:
                if(Vector2.getDistance(position, Player.instance.position) > 600){
                    //Sets state to idle if player is far enough.
                    currentState = State.Idle;
                }
                //Follows the player around. No pathfinding, just straight towards.
                AABB playerBounds = Player.instance.getBounds();
                velocity = Player.instance.getCenter();
                velocity.subtract(getCenter());
                velocity.normalize();
                velocity.scale(maximumMoveSpeed);
                break;
        }
    }
}
