package com.ellzone.slotpuzzle2d.components;

import com.badlogic.ashley.core.Component;
import com.ellzone.slotpuzzle2d.sprites.LightButton;

public class LightButtonComponent implements Component {
    public LightButton lightButton;
    public LightButtonComponent(LightButton lightButton) {
        this.lightButton = lightButton;
    }
}
