package com.ellzone.slotpuzzle2d.level.creator;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.level.FlashSlots;
import com.ellzone.slotpuzzle2d.level.hidden.HiddenPattern;
import com.ellzone.slotpuzzle2d.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.lights.HoldLightButton;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.slothandle.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.FrameRate;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public interface PlayScreenLevelInterface {
    void loadLevel(
            MapTile mapTileLevel,
            LevelCallback stoppedSpinningCallback,
            LevelCallback stoppedFlashingCallback);

    Texture getSlotReelScrollTexture();

    World getBox2dWorld();

    AnnotationAssetManager getAnnotationAssetManager();

    Hud getHud();

    FrameRate getFrameRate();

    GridSize getLevelGridSize();

    Array<AnimatedReel> getAnimatedReels();

    Array<ReelTile> getReelTiles();

    Array<HoldLightButton> getHoldLightButtons();

    Array<SlotHandleSprite> getSlotHandles();

    FlashSlots getFlashSlots();

    HiddenPattern getHiddenPattern();

    LevelLoader getLevelLoader();

    TweenManager getTweenManager();

    TiledMap getTiledMapLevel();
}
