package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/*
    Course: COMP 486 (Mobile and Internet Game Development
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: SpellPickup (extends Pickup)
    Description: Powers up the player when picked up.

 */
public class SpellPickup extends Pickup{

    private Sprite staffSprite; // Simple sprite.

    /*
    Inputs: None

    Outputs: None

    Called by: constructor

    Calls: Super.initialize()
    */
    @Override
    protected void initialize() {
        super.initialize();
        staffSprite = new Sprite(BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.weapon_red_magic_staff), 0.75f);
    }

    /*
    Inputs: None.

    Outputs: None. Gives the player a spell powerup.

    Called by: super.onCollision

    Calls: Player.powerUpSpell(), SoundManager.PlaySound()
    */
    @Override
    protected void pickup() {
        if(framesToPickup > 0) {return;}
        SoundManager.instance.playSound(SoundManager.Sounds.Powerup);
        Player.instance.powerUpSpell();
        toRemove = true;
        isTrigger = false;
    }

    /*
    Inputs: None.

    Outputs: Returns a Bitmap of the spell powerup.

    Called by: GameView loop

    Calls: None.
    */
    @Override
    public Bitmap getDrawable() {
        lastDrawable = staffSprite.getBitmap();
        return lastDrawable;
    }


}
