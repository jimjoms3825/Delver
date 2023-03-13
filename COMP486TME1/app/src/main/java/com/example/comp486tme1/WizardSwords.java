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

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: October 7th, 2022

    Class: WizardSwords (extends GameObject)
    Description: The wizard boss' swords. They change transparency at a random rate.

 */

public class WizardSwords extends GameObject{

    public static Sprite sprite;
    public Vector2 direction; // The direction of the Swords.
    private int drawOpacity;
    private boolean drawOpacityIncrement;

    /*
    Inputs: None

    Outputs: None.

    Called by: Constructor

    Calls: None.
    */
    @Override
    protected void initialize() {
        isTrigger = true;
        canCollide = false;
        collisionDetection = true;
        drawOrder = 3;
        drawOpacity = (int)(Math.random() * 255);
    }

    /*
    Inputs: GameObject collided with

    Outputs: None. Deals damage to player on collision.

    Called by: GameView loop

    Calls: GameObject.hit()
    */
    @Override
    public void onCollision(GameObject other) {
        if(Player.class.isInstance(other)){
            other.hit(20, Vector2.zero);
        }
    }

    /*
    Inputs: None.

    Outputs: Returns a Bitmap rotated and opacity adjusted.

    Called by: GameView loop

    Calls: drawing methods.
    */
    @Override
    public Bitmap getDrawable() {
        if(sprite == null){
            sprite = new Sprite(BitmapFactory.decodeResource(MainActivity.instance.getResources(), R.drawable.weapon_duel_sword), 1);
        }
        //Rotate
        Matrix matrix = new Matrix();
        matrix.preRotate(direction.getAngle() + 90);
        Bitmap source = Bitmap.createBitmap(sprite.getBitmap(), 0, 0,
                sprite.getBitmap().getWidth(), sprite.getBitmap().getHeight(), matrix, false);

        //Change opacity.
        if(drawOpacityIncrement){
            drawOpacity += (int)(Math.random() * 5);
            if(drawOpacity >= 250){
                drawOpacityIncrement = false;
            }
        }
        else{
            drawOpacity -= (int)(Math.random() * 5);
            if(drawOpacity <= 30){
                drawOpacityIncrement = true;
            }
        }
        Bitmap tempMap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), null, false);
        Bitmap returnMap = Bitmap.createBitmap(tempMap.getWidth(), tempMap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(returnMap);
        tempCanvas.drawARGB(0, 0, 0, 0);
        Paint paint = new Paint();
        paint.setAlpha(drawOpacity);
        tempCanvas.drawBitmap(tempMap, 0, 0, paint);
        lastDrawable = tempMap;
        return returnMap;
    }
}
