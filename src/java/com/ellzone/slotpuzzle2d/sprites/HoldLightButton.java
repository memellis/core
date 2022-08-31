package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;


import box2dLight.RayHandler;

public class HoldLightButton extends LightButton {
    private Array<Integer> entityIds;

    public HoldLightButton(
            World world,
            RayHandler rayHandler,
            float positionX,
            float positionY,
            int buttonWidth,
            int buttonHeight) {
        super(
            world,
            rayHandler,
            positionX,
            positionY,
            buttonWidth,
            buttonHeight,
            new BitmapFont(),
            "",
             "Hold",
              false);
    }

    public void setEntityIds(Array<Integer> entityIds) {
        this.entityIds = entityIds;
    }
}
