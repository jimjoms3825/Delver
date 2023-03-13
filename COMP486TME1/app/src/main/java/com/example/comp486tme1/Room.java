package com.example.comp486tme1;

import java.util.ArrayList;
import java.util.LinkedList;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Room
    Description: A Room contained within a level. Used mostly in level generation.

 */
public class Room {
    /*
    This first variable is a 2D array for the shape of the room. Though, in the end I did not use this,
    I think I will make use of it in the 3rd assignment, and so chose not to refactor it.
     */
    public int[][] shape;
    public Vector2 position; // The rooms position.
    public int width; //The width of the room.
    public int height; // The Height of the room.

    public enum RoomType {StartingRoom, ExitRoom, Normal} //Enum for types of rooms.
    public RoomType roomType = RoomType.Normal; // This rooms type.

    public LinkedList<Room> connections; // The rooms connected to this room.

    /*
    Inputs: None.

    Outputs: Creates a room.

    Called by: LevelGenerator, getRoom()

    Calls: None.
    */
    public Room(){
        connections = new LinkedList<Room>();
    }

    //Creates a room within the given parameters.
    /*
    Inputs: integer for the max size of either width and height

    Outputs: Returns a room created with passed size.

    Called by: LevelGenerator.generateRooms()

    Calls:None.
    */
    public static Room createRoom(int size){
        Room room = new Room();
        room.position = new Vector2();
        room.width = size - (int)(Math.random() * (size / 2));
        room.height = size - (int)(Math.random() * (size / 2));

        if(room.width <= 2) room.width = 3; // minimum of 3 for width and height.
        if(room.height <= 2) room.height = 3;
        room.shape = new int[room.width][room.height];

        for(int i = 0; i < room.width; i++){
            for(int j = 0; j < room.height; j++){
                room.shape[i][j] = 1;
            }
        }
        return room;
    }

    public AABB getBounds(){
        AABB bounds = new AABB();
        bounds.min = position.clone();
        bounds.min.x--;
        bounds.min.y--;
        bounds.max = bounds.min.clone();
        bounds.max.add(new Vector2(width + 1, height + 1));
        return bounds;
    }
}
