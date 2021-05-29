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

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.SlotPuzzleGame;
import com.ellzone.slotpuzzle2d.level.FlashSlots;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.hidden.HiddenPattern;
import com.ellzone.slotpuzzle2d.level.map.MapLevelNameComparator;
import com.ellzone.slotpuzzle2d.level.reel.ReelType;
import com.ellzone.slotpuzzle2d.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.HoldLightButton;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.FrameRate;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import box2dLight.RayHandler;

public class PlayScreenLevel {

    private final LevelCreatorInjectionInterface levelCreatorInjection;
    private final SlotPuzzleGame game;
    private final LevelDoor levelDoor;
    private TiledMap tiledMapLevel;
    private Texture slotReelScrollTexture;
    private World box2dWorld;
    private RayHandler rayHandler;
    private LevelLoader levelLoader;
    protected Array<ReelTile> reelTiles;
    private HiddenPattern hiddenPattern;
    private FlashSlots flashSlots;
    private Array<AnimatedReel> animatedReels;
    private Array<HoldLightButton> holdLightButtons;
    private Array<SlotHandleSprite> slotHandles;
    private ReelSprites reelSprites;
    private GridSize levelGridSize;
    private Hud hud;
    private FrameRate frameRate;

    public PlayScreenLevel(
            LevelCreatorInjectionInterface levelCreatorInjection,
            SlotPuzzleGame game,
            LevelDoor levelDoor) {
        this.levelCreatorInjection = levelCreatorInjection;
        this.game = game;
        this.levelDoor = levelDoor;
        initialise();
    }

    private void initialise() {
        createSprites(game.annotationAssetManager);
        getLevelAssets(game.annotationAssetManager);
        initialiseHud();
        initialiseWorld();
    }

    private void createSprites(AnnotationAssetManager annotationAssetManager) {
        reelSprites = new ReelSprites(annotationAssetManager);
    }

    private void getLevelAssets(AnnotationAssetManager annotationAssetManager) {
        tiledMapLevel = annotationAssetManager.get(
                "levels/level " + (this.levelDoor.getId() + 1) + " - 40x40.tmx");
    }

    private void initialiseHud() {
        hud = new Hud(game.batch);
        hud.setLevelName(levelDoor.getLevelName());
        frameRate = new FrameRate();
    }


    private void initialiseWorld() {
        box2dWorld = new World(new Vector2(0, -9.8f), true);
        rayHandler = new RayHandler(box2dWorld);
        RayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.25f, 0.25f, 0.25f, 0.25f);
    }

    public void loadLevel(
            MapTile mapTileLevel,
            LevelCallback stoppedSpinningCallback,
            LevelCallback stoppedFlashingCallback) {
        setUpMapProperties();
        slotReelScrollTexture = createSlotReelScrollTexture();
        createLevelObjects();
        loadLevelDetails(mapTileLevel, stoppedSpinningCallback, stoppedFlashingCallback);
        setUpLevelDetails();
    }

    public Texture getSlotReelScrollTexture() {
        return slotReelScrollTexture;
    }

    public World getBox2dWorld() { return box2dWorld; }

    public AnnotationAssetManager getAnnotationAssetManager() {
        return game.annotationAssetManager;
    }

    public Hud getHud() { return hud; }

    public FrameRate getFrameRate() { return frameRate; }

    public GridSize getLevelGridSize() { return levelGridSize; }

    public Array<AnimatedReel> getAnimatedReels() {
        return animatedReels;
    }

    public Array<ReelTile> getReelTiles() {
        return reelTiles;
    }

    public Array<HoldLightButton> getHoldLightButtons() {
        return holdLightButtons;
    }

    public Array<SlotHandleSprite> getSlotHandles() {
        return slotHandles;
    }

    public FlashSlots getFlashSlots() {
        return flashSlots;
    }

    public HiddenPattern getHiddenPattern() {
        return hiddenPattern;
    }

    public LevelLoader getLevelLoader() {
        return levelLoader;
    }

    public TweenManager getTweenManager() {
        return levelCreatorInjection.getTweenManager();
    }

    public TiledMap getTiledMapLevel() {
        return tiledMapLevel;
    }

    private void setUpLevelDetails() {
        hiddenPattern = levelLoader.getHiddenPattern();
        flashSlots = new FlashSlots(
                game.getTweenManager(),
                levelGridSize,
                reelTiles);
    }

    private void loadLevelDetails(MapTile mapTileLevel,
                                  LevelCallback stoppedSpinningCallback,
                                  LevelCallback stoppedFlashingCallback) {
        levelLoader = createLevelLoader(
                mapTileLevel, stoppedSpinningCallback, stoppedFlashingCallback);
        levelLoader.createAnimatedReelsInLevel(levelGridSize);
    }

    private void setUpMapProperties() {
        MapProperties mapProperties = getMapProperties(tiledMapLevel);
        getLevelGrid(mapProperties);
        String addReel = getStringProperty(mapProperties, SlotPuzzleConstants.ADD_REEL_KEY);

        if (addReel.equals(ReelType.Bomb.name))
            addBombSprite();
    }

    private void createLevelObjects() {
        LevelObjectCreatorEntityHolder levelObjectCreator =
                new LevelObjectCreatorEntityHolder(levelCreatorInjection, box2dWorld, rayHandler);
        Array<RectangleMapObject> extractedLevelRectangleMapObjects =
                extractLevelAssets(tiledMapLevel);
        levelObjectCreator.createLevel(extractedLevelRectangleMapObjects);
        getLevelEntities(levelObjectCreator);
    }

    private MapProperties getMapProperties(TiledMap tiledMapLevel) {
        return tiledMapLevel.getProperties();
     }

    private void getLevelGrid(MapProperties mapProperties) {
        int gridWidth = getIntProperty(mapProperties, SlotPuzzleConstants.GRID_WIDTH_KEY);
        int gridHeight = getIntProperty(mapProperties, SlotPuzzleConstants.GRID_HEIGHT_KEY);
        if (gridWidth == 0 | gridHeight == 0)
            levelGridSize = new GridSize(
                    SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                    SlotPuzzleConstants.GAME_LEVEL_HEIGHT);
        else
            levelGridSize = new GridSize(
                    gridWidth,
                    gridHeight
            );
    }

    private String getStringProperty(MapProperties mapProperties, String key) {
        return mapProperties.get(key, String.class) == null ?
                "" : mapProperties.get(key, String.class);
    }

    private int getIntProperty(MapProperties mapProperties, String key) {
        return mapProperties.get(key, String.class) == null ?
                0 :
                Integer.parseInt(mapProperties.get(key, String.class));
    }

    private Texture createSlotReelScrollTexture() {
        Pixmap slotReelScrollPixmap =
                PixmapProcessors.createPixmapToAnimate(reelSprites.getSprites());
        return new Texture(slotReelScrollPixmap);
    }

    private void addBombSprite() {
        TextureAtlas reelAtlas = game.annotationAssetManager.get(AssetsAnnotation.REELS_EXTENDED);
        reelSprites.addSprite(reelAtlas.createSprite(AssetsAnnotation.BOMB40x40));
    }


    private void getLevelEntities(LevelObjectCreatorEntityHolder levelObjectCreator) {
        animatedReels = levelObjectCreator.getAnimatedReels();
        reelTiles = levelObjectCreator.getReelTiles();
        holdLightButtons = levelObjectCreator.getHoldLightButtons();
        slotHandles = levelObjectCreator.getHandles();
    }

    private Array<RectangleMapObject> extractLevelAssets(TiledMap level) {
        Array<RectangleMapObject> levelRectangleMapObjects =
                getRectangleMapObjectsFromLevel(level);
        MapLevelNameComparator mapLevelNameComparator = new MapLevelNameComparator();
        levelRectangleMapObjects.sort(mapLevelNameComparator);
        return levelRectangleMapObjects;
    }

    private Array<RectangleMapObject> getRectangleMapObjectsFromLevel(TiledMap level) {
        return level.getLayers().get(SlotPuzzleConstants.REEL_OBJECT_LAYER).
                getObjects().getByType(RectangleMapObject.class);
    }

    private LevelLoader createLevelLoader(
        MapTile mapTileLevel,
        LevelCallback stoppedSpinningCallback,
        LevelCallback stoppedFlashingCallback) {
        LevelLoader levelLoader =
                new LevelLoader(
                        game.annotationAssetManager,
                        levelDoor,
                        mapTileLevel,
                        animatedReels);
        levelLoader.setStoppedSpinningCallback(stoppedSpinningCallback);
        levelLoader.setStoppedFlashingCallback(stoppedFlashingCallback);
        return levelLoader;
    }
}
