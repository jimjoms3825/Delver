package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.w3c.dom.Text;

import java.util.ArrayList;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: October 7th, 2022

    Class: TextBox (extends GameObject)
    Description: A class which converts passed text into a textBox Bitmap.

 */

public class TextBox extends GameObject{

    private Bitmap textMap;

    /*
    Inputs: The string displayed in the box, the extents of the x axis (Y is determined automatically).

    Outputs: Creates a textbox.

    Called by: LevelGenerator.generateTutorial()

    Calls: None.
    */
    public TextBox(String text, int xSize){
        super();
        Paint paint = new Paint();
        paint.setTextSize(40);
        int ySize = 0;
        int padding = 10;

        char[] chars = text.toCharArray();
        ArrayList<String> words = new ArrayList<>();
        String word = "";

        //Parse the text into words.
        for(int i = 0; i < chars.length; i++){
            if(chars[i] != ' '){
                word += chars[i];
            }
            else{
                words.add(word);
                word = new String();
            }
        }
        if(word != ""){
            words.add(word);
        }

        ArrayList<String> lines = new ArrayList<>();

        //Parse the words into lines that fit in the x-bounds.
        if(words.size() > 0){
            int charsPerLine = (int) (xSize / paint.getTextSize() * 1.5);
            String currentLine = "";
            for(String s: words){
                if(currentLine.length() > charsPerLine){
                    lines.add(currentLine);
                    currentLine = "";
                }
                currentLine += s + " ";
            }
            if(currentLine != ""){
                lines.add(currentLine);
            }
            ySize = (int)((paint.getTextSize() + padding) * (lines.size() + 0.5));
        }
        else{
            toDraw = false;
            toRemove = true;
            return;
        }

        Bitmap image = Bitmap.createBitmap(xSize, ySize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, xSize, ySize, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(5, 5, xSize - 5, ySize - 5, paint);

        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);

        //Print all lines to the canvas.
        for(int i = 0; i < lines.size(); i++){
            canvas.drawText(lines.get(i), xSize / 2, (padding + (paint.getTextSize())) * (1 + i), paint);
        }
        drawOrder = 3;
        textMap = image;
    }

    /*
    Inputs: None.

    Outputs: Returns the textBox bitmap

    Called by: GameView loop

    Calls: None.
    */
    @Override
    public Bitmap getDrawable() {
        return textMap;
    }
}
