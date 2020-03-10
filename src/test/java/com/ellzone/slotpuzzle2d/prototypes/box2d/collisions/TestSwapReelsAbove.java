package com.ellzone.slotpuzzle2d.prototypes.box2d.collisions;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.ellzone.slotpuzzle2d.gdx.MyGDXApplication;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;

import org.junit.jupiter.api.Test;

public class TestSwapReelsAbove {
    @Test
    public void testSwapReelsAboveMeTwoAnimatedReels() {
        Gdx.app = new MyGDXApplication();
        Array<AnimatedReel> animatedReels = new Array<>();
        AnimatedReel animatedReel = new AnimatedReel(
                null,
                160,
                40,
                40,
                40,
                40,
                40,
                0,
                null
        );
        animatedReel.getReel().setDestinationX(160);
        animatedReel.getReel().setDestinationY(40);
        animatedReels.add(animatedReel);
        animatedReel = new AnimatedReel(
                null,
                160,
                80,
                40,
                40,
                40,
                40,
                0,
                null
        );
        animatedReels.add(animatedReel);
    }

}
