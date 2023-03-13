package com.example.comp486tme1;

import android.graphics.Canvas;
import android.graphics.Paint;

/*
Based partially on code from https://www.instructables.com/A-Simple-Android-UI-Joystick/

 */

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Joystick
    Description: A simulated joystick.

 */

public class JoyStick {

    public float size; // The size of the joystick container.
    public float handleSizePercentage = 0.5f; // How big the handle is inside the container.
    public Vector2 center; // The center of the joystick.
    public Vector2 handlePos; // The current position of the handle.
    public boolean isGrabbed = false; // Whether the joystick is currently being grabbed.
    public int pointerID; // The ID of the pointer grabbing the joystick.

    /*
    Inputs: Float for the size (radius) of the joystick. Vector2 for the position the joystick is in.

    Outputs: None. Creates the input device.

    Called by: ControlsView & GameView constructors.

    Calls: None.
    */
    public JoyStick(float _size, Vector2 _position) {
        size = _size;
        center = new Vector2(size / 2 + _position.x, size / 2 + _position.y);
        handlePos = center.clone();
    }

    /*
    Inputs: Integer index of the pointer,

    Outputs: None. Tells the joystick it has been grabbed by a specific pointer ID.

    Called by: GameView.onTouch()

    Calls: None.
    */
    public void grab(int newPointerIndex){
        isGrabbed = true;
        pointerID = newPointerIndex;
    }

    /*
    Inputs: None

    Outputs: None. When the player releases the joySick.

    Called by: GameView.onTouch(), resetGame()

    Calls: none.
    */
    public void release(){
        isGrabbed = false;
        handlePos = center;
        pointerID = -1;
    }

    /*
    Inputs: Canvas to be drawn on.

    Outputs: None. Draws the joystick to the passed canvas.

    Called by: GameView.draw(), ControlsView.draw()

    Calls: Paint and Canvas methods.
    */
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setARGB(100, 255, 255, 255);
        canvas.drawCircle(center.x, center.y, size, paint);
        paint.setARGB(150, 150, 150, 150);
        canvas.drawCircle(handlePos.x, handlePos.y, size * handleSizePercentage, paint);
    }

    /*
    Inputs: none.

    Outputs: Returns a Vector2 representing the distance the handle is from the center.

    Called by: GameView.draw(), Player.update()

    Calls: Vector methods for calculation.
    */
    public Vector2 getHandlePosition(){
        Vector2 returnVector = handlePos.clone();
        returnVector.subtract(center);
        returnVector.divide(size);
        return returnVector; // Returns difference.
    }

    /*
    Inputs: Vector 2 representing where the player is touching on the screen.

    Outputs: None. Tells the joystick where it's new position is.

    Called by: GameView.onTouch()

    Calls: Vector2 calls.
    */
    public void input(Vector2 position) {
        handlePos = position;
        float distance = Vector2.getDistance(handlePos, center);
        if(distance > size){
            Vector2 newPos = Vector2.getNormalized(new Vector2(handlePos.x - center.x, handlePos.y - center.y));
            newPos.scale(size);
            newPos.add(center);
            handlePos = newPos;
        }
    }
}
