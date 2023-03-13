package com.example.comp486tme1;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*
    Course: COMP 486 (Mobile and Internet Game Development)
    Project: DELVER! (Assignment 1)
    Name: James Bombardier
    Date: August 24th, 2022

    Class: SoundManager
    Description: An API for easily playing sounds from a soundpool.

 */

public class SoundManager {

    public static SoundManager instance; // For static access.
    private SoundPool soundPool; // The soundpool for playing all sounds.

    public enum Sounds {Cast, Chest, Coin, Explosion, Heal, Hit, Powerup, Accept, ShopBuy, ShopNoMoney} // enum for easy access.
    private MediaPlayer music;

    /*
    Inputs: Context for resource loading

    Outputs: Creates a sound manager.

    Called by: create()

    Calls: setMusicVolume(), soundPool.load(), mediaPlayer methods.
    */
    public SoundManager(Context context){
        instance = this;
        music = MediaPlayer.create(context, R.raw.music);
        music.setLooping(true);
        setMusicVolume(0.5f);
        music.start();

        soundPool = new SoundPool.Builder().build();
        //Add all sounds in.
        soundPool.load(context, R.raw.cast, 0);
        soundPool.load(context, R.raw.chest_open, 0);
        soundPool.load(context, R.raw.coin, 0);
        soundPool.load(context, R.raw.explosion, 0);
        soundPool.load(context, R.raw.heal, 0);
        soundPool.load(context, R.raw.hit, 0);
        soundPool.load(context, R.raw.powerup, 0);
        soundPool.load(context, R.raw.confirm, 0);
        soundPool.load(context, R.raw.shop_buy, 0);
        soundPool.load(context, R.raw.shop_no_money, 0);
    }
    //Plays a sound.
    /*
    Inputs:

    Outputs:

    Called by: Many places.

    Calls: Soundpool.play()
    */
    public void playSound(Sounds sound){
        soundPool.play(sound.ordinal() + 1, 0.5f, 0.5f, 0, 0, 1);
    }

    /*
    Inputs: Sound to be played, position sound is coming from.

    Outputs: None. Plays a sound to the soundpool

    Called by: Player projectile

    Calls: Soundpool.play
    */
    public void playSound(Sounds sound, Vector2 soundPosition){
        float rightVolume = 0.5f;
        float leftVolume = 0.5f;
        if(Player.instance != null){
            if(soundPosition.x > Player.instance.position.x){
                leftVolume = 0;
            }
            else{
                rightVolume = 0;
            }
        }
        soundPool.play(sound.ordinal() + 1, leftVolume, rightVolume, 0, 0, 1);
    }

    /*
    Inputs: Context (for resource loading)

    Outputs: None.

    Called by: MainActivity.

    Calls: None.
    */
    public static void create(Context context){
        if(instance != null) {return;}
        instance = new SoundManager(context);
    }

    /*
    Inputs: A float for the new volume of the music.

    Outputs: None. Sets the music volume to the passed level.

    Called by: LoadingView, SoundManager.

    Calls: MediaPlayer.setVolume()
    */
    public void setMusicVolume(float newVolume){
        music.setVolume(newVolume, newVolume);
    }


}
