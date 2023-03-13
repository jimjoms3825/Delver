package com.example.comp486tme1;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: AABB
    Description: An Axis-Aligned Bounding Box (AABB) class which offers high performance
    collision detection.

 */
public class AABB {

    public Vector2 min; // Start pos of AABB.
    public Vector2 max; // End pos of AABB.

    //Simple constructor.
    public AABB(){
        min = new Vector2();
        max = new Vector2();
    }

    /*
    Inputs: Another Axis Aligned Bounding Box object.

    Outputs: A boolean representing whether the other AABB intersects with this AABB.

    Called by: GameObject, Level, Pickup

    Calls: None
    */
    public boolean isInside(AABB other){
        //Not overlapping in x direction
        if(other.min.x > max.x || other.max.x < min.x){
            return false;
        }
        //Not overlapping in y direction
        if(other.min.y > max.y || other.max.y < min.y){
            return false;
        }
        return true;
    }

    /*
    Inputs: A Vector2 representing a point in simulated space.

    Outputs: Returns a boolean representing whether the point is located within the bounds of this AABB.

    Called by: GameButton

    Calls: None
    */
    public boolean isInside(Vector2 other){
        //Not overlapping in x direction
        if(other.x > max.x || other.x < min.x){
            return false;
        }
        //Not overlapping in y direction
        if(other.y > max.y || other.y < min.y){
            return false;
        }
        return true;
    }

    /*
    Inputs: None

    Outputs: Returns a copy of this AABB.

    Called by: GameObject

    Calls: Vector2.clone
     */
    public AABB clone(){
        AABB newBox  = new AABB();
        newBox.min = min.clone();
        newBox.max = max.clone();
        return newBox;
    }


}
