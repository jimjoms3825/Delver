package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: October 7th, 2022

    Class: SlimeBoss (extends GameObject)
    Description: An object used for controlling the slime boss fight.

 */

public class SlimeBoss extends GameObject {

    public ArrayList<SlimeBossEnemy> slimeList; // The currently spawned slimes.
    public ArrayList<Spawn> spawns; // The slimes that need to be spawned.

    /*
    Inputs: A Vector 2 for positioning the boss.

    Outputs: Creates a slimeBoss object.

    Called by: LevelGenerator.

    Calls: None.
    */
    public SlimeBoss(Vector2 spawnPos){
        super(spawnPos);
        toDraw = false;
        canCollide = false;
        isTrigger = false;
        collisionDetection = false;
    }

    /*
    Inputs: None.

    Outputs: None. Provides update logic.

    Called by: GameView loop

    Calls: spawnSlimes()
    */
    @Override
    public void update() {
        spawnSlimes();
    }

    /*
    Inputs: None

    Outputs: None.

    Called by: constructor

    Calls: super.initialize()
    */
    @Override
    public void initialize(){
        super.initialize();
        slimeList = new ArrayList<>();
        spawns = new ArrayList<>();
        SlimeBossEnemy first = new SlimeBossEnemy(16,position.clone(), this);
    }

    /*
    Inputs: None.

    Outputs: Returns nothing, no bitmap to display.

    Called by: Nowhere

    Calls: None.
    */
    @Override
    public Bitmap getDrawable() {
        return null;
    }

    /*
    Inputs: The Slime enemy that was killed.

    Outputs: None. Spawns more slimes in if slime wasnt the smallest type.

    Called by: SlimeBossEnemy.onDestroy()

    Calls: Level.clearBossWall()
    */
    public void slimeKilled(SlimeBossEnemy enemy){
        slimeList.remove(enemy);
        if(enemy.size > 1){
            spawns.add(new Spawn(enemy.position, enemy.size / 2));
        }
        if(slimeList.size() == 0 && spawns.size() == 0){
            GameView.level.clearBossWall();
        }
    }

    /*
    Inputs:None.

    Outputs: None. Spawns slimes based on Spawn objects stored in spawns list.

    Called by: update()

    Calls: None.
    */
    private void spawnSlimes(){
        for(Spawn spawn: spawns){
            GameObject go = new SlimeBossEnemy(spawn.size, spawn.position.clone(), this);
            go.position.add(new Vector2(-10 + (int)(Math.random() * 20), -10 + (int)(Math.random() * 20)));
            go = new SlimeBossEnemy(spawn.size, spawn.position.clone(), this);
            go.position.add(new Vector2(-10 + (int)(Math.random() * 20), -10 + (int)(Math.random() * 20)));
        }
        spawns.clear();
    }

    //Simple two-tuple used to avoid concurrent access issues with the slime spawning and GameObject list.
    private class Spawn{
        Vector2 position;
        int size;

        public Spawn(Vector2 _pos, int _size){
            position = _pos;
            size = _size;
        }
    }
}
