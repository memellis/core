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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.camera.CameraLerp;
import com.ellzone.slotpuzzle2d.effects.CameraAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.creator.LevelObjectCreatorEntityHolder;
import com.ellzone.slotpuzzle2d.level.map.MapLevel1;
import com.ellzone.slotpuzzle2d.level.map.MapLevel2;
import com.ellzone.slotpuzzle2d.level.map.MapLevel3;
import com.ellzone.slotpuzzle2d.level.map.MapLevel4;
import com.ellzone.slotpuzzle2d.level.map.MapLevel5;
import com.ellzone.slotpuzzle2d.level.map.MapLevel6;
import com.ellzone.slotpuzzle2d.level.map.MapLevel7;
import com.ellzone.slotpuzzle2d.level.map.MapLevel8;
import com.ellzone.slotpuzzle2d.level.map.MapLevelNameComparator;
import com.ellzone.slotpuzzle2d.pixmap.PixmapDrawAction;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.spin.SpinWheel;
import com.ellzone.slotpuzzle2d.spin.SpinWheelSlotPuzzleTileMap;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.level.LevelEntrance;
import com.ellzone.slotpuzzle2d.sprites.sign.ScrollSign;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import org.jrenner.smartfont.SmartFontGenerator;

import java.io.IOException;

import aurelienribon.tweenengine.equations.Quart;

import static com.ellzone.slotpuzzle2d.level.creator.LevelCreator.FALLING_REELS_LEVEL_TYPE;
import static com.ellzone.slotpuzzle2d.level.creator.LevelCreator.HIDDEN_PATTERN_LEVEL_TYPE;
import static com.ellzone.slotpuzzle2d.level.creator.LevelCreator.MINI_SLOT_MACHINE_LEVEL_TYPE;
import static com.ellzone.slotpuzzle2d.level.creator.LevelCreator.PLAYING_CARD_LEVEL_TYPE;

public class WorldScreen implements Screen, LevelCreatorInjectionInterface {

    public static final String LOG_TAG = "SlotPuzzle_WorldScreen";
    public static final String LIBERATION_MONO_REGULAR_FONT_NAME = "LiberationMono-Regular.ttf";
    public static final String GENERATED_FONTS_DIR = "generated-fonts/";
    public static final String FONT_SMALL = "exo-small";
    public static final int FONT_SMALL_SIZE = 24;
    public static final int SIGN_WIDTH = 96;
    public static final int SIGN_HEIGHT = 32;

	private static final String WORLD_MAP = "levels/WorldMap.tmx";
    private static final String TILE_PACK_ATLAS = "tiles/tiles.pack.atlas";
    private static final String WORLD_MAP_LEVEL_DOORS = "Level Doors";
	public static final String LEVEL_TEXT = "Level";
    public static final String ENTRANCE_TEXT = "Entrance";
    public static final char SPACE = ' ';
    public static final int ORTHO_VIEWPORT_WIDTH = 10;
    public static final int ORTHO_VIEWPORT_HEIGHT = 10;
    public static final String LEVEL_DELIMETER = "-";
	public static final String WORLD_LEVEL = "World Level";
	private static final String SLOT_MACHINE_LAYER = "Slot Machine Components";

	private SlotPuzzle game;
	private Viewport viewport;
	private OrthographicCamera camera;
	private TiledMap worldMap;
	private GestureDetector gestureDetector;
	private MapGestureListener mapGestureListener;
	private TiledMapTileLayer mapTextureLayer;
	private Array<LevelDoor> levelDoors;
	private Array<MapTile> mapTiles;
	private Array<LevelEntrance> levelEntrances;
	private Array<ScrollSign> scrollSigns;
	private OrthogonalTiledMapRenderer renderer;
	private BitmapFont font;
	private BitmapFont fontSmall;
	private float resizeWidth, resizeHeight, cww, cwh, aspectRatio;
	private float screenOverCWWRatio, screenOverCWHRatio;
	private Texture levelDoorTexture;
	private Sprite levelDoorSprite;
	private TextureAtlas tilesAtlas;
	private MapTile selectedTile;
	private TweenManager tweenManager;
	private int mapWidth, mapHeight, tilePixelWidth, tilePixelHeight;
	private String message = "";
    private InputMultiplexer inputMultiplexer;
	private boolean inPlayScreen = false;
    private boolean show = false;
	private Hud hud	;
	private int worldScreenScore = 0;
    private World world;
    private LevelObjectCreatorEntityHolder levelObjectCreator;
    private Array<SpinWheelSlotPuzzleTileMap> spinWheels;
    private CameraLerp cameraLerp;

	public WorldScreen(SlotPuzzle game) {
		this.game = game;
		this.game.setWorldScreen(this);
		createWorldScreen();
	}

	private void createWorldScreen() {
		scrollSigns = new Array<ScrollSign>();
		levelEntrances = new Array<LevelEntrance>();
        world = new World(new Vector2(0, -10), true);
        getAssets(game.annotationAssetManager);
		initialiseCamera();
		initialiseUniversalTweenEngine();
		initialiseLibGdx();
		initialiseFonts();
		loadWorld();
		createLevelEntrances();
		initialiseMap();
		createPopUps();
		createHud();
	}

	private void getAssets(AnnotationAssetManager annotationAssetManager) {
		worldMap = annotationAssetManager.get(WORLD_MAP);
		tilesAtlas = annotationAssetManager.get(TILE_PACK_ATLAS, TextureAtlas.class);
	}

	private void initialiseCamera() {
		camera = new OrthographicCamera();
		viewport = new FitViewport(SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, this.camera);
		aspectRatio = SlotPuzzleConstants.VIRTUAL_WIDTH / SlotPuzzleConstants.VIRTUAL_HEIGHT;
		camera.setToOrtho(false, aspectRatio * ORTHO_VIEWPORT_WIDTH, ORTHO_VIEWPORT_HEIGHT);
		camera.zoom = 2;
		camera.update();
		cww = camera.viewportWidth * camera.zoom * tilePixelWidth;
		cwh = camera.viewportHeight * camera.zoom * tilePixelHeight;
		screenOverCWWRatio = SlotPuzzleConstants.VIRTUAL_WIDTH / cww;
		screenOverCWHRatio = SlotPuzzleConstants.VIRTUAL_HEIGHT / cwh;
		cameraLerp = new CameraLerp(camera);
	}

	private void initialiseLibGdx() {
		font = new BitmapFont();
		mapGestureListener = new MapGestureListener(this.camera);
		gestureDetector = new GestureDetector(2, 0.5f, 2, 0.15f, mapGestureListener);
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(gestureDetector);
		Gdx.input.setInputProcessor(inputMultiplexer);
		renderer = new OrthogonalTiledMapRenderer(worldMap, 1f / 40f);
		Matrix4 gameProjectionMatrix = new Matrix4();
		gameProjectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		game.batch.setProjectionMatrix(gameProjectionMatrix);
	}

	private void initialiseUniversalTweenEngine() {
		SlotPuzzleTween.setWaypointsLimit(10);
		SlotPuzzleTween.setCombinedAttributesLimit(3);
		SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
		SlotPuzzleTween.registerAccessor(Camera.class, new CameraAccessor());
		tweenManager = new TweenManager();
	}

	private void initialiseFonts() {
		SmartFontGenerator fontGen = new SmartFontGenerator();
		FileHandle internalFontFile = Gdx.files.internal(LIBERATION_MONO_REGULAR_FONT_NAME);
		FileHandle generatedFontDir = Gdx.files.local(GENERATED_FONTS_DIR);
		generatedFontDir.mkdirs();

		FileHandle generatedFontFile = Gdx.files.local("generated-fonts/LiberationMono-Regular.ttf");
		if (!generatedFontFile.exists())
			try {
				FileUtils.copyFile(internalFontFile, generatedFontFile);
			} catch (IOException ex) {
				Gdx.app.error(SlotPuzzleConstants.SLOT_PUZZLE, "Could not copy " + internalFontFile.file().getPath() + " to file " + generatedFontFile.file().getAbsolutePath() + " " + ex.getMessage());
			}
		fontSmall = fontGen.createFont(generatedFontFile, FONT_SMALL, FONT_SMALL_SIZE);
	}

    private Array<String> initialiseScrollSignMessages(String message) {
        Array<String> scrollSignMessages = new Array<String>();
        scrollSignMessages.add(message);
        scrollSignMessages.add(message + "level completed ");
        return scrollSignMessages;
    }

    private Array<Texture> initialiseFontTextures(Array<String> textureTexts) {
        Texture textTexture;
        Array <Texture> textTextures = new Array<Texture>();
        for (String textureText : textureTexts) {
            Pixmap textPixmap = new Pixmap(textureText.length() * SIGN_WIDTH / 6, SIGN_HEIGHT, Pixmap.Format.RGBA8888);
            textPixmap = PixmapProcessors.createDynamicHorizontalFontTextViaFrameBuffer(fontSmall, Color.BLUE, textureText, textPixmap, 3, 20);
            textTexture = new Texture(textPixmap);
            textTextures.add(textTexture);
        }
        return  textTextures;
    }

    private void createLevelEntrances() {
		for (int i = 0; i < levelDoors.size; i++) {
			levelEntrances.add(new LevelEntrance((int) levelDoors.get(i).getDoorPosition().getWidth(), (int) levelDoors.get(i).getDoorPosition().getHeight()));
		}
	}

	private void initialiseMap() {
		mapTextureLayer = (TiledMapTileLayer) worldMap.getLayers().get("Tile Layer 1");

		for (int levelNumber = 0; levelNumber < levelDoors.size; levelNumber++) {
			ScrollSign scrollSign = addScrollSign(levelNumber, levelEntrances.get(levelNumber).getLevelEntrance().getWidth());
			scrollSigns.add(scrollSign);
            drawLevelEntrance(levelNumber, mapTextureLayer);
			TextureRegion[][] splitTiles = TextureRegion.split(levelEntrances.get(levelNumber).getLevelEntrance(), 40, 40);
			int xx = (int) levelDoors.get(levelNumber).getDoorPosition().getX() / 40;
			int yy = (int) levelDoors.get(levelNumber).getDoorPosition().getY() / 40;
			for (int row = 0; row < splitTiles.length; row++) {
				for (int col = 0; col < splitTiles[row].length; col++) {
					TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
					cell.setTile(new StaticTiledMapTile(splitTiles[row][col]));
					mapTextureLayer.setCell(xx + col, yy + (splitTiles.length - row), cell);
				}
			}
		}
	}

	private ScrollSign addScrollSign(int levelNumber, int scrollSignWidth) {
        Array<String> textMessages = initialiseScrollSignMessages(LEVEL_TEXT + SPACE + (levelNumber + 1) + SPACE + ENTRANCE_TEXT + SPACE);
        Array<Texture> textTextures = initialiseFontTextures(textMessages);
        return new ScrollSign(textTextures, 0, 0, scrollSignWidth, SIGN_HEIGHT, ScrollSign.SignDirection.RIGHT);
    }

	private void drawOnCell(TiledMapTileLayer layer, int cellX, int cellY, PixmapDrawAction drawAction) {
        TiledMapTileLayer.Cell cell = layer.getCell(cellX, cellY);
		TiledMapTile tile = cell.getTile();
		Pixmap tilePixmap = PixmapProcessors.getPixmapFromTextureRegion(tile.getTextureRegion());
		drawAction.drawAction(tilePixmap);
        Texture tileTexture = new Texture(tilePixmap);
        TextureRegion tileTextureRegion = new TextureRegion(tileTexture);
        cell.setTile(new StaticTiledMapTile(tileTextureRegion));
        layer.setCell(cellX, cellY, cell);
    }

	private void drawLevelEntrance(int levelNumber, TiledMapTileLayer layer) {
		int levelDoorX = (int) levelDoors.get(levelNumber).getDoorPosition().getX() / 40;
		int levelDoorY = (int) levelDoors.get(levelNumber).getDoorPosition().getY() / 40;
		int levelDoorWidth = (int) levelDoors.get(levelNumber).getDoorPosition().getWidth() / 40;
		int levelDoorHeight = (int) levelDoors.get(levelNumber).getDoorPosition().getHeight() / 40;

        drawOnCell(layer, levelDoorX - 1, levelDoorY + levelDoorHeight, new PixmapDrawAction() {
            @Override
            public void drawAction(Pixmap pixmap) {
                pixmap.setColor(Color.RED);
                int tileWidth = pixmap.getWidth();
                int tileHeight = pixmap.getHeight();
                pixmap.fillRectangle(tileWidth - 4, 0, 4, tileHeight);
            }
        });

		for (int ceilingX = levelDoorX; ceilingX < levelDoorX + levelDoorWidth; ceilingX++) {
            drawOnCell(layer, ceilingX, levelDoorY + levelDoorHeight + 1, new PixmapDrawAction() {
                @Override
                public void drawAction(Pixmap pixmap) {
                    pixmap.setColor(Color.RED);
                    int tileWidth = pixmap.getWidth();
                    int tileHeight = pixmap.getHeight();
                    pixmap.fillRectangle(0, tileHeight - 4, tileWidth, tileHeight);
                }
            });
 		}

		drawOnCell(layer, levelDoorX + levelDoorWidth, levelDoorY + levelDoorHeight, new PixmapDrawAction() {
            @Override
            public void drawAction(Pixmap pixmap) {
                pixmap.setColor(Color.RED);
                int tileHeight = pixmap.getHeight();
                pixmap.fillRectangle(0, 0, 4, tileHeight);
            }
        });
	}

	private void loadWorld() {
		getMapProperties();
		loadDoors();
		loadEntities();
		setUpEntities();
	}

    private void loadDoors() {
		levelDoors = new Array();
		levelDoors.setSize(worldMap.getLayers().get(WORLD_MAP_LEVEL_DOORS).getObjects().getByType(RectangleMapObject.class).size);
		for (MapObject mapObject : worldMap.getLayers().get(WORLD_MAP_LEVEL_DOORS).getObjects().getByType(RectangleMapObject.class)) {
			LevelDoor levelDoor = new LevelDoor();
			levelDoor.setLevelName(((RectangleMapObject) mapObject).getName());
			levelDoor.setLevelType((String) ((RectangleMapObject) mapObject).getProperties().get("type"));
			levelDoor.setDoorPosition(((RectangleMapObject) mapObject).getRectangle());
			int levelDoorIndex = getLevelIndex(levelDoor);
			if (levelDoorIndex > levelDoors.size)
			    levelDoors.setSize(levelDoorIndex);
			levelDoors.set(getLevelIndex(levelDoor), levelDoor);
		}
	}

	public void loadEntities() {
        levelObjectCreator = new LevelObjectCreatorEntityHolder(this, world, null);
		Array<RectangleMapObject> extractedLevelRectangleMapObjects = extractLevelAssets(worldMap);
		levelObjectCreator.createLevel(extractedLevelRectangleMapObjects);
		spinWheels = levelObjectCreator.getSpinWheels();
	}

	private Array<RectangleMapObject> extractLevelAssets(TiledMap level) {
		Array<RectangleMapObject> levelRectangleMapObjects = getRectangleMapObjectsFromLevel(level);
		MapLevelNameComparator mapLevelNameComparator = new MapLevelNameComparator();
		levelRectangleMapObjects.sort(mapLevelNameComparator);
		return levelRectangleMapObjects;
	}

	private Array<RectangleMapObject> getRectangleMapObjectsFromLevel(TiledMap level) {
		return level.getLayers().get(SLOT_MACHINE_LAYER).
				getObjects().getByType(RectangleMapObject.class);
	}


	private int getLevelIndex(LevelDoor levelDoor) {
        String[] levelNumber = levelDoor.getLevelName().split(LEVEL_DELIMETER);
        return Integer.parseInt(levelNumber[1]) - 1;
    }

    private void getMapProperties() {
		MapProperties worldProps = worldMap.getProperties();
		mapWidth = worldProps.get("width", Integer.class);
		mapHeight = worldProps.get("height", Integer.class);
		tilePixelWidth = worldProps.get("tilewidth", Integer.class);
		tilePixelHeight = worldProps.get("tileheight", Integer.class);
	}

    private void setUpEntities() {
        setUpSpinWheels();
    }

    private void setUpSpinWheels() {
        for (SpinWheelSlotPuzzleTileMap spinWheel : spinWheels)
            setUpSpinWheel(spinWheel);
    }

    private void setUpSpinWheel(SpinWheelSlotPuzzleTileMap spinWheel) {
        spinWheel.setUpSpinWheel();
	}

    private void createPopUps() {
		mapTiles = new Array<MapTile>();
		mapTiles.add(new MapTile(20, 20, 200, 200, SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, new MapLevel1(), tilesAtlas, this.camera, font, tweenManager, new Sprite(levelEntrances.get(0).getLevelEntrance())));
		mapTiles.add(new MapTile(20, 20, 200, 200, SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, new MapLevel2(), tilesAtlas, this.camera, font, tweenManager, new Sprite(levelEntrances.get(1).getLevelEntrance())));
		mapTiles.add(new MapTile(20, 20, 200, 200, SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, new MapLevel3(), tilesAtlas, this.camera, font, tweenManager, new Sprite(levelEntrances.get(2).getLevelEntrance())));
		mapTiles.add(new MapTile(20, 20, 200, 200, SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, new MapLevel4(), tilesAtlas, this.camera, font, tweenManager, new Sprite(levelEntrances.get(3).getLevelEntrance())));
		mapTiles.add(new MapTile(20, 20, 200, 200, SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, new MapLevel5(), tilesAtlas, this.camera, font, tweenManager, new Sprite(levelEntrances.get(4).getLevelEntrance())));
		mapTiles.add(new MapTile(20, 20, 200, 200, SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, new MapLevel6(), tilesAtlas, this.camera, font, tweenManager, new Sprite(levelEntrances.get(5).getLevelEntrance())));
		mapTiles.add(new MapTile(20, 20, 200, 200, SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, new MapLevel7(), tilesAtlas, this.camera, font, tweenManager, new Sprite(levelEntrances.get(6).getLevelEntrance())));
		mapTiles.add(new MapTile(20, 20, 200, 200, SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, new MapLevel8(), tilesAtlas, this.camera, font, tweenManager, new Sprite(levelEntrances.get(7).getLevelEntrance())));
	}

	private void createHud() {
		hud = new Hud(game.batch);
		hud.setCountDownVisible(false);
		hud.setLevelName(WORLD_LEVEL);
	}

	private final TweenCallback maximizeCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
        selectedTile = (MapTile) source.getUserData();
        selectedTile.disableDraw();
        selectedTile.getLevel().initialise();
        int levelNumber = selectedTile.getLevel().getLevelNumber();
		levelDoors.get(levelNumber).setId(levelNumber);
		inPlayScreen = true;
		Gdx.input.setInputProcessor(null);
		setNextPlayScreen(levelNumber);
		tweenManager.killAll();
		}
	};

	private void setNextPlayScreen(int levelNumber) {
		if (levelDoors.get(levelNumber).getLevelType().equals(HIDDEN_PATTERN_LEVEL_TYPE))
			game.setScreen(new PlayScreenHiddenPattern(game, levelDoors.get(levelNumber), selectedTile));
		if (levelDoors.get(levelNumber).getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
			game.setScreen(new PlayScreenHiddenPattern(game, levelDoors.get(levelNumber), selectedTile));
		if (levelDoors.get(levelNumber).getLevelType().equals(MINI_SLOT_MACHINE_LEVEL_TYPE))
			game.setScreen(new PlayScreenMiniSlotMachine(game, levelDoors.get(levelNumber), selectedTile));
		if (levelDoors.get(levelNumber).getLevelType().equals(FALLING_REELS_LEVEL_TYPE))
			game.setScreen(new PlayScreenFallingReels(game, levelDoors.get(levelNumber), selectedTile));
	}

	@Override
	public void show() {
        this.show = true;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "show() called.");
    }

	private void updateDynamicDoors(float dt) {
		int levelNumber = 0,
				levelDoorX,
				levelDoorY,
				levelDoorHeight;
		TiledMapTileLayer.Cell cell;

		for (LevelDoor levelDoor : levelDoors) {
			levelDoorX = (int) levelDoor.getDoorPosition().getX() / 40;
			levelDoorY = (int) levelDoor.getDoorPosition().getY() / 40;
			levelDoorHeight = (int) levelDoor.getDoorPosition().getHeight() / 40;
			for (int col = 0; col < (scrollSigns.get(levelNumber).getSignWidth()) / 40; col++) {
				cell = new TiledMapTileLayer.Cell();
				cell.setTile(new StaticTiledMapTile(new TextureRegion(scrollSigns.get(levelNumber), col * 40, 0, 40, 40)));
				mapTextureLayer.setCell(levelDoorX + col, levelDoorY + levelDoorHeight, cell);

				if (mapTiles.get(levelNumber).getLevel().isLevelCompleted()) {
                    if (!mapTiles.get(levelNumber).getLevel().hasLevelScrollSignChanged()) {
                        updateScrollSignToLevelCompleted(mapTiles.get(levelNumber), scrollSigns.get(levelNumber));
                    }
				}
			}
			levelNumber++;
		}
		for (ScrollSign scrollSign : scrollSigns) {
			scrollSign.update(dt);
			scrollSign.setSx(scrollSign.getSx() + 1);
		}
	}

	private void updateScrollSignToLevelCompleted(MapTile maptile, ScrollSign scrollSign) {
        maptile.getLevel().setLevelScrollSignChanged(true);
        Array<Texture> signTextures = scrollSign.getSignTextures();
        String textureText = maptile.getLevel().getTitle() + " level completed with Score: " + maptile.getLevel().getScore();
        Pixmap textPixmap = new Pixmap(textureText.length() * SIGN_WIDTH / 6, SIGN_HEIGHT, Pixmap.Format.RGBA8888);
        textPixmap = PixmapProcessors.createDynamicHorizontalFontTextViaFrameBuffer(fontSmall, Color.CYAN, textureText, textPixmap, 0, 20);
        Texture textTexture = new Texture(textPixmap);
        signTextures.set(1, textTexture);
        scrollSign.switchSign(scrollSign.getCurrentSign() == 0 ? 1 : 0);
	}

	public void update(float delta) {
		tweenManager.update(delta);
		updateWorld(delta);
		updateDynamicDoors(delta);
		updateEntities(delta);
		if (Gdx.input.isKeyPressed(Input.Keys.S))
			for (SpinWheelSlotPuzzleTileMap spinWheel : spinWheels)
				spinWheel.spin(MathUtils.random(5F, 30F));

		if (Gdx.input.isKeyPressed(Input.Keys.L) &
			!cameraLerp.isCameraLerpStarted())
			cameraLerp.setUpCameraLerp(
					new Vector2(
							spinWheels.get(0).getWorldPositionX() / SpinWheel.PPM,
							spinWheels.get(0).getWorldPositionY() / SpinWheel.PPM - 15.0f),
					endOfCameraLerpCallback)
					.start(tweenManager);
	}

	TweenCallback endOfCameraLerpCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			cameraLerp.setCameraLerpStarted(false);
			camera.zoom = 5.0f;
		}
	};

	public void updateWorld(float delta) {
		world.step(1 / 60f, 8, 2);
	}

	public void updateEntities(float delta) {
		updateSpinWheels();
	}

	private void updateSpinWheels() {
		for (SpinWheelSlotPuzzleTileMap spinWheel : spinWheels)
			updateSpinWheel(spinWheel);
	}

	private void updateSpinWheel(SpinWheelSlotPuzzleTileMap spinWheel) {
		if (!spinWheel.spinningStopped()) {
			updateCoordinates(spinWheel.getWheelBody(), spinWheel.getWheelImage(), 0, 0);
			updateCoordinates(spinWheel.getNeedleBody(), spinWheel.getNeedleImage(), 0, -25F);
		} else {
			System.out.println("lucky element is: " + spinWheel.getLuckyWinElement());
		}
	}

	private void updateCoordinates(Body body, Image image, float incX, float incY) {
		image.setPosition(
				body.getPosition().x + (incX / SpinWheel.PPM),
				body.getPosition().y + (incY / SpinWheel.PPM),
				Align.center);
		image.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
	}

	@Override
	public void render(float delta) {
        if (show) {
            if (!inPlayScreen) {
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                update(delta);
                mapGestureListener.update();
				renderWorld();
			}
        }
	}

	private void renderWorld() {
		renderer.render();
		renderer.setView(camera);
		camera.update();
		game.batch.begin();
		renderMapTiles();
		font.draw(game.batch, message, 80, 100);
		game.batch.end();
		renderer.getBatch().begin();
		renderEntities((SpriteBatch) renderer.getBatch());
		renderer.getBatch().end();
		renderHud();
	}

	private void renderEntities(SpriteBatch batch) {
		renderSpinWheels(batch);
	}

	private void renderSpinWheels(SpriteBatch batch) {
		for (SpinWheelSlotPuzzleTileMap spinWheel : spinWheels)
			renderSpinWheel(spinWheel, batch);
	}

	private void renderSpinWheel(SpinWheelSlotPuzzleTileMap spinWheel, SpriteBatch batch) {
		spinWheel.getWheelImage().draw(batch, 1.0f);
		spinWheel.getNeedleImage().draw(batch, 1.0f);
	}

	private void renderMapTiles() {
		for (MapTile mapTile : mapTiles)
			mapTile.draw(game.batch);
	}

	private void renderHud() {
		game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
		hud.stage.draw();
	}

	@Override
	public void resize(int width, int height) {
        this.resizeWidth = width;
        this.resizeHeight = height;
        Gdx.app.log(LOG_TAG, "resize(int width, int height) called: width=" + width + ", height="+height);
    }

	@Override
	public void pause() {
        this.show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "pause() called.");
    }

	@Override
	public void resume() {
        this.show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "resume() called.");
    }

	@Override
	public void hide() {
        this.show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "hide() called.");
    }

	@Override
	public void dispose() {
        Gdx.app.log(LOG_TAG, "dispose() called.");
    }

	@Override
	public AnnotationAssetManager getAnnotationAssetManager() {
		return null;
	}

	@Override
	public ReelSprites getReelSprites() {
		return null;
	}

	@Override
	public Texture getSlotReelScrollTexture() {
		return null;
	}

	@Override
	public TweenManager getTweenManager() {
		return tweenManager;
	}

	@Override
	public TextureAtlas getSlothandleAtlas() {
		return game.annotationAssetManager.get(AssetsAnnotation.SLOT_HANDLE);
	}

	public class MapGestureListener implements GestureListener {

		@Override
		public void pinchStop() {
			Gdx.app.debug(LOG_TAG, "pinchStop()");
		}

		private final OrthographicCamera camera;
		float velX, velY;
		boolean flinging = false;
		float initialScale = 1;

		public MapGestureListener(OrthographicCamera camera) {
			this.camera = camera;
		}

		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {
			flinging = false;
			initialScale = camera.zoom;
			processTouch(x, y);
			return false;
		}

		@Override
		public boolean tap(float x, float y, int count, int button) {
			Gdx.app.debug(LOG_TAG, "tap() x=" + x + " y=" + y + " count="+count+ " button="+button);
			return false;
		}

		@Override
		public boolean longPress(float x, float y) {
			Gdx.app.debug(LOG_TAG, "longPress() x="+x+" y="+y);
			return false;
		}

		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			flinging = true;
			velX = camera.zoom * velocityX * 0.01f;
			velY = camera.zoom * velocityY * 0.01f;
			return false;
		}

		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY) {
			camera.position.add(-deltaX * camera.zoom * 0.01f, deltaY * camera.zoom * 0.01f, 0);
			clampCamera();
			return false;
		}

		@Override
		public boolean panStop(float x, float y, int pointer, int button) {
			return false;
		}

		@Override
		public boolean zoom (float originalDistance, float currentDistance){
			float ratio = originalDistance / currentDistance;
			camera.zoom = initialScale * ratio;
			return false;
		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
			return false;
		}

		public void update () {
			if (flinging) {
				velX *= 0.9f;
				velY *= 0.9f;
				camera.position.add(-velX * Gdx.graphics.getDeltaTime(), velY * Gdx.graphics.getDeltaTime(), 0);
				clampCamera();
				if (Math.abs(velX) < 0.01f) velX = 0;
				if (Math.abs(velY) < 0.01f) velY = 0;
			}
		}

		private void processTouch(float x, float y) {
			float wx = screenXToWorldX(x);
			float wy = screenYToWorldY(y);
            int levelDoorIndex = 0;
			for (LevelDoor levelDoor : levelDoors) {
				if (levelDoor.getDoorPosition().contains(wx, wy))
					enterLevel(levelDoor, levelDoorIndex);
				levelDoorIndex++;
			}
		}

		private void enterLevel(LevelDoor levelDoor, int levelDoorIndex) {
			int sx = (int) (worldXToScreenX(levelDoor.getDoorPosition().x));
			int sy = (int) (worldYToScreenY(levelDoor.getDoorPosition().y + tilePixelHeight));
			int sw = (int) (levelDoor.getDoorPosition().width * screenOverCWWRatio);
			int sh = (int) (levelDoor.getDoorPosition().height * screenOverCWHRatio);

			levelDoorTexture = levelEntrances.get(levelDoorIndex).getLevelEntrance();
			levelDoorSprite = new Sprite(levelDoorTexture);
			levelDoorSprite.setOrigin(0, 0);
			levelDoorSprite.setBounds(sx, sy, sw, sh);
			mapTiles.get(levelDoorIndex).setSprite(levelDoorSprite);
			mapTiles.get(levelDoorIndex).reinitialise();
			mapTiles.get(levelDoorIndex).maximize(maximizeCallback);
	}

		private void clampCamera() {
			if (camera.position.x < 0)
				camera.position.x = 0;
			if (camera.position.x > mapWidth)
				camera.position.x = mapWidth;
			if (camera.position.y < 0)
				camera.position.y = 0;
			if (camera.position.y > mapHeight)
				camera.position.y = mapHeight;
		}

		public float screenXToWorldX(float x) {
			return ((camera.position.x - aspectRatio * ORTHO_VIEWPORT_WIDTH) * tilePixelWidth) + ((x / screenOverCWWRatio) * (float)SlotPuzzleConstants.VIRTUAL_WIDTH / Gdx.graphics.getWidth());
		}

		public float screenYToWorldY(float y) {
            return ((camera.position.y - ORTHO_VIEWPORT_HEIGHT) * tilePixelHeight) + (( Gdx.graphics.getHeight() - y) / screenOverCWHRatio) * (float) SlotPuzzleConstants.VIRTUAL_HEIGHT / Gdx.graphics.getHeight();
		}

		public float worldXToScreenX(float wx) {
			return (wx  - ((camera.position.x - aspectRatio * ORTHO_VIEWPORT_WIDTH) * tilePixelWidth)) * screenOverCWWRatio;
		}

		public float worldYToScreenY(float wy) {
			return (wy - ((camera.position.y - ORTHO_VIEWPORT_HEIGHT) * tilePixelHeight)) * screenOverCWHRatio;
		}
	}

	public void worldScreenCallBack(MapTile mapTile) {
		inPlayScreen = false;
		hud.addScore(mapTile.getLevel().getScore());
		tweenManager.killAll();
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
}
