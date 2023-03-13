package com.example.comp486tme1;

import android.widget.TextView;

import java.util.ArrayList;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: LevelGenerator
    Description: A class which generates 2d tile-based levels.

 */
public class LevelGenerator implements Runnable{
    public Level level;
    private int numberOfRooms;
    public float progress = 0f;

    public enum EnemySpawns {Slime, Imp, Ghost, Demon, Mimic, Ogre, Orc}

    private Thread generationThread;

    /*
    Inputs: Int for how many rooms should be generated.

    Outputs: Creates a room of given size.

    Called by: LoadingView constructor

    Calls: Creates and starts generation thread.
    */
    public LevelGenerator(int _numberOfRooms){
        level = new Level(GameView.floorsCleared);
        GameView.level = level;
        numberOfRooms = _numberOfRooms;
        generationThread = new Thread(this);
        generationThread.start();
    }

    /*
    Inputs: None

    Outputs:None.

    Called by: Android system.

    Calls: generateTutorial(), generateBossRoom1(), generateBossRoom2(), generateBossRoom3(),
    generateRooms() makePaths() populateTiles() fillRooms() generateExit()
    */
    @Override
    public void run() {
        //Generate tutorial
        if(GameView.floorsCleared == -1){
            generateTutorial();
            makePaths();
            populateTiles();
            progress = 1f;
        }
        //Generate Knight boss
        else if(GameView.floorsCleared == 3){
            generateBossRoom1();
            makePaths();
            populateTiles();
            progress = 1f;
        }
        //Generate Slime Boss
        else if(GameView.floorsCleared == 7){
            generateBossRoom2();
            makePaths();
            populateTiles();
            progress = 1f;
        }
        //Generate wizard boss
        else if(GameView.floorsCleared >= 11){ // 11
            generateBossRoom3();
            makePaths();
            populateTiles();
            for(Tile tile: level.tiles){
                if(tile.walkable && GameView.floorsCleared > 7 && Math.random() > 0.98){
                    tile.createSpikeTrap();
                }
            }
            progress = 1f;
        }
        //Generate room normally.
        else{
            generateRooms();
            makePaths();
            populateTiles();
            fillRooms();
            generateExit();
            progress = 1f;
        }
    }

    /*
    Inputs: None.

    Outputs: None. Generates a number of rooms as specified in numberOfRooms.

    Called by: run()

    Calls: Lots of Vector calls. Thread.yield()
    */
    private void generateRooms(){
        //Generate starting room
        int startingRoomSize = 5 + (int)(Math.random() * 5);
        Room startingRoom = Room.createRoom(startingRoomSize);
        startingRoom.position = new Vector2((int)(500 - startingRoomSize / 2), (int)(500 - startingRoomSize / 2));
        level.rooms.add(startingRoom);
        startingRoom.roomType = Room.RoomType.StartingRoom;

        //Where to generate the end room/
        int endRoomIndex = (int)(Math.random() * (numberOfRooms - 1));
        //Generate Rooms
        for(int i = 0; i < numberOfRooms; i++){
            Room room = Room.createRoom(3 + (int)(Math.random() * 4));
            int randomIndex = (int)(Math.random() * level.rooms.size()) - 1;
            boolean isColliding = false;
            if(randomIndex < 0) { randomIndex = 0; }
            int distance = 5;
            do{
                Vector2 randomPos = level.rooms.get(randomIndex).position;
                int newXPos = -distance + (int)(Math.random() * (2 * distance));
                int newYPos = -distance + (int)(Math.random() * (2 * distance));
                newXPos += (int)randomPos.x;
                newYPos += (int)randomPos.y;
                room.position = new Vector2(newXPos, newYPos);
                for(Room otherRoom: level.rooms){
                    if(room.getBounds().isInside(otherRoom.getBounds())){
                        isColliding = true;
                        distance++;
                        break;
                    }
                    else{
                        isColliding = false;
                    }
                }

            } while(isColliding);

            level.rooms.add(room);
            if(i == endRoomIndex){
                room.roomType = Room.RoomType.ExitRoom;
            }
            progress = 0.05f * (i / numberOfRooms);
        }

        //open and closed graphs walk
        ArrayList<Room> closedList = new ArrayList<Room>();
        closedList.add(startingRoom);
        for(int i = 0; i < level.rooms.size(); i++){
            if(level.rooms.get(i).equals(startingRoom)) continue;
            //Connect room to closest other room
            int closest = 0;
            int closestDistance = (int)Vector2.getDistance(level.rooms.get(i).position, closedList.get(0).position);
            for(int j = 0; j < closedList.size(); j++){
                int newDistance = (int)Vector2.getDistance(level.rooms.get(j).position, closedList.get(j).position);
                if(newDistance < closestDistance){
                    closestDistance = newDistance;
                    closest = j;
                }
            }
            level.rooms.get(i).connections.add(closedList.get(closest));
            closedList.add(level.rooms.get(i));
            progress = 0.1f * (i / level.rooms.size());
            generationThread.yield();
        }
    }

    /*
    Inputs: None.

    Outputs: None. Creates paths between rooms.

    Called by: run()

    Calls: Thread.yield()
    */
    private void makePaths(){
        //Makes path to each connected room.
        for(Room r: level.rooms){
            for(Room r2: r.connections){
                if(Math.random() > 0.5f){ // Make X path first
                    int targetX = (int)(r2.position.x + (r2.width / 2));
                    int targetY = (int)(r2.position.y + (r2.height / 2));
                    int currentX = (int)(r.position.x + (r.width / 2));
                    int currentY = (int)(r.position.y + (r.height / 2));
                    while(currentX != targetX && currentX < 998 && currentX >= 1
                            && currentY < 998 && currentY >= 1){
                        if(currentX < targetX){
                            currentX++;
                            level.walkable[currentX + 1][currentY - 1] = 1;
                            level.walkable[currentX + 1][currentY] = 1;
                            level.walkable[currentX + 1][currentY + 1] = 1;
                        }
                        else {
                            currentX--;
                            level.walkable[currentX - 1][currentY - 1] = 1;
                            level.walkable[currentX - 1][currentY] = 1;
                            level.walkable[currentX - 1][currentY + 1] = 1;
                        }
                    }

                    while(currentY != targetY && currentY < 998 && currentY >= 1
                            && currentX < 998 && currentX >= 1){
                        if(currentY < targetY){
                            currentY++;
                            level.walkable[currentX - 1][currentY + 1] = 1;
                            level.walkable[currentX][currentY + 1] = 1;
                            level.walkable[currentX + 1][currentY + 1] = 1;
                        }
                        else {
                            currentY--;
                            level.walkable[currentX - 1][currentY - 1] = 1;
                            level.walkable[currentX][currentY - 1] = 1;
                            level.walkable[currentX + 1][currentY - 1] = 1;
                        }
                    }
                }
                else{ // Make Y path first
                    int targetX = (int)(r2.position.x + (r2.width / 2));
                    int targetY = (int)(r2.position.y + (r2.height / 2));
                    int currentX = (int)(r.position.x + (r.width / 2));
                    int currentY = (int)(r.position.y + (r.height / 2));

                    while(currentY != targetY && currentY < 998 && currentY >= 1
                            && currentX < 998 && currentX >= 1){
                        if(currentY < targetY){
                            currentY++;
                            level.walkable[currentX - 1][currentY + 1] = 1;
                            level.walkable[currentX][currentY + 1] = 1;
                            level.walkable[currentX + 1][currentY + 1] = 1;
                        }
                        else {
                            currentY--;
                            level.walkable[currentX - 1][currentY - 1] = 1;
                            level.walkable[currentX][currentY - 1] = 1;
                            level.walkable[currentX + 1][currentY - 1] = 1;
                        }
                    }
                    while(currentX != targetX && currentX < 998 && currentX >= 1
                            && currentY < 998 && currentY >= 1){
                        if(currentX < targetX){
                            currentX++;
                            level.walkable[currentX + 1][currentY - 1] = 1;
                            level.walkable[currentX + 1][currentY] = 1;
                            level.walkable[currentX + 1][currentY + 1] = 1;
                        }
                        else {
                            currentX--;
                            level.walkable[currentX - 1][currentY - 1] = 1;
                            level.walkable[currentX - 1][currentY] = 1;
                            level.walkable[currentX - 1][currentY + 1] = 1;
                        }
                    }

                }
            }
        }
        progress = 0.2f;
        generationThread.yield();
    }

    /*
    Inputs: None.

    Outputs: None. Populates tiles, walls, and then blanks (for collision detection) as defined by the room geometry.

    Called by: run()

    Calls: ProcessTile(), Level.setExitTile(), Thread.yield, processWall()
    */
    private void populateTiles(){
        //Fill level walkable array with room information.
        for(Room r: level.rooms){
            for(int i = 0; i < r.width; i++){
                for(int j = 0; j < r.height; j++){
                    if(r.shape[i][j] != 0){
                        if(i + (int)r.position.x < level.levelWidth && i + (int)r.position.x >= 0
                                && j + (int)r.position.y < level.levelHeight && j + (int)r.position.y >= 0)
                            level.walkable[i + (int)r.position.x][j + (int)r.position.y] = r.shape[i][j];
                    }
                }
            }
        }
        //Generate floor tiles.
        for(int i = 0; i < 1000; i++){
            for(int j = 0; j < 1000; j++) {
                if(level.walkable[i][j] == 1){
                    Tile tile = new Tile();
                    tile.walkable = true;
                    tile.position = new Vector2(i * level.scaledTileWidth, j * level.scaledTileWidth);
                    tile.sprite = new Sprite(level.floorImages.get((int)(Math.random() * 5)), level.tileScale);
                    //Process floor tiles (creates walls).
                    processTile(i, j);
                    level.tiles.add(tile);
                }
                else if(level.walkable[i][j] == 3){ // Manual exit placement
                    Tile tile = new Tile();
                    tile.walkable = true;
                    tile.position = new Vector2(i * level.scaledTileWidth, j * level.scaledTileWidth);
                    level.setExitTile(tile);
                    //Process floor tiles (creates walls).
                    processTile(i, j);
                    level.tiles.add(tile);
                }
                else if(level.walkable[i][j] == 4){ // Special Boss Wall
                    Tile tile = new Tile();
                    tile.walkable = true;
                    tile.position = new Vector2(i * level.scaledTileWidth, j * level.scaledTileWidth);
                    level.setBossWall(tile);
                    //Process floor tiles (creates walls).
                    processTile(i, j);
                    level.tiles.add(tile);
                }
            }
            progress = 0.2f + 0.5f * (i / 1000);
            generationThread.yield();
        }
        //Process wall tiles after other generation (adds empty border for col detection).
        for(int i = 0; i < 1000; i++){
            for(int j = 0; j < 1000; j++) {
                if(level.walkable[i][j] == 2){ // Generate blanks on walls.
                    processWall(i, j);
                }
            }
            progress = 0.7f + 0.2f * (i / 1000);
            generationThread.yield();
        }
    }

    /*
    Inputs: None.

    Outputs: None. Parses level information to generate walls where necessary.

    Called by: PopulateTiles

    Calls: None.
    */
    private void processTile(int x, int y){
        if(x > 998 || x < 1 || y > 998 || y < 1) { return; } //out of bounds.

        if(level.walkable[x][y] != 0){ // generate walls around tile if necessary.
            if(level.walkable[x + 1][y] == 0)
                generateWall(x + 1, y);
            if(level.walkable[x][y + 1] == 0)
                generateWall(x, y + 1);
            if(level.walkable[x - 1][y] == 0)
                generateWall(x - 1, y);
            if(level.walkable[x][y - 1] == 0)
                generateWall(x, y - 1);
        }
    }

    /*
    Inputs: x and y coords of the wall to be processed.

    Outputs: None. Processes a wall at given position to generate blanks.

    Called by: populateTiles()

    Calls: generateBlank()
    */
    private void processWall(int x, int y){
        if(x > 998 || x < 1 || y > 998 || y < 1) { return; } //out of bounds.

        if(level.walkable[x][y] == 2){ // generate blanks around wall if necessary.
            if(level.walkable[x + 1][y] == 0)
                generateBlank(x + 1, y);
            if(level.walkable[x][y + 1] == 0)
                generateBlank(x, y + 1);
            if(level.walkable[x - 1][y] == 0)
                generateBlank(x - 1, y);
            if(level.walkable[x][y - 1] == 0)
                generateBlank(x, y - 1);
        }
    }

    /*
    Inputs: int coords of the wall to be generated.

    Outputs: None. Generates a wall and applies the correct sprite based on level data.

    Called by: ProcessTile

    Calls: None.
    */
    private void generateWall(int x, int y){
        Tile tile = new Tile();
        tile.position = new Vector2(x * level.scaledTileWidth, y * level.scaledTileWidth);
        tile.walkable = false;
        tile.canCollide = true;
        level.walkable[x][y] = 2; // generated walls flag

        boolean up = level.walkable[x][y - 1] != 0 && level.walkable[x][y - 1] != 2;
        boolean down = level.walkable[x][y + 1] != 0 && level.walkable[x][y + 1] != 2;
        boolean left = level.walkable[x - 1][y] != 0 && level.walkable[x - 1][y] != 2;
        boolean right = level.walkable[x + 1][y] != 0 && level.walkable[x + 1][y] != 2;

        level.tiles.add(tile);

        if(down) {
            tile.sprite = new Sprite(level.wallImages.get(0), level.tileScale);
        }
        else if(up){
            if(left){
                if(right){
                    tile.sprite = new Sprite(level.wallImages.get(6), level.tileScale);
                }
                else{
                    tile.sprite = new Sprite(level.wallImages.get(4), level.tileScale);
                }
            }
            else if(right){
                tile.sprite = new Sprite(level.wallImages.get(4), Sprite.Orientations.Horizontal, level.tileScale);
            }
            else{
                tile.sprite = new Sprite(level.wallImages.get(1), level.tileScale);
            }
        }
        else if(right){
            if(left){
                tile.sprite = new Sprite(level.wallImages.get(3), Sprite.Orientations.Horizontal, level.tileScale);
            }
            else {
                tile.sprite = new Sprite(level.wallImages.get(2), Sprite.Orientations.Horizontal, level.tileScale);
            }
        }
        else if(left){
            tile.sprite = new Sprite(level.wallImages.get(2), level.tileScale);
        }
    }

    //Generates a blank tile at the given position.
    /*
    Inputs:

    Outputs: None. Generates a blank tile at the given position.

    Called by: ProcessWall()

    Calls: None.
    */
    private void generateBlank(int x, int y) {
        Tile tile = Tile.getBlankTile(level.scaledTileWidth);
        tile.position = new Vector2(x * level.scaledTileWidth, y * level.scaledTileWidth);
        level.walkable[x][y] = 3; // generated walls flag
    }

    //Generates the exit in the level.
    /*
    Inputs: none.

    Outputs: None. Generates an exit somewhere in the level.

    Called by: run()

    Calls: setExitTile()
    */
    private void generateExit() {

        ArrayList<Tile> potentialTiles = new ArrayList<>();
        //Get all tiles that lie within the parameters of the exit.
        for (Tile tile : level.tiles) {
            if ( tile.walkable && Vector2.getDistance(tile.position, new Vector2(500 * level.scaledTileWidth, 500 * level.scaledTileWidth)) > 1000) {
                potentialTiles.add(tile);
            }
        }
        //If no tiles are found, grab a random tile (failsafe).
        while (potentialTiles.size() == 0) {
            Tile t = level.tiles.get((int) (Math.random() * (level.tiles.size() - 1)));
            //Ensure the randomly chosen tile can be walked on.
            if (t.walkable) {
                potentialTiles.add(t);
            }
        }
        Tile endTile = potentialTiles.get((int) (Math.random() * (potentialTiles.size() - 1)));
        level.setExitTile(endTile); // Mark the exit tile.
    }

    /*
    Inputs: None.

    Outputs: None. Populates chests and enemies in generated geometry.

    Called by: run()

    Calls: Tile.createSpikeTrap()
    */
    private void fillRooms(){

        ArrayList<EnemySpawns> availableSpawns = new ArrayList<>();
        //Add default enemies.
        availableSpawns.add(EnemySpawns.Slime);
        availableSpawns.add(EnemySpawns.Imp);
        //Once first boss is cleared.
        if(GameView.floorsCleared > 3){
            availableSpawns.add(EnemySpawns.Ogre);
            availableSpawns.add(EnemySpawns.Demon);
        }
        //Once second boss is cleared.
        if(GameView.floorsCleared > 7){
            availableSpawns.add(EnemySpawns.Ghost);
            availableSpawns.add(EnemySpawns.Mimic);
            availableSpawns.add(EnemySpawns.Orc);
        }


        //Enemy amount adjusted by difficulty modifier.
        int enemiesToSpawn = level.rooms.size() * (1 + (int)(GameView.difficultyModifier / 4));
        for(int i = 0; i < enemiesToSpawn; i++){
            Enemy enemy = null;
            EnemySpawns toSpawn = availableSpawns.get((int)(Math.random() * availableSpawns.size()));
            switch (toSpawn){
                case Slime:
                    enemy = new Slime();
                    break;
                case Imp:
                    enemy = new Imp();
                    break;
                case Ogre:
                    enemy = new Ogre();
                    break;
                case Demon:
                    enemy = new Demon();
                    break;
                case Ghost:
                    enemy = new Ghost();
                    break;
                case Mimic:
                    enemy = new Mimic();
                    break;
                case Orc:
                    enemy = new Orc();
                    break;
            }

            int attempts = 0;
            Tile spawnTile;

            //Spawn it in a random position up to 4 times to avoid spawning to close to player.
           do {
                do{
                    spawnTile = level.tiles.get((int)(Math.random() * level.tiles.size()));
                } while(!spawnTile.walkable); // Get a walkable tile

               enemy.position = spawnTile.position.clone();
               enemy.position.subtract(enemy.getSpriteCenter());
               enemy.position.add(spawnTile.getSpriteCenter());
            }  while(Vector2.getDistance(new Vector2(500 * level.scaledTileWidth,
                   500 * level.scaledTileWidth), enemy.position) < 700 && attempts++ <= 4);

            //Despawn the enemy if no location found for spawn.
            if(attempts >= 4){
                enemy.toRemove = true;
            }
            progress = 0.9f + 0.1f * (i / enemiesToSpawn);
            generationThread.yield();
        }

        int numberOfChests = level.rooms.size() / 2 + (int)(Math.random() * level.rooms.size() / 2);
        for(int i = 0; i < numberOfChests; i++){
            Tile spawnTile;
            do{
                spawnTile = level.tiles.get((int)(level.tiles.size() * Math.random()));
            } while(!spawnTile.isExit && !spawnTile.walkable);
            GameObject chest = new Chest();
            chest.position = spawnTile.position.clone();
            chest.position.subtract(chest.getSpriteCenter());
            chest.position.add(spawnTile.getSpriteCenter());
        }

        for(Tile tile: level.tiles){
            if(tile.walkable && !tile.isExit && GameView.floorsCleared > 7 && Math.random() > 0.98){
                tile.createSpikeTrap();
            }
        }
    }

    /*
    Inputs: None.

    Outputs: None. Generates a tutorial level for the player.

    Called by: run()

    Calls: None.
    */
    private void generateTutorial(){
        Room room = new Room();
        room.position = new Vector2(496, 500);
        room.width = 9;
        room.height = 20;
        room.shape = new int[][]{ // Rotated 90degrees.
                {1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0},
                {1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1},
                {1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 ,1 ,1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 ,1, 1, 1, 1, 1, 1, 1, 3, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 ,1 ,1, 1},
                {1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1},
                {1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1},
                {1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0},
                };

        GameObject go = new CoinPickup();
        go.isStatic = true;
        go.position = new Vector2(497.5f, 501.5f);
        go.position.scale(GameView.level.scaledTileWidth);
        go = new TextBox("You will find coins around that can be used to purchase upgrades to " +
                "your health and weapons! Spend them wisely, as you will need to be powerful for the " +
                "challenges ahead!", 3 * level.scaledTileWidth);
        go.position = new Vector2(496f, 503f);
        go.position.scale(GameView.level.scaledTileWidth);

        go = new Chest();
        go.position = new Vector2(497.5f, 507.5f);
        go.position.scale(GameView.level.scaledTileWidth);
        go = new TextBox("Chests contain valuable loot like coins, health potions, and spell " +
                "powerups! Just walk near them and they will open!", 3 * level.scaledTileWidth);
        go.position = new Vector2(496f, 509);
        go.position.scale(GameView.level.scaledTileWidth);

        go = new SpellPickup();
        go.isStatic = true;
        go.position = new Vector2(497.5f, 513.5f);
        go.position.scale(GameView.level.scaledTileWidth);
        go = new TextBox("Picking these up will give you a spell cast time reduction! You can view " +
                "your current reduction in the top left of your screen.", 3 * level.scaledTileWidth);
        go.position = new Vector2(496f, 515);
        go.position.scale(GameView.level.scaledTileWidth);

        go = new Slime();
        go.position = new Vector2(503, 501);
        go.position.scale(GameView.level.scaledTileWidth);
        go = new TextBox("All through this dungeon you will find enemies just like this slime! " +
                "It seems that the deeper you go, the stronger and more plentiful they get. Be careful!",
                3 * level.scaledTileWidth);
        go.position = new Vector2(502, 503f);
        go.position.scale(GameView.level.scaledTileWidth);

        go = new KnightBoss();
        go.position = new Vector2(503, 507);
        go.position.scale(GameView.level.scaledTileWidth);
        go = new TextBox("Every three floors there is an extra tough enemy waiting for you, just like" +
                " this one here that I managed to trap! Be extra careful around them.", 3 * level.scaledTileWidth);
        go.position = new Vector2(502, 509);
        go.position.scale(GameView.level.scaledTileWidth);

        go = new TextBox("The dungeons randomly generate, and seem to have more rooms the deeper you go. " +
                "Make sure to explore lots of areas!", 3 * level.scaledTileWidth);
        go.position = new Vector2(502, 513);
        go.position.scale(GameView.level.scaledTileWidth);


        go = new TextBox("Moving your left joystick will aim your staff and cast spells! It also" +
                "allows you to look slightly further in the direction you are pointing.", 3 * level.scaledTileWidth);
        go.position = new Vector2(496, 498);
        go.position.scale(GameView.level.scaledTileWidth);

        go = new TextBox("Moving your right joystick will move you through the dungeon!", 3 * level.scaledTileWidth);
        go.position = new Vector2(502, 498);
        go.position.scale(GameView.level.scaledTileWidth);

        go = new TextBox("These ladders take you deeper into the dungeon. Walk on this one to reach " +
                "the first floor.", 3 * level.scaledTileWidth);
        go.position = new Vector2(501, 517);
        go.position.scale(GameView.level.scaledTileWidth);
        level.rooms.add(room);
    }

    /*
    Inputs: None.

    Outputs: None. Generates a boss room.

    Called by: run()

    Calls: None
    */
    private void generateBossRoom1(){
        Room room = new Room();
        room.position = new Vector2(495, 500);
        room.width = 10;
        room.height = 15;
        room.shape = new int[][]{ // Rotated 90degrees.
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 3, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0}};
        level.rooms.add(room);
        KnightBoss boss = new KnightBoss();
        boss.position = new Vector2(500 * level.scaledTileWidth, 503 * level.scaledTileWidth);
    }

    /*
    Inputs: None.

    Outputs: None. Generates a boss room.

    Called by: run()

    Calls: None
    */
    private void generateBossRoom2(){
        Room room = new Room();
        room.position = new Vector2(496, 491);
        room.width = 13;
        room.height = 10;
        room.shape = new int[][]{ // Rotated 90degrees.
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 4, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 3, 1, 1, 1, 1, 1}};
        level.rooms.add(room);
        SlimeBoss boss = new SlimeBoss(new Vector2(500 * level.scaledTileWidth, 497 * level.scaledTileWidth));
    }

    /*
    Inputs: None.

    Outputs: None. Generates a boss room.

    Called by: run()

    Calls: None
    */
    private void generateBossRoom3(){
        Room room = new Room();
        room.position = new Vector2(495, 500);
        room.width = 9;
        room.height = 9;
        room.shape = new int[][]{ // Rotated 90degrees.
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},};
        level.rooms.add(room);
        WizardBoss boss = new WizardBoss();
        boss.position = new Vector2(500 * level.scaledTileWidth, 504 * level.scaledTileWidth);

    }
}
