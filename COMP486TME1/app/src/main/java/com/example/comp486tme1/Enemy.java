package com.example.comp486tme1;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Enemy (Abstract; Extends GameObject)
    Description: An abstract class which forms the basic logic and interface for all ingame enemies.

 */
public abstract class Enemy extends GameObject {

    public float strength;
    public float knockBackStrength;
    public float maxHealth;
    public float health;
    public float speed;
    public boolean killed = false; // Whether the player got the kill. Otherwise, was a culling situation.

    protected int lootOnDeath; // How much loot this enemy type will drop when they die.

    /*
    Inputs: None.

    Outputs: None. Constructor.

    Called by: Inherited classes.

    Calls: Gameobject constructor.
    */

    public Enemy(){
        super();
    }

    /*
    Inputs: Vector2 for the position of the spawned enemy.

    Outputs: None. Spawns an enemy at passed position.

    Called by: Inherited classes

    Calls: Super constructor
    */
    public Enemy(Vector2 position){
        super(position);
    }

    /*
    Inputs: None.

    Outputs: None. Sets up the default gameobject and enemy variables.

    Called by: Gameobject Constructor.

    Calls: None.
    */
    @Override
    protected void initialize() {
        collisionDetection = true;
        isStatic = false;
        canCollide = true;
        maxHealth = maxHealth * (1 + GameView.difficultyModifier / 5); // a fifth of the difficulty modifier.
        strength = strength * (1 + GameView.difficultyModifier / 5); // a fifth of the difficulty modifier.
        speed = speed * (1 + GameView.difficultyModifier / 5); // a fifth of the difficulty modifier.
        health = maxHealth;
        drawOrder = 1;
    }

    /*
    Inputs: Gameobject that was collide with.

    Outputs: None. Applies damage to the player if they were passed as the collision.

    Called by: GameView main loop.

    Calls: Player.hit method
    */
    @Override
    public void onCollision(GameObject other){
        //Only care if enemy is colliding with player.
        if(!Player.class.isInstance(other) || Player.instance == null) { return; }
        Vector2 knockBack = new Vector2();
        knockBack.x = Player.instance.getCenter().x - getCenter().x;
        knockBack.y = Player.instance.getCenter().y - getCenter().y;
        knockBack.normalize();
        knockBack.scale(knockBackStrength);
        Player.instance.hit(strength, knockBack);
    }

    //Takes damage and knock-back on hit.
    /*
    Inputs: Float representing damage, Vector2 representing the amount of knockback recieved.

    Outputs: None. Damages the enemy and knocks them back.

    Called by: Player Projectile.

    Calls:
    */
    @Override
    public void hit(float damage, Vector2 knockBack) {
        if(killed) {return;}
        health -= damage;
        knockBackVelocity = knockBack;
        if (health <= 0) {
            killed = true;
            toRemove = true;
            int lootNumber = (int)((lootOnDeath / 2) + lootOnDeath * Math.random() / 2);
            for(int i = 0; i < lootNumber; i++){
                GameObject toDrop;
                float roll = (float)Math.random();
                if(roll < 0.98){
                    toDrop = new CoinPickup();
                }else{
                    toDrop = new SpellPickup();
                }
                toDrop.position = position.clone();
                toDrop.position.add(new Vector2(getDrawable().getWidth() / 2, getDrawable().getHeight() / 2));
                toDrop.velocity = new Vector2((1 - (float) Math.random() * 2), (1 - (float) Math.random() * 2));
                toDrop.velocity.normalize();
                toDrop.velocity.scale(5 + (float)Math.random() * 5);
            }
        }
    }

    /*
    Inputs: None

    Outputs: None. Increments the enemiesKilled variable of GameView.

    Called by: GameView Main loop

    Calls: None.
    */
    @Override
    public void onDestroy() {
        if(killed) {
            GameView.enemiesKilled++;
        }
    }
}
