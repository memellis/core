package com.ellzone.slotpuzzle2d.level.map;

import com.badlogic.gdx.InputProcessor;
import com.ellzone.slotpuzzle2d.level.Level;

public class MapLevel8 extends Level {
    @Override
    public void initialise() {
    }

    @Override
    public String getImageName() {
        return "MapTile";
    }

    @Override
    public InputProcessor getInput() {
        return null;
    }

    @Override
    public String getTitle() {
        String title = "1-8";
        return title;
    }

    public int getLevelNumber() {
        return 7;
    }

    @Override
    public void dispose() {
    }
}
