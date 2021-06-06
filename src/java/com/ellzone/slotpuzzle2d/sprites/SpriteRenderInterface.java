package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public interface SpriteRenderInterface {
    public Array<TextureRegion> getTextureRegions();
    public Array<Integer> getEntityIds();
    public void setEntityIds(Array<Integer> entityId);
}
