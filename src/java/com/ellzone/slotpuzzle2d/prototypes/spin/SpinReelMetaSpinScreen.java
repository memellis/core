package com.ellzone.slotpuzzle2d.prototypes.spin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.spin.SpinWheel;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.Random;
import com.ellzone.slotpuzzle2d.utils.ScreenshotFactory;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class SpinReelMetaSpinScreen extends SPPrototype {
    private static final String TAG = SpinScreen.class.getSimpleName();
    private static final float WIDTH = 1280;
    private static final float HEIGHT = 1080;
    private static final float WHEEL_DIAMETER = 750F;
    private static final int NUMBER_OF_PEGS = 12;

    private Stage stage;
    private SpriteBatch batch;
    private SpinWheel spinWheel;
    private Image wheelImage;
    private Image needleImage;
    private Texture animatedScreenLeft, animatedScreenMiddle, animatedScreenRight;
    private boolean screenCaptured = false;
    private TweenManager tweenManager;
    private AnimatedReel animatedReelScreenLeft;
    private Array<AnimatedReel> animatedScreenReels = new Array<>();
    private int numberOfReelsSpinning = 0;

    @Override
    public void create() {
        stage = new Stage(new ExtendViewport(WIDTH, HEIGHT));
        Gdx.input.setInputProcessor(stage);
        final float width = stage.getWidth();
        final float height = stage.getHeight();

        batch = new SpriteBatch();
        tweenManager = new TweenManager();
        initialiseTweenEngine();
        setUpSpinWheel(width, height);
    }

    private void setUpSpinWheel(float width, float height) {
        spinWheel = new SpinWheel(width, height, WHEEL_DIAMETER, width / 2, height / 2, NUMBER_OF_PEGS);

        final TextureAtlas atlas = new TextureAtlas("spin/spin_wheel_ui.atlas");

        spinWheel.getWheelBody().setUserData(wheelImage = new Image(atlas.findRegion("spin_wheel")));
        updateCoordinates(spinWheel.getWheelBody(), wheelImage, 0, 0);
        wheelImage.setOrigin(Align.center);
        stage.addActor(wheelImage);

        final Image btnSpin = new Image(atlas.findRegion("spin_button"));
        btnSpin.setOrigin(Align.center);
        btnSpin.setPosition(width / 2, height / 2, Align.center);
        stage.addActor(btnSpin);

        btnSpin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btnSpin.addAction(sequence(scaleTo(1.25F, 1.25F, 0.10F), scaleTo(1F, 1F, 0.10F)));
                spinWheel.spin(MathUtils.random(5F, 30F));

                Gdx.app.debug(TAG, "Spinning.");
            }
        });

        spinWheel.getNeedleBody().setUserData(needleImage = new Image(new Sprite(atlas.findRegion("needle"))));
        updateCoordinates(spinWheel.getNeedleBody(), needleImage, 0, -25F);
        needleImage.setOrigin(spinWheel.getNeedleCenterX(needleImage.getWidth()), spinWheel.getNeedleCenterY(needleImage.getHeight()));
        stage.addActor(needleImage);

        setElementData();
    }

    private void updateCoordinates(Body body, Image image, float incX, float incY) {
        image.setPosition((body.getPosition().x * SpinWheel.PPM) + incX, (body.getPosition().y * SpinWheel.PPM) + incY, Align.center);
        image.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
    }

    private void setElementData() {
        spinWheel.addElementData(Color.valueOf("e966ac"), getData(1, 2));
        spinWheel.addElementData(Color.valueOf("b868ad"), getData(2, 3));
        spinWheel.addElementData(Color.valueOf("8869ad"), getData(3, 4));
        spinWheel.addElementData(Color.valueOf("3276b5"), getData(4, 5));
        spinWheel.addElementData(Color.valueOf("33a7d8"), getData(5, 6));
        spinWheel.addElementData(Color.valueOf("33b8a5"), getData(6, 7));
        spinWheel.addElementData(Color.valueOf("a3fd39"), getData(7, 8));
        spinWheel.addElementData(Color.valueOf("fff533"), getData(8, 9));
        spinWheel.addElementData(Color.valueOf("fece3e"), getData(9, 10));
        spinWheel.addElementData(Color.valueOf("f9a54b"), getData(10, 11));
        spinWheel.addElementData(Color.valueOf("f04950"), getData(12, 1));
    }

    private IntArray getData(int peg_1, int peg_2) {
        IntArray array = new IntArray(2);
        array.addAll(peg_1, peg_2);
        return array;
    }

    private void initialiseTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
    }

    public void render() {
        final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        update(delta);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spinWheel.render(false);

        if (!spinWheel.spinningStopped()) {
            updateCoordinates(spinWheel.getWheelBody(), wheelImage, 0, 0);

            updateCoordinates(spinWheel.getNeedleBody(), needleImage, 0, -25F);
        } else {

            System.out.println("lucky element is: " + spinWheel.getLuckyWinElement());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER))
            spinWheel.spin(0.2F);


        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (!screenCaptured) {
                screenCapture();
                screenCaptured = true;
            }
        }

        if (screenCaptured == true) {
            batch.begin();
            for(AnimatedReel animatedScreenReel : animatedScreenReels)
                animatedScreenReel.draw(batch);
            batch.end();
        }

    }

    private void update(float delta) {
        for (AnimatedReel animatedScreenReel : animatedScreenReels)
            animatedScreenReel.update(delta);
        tweenManager.update(delta);
    }

    private void screenCapture() {
        Pixmap screenShot = ScreenshotFactory.getScreenshot(
                0,
                0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight(),
                true);

        Texture screenShotsheet = new Texture(screenShot);
        TextureRegion[][] screenShotGrid = TextureRegion.split(
                screenShotsheet,
                screenShotsheet.getWidth() / 3,
                screenShot.getHeight() / 3
        );

        TextureRegion[] screenFrames = new TextureRegion[3 * 3];
        int index = 0;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                screenFrames[index++] = screenShotGrid[i][j];

        Sprite[] spritesLeft = new Sprite[3];
        Sprite[] spritesMiddle = new Sprite[3];
        Sprite[] spritesRight = new Sprite[3];


        for (int i = 0; i < 3; i++) {
            spritesLeft[i]   = new Sprite(screenFrames[0 + 3 * i]);
            spritesMiddle[i] = new Sprite(screenFrames[1 + 3 * i]);
            spritesRight[i]  = new Sprite(screenFrames[2 + 3 * i]);
        }

        Array<Texture> animatedScreenTextures = new Array<>();
        animatedScreenTextures.add(new Texture(PixmapProcessors.createPixmapToAnimate(spritesLeft)));
        animatedScreenTextures.add(new Texture(PixmapProcessors.createPixmapToAnimate(spritesMiddle)));
        animatedScreenTextures.add(new Texture(PixmapProcessors.createPixmapToAnimate(spritesRight)));

        int reelIndex = 0;
        for (Texture animatedScreenTexture : animatedScreenTextures) {
            final AnimatedReel animatedReel = new AnimatedReel(
                    animatedScreenTexture,
                    reelIndex++ * Gdx.graphics.getWidth() / 3,
                    0 ,
                    Gdx.graphics.getWidth() / 3,
                    Gdx.graphics.getHeight() / 3,
                    Gdx.graphics.getWidth() / 3,
                    Gdx.graphics.getWidth(),
                    Random.getInstance().nextInt(3),
                    tweenManager);
            animatedReel.setupSpinning();
            animatedReel.getReel().setSpinning(true);
            numberOfReelsSpinning++;
            animatedReel.getReel().addListener(new ReelTileListener() {
                @Override
                public void actionPerformed(ReelTileEvent event, ReelTile source) {
                    if (event instanceof ReelStoppedSpinningEvent) {
                        numberOfReelsSpinning--;
                        if (numberOfReelsSpinning<=0)
                            animatedScreenReels.clear();
                    }
                }
            });
            animatedScreenReels.add(animatedReel);
        }
   }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
        spinWheel.dispose();
    }

    public Stage getStage() {
        return stage;
    }
}
