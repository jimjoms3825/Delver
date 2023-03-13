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

    Class: ControlsView (extends Viewable)
    Description: This class provides a viewable object made to show the player the games controls.

 */

public class ControlsView extends Viewable{
    private final Paint paint;
    private final SurfaceHolder ourHolder;

    //Control variables.
    private volatile boolean running;
    private Thread gameThread;
    private int FRAMES_PER_SECOND = 60;
    private long nextFrame;
    private long lastFrame;

    private JoyStick leftJoy; // Sample joystick.
    private JoyStick rightJoy; // Sample Joystick
    private GameButton spellButton; // Shows the position and appearance of the spell button.

    /*
    Inputs: Context.

    Outputs: None. Constructs a controls view which shows the player the game controls.

    Called by: GameActivity.onCreate.

    Calls: Constructors for UI elelements and paint methods.
    */
    public ControlsView(Context context) {
        super(context);
        paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setAntiAlias(false);
        ourHolder = getHolder();
        int joySize = (int)(getScreenSize().y / 8);
        leftJoy = new JoyStick(joySize, new Vector2( 300 , getScreenSize().y - joySize * 3f));
        rightJoy = new JoyStick(joySize, new Vector2(getScreenSize().x - 300, getScreenSize().y - joySize * 3f));
        spellButton = new SpellButton(new Vector2(rightJoy.center.x - rightJoy.size, rightJoy.center.y - rightJoy.size * 4),
                new Vector2(rightJoy.size * 2, rightJoy.size * 2));
        setOnTouchListener(this);
    }

    /*
    Inputs: None

    Outputs: None. Pauses activity.

    Called by: GameActivity

    Calls: none.
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

    Outputs: None. Resumes activity.

    Called by: GameActivity.

    Calls: thread.start.
    */
    @Override
    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
        nextFrame = lastFrame + (1000 / FRAMES_PER_SECOND);
    }

    /*
    Inputs: View, MotionEvent

    Outputs: Boolean. Part of android API.

    Called by: Android system.

    Calls: MainActivity.setView.
    */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        MainActivity.instance.setView(MainActivity.VIEWS.MainMenu);
        return false;
    }

    /*
    Inputs: None.

    Outputs: None. draws info to the screen and slows the thread.

    Called by: Java Thread control class.

    Calls: Draw, Control.
    */
    @Override
    public void run() {
        while(running){
            draw();
            control();
        }
    }

    /*
    Inputs: None.

    Outputs: None. Draws specified information to the canvas.

    Called by: run()

    Calls: DrawUI.
    */
    private void draw(){
        if(!ourHolder.getSurface().isValid()) return;
        Canvas canvas = ourHolder.lockCanvas();
        if(canvas == null) return;
        canvas.drawRGB(0, 0,0);
        drawUI(canvas);
        ourHolder.unlockCanvasAndPost(canvas);
    }

    /*
    Inputs: None.

    Outputs: None. Sleeps thread.

    Called by: run()

    Calls: none.
    */
    private void control(){
        while(System.currentTimeMillis() < nextFrame){
            try {
                gameThread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        lastFrame = System.currentTimeMillis();
        nextFrame = System.currentTimeMillis() + (1000 / FRAMES_PER_SECOND);
    }

    /*
    Inputs: None

    Outputs: Vector2 representing screen size.

    Called by: DrawUI;

    Calls: Resource information calls.
    */
    private Vector2 getScreenSize() {
        return new Vector2(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
    }

    /*
    Inputs: Canvas to be drawn on.

    Outputs: None. Draws the specified information to the canvas.

    Called by: draw()

    Calls: Paint / canvas calls. Nothing significant.
    */
    private void drawUI(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.rgb(255, 255, 255));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(150);
        int screenCenter = (int)getScreenSize().x / 2;
        float textSpacing = getScreenSize().y / 10;
        canvas.drawText("Controls", screenCenter, textSpacing * 2, paint);

        paint.setColor(Color.WHITE);
        paint.setColor(Color.rgb(0, 200, 0));
        canvas.drawCircle(leftJoy.center.x, leftJoy.center.y, leftJoy.size * 1.5f, paint);
        paint.setColor(Color.rgb(100, 100, 255));
        canvas.drawCircle(rightJoy.center.x, rightJoy.center.y, rightJoy.size * 1.5f, paint);
        paint.setColor(Color.RED);
        canvas.drawRect(spellButton.bounds.min.x - 50, spellButton.bounds.min.y - 50,
                spellButton.bounds.max.x + 50, spellButton.bounds.max.y + 50, paint);

        leftJoy.draw(canvas);
        rightJoy.draw(canvas);
        spellButton.draw((canvas));

        paint.setTextSize(getScreenSize().x / 50);
        canvas.drawText("The game is controlled using two simulated joysticks.", screenCenter, textSpacing * 4, paint);
        canvas.drawText("Tapping the spell icon cycles through your available spells.", screenCenter, textSpacing * 7, paint);
        canvas.drawText("The right stick aims your staff and shoots your spell.", screenCenter, textSpacing * 6, paint);
        canvas.drawText("The left joystick allows your player to move. ", screenCenter, textSpacing * 5, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(getScreenSize().x / 40);
        canvas.drawText("Press anywhere to return to the main menu.", screenCenter, textSpacing * 8, paint);
    }
}
