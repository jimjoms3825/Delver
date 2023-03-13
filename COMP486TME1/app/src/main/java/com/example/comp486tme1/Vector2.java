package com.example.comp486tme1;
/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: Vector2
    Description: A convenience class for handling 2d coordinates.

 */
public class Vector2 {

    public float x; // The X point.
    public float y; // The Y Point.
    public static final Vector2 zero = new Vector2(0, 0); // Static access to a 0,0 vector

    //Simple Constructor.
    /*
    Inputs: None.

    Outputs: None.
    */
    public Vector2(){x = 0; y = 0;}

    //Pass the coords in with this constructor.
    /*
    Inputs: x and y coords.

    Outputs: None.
    */
    public Vector2(float _x, float _y){
        x = _x;
        y = _y;
    }

    /*
    Inputs: A vector to be normalized.

    Outputs: Returns a normalized copy of the passed vector.
    */
    public static Vector2 getNormalized(Vector2 v){
        float length = (float)Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2));
        Vector2 newV = new Vector2(v.x / length, v.y / length);
        return newV;
    }

    /*
    Inputs: None.

    Outputs: None. Normalizes the vector.
    */
    public void normalize(){
        float length = (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        x = x/length;
        y = y/length;
    }

    /*
    Inputs: Vector original, Vector other.

    Outputs: Gets the distance between the two vectors.
    */
    public static float getDistance(Vector2 v1, Vector2 v2){
        return (float)Math.abs(Math.sqrt((Math.pow((v2.x - v1.x), 2)) + (Math.pow((v2.y - v1.y), 2))));
    }

    /*
    Inputs: Vector to be added.

    Outputs: None. Adds the values of the passed vector to the current vector.
    */
    public void add(Vector2 v){
        x += v.x;
        y += v.y;
    }

    /*
    Inputs: To be Subtracted

    Outputs: None. Subtracts the values of the passed vector to the current vector.
    */
    public void subtract(Vector2 v){
        x -= v.x;
        y -= v.y;
    }

    /*
    Inputs: None.

    Outputs: Returns a copy of this vector.
    */
    public Vector2 clone(){
        return new Vector2(x, y);
    }

    /*
    Inputs: the factor for scaling.

    Outputs: None. Scales the vector by the passed value.
    */
    public void scale(float scaleFactor){
        x = x * scaleFactor;
        y = y * scaleFactor;
    }

    /*
    Inputs: The factor of division.

    Outputs: None. Divides the vector by the passed value.
    */
    public void divide(float divisionFactor){
        x = x / divisionFactor;
        y = y / divisionFactor;
    }

    /*
    Inputs:

    Outputs: Returns the angle in radians of the vector.
    */
    public float getAngle() {
        return (float)(Math.atan2(y, x) * 180 / 3.14);
    }

    /*
    Inputs: None

    Outputs: String formatted Vector2
    */
    @Override
    public String toString() {
        return  "x=" + x + ", y=" + y;
    }

    /*
    Inputs: The vector to be lerped to. The amount to lerp between the two.

    Outputs: None.

    */
    public void Lerp(Vector2 other, float amount){
        if(amount <= 0) { return; }
        if(amount >= 1) { x = other.x; y = other.y; }

        Vector2 difference = new Vector2( other.x - x, other.y - y);
        difference.scale(amount);
        add(difference);
    }

    /*
    Inputs: The radians of the desired angle vector.

    Outputs: A normalized vector representation of the passed radian value.
    */
    public static Vector2 degreesToVector(float radians){
        return getNormalized(new Vector2((float)Math.cos(radians), (float)Math.sin(radians)));
    }

}
