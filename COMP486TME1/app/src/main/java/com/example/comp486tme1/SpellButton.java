package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: October 7th, 2022

    Class: ShopButton (extends GameButton)
    Description: A button used for changing the player spell during gameplay.

 */

public class SpellButton extends GameButton{

    public Sprite defaultSprite;

    /*
    Inputs: Position for the button, and extents of the button.

    Outputs: Creates a button for the player to change spells with.

    Called by: ControlsView, GameView.

    Calls: None.
    */
    public SpellButton(Vector2 position, Vector2 size) {
        super(position, size);
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.spell_bolt);
        int frameWidth = spriteSheet.getWidth() / 4;
        int frameHeight = spriteSheet.getHeight();
        defaultSprite = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 1);
    }

    /*
    Inputs: None.

    Outputs: Returns the icon of the players current weapon.

    Called by: GameButton.draw()

    Calls: None.
    */
    @Override
    public Sprite getSprite() {
        if (Player.instance == null) {
            return defaultSprite;
        }
        return Player.instance.currentWeapon.icon;
    }

    /*
        Inputs: None.

        Outputs: Slightly modivies the original method by drawing the weapon type over the button.

        Called by: GameView loop

        Calls: None.
        */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(Player.instance == null) {return;}
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        canvas.drawText(Player.instance.currentWeapon.thisType.toString(), bounds.min.x + 5, bounds.min.y + paint.getTextSize(), paint);
    }

    /*
        Inputs: None.

        Outputs: None. Changes the players weapon.

        Called by: Android system

        Calls: Player.changeWeapon
        */
    @Override
    public void onClick() {
        if(Player.instance != null){
            Player.instance.changeWeapon();
        }
    }
}
