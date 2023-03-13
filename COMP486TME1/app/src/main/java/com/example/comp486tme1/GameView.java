package com.example.comp486tme1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: GameView (extends Viewable)
    Description: This class is functionally the GameManager of this project. Contains all of the drawing
    and updating logic required to run the game, handles controls and UI, and looks after game
    state changes.

 */
public class GameView extends Viewable {

    public static GameView instance;
    public static Level level;
    public static float difficultyModifier = 1; // Adjusts how many enemies spawn and their stats.

    //info for end game screen.
    public static int floorsCleared = 0;
    public static int enemiesKilled = 0;
    public static int coinsCollected = 0;

    private final SurfaceHolder ourHolder;

    //Control information.
    private volatile boolean running;
    private volatile int updatesThisFrame = 0;
    private Thread gameThread;
    private int FRAMES_PER_SECOND = 60;
    private long nextFrame;
    private long lastFrame;

    // Tracks which objects will be updated each frame based on player distance
    private ArrayList<GameObject> updateList;

    public Vector2 screenPosition; // Draw position of the screen.
    public Vector2 handleAdditions; // Adds for look direction.

    public JoyStick leftJoy;
    public JoyStick rightJoy;
    public GameButton spellButton;
    private ArrayList<Touch> touches; // list of active touches.

    public UI ui;
    public Player player;


    /*
    Inputs: Context (activity constructor.)

    Outputs: None. Creates a GameViewObject.

    Called by: GameActivity.

    Calls: Various constructors, AddPlayer()
    */
    public GameView(Context context) {
        super(context);
        instance = this;
        running = true;

        addPlayer();
        ourHolder = getHolder();
        ui = new UI();
        //Create gameobjects.

        screenPosition = player.position.clone();
        handleAdditions = new Vector2(0.0001f, 0.0001f);
        int joySize = (int)(getScreenSize().y / 8);
        leftJoy = new JoyStick(joySize, new Vector2( 300 , getScreenSize().y - joySize * 3f));
        rightJoy = new JoyStick(joySize, new Vector2(getScreenSize().x - 300, getScreenSize().y - joySize * 3f));
        spellButton = new SpellButton(new Vector2(rightJoy.center.x - rightJoy.size, rightJoy.center.y - rightJoy.size * 4),
                new Vector2(rightJoy.size * 2, rightJoy.size * 2));
        touches = new ArrayList<>();
        setOnTouchListener(this);

        nextFrame = System.currentTimeMillis() + (FRAMES_PER_SECOND / 1000);
        lastFrame = System.currentTimeMillis();
    }

    /*
    Inputs: None.

    Outputs: None. Performs the games main loop.

    Called by: Java Internals.

    Calls: CreateUpdateList, Update, Draw, Control
    */
    @Override
    public void run() {
        while(running){
            //System.out.println("Creating update List");
            createUpdateList();
            //System.out.println("Starting updates");
            while(updatesThisFrame > 0){ // For if the frame rate is above or below the desired framerate.
                //System.out.println("Updates Left: " + updatesThisFrame);
                update();
                updatesThisFrame--;
            }
            //System.out.println("Drawing");
            draw();
            //System.out.println("Controlling");
            control();
        }
    }

    /*
    Inputs: None.

    Outputs: None. Determines what gameobjects are drawn and updated each frame.

    Called by: run()

    Calls: getScreenSize()
    */
    private void createUpdateList(){
        updateList = new ArrayList<>();
        GameObject.collidableObjects = new ArrayList<>();
        Vector2 updatePosition = screenPosition.clone();
        updatePosition.x += getScreenSize().x / 2;
        updatePosition.y += getScreenSize().y / 2;
        float updateDistance = getScreenSize().x * 0.7f;
        if(GameObject.gameObjects != null){
            for(GameObject go: GameObject.gameObjects){
                //Only updates and draws gameobjects that are a given distance from the player. Always updates the player.
                if(Vector2.getDistance(updatePosition, go.position) < updateDistance || Player.class.isInstance(go)){
                    updateList.add(go);
                    if(go.isTrigger || go.canCollide){
                        GameObject.collidableObjects.add(go);
                    }
                }
                else if(go.distanceCulling){
                    go.toRemove = true;
                }
            }
        }
    }

    /*
    Inputs: None.

    Outputs: None. Updates each GameObject in the GameObject list. Handles destroying of gameobjects as well.

    Called by: run()

    Calls: GameObject.update, GameObject.onDestroy
    */
    private void update(){
        //Update all gameObjects in the update zone.
        if(updateList != null){
            for(GameObject go: updateList){
                go.update();
            }
        }

        //Deal with clean up and spawning after update to avoid concurrent issues.
        if(GameObject.gameObjects != null){
            Iterator<GameObject> it = GameObject.gameObjects.iterator();
            while(it.hasNext()){
                GameObject go = it.next();
                if(go.toRemove){
                    go.onDestroy();
                    it.remove();
                }
            }
        }
    }

    /*
    Inputs: None.

    Outputs: None. Draws all gameObjects and UI to the screen.

    Called by: run()

    Calls: Paint and Canvas methods. drawInFrame().
    */
    private void draw(){
        if(!ourHolder.getSurface().isValid()) return;
        Canvas canvas = ourHolder.lockCanvas();
        if(canvas == null) return;
        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setAntiAlias(false);
        paint.setDither(false);
        paint.setAntiAlias(false);
        screenPosition = player.getCenter();
        screenPosition.subtract(new Vector2(getScreenSize().x / 2, getScreenSize().y / 2));
        //Add the position of a stick for "Looking" in a given direction.
        Vector2 newHandleAdditions = new Vector2(0.0001f, 0.0001f); // avoiding NANs
        if(leftJoy.isGrabbed){
            newHandleAdditions.add(leftJoy.getHandlePosition());
        }
        newHandleAdditions.normalize();
        newHandleAdditions.scale(10);
        handleAdditions.add(newHandleAdditions);
        screenPosition.add(handleAdditions);
        handleAdditions.scale(0.85f);
        canvas.drawARGB(255, 0, 0,0); // Erase the previous frame.
        //Draw item layers 0 - 3.
        for(int i = 0; i < 4; i++){
            if(GameObject.gameObjects != null){
                for(GameObject go: updateList){
                    if(go.drawOrder != i || !go.toDraw) { continue; }
                    if(Enemy.class.isInstance(go)) { drawHealthBar(canvas, (Enemy) go, paint); }
                    drawInFrame(canvas, go.getDrawable(), go.position, paint);
                    if(Tile.class.isInstance(go)){
                        if(((Tile) go).walkable){
                            paint.setColor(Color.GREEN);

                        }else{
                            paint.setColor(Color.RED);
                        }
                        //Debugs whether a tile is walkable or not.
                        /*
                        canvas.drawRect(go.position.x - screenPosition.x, go.position.y - screenPosition.y,
                                go.position.x - screenPosition.x + level.scaledTileWidth,
                                go.position.y - screenPosition.y + level.scaledTileWidth, paint);

                         */
                    }
                }
            }
        }
        //Draw UI elements.
        leftJoy.draw(canvas);
        rightJoy.draw(canvas);
        spellButton.draw(canvas);
        ui.drawUI(canvas);
        ourHolder.unlockCanvasAndPost(canvas);
    }

    /*
    Inputs: Canvas to draw on. Bitmap to be drawn. Vector reprenting the world position to draw at. Paint.

    Outputs: None. For drawing in the cameras view instead of world view.

    Called by: draw()

    Calls: Canvas.drawBitmap
    */
    private void drawInFrame(Canvas c, Bitmap b, Vector2 v, Paint paint){
        c.drawBitmap(b, v.x - screenPosition.x, v.y - screenPosition.y, paint);
    }

    /*
    Inputs: Canvas to be drawn on, Enemy object to retrieve data from, Paint.

    Outputs: None. Draws the health of the given enemy to the screen.

    Called by: draw()

    Calls: None.
    */
    private void drawHealthBar(Canvas c, Enemy enemy, Paint paint){
        if(enemy.lastDrawable == null){return;}
        if(Mimic.class.isInstance(enemy) && !((Mimic)enemy).attacking){ return; } // Dont draw the mimic until it is revealed
        int enemyWidth = enemy.lastDrawable.getWidth();
        float enemyHealth = enemy.health / enemy.maxHealth;
        if(enemyHealth < 0){
            enemyHealth = 0;
        }

        paint.setARGB(100, 255, 255, 255);
        Rect r = new Rect((int)(enemy.position.x - screenPosition.x - 10), (int)(enemy.position.y - screenPosition.y),
                (int)(enemy.position.x + enemyWidth - screenPosition.x + 10), (int)(enemy.position.y - 10 - screenPosition.y));
        c.drawRect(r, paint);
        r.left += 2;
        r.right -= 2;
        r.top -= 2;
        r.bottom += 2;

        r.right = r.left - (int)((r.left - r.right) * enemyHealth);
        paint.setARGB(200, 255, 0, 0);
        c.drawRect(r, paint);
        paint.setColor(Color.WHITE);
    }

    /*
    Inputs: None.

    Outputs: None.  Locks the framerate at 60fps. Allows for multiple updates per frame when framerate is low.

    Called by: run()

    Calls: None.
    */
    private void control(){
        //Sleep if not ready for next update.
        while(System.currentTimeMillis() < nextFrame){
            try {
                gameThread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        //System.out.println((int)(1000.0 / (System.currentTimeMillis() - lastFrame))); // Prints the FPS
        while(nextFrame < System.currentTimeMillis()){ //While not updated to current time.
            nextFrame += (1000 / FRAMES_PER_SECOND);
            updatesThisFrame++;
        }
        lastFrame = System.currentTimeMillis();
    }

    /*
    Inputs: None.

    Outputs: None. Pauses the view.

    Called by: GameActivity

    Calls: Thread method.
    */
    public void pause(){
        running = false;
        try{
            gameThread.join();
        } catch (InterruptedException e) {  }
    }

    /*
    Inputs: None.

    Outputs: None. Resumes execution.

    Called by: GameActivity.

    Calls: threading methods.
    */
    public void resume(){
        running = true;
        gameThread = new Thread(this);
        gameThread.setPriority(Thread.MAX_PRIORITY);
        gameThread.start();
        nextFrame = System.currentTimeMillis() + (1000 / FRAMES_PER_SECOND);
    }

    /*
    Inputs: N/A

    Outputs: Class used for interfacing with the multiple joysticks.

    Called by: N/A

    Calls: N/A
    */
    public class Touch {
        public Vector2 position;
        public int ID;
    }

    /*
    Inputs: The current view, and a MotionEvent.

    Outputs: Boolean (from Activity inheritence).

    Called by: Android system

    Calls: Various Joystick methods. Touch (above class) assignments and constructor calls.
    */
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        touches.clear();
        //Add touches.
        for(int i = 0; i < event.getPointerCount(); i++){
            Touch touch = new Touch();
            touch.position = new Vector2(event.getX(i), event.getY(i));
            touch.ID = event.getPointerId(i);
            touches.add(touch);
        }
        //Remove the touch that is lifting on lift event
        if(event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_POINTER_UP){
            if(event.getPointerCount() == 1){
                touches.clear();
                leftJoy.release();
                rightJoy.release();
                return true;
            } else{
                Touch toRemove = null;
                for(Touch touch: touches){
                    if(touch.ID == event.getActionIndex()){
                        toRemove = touch;
                    }
                }
                touches.remove(toRemove);
            }
        }
        //Reset all joys whos touch have been reset.
        for(Touch touch: touches){
            if(!rightJoy.isGrabbed && Vector2.getDistance(touch.position, rightJoy.center) < rightJoy.size){
                rightJoy.grab(touch.ID);
            }
            else if(rightJoy.isGrabbed && rightJoy.pointerID == touch.ID){
                rightJoy.input(touch.position);
            }
            if(!leftJoy.isGrabbed && Vector2.getDistance(touch.position, leftJoy.center) < leftJoy.size){
                leftJoy.grab(touch.ID);
            }
            else if(leftJoy.isGrabbed && leftJoy.pointerID == touch.ID){
                leftJoy.input(touch.position);
            }

        }
        spellButton.input( event);
        //Release joysticks if necessary
        boolean right = false;
        boolean left = false;
        for(Touch touch: touches) {
            if(touch.ID == rightJoy.pointerID){
                right = true;
            }
            if(touch.ID == leftJoy.pointerID){
                left = true;
            }
        }
        if(!left) leftJoy.release();
        if(!right) rightJoy.release();
        return true;
    }

    /*
    Inputs: None.

    Outputs: Returns the current gameView instance.

    Called by: Player.update(), Player.Hit(), Tile.onCollision() (exit tile)

    Calls: None.
    */
    public static GameView getView(){
        return instance;
    }

    /*
    Inputs: None.

    Outputs: Vector2 for getting the size of the screen.

    Called by: Constructor. createUpdateList(), draw()

    Calls: System Resources methods.
    */
    public static Vector2 getScreenSize() {
        return new Vector2(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
    }

    /*
    Inputs: None.

    Outputs: None. Called when the player dies to change to the end game view.

    Called by: Player.hit()

    Calls: resetGame(), MainActivity.setView()
    */
    public void GameOver(){
        player.alive = false;
        resetGame();
        MainActivity.instance.setView(MainActivity.VIEWS.GameOverView);
    }

    /*
    Inputs: None.

    Outputs: None. Starts the process of changing to the next floor.

    Called by: Tile.onCollision()

    Calls: resetGame(), MainActivity.setView()
    */
    public void leaveLevel(){
        resetGame();
        floorsCleared++;
        difficultyModifier = 1 + floorsCleared;
        MainActivity.instance.setView(MainActivity.VIEWS.ShopView);
    }

    /*
    Inputs: None

    Outputs: None. Changes to endGame state with win flag set.

    Called by: WizardBoss.onDestroy()

    Calls: resetGame(), MainActivity.setView()
    */
    public void win() {
        player.alive = false;
        resetGame();
        MainActivity.instance.setView(MainActivity.VIEWS.GameWonView);
    }

    //Resets the instance specific variables.
    /*
    Inputs: None.

    Outputs: None. Resets the state of the gameView to original state.

    Called by:

    Calls: Joystick.release().
    */
    private void resetGame(){
        GameObject.gameObjects = new ArrayList<>();
        level = null;
        touches = new ArrayList<>();
        rightJoy.release();
        leftJoy.release();
        difficultyModifier = 1;
    }

    /*
    Inputs: None.

    Outputs: None. Adds player and weapons to scene if existent, otherwise creates one.

    Called by: GameActivity.onCreate(), this constructor.

    Calls: Player().
    */
    public void addPlayer(){
        if(Player.instance == null || !Player.instance.alive){ // create new player if necessary
            player = new Player();
        }
        else{
            player = Player.instance;
            GameObject.gameObjects.add(Player.instance);
            for(GameObject go: Player.instance.weapons){
                GameObject.gameObjects.add(go);
            }
        }
        player.position = new Vector2(500 * level.scaledTileWidth, 500* level.scaledTileWidth);
    }

    /*
    Inputs: None.

    Outputs: None. Resets the static data of the gameView and player.

    Called by: EndView.onTouch()

    Calls: none.
    */
    public static void clearData(){
        coinsCollected = 0;
        floorsCleared = 0;
        enemiesKilled = 0;
        Player.instance = null;
    }

}
