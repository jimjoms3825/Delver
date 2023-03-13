package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: October 7th, 2022

    Class: WizardSpell (extends GameObject)
    Description: The wizard boss' spell. Spawns with random scale and only interacts with players and walls.

 */

public class WizardSpell extends GameObject{
    private Animator animator;
    private float damage; // The damage inflicted on hit.
    public Vector2 direction; // The direction of the projectile.
    private float speed = 10; // The speed used to scale the direction vector.
    private boolean hit = false; // Boolean flag for when the projectile hits something.

    /*
    Inputs: Float representing the amount of damage dealt on impact. Degrees representing the direction of the projectile

    Outputs: None.

    Called by: WizardBoss.update(), WizardBoss.changeState()

    Calls:Super
    */
    WizardSpell(float _damage, float degrees){
        super();
        distanceCulling = true;
        animator = new Animator();
        animator.scale = 0.25f + (float)Math.random() * 0.5f;
        Bitmap source = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.spell_bolt);
        animator.addAnimation(new Animation("Bullet", source, 1, 4, 4, 5));
        source = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.spell_hit);
        animator.addAnimation(new Animation("Explosion", source, 1, 5, 5, 5, true));

        damage = _damage;
        direction = Vector2.degreesToVector(degrees);
        velocity = direction.clone();
        velocity.scale(speed);
    }

    /*
    Inputs: None.

    Outputs: None. Initializes the object.

    Called by: constructor

    Calls:
    */
    @Override
    protected void initialize() {
        canCollide = false;
        isTrigger = true;
        distanceCulling = true;
    }

    /*
    Inputs: None.

    Outputs: None. Controls update logic.

    Called by: GameView loop.

    Calls: move()
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
        if(animator.getAnimation().finished == true){ // When the animation is done, remove.
            toRemove = true;
        }
    }

    /*
    Inputs: GameObject collided with.

    Outputs: None. Handles collision.

    Called by: GameView loop

    Calls: GameObject.hit()
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

    /*
    Inputs: None.

    Outputs: Returns a rotated animation based on direction.

    Called by: GameView loop

    Calls: Matrix, bitmap, animator methods.
    */
    @Override
    public Bitmap getDrawable() {
        Matrix matrix = new Matrix();
        matrix.preRotate(direction.getAngle());
        lastDrawable = animator.getBitmap();
        lastDrawable = Bitmap.createBitmap(lastDrawable, 0, 0,
                lastDrawable.getWidth(), lastDrawable.getHeight(), matrix, false);

        return lastDrawable;
    }
}
