package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReelHelper;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedFlashingEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import static com.ellzone.slotpuzzle2d.level.LevelCreator.PLAYING_CARD_LEVEL_TYPE;
import static com.ellzone.slotpuzzle2d.level.LevelCreator.MINI_SLOT_MACHINE_TYPE;

import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_HEIGHT;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_WIDTH;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.SLOT_REEL_OBJECT_LAYER;

public class LevelLoader {
    private final AnnotationAssetManager annotationAssetManager;
    private LevelDoor levelDoor;
    private MapTile mapTile;
    private TiledMap tiledMapLevel;
    private AnimatedReelHelper animatedReelHelper;
    private Array<ReelTile> reelTiles;
    private int levelWidth, levelHeight;
 	private Array<Card> cards;
    private Array<Integer> hiddenPlayingCards;
    private LevelCallBack stoppedSpinningCallback, stoppedFlashingCallback;
    private HiddenPattern hiddenPattern;
    private PuzzleGridTypeReelTile puzzleGridTypeReelTile;

    public LevelLoader(AnnotationAssetManager annotationAssetManager, LevelDoor levelDoor, MapTile mapTile, AnimatedReelHelper animatedReelHelper) {
        this.annotationAssetManager = annotationAssetManager;
        this.levelDoor = levelDoor;
        this.mapTile = mapTile;
        this.animatedReelHelper = animatedReelHelper;
        this.reelTiles = animatedReelHelper.getReelTiles();
    }

     public Array<ReelTile> createLevel(int levelWidth, int levelHeight) {
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;
        puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        tiledMapLevel = getLevelAssets(annotationAssetManager);
        if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
            initialiseHiddenPlayingCards();
        else
            initialiseHiddenShape();
        addReelsFromLevel();
        if (!levelDoor.getLevelType().equals(MINI_SLOT_MACHINE_TYPE)) {
            reelTiles = checkLevel(reelTiles, levelWidth, levelHeight);
            reelTiles = adjustForAnyLonelyReels(reelTiles, levelWidth, levelHeight);
        }
        return reelTiles;
    }

    public void setStoppedSpinningCallback(LevelCallBack callback) {
        this.stoppedSpinningCallback = callback;
    }

    public void setStoppedFlashingCallback(LevelCallBack callback) {
        this.stoppedFlashingCallback = callback;
    }

    private TiledMap getLevelAssets(AnnotationAssetManager annotationAssetManager) {
        return annotationAssetManager.get("levels/level " + (this.levelDoor.getId() + 1) + " - 40x40.tmx");
    }

    private void addReelsFromLevel() {
        int index = 0;
        for (MapObject mapObject : tiledMapLevel.getLayers().get(SLOT_REEL_OBJECT_LAYER).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            int c = (int) (mapRectangle.getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
            int r = (int) (mapRectangle.getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
            r = GAME_LEVEL_HEIGHT - r;
            if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
                addReel(mapRectangle, index);
                index++;
            } else {
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to grid r="+r+" c="+c+". There it won't be added to the level! Sort it out in a level editor.");
            }
        }
    }

    private void initialiseHiddenPlayingCards() {
        TextureAtlas carddeckAtlas = annotationAssetManager.get(AssetsAnnotation.CARDDECK);
        hiddenPattern = new HiddenPlayingCard(tiledMapLevel, carddeckAtlas);
        HiddenPlayingCard hiddenPlayingCard = (HiddenPlayingCard) hiddenPattern;
        cards = hiddenPlayingCard.getCards();
        hiddenPlayingCards = hiddenPlayingCard.getHiddenPlayingCards();
    }

    private void initialiseHiddenShape() {
        hiddenPattern = new HiddenShape(tiledMapLevel);
    }

    private void addReel(Rectangle mapRectangle, int index) {
        ReelTile reelTile = reelTiles.get(index);
        reelTile.setIndex(index);
        reelTile.setX(mapRectangle.getX());
        reelTile.setY(mapRectangle.getY());
        reelTile.setDestinationX(mapRectangle.getX());
        reelTile.setDestinationY(mapRectangle.getY());
        reelTile.setSx(0);

        int startReel = Random.getInstance().nextInt(reelTile.getScrollTextureHeight());
        startReel = (startReel / ((int) reelTile.getTileHeight())) * (int)reelTile.getTileHeight();
        reelTile.setSy(startReel);
        reelTile.addListener(new ReelTileListener() {
                                 @Override
                                 public void actionPerformed(ReelTileEvent event, ReelTile source) {
                                     if (event instanceof ReelStoppedSpinningEvent)
                                         processReelHasStoppedSpinning(source);

                                     if (event instanceof ReelStoppedFlashingEvent)
                                         processReelHasStoppedFlashing(source);
                                 }
                             }
        );
        reelTile.unDeleteReelTile();
        reelTile.startSpinning();
        animatedReelHelper.getAnimatedReels().get(reelTile.getIndex()).reinitialise();
    }

    private void processReelHasStoppedSpinning(ReelTile source) {
        stoppedSpinningCallback.onEvent(source);
    }

    private void processReelHasStoppedFlashing(ReelTile source) {
        stoppedFlashingCallback.onEvent(source);
    }

    private Array<ReelTile> checkLevel(Array<ReelTile> reelLevel, int levelWidth, int levelHeight) {
        return puzzleGridTypeReelTile.checkGrid(reelLevel, levelWidth, levelHeight);
    }

    private Array<ReelTile> adjustForAnyLonelyReels(Array<ReelTile> levelReel, int levelWidth, int levelHeight) {
        return puzzleGridTypeReelTile.adjustForAnyLonelyReels(levelReel, levelWidth, levelHeight);
    }

    public TupleValueIndex[][] populateMatchGrid(Array<ReelTile> reelLevel, int levelWidth, int levelHeight) {
        return puzzleGridTypeReelTile.populateMatchGrid(reelLevel, levelWidth, levelHeight);
    }

    public Array<ReelTile> getReelTiles() {
        return reelTiles;
    }

    public Array<Card> getCards() {
        return cards;
    }

    public HiddenPattern getHiddenPattern() {return hiddenPattern; }

    public class LevelLoaderException extends GdxRuntimeException {
        public LevelLoaderException(String message) {
            super(message);
        }
    }
}
