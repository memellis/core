/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.level.creator;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class GameLevel {
    private final World world;
    private final LevelDoor levelDoor;
    private final Array<AnimatedReel> animatedReels;
    private final Array<ReelTile> reelTiles;
    private final TiledMap levelTileMap;
    private final AnnotationAssetManager annotationAssetManager;
    private final TextureAtlas cardDeckAtlas;
    private final TweenManager tweenManager;
    private final PhysicsManagerCustomBodies physics;
    private final GridSize levelGridSize;
    private final PlayStates playState;

    public GameLevel(World world,
                     PhysicsManagerCustomBodies physics,
                     LevelDoor levelDoor,
                     Array<AnimatedReel> animatedReels,
                     Array<ReelTile> reelTiles,
                     TiledMap tileMapLevel,
                     AnnotationAssetManager annotationAssetManager,
                     TextureAtlas cardDeckAtlas,
                     TweenManager tweenManager,
                     GridSize levelGridSize,
                     PlayStates playState) {
        this.world = world;
        this.levelDoor = levelDoor;
        this.animatedReels = animatedReels;
        this.reelTiles = reelTiles;
        this.levelTileMap = tileMapLevel;
        this.annotationAssetManager = annotationAssetManager;
        this.cardDeckAtlas = cardDeckAtlas;
        this.tweenManager = tweenManager;
        this.physics = physics;
        this.levelGridSize = levelGridSize;
        this.playState = playState;
    }

    public World getWorld() {
        return world;
    }

    public LevelDoor getLevelDoor() {
        return levelDoor;
    }

    public Array<AnimatedReel> getAnimatedReels() {
        return animatedReels;
    }

    public Array<ReelTile> getReelTiles() {
        return reelTiles;
    }

    public TiledMap getLevelTileMap() {
        return levelTileMap;
    }

    public AnnotationAssetManager getAnnotationAssetManager() {
        return annotationAssetManager;
    }

    public TextureAtlas getCardDeckAtlas() {
        return cardDeckAtlas;
    }

    public TweenManager getTweenManager() {
        return tweenManager;
    }

    public PhysicsManagerCustomBodies getPhysics() {
        return physics;
    }

    public GridSize getLevelGridSize() {
        return levelGridSize;
    }

    public PlayStates getPlayState() {
        return playState;
    }
}
