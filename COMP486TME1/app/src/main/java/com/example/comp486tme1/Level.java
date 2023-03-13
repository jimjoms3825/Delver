package com.example.comp486tme1;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import java.util.ArrayList;
import java.util.Random;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Level
    Description: An object with data necessary for easy access to each iteration of the game world.

 */
public class Level {
    public ArrayList<Room> rooms; // List of room objects in level.
    public static ArrayList<Bitmap> floorImages; // Array of floor tiles in current level.
    public static ArrayList<Bitmap> wallImages; // Array of wall tiles in current level.
    public ArrayList<Tile> tiles; // List of all tile objects in level.
    public static float tileScale = 1; // The scale of all tiles.
    public int tileWidth; // The width of unscaled tiles.
    public int scaledTileWidth; // The width (in pixels) of the tiles.
    public int levelWidth = 1000; // Maximum width (in tiles) of the level.
    public int levelHeight = 1000; // Maximum height (in tiles) of the level.
    public int[][] walkable; // Array of walkable tiles.

    private Tile bossTile; // A tile which gets cleared when the boss is killed.

    /*
    Inputs: Integer for the number of floors cleared

    Outputs: None. Creates a level and assigns the spritesheets.

    Called by: LevelGenerator constructor.

    Calls: Bitmap and sprite methods.
    */
    public Level(int levelIndex){
        rooms = new ArrayList<Room>();
        walkable = new int[levelWidth][levelHeight];

        floorImages = new ArrayList<Bitmap>();
        wallImages = new ArrayList<Bitmap>();
        tiles = new ArrayList<Tile>();
        int floorSheetID;
        int wallSheetID;
        if(levelIndex <= 3){
            floorSheetID = R.drawable.floor_blue;
            wallSheetID = R.drawable.wall_blue;
        }
        else if(levelIndex <= 7){
            floorSheetID = R.drawable.floor_green;
            wallSheetID = R.drawable.wall_green;
        }
        else{
            floorSheetID = R.drawable.floor_red;
            wallSheetID = R.drawable.wall_red;
        }


        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(), floorSheetID);


        tileWidth = spriteSheet.getHeight();
        int spriteMargins = (tileWidth / 16) * 2;
        scaledTileWidth = (int)(tileWidth * tileScale);
        for(int i = 0; i < 6; i++){
            System.out.println(tileWidth);
            Bitmap newBitmap = Bitmap.createBitmap(spriteSheet, i * (tileWidth + spriteMargins), 0, tileWidth, tileWidth);
            newBitmap = Bitmap.createScaledBitmap(newBitmap, tileWidth, tileWidth, false);
            floorImages.add(newBitmap);
        }
        spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(), wallSheetID);
        for(int i = 0; i < 7; i++){
            Bitmap newBitmap = Bitmap.createBitmap(spriteSheet, i * (tileWidth + spriteMargins), 0, tileWidth, tileWidth);
            newBitmap = Bitmap.createScaledBitmap(newBitmap, tileWidth, tileWidth, false);
            wallImages.add(newBitmap);
        }
    }

    /*
    Inputs: Vector2 looking for nearest position.

    Outputs: The Tile closest to the passed position.

    Called by: GameObject.handleOverlapping(), GameObject.ensureInBounds()

    Calls: None.
    */
    public Tile getClosestWalkable(Vector2 position){
        Tile closest;
        do{
            closest = tiles.get((int)(Math.random() * tiles.size()));
        } while(!closest.walkable);
        float bestDistance = Vector2.getDistance(closest.position, position);
        for(Tile tile: tiles){
            float tileDistance = Vector2.getDistance(tile.position, position);
            if(tile.walkable && tileDistance < bestDistance){
                closest = tile;
                bestDistance = tileDistance;
            }
        }
        return closest;
    }

    /*
    Inputs: Position of tile.

    Outputs: Returns the tile at the given position if one exists.

    Called by: GameObject.handleOverlapping(), GameObject.ensureInBounds()

    Calls: AABB calls.
    */
    public Tile getTileAtPosition(Vector2 position){
        AABB box = new AABB();
        box.min = position.clone();
        box.max = box.min;
        for(Tile tile: tiles){
            if(tile.getBounds().isInside(box)){
                return tile;
            }
        }
        return null;
    }

    /*
    Inputs: Tile to be the exit tile.

    Outputs: None. Changes passed tile to become the exit tile.

    Called by: Level Generator.

    Calls: None.
    */
    public void setExitTile(Tile tile){
        tile.sprite = new Sprite(floorImages.get(5), tileScale);
        tile.isTrigger = true;
        tile.isExit = true;
    }

    /*
    Inputs: Tile to become the boss wall.

    Outputs: None. Sets the passed tile to be the boss wall.

    Called by: Level Generator.

    Calls: None.
    */
    public void setBossWall(Tile tile) {
        tile.sprite = new Sprite(wallImages.get((0)), tileScale);
        tile.canCollide = true;
        bossTile = tile;
    }

    /*
    Inputs: None.

    Outputs: None. Turns the boss wall into a floor tile.

    Called by: KnightBoss.onDestroy(), SlimeBoss.slimeKilled()

    Calls: none.
    */
    public void clearBossWall(){
        bossTile.sprite = new Sprite(floorImages.get((0)), tileScale);
        bossTile.canCollide = false;
        SoundManager.instance.playSound(SoundManager.Sounds.Accept);
    }
}


