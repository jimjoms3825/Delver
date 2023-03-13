package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 3)
    Name: James Bombardier
    Date: October 7, 2022

    Class: KnightSwordSpell (extends GameObject)
    Description: A projectile shot by the KnightBoss class.

 */

public class KnightSwordSpell extends GameObject{
    private Sprite sprite;
    private float damage; // The damage inflicted on hit.
    public Vector2 direction; // The direction of the projectile.
    private float speed = 15; // The speed used to scale the direction vector.
    private boolean hit = false; // Boolean flag for when the projectile hits something.
    private int spin = 0;

    /*
    Inputs: Float for damage, and a float for the direction of the projectile (in degrees).

    Outputs: None. Creates the object.

    Called by: KnightBoss.update()

    Calls: super(), sprite and Vector2 calls.
    */
    KnightSwordSpell(float _damage, float degrees){
        super();
        sprite = new Sprite(BitmapFactory.decodeResource(MainActivity.instance.getResources(), R.drawable.weapon_lavish_sword), 1);
        damage = _damage;
        distanceCulling = true;
        direction = Vector2.degreesToVector(degrees);
        velocity = direction.clone();
        velocity.scale(speed);
    }

    /*
    Inputs: None

    Outputs: None. Initializes the object.

    Called by: Constructor.

    Calls: Super.initialize()
    */
    @Override
    protected void initialize() {
        super.initialize();
        canCollide = false;
        isTrigger = true;
    }

    /*
    Inputs:None.

    Outputs: None. Controls the update logic.

    Called by: GameView loop

    Calls: Move()
    */
    @Override
    public void update() {
        if(Player.instance == null) { return; }
        if(hit){ // If the projectile has collided with something.
            isTrigger = false;
            canCollide = false;
            toRemove = true;
            velocity = Vector2.zero;
            return;
        }
        move();
        if(Vector2.getDistance(Player.instance.position, position) > 1500){ // Remove if too far from player.
            toRemove = true;
        }
    }

    /*
    Inputs: The other GameObject collided with.

    Outputs: None.

    Called by: GameView loop

    Calls: GameObject.hit().
    */
    @Override
    public void onCollision(GameObject other) {
        if((Player.class.isInstance(other) || Tile.class.isInstance(other)) && other.canCollide){ // Explode if hits enemy and deal damage.
            Vector2 knockBack = Vector2.getNormalized(direction);
            knockBack.scale(20);
            other.hit(damage, knockBack);
            hit = true;
        }
    }

    //
    /*
    Inputs: None.

    Outputs:Returns the next animation frame rotated.

    Called by: GameView loop.

    Calls: Matrix and Bitmap methods.
    */
    @Override
    public Bitmap getDrawable() {
        Matrix matrix = new Matrix();
        spin += 10;
        matrix.preRotate(direction.getAngle() + spin);
        lastDrawable = Bitmap.createBitmap(sprite.getBitmap(), 0, 0,
                sprite.getBitmap().getWidth(), sprite.getBitmap().getHeight(), matrix, false);
        return lastDrawable;
    }
}
