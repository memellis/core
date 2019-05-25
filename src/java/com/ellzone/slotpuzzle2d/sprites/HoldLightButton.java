package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import box2dLight.RayHandler;

public class HoldLightButton extends LightButton {
    public HoldLightButton(World world, RayHandler rayHandler, float positionX, float positionY, int buttonWidth, int buttonHeight) {
        super(world, rayHandler, positionX, positionY, buttonWidth, buttonHeight, new BitmapFont(), "", "Hold", true);
    }

    public void addTo(Object holdLightButtons) {
        ((Array<HoldLightButton>) holdLightButtons).add(this);
    }
}
