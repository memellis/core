package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public interface LevelCreatorInjectionInterface {
    public AnnotationAssetManager getAnnotationAssetManager();
    public ReelSprites getReelSprites();
    public Texture getSlotReelScrollTexture();
    public TweenManager getTweenManager();
    public TextureAtlas getSlothandleAtlas();
}
