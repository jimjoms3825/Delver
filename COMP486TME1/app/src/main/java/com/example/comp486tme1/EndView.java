package com.example.comp486tme1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 3)
    Name: James Bombardier
    Date: October 7, 2022

    Class: EndView (extends Viewable) (Previously GameoverView)
    Description: This class provides a viewable object made to display post-game information.

 */
public class EndView extends Viewable {

    //Control methods
    private final Paint paint;
    private final SurfaceHolder ourHolder;
    private volatile boolean running;
    private Thread gameThread;
    private int FRAMES_PER_SECOND = 60;
    private long nextFrame;
    private long lastFrame;

    //Variables for display information.
    private float coins;
    private float enemiesKilled;
    private float floorsCleared;
    private float score;
    private float fullScore;
    private boolean gameWon; // Whether the game ended in victory or death.

    private int timeToCount = 1; //Time(seconds) to count element on end screen.

    private int colorIndex = 255; // For the fading text.
    private boolean colorIndexIncrement = false; // Whether to increment or decrement color Index.

    private boolean displayContinue = false; // Controls whether the player can restart.

    /*
    Inputs: Context (from activity class), Boolean representing the win state.

    Outputs: none. Creates the Viewable for display.

    Called by: GameActivity.

    Calls: CalculateScore
    */
    public EndView(Context context, boolean _gameWon) {
        super(context);
        paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setAntiAlias(false);
        ourHolder = getHolder();
        timeToCount = timeToCount * 60; // adjust for FPS.
        gameWon = _gameWon;

        fullScore = calculateScore();
        setOnTouchListener(this);
    }

    /*
    Inputs: None

    Outputs: None.

    Called by: Java thread internals.

    Calls: Update, Draw, Control
    */
    @Override
    public void run() {
        while(running){
            update();
            draw();
            control();
        }
    }

    /*
    Inputs: None

    Outputs: None, Updates the UI information variables.

    Called by: run

    Calls: None
    */
    private void update(){
        if(colorIndexIncrement){
            if(colorIndex++ >= 254){
                colorIndexIncrement = false;
            }
        }
        else {
            if(colorIndex-- <= 100){
                colorIndexIncrement = true;
            }
        }

        //updates the categories one at a time.
        if(coins != GameView.coinsCollected){
            coins += (float) GameView.coinsCollected / timeToCount;
            if(coins >  GameView.coinsCollected) { coins = GameView.coinsCollected; }
        }
        else if (enemiesKilled != GameView.enemiesKilled){
            enemiesKilled += (float) GameView.enemiesKilled / timeToCount;
            if(enemiesKilled > GameView.enemiesKilled) { enemiesKilled = GameView.enemiesKilled; }
        }
        else if (floorsCleared != GameView.floorsCleared){
            floorsCleared += (float) GameView.floorsCleared / timeToCount;
            if(floorsCleared > GameView.floorsCleared) { floorsCleared = GameView.floorsCleared; }
        }
        else if (score != fullScore){
            score += fullScore / timeToCount;
            if(score > fullScore) { score = fullScore; }
        }
        else{
            displayContinue = true;
        }
    }

    /*
    Inputs: None

    Outputs: None. Draws the UI to screen.

    Called by: run

    Calls: DrawUI
    */
    private void draw() {
        if(!ourHolder.getSurface().isValid()) return;
        Canvas canvas = ourHolder.lockCanvas();
        if(canvas == null) return;
        canvas.drawARGB(255, 0, 0,0);
        drawUI(canvas);
        ourHolder.unlockCanvasAndPost(canvas);
    }

    /*
    Inputs: None

    Outputs: None. Slows down thread.

    Called by: run

    Calls: Thread calls.
    */
    private void control(){
        while(System.currentTimeMillis() < nextFrame){
            try {
                gameThread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        //System.out.println(1000.0 / (System.currentTimeMillis() - lastFrame)); // Prints the FPS
        lastFrame = System.currentTimeMillis();
        nextFrame = System.currentTimeMillis() + (1000 / FRAMES_PER_SECOND);
    }

    /*
    Inputs: None

    Outputs: None. Pauses the game.

    Called by: GameActivity

    Calls: Thread calls.
    */
    @Override
    public void pause() {
        running = false;
        try{
            gameThread.join();
        } catch (InterruptedException e) {  }
    }

    /*
    Inputs: None

    Outputs: None. Resumes execution.

    Called by: GameActivity

    Calls: Thread calls.
    */
    @Override
    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
        nextFrame = lastFrame + (1000 / FRAMES_PER_SECOND);
    }

    //Sends to next level after all text displayed on touch.
    /*
    Inputs: View, MotionEvent (From overriden method)

    Outputs: boolean (from overriden method)

    Called by: Android system

    Calls: GameView.ClearData.
    */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(displayContinue){
            GameView.clearData();
            MainActivity.instance.setView(MainActivity.VIEWS.MainMenu);
        }
        return true;
    }

    /*
    Inputs: None.

    Outputs: Vector2 with the dimensions of the current screen size.

    Called by: DrawUI

    Calls: Resources calls for display size.
    */
    private Vector2 getScreenSize() {
        return new Vector2(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
    }

    /*
    Inputs: Canvas to be drawn on.

    Outputs: None. Draws the UI to the provided canvas.

    Called by: Draw

    Calls: Paint and canvas methods.
    */
    private void drawUI(Canvas canvas){
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(150);
        int screenCenter = (int)getScreenSize().x / 2;
        if(gameWon){
            paint.setColor(Color.rgb(colorIndex, colorIndex, colorIndex));
            canvas.drawText("You Have Won", screenCenter, 200, paint);
        }
        else{
            paint.setColor(Color.rgb(colorIndex, 0, 0));
            canvas.drawText("You Have Perished", screenCenter, 200, paint);
        }
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        canvas.drawText("Coins: ", screenCenter - 5, 300, paint);
        canvas.drawText("Enemies Killed:", screenCenter - 5, 400, paint);
        canvas.drawText("Floors Cleared:", screenCenter - 5, 500, paint);
        canvas.drawText("Score:", screenCenter - 5, 600, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(Integer.toString((int)coins) + " x 1 = " + Integer.toString((int)coins), screenCenter + 5, 300, paint);
        canvas.drawText(Integer.toString((int)enemiesKilled) + " x 10 = " + Integer.toString((int)(enemiesKilled * 10)), screenCenter + 5, 400, paint);
        canvas.drawText(Integer.toString((int)floorsCleared) + " x 100 = " + Integer.toString((int)(floorsCleared * 100)), screenCenter + 5, 500, paint);
        canvas.drawText(Integer.toString((int)score), screenCenter + 5, 600, paint);
        if(displayContinue){
            paint.setTextSize(75);
            paint.setColor(Color.argb(colorIndex, 255, 255, 255));
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Press Anywhere to Return to the Main Menu", screenCenter, 800, paint);
        }
    }

    /*
    Inputs: None.

    Outputs: Integer representing the players total score.

    Called by: Constructor.

    Calls: None.
    */
    private int calculateScore(){
        int tempScore = 0;
        tempScore += GameView.coinsCollected;
        tempScore += GameView.enemiesKilled * 10;
        tempScore += GameView.floorsCleared * 100;
        return tempScore;
    }
}
