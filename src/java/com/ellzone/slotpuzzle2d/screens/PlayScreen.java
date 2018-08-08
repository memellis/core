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

package com.ellzone.slotpuzzle2d.screens;

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.Card;
import com.ellzone.slotpuzzle2d.level.HiddenPlayingCard;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.LevelPopUp;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReelHelper;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedFlashingEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.Reels;
import com.ellzone.slotpuzzle2d.sprites.Score;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Sine;

import static com.ellzone.slotpuzzle2d.scene.Hud.addScore;

public class PlayScreen implements Screen {
    public static final int TILE_WIDTH = 40;
	public static final int TILE_HEIGHT = 40;
	public static final int GAME_LEVEL_WIDTH = 11;
	public static final int GAME_LEVEL_HEIGHT = 8;
	public static final int SLOT_REEL_OBJECT_LAYER = 2;
	private static final String HIDDEN_PATTERN_LAYER_NAME = "Hidden Pattern Object";
	private static final int HIDDEN_PATTERN_LAYER = 0;
	public static final float PUZZLE_GRID_START_X = 160.0f;
	public static final float PUZZLE_GRID_START_Y = 40.0f;
    private static final String REELS_LAYER_NAME = "Reels";
    private static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    private static final String HIDDEN_PATTERN_LEVEL_TYPE = "HiddenPattern";
	private static final String SLOTPUZZLE_SCREEN = "PlayScreen";
	private static final String LEVEL_TIP_DESC =  "Reveal the hidden pattern to complete the level.";
	private static final String LEVEL_LOST_DESC =  "Sorry you lost that level. Touch/Press to restart the level.";
	private static final String LEVEL_WON_DESC =  "Well done you've won that level. Touch/Press to start the nextlevel.";

	public enum PlayStates {INITIALISING,
                            INTRO_SEQUENCE,
                            INTRO_POPUP,
                            INTRO_SPINNING,
                            HIT_SINK_BOTTOM,
                            INTRO_FLASHING,
                            CREATED_REELS_HAVE_FALLEN,
                            PLAYING,
                            LEVEL_TIMED_OUT,
                            LEVEL_LOST,
                            WON_LEVEL,
                            RESTARTING_LEVEL,
                            REELS_SPINNING,
                            REELS_FLASHING}
	private PlayStates playState;
	private SlotPuzzle game;
	private final OrthographicCamera camera = new OrthographicCamera();
	private Viewport viewport;
	private Stage stage;
    private float sW, sH;
 	private final TweenManager tweenManager = new TweenManager();
    private Timeline reelFlashSeq;
    private TextureAtlas tilesAtlas;
    private TextureAtlas carddeckAtlas;
 	private Sound chaChingSound, pullLeverSound, reelSpinningSound, reelStoppedSound;
    private boolean isLoaded = false;
    private Array<ReelTile> reelTiles;
	private AnimatedReelHelper animatedReelHelper;
	private int reelsSpinning;
	private TiledMap level;
	private Random random;
	private OrthogonalTiledMapRenderer renderer;
	private boolean gameOver = false;
	private boolean inRestartLevel = false;
	private boolean win = false;
	private int touchX, touchY;
	private boolean displaySpinHelp;
	private int displaySpinHelpSprite;
	private Sprite[] sprites;
    private Hud hud;
    private Array<Score> scores;
    private BitmapFont font;
	private LevelPopUp levelPopUp, levelLostPopUp, levelWonPopUp;
	private Array<Sprite> popUpSprites, levelLostSprites, levelWonSprites;
    private LevelDoor levelDoor;
	private Array<Integer> hiddenPlayingCards;
	private Array<Card> cards;
	private MapTile mapTile;
	private int mapWidth;
    private int mapHeight;
    private boolean show = false;

	public PlayScreen(SlotPuzzle game, LevelDoor levelDoor, MapTile mapTile) {
		this.game = game;
		this.levelDoor = levelDoor;
		this.mapTile = mapTile;
		createPlayScreen();
	}

	private void createPlayScreen() {
		playState = PlayStates.INITIALISING;
		initialiseScreen();
		initialiseTweenEngine();
		getAssets(game.annotationAssetManager);
		createSprites();
		initialisePlayScreen();
		createLevels();
        getMapProperties(this.level);
        hud = new Hud(game.batch);
		hud.setLevelName(levelDoor.getLevelName());
		createReelIntroSequence();
   	}

	private void initialiseScreen() {
		viewport = new FitViewport(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
	}

	private void initialiseTweenEngine() {
		SlotPuzzleTween.setWaypointsLimit(10);
		SlotPuzzleTween.setCombinedAttributesLimit(3);
		SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
		SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
		SlotPuzzleTween.registerAccessor(Score.class, new ScoreAccessor());
	}

	private void getAssets(AnnotationAssetManager annotationAssetManager) {
		getAtlasAssets(annotationAssetManager);
		getSoundAssets(annotationAssetManager);
		getLevelAssets(annotationAssetManager);
	}

	private void getLevelAssets(AnnotationAssetManager annotationAssetManager) {
		level = annotationAssetManager.get("levels/level " + (this.levelDoor.getId() + 1) + " - 40x40.tmx");
	}

	private void getSoundAssets(AnnotationAssetManager annotationAssetManager) {
		chaChingSound = annotationAssetManager.get(AssetsAnnotation.SOUND_CHA_CHING);
		pullLeverSound = annotationAssetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
		reelSpinningSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
		reelStoppedSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
	}

	private void getAtlasAssets(AnnotationAssetManager annotationAssetManager) {
        tilesAtlas = annotationAssetManager.get(AssetsAnnotation.TILES);
		carddeckAtlas = annotationAssetManager.get(AssetsAnnotation.CARDDECK);
	}

	private void createSprites() {
        Reels reelsSprites = new Reels(game.annotationAssetManager);
		sprites = reelsSprites.getReels();

		setUpPopSprites();
		setUpLevelLostSprtes();
		setUpLevelWonSprites();
	}

	private void setUpPopSprites() {
		popUpSprites = new Array<>();
		popUpSprites.add(tilesAtlas.createSprite(AssetsAnnotation.GAME_POPUP));
		popUpSprites.add(tilesAtlas.createSprite(AssetsAnnotation.LEVEL_SPRITE));
		setPopUpSpritePositions();
	}

	private void setUpLevelLostSprtes() {
		levelLostSprites = new Array<>();
		levelLostSprites.add(tilesAtlas.createSprite(AssetsAnnotation.GAME_POPUP));
		levelLostSprites.add(tilesAtlas.createSprite(AssetsAnnotation.LEVEL_SPRITE));
		levelLostSprites.add(tilesAtlas.createSprite(AssetsAnnotation.LEVEL_SPRITE));
		levelLostSprites.add(tilesAtlas.createSprite(AssetsAnnotation.OVER));
		setLevelLostSpritePositions();
	}

	private void setUpLevelWonSprites() {
		levelWonSprites = new Array<>();
		levelWonSprites.add(tilesAtlas.createSprite(AssetsAnnotation.GAME_POPUP));
		levelWonSprites.add(tilesAtlas.createSprite(AssetsAnnotation.LEVEL_SPRITE));
		levelWonSprites.add(tilesAtlas.createSprite(AssetsAnnotation.LEVEL_SPRITE));
		levelWonSprites.add(tilesAtlas.createSprite(AssetsAnnotation.COMPLETE));
		setLevelWonSpritePositions();
	}

	private void setPopUpSpritePositions() {
	    popUpSprites.get(0).setPosition(sW/ 2 - popUpSprites.get(0).getWidth() / 2, sH / 2 - popUpSprites.get(0).getHeight() /2);
		popUpSprites.get(1).setPosition(-200, sH / 2 - popUpSprites.get(1).getHeight() /2);
	}

	private void setLevelLostSpritePositions() {
	    levelLostSprites.get(0).setPosition(sW / 2 - levelLostSprites.get(0).getWidth() / 2, sH / 2 - levelLostSprites.get(0).getHeight() /2);
	    levelLostSprites.get(1).setPosition(-200, sH / 2 - levelLostSprites.get(1).getHeight() / 2);
	    levelLostSprites.get(2).setPosition(-200, sH / 2 - levelLostSprites.get(2).getHeight() / 2 + 40);
	    levelLostSprites.get(3).setPosition(200 + sW, sH / 2 - levelLostSprites.get(3).getHeight() / 2 + 40);
	}

	private void setLevelWonSpritePositions() {
	    levelWonSprites.get(0).setPosition(sW / 2 - levelWonSprites.get(0).getWidth() / 2, sH / 2 - levelWonSprites.get(0).getHeight() /2);
	    levelWonSprites.get(1).setPosition(-200, sH / 2 - levelWonSprites.get(1).getHeight() / 2);
	    levelWonSprites.get(2).setPosition(-200, sH / 2 - levelWonSprites.get(2).getHeight() / 2 + 40);
	    levelWonSprites.get(3).setPosition(200 + sW, sH / 2 - levelWonSprites.get(3).getHeight() / 2 + 40);
	}

	private void initialisePlayScreen() {
	    random = new Random();
	    renderer = new OrthogonalTiledMapRenderer(level);
	    animatedReelHelper = new AnimatedReelHelper(game.annotationAssetManager,
                                                    tweenManager,
                                                    level.getLayers().get(REELS_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).size);
	    reelTiles = animatedReelHelper.getReelTiles();
	    displaySpinHelp = false;
	    scores = new Array<>();
	    font = new BitmapFont();
	    sW = SlotPuzzleConstants.V_WIDTH;
	    sH = SlotPuzzleConstants.V_HEIGHT;
        createPopUps();
	}

	private void createPopUps() {
        BitmapFont currentLevelFont = new BitmapFont();
	    currentLevelFont.getData().scale(1.5f);
	    levelPopUp = new LevelPopUp(game.batch, tweenManager, popUpSprites, currentLevelFont, levelDoor.getLevelName(), LEVEL_TIP_DESC);
	    levelLostPopUp = new LevelPopUp(game.batch, tweenManager, levelLostSprites, currentLevelFont, levelDoor.getLevelName(), LEVEL_LOST_DESC);
	    levelWonPopUp = new LevelPopUp(game.batch, tweenManager, levelWonSprites, currentLevelFont, levelDoor.getLevelName(), LEVEL_WON_DESC);
	}

    private void getMapProperties(TiledMap level) {
        MapProperties mapProperties = level.getProperties();
        mapWidth = mapProperties.get("width", Integer.class);
        mapHeight = mapProperties.get("height", Integer.class);
    }

    private void createLevels() {
 		if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
 			initialiseHiddenPlayingCards();
		addReelsFromLevel();
		reelsSpinning = reelTiles.size - 1;
		reelTiles = checkLevel(reelTiles);
		reelTiles = adjustForAnyLonelyReels(reelTiles);
	}

	private void addReelsFromLevel() {
        int index = 0;
        for (MapObject mapObject : level.getLayers().get(SLOT_REEL_OBJECT_LAYER).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
			int c = (int) (mapRectangle.getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
			int r = (int) (mapRectangle.getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
			r = PlayScreen.GAME_LEVEL_HEIGHT - r;
			if ((r >= 0) & (r <= PlayScreen.GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= PlayScreen.GAME_LEVEL_WIDTH)) {
				addReel(mapRectangle, index);
				index++;
			} else {
				Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to grid r="+r+" c="+c+". There it won't be added to the level! Sort it out in a level editor.");
			}
		}
	}

	private void initialiseHiddenPlayingCards() {
		HiddenPlayingCard hiddenPlayingCard = new HiddenPlayingCard(level, carddeckAtlas);
		cards = hiddenPlayingCard.getCards();
		hiddenPlayingCards = hiddenPlayingCard.getHiddenPlayingCards();
	}

	private void addReel(Rectangle mapRectangle, int index) {
        ReelTile reelTile = reelTiles.get(index);
        reelTile.setX(mapRectangle.getX());
		reelTile.setY(mapRectangle.getY());
		reelTile.setDestinationX(mapRectangle.getX());
		reelTile.setDestinationY(mapRectangle.getY());
		reelTile.setSx(0);

		int startReel = random.nextInt(reelTile.getScrollTextureHeight());
		startReel = (startReel / ((int) reelTile.getTileHeight())) * (int)reelTile.getTileHeight();
		reelTile.setSy(startReel);
		reelTile.addListener(new ReelTileListener() {
			@Override
			public void actionPerformed(ReelTileEvent event, ReelTile source) {
					if (event instanceof ReelStoppedSpinningEvent)
                        processReelHasStoppedSpinning();

					if (event instanceof ReelStoppedFlashingEvent)
                        processReelHasStoppedFlashing(source);
			    }
			}
		);
	}

    private void processReelHasStoppedFlashing(ReelTile source) {
        if (testForAnyLonelyReels(reelTiles)) {
            win = false;
            if (Hud.getLives() > 0) {
                playState = PlayStates.LEVEL_LOST;
                setLevelLostSpritePositions();
                levelLostPopUp.showLevelPopUp(null);
            } else {
                gameOver = true;
            }
        }
        reelScoreAnimation(source);
        deleteReelAnimation(source);
    }

    private void processReelHasStoppedSpinning() {
        reelStoppedSound.play();
        reelsSpinning--;
        if (playState == PlayStates.PLAYING) {
            if (reelsSpinning <= -1) {
                if (levelDoor.getLevelType().equals(HIDDEN_PATTERN_LEVEL_TYPE)) {
                    if (testForHiddenPatternRevealed(reelTiles))
                        iWonTheLevel();
                } else {
                    if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE)) {
                        if (testForHiddenPlayingCardsRevealed(reelTiles))
                            iWonTheLevel();
                    }
                }
            }
        }
    }

	private void createReelIntroSequence() {
		playState = PlayStates.INTRO_SEQUENCE;
		Timeline introSequence = Timeline.createParallel();
		for(int i=0; i < reelTiles.size; i++) {
			introSequence = introSequence
					      .push(buildSequence(reelTiles.get(i), i, random.nextFloat() * 3.0f, random.nextFloat() * 3.0f));
		}
		introSequence.pushPause(0.3f)
				     .setCallback(introSequenceCallback)
				     .setCallbackTriggers(TweenCallback.END)
				     .start(tweenManager);
	}

	private Timeline buildSequence(Sprite target, int id, float delay1, float delay2) {
        Vector2 targetXY = getRandomCorner();
        return Timeline.createSequence()
			.push(SlotPuzzleTween.set(target, SpriteAccessor.POS_XY).target(targetXY.x, targetXY.y))
			.push(SlotPuzzleTween.set(target, SpriteAccessor.SCALE_XY).target(20, 20))
			.push(SlotPuzzleTween.set(target, SpriteAccessor.ROTATION).target(0))
			.push(SlotPuzzleTween.set(target, SpriteAccessor.OPACITY).target(0))
			.pushPause(delay1)
			.beginParallel()
				.push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
				.push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
			.end()
			.pushPause(-0.5f)
			.push(SlotPuzzleTween.to(target, SpriteAccessor.POS_XY, 0.8f).target(reelTiles.get(id).getX(), reelTiles.get(id).getY()).ease(Back.OUT))
			.push(SlotPuzzleTween.to(target, SpriteAccessor.ROTATION, 0.8f).target(360).ease(Cubic.INOUT))
			.pushPause(delay2)
			.beginParallel()
				.push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 0.3f).target(3, 3).ease(Quad.IN))
				.push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 0.3f).target(0).ease(Quad.IN))
			.end()
			.pushPause(-0.5f)
			.beginParallel()
			    .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
			    .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1.0f, 1.0f).ease(Quart.INOUT))
		    .end();
	}

    private Vector2 getRandomCorner() {
        int randomCorner = random.nextInt(4);
        switch (randomCorner) {
            case 0:
                return new Vector2(-1 * random.nextFloat(), -1 * random.nextFloat());
            case 1:
                return new Vector2(-1 * random.nextFloat(), SlotPuzzleConstants.V_WIDTH + random.nextFloat());
            case 2:
                return new Vector2(SlotPuzzleConstants.V_HEIGHT / 2 + random.nextFloat(), -1 * random.nextFloat());
            case 3:
                return new Vector2(SlotPuzzleConstants.V_HEIGHT + random.nextFloat(), SlotPuzzleConstants.V_WIDTH + random.nextFloat());
            default:
                return new Vector2(-0.5f, -0.5f);
        }
    }

    private TweenCallback introSequenceCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			delegateIntroSequenceCallback(type);
		}
	};

	private void delegateIntroSequenceCallback(int type) {
		switch (type) {
		    case TweenCallback.END:
	        	playState = PlayStates.INTRO_POPUP;
	        	setPopUpSpritePositions();
	        	levelPopUp.showLevelPopUp(null);
		        break;
		}
	}

    private Array<ReelTile> checkLevel(Array<ReelTile> reelLevel) {
        TupleValueIndex[][] grid = populateMatchGrid(reelLevel);
        int arraySizeR = grid.length;
        int arraySizeC = grid[0].length;

        for(int r = 0; r < arraySizeR; r++) {
            for(int c = 0; c < arraySizeC; c++) {
                if(grid[r][c] == null) {
                    Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "Found null grid tile. r=" + r + " c= " + c + ". I will therefore create a deleted entry for the tile.");
                    throw new GdxRuntimeException("Level incorrect. Found null grid tile. r=" + r + " c= " + c);
               }
            }
        }
        return reelLevel;
    }

	private boolean testForHiddenPatternRevealed(Array<ReelTile> levelReel) {
		TupleValueIndex[][] matchGrid = flashSlots(levelReel);
		return hiddenPatternRevealed(matchGrid);
	}

	private boolean testForHiddenPlayingCardsRevealed(Array<ReelTile> levelReel) {
		TupleValueIndex[][] matchGrid = flashSlots(levelReel);
		return hiddenPlayingCardsRevealed(matchGrid);
	}

    private boolean testForAnyLonelyReels(Array<ReelTile> levelReel) {
		PuzzleGridType puzzleGrid = new PuzzleGridType();
		TupleValueIndex[][] grid = populateMatchGrid(levelReel);
		return puzzleGrid.anyLonelyTiles(grid);
	}

    private Array<ReelTile> adjustForAnyLonelyReels(Array<ReelTile> levelReel) {
		PuzzleGridType puzzleGrid = new PuzzleGridType();
		TupleValueIndex[][] grid = populateMatchGrid(levelReel);
		Array<TupleValueIndex> lonelyTiles = puzzleGrid.getLonelyTiles(grid);
		for (TupleValueIndex lonelyTile : lonelyTiles) {
			if (lonelyTile.r == 0) {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r+1][lonelyTile.c].index).getEndReel());
			} else if (lonelyTile.c == 0) {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c+1].index).getEndReel());
			} else if (lonelyTile.r == GAME_LEVEL_HEIGHT) {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r-1][lonelyTile.c].index).getEndReel());
			} else if (lonelyTile.c == GAME_LEVEL_WIDTH) {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c-1].index).getEndReel());
			} else {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r+1][lonelyTile.c].index).getEndReel());
			}
		}
		return levelReel;
	}

	private TupleValueIndex[][] populateMatchGrid(Array<ReelTile> reelLevel) {
		TupleValueIndex[][] matchGrid = new TupleValueIndex[9][12];
		int r, c;
		for (int i = 0; i < reelLevel.size; i++) {
			c = (int) (reelLevel.get(i).getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
			r = (int) (reelLevel.get(i).getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
			r = GAME_LEVEL_HEIGHT - r;
			if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
				if (reelLevel.get(i).isReelTileDeleted()) {
					matchGrid[r][c] = new TupleValueIndex(r, c, i, -1);
				} else {
					matchGrid[r][c] = new TupleValueIndex(r, c, i, reelLevel.get(i).getEndReel());
				}
			} else {
				Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't know how to deal with r="+r+" c="+c);
			}
		}
		return matchGrid;
	}

	private void deleteReelAnimation(ReelTile source) {
		Timeline.createSequence()
		    .beginParallel()
			    .push(SlotPuzzleTween.to(source, SpriteAccessor.SCALE_XY, 0.3f).target(6, 6).ease(Quad.IN))
			    .push(SlotPuzzleTween.to(source, SpriteAccessor.OPACITY, 0.3f).target(0).ease(Quad.IN))
		    .end()
		    .setUserData(source)
		    .setCallback(deleteReelCallback)
		    .setCallbackTriggers(TweenCallback.COMPLETE)
			.start(tweenManager);
	}

	private TweenCallback deleteReelCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			switch (type) {
				case TweenCallback.COMPLETE:
                    proceesDeleteReel(source);
			}
		}
	};

    private void proceesDeleteReel(BaseTween<?> source) {
        ReelTile reel = (ReelTile) source.getUserData();
        addScore((reel.getEndReel() + 1) * reel.getScore());
        reelStoppedSound.play();
        chaChingSound.play();
        reel.deleteReelTile();
        if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE)) {
            testPlayingCardLevelWon();
        } else {
            if (levelDoor.getLevelType().equals(HIDDEN_PATTERN_LEVEL_TYPE)) {
                testForHiddenPlatternLevelWon();
            }
        }
    }

    private void reelScoreAnimation(ReelTile source) {
		Score score = new Score(source.getX(), source.getY(), (source.getEndReel() + 1) * source.getScore());
		scores.add(score);
		Timeline.createSequence()
			.beginParallel()
				.push(SlotPuzzleTween.to(score, ScoreAccessor.POS_XY, 2.0f).targetRelative(random.nextInt(20), random.nextInt(160)).ease(Quad.IN))
				.push(SlotPuzzleTween.to(score, ScoreAccessor.SCALE_XY, 2.0f).target(2.0f, 2.0f).ease(Quad.IN))
			.end()
			.setUserData(score)
			.setCallback(deleteScoreCallback)
			.setCallbackTriggers(TweenCallback.COMPLETE)
			.start(tweenManager);
	}

	private TweenCallback deleteScoreCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			switch (type) {
			case TweenCallback.COMPLETE:
				Score score = (Score) source.getUserData();
				scores.removeValue(score, false);
			}
		}
	};

	private void initialiseReelFlash(ReelTile reel, float pushPause) {
		Array<Object> userData = new Array<>();
		reel.setFlashTween(true);
		reelFlashSeq = Timeline.createSequence();
        reelFlashSeq = reelFlashSeq.pushPause(pushPause);

		Color fromColor = new Color(Color.WHITE);
		fromColor.a = 1;
		Color toColor = new Color(Color.RED);
		toColor.a = 1;

		userData.add(reel);
		userData.add(reelFlashSeq);

        setUpFlashSequence(reel, userData, fromColor, toColor);
	}

    private void setUpFlashSequence(ReelTile reel, Array<Object> userData, Color fromColor, Color toColor) {
        reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.set(reel, ReelAccessor.FLASH_TINT)
                   .target(fromColor.r, fromColor.g, fromColor.b)
                   .ease(Sine.IN));
        reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.FLASH_TINT, 0.2f)
                   .target(toColor.r, toColor.g, toColor.b)
                   .ease(Sine.OUT)
                   .repeatYoyo(17, 0));

        reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.set(reel, ReelAccessor.FLASH_TINT)
                           .target(fromColor.r, fromColor.g, fromColor.b)
                           .ease(Sine.IN));
        reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.FLASH_TINT, 0.05f)
                           .target(toColor.r, toColor.g, toColor.b)
                           .ease(Sine.OUT)
                           .repeatYoyo(25, 0))
                           .setCallback(reelFlashCallback)
                           .setCallbackTriggers(TweenCallback.COMPLETE)
                           .setUserData(userData)
                           .start(tweenManager);
    }

    private TweenCallback reelFlashCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			switch (type) {
				case TweenCallback.COMPLETE:
					delegateReelFlashCallback(source);
			}
		}
	};

	private void delegateReelFlashCallback(BaseTween<?> source) {
		@SuppressWarnings("unchecked")
		Array<Object> userData = (Array<Object>) source.getUserData();
		ReelTile reel = (ReelTile) userData.get(0);
		Timeline reelFlashSeq = (Timeline) userData.get(1);
		reelFlashSeq.kill();
		if (reel.getFlashTween()) {
			reel.setFlashOff();
			reel.setFlashTween(false);
			reel.processEvent(new ReelStoppedFlashingEvent());
		}
	}

	public void handleInput() {
		if (Gdx.input.justTouched()) {
			touchX = Gdx.input.getX();
			touchY = Gdx.input.getY();
            Vector3 unprojTouch = new Vector3(touchX, touchY, 0);
            viewport.unproject(unprojTouch);
			switch (playState) {
				case INITIALISING:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Initialising");
					break;
				case INTRO_SEQUENCE:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Intro Sequence");
					break;
				case INTRO_POPUP:
                    if (isOver(popUpSprites.get(0), unprojTouch.x, unprojTouch.y)) {
						levelPopUp.hideLevelPopUp(hideLevelPopUpCallback);
					}
					break;
				case INTRO_SPINNING:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Intro Spinning");
					break;
				case INTRO_FLASHING:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Intro Flashing");
					break;
				case PLAYING:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Play");
					processIsTileClicked();
					break;
				case LEVEL_LOST:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Lost Level");
					if (isOver(levelLostSprites.get(0), unprojTouch.x, unprojTouch.y)) {
					    levelLostPopUp.hideLevelPopUp(levelOverCallback);
					}
					break;
				case WON_LEVEL:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Won Level");
					if(isOver(levelWonSprites.get(0), unprojTouch.x, unprojTouch.y)) {
						levelWonPopUp.hideLevelPopUp(levelWonCallback);
					}
					break;
				case RESTARTING_LEVEL:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Restarting Level");
					break;
				default: break;
			}
		}
	}

	private boolean isOver(Sprite sprite, float x, float y) {
        return sprite.getX() <= x && x <= sprite.getX() + sprite.getWidth()
			&& sprite.getY() <= y && y <= sprite.getY() + sprite.getHeight();
	}

	private void testPlayingCardLevelWon() {
		PuzzleGridType puzzleGrid = new PuzzleGridType();
		TupleValueIndex[][] matchGrid = populateMatchGrid(reelTiles);
		puzzleGrid.matchGridSlots(matchGrid);
		if (hiddenPlayingCardsRevealed(matchGrid)) {
			iWonTheLevel();
		}
	}

	private void testForHiddenPlatternLevelWon() {
		PuzzleGridType puzzleGrid = new PuzzleGridType();
		TupleValueIndex[][] matchGrid = populateMatchGrid(reelTiles);
		puzzleGrid.matchGridSlots(matchGrid);
		if (hiddenPatternRevealed(matchGrid)) {
			iWonTheLevel();
		}
	}

	private void iWonTheLevel() {
		gameOver = true;
	    win = true;
	    playState = PlayStates.WON_LEVEL;
		mapTile.getLevel().setLevelCompleted();
		mapTile.getLevel().setScore(Hud.getScore());
	    setLevelWonSpritePositions();
	    levelWonPopUp.showLevelPopUp(null);
	}

	private TweenCallback hideLevelPopUpCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			switch (type) {
			    case TweenCallback.END:
			    	playState = PlayStates.PLAYING;
			    	hud.resetWorldTime(300);
			    	hud.startWorldTimer();
			    	testForHiddenPatternRevealed(reelTiles);
			}
		}
	};

	private TweenCallback levelWonCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			dispose();
			((WorldScreen)game.getWorldScreen()).worldScreenCallBack();
			game.setScreen(game.getWorldScreen());
		}
	};

    private void processIsTileClicked() {
		touchX = Gdx.input.getX();
		touchY = Gdx.input.getY();
		Vector2 newPoints = new Vector2(touchX, touchY);
		newPoints = viewport.unproject(newPoints);
		int c = (int) (newPoints.x - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
		int r = (int) (newPoints.y - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
		r = GAME_LEVEL_HEIGHT - r;
		if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
			TupleValueIndex[][] grid = populateMatchGrid(reelTiles);
			ReelTile reelTile = reelTiles.get(grid[r][c].index);
            AnimatedReel animatedReel = animatedReelHelper.getAnimatedReels().get(grid[r][c].index);
			if (!reelTile.isReelTileDeleted()) {
				if (reelTile.isSpinning()) {
                    if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                        processReelTouchedWhileSpinning(reelTile);
					}
				} else {
					if (!reelTile.getFlashTween()) {
                        startReelSpinning(reelTile, animatedReel);
					}
				}
			}
		} else {
			Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
		}
	}

    private void startReelSpinning(ReelTile reel, AnimatedReel animatedReel) {
        reel.setEndReel(random.nextInt(sprites.length - 1));
        reel.startSpinning();
        reelsSpinning++;
        reel.setSy(0);
        animatedReel.reinitialise();
        addScore(-1);
        pullLeverSound.play();
    }

    private void processReelTouchedWhileSpinning(ReelTile reel) {
        reel.setEndReel(reel.getCurrentReel());
        displaySpinHelp = true;
        displaySpinHelpSprite = reel.getCurrentReel();
        addScore(-1);
        pullLeverSound.play();
        reelSpinningSound.play();
    }

    private boolean hiddenPatternRevealed(TupleValueIndex[][] grid) {
		boolean hiddenPattern = true;
		for (MapObject mapObject : level.getLayers().get(HIDDEN_PATTERN_LAYER).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
			int c = (int) (mapRectangle.getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
			int r = (int) (mapRectangle.getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
			r = GAME_LEVEL_HEIGHT - r;
			if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
				if (grid[r][c] != null) {
					if (!reelTiles.get(grid[r][c].getIndex()).isReelTileDeleted())
						hiddenPattern = false;
				}
			} else {
				Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
			}
		}
		return hiddenPattern;
	}

	private boolean hiddenPlayingCardsRevealed(TupleValueIndex[][] grid) {
		boolean hiddenPlayingCardsRevealed = true;
		for (Integer hiddenPlayingCard : hiddenPlayingCards) {
		    MapObject mapObject = level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).get(hiddenPlayingCard);
			Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
			for (int ro = (int) (mapRectangle.getX()); ro < (int) (mapRectangle.getX() + mapRectangle.getWidth()); ro += PlayScreen.TILE_WIDTH) {
			    for (int co = (int) (mapRectangle.getY()) ; co < (int) (mapRectangle.getY() + mapRectangle.getHeight()); co += PlayScreen.TILE_HEIGHT) {
					int c = (int) (ro - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
					int r = (int) (co - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
					r = GAME_LEVEL_HEIGHT - r;
					if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
						if (grid[r][c] != null) {
							if (!reelTiles.get(grid[r][c].getIndex()).isReelTileDeleted()) {
								hiddenPlayingCardsRevealed = false;
							}
						}
					} else {
						Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
					}
			    }
			}
		}
		return hiddenPlayingCardsRevealed;
	}

	private ReelTileGridValue[][] flashSlots(Array<ReelTile> reelTiles) {
		PuzzleGridTypeReelTile puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
		ReelTileGridValue[][] puzzleGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles,  mapWidth, mapHeight);

		Array<ReelTileGridValue> matchedSlots = puzzleGridTypeReelTile.matchGridSlots(puzzleGrid);
		Array<ReelTileGridValue> duplicateMatchedSlots = PuzzleGridTypeReelTile.findDuplicateMatches(matchedSlots);

		matchedSlots = PuzzleGridTypeReelTile.adjustMatchSlotDuplicates(matchedSlots, duplicateMatchedSlots);
		for (TupleValueIndex matchedSlot : matchedSlots) {
			reelTiles.get(matchedSlot.index).setScore(matchedSlot.value);
		}
		flashMatchedSlots(matchedSlots, puzzleGridTypeReelTile);
		return puzzleGrid;
	}

	private void flashMatchedSlotsBatch(Array<ReelTileGridValue> matchedSlots, float pushPause) {
		int index;
		for (int i = 0; i < matchedSlots.size; i++) {
			index = matchedSlots.get(i).getIndex();
			if (index  >= 0) {
				ReelTile reel = reelTiles.get(index);
				if (!reel.getFlashTween()) {
					reel.setFlashMode(true);
					Color flashColor = new Color(Color.WHITE);
					reel.setFlashColor(flashColor);
					initialiseReelFlash(reel, pushPause);
				}
			}
		}
	}

	private void flashMatchedSlots(Array<ReelTileGridValue> matchedSlots, PuzzleGridTypeReelTile puzzleGridTypeReelTile) {
		int matchSlotIndex, batchIndex, batchPosition;
		Array<ReelTileGridValue> matchSlotsBatch = new Array<>();
		float pushPause = 0.0f;
		matchSlotIndex = 0;
		while (matchedSlots.size > 0) {
			batchIndex = matchSlotIndex;
			for (int batchCount = batchIndex; batchCount < batchIndex+3; batchCount++) {
				if (batchCount < matchedSlots.size) {
					batchPosition = matchSlotsBatch.size;
					matchSlotsBatch = puzzleGridTypeReelTile.depthFirstSearchAddToMatchSlotBatch(matchedSlots.get(0), matchSlotsBatch);

					for (int deleteIndex=batchPosition; deleteIndex<matchSlotsBatch.size; deleteIndex++) {
						matchedSlots.removeValue(matchSlotsBatch.get(deleteIndex), true);
					}
				}
			}
			flashMatchedSlotsBatch(matchSlotsBatch, pushPause);
			pushPause += 2.0f;
			matchSlotsBatch.clear();
		}
	}

	private TweenCallback levelOverCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			switch (type) {
				case TweenCallback.END:
					delegateLevelOverCallback();
			}
		}
	};

	private void delegateLevelOverCallback() {
		tweenManager.killAll();
		Hud.resetScore();
		Hud.loseLife();
		hud.resetWorldTime(300);
		renderer = new OrthogonalTiledMapRenderer(level);
		displaySpinHelp = false;
		inRestartLevel = false;
		createLevels();
		createReelIntroSequence();
	}

    private void update(float delta) {
		tweenManager.update(delta);
		renderer.setView(camera);
		hud.update(delta);
		if (hud.getWorldTime() == 0) {
			if ((Hud.getLives() > 0) & (!inRestartLevel)) {
				inRestartLevel = true;
				playState = PlayStates.LEVEL_LOST;
				setLevelLostSpritePositions();
				levelLostPopUp.showLevelPopUp(null);
			} else {
				gameOver = true;
			}
		}
		if ((gameOver) & (!win) & (Hud.getLives() == 0)) {
			dispose();
			game.setScreen(new EndOfGameScreen(game));
		}
        animatedReelHelper.update(delta);
    }

	@Override
	public void render(float delta) {
        if (show) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            if (isLoaded) {
                update(delta);
                handleInput();
                renderer.render();
                game.batch.begin();
                if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE)) {
                    drawPlayingCards(game.batch);
                }
                for (ReelTile reelTile : reelTiles) {
                    if (!reelTile.isReelTileDeleted()) {
                        reelTile.draw(game.batch);
                    }
                }
                for (Score score : scores) {
                    score.render(game.batch);
                }
                if (displaySpinHelp) {
                    sprites[displaySpinHelpSprite].draw(game.batch);
                }
                game.batch.end();
                drawCurrentPlayState();
                game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
                hud.stage.draw();
            } else {
                if (game.annotationAssetManager.getProgress() < 1) {
                    game.annotationAssetManager.update();
                } else {
                    isLoaded = true;
                }
            }
            stage.draw();
        }
    }

    private void drawCurrentPlayState() {
        switch (playState) {
            case INTRO_POPUP:
                levelPopUp.draw(game.batch);
                break;
            case LEVEL_LOST:
                levelLostPopUp.draw(game.batch);
                break;
            case WON_LEVEL:
                levelWonPopUp.draw(game.batch);
                break;
            case INITIALISING:
                break;
            case INTRO_FLASHING:
                break;
            case INTRO_SEQUENCE:
                break;
            case INTRO_SPINNING:
                break;
            case PLAYING:
                break;
            case RESTARTING_LEVEL:
                break;
            default:
                break;
        }
    }

    @Override
    public void show() {
        this.show = true;
		Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "show() called.");
    }

    @Override
	public void resize(int width, int height) {
		viewport.update(width,  height);
	}

	@Override
	public void pause() {
        this.show = false;
		Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "pause() called.");
	}

	@Override
	public void resume() {
		Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "resume() called.");
	}

	@Override
	public void hide() {
		this.show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "hide() called.");
	}

	@Override
	public void dispose() {
		stage.dispose();
		font.dispose();
		chaChingSound.dispose();
	}

	private void drawPlayingCards(SpriteBatch spriteBatch) {
		for (Card card : cards) {
			card.draw(spriteBatch);
		}
	}
}
