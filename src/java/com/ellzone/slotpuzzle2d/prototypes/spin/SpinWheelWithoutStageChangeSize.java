package com.ellzone.slotpuzzle2d.prototypes.spin;

import com.badlogic.gdx.Gdx;
import com.ellzone.slotpuzzle2d.spin.SpinWheelForSlotPuzzle;

public class SpinWheelWithoutStageChangeSize extends SpinWheelWithoutStage {
    public SpinWheelWithoutStageChangeSize() {
        super();
    }

    @Override
    protected void setUpSpinWheel() {
        spinWheel = new SpinWheelForSlotPuzzle(
                550.0f,
                Gdx.graphics.getWidth() / 2,
                Gdx.graphics.getHeight() / 2,
                NUMBER_OF_PEGS,
                world);
        spinWheel.setUpSpinWheel();
    }
}
