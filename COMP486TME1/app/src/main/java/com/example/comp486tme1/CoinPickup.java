package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Coin Pickup (Extends Pickup)
    Description: A coin which the player can pickup. Dropped by chests and enemies.

 */
public class CoinPickup extends Pickup {

    Animator animator; // Coin animator.
    private enum CoinType{Copper, Silver, Gold} // Different values of coin.
    private CoinType coinType; // The type of this coin.

    /*
    Inputs: None.

    Outputs: None. Initializes the animator and gameObject values. Also determines the coinType.

    Called by: GameObject Constructor.

    Calls: Bitmap creation Methods; Animator and Animation constructors.
    */
    @Override
    protected void initialize() {
        super.initialize();
        animator = new Animator();
        animator.scale = 1;
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.coin_gold);
        float coinValue = (float)Math.random() * GameView.floorsCleared * 2;
        if(coinValue > 10){
            coinType = CoinType.Gold;
        }
        else if(coinValue > 5){
            coinType = CoinType.Silver;
            spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                    R.drawable.coin_silver);
        }
        else{
            coinType = CoinType.Copper;
            spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                    R.drawable.coin_copper);
        }
        animator.addAnimation(new Animation("Spin", spriteSheet, 1, 4, 4, 4));
    }

    /*
    Inputs: None.

    Outputs: None. Adds coins to the GameView and disables the trigger. Removes the object from the object pool.

    Called by: OnCollision method of Pickup class.

    Calls: Soundmanager...Playsound.
    */
    @Override
    protected void pickup() {
        if(framesToPickup > 0) {return;}
        switch (coinType){
            case Gold:
                GameView.coinsCollected += 10;
                break;
            case Silver:
                GameView.coinsCollected += 5;
                break;
            case Copper:
                GameView.coinsCollected++;
                break;
        }
        SoundManager.instance.playSound(SoundManager.Sounds.Coin);
        toRemove = true;
        isTrigger = false;
    }

    /*
    Inputs: None

    Outputs: Returns the current frame of the animator.

    Called by: Main GameView loop.

    Calls: Animator.getBitmap.
    */
    @Override
    public Bitmap getDrawable() {
        lastDrawable = animator.getBitmap();
        return lastDrawable;
    }
}
