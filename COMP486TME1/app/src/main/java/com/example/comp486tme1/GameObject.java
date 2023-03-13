package com.example.comp486tme1;

import android.graphics.Bitmap;

import java.util.ArrayList;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: GameObject (Abstract)
    Description: The abstract class that encompasses all (non-ui) drawn and updated objects which
    exist in the simulated world. This class contains physics and collision handling, and provides
    an interface for other classes.

 */
public abstract class GameObject {
    protected Vector2 position;
    protected Vector2 rotation;
    protected Vector2 velocity;
    protected Vector2 knockBackVelocity;
    protected boolean hasKnockback = true;
    protected float maximumMoveSpeed = 50;

    protected Bitmap lastDrawable;

    protected boolean isStatic = false; // Whether objects can move.
    protected boolean collisionDetection = true; // Whether objects perform their own collision detection
    protected  boolean canCollide = true; //whether objects can collide with this.
    protected  boolean isTrigger = false; //whether objects will trigger a collision response.
    public int drawOrder = 0; // from 0-3 the order in which objects are drawn.

    public boolean toRemove = false; // If true, removes the object from updates and drawing at the end of frame.
    public boolean toDraw = true; //Determines whether this object will be drawn in the main game loop.
    public boolean distanceCulling = false; // If true, will delete objects once out of render and update range.

    protected static ArrayList<GameObject> gameObjects; // All gameobjects present in scene.
    protected static ArrayList<GameObject> collidableObjects; // All objects which can be collided with. Updated each frame.

    /*
    Inputs: None.

    Outputs: Creates a simple gameobject.

    Called by: Inheriting classes.

    Calls: Initialize(). Constructors. Adds this gameobject to the list of gameobjects.
    */
    protected GameObject(){
        position = new Vector2();
        rotation = new Vector2();
        velocity = new Vector2();
        knockBackVelocity = new Vector2();
        if(gameObjects == null){
            gameObjects = new ArrayList<>();
        }
        gameObjects.add(this);
        initialize();
    }

    /*
    Inputs: Vector2

    Outputs: None. Creates a simple gameObject at a given position.

    Called by: Inherited classes.

    Calls: Initialize(). Constructors. Adds this gameobject to the list of gameobjects.
    */
    protected GameObject(Vector2 _position){
        position = _position;
        rotation = new Vector2();
        velocity = new Vector2();
        knockBackVelocity = new Vector2();
        if(gameObjects == null){
            gameObjects = new ArrayList<>();
        }
        gameObjects.add(this);
        initialize();
    }

    //Methods to be overridden.
    protected void initialize() {}
    public void update() {}
    public Bitmap getDrawable() { return null; }

    /*
    Inputs: None.

    Outputs: None. Moves gameobject each frame. Must be explicitly called.

    Called by: Inheriting classes.

    Calls: HandleCollision, HandleOverlapping.
    */
    protected void move(){
        if(isStatic) return;
        if(velocity.x > maximumMoveSpeed) velocity.x = maximumMoveSpeed;
        if(velocity.x < -maximumMoveSpeed) velocity.x = -maximumMoveSpeed;
        if(velocity.y > maximumMoveSpeed) velocity.y = maximumMoveSpeed;
        if(velocity.y < -maximumMoveSpeed) velocity.y = -maximumMoveSpeed;
        knockBackVelocity.scale(0.8f);
        if(knockBackVelocity.x < 1 && knockBackVelocity.x > -1) { knockBackVelocity.x = 0; }
        if(knockBackVelocity.y < 1 && knockBackVelocity.y > -1) { knockBackVelocity.y = 0; }

        Vector2 velocityToBeAdded = new Vector2();
        if(hasKnockback){
            velocityToBeAdded.add(knockBackVelocity);
        }
        velocityToBeAdded.add(velocity);
        if(collisionDetection){
            handleCollisions(velocityToBeAdded);
            handleOverlapping();
            ensureInBounds();
        }
        position.add(velocityToBeAdded);
    }

    /*
    Inputs: None.

    Outputs: None. Replaces overlapping gameobjects to avoid deadlock condition for movement.

    Called by: move().

    Calls: Vector2 methods.
    */
    protected void handleOverlapping(){
        if(collidableObjects == null) {return;}
        AABB bounds = getBounds();
        for (GameObject go : collidableObjects) {
            if (go.equals(this) || !go.canCollide || !canCollide ) {
                continue;
            }
            AABB other = go.getBounds();
            if (bounds.isInside(other)) {
                //There is overlap at this point.
                AABB newBoundsThis = bounds.clone();
                Vector2 direction = position.clone();
                direction.subtract(go.position);
                direction.normalize();
                direction.scale(5); // Move the object away 5 pixels at a time.
                for(int i = 0; i < 10; i++){
                    newBoundsThis.min.add(direction);
                    newBoundsThis.max.add(direction);
                    if(newBoundsThis.isInside(other)) { break; }
                }
                position = newBoundsThis.min;
                if(GameView.level == null) { return; }
                Tile tile = GameView.level.getTileAtPosition(position);
                if(tile != null && !tile.walkable){ // If placed out of bounds by this algorithm
                    //Place back in bounds at nearest tile.
                    position = GameView.level.getClosestWalkable(position).position.clone();
                }
            }
        }
    }

    /*
    Inputs: Vector2 representing how far the gameobject will move.

    Outputs: None. Ensures that gameObjects will not collide with another entity at their new position.

    Called by: move()

    Calls: Vector2 methods, AABB methods.
    */
    protected void handleCollisions(Vector2 velocityToBeAdded){
        //Checks if gameobject is colliding with walls.
        if(collidableObjects == null) {return;}
        AABB bounds = getBounds();
        AABB velBounds = getBounds().clone(); // Adds velocity to the bounding box.
        velBounds.min.add(velocityToBeAdded);
        velBounds.max.add(velocityToBeAdded);
        for (GameObject go : collidableObjects) {
            if(go.equals(this) || (!go.canCollide && !go.isTrigger)){ continue; }
            AABB other = go.getBounds();
            //Check if current position overlaps other to prevent entities stuck on each other
            if(velBounds.isInside(other)){ // going to collide based on change to velocity
                //pass collision object references
                go.onCollision(this);
                onCollision(go);
                //Stop here if triggerable but not collidable on either object
                if(!canCollide || !go.canCollide){ continue; }
                AABB xVelBounds = bounds.clone();
                xVelBounds.min.x += velocityToBeAdded.x;
                xVelBounds.max.x += velocityToBeAdded.x;
                AABB yVelBounds = bounds.clone();
                yVelBounds.min.y += velocityToBeAdded.y;
                yVelBounds.max.y += velocityToBeAdded.y;
                //Check if collision is from x axis.
                if(xVelBounds.isInside(other)){
                    velocityToBeAdded.x = 0;
                }
                //check if collision is from y axis.
                if(yVelBounds.isInside(other)){
                    velocityToBeAdded.y = 0;
                }
            }
        }
    }

    /*
    Inputs: None.

    Outputs: None. Ensures that the object is on a walkable tile.

    Called by: move()

    Calls: None.
    */
    protected void ensureInBounds(){
        if(GameView.level == null){return;}
        Tile tile = GameView.level.getTileAtPosition(position);
        if(tile == null || tile.walkable){
            return;
        }
        position = GameView.level.getClosestWalkable(position).position.clone();
    }

    /*
    Inputs: None.

    Outputs: returns an AABB representing the bounds of the GameObject based on the last drawn sprite.

    Called by: GameObject, Level, Orc.Bow, Pickup, Slime, Weapon.

    Calls: AABB methods.
    */
    public AABB getBounds(){
        AABB bounds = new AABB();
        bounds.min = position.clone();
        bounds.max = position.clone();
        if(lastDrawable != null){
            bounds.max.add(new Vector2(lastDrawable.getWidth(), lastDrawable.getHeight()));
        }
        return bounds;
    }

    /*
    Inputs: The GameObject collided with.

    Outputs: None. Called when a collidable or triggerable object hits another.

    Called by: HandleCollision()

    Calls: To be filled by inherited classes/
    */
    public void onCollision(GameObject other) {}

    /*
    Inputs: Float for damage amount, and Vector2 for knockback application.

    Outputs: None. Affects what happens when the player is hit. To be filled by inherited class.

    Called by: None.

    Calls: None.
    */
    public void hit(float damage, Vector2 knockBack) {}

    /*
    Inputs: None.

    Outputs: None. Allows inherited classes to apply logic for when they die.

    Called by:  GameView loop when object is being removed.

    Calls: None.
    */
    public void onDestroy() {}

    /*
    Inputs: None.

    Outputs: Vector2 representing the middle point of the last drawn sprite in world Coords.

    Called by: Various classes.

    Calls: Vector2 methods, Bitmap methods.
    */
    public Vector2 getCenter(){
        if(lastDrawable == null) { return position.clone(); }
        return new Vector2(position.x + lastDrawable.getWidth() / 2, position.y + lastDrawable.getHeight() / 2);
    }

    /*
    Inputs: None.

    Outputs: Vector2 representing the middle of the sprite in local coords.

    Called by: Weapon

    Calls: Bitmap calls.
    */
    public Vector2 getSpriteCenter(){
        if(lastDrawable == null) { getDrawable(); }
        return new Vector2(lastDrawable.getWidth() / 2, lastDrawable.getHeight() / 2);
    }

}
