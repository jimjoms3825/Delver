package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;


/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 3)
    Name: James Bombardier
    Date: October 7, 2022

    Class: GameButton
    Description: A custom abstract button script for use with Viewable objects.

 */

public abstract class GameButton {

    public AABB bounds; // The bounds of the button.

    /*
    Inputs: Vector2 representing the position, Vector2 Representing the extents.

    Outputs: None. Creates button with specified parameters.

    Called by: Inherited classes.

    Calls: AABB methods.
    */
    public GameButton(Vector2 position, Vector2 size){
        bounds = new AABB();
        bounds.min = position;
        bounds.max = position.clone();
        bounds.max.add(size);
    }

    /*
    Inputs: Canvas to be drawn on.

    Outputs: None. Draws on canvas.

    Called by: Viewable threads.

    Calls: Paint and canvas methods. Sprite.getBitmap().
    */
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setARGB(150, 200, 200, 200);
        canvas.drawRect(bounds.min.x, bounds.min.y, bounds.max.x, bounds.max.y, paint);
        Sprite sprite = getSprite();
        if (sprite != null) {
            Vector2 spriteCenter = new Vector2(sprite.getBitmap().getWidth() / 2,
                    sprite.getBitmap().getHeight() / 2);
            Vector2 drawPos = bounds.min.clone();
            drawPos.x -= (bounds.min.x - bounds.max.x) / 2;
            drawPos.y -= (bounds.min.y - bounds.max.y) / 2;

            drawPos.subtract(spriteCenter);

            paint.setARGB(255, 255, 255, 255);
            canvas.drawBitmap(getSprite().getBitmap(), drawPos.x, drawPos.y, paint);
        }
    }

    /*
    Inputs: None.

    Outputs: Sprite object.

    Called by: draw.

    Calls: Abstract...
    */
    public abstract Sprite getSprite();

    /*
    Inputs: MotionEvent used to determine whether the motionevent was a click (down) and also inside
    the bounds of the button.

    Outputs: None. Uses the onClick method.

    Called by: Viewables in response to android system calls.

    Calls: onClick().
    */
    public void input(MotionEvent event) {

        Vector2 position = new Vector2(event.getX(event.getActionIndex()), event.getY(event.getActionIndex()));
        if(bounds.isInside(position) && (event.getActionMasked() == MotionEvent.ACTION_DOWN
                || event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)){
            onClick();
        }
    }

    /*
    Inputs: None.

    Outputs: None. Inheriting classes define function.

    Called by: input.

    Calls: Abstract...
    */
    public abstract void onClick();
}
