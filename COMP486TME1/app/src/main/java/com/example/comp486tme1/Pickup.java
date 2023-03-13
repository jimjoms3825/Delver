package com.example.comp486tme1;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Pickup (Abstract; extends GameObject)
    Description: This abstract class represents any interactable object (coins, chest, powerups) in
    an environment and provides the API for handling them.

 */
public abstract class Pickup extends GameObject {

    protected int framesToPickup = 30; // The amount of updates needed before the item can be picked up.

    /*
    Inputs: None.

    Outputs: None. Initializes the pickup.

    Called by: constructor.

    Calls: None.
    */
    @Override
    protected void initialize() {
        collisionDetection = true;
        isTrigger = true;
        canCollide = false;
        isStatic = false;
        drawOrder = 1;
    }

    /*
    Inputs: None.

    Outputs: None. Handles the movement logic of the item.

    Called by: GameView loop

    Calls:
    */
    @Override
    public void update(){
        if(framesToPickup-- > 0) {
            super.move();
            velocity.scale(0.95f); // scale down velocity.
            return;
        }
        else{
            collisionDetection = false;
        }
        float playerDistance = Vector2.getDistance(position, Player.instance.position);
        if(playerDistance < 250) {
            Vector2 newVel = Player.instance.getCenter();
            newVel.subtract(getCenter());
            newVel.normalize();
            newVel.scale( 2 + (250 / playerDistance) * 5);
            velocity.Lerp(newVel, 0.1f);
        }
        else {
            velocity.Lerp(Vector2.zero, 0.1f);
        }
        super.move();

    }

    /*
    Inputs: None.

    Outputs: None. The behaviour called on pickup.

    Called by: Pickup.onCollision

    Calls: none.
    */
    protected abstract void pickup();

    /*
    Inputs: None.

    Outputs: Bitmap.

    Called by: GameView loop

    Calls: None.
    */
    public abstract Bitmap getDrawable();

    /*
    Inputs: GameObject collided with.

    Outputs: None. Calls pickup behaviour if player.

    Called by:GameView loop

    Calls: pickup()
    */
    @Override
    public void onCollision(GameObject other){
        if(Player.class.isInstance(other)) {
            pickup();
        }
        else if(Tile.class.isInstance(other)){ // Makes the objects bounce off walls.
            AABB bounds = getBounds();

            AABB otherBounds = other.getBounds();
            AABB xVelBounds = bounds.clone();
            xVelBounds.min.x += velocity.x;
            xVelBounds.max.x += velocity.x;
            AABB yVelBounds = bounds.clone();
            yVelBounds.min.y += velocity.y;
            yVelBounds.max.y += velocity.y;
            //Check if collision is from x axis.
            if(xVelBounds.isInside(otherBounds)){
                velocity.x = -velocity.x;
            }
            //check if collision is from y axis.
            if(yVelBounds.isInside(otherBounds)){
                velocity.y = -velocity.y;
            }
        }
    }
}
