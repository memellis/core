package com.ellzone.slotpuzzle2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LightVisualComponent implements Component {
    public TextureRegion region;

    public LightVisualComponent(TextureRegion region) {
        this.region = region;
    }
}
