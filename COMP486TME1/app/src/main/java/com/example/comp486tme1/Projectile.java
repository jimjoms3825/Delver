package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Projectile
    Description: A class which represents the Player projectiles.

 */
public class Projectile extends GameObject{
    private Animator animator; // Projectile animator.
    private float damage; // The damage inflicted on hit.
    public Vector2 direction; // The direction of the projectile.
    private float speed = 30; // The speed used to scale the direction vector.
    private boolean hit = false; // Boolean flag for when the projectile hits something.
    public boolean explosive = false;

    /*
    Inputs: Float for damage, Vector2 for direction of the sprite, and an animation for the projectile movement.

    Outputs: Creates a projectile with the specified parameters.

    Called by: Weapon.fire()

    Calls: None.
    */
    Projectile(float _damage, Vector2 _direction, Animation spellAnim){
        super();
        distanceCulling = true;
        damage = _damage;
        direction = _direction;
        velocity = direction.clone();
        velocity.scale(speed);
        animator = new Animator();
        animator.scale = 0.5f; // Updates the scale of the animator to represent how powerful the projectile is.
        if(animator.scale > 1.5){ animator.scale = 1.5f; }

        animator.addAnimation(spellAnim);
        Bitmap source = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.spell_hit);
        animator.addAnimation(new Animation("Explosion", source, 1, 5, 5, 5, true));
    }

    /*
    Inputs: None

    Outputs: None.

    Called by: Constructor

    Calls: super.initialize()
    */
    @Override
    protected void initialize() {
        super.initialize();
        canCollide = false;
        isTrigger = true;
    }

    /*
    Inputs: None

    Outputs: None. Handles update logic

    Called by: GameView loop

    Calls: move()
    */
    @Override
    public void update() {
        if(Player.instance == null) { return; }
        if(hit){ // If the projectile has collided with something.
            if(!explosive){
                isTrigger = false;
            }
            canCollide = false;
            velocity = Vector2.zero;
            if(animator.getAnimation().finished == true){ // When the animation is done, remove.
                toRemove = true;
            }
            return;
        }
        move();
        if(Vector2.getDistance(Player.instance.position, position) > 1500){ // Remove if too far from player.
            toRemove = true;
        }
    }

    /*
    Inputs: Gameobject collided with

    Outputs: None. Interacts with tiles and player.

    Called by: GameView loop

    Calls: SoundManager.playSound(), Player.hit()
    */
    @Override
    public void onCollision(GameObject other) {
        if(Tile.class.isInstance(other) && other.canCollide){ // Explode if hits wall.
            if(!hit){
                animator.playAnimation("Explosion");
            }
            SoundManager.instance.playSound(SoundManager.Sounds.Explosion, position);
            if(explosive && !hit) {
                animator.scale = 3; //increase the size of blast if explosive.
                position.x -= animator.getAnimation().frames.get(0).getWidth() * 1.5;
                position.y -= animator.getAnimation().frames.get(0).getHeight() * 1.5;
            }
            hit = true;
        }
        else if(Enemy.class.isInstance(other)){ // Explode if hits enemy and deal damage.
            if(!hit){
                animator.playAnimation("Explosion");
            }
            SoundManager.instance.playSound(SoundManager.Sounds.Explosion, position);
            Vector2 knockBack = Vector2.getNormalized(direction);
            knockBack.scale(20);
            other.hit(damage, knockBack);
            if(explosive && !hit) {
                animator.scale = 3; //increase the size of blast if explosive.
                position.x -= animator.getAnimation().frames.get(0).getWidth() * 1.5;
                position.y -= animator.getAnimation().frames.get(0).getHeight() * 1.5;
            }
            hit = true;
        }
    }

    /*
    Inputs: None

    Outputs: Returns the next frame of animation rotated.

    Called by: GameView loop

    Calls: None.
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
