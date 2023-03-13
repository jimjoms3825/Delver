package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 3)
    Name: James Bombardier
    Date: October 7, 2022

    Class: Orc (extends Enemy)
    Description: A ranged enemy that has a bow and shoots arrows. tries to stay within a specified
    distance from the player, while also being close to them.

 */

public class Orc extends Enemy{

    private Animator animator;
    private Bow bow; // Weapon.
    private Vector2 playerDirection; // Stored as a variable to reduce calls.

    private final int FramesToShoot = 60; // The number of frames before the orc will shoot.
    private int shootTimer = 0; // Timing out the shooting.

    /*
    Inputs: None.

    Outputs: None. Initializes the enemy.

    Called by: constructor.

    Calls: super.initialize()
    */
    @Override
    public void initialize(){
        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.orc);
        strength = 5;
        maximumMoveSpeed = 6;
        speed = maximumMoveSpeed;
        maxHealth = 30;
        knockBackStrength = 0;
        animator = new Animator();
        animator.addAnimation(new Animation("Idle", spriteSheet, 5, 8, 8, 12, true));
        animator.addAnimation(new Animation("Run", spriteSheet, 1, 4, 8, 8));
        health = maxHealth;
        this.lootOnDeath = 12;

        playerDirection = new Vector2();
        bow = new Bow(this);
        super.initialize();
    }

    /*
    Inputs: None.

    Outputs: None. Controls thinking logic

    Called by: GameView loop

    Calls: think(), bow.pointBow(), bow.shoot(), move()
    */
    @Override
    public void update() {
        think();
        bow.pointBow(playerDirection);
        if(shootTimer++ > FramesToShoot){
            shootTimer = 0;
            bow.shoot();
        }
        move();
    }

    /*
    Inputs: none.

    Outputs: Returns a flipped bitmap based on velocity.

    Called by: GameView loop.

    Calls:  None.
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
    Inputs: None.

    Outputs: None. Provides simple logic for movement.

    Called by: update()

    Calls: none.
    */
    private void think(){
        if(Player.instance == null) { return; }
        float playerDistance = Vector2.getDistance(position, Player.instance.position);
        playerDirection = Player.instance.getCenter();
        playerDirection.subtract(getCenter());
        playerDirection.normalize();
        Vector2 targetVelocity = playerDirection.clone();

        if(playerDistance < 200){
            targetVelocity.scale(-maximumMoveSpeed);
        }
        else if (playerDistance > 750){
            targetVelocity.scale(maximumMoveSpeed);
        }
        else{
            targetVelocity = Vector2.zero;
        }
        velocity.Lerp(targetVelocity, 0.05f);
    }

    @Override
    public void onCollision(GameObject other) {
        // No collision for orc.
    }

    private class Bow extends GameObject{
        private Vector2 weaponDirection;
        private Sprite weaponSprite;
        private Vector2 weaponOffset;
        private Enemy parent; // Stored to keep positions together.

        /*
        Inputs: Enemy that is holding the bow.

        Outputs: None.

        Called by: Orc.initialize()

        Calls: super()
        */
        public Bow(Enemy _parent){
            super();
            parent = _parent;
        }

        /*
        Inputs: None.

        Outputs: None. Initializes the bow.

        Called by: constructor.

        Calls: getDrawable(), pointBow()
        */
        @Override
        protected void initialize() {
            weaponSprite = new Sprite(BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                    R.drawable.weapon_bow), .8f);
            weaponDirection = new Vector2();
            weaponOffset = new Vector2();
            canCollide = false;
            isStatic = false;
            collisionDetection = false;
            drawOrder = 3;

            // Sets Bow rotation and offset.
            getDrawable();
            pointBow(new Vector2(1, 1));
        }

        /*
        Inputs: None.

        Outputs: Returns a rotated bitmap.

        Called by: GameView loop

        Calls: None.
        */
        @Override
        public Bitmap getDrawable() {
            Matrix matrix = new Matrix();
            matrix.preRotate(weaponDirection.getAngle());
            lastDrawable = Bitmap.createBitmap(weaponSprite.getBitmap(), 0, 0,
                    weaponSprite.getBitmap().getWidth(), weaponSprite.getBitmap().getHeight(), matrix, false);
            return lastDrawable;
        }

        /*
        Inputs: None.

        Outputs: none. controls update logic.

        Called by: GameView loop

        Calls: super.update()
        */
        @Override
        public void update() {
            if(parent.toRemove){
                toRemove = true;
                toDraw = false;
            }
        }

        /*
        Inputs: Vector2 direction vector for aiming bow and projectiles.

        Outputs: none.

        Called by: Orc.update()

        Calls: getBounds(), vector calls.
        */
        public void pointBow(Vector2 input) {
            if (parent == null) {
                return;
            }
            if (input.x != 0 && input.y != 0) { // NonZero input
                weaponDirection = Vector2.getNormalized(input);
                if (lastDrawable != null) {
                    weaponOffset = new Vector2(lastDrawable.getWidth(), lastDrawable.getHeight());
                    weaponOffset.x = weaponOffset.x * input.x;
                    weaponOffset.y = weaponOffset.y * input.y;
                    weaponOffset.scale(0.5f);
                }
                if (parent.lastDrawable != null) {
                    weaponOffset.add(new Vector2(parent.lastDrawable.getWidth() / 4,
                            parent.lastDrawable.getHeight() / 2));
                }
                getBounds();
            }
            position = parent.position.clone();
            position.add(weaponOffset);
        }

        /*
        Inputs: None.

        Outputs: None. Shoots arrow.

        Called by: Orc.update()

        Calls: None.
        */
        public void shoot(){
            GameObject arrow = new Arrow(strength, playerDirection);
            arrow.position = position.clone();
        }

        private class Arrow extends GameObject {
            private Sprite sprite;
            private float damage; // The damage inflicted on hit.
            public Vector2 direction; // The direction of the projectile.
            private float speed = 10; // The speed used to scale the direction vector.
            private boolean hit = false; // Boolean flag for when the projectile hits something.

            /*
            Inputs: float for damage. Vector2 for direction of projectile.

            Outputs: None.

            Called by: Bow.shoot()

            Calls: super()
            */
            public Arrow(float _damage, Vector2 _direction){
                super();
                Bitmap source = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.weapon_arrow);
                sprite = new Sprite(source, 0.8f);

                damage = _damage;
                direction = _direction;
                velocity = direction.clone();
                velocity.scale(speed);
            }

            /*
            Inputs:None.

            Outputs: None.

            Called by: constructor

            Calls: super.initialize().
            */
            @Override
            protected void initialize() {
                super.initialize();
                canCollide = false;
                isTrigger = true;
            }

            /*
            Inputs: None.

            Outputs: None. Handles update logic.

            Called by: GameView loop

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
                if(Vector2.getDistance(Player.instance.position, position) > 1500){ // Remove if too far from player.
                    toRemove = true;
                }
                if(hit){ // When the animation is done, remove.
                    toRemove = true;
                }
            }

            /*
            Inputs: Gameobject collided with

            Outputs: None. Stops and hits if player or wall.

            Called by: GameView loop.

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

            Outputs: Returns a rotated bitmap.

            Called by: GameView loop

            Calls: None.
            */
            @Override
            public Bitmap getDrawable() {
                Matrix matrix = new Matrix();
                matrix.preRotate(direction.getAngle() + 90);
                lastDrawable = sprite.getBitmap();
                lastDrawable = Bitmap.createBitmap(lastDrawable, 0, 0,
                        lastDrawable.getWidth(), lastDrawable.getHeight(), matrix, false);
                return lastDrawable;
            }
        }
    }
}
