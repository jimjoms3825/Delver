package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.util.LinkedList;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Player (Extends GameObject)
    Description: The player class, with methods for handling input.

 */
public class Player extends GameObject{

    private float movementEntropy = 0.85f; // How quickly the player loses velocity (inverted)
    public Animator animator; // Animator for the player sprites.
    public static Player instance; // For access by other scripts.
    public float health; // The players current Health.
    public float maxHealth; // The maximum health of the player.
    private int invulnFrames; // Counter for invulnerability frames.
    public Weapon currentWeapon; // The players weapon object.
    public LinkedList<Weapon> weapons; // Stores the players weapons.

    public boolean alive; // Whether the player has remaining life.

    public float weaponCastReduction = 1f; //The firerate multiplier.

    /*
    Inputs: None.

    Outputs: None. Sets up the player object.

    Called by: constructor.

    Calls: getDrawable()
    */
    @Override
    protected void initialize() {
        instance = this;
        alive = true;
        maxHealth = 100;
        health = maxHealth;
        maximumMoveSpeed = 8;
        animator = new Animator();
        Bitmap playerSpriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.player_wizard);
        animator.scale = 1;
        animator.addAnimation(new Animation("Idle", playerSpriteSheet, 1, 4, 8, 16));
        animator.addAnimation(new Animation("Run", playerSpriteSheet, 5, 8, 8, 10));
        canCollide = true;
        collisionDetection = true;
        getDrawable(); // Update the players last sprite for the weapon constructor.
        weapons = new LinkedList<Weapon>();
        currentWeapon = new Weapon();
        currentWeapon.toDraw = true;
        weapons.add(currentWeapon);

        drawOrder = 2;
    }

    /*
    Inputs: None

    Outputs: None

    Called by: GameView loop

    Calls: super.move()
    */
    @Override
    public void update() {
        calculateVelocity(GameView.getView().rightJoy.getHandlePosition());
        currentWeapon.setInput(GameView.getView().leftJoy.getHandlePosition(), true);
        move();
        invulnFrames--;
    }

    /*
    Inputs: None.

    Outputs: Returns a rotated sprite based on velocity.

    Called by: GameView loop, constructor.

    Calls: none.
    */
    @Override
    public Bitmap getDrawable() {
        Matrix matrix = new Matrix();
        Bitmap source = animator.getBitmap();
        if(velocity.x < 0){
            matrix.preScale(-1, 1);
        }
        lastDrawable = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
        return lastDrawable;
    }

    /*
    Inputs: Vector2 of joystick input.

    Outputs: None. Sets the players velocity based on the input from a joystick.

    Called by: update()

    Calls: None.
    */
    public void calculateVelocity(Vector2 input){
        if(input.x != 0 || input.y != 0){
            input.scale(maximumMoveSpeed);
            velocity = (input);
            animator.playAnimation("Run");
        } else {
            animator.playAnimation("Idle");
            velocity.scale(movementEntropy);

            if(velocity.x < 0.05 && velocity.x > -0.05) {velocity.x = 0;}
            if(velocity.y < 0.05 && velocity.y > -0.05) {velocity.y = 0;}
        }
    }

    /*
    Inputs: float for damage, and a vector 2 for scaled knockback.

    Outputs: None. Handles when a player receives damage from any source.

    Called by: Various enemies, projectiles, and traps.

    Calls: setInvulnFrames(), SoundManager.playSound()
    */
    @Override
    public void hit(float damage, Vector2 knockBack){
        if(invulnFrames > 0){
            return;
        }
        health -= damage;
        knockBackVelocity = knockBack;
        SoundManager.instance.playSound(SoundManager.Sounds.Hit);
        setInvulnFrames(30);
        if(health <= 0){
            GameView.getView().GameOver();
        }
    }


    /*
    Inputs: Int number of frames to be set to.

    Outputs: None. Sets the players invulnerability frames if less than current invulnerability frames (wont lose any this way).

    Called by: hit()

    Calls: None.
    */
    public void setInvulnFrames(int newFrames){
        if(newFrames >= invulnFrames){
            invulnFrames = newFrames;
        }
    }

    //Adds health (not over maximum).
    /*
    Inputs: Float amount of health to be added.

    Outputs: None. Adds health (not over maximum).

    Called by: PotionPickup.pickup()

    Calls: none.
    */
    public void addHealth(float healthAddition){
        health += healthAddition;
        if(health > maxHealth){
            health = maxHealth;
        }
    }

    /*
    Inputs: None.

    Outputs: Provides a 5% increase in weapon cast reduction.

    Called by: SpellPickup.pickup()

    Calls:
    */
    public void powerUpSpell(){
        if(weaponCastReduction <= 0) {return;}
        weaponCastReduction -= 0.01f;

    }

    /*
    Inputs: None.

    Outputs: None. Changes weapon to next weapon in weapon list.

    Called by: SpellButton.onClick()

    Calls: none.
    */
    public void changeWeapon(){
        boolean nextWeapon = false;
        currentWeapon.toDraw = false;
        for(Weapon weapon: weapons){
            if(nextWeapon){
                weapon.position = currentWeapon.position.clone();
                currentWeapon = weapon;
                currentWeapon.toDraw = true;
                return;
            }
            if(weapon == currentWeapon){
                nextWeapon = true;
            }
        }
        currentWeapon = weapons.get(0);
        currentWeapon.toDraw = true;
    }

    /*
    Inputs: Weapon Type representing the new weapons type to be added.

    Outputs: None. Adds weapon of passed type to player inventory.

    Called by: ShopButton.onClick().

    Calls: Weapon.setType()
    */
    public void addWeapon(Weapon.weaponType type){
        for (Weapon w: weapons){
            if(w.thisType == type) { return; }
        }
        Weapon newWeapon = new Weapon();
        newWeapon.setType(type);
        weapons.add(newWeapon);
    }
}
