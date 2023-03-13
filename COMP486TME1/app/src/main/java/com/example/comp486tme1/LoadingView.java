package com.example.comp486tme1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    Class: LoadingView (extends Viewable)
    Description: A Viewable class which shows players information while level loading takes place.
    Also shows the approximate progress of loading at a given time.

 */

public class LoadingView extends Viewable{

    private final Paint paint;
    private final SurfaceHolder ourHolder;

    //Control variables.
    private volatile boolean running;
    private Thread loadingThread;
    private int FRAMES_PER_SECOND = 60;
    private long nextFrame;
    private long lastFrame;

    private LevelGenerator levelGenerator;

    private boolean doneLoading = false;

    private Bitmap screenMap;
    private Animator bossAnim;
    private String bossName;

    /*
    Inputs: Context (from activity)

    Outputs: Creates a LoadingView object.

    Called by: GameActivity.

    Calls: SoundManager.setMusicVolume(), LevelGenerator(), other constructors and vector calls.
    */
    public LoadingView(Context context) {
        super(context);
        paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setAntiAlias(false);
        ourHolder = getHolder();

        if(GameView.floorsCleared < 3){
            SoundManager.instance.setMusicVolume(0.5f);
            screenMap = BitmapFactory.decodeResource(getResources(), R.drawable.wave_1);
        } else if (GameView.floorsCleared == 3){
            screenMap = BitmapFactory.decodeResource(getResources(), R.drawable.wave_boss);
            bossName = "Evil Knight";
            bossAnim = new Animator();
            Bitmap temp = BitmapFactory.decodeResource(getResources(),
                    R.drawable.boss_knight);
            bossAnim.scale = 3f;
            bossAnim.addAnimation(new Animation("Idle", temp, 1, 4, 8, 6));
            SoundManager.instance.setMusicVolume(0.75f);
        } else if (GameView.floorsCleared < 7){
            SoundManager.instance.setMusicVolume(0.5f);
            screenMap = BitmapFactory.decodeResource(getResources(), R.drawable.wave_2);
        } else if (GameView.floorsCleared == 7){
            screenMap = BitmapFactory.decodeResource(getResources(), R.drawable.wave_boss);
            bossName = "King Slime";
            bossAnim = new Animator();
            Bitmap temp = BitmapFactory.decodeResource(getResources(),
                    R.drawable.slime_red);
            bossAnim.scale = 3f;
            bossAnim.addAnimation(new Animation("Idle", temp, 1, 2, 7, 15));
            SoundManager.instance.setMusicVolume(0.75f);
        } else if (GameView.floorsCleared < 11){
            SoundManager.instance.setMusicVolume(0.5f);
            screenMap = BitmapFactory.decodeResource(getResources(), R.drawable.wave_3);
        } else {
            screenMap = BitmapFactory.decodeResource(getResources(), R.drawable.wave_boss);
            bossName = "Dark Wizard";
            bossAnim = new Animator();
            Bitmap temp = BitmapFactory.decodeResource(getResources(),
                    R.drawable.boss_dark_wizard);
            bossAnim.scale = 3f;
            bossAnim.addAnimation(new Animation("Idle", temp, 1, 4, 4, 6));
            SoundManager.instance.setMusicVolume(0.75f);
        }

        float desiredScale = (screenMap.getHeight() / getScreenSize().y * 1.5f); // only use 75% of screen
        screenMap = Bitmap.createScaledBitmap(screenMap, (int)(screenMap.getWidth() / desiredScale), (int)(screenMap.getHeight() / desiredScale), false);
        levelGenerator = new LevelGenerator(3 + GameView.floorsCleared / 3 + (int)(Math.random() * GameView.floorsCleared / 2));

        setOnTouchListener(this);
    }

    /*
    Inputs: None.

    Outputs: None. Pauses thread.

    Called by: GameActivity.

    Calls: None.
    */
    @Override
    public void pause() {
        running = false;
        try{
            loadingThread.join();
        } catch (InterruptedException e) {  }
    }

    /*
    Inputs: None.

    Outputs: None. Resumes Thread.

    Called by: GameActivity.

    Calls: None.
    */
    @Override
    public void resume() {
        running = true;
        loadingThread = new Thread(this);
        loadingThread.start();
        nextFrame = lastFrame + (1000 / FRAMES_PER_SECOND);
    }

    /*
    Inputs: View, MotionEvent (from Activity)

    Outputs: boolean (from Activity). When loading is done, sends player to gameView.

    Called by:

    Calls:
    */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(doneLoading){
            MainActivity.instance.setView(MainActivity.VIEWS.GameView);
        }
        return false;
    }

    /*
    Inputs: None.

    Outputs: None. Controls execution.

    Called by: Java Thread library

    Calls: draw(), Control()
    */
    @Override
    public void run() {
        while(running){
            if(levelGenerator.progress >= 1) {
                doneLoading = true;
                GameView.level = levelGenerator.level;
            }
            draw();
            control();
        }
    }

    /*
    Inputs: None.

    Outputs: None.

    Called by: run()

    Calls:None.
    */
    private void control(){
        //Sleep if not ready for next update.
        while(System.currentTimeMillis() < nextFrame){
            try {
                loadingThread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        nextFrame = System.currentTimeMillis() + (1000 / FRAMES_PER_SECOND);
        lastFrame = System.currentTimeMillis();
    }

    /*
    Inputs: None.

    Outputs: Vector2 with dimensions of screen.

    Called by: constructor, draw()

    Calls: Resources calls.
    */
    public static Vector2 getScreenSize() {
        return new Vector2(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
    }

    /*
    Inputs: None.

    Outputs: None. Draws to the screen each frame.

    Called by: run()

    Calls: Drawing calls.
    */
    private void draw(){
        if(!ourHolder.getSurface().isValid()) return;
        Canvas canvas = ourHolder.lockCanvas();
        if(canvas == null) return;

        Paint paint = new Paint();
        Vector2 screenSize = getScreenSize();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLACK);
        paint.setTextSize(80);

        //Erase screen
        canvas.drawRect(0, 0, screenSize.x, screenSize.y, paint);

        //if not tutorial
        if(GameView.floorsCleared >= 0){
            //Draw header bitmap
            canvas.drawBitmap(screenMap, screenSize.x / 2 - screenMap.getWidth() / 2, 0, paint);
        }

        //Draw boss on boss levels
        if(bossAnim != null){
            Bitmap frame = bossAnim.getBitmap();
            canvas.drawBitmap(frame, screenSize.x / 2 - frame.getWidth() / 2,
                    screenSize.y / 2 - frame.getHeight() / 2, paint);
            canvas.drawText(bossName, screenSize.x / 2 - frame.getWidth() / 2,
                    screenSize.y / 2 - frame.getHeight() / 2, paint);
        }

        float progress = levelGenerator.progress;
        float progressBarRight = (screenSize.x - 300) * progress;
        //Draw the progress bar.

        paint.setColor(Color.rgb(200, 200, 200));
        canvas.drawRect(100, screenSize.y - 100,  screenSize.x - 100, screenSize.y - 300, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(105, screenSize.y - 105,  screenSize.x - 105, screenSize.y - 295, paint);
        paint.setColor(Color.rgb(100, 100, 100));
        canvas.drawRect(150, screenSize.y - 150,  screenSize.x - 150, screenSize.y - 250, paint);
        paint.setColor(Color.rgb(200, 200, 200));
        canvas.drawRect(150, screenSize.y - 150,  150 + progressBarRight, screenSize.y - 250, paint);

        paint.setTextSize(60);
        paint.setColor(Color.BLACK);
        if(doneLoading){
            canvas.drawText("Press Anywhere to Begin", screenSize.x / 2,screenSize.y - 210 + paint.getTextSize() / 2, paint);
        }
        else{
            canvas.drawText("Loading...", screenSize.x / 2, screenSize.y - 210 + paint.getTextSize() / 2, paint);
        }


        ourHolder.unlockCanvasAndPost(canvas);
    }
}
