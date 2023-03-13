package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Weapon (extends GameObject)
    Description: The players weapon. Shoots Projectiles, and takes input.

 */
public class Weapon extends GameObject{
    private Sprite weaponSprite; // Simple sprite.
    private Vector2 weaponDirection; // The direction vector of the weapon.
    private Vector2 weaponOffset; // The offset of the weapon (adjusted by input.)

    public float damage = 5; // Damage of weapon.
    public float spellPower = 1;
    public int baseCastSpeed = 30; // How many updates between shots.
    public int castSpeed = baseCastSpeed;
    private int lastCastFrames = 0; // Counter for cast speed.

    public Sprite icon; // For display on buttons.
    public Animation spellAnimation;

    public enum weaponType {Bolt, Twin, Blast, Explosion, Rapid}
    public weaponType thisType;

    /*
    Inputs: None.

    Outputs: None.

    Called by: constructor.

    Calls: setType(), setInput()
    */
    @Override
    protected void initialize() {
        weaponSprite = new Sprite(BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.weapon_red_magic_staff), 0.5f);
        weaponDirection = new Vector2();
        weaponOffset = new Vector2();
        canCollide = false;
        isStatic = false;
        toDraw = false;
        collisionDetection = false;
        drawOrder = 3;

        // Sets staff rotation and offset.
        getDrawable();
        setInput(new Vector2(1, -1), false);

        setType(weaponType.Bolt); //default
    }

    /*
    Inputs: None.

    Outputs: None. increments lastCastFrames.

    Called by: GameView loop.

    Calls: None.
    */
    @Override
    public void update() {
        if(lastCastFrames < castSpeed){ lastCastFrames++; }
    }

    /*
    Inputs: Vector2 as a normalized input direction vector. Boolean represents whether the staff should shoot or not.

    Outputs: None. Sets the weapon direction and offset based on passed input. Also fires shots if there is input.

    Called by: Player.update()

    Calls: getBounds(), fire()
    */
    public void setInput(Vector2 input, boolean shoot){
        if(Player.instance == null) { return; }
        if(input.x != 0 && input.y != 0) { // NonZero input
            weaponDirection = Vector2.getNormalized(input);
            if(lastDrawable != null){
                weaponOffset = new Vector2(lastDrawable.getWidth(), lastDrawable.getHeight());
                weaponOffset.x = weaponOffset.x * input.x;
                weaponOffset.y = weaponOffset.y * input.y;
                weaponOffset.scale(0.5f);
            }
            if(Player.instance.lastDrawable != null){
                weaponOffset.add(new Vector2(Player.instance.lastDrawable.getWidth() / 4,
                        Player.instance.lastDrawable.getHeight() / 2));
            }
            getBounds();
            if(lastCastFrames >= castSpeed && shoot){ // can cast.
                fire(input);
            }
        }
        if(Player.instance != null){
            position = Player.instance.position.clone();
            position.add(weaponOffset);
        }
    }

    /*
    Inputs: None.

    Outputs: Returns rotated sprite based on last joystick position.

    Called by: GameView loop

    Calls: None.
    */
    @Override
    public Bitmap getDrawable() {
        Matrix matrix = new Matrix();
        matrix.preRotate(weaponDirection.getAngle() + 90);
        lastDrawable = Bitmap.createBitmap(weaponSprite.getBitmap(), 0, 0,
                weaponSprite.getBitmap().getWidth(), weaponSprite.getBitmap().getHeight(), matrix, false);
        return lastDrawable;
    }

    /*
    Inputs: The direction to fire.

    Outputs: Non. Fires a projectile in the given direction.

    Called by: setInput()

    Calls: SoundManager.playSound()
    */
    private void fire(Vector2 dir){
        castSpeed = (int)(baseCastSpeed * Player.instance.weaponCastReduction); // adjust cast speed so player can speed up casting.

        Projectile proj = new Projectile(damage * spellPower, Vector2.getNormalized(dir), spellAnimation);
        proj.position = Player.instance.getCenter();
        proj.position.x += dir.x * 5;
        proj.position.y += dir.y * 5;
        proj.position.subtract(proj.getSpriteCenter());
        if(thisType == weaponType.Rapid || thisType == weaponType.Blast){
            proj.direction.x += -0.2 + Math.random() * 0.4f;
            proj.direction.y += -0.2 + Math.random() * 0.4f;
            proj.direction.normalize();
            proj.velocity = proj.direction;
            proj.velocity.scale(proj.maximumMoveSpeed);
        }
        if(thisType == weaponType.Blast){
            for(int i = 0; i < 5; i++){
                proj = new Projectile(damage, Vector2.getNormalized(dir), spellAnimation);
                proj.position = Player.instance.getCenter();
                proj.position.x += dir.x * 5;
                proj.position.y += dir.y * 5;
                proj.position.subtract(proj.getSpriteCenter());
                proj.direction.x += -0.2 + Math.random() * 0.4f;
                proj.direction.y += -0.2 + Math.random() * 0.4f;
                proj.direction.normalize();
                proj.velocity = proj.direction;
                proj.velocity.scale(proj.maximumMoveSpeed);
            }
        }
        if(thisType == weaponType.Explosion){
            proj.explosive = true;
        }
        SoundManager.instance.playSound(SoundManager.Sounds.Cast);
        lastCastFrames = 0;
    }

    /*
    Inputs: WeaponType to be created.

    Outputs: None.

    Called by: initialize(), player.addWeapon()

    Calls: None.
    */
    public void setType(weaponType newType){
        Bitmap spriteSheet;
        int frameWidth;
        int frameHeight;
        int frames = 4;
        thisType = newType;
        Bitmap source = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.spell_bolt);;
        switch (newType){
            case Bolt:
                spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.spell_bolt);
                frameWidth = spriteSheet.getWidth() / frames;
                frameHeight = spriteSheet.getHeight();
                icon = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 1);
                source = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.spell_bolt);
                break;
            case Twin:
                spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.spell_crossed);
                frames = 6;
                frameWidth = spriteSheet.getWidth() / frames;
                frameHeight = spriteSheet.getHeight();
                icon = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 1);
                source = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.spell_crossed);
                damage = 15;
                baseCastSpeed = 50;
                break;
            case Blast:
                spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.spell_wave);
                frameWidth = spriteSheet.getWidth() / frames;
                frameHeight = spriteSheet.getHeight();
                icon = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 0.8f);
                source = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.spell_wave);
                damage = 1.5f;
                break;
            case Rapid:
                spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.spell_wave);
                frameWidth = spriteSheet.getWidth() / frames;
                frameHeight = spriteSheet.getHeight();
                icon = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 1);
                source = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.spell_wave);
                damage = 2.5f;
                baseCastSpeed = 12;
                break;
            case Explosion:
                spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.spell_charged);
                frames = 6;
                frameWidth = spriteSheet.getWidth() / frames;
                frameHeight = spriteSheet.getHeight();
                icon = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 0.8f);
                source = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.spell_bolt);
                damage = 1;
                baseCastSpeed = 80;
                break;
        }
        spellAnimation = new Animation("Spell", source, 1, frames, frames, 5);
    }
}
