package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Tile (extends GameObject)
    Description: A Tile object for the environment and the logic needed to handle them. Tiles also include
    walls and interactable objects (such as the exit ladder).

 */

public class Tile extends GameObject{
    public Sprite sprite; // Simple Sprite.
    public boolean walkable; // Whether the tile can be walked on.
    public boolean isExit = false; // Whether the tile is the exit.
    public boolean isTrap = false;
    public static Bitmap blankTile;

    public Animator spikeTrapAnimator;

    /*
    Inputs:

    Outputs:

    Called by:

    Calls:
    */
    @Override
    protected void initialize() {
        isStatic = true;
        collisionDetection = false;
        canCollide = false;
        sprite = new Sprite(BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.wall_blue), 4);
    }


    /*
    Inputs:

    Outputs:

    Called by:

    Calls:
    */
    @Override
    public Bitmap getDrawable() {
        if(spikeTrapAnimator != null){
            lastDrawable = spikeTrapAnimator.getBitmap();
        }
        else{
            lastDrawable = sprite.getBitmap();
        }
        return lastDrawable;
    }

    /*
    Inputs: The gameobject collided with

    Outputs: None. If exit, transitions level. If trapped, hurts player.

    Called by: GameView loop.

    Calls: GameView.leaveLevel(), Player.hit()
    */
    @Override
    public void onCollision(GameObject other) {
        if(!Player.class.isInstance(other)) { return; }
        if(isExit) {
            GameView.getView().leaveLevel();
            isExit = false; // Avoids double triggers.
        }
        else if(isTrap){
            other.hit(5, Vector2.zero);
            spikeTrapAnimator.playAnimation("spike");
        }

    }

    //Returns a tile with no sprite of the right size. Used for keeping GOs in bounds.
    /*
    Inputs: An integer to properly scale the bitmap (for AABB) of the tile.

    Outputs: Returns a tile with a empty bitmap.

    Called by: LevelGenerator.generateBlank()

    Calls: None.
    */
    public static Tile getBlankTile(int scaledTileWidth){
        Tile tile = new Tile();
        tile.walkable = false;
        tile.canCollide = true;
        if(blankTile == null){
            blankTile = Bitmap.createBitmap(scaledTileWidth, scaledTileWidth, Bitmap.Config.ARGB_8888);
        }
        tile.sprite = new Sprite(blankTile, 1);
        return tile;
    }

    /*
    Inputs: None.

    Outputs: None. Turns the tile into a spike trap.

    Called by: LevelGenerator.fillRooms() LevelGenerator.run()

    Calls: None.
    */
    public void createSpikeTrap(){
        spikeTrapAnimator = new Animator();
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(), R.drawable.floor_red);
        spikeTrapAnimator.addAnimation(new Animation("spike", spriteSheet, 7, 9, 9, 4, true));
        spikeTrapAnimator.scale = Level.tileScale;
        isTrigger = true;
        isTrap = true;
    }


}
