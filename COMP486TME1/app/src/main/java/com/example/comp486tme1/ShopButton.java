package com.example.comp486tme1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: October 7th, 2022

    Class: ShopButton (extends GameButton)
    Description: A button used for purchasing items in the ShopView.

 */

public class ShopButton extends GameButton{

    public enum ShopType {HealthAdd, HealthRefill, WeaponBlast, WeaponTwin, WeaponRapid,
        WeaponExplosive, WeaponBolt, Exit}
    public ShopType shopType;
    public boolean hasWeapon;
    public int cost;
    public Sprite icon;
    public String infoText = "";

    /*
    Inputs: Position of the button, extents of the button, and type of button the shopButton will be.

    Outputs: Creates shop button of specified type at specified position and size.

    Called by: ShopView

    Calls: super(), setInfo()
    */
    public ShopButton(Vector2 position, Vector2 size, ShopType type) {
        super(position, size);
        shopType = type;
        Bitmap spriteSheet;
        int frameWidth;
        int frameHeight;
        int frames = 4;

        switch (shopType) {
            case HealthAdd:
                cost = 150;
                frames = 3;
                spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.ui_heart);
                frameWidth = spriteSheet.getWidth() / frames;
                frameHeight = spriteSheet.getHeight();
                icon = new Sprite(Bitmap.createBitmap(spriteSheet, (frames - 1) * frameWidth, 0, frameWidth, frameHeight), 2f);
                break;
            case HealthRefill:
                cost = 75;
                frames = 3;
                spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.ui_heart);
                frameWidth = spriteSheet.getWidth() / frames;
                frameHeight = spriteSheet.getHeight();
                icon = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 2f);
                break;
            case WeaponBolt:
                for(Weapon weapon: Player.instance.weapons){
                    hasWeapon = weapon.thisType == Weapon.weaponType.Bolt;
                    if(hasWeapon){
                        cost = 125;
                        break;
                    }
                    cost = 200;
                }
                spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.spell_bolt);
                frameWidth = spriteSheet.getWidth() / frames;
                frameHeight = spriteSheet.getHeight();
                icon = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 1);
                break;
            case WeaponBlast:
                for(Weapon weapon: Player.instance.weapons){
                    hasWeapon = weapon.thisType == Weapon.weaponType.Blast;
                    if(hasWeapon){
                        cost = 125;
                        break;
                    }
                    cost = 200;
                }
                spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.spell_wave);
                frameWidth = spriteSheet.getWidth() / frames;
                frameHeight = spriteSheet.getHeight();
                icon = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 0.5f);
                break;
            case WeaponRapid:
                for(Weapon weapon: Player.instance.weapons){
                    hasWeapon = weapon.thisType == Weapon.weaponType.Rapid;
                    if(hasWeapon){
                        cost = 125;
                        break;
                    }
                    cost = 200;
                    spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                            R.drawable.spell_wave);
                    frameWidth = spriteSheet.getWidth() / frames;
                    frameHeight = spriteSheet.getHeight();
                    icon = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 0.8f);
                }
                break;
            case WeaponExplosive:
                for(Weapon weapon: Player.instance.weapons){
                    hasWeapon = weapon.thisType == Weapon.weaponType.Explosion;
                    if(hasWeapon){
                        cost = 125;
                        break;
                    }
                    cost = 200;
                }
                spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                        R.drawable.spell_charged);
                frames = 6;
                frameWidth = spriteSheet.getWidth() / frames;
                frameHeight = spriteSheet.getHeight();
                icon = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 0.8f);
                break;
            case WeaponTwin:
                for(Weapon weapon: Player.instance.weapons){
                    hasWeapon = weapon.thisType == Weapon.weaponType.Twin;
                    if(hasWeapon){
                        cost = 125;
                        break;
                    }
                    cost = 200;
                    spriteSheet = BitmapFactory.decodeResource(MainActivity.instance.getResources(),
                            R.drawable.spell_crossed);
                    frames = 6;
                    frameWidth = spriteSheet.getWidth() / frames;
                    frameHeight = spriteSheet.getHeight();
                    icon = new Sprite(Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight), 1);
                }
                break;
        }
        setInfo();
    }

    /*
    Inputs: None

    Outputs: Returns the icon

    Called by: ShopView loop

    Calls: None.
    */
    @Override
    public Sprite getSprite() {
        return icon;
    }

    /*
    Inputs: None.

    Outputs: None. Has differing behavior depending on button type.

    Called by: GameButton.input()

    Calls: SoundManager.playSound(), Player.addWeapon(), MainActivity.setView()
    */
    @Override
    public void onClick() {
        if(GameView.coinsCollected < cost){
            SoundManager.instance.playSound(SoundManager.Sounds.ShopNoMoney);
            return;
        }
        if(shopType == ShopType.HealthRefill && Player.instance.health >= Player.instance.maxHealth){
            SoundManager.instance.playSound(SoundManager.Sounds.ShopNoMoney);
            return;
        }
        GameView.coinsCollected -= cost;
        SoundManager.instance.playSound(SoundManager.Sounds.ShopBuy);
        switch (shopType) {
            case HealthAdd:
                Player.instance.maxHealth += 10;
                Player.instance.health += 10;
                break;
            case HealthRefill:
                if(Player.instance.health >= Player.instance.maxHealth){
                    SoundManager.instance.playSound(SoundManager.Sounds.ShopNoMoney);
                    return;
                }
                Player.instance.health = Player.instance.maxHealth;
                System.out.println("Health Refilled");
                break;
            case WeaponBolt:
                if(hasWeapon){
                    for(Weapon weapon: Player.instance.weapons){
                        if(weapon.thisType == Weapon.weaponType.Bolt){
                            weapon.spellPower += 0.05f;
                        }
                    }
                }else{
                    Player.instance.addWeapon(Weapon.weaponType.Bolt);
                    hasWeapon = true;
                    cost = 125;
                }
                break;
            case WeaponBlast:
                if(hasWeapon){
                    for(Weapon weapon: Player.instance.weapons){
                        if(weapon.thisType == Weapon.weaponType.Blast){
                            weapon.spellPower += 0.05f;
                        }
                    }
                }else{
                    Player.instance.addWeapon(Weapon.weaponType.Blast);
                    hasWeapon = true;
                    cost = 125;
                }
                break;
            case WeaponRapid:
                if(hasWeapon){
                    for(Weapon weapon: Player.instance.weapons){
                        if(weapon.thisType == Weapon.weaponType.Rapid){
                            weapon.spellPower += 0.05f;
                        }
                    }
                }else{
                    Player.instance.addWeapon(Weapon.weaponType.Rapid);
                    hasWeapon = true;
                    cost = 125;
                }
                break;
            case WeaponExplosive:
                if(hasWeapon){
                    for(Weapon weapon: Player.instance.weapons){
                        if(weapon.thisType == Weapon.weaponType.Explosion){
                            weapon.spellPower += 0.05f;
                        }
                    }
                }else{
                    Player.instance.addWeapon(Weapon.weaponType.Explosion);
                    hasWeapon = true;
                    cost = 125;
                }
                break;
            case WeaponTwin:
                if(hasWeapon){
                    for(Weapon weapon: Player.instance.weapons){
                        if(weapon.thisType == Weapon.weaponType.Twin){
                            weapon.spellPower += 0.05f;
                        }
                    }
                }else{
                    Player.instance.addWeapon(Weapon.weaponType.Twin);
                    hasWeapon = true;
                    cost = 125;
                }
                break;
            case Exit:
                SoundManager.instance.playSound(SoundManager.Sounds.Accept);
                MainActivity.instance.setView(MainActivity.VIEWS.LoadingView);
                break;
        }
        GameObject.gameObjects.clear();
        setInfo();
    }

    /*
    Inputs: None.

    Outputs: None. Sets the info text of the shopButton.

    Called by: constructor, onClick()

    Calls: None.
    */
    public void setInfo(){
        String string = "";
        switch (shopType) {
            case HealthAdd:
                string = "Adds 1 heart!!";
                break;
            case HealthRefill:
                if(Player.instance.health >= Player.instance.maxHealth){
                    string = "Health full!";
                }
                int healthPercentage = (int)((Player.instance.health / Player.instance.maxHealth) * 100);
                string = "Health at " + healthPercentage + "%";
                break;
            case WeaponBolt:
                if(!hasWeapon) {
                    string = "Power: 100%";
                    break;
                }
                for(Weapon weapon: Player.instance.weapons){
                    if(weapon.thisType == Weapon.weaponType.Bolt){
                        string = "Power: " + (int)(weapon.spellPower * 100) + "%";
                    }
                }
                break;
            case WeaponBlast:
                if(!hasWeapon) {
                    string = "Power: 100%";
                    break;
                }
                for(Weapon weapon: Player.instance.weapons){
                    if(weapon.thisType == Weapon.weaponType.Blast){
                        string = "Power: " + (int)(weapon.spellPower * 100) + "%";
                    }
                }
                break;
            case WeaponRapid:
                if(!hasWeapon) {
                    string = "Power: 100%";
                    break;
                }
                for(Weapon weapon: Player.instance.weapons){
                    if(weapon.thisType == Weapon.weaponType.Rapid){
                        string = "Power: " + (int)(weapon.spellPower * 100) + "%";
                    }
                }
                break;
            case WeaponExplosive:
                if(!hasWeapon) {
                    string = "Power: 100%";
                    break;
                }
                for(Weapon weapon: Player.instance.weapons){
                    if(weapon.thisType == Weapon.weaponType.Explosion){
                        string = "Power: " + (int)(weapon.spellPower * 100) + "%";
                    }
                }
                break;
            case WeaponTwin:
                if(!hasWeapon) {
                    string = "Power: 100%";
                    break;
                }
                for(Weapon weapon: Player.instance.weapons){
                    if(weapon.thisType == Weapon.weaponType.Twin){
                        string = "Power: " + (int)(weapon.spellPower * 100) + "%";
                    }
                }
                break;
        }
        infoText = string;
    }
}
