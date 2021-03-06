package com.ellzone.slotpuzzle2d.sprites.reel;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelSprites;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class SlotMachineAnimatedReel {
    private com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel animatedReel;
    private float x, y, tileWith, tileHeight, reelTileWidth, reelTileHeight;
    private int endReel;
    private AnnotationAssetManager annotationAssetManager;

    public SlotMachineAnimatedReel(float x,
                                   float y,
                                   float tileWidth,
                                   float tileHeight,
                                   float reelDisplayWidth,
                                   float reelDisplayHeight,
                                   int endReel,
                                   TweenManager tweenManager,
                                   AnnotationAssetManager annotationAssetManager) {
        initialiseAnimatedReel(x, y , tileWidth, tileHeight,
                               reelDisplayWidth, reelDisplayHeight,
                               endReel,
                               tweenManager,
                               annotationAssetManager);
    }

    public AnimatedReel getAnimatedReel() {
        return animatedReel;
    }

    private void initialiseAnimatedReel(float x,
                                        float y,
                                        float tileWidth,
                                        float tileHeight,
                                        float reelDisplayWidth,
                                        float reelDisplayHeight,
                                        int endReel,
                                        TweenManager tweenManager,
                                        AnnotationAssetManager annotationAssetManager) {
        this.annotationAssetManager = annotationAssetManager;
        animatedReel = new com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel(getReelTexture(),
                                        x, y,
                                        tileWidth, tileHeight,
                                        reelDisplayWidth, reelDisplayHeight,
                                        endReel,
                tweenManager);
    }

    private Sound getStoppingSound() {
        return (Sound) annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
    }

    private Sound getSpinningSound() {
        return (Sound) annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
    }

    private Texture getReelTexture() {
        com.ellzone.slotpuzzle2d.sprites.reel.ReelSprites reelSprites = new ReelSprites(annotationAssetManager);
        Sprite[] sprites = reelSprites.getSprites();
        Pixmap slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        return new Texture(slotReelScrollPixmap);
    }
}
