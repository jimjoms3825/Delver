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
    Date: August 24th, 2022

    Class: UI
    Description: The UI which is drawn in the game.

 */
public class UI {
    public Sprite fullHeart; // Sprite for hearts.
    public Sprite halfHeart;
    public Sprite emptyHeart;
    public Sprite coin; // Sprite for coin
    public Sprite staff;
    private int heartSpacing = 50; // Spacing between each heart.

    /*
    Inputs: None.

    Outputs: None. Creates a UI.

    Called by: GameView.

    Calls: None.
    */
    public UI(){
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.ui_heart);
        int frameWidth = spriteSheet.getWidth() / 3;
        int frameHeight = spriteSheet.getHeight();
        emptyHeart = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 1);
        halfHeart = new Sprite(Bitmap.createBitmap(spriteSheet, frameWidth, 0, frameWidth, frameHeight), 1);
        fullHeart = new Sprite(Bitmap.createBitmap(spriteSheet, 2 * frameWidth, 0, frameWidth, frameHeight), 1);
        spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.coin);
        frameWidth = spriteSheet.getWidth() / 4;
        frameHeight = spriteSheet.getHeight();
        coin = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 2);
        staff = new Sprite(BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.weapon_red_magic_staff), 0.75f);
    }

    /*
    Inputs: Canvas for drawing

    Outputs: None. Draws to the provided canvas.

    Called by: GameView.draw().

    Calls:  None.
    */
    public void drawUI(Canvas canvas){
        //Access variables for display.
        if(Player.instance == null) {return;}
        int coins = GameView.coinsCollected;
        float health = 0;
        float maxHealth = 100;
        if(Player.instance != null){
            health = Player.instance.health;
            maxHealth = Player.instance.maxHealth;
        }
        int numberOfHearts = (int)(maxHealth / 10);
        float healthIncrement = maxHealth / numberOfHearts; // How many hearts each unit of health represents.
        //Set paint style for text.
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        //Handle heart drawing.
        for(int i = 0; i < numberOfHearts; i++){
            int row = (int)(i / 10);
            int xPos = 50 + (i - (row * 10)) * heartSpacing;
            int yPos = 100 + (row * 20);
            if(health <= 0){
                canvas.drawBitmap(emptyHeart.getBitmap(), xPos, yPos, paint);
            }
            else if(health <= healthIncrement / 2){
                canvas.drawBitmap(halfHeart.getBitmap(), xPos, yPos, paint);
            }
            else{
                canvas.drawBitmap(fullHeart.getBitmap(), xPos, yPos, paint);
            }
            health -= healthIncrement;
        }
        //Draw coin sprite and quantity.
        canvas.drawBitmap(coin.getBitmap(), 50 + heartSpacing * 10, 100, paint);
        canvas.drawText(Integer.toString(coins), 100 + (heartSpacing * 10), 90 + coin.getBitmap().getHeight(), paint);
        canvas.drawBitmap(staff.getBitmap(), 60 + heartSpacing * 10, 150, paint);
        canvas.drawText("-" + Integer.toString((int)((1 - Player.instance.weaponCastReduction) * 100)) + "%",
                100 + (heartSpacing * 10), 140 + staff.getBitmap().getHeight(), paint);
    }
}
