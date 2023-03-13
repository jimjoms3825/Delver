package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: October 7th, 2022

    Class: SlimeBossEnemy (extends Enemy)
    Description: The enemy used in the slime boss fight.

 */

public class SlimeBossEnemy extends Enemy {
    private Animator animator; // Animator instance.

    public int size; // The size of this slime.
    public SlimeBoss boss;
    private AABB levelBounds;

    /*
    Inputs: The size of this slime, the position it will spawn in, and the boss (to notify on death)

    Outputs: None.

    Called by: SlimeBoss.

    Calls: None.
    */
    public SlimeBossEnemy(int _size, Vector2 position, SlimeBoss _boss){
        super(position);
        levelBounds = new AABB();
        levelBounds.min = new Vector2(496, 491);
        levelBounds.max = levelBounds.min.clone();
        levelBounds.max.add(new Vector2(8, 10));
        levelBounds.min.scale(GameView.level.scaledTileWidth);
        levelBounds.max.scale(GameView.level.scaledTileWidth);

        size = _size;
        boss = _boss;
        boss.slimeList.add(this);
        Bitmap slimeSheet;
        maximumMoveSpeed = 20;

        if(size == 1){
            slimeSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                    R.drawable.slime_green);
        }
        else if(size == 2){
            slimeSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                    R.drawable.slime_white);
        }
        else if(size == 4){
            slimeSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                    R.drawable.slime_blue);
        }
        else if(size == 8){
            slimeSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                    R.drawable.slime_black);
        }
        else{// Largest.
            slimeSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                    R.drawable.slime_red);
        }
        strength = 5 + size * 2; //5 - 37
        maximumMoveSpeed = 4 + (16 - size); //4 - 20
        speed = maximumMoveSpeed;
        maxHealth = size * 20; // 20 - 320
        lootOnDeath = size * 5; // 5 - 80
        animator = new Animator();
        animator.scale = 1 + size / 2;
        animator.addAnimation(new Animation("Idle", slimeSheet, 1, 2, 7, 20));
        animator.addAnimation(new Animation("Jump", slimeSheet, 3, 6, 7, 10));
        health = maxHealth;
    }

    /*
    Inputs: None.

    Outputs: None. simple follow logic.

    Called by: GameView Loop

    Calls: move()
    */
    @Override
    public void update() {
        if(Player.instance == null) { return; }
        Vector2 targetVelocity = new Vector2();
        targetVelocity.x = Player.instance.getCenter().x - getCenter().x;
        targetVelocity.y = Player.instance.getCenter().y - getCenter().y;
        targetVelocity.normalize();
        targetVelocity.scale(speed);
        velocity.Lerp(targetVelocity, 0.03f);
        move();
        checkOutOfBounds();
    }

    /*
    Inputs: None.

    Outputs: Returns animator frame.

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

    Outputs: None. Ensures that the enemy stays in the bounds of the boss arena.

    Called by: Update().

    Calls: None.
    */
    private void checkOutOfBounds(){
        if(!getBounds().isInside(levelBounds)){
            toRemove = true;
        }
    }

    /*
    Inputs: None.

    Outputs: None. Notifies the slimeBoss object that this slime has died.

    Called by:

    Calls:
    */
    @Override
    public void onDestroy() {
        boss.slimeKilled(this);
    }
}
