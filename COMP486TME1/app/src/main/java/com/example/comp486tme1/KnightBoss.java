package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import java.util.ArrayList;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 3)
    Name: James Bombardier
    Date: October 7, 2022

    Class: KnightBoss (extends Enemy)
    Description: The first boss of the game. Uses a finite state machine to control AI.

 */

public class KnightBoss extends Enemy{

    private Animator animator;

    private boolean followPlayer = false;
    private boolean castingSpell = false;
    private enum AttackState {Pause, Follow, Charge, Rush, Spell}

    private AttackState currentState = AttackState.Pause;
    private int thinkTimer;
    private int actionTimer;

    /*
    Inputs: None

    Outputs: None. Initializes the boss.

    Called by: Constructor.

    Calls: Super.initialize()
    */
    @Override
    public void initialize(){
        super.initialize(); // Called before as to not scale the stats of the enemy.
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.boss_knight);
        strength = 15;
        maximumMoveSpeed = 40;
        speed = 15;
        maxHealth = 400;
        knockBackStrength = 50;
        hasKnockback = false;
        animator = new Animator();
        animator.scale = 2f;
        animator.addAnimation(new Animation("Idle", spriteSheet, 5, 8, 8, 12));
        animator.addAnimation(new Animation("Run", spriteSheet, 1, 4, 7, 5));
        health = maxHealth;
        lootOnDeath = 100;
    }

    /*
    Inputs: None.

    Outputs: None. Controls the update execution of the boss.

    Called by: GameView loop.

    Calls: think(), move()
    */
    @Override
    public void update() {
        if(Player.instance == null) { return; }
        think();
        move();
        if(castingSpell && actionTimer % 2 == 0){ // If the knight is currently casting it's spell;
            //Fire a sword in a random direction.
            GameObject go = new KnightSwordSpell(7.5f, (float)Math.random() * 360);
            go.position = position.clone();
        }
    }
    /*
    Inputs: None.

    Outputs: Returns a flipped Bitmap based on the velocity of the sprite.

    Called by: GameView loop.

    Calls: Bitmap and matrix methods.
    */
    @Override
    public Bitmap getDrawable() {

        if(currentState == AttackState.Charge && actionTimer % 20 < 5){
            return getChargingSprite();
        }
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

    Outputs: Returns a Bitmap which is colored over in white to represent a spell charging.

    Called by: draw

    Calls: Various drawing methods, Nothing significant.
    */
    private Bitmap getChargingSprite(){
        Bitmap frame = animator.getBitmap();
        Matrix matrix = new Matrix();
        if(velocity.x < 0){
            matrix.preScale(-1, 1);
        }
        Bitmap tempMap = Bitmap.createBitmap(frame, 0, 0, frame.getWidth(), frame.getHeight(), matrix, false);

        Paint paint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);

        Canvas tempCanvas = new Canvas(tempMap);
        tempCanvas.drawARGB(0, 0, 0, 0);

        tempCanvas.drawBitmap(tempMap, 0, 0, paint);
        lastDrawable = tempMap;
        return tempMap;
    }

    /*
    Inputs:

    Outputs:

    Called by:

    Calls:
    */
    @Override
    public void onDestroy() {
        if(killed)
        {
            GameView.enemiesKilled++;
            GameView.level.clearBossWall();
        }
    }

    /*
    Inputs:

    Outputs:

    Called by:

    Calls:
    */
    public void think(){
        if(followPlayer){
            Vector2 targetVelocity = new Vector2();
            targetVelocity.x = Player.instance.getCenter().x - getCenter().x;
            targetVelocity.y = Player.instance.getCenter().y - getCenter().y;
            targetVelocity.normalize();
            targetVelocity.scale(speed);
            velocity.Lerp(targetVelocity, 0.03f);
        }
        else{
            velocity.Lerp(Vector2.zero, 0.03f) ;
        }
        if(actionTimer-- > 0) { return; }
        if(thinkTimer++ >= 30){ // think twice per second..
            thinkTimer = 0;
            float rand = (float)Math.random();
            switch (currentState){
                case Pause:
                    followPlayer = false;
                    if(rand < 0.3f){
                        changeState(AttackState.Follow);
                        actionTimer = 100;
                    } else {
                        changeState(AttackState.Charge);
                        actionTimer = 150;
                    }
                    break;
                case Spell:
                    changeState(AttackState.Follow);
                    castingSpell = false;
                    actionTimer = 50;
                    break;

                case Rush:
                    changeState(AttackState.Follow);
                    actionTimer = 50;
                    break;

                case Follow:
                    changeState(AttackState.Pause);
                    actionTimer = 50;
                    break;
                case Charge:
                    followPlayer = false;
                    if(rand < 0.4){
                        changeState(AttackState.Spell);
                        actionTimer = 20;
                    } else{
                        changeState(AttackState.Rush);
                        actionTimer = 60;
                    }
                    break;
            }
        }
    }

    /*
    Inputs:

    Outputs:

    Called by:

    Calls:
    */
    private void changeState(AttackState newState){
        currentState = newState;
        switch (currentState){
            case Pause:
                followPlayer = false;
                break;
            case Follow:
                followPlayer = true;
                break;
            case Spell:
                followPlayer = false;
                castingSpell = true;
                break;
            case Rush:
                followPlayer = true;
                Vector2 targetVelocity = new Vector2();
                targetVelocity.x = Player.instance.getCenter().x - getCenter().x;
                targetVelocity.y = Player.instance.getCenter().y - getCenter().y;
                targetVelocity.normalize();
                targetVelocity.scale(maximumMoveSpeed);
                velocity = targetVelocity;
                break;
            case Charge:
                followPlayer = false;
                break;
        }
    }
}
