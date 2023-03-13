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

import java.util.ArrayList;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: October 7th, 2022

    Class: ShopView (extends Viewable)
    Description: A viewable which allows the player to interact with the in-game shop.

 */

public class ShopView extends Viewable{

    private final Paint paint;
    private final SurfaceHolder ourHolder;
    private volatile boolean running;
    private Thread gameThread;
    private int FRAMES_PER_SECOND = 60;
    private long nextFrame;
    private long lastFrame;

    //Sprites for display.
    private Sprite coin;
    private Sprite heart;

    //List of buttons to be drawn and handled.
    private ArrayList<ShopButton> buttons;

    /*
    Inputs: Context (from activity)

    Outputs: Creates a shopView

    Called by: GameActivity

    Calls: None.
    */
    public ShopView(Context context) {
        super(context);
        paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setAntiAlias(false);
        ourHolder = getHolder();
        setOnTouchListener(this);

        Bitmap spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.coin);
        int frameWidth = spriteSheet.getWidth() / 4;
        int frameHeight = spriteSheet.getHeight();
        coin = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 2);

        spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                R.drawable.ui_heart);
        frameWidth = spriteSheet.getWidth() / 3;
        frameHeight = spriteSheet.getHeight();
        heart = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 1);

        buttons = new ArrayList<>();

        int screenCenter = (int)getScreenSize().x / 2;
        buttons.add(new ShopButton(new Vector2(screenCenter - 700, 300), new Vector2(200, 200),
                ShopButton.ShopType.HealthAdd));
        buttons.add(new ShopButton(new Vector2(screenCenter - 400, 300), new Vector2(200, 200),
                ShopButton.ShopType.HealthRefill));
        buttons.add(new ShopButton(new Vector2(screenCenter + 200, 300), new Vector2(500, 200),
                ShopButton.ShopType.Exit));

        buttons.add(new ShopButton(new Vector2(screenCenter - 700, 600), new Vector2(200, 200),
                ShopButton.ShopType.WeaponBolt));
        buttons.add(new ShopButton(new Vector2(screenCenter - 400, 600), new Vector2(200, 200),
                ShopButton.ShopType.WeaponExplosive));
        buttons.add(new ShopButton(new Vector2(screenCenter - 100, 600), new Vector2(200, 200),
                ShopButton.ShopType.WeaponRapid));
        buttons.add(new ShopButton(new Vector2(screenCenter + 200, 600), new Vector2(200, 200),
                ShopButton.ShopType.WeaponTwin));
        buttons.add(new ShopButton(new Vector2(screenCenter + 500, 600), new Vector2(200, 200),
                ShopButton.ShopType.WeaponBlast));


    }

    /*
    Inputs: None

    Outputs: None. Pauses execution

    Called by: GameActivity.pause()

    Calls: None
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

    Outputs: None. Resumes activity

    Called by: GameActivity.resume()

    Calls:None
    */
    @Override
    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
        nextFrame = lastFrame + (1000 / FRAMES_PER_SECOND);
    }

    /*
    Inputs: View, Motionevent (Activity)

    Outputs: Boolean (Activity)

    Called by: Android system

    Calls: ShopButton.input()
    */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        for(ShopButton button: buttons){
            button.input(motionEvent);
        }
        return false;
    }

    /*
    Inputs: None

    Outputs: None. update logic.

    Called by: Android system

    Calls: draw() control()
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

    Outputs: None. Draws the screen

    Called by: run()

    Calls: drawUI()
    */
    private void draw(){
        if(!ourHolder.getSurface().isValid()) return;
        Canvas canvas = ourHolder.lockCanvas();
        if(canvas == null) return;
        paint.setColor(Color.rgb((int)(Math.random() * 255), 0, 0));
        canvas.drawARGB(255, 0, 0,0);
        drawUI(canvas);
        ourHolder.unlockCanvasAndPost(canvas);
    }

    /*
    Inputs: None

    Outputs: None. Slows the thread.

    Called by: run()

    Calls: None
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

    Outputs: Vector2 storing screen dimensions.

    Called by: DrawUI()

    Calls: Resources calls.
    */
    public static Vector2 getScreenSize() {
        return new Vector2(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
    }

    /*
    Inputs: Canvas to be drawn on.

    Outputs: none. Draws UI and buttons to canvas.

    Called by: draw()

    Calls: Paint / canvas / sprite methods.
    */
    private void drawUI(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.rgb(235, 210, 50));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(150);
        int screenCenter = (int)getScreenSize().x / 2;
        canvas.drawText("Welcome to the shop!", screenCenter, 200, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);

        //Draw player coins
        canvas.drawBitmap(coin.getBitmap(), 100, 100, paint);
        canvas.drawText(Integer.toString(GameView.coinsCollected), 100 + coin.getBitmap().getWidth(),
              100 + coin.getBitmap().getHeight() / 2 + paint.getTextSize() / 2, paint);

        canvas.drawBitmap(heart.getBitmap(), 100, 200, paint);
        canvas.drawText(Integer.toString((int)Player.instance.health) + "/" + Integer.toString((int)Player.instance.maxHealth),
                100 + heart.getBitmap().getWidth(),200 + heart.getBitmap().getHeight() / 2 + paint.getTextSize() / 2, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        for(ShopButton button: buttons){
            button.draw(canvas);
            String text = "";
            switch (button.shopType){
                case HealthAdd:
                    text = "Buy Hearts!";
                    break;
                case HealthRefill:
                    text = "Refill Hearts!";
                    break;
                case WeaponBolt:
                    if(button.hasWeapon){
                        text = "Upgrade Bolt!";
                    }
                    else{
                        text = "Purchase Bolt!";
                    }
                    break;
                case WeaponBlast:
                    if(button.hasWeapon){
                        text = "Upgrade Blast!";
                    }
                    else{
                        text = "Purchase Blast!";
                    }
                    break;
                case WeaponRapid:
                    if(button.hasWeapon){
                        text = "Upgrade Rapid!";
                    }
                    else{
                        text = "Purchase Rapid!";
                    }
                    break;
                case WeaponExplosive:
                    if(button.hasWeapon){
                        text = "Upgrade Explosive!";
                    }
                    else{
                        text = "Purchase Explosive!";
                    }
                    break;
                case WeaponTwin:
                    if(button.hasWeapon){
                        text = "Upgrade Twin!";
                    }
                    else{
                        text = "Purchase Twin!";
                    }
                    break;
            }
            canvas.drawText(text, button.bounds.min.x + (button.bounds.max.x - button.bounds.min.x)/ 2,
                    button.bounds.min.y - 20, paint);
            canvas.drawText(button.infoText, button.bounds.min.x + (button.bounds.max.x - button.bounds.min.x)/ 2,
                    button.bounds.max.y - 5, paint);

            if(button.shopType != ShopButton.ShopType.Exit){
                //Draw cost on button
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawBitmap(coin.getBitmap(), button.bounds.min.x,
                        button.bounds.min.y, paint);
                canvas.drawText(Integer.toString(button.cost), button.bounds.min.x + coin.getBitmap().getWidth(),
                        button.bounds.min.y + coin.getBitmap().getHeight() / 2 + paint.getTextSize() / 2, paint);
                paint.setTextAlign(Paint.Align.CENTER);
            }
            else{
                paint.setTextSize(60);
                paint.setTextAlign(Paint.Align.CENTER);
                Vector2 buttonMiddle = button.bounds.max.clone();
                buttonMiddle.subtract(button.bounds.min);
                canvas.drawText("Next Level", button.bounds.min.x + buttonMiddle.x / 2,
                        button.bounds.min.y + buttonMiddle.y / 2 + paint.getTextSize() / 2, paint);
                paint.setTextSize(30);
            }

        }
    }
}
