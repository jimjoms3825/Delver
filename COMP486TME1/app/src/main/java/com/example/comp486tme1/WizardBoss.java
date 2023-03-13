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
import java.util.Random;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: October 7th, 2022

    Class: WizardBoss (extends Enemy)
    Description: The final boss of the game. Shoots projectiles and has an array of swords protecting it.

 */

public class WizardBoss extends Enemy {
    private Animator animator;

    private boolean followPlayer = false;
    private boolean castingSpell = false;
    private enum AttackState {Pause, Wander, Charge, Burst, Spell}

    private AttackState currentState = AttackState.Pause;
    private int thinkTimer;
    private int actionTimer;

    private ArrayList<WizardSwords> swords;
    private int swordOffset = 150;
    private float swordSpin;
    private float spellSpin;
    private float spellSpinSpeed = 0f;
    private boolean spellSpinClockwise = true;

    private int spellTimer;

    /*
    Inputs: None.

    Outputs: None.

    Called by: constructor.

    Calls: None.
    */
    @Override
    public void initialize(){
        super.initialize();
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.boss_dark_wizard);
        hasKnockback = false;
        strength = 20;
        maximumMoveSpeed = 20;
        speed = 5;
        maxHealth = 4000;
        knockBackStrength = 50;
        animator = new Animator();
        animator.scale = 2f;
        animator.addAnimation(new Animation("Run", spriteSheet, 1, 4, 4, 12));
        health = maxHealth;

        swords = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            swords.add(new WizardSwords());
        }
    }

    /*
    Inputs: None.

    Outputs: None. Controls the update logic of the boss.

    Called by: GameView loop

    Calls: think(), move(), Vector methods.
    */
    @Override
    public void update() {
        if(Player.instance == null) { return; }
        think();
        move();
        if(castingSpell && spellTimer++ > 4){
            spellTimer = 0;
            for(int i = 0; i < 3; i++){
                GameObject go = new WizardSpell(20, spellSpin / 2 + 90 * i);
                go.position = position.clone();
            }
        }
        else if(!castingSpell && Math.random() > 0.97f){ // Cast randomly when not in spell mode
            for(int i = 0; i < Math.random() * 20; i++){
                GameObject go = new WizardSpell(20, (float)Math.random() * 360);
                go.position = position.clone();
            }
        }

        swordSpin += 0.03;

        //Change spin speed
        if(spellSpinClockwise){
            spellSpinSpeed += 0.0005;
            if(spellSpinSpeed >= 0.05){
                spellSpinClockwise = false;
            }
        } else{
            spellSpinSpeed -= 0.0005;
            if(spellSpinSpeed <= -0.05){
                spellSpinClockwise = true;
            }

        }

        spellSpin += spellSpinSpeed;

        for(int i = 0; i < swords.size(); i++){
            WizardSwords sword = swords.get(i);
            sword.position = position.clone();
            sword.direction = Vector2.degreesToVector(((360 / swords.size() + 1) * i) + swordSpin);
            Vector2 offset = Vector2.getNormalized(sword.direction);
            offset.scale(swordOffset);
            sword.position.add(offset);

        }
    }

    /*
    Inputs: None.

    Outputs: Returns a flipped Bitmap based on velocity

    Called by: GameView loop

    Calls:  None.
    */
    @Override
    public Bitmap getDrawable() {

        if(currentState == WizardBoss.AttackState.Charge && actionTimer % 20 < 5){
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
    Inputs: None.

    Outputs: None. Wins the game.

    Called by: GameView loop

    Calls: GameView.win()
    */
    @Override
    public void onDestroy() {
        if(killed)
        {
            GameView.floorsCleared++;
            GameView.enemiesKilled++;
            GameView.instance.win();
        }
    }

    /*
    Inputs: None.

    Outputs: None. Changes the state of the AI.

    Called by: update()

    Calls: changeState()
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
                    changeState(AttackState.Charge);
                    actionTimer = 100;
                    break;
                case Spell:
                    changeState(AttackState.Wander);
                    castingSpell = false;
                    actionTimer = 25;
                    break;

                case Burst:
                    changeState(AttackState.Wander);
                    actionTimer = 50;
                    break;

                case Wander:
                    changeState(AttackState.Pause);
                    actionTimer = 50;
                    break;
                case Charge:
                    followPlayer = false;
                    if(rand < 0.6){
                        changeState(AttackState.Spell);
                        actionTimer = 200 + (int)(Math.random() * 150);
                    } else{
                        changeState(AttackState.Burst);
                        actionTimer = 50;
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
            case Wander:
                followPlayer = true;
                break;
            case Spell:
                followPlayer = false;
                castingSpell = true;
                break;
            case Burst:
                followPlayer = true;
                Vector2 targetVelocity = new Vector2();
                targetVelocity.x = Player.instance.getCenter().x - getCenter().x;
                targetVelocity.y = Player.instance.getCenter().y - getCenter().y;
                targetVelocity.normalize();
                targetVelocity.scale(maximumMoveSpeed);
                velocity = targetVelocity;
                int numberOfProjectiles = 20 + (int)(Math.random() * 80);
                for(int i = 0; i < numberOfProjectiles; i++){
                    GameObject go = new WizardSpell(5,  (float) Math.random() + (360 / numberOfProjectiles) * i);
                    go.position = getCenter().clone();
                }
                break;
            case Charge:
                followPlayer = false;
                break;
        }
    }
}
