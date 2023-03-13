package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Sprite
    Description: Contains a single Bitmap image and provides a convenient API for modifications on the sprite.

 */
public class Sprite {

    private Bitmap bitmap; // The bitmap of the sprite
    enum Orientations {Vertical, Horizontal, Both}; // Orientation for easy rotation.
    public float scale; // Scale of the sprite.

    /*
    Inputs: The bitmap to use for the sprite, and the scale of the sprite.

    Outputs: None.

    Called by: Too many places to note.

    Calls: Bitmap.createScaledBitmap().
    */
    public Sprite(Bitmap _bitmap, float _scale){
        scale = _scale;
        bitmap = Bitmap.createScaledBitmap(_bitmap, (int)(_bitmap.getWidth() * scale), (int)(_bitmap.getHeight() * scale), false);
    }

    /*
    Inputs: The bitmap to use for the sprite, the orientation of the sprite, and the scale of the sprite.

    Outputs: none.

    Called by: Only the LevelGenerator class.

    Calls: Bitmap.createScaledBitmap(), Bitmap.createBitmap(), Matrix functions.
    */
    public Sprite(Bitmap _bitmap, Orientations orientation, float _scale){
        scale = _scale;
        Matrix matrix = new Matrix();
        switch (orientation){
            case Vertical:
                matrix.preScale(1, -1);
                break;
            case Horizontal:
                matrix.preScale(-1, 1);
                break;
            case Both:
                matrix.preScale(-1,  -1);
                break;
        }
        bitmap = Bitmap.createBitmap(_bitmap, 0, 0, _bitmap.getWidth(), _bitmap.getHeight(), matrix, false);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth() * scale), (int)(bitmap.getHeight() * scale), false);
    }

    /*
    Inputs: None.

    Outputs: Returns the bitmap of the sprite.

    Called by: GameView main loop

    Calls: None.
    */
    public Bitmap getBitmap() {
        return bitmap;
    }

}
