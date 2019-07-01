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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Random;
import org.jrenner.smartfont.SmartFontGenerator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.Version;
import com.ellzone.slotpuzzle2d.audio.AudioManager;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ReelLetterAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.messaging.MessageType;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.LightButtonBuilder;
import com.ellzone.slotpuzzle2d.sprites.ReelLetter;
import com.ellzone.slotpuzzle2d.sprites.ReelLetterTile;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.sprites.StarField;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Elastic;
import box2dLight.PointLight;
import box2dLight.RayHandler;

public class IntroScreen extends InputAdapter implements Screen, Telegraph {
	public static final String LIBERATION_MONO_REGULAR_FONT_NAME = "LiberationMono-Regular.ttf";
	public static final String GENERATED_FONTS_DIR = "generated-fonts/";
	public static final String FONT_SMALL = "exo-small";
	public static final String FONT_MEDIUM = "exo-medium";
	public static final String FONT_LARGE = "exo-large";
    public static float SCALE = 0.5f;
    public static int NUM_STARS = 64;

    private static final int TEXT_SPACING_SIZE = 30;
    private static final int REEL_WIDTH = 40;
    private static final int REEL_HEIGHT = 40;
    private static final String COPYRIGHT = "\u00a9";
    private static final String SLOT_PUZZLE_REEL_TEXT = "Slot Puzzle";
    private static final String BY_TEXT = "by";
    private static final String AUTHOR_TEXT = "Mark Ellis";
    private static final String COPYRIGHT_YEAR_AUTHOR_TEXT = COPYRIGHT + "2019 " + AUTHOR_TEXT;
    private static final String LAUNCH_BUTTON_LABEL = "LAUNCH!";
    public static final float ONE_SECOND = 1.0f;
    private SlotPuzzle game;
    private Texture textTexture;
    private Pixmap slotReelPixmap;
    private Texture slotReelTexture;
    private final OrthographicCamera camera = new OrthographicCamera();
    private Viewport viewport, lightViewport;
    private Stage stage;
    private BitmapFont fontSmall;
    private BitmapFont fontMedium;
    private BitmapFont fontLarge;
    private Array<AnimatedReel> reelLetterTiles;
    private Array<DampenedSineParticle> dampenedSines;
    private int numReelLettersSpinning, numReelLetterSpinLoops;
    private boolean endOfIntroScreen;
    private TextButton button;
    private TextButtonStyle textButtonStyle;
    private Skin skin;
    private Slider slider;
    private TextureAtlas buttonAtlas;
    private TextureRegion playBackButtons;
    private TweenManager tweenManager = new TweenManager();
    private ReelTile reelTile;
    private ReelSprites reelSprites;
    private Timeline endReelSeq;
    private boolean isLoaded = false;
    private Random random;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private RayHandler rayHandler;
    private LightButtonBuilder launchButton;
    private Vector3 point = new Vector3();
    private Array<PointLight> signLights;
    private float timerCount = 0;
    private int nextScreenTimer = 3;
    private ShapeRenderer shapeRenderer;
    private StarField starField;
    private float sceneWidth = SlotPuzzleConstants.VIRTUAL_WIDTH / SlotPuzzleConstants.PIXELS_PER_METER;
    private float sceneHeight = SlotPuzzleConstants.VIRTUAL_HEIGHT / SlotPuzzleConstants.PIXELS_PER_METER;
    private boolean show = false;
    private AudioManager audioManager;
    private MessageManager messageManager;
    private boolean sliderUpdating;
    private Rectangle playButton, stopButton, pauseButton;
    private Music currentTrack = null;
    private float songDuration = 183.0f;
    private float currentPosition = 0.0f;
    private float slidertimerCount = 0.0f;
    private InputMultiplexer inputMultiplexer;

    public IntroScreen(SlotPuzzle game) {
        this.game = game;
        defineIntroScreen();
    }

    void defineIntroScreen() {
    	initialiseIntroScreen();
        initialiseTweenEngine();
        initialiseFonts();
        reelSprites = initialiseReelSprites(game.annotationAssetManager);
        initialiseIntroScreenText();
        initialiseBox2D();
        initialiseLaunchButton();
        initialisePlaybackButtons();
        initialiseIntroSequence();
        initialiseStarfield();
        audioManager = new AudioManager(game.annotationAssetManager);
        messageManager = setUpMessages();
        messageManager.dispatchMessage(MessageType.PlayMusic.index, AssetsAnnotation.MUSIC_INTRO_SCREEN);
        isLoaded = true;
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void initialisePlaybackButtons() {
        playBackButtons = new TextureRegion(new Texture(Gdx.files.internal("playback.png")));
        playButton = new Rectangle(13,60, 40,42);
        stopButton = new Rectangle(79, 60, 40, 42);
        pauseButton = new Rectangle(147, 60, 40, 42);

        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        slider = new Slider(0, 100, 0.1f, false, skin);
        slider.setPosition(30, 20);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                if (!sliderUpdating && slider.isDragging())
                    System.out.println("slider.getValue() / 100f"+slider.getValue() / 100f);
                    if (currentTrack != null)
                      currentTrack.setPosition((slider.getValue() / 100f) * songDuration);
            }
        });
        stage.addActor(slider);
    }

    private MessageManager setUpMessages() {
        messageManager = MessageManager.getInstance();
        messageManager.addListeners(audioManager,
                MessageType.PlayMusic.index,
                MessageType.StopMusic.index,
                MessageType.PauseMusic.index);
        messageManager.addListeners(this,
                MessageType.GetCurrentMusicTrack.index);
        return messageManager;
    }

    private ReelSprites initialiseReelSprites(AnnotationAssetManager annotationAssetManager) {
        return new ReelSprites(annotationAssetManager);
    }

    private void initialiseIntroScreen() {
        viewport = new FitViewport(SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        lightViewport = new FitViewport(sceneWidth, sceneHeight);
        lightViewport.getCamera().position.set(lightViewport.getCamera().position.x + sceneWidth * 0.5f,
                                               lightViewport.getCamera().position.y + sceneHeight * 0.5f,
                                               0);
        lightViewport.getCamera().update();
        lightViewport.update(SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT);

        ReelLetter.instanceCount = 0;
        endOfIntroScreen = false;
        Gdx.input.setInputProcessor(this);
        random = new Random();
    }

    private void initialiseTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());    	
        SlotPuzzleTween.registerAccessor(ReelLetterTile.class, new ReelLetterAccessor());    	
    }

    private void initialiseFonts() {
        SmartFontGenerator fontGen = new SmartFontGenerator();
        FileHandle exoFileInternal = Gdx.files.internal(LIBERATION_MONO_REGULAR_FONT_NAME);
        FileHandle generatedFontDir = Gdx.files.local(GENERATED_FONTS_DIR);
        generatedFontDir.mkdirs();

        FileHandle exoFile = Gdx.files.local(GENERATED_FONTS_DIR + LIBERATION_MONO_REGULAR_FONT_NAME);

        try {
            FileUtils.copyFile(exoFileInternal, exoFile);
        } catch (IOException ex) {
            Gdx.app.error(SlotPuzzleConstants.SLOT_PUZZLE, "Could not copy " + exoFileInternal.file().getPath() + " to file " + exoFile.file().getAbsolutePath() + " " + ex.getMessage());
        }

        fontSmall = fontGen.createFont(exoFile, FONT_SMALL, 24);
        fontMedium = fontGen.createFont(exoFile, FONT_MEDIUM, 48);
        fontLarge = fontGen.createFont(exoFile, FONT_LARGE, 64);    	
    }

	private Texture initialiseFontTexture(String reelText) {
		Pixmap textPixmap = new Pixmap(REEL_WIDTH, reelText.length() * REEL_HEIGHT, Pixmap.Format.RGBA8888);
		textPixmap = PixmapProcessors.createDynamicVerticalFontText(fontSmall, reelText, textPixmap);
		Texture textTexture = new Texture(textPixmap);
		return textTexture;
	}

    private ReelTileListener reelTileListener = new ReelTileListener() {
        @Override
        public void actionPerformed(ReelTileEvent event, ReelTile source) {
            if (event instanceof ReelStoppedSpinningEvent) {
                source.setSpinning(false);
                numReelLettersSpinning--;
                if (numReelLettersSpinning == 0) {
                    numReelLettersSpinning = reelLetterTiles.size;
                    numReelLetterSpinLoops--;
                    if (numReelLetterSpinLoops > 0)
                        restartReelLettersSpinning();
                    else
                        endOfIntroScreen = true;
                }
            }
        }
    };

    private void initialiseIntroScreenText() {
        reelLetterTiles = new Array<AnimatedReel>();

        initialiseFontReel(SLOT_PUZZLE_REEL_TEXT, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + TEXT_SPACING_SIZE + 10);
    	initialiseFontReel(BY_TEXT, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + TEXT_SPACING_SIZE + 10);
    	initialiseFontReel(AUTHOR_TEXT, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + TEXT_SPACING_SIZE + 10);
    	initialiseFontReel(COPYRIGHT_YEAR_AUTHOR_TEXT, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + TEXT_SPACING_SIZE + 10);
        initialiseFontReel("v"+ Version.VERSION, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 4.0f + TEXT_SPACING_SIZE + 10);
        numReelLettersSpinning = reelLetterTiles.size;
        numReelLetterSpinLoops = 10;
    }

    private void initialiseFontReel(String reelText, float x, float y) {
        Texture textTexture = initialiseFontTexture(reelText);
        for (int i = 0; i < reelText.length(); i++) {
            AnimatedReel reelLetterTile = new AnimatedReel(textTexture, (float)(x + i * REEL_WIDTH), y, (float)REEL_WIDTH, (float)REEL_HEIGHT, (float)REEL_WIDTH, (float)REEL_HEIGHT, i, tweenManager);
            reelLetterTile.setSy(random.nextInt(reelText.length() - 1) * REEL_HEIGHT);
            reelLetterTile.getReel().addListener(reelTileListener);
            reelLetterTile.getReel().startSpinning();
            reelLetterTiles.add(reelLetterTile);
        }
    }

    private void initialiseBox2D() {
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        rayHandler = new RayHandler(world);
        rayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.5f, 0.5f, 0.5f, 0.1f);

        signLights = new Array<PointLight>();
        PointLight signLight1 = new PointLight(rayHandler, 32);
        signLight1.setActive(true);
        signLight1.setColor(Color.WHITE);
        signLight1.setDistance(2.0f);
        signLight1.setPosition(sceneWidth / 2, sceneHeight / 2);
        signLights.add(signLight1);

        PointLight signLight2 = new PointLight(rayHandler, 32);
        signLight2.setActive(true);
        signLight2.setColor(Color.WHITE);
        signLight2.setDistance(2.0f);
        signLight2.setPosition(sceneWidth / 4, sceneHeight / 2);
        signLights.add(signLight2);

        PointLight signLight3 = new PointLight(rayHandler, 32);
        signLight1.setActive(true);
        signLight1.setColor(Color.WHITE);
        signLight1.setDistance(2.0f);
        signLight1.setPosition(sceneWidth / 2 + sceneWidth / 4, sceneHeight / 2);
        signLights.add(signLight3);
    }

    private void initialiseLaunchButton() {
        Color buttonBackgroundColor = new Color(Color.ORANGE);
        Color buttonForeGroundColor = new Color(Color.ORANGE.r, Color.ORANGE.g, Color.ORANGE.b, 120);
        Color buttonEdgeColor = new Color(Color.BROWN);
        Color buttonTransparentColor = new Color(0, 200, 200, 0);
        Color buttonFontColor = new Color(Color.YELLOW);
        float buttonPositionX = 320 / (float) SlotPuzzleConstants.PIXELS_PER_METER;
        float buttonPositionY = sceneHeight / 12.0f;
        int buttonWidth = 200;
        int buttonHeight = 40;

        launchButton = new LightButtonBuilder.Builder()
                .world(world)
                .rayHandler(rayHandler)
                .buttonBackground(buttonBackgroundColor)
                .buttonForeground(buttonForeGroundColor)
                .buttonEdgeColor(buttonEdgeColor)
                .buttontTransparentColor(buttonTransparentColor)
                .buttonLightColor(Color.RED)
                .buttonLightDistance(1.5f)
                .buttonFontColor(buttonFontColor)
                .buttonPositionX(buttonPositionX)
                .buttonPositionY(buttonPositionY)
                .buttonWidth(buttonWidth)
                .buttonHeight(buttonHeight)
                .buttonFont(fontMedium)
                .buttonText(LAUNCH_BUTTON_LABEL)
                .startButtonTextX(4)
                .startButtonTextY(36)
                .build();

       launchButton.
               getSprite().
               setSize((float) (buttonWidth / (float)SlotPuzzleConstants.PIXELS_PER_METER),
                        buttonHeight / (float)SlotPuzzleConstants.PIXELS_PER_METER);
    }

    private void initialiseIntroSequence() {
        Timeline introSeq = Timeline.createSequence();
        for (int i = 0; i < reelLetterTiles.size; i++)
            introSeq = introSeq.push(SlotPuzzleTween.set(reelLetterTiles.get(i).getReel(), ReelLetterAccessor.POS_XY).target(-40f, -20f + i * 20f));

        introSeq = introSeq.pushPause(1.0f);
        introSeq = getTimeline(introSeq, 0, SLOT_PUZZLE_REEL_TEXT.length(), 0.4f, 250.0f, 30.0f, 360.0f);

        int startOfText = SLOT_PUZZLE_REEL_TEXT.length();
        int endOfText = SLOT_PUZZLE_REEL_TEXT.length() + BY_TEXT.length();
        introSeq = getTimeline(introSeq, startOfText, endOfText, 0.4f, 60.0f, 30.0f, 320.0f);

        startOfText = endOfText;
        endOfText = startOfText + AUTHOR_TEXT.length();
        introSeq = getTimeline(introSeq, startOfText, endOfText, 0.4f, -120.0f, 30.0f, 280.0f);

        startOfText = endOfText;
        endOfText = startOfText + COPYRIGHT_YEAR_AUTHOR_TEXT.length();
        introSeq = getTimeline(introSeq, startOfText, endOfText, 0.4f, -520.0f, 30.0f, 150.0f);

        startOfText = endOfText;
        endOfText = startOfText + 1 + Version.VERSION.length();
        introSeq = getTimeline(introSeq, startOfText, endOfText, 0.4f, -850.0f, 30.0f, 110.0f);

        introSeq.start(tweenManager);

        slotReelPixmap = new Pixmap(REEL_WIDTH, REEL_HEIGHT, Pixmap.Format.RGBA8888);
        slotReelPixmap = PixmapProcessors.createPixmapToAnimate(reelSprites.getSprites());
        slotReelTexture = new Texture(slotReelPixmap);

        reelTile = new ReelTile(
                 slotReelTexture,
                slotReelTexture.getHeight() / REEL_HEIGHT,
                 slotReelTexture.getWidth(),
                viewport.getScreenHeight() / 2,
                 slotReelTexture.getWidth(),
                viewport.getWorldHeight() / 2,
                 REEL_WIDTH, REEL_HEIGHT,
                0,
                null);

        Timeline reelSeq = Timeline.createSequence();
        reelSeq = reelSeq.push(SlotPuzzleTween.set(reelTile, ReelAccessor.SCROLL_XY).target(0f, 0f).ease(Bounce.IN));
        reelSeq = reelSeq.push(SlotPuzzleTween.to(reelTile, ReelAccessor.SCROLL_XY, 5.0f).target(0f, 40.0f * 8 * 3 + random.nextInt(slotReelTexture.getHeight() / 40) * 40).ease(Elastic.OUT));

        reelSeq.
            repeat(100, 0.0f).
            push(SlotPuzzleTween.to(reelTile, ReelAccessor.SCROLL_XY, 5.0f).
            target(0f, 40.0f * 8 * 3 + random.nextInt(slotReelTexture.getHeight() / 40) * 40).
            ease(Elastic.OUT)).
            start(tweenManager);
    }

    private Timeline getTimeline(Timeline introSeq, int startOfText, int endOfText, float duration, float targetX, float targetXFactor, float targetY) {
        for (int i = startOfText; i < endOfText; i++)
            introSeq = introSeq.push(SlotPuzzleTween.to(reelLetterTiles.get(i).getReel(), ReelLetterAccessor.POS_XY, duration).target(targetX + i * targetXFactor, targetY));
        return introSeq;
    }

    private void initialiseStarfield() {
        shapeRenderer = new ShapeRenderer();
        starField = new StarField(shapeRenderer,
                NUM_STARS,
                SCALE,
                SlotPuzzleConstants.VIRTUAL_WIDTH,
                SlotPuzzleConstants.VIRTUAL_HEIGHT,
                random,
                viewport);
    }

	private void restartReelLettersSpinning() {
		int nextSy = 0;
		int endReel = 0;
		for (AnimatedReel reel : reelLetterTiles) {
			reel.setEndReel(endReel++);
			if (endReel == reel.getReel().getNumberOfReelsInTexture())
				endReel = 0;
	        reel.getReel().startSpinning();
	        nextSy = random.nextInt(reelLetterTiles.size - 1) * REEL_HEIGHT;
			reel.setSy(nextSy);
			reel.reinitialise();
		}
	}

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            System.out.println("Left button pressed");
            point.set(screenX, screenY, 0);
            lightViewport.getCamera().unproject(point);
            if (launchButton.getSprite().getBoundingRectangle().contains(point.x, point.y)) {
                launchButton.getLight().setActive(true);
                endOfIntroScreen = true;
                messageManager.dispatchMessage(MessageType.StopMusic.index, AssetsAnnotation.MUSIC_INTRO_SCREEN);
                return true;
            }
        }
        return false;
    }

    private void updateTimer(float delta) {
        if (endOfIntroScreen) {
            timerCount += delta;
            if (timerCount > ONE_SECOND) {
                timerCount = 0;
                nextScreenTimer--;
            }
        }
    }

    public void update(float delta) {
        handleInput();
        tweenManager.update(delta);
        updateTimer(delta);
		for (AnimatedReel reel : reelLetterTiles)
         	reel.update(delta);
        reelTile.update(delta);
        if (endOfIntroScreen) {
            if (nextScreenTimer < 1) {
                game.setScreen(new WorldScreen(game));
                dispose();
            }
        }
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 unprojectedTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(unprojectedTouch);
            if (playButton.contains(unprojectedTouch.x, unprojectedTouch.y))
                messageManager.dispatchMessage(MessageType.PlayMusic.index, AssetsAnnotation.MUSIC_INTRO_SCREEN);
            if (stopButton.contains(unprojectedTouch.x, unprojectedTouch.y))
                messageManager.dispatchMessage(MessageType.StopMusic.index, AssetsAnnotation.MUSIC_INTRO_SCREEN);
            if (pauseButton.contains(unprojectedTouch.x, unprojectedTouch.y))
                messageManager.dispatchMessage(MessageType.PauseMusic.index, AssetsAnnotation.MUSIC_INTRO_SCREEN);
        }
    }

    @Override
    public void render(float delta) {
        if (show) {
            update(delta);

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            if (isLoaded) {
                slidertimerCount += delta;
                if (slidertimerCount > 0.25f) {
                    slidertimerCount = 0.0f;
                    if (currentTrack != null) {
                        currentPosition = currentTrack.getPosition();
//                        sliderUpdating = true;
//                        slider.setValue((currentTrack.getPosition() / songDuration) * 100f);
//                        sliderUpdating = false;
                    }
                }

                starField.updateStarfield(delta, this.shapeRenderer);
                game.batch.begin();
                game.batch.draw(playBackButtons, 0, 60);
                fontSmall.draw(game.batch, String.format("%02d:%02d",(int)currentPosition / 60, (int)currentPosition % 60), 75, 60);
                for (AnimatedReel reel : reelLetterTiles)
                    reel.draw(game.batch);
                reelTile.draw(game.batch);
                game.batch.setProjectionMatrix(lightViewport.getCamera().combined);
                launchButton.getSprite().draw(game.batch);
                game.batch.end();
                rayHandler.setCombinedMatrix((OrthographicCamera) lightViewport.getCamera());
                rayHandler.updateAndRender();
                debugRenderer.render(world, lightViewport.getCamera().combined);
                stage.act();
                stage.draw();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        lightViewport.update(width, height);
        fontSmall.newFontCache();
    }

    @Override
    public void show() {
        this.show = true;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "show() called.");
    }

    @Override
    public void pause() {
        this.show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "pause() called.");
    }

    @Override
    public void resume() {
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "rename() called.");
    }

    @Override
    public void hide() {
        this.show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "hide() called.");
    }

    @Override
    public void dispose() {
        if (tweenManager != null)
        	tweenManager.killAll();

        if (fontSmall != null)
        	fontSmall.dispose();

        if (fontMedium != null)
        	fontMedium.dispose();

        if (fontLarge != null)
        	fontLarge.dispose();
    }

    public SlotPuzzle getGame() {
        return this.game;
    }

    @Override
    public boolean handleMessage(Telegram message) {
        if (message.message == MessageType.GetCurrentMusicTrack.index)
            currentTrack = (Music) message.extraInfo;
        return false;
    }
}
