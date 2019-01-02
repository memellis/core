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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayInterface;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayState;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStateMachine;
import com.ellzone.slotpuzzle2d.level.Card;
import com.ellzone.slotpuzzle2d.level.FlashSlots;
import com.ellzone.slotpuzzle2d.level.HiddenPattern;
import com.ellzone.slotpuzzle2d.level.HiddenPlayingCard;
import com.ellzone.slotpuzzle2d.level.LevelCallBack;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.LevelLoader;
import com.ellzone.slotpuzzle2d.level.PlayScreenIntroSequence;
import com.ellzone.slotpuzzle2d.level.PlayScreenPopUps;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReelHelper;
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
import aurelienribon.tweenengine.equations.Quad;

import static com.ellzone.slotpuzzle2d.scene.Hud.addScore;

public class PlayScreen implements Screen, PlayInterface {
    public static final int TILE_WIDTH = 40;
	public static final int TILE_HEIGHT = 40;
	public static final int GAME_LEVEL_WIDTH = 12;
	public static final int GAME_LEVEL_HEIGHT = 9;
	public static final int SLOT_REEL_OBJECT_LAYER = 2;
	public static final float PUZZLE_GRID_START_X = 160.0f;
	public static final float PUZZLE_GRID_START_Y = 40.0f;
    private static final String REELS_LAYER_NAME = "Reels";
    private static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    private static final String HIDDEN_PATTERN_LEVEL_TYPE = "HiddenPattern";
	private static final String SLOTPUZZLE_SCREEN = "PlayScreen";
    private LevelLoader levelLoader;
	private PlayStateMachine playStateMachine;

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
 	private FlashSlots flashSlots;
    private TextureAtlas tilesAtlas;
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
    private LevelDoor levelDoor;
	private HiddenPattern hiddenPattern;
	private MapTile mapTile;
	private int mapWidth;
    private int mapHeight;
    private boolean show = false;
    private PlayScreenIntroSequence playScreenIntroSequence;
    private PlayScreenPopUps playScreenPopUps;

	public PlayScreen(SlotPuzzle game, LevelDoor levelDoor, MapTile mapTile) {
		this.game = game;
		this.levelDoor = levelDoor;
		this.mapTile = mapTile;
		createPlayScreen();
		playStateMachine.getStateMachine().changeState(PlayState.INTRO_SPINNING_SEQUENCE);
	}

	private void createPlayScreen() {
		initialisePlayFiniteStateMachine();
		playState = PlayStates.INITIALISING;
        createPlayScreenPart1();
        createPlayScreenPart2();
		createReelIntroSequence();
   	}

    private void initialisePlayFiniteStateMachine() {
		playStateMachine = new PlayStateMachine();
		playStateMachine.setConcretePlay(this);
        playStateMachine.getStateMachine().changeState(PlayState.INITIALISING);
   }

    private void createPlayScreenPart1() {
        initialiseScreen();
        initialiseTweenEngine();
        getAssets(game.annotationAssetManager);
        createSprites();
    }

    private void createPlayScreenPart2() {
        initialisePlayScreen();
        loadLevel();
        initialiseHud();
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
        playScreenPopUps = new PlayScreenPopUps(tilesAtlas, (int) sW, (int) sH, game.batch, tweenManager, levelDoor);
        playScreenPopUps.initialise();
    }

    private void initialiseHud() {
        hud = new Hud(game.batch);
        hud.setLevelName(levelDoor.getLevelName());
    }

    private void loadLevel() {
        levelLoader = getLevelLoader();
        reelTiles = levelLoader.createLevel(GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
        reelsSpinning = reelTiles.size - 1;
        hiddenPattern = levelLoader.getHiddenPattern();
        getMapProperties(level);
        flashSlots = new FlashSlots(tweenManager, mapWidth, mapHeight, reelTiles);
    }

    private LevelLoader getLevelLoader() {
        LevelLoader levelLoader = new LevelLoader(game.annotationAssetManager, levelDoor, mapTile, animatedReelHelper);
        levelLoader.setStoppedSpinningCallback(stoppedSpinningCallback);
        levelLoader.setStoppedFlashingCallback(stoppedFlashingCallback);
        return levelLoader;
    }

    private LevelCallBack stoppedSpinningCallback = new LevelCallBack() {
        @Override
        public void onEvent (ReelTile source){
            reelStoppedSound.play();
            reelsSpinning--;

            if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY) {
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
    };

	private LevelCallBack stoppedFlashingCallback = new LevelCallBack() {
        @Override
        public void onEvent(ReelTile source) {
            if (testForAnyLonelyReels(reelTiles)) {
              win = false;
              if (Hud.getLives() > 0) {
                  playState = PlayScreen.PlayStates.LEVEL_LOST;
                  playScreenPopUps.setLevelLostSpritePositions();
                  playScreenPopUps.getLevelLostPopUp().showLevelPopUp(null);
              } else {
                  gameOver = true;
              }
          }
          reelScoreAnimation(source);
          deleteReelAnimation(source);
        }
    };

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
	}

	private void createSprites() {
        Reels reelsSprites = new Reels(game.annotationAssetManager);
		sprites = reelsSprites.getReels();
	}

    private void getMapProperties(TiledMap level) {
        MapProperties mapProperties = level.getProperties();
        mapWidth = mapProperties.get("width", Integer.class);
        mapHeight = mapProperties.get("height", Integer.class);
    }

    private void createReelIntroSequence() {
        playState = PlayScreen.PlayStates.INTRO_SEQUENCE;
        playScreenIntroSequence = new PlayScreenIntroSequence(reelTiles, tweenManager);
        playScreenIntroSequence.createReelIntroSequence(introSequenceCallback);
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
	        	playScreenPopUps.setPopUpSpritePositions();
	        	playScreenPopUps.getLevelPopUp().showLevelPopUp(null);
		        break;
		}
	}

	private boolean testForHiddenPatternRevealed(Array<ReelTile> levelReel) {
		TupleValueIndex[][] matchGrid = flashSlots.flashSlots(levelReel);
		return hiddenPattern.isHiddenPatternRevealed(matchGrid, reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
	}

	private boolean testForHiddenPlayingCardsRevealed(Array<ReelTile> levelReel) {
		TupleValueIndex[][] matchGrid = flashSlots.flashSlots(levelReel);
		return hiddenPattern.isHiddenPatternRevealed(matchGrid, reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
	}

    private boolean testForAnyLonelyReels(Array<ReelTile> levelReel) {
		PuzzleGridType puzzleGrid = new PuzzleGridType();
		TupleValueIndex[][] grid = levelLoader.populateMatchGrid(levelReel, GAME_LEVEL_WIDTH , GAME_LEVEL_HEIGHT);
		return puzzleGrid.anyLonelyTiles(grid);
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
        flashSlots.deleteAReel();
        if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
            testPlayingCardLevelWon();
        else {
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

	public void handleInput() {
		if (Gdx.input.justTouched()) {
			touchX = Gdx.input.getX();
			touchY = Gdx.input.getY();
            Vector3 unprojTouch = new Vector3(touchX, touchY, 0);
            viewport.unproject(unprojTouch);
 			switch (playState) {
				case INTRO_POPUP:
                    if (isOver(playScreenPopUps.getLevelPopUpSprites().get(0), unprojTouch.x, unprojTouch.y)) {
						playScreenPopUps.getLevelPopUp().hideLevelPopUp(hideLevelPopUpCallback);
					}
					break;
				case LEVEL_LOST:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Lost Level");
					if (isOver(playScreenPopUps.getLevelLostSprites().get(0), unprojTouch.x, unprojTouch.y)) {
					    playScreenPopUps.getLevelLostPopUp().hideLevelPopUp(levelOverCallback);
					}
					break;
				case WON_LEVEL:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Won Level");
					if(isOver(playScreenPopUps.getLevelWonSprites().get(0), unprojTouch.x, unprojTouch.y)) {
						playScreenPopUps.getLevelWonPopUp().hideLevelPopUp(levelWonCallback);
					}
					break;
				default: break;
			}
			if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY) {
                Gdx.app.debug(SLOTPUZZLE_SCREEN, "Play");
                processIsTileClicked();
            }
		}
	}

	private boolean isOver(Sprite sprite, float x, float y) {
        return sprite.getX() <= x && x <= sprite.getX() + sprite.getWidth()
			&& sprite.getY() <= y && y <= sprite.getY() + sprite.getHeight();
	}

	private void testPlayingCardLevelWon() {
		PuzzleGridType puzzleGrid = new PuzzleGridType();
		TupleValueIndex[][] matchGrid = levelLoader.populateMatchGrid(reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
		puzzleGrid.matchGridSlots(matchGrid);
		if (hiddenPattern.isHiddenPatternRevealed(matchGrid, reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT))
			iWonTheLevel();
	}

	private void testForHiddenPlatternLevelWon() {
		PuzzleGridType puzzleGrid = new PuzzleGridType();
		TupleValueIndex[][] matchGrid = levelLoader.populateMatchGrid(reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
		puzzleGrid.matchGridSlots(matchGrid);
		if (hiddenPattern.isHiddenPatternRevealed(matchGrid, reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT))
			iWonTheLevel();
	}

	private void iWonTheLevel() {
		gameOver = true;
	    win = true;
	    playState = PlayStates.WON_LEVEL;
		mapTile.getLevel().setLevelCompleted();
		mapTile.getLevel().setScore(Hud.getScore());
	    playScreenPopUps.setLevelWonSpritePositions();
	    playScreenPopUps.getLevelWonPopUp().showLevelPopUp(null);
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
		int c = PuzzleGridTypeReelTile.getColumnFromLevel(newPoints.x);
		int r = PuzzleGridTypeReelTile.getRowFromLevel(newPoints.y, GAME_LEVEL_HEIGHT);
		if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
			TupleValueIndex[][] grid = levelLoader.populateMatchGrid(reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
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
		hud.stopWorldTimer();
		renderer = new OrthogonalTiledMapRenderer(level);
		displaySpinHelp = false;
		inRestartLevel = false;
		loadLevel();
		createReelIntroSequence();
	}

    @Override
    public int getNumberOfReelsFalling() {
        return 0;
    }

    @Override
    public int getNumberOfReelsSpinning() {
        return reelsSpinning;
    }

    @Override
    public int getNumberOfReelsMatched() {
        return 0;
    }

    @Override
    public int getNumberOfReelsFlashing() {
        return flashSlots.getNumberOfReelsFlashing();
    }

    @Override
    public int getNumberOfReelsToDelete() {
        return flashSlots.getNumberOfReelsToDelete();
    }

    @Override
    public boolean areReelsFalling() {
        return false;
    }

    @Override
    public boolean areReelsSpinning() {
        return reelsSpinning > 0;
    }

    @Override
    public boolean areReelsFlashing() {
        return flashSlots.areReelsFlashing();
    }

    @Override
    public boolean areReelsStartedFlashing() {
        return flashSlots.areReelsStartedFlashing();
    }

    @Override
    public boolean areReelsDeleted() {
        return flashSlots.areReelsDeleted();
    }

	@Override
	public void setReelsAreFlashing(boolean reelsAreFlashing) {
		flashSlots.setReelsAreFlashing(reelsAreFlashing);
	}

	public void updateState(float delta) {
    }

    private void update(float delta) {
	    playStateMachine.update();
		tweenManager.update(delta);
		renderer.setView(camera);
        animatedReelHelper.update(delta);
		hud.update(delta);
		if (hud.getWorldTime() == 0) {
			if ((Hud.getLives() > 0) & (!inRestartLevel)) {
				inRestartLevel = true;
				playState = PlayStates.LEVEL_LOST;
				playScreenPopUps.setLevelLostSpritePositions();
				playScreenPopUps.getLevelLostPopUp().showLevelPopUp(null);
			} else
				gameOver = true;
		}
		if ((gameOver) & (!win) & (Hud.getLives() == 0)) {
			dispose();
			game.setScreen(new EndOfGameScreen(game));
		}
    }

    @Override
    public void start() {
    }

    @Override
    public boolean isStopped() {
        return false;
    }

    @Override
	public void render(float delta) {
        if (show) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            if (isLoaded)
                renderGame(delta);
            else
                isLoaded = isAssetsLoaded();
            }
    }

    private void renderGame(float delta) {
        update(delta);
        handleInput();
        renderer.render();
        renderMainGameElements();
        drawCurrentPlayState();
        renderHud();
        stage.draw();
    }

    private boolean isAssetsLoaded() {
        if (game.annotationAssetManager.getProgress() < 1) {
            game.annotationAssetManager.update();
            return false;
        } else
            return true;
    }

    private void renderMainGameElements() {
		game.batch.begin();
		renderHiddenPattern();
		renderMiniSlotMachine();
		renderReelTiles();
		renderScore();
		renderSpinHelper();
		game.batch.end();
	}

    private void renderHud() {
		game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
		hud.stage.draw();
	}

	private void renderHiddenPattern() {
		if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
            drawPlayingCards(game.batch);
	}

    private void renderMiniSlotMachine() {
    }

	private void renderReelTiles() {
		for (ReelTile reelTile : reelTiles)
			if (!reelTile.isReelTileDeleted())
				reelTile.draw(game.batch);
	}

	private void renderSpinHelper() {
		if (displaySpinHelp)
            sprites[displaySpinHelpSprite].draw(game.batch);
	}

	private void renderScore() {
		for (Score score : scores)
            score.render(game.batch);
	}

	private void drawCurrentPlayState() {
        switch (playState) {
            case INTRO_POPUP:
                playScreenPopUps.getLevelPopUp().draw(game.batch);
                break;
            case LEVEL_LOST:
                playScreenPopUps.getLevelLostPopUp().draw(game.batch);
                break;
            case WON_LEVEL:
                playScreenPopUps.getLevelWonPopUp().draw(game.batch);
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
	    if (hiddenPattern instanceof HiddenPlayingCard)
		    for (Card card : ((HiddenPlayingCard) hiddenPattern).getCards())
			    card.draw(spriteBatch);

	}
}
