package com.ellzone.slotpuzzle2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class ColorComponent implements Component {
    public Color color;
    public ColorComponent(float red, float green, float blue, float alpha) {
        this.color = new Color(red, green, blue, alpha);
    }
}
