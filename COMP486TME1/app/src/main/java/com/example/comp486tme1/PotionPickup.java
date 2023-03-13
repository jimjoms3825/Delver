package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: PotionPickup (Extends Pickup)
    Description: A health potion for the player to pick up.

 */
public class PotionPickup extends Pickup{

    private Sprite potionSprite; // Simple sprite.

    /*
    Inputs: None

    Outputs: None.

    Called by: constructor.

    Calls: super.initialize()
    */
    @Override
    public void initialize(){
        super.initialize();
        potionSprite = new Sprite(BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.potion), 1);
    }

    /*
    Inputs: None.

    Outputs: None. Heals the player by 15% of their max health.

    Called by: Pickup.onCollision()

    Calls: SoundManager.playSound(), Player.addHealth()
    */
    @Override
    protected void pickup() {
        if(framesToPickup > 0) {return;}
        Player.instance.addHealth(Player.instance.maxHealth * 0.15f);
        SoundManager.instance.playSound(SoundManager.Sounds.Heal);
        toRemove = true;
        isTrigger = false;
    }

    /*
    Inputs: None.

    Outputs: Returns bitmap sprite.

    Called by: GameView loop

    Calls: None.
    */
    @Override
    public Bitmap getDrawable() {
        lastDrawable =  potionSprite.getBitmap();
        return  lastDrawable;
    }

}
