package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.graphics.Texture;
import com.ellzone.slotpuzzle2d.sprites.Reels;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public interface LevelCreatorInjectionInterface {
    public AnnotationAssetManager getAnnotationAssetManager();
    public Reels getReels();
    public Texture getSlotReelScrollTexture();
    public TweenManager getTweenManager();
}
