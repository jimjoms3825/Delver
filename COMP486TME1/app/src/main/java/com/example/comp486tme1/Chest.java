package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Chest (extends Pickup)
    Description: Represents an in game chest. Spawns loot when interacted with by the player.

 */
public class Chest extends Pickup {

    private boolean opened = false; // Whether the chest has been opened.
    private Sprite[] chestMaps; // Manual control of chest animation.
    private int state = 0; // The current state of the chest.
    private int stateTransitionTime = 0; // Index between state transitions.
    private Bitmap chestMap; // The spritesheet of the chest.

    /*
    Inputs: None

    Outputs: None. Initializes the chests sprites and settings.

    Called by: Object constructor.

    Calls: Bitmap creation methods.
    */
    @Override
    protected void initialize() {
        super.initialize();
        isStatic = true;
        collisionDetection = false;
        canCollide = false;
        chestMap = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.chest);
        chestMaps = new Sprite[3];
        int frameWidth = chestMap.getWidth() / chestMaps.length;
        int frameHeight = chestMap.getHeight();
        for(int i = 0; i < chestMaps.length ; i++){
            Bitmap temp = Bitmap.createBitmap(chestMap, i * frameWidth,
                    0, frameWidth, frameHeight);
            chestMaps[i] = new Sprite(Bitmap.createScaledBitmap(temp, frameWidth * 2, frameHeight * 2, false), 0.5f);
        }
    }

    /*
    Inputs: None

    Outputs: None. Handles the opening logic of the chest and releasing loot.

    Called by: GameView Loop

    Calls: Vector2 Methods.
    */
    @Override
    public void update() {
        //Opening logic.
        if(opened && state < chestMaps.length - 1 && stateTransitionTime++ > 10){
            state++;
            stateTransitionTime = 0;
            if(state == 2){ // Spawn all of the items.
                int numberOfDrops = (int)(20 + Math.random() * 50);
                int spawnedVelocity = 8; //
                for(int i = 0; i < numberOfDrops; i++){
                    GameObject toDrop;
                    float roll = (float)Math.random();
                    if(roll < 0.94){
                        toDrop = new CoinPickup();
                    }
                    else if(roll < 0.98){
                        toDrop = new PotionPickup();
                    } else{
                        toDrop = new SpellPickup();
                    }
                    toDrop.position = position.clone();
                    toDrop.position.add(new Vector2(getDrawable().getWidth() / 2, getDrawable().getHeight() / 2));
                    toDrop.velocity = new Vector2((1 - (float) Math.random() * 2), (1 - (float) Math.random() * 2));
                    toDrop.velocity.normalize();
                    toDrop.velocity.scale(5 + (float)Math.random() * spawnedVelocity);
                }
                //Once this point is reached, this code will never be called again, and the update
                //will always return at the first conditional. The second predicate always fails after this code.
            }
        }
    }

    /*
    Inputs: None

    Outputs: None. When the player collides, open the chest and disable the trigger.

    Called by: Pickup.onCollision method.

    Calls: Bitmap creation methods.
    */
    @Override
    protected void pickup() {
        opened = true;
        isTrigger = false;
        SoundManager.instance.playSound(SoundManager.Sounds.Chest);
    }

    /*
    Inputs: None

    Outputs: Returns the chest bitmap at the appropriate state.

    Called by: GameView Loop.

    Calls: None.
    */
    @Override
    public Bitmap getDrawable() {
        lastDrawable = chestMaps[state].getBitmap();
        return lastDrawable;
    }
}
