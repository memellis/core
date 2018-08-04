/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes.minislotmachine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGrid;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.LightButton;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.sprites.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import java.util.Random;

import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import box2dLight.PointLight;
import box2dLight.RayHandler;

public class SpinningSlotsWithMatchesWin extends SPPrototypeTemplate {
    private Random random;
    private Array<AnimatedReel> reels;
    private Sound pullLeverSound, reelSpinningSound, reelStoppingSound;
    private Vector2 touch;
    private TextureAtlas slotHandleAtlas;
    private int reelSpriteHelp;
    private SlotHandleSprite slotHandleSprite;
    private static final float PIXELS_PER_METER = 100;
    private Viewport lightViewport, hudViewport;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private RayHandler rayHandler;
    private Array<LightButton> lightButtons;
    private Array<PointLight> levelLights;
    private int[][] reelGrid = new int[3][3];
    private int levelLightX, levelLightY;
    private Array<Array<Vector2>> rowMacthesToDraw;
    private ShapeRenderer shapeRenderer;

    @Override
    protected void initialiseOverride() {
        touch = new Vector2();
        shapeRenderer = new ShapeRenderer();
        rowMacthesToDraw = new Array<Array<Vector2>>();
        createHoldButtons();
    }

    @Override
    protected void initialiseScreenOverride() {
    }

    private void createHoldButtons() {
        lightViewport = new FitViewport(SlotPuzzleConstants.V_WIDTH / PIXELS_PER_METER, SlotPuzzleConstants.V_HEIGHT / PIXELS_PER_METER);
        lightViewport.getCamera().position.set(lightViewport.getCamera().position.x + SlotPuzzleConstants.V_WIDTH / PIXELS_PER_METER * 0.5f,
                lightViewport.getCamera().position.y + SlotPuzzleConstants.V_HEIGHT / PIXELS_PER_METER * 0.5f,
                0);
        lightViewport.getCamera().update();
        hudViewport = new FitViewport(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT, new OrthographicCamera());

        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        rayHandler = new RayHandler(world);
        rayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.2f, 0.2f, 0.2f, 0.25f);

        PointLight reelLight = new PointLight(rayHandler, 32);
        reelLight.setActive(true);
        reelLight.setColor(Color.WHITE);
        reelLight.setDistance(2.0f);
        reelLight.setPosition(SlotPuzzleConstants.V_WIDTH / ( PIXELS_PER_METER * 2), (SlotPuzzleConstants.V_HEIGHT + 96) / (PIXELS_PER_METER * 2));

        PointLight handleLight = new PointLight(rayHandler, 32);
        handleLight.setActive(true);
        handleLight.setColor(Color.WHITE);
        handleLight.setDistance(1.5f);
        Rectangle slotHandleSprintBoundingRectangle = slotHandleSprite.getBoundingRectangle();
        float slotHandleSpriteCenterX = slotHandleSprintBoundingRectangle.getX() + slotHandleSprintBoundingRectangle.getWidth() / 2;
        float slotHandleSpriteCenterY = slotHandleSprintBoundingRectangle.getY() + slotHandleSprintBoundingRectangle.getHeight() / 2;
        handleLight.setPosition(slotHandleSpriteCenterX / PIXELS_PER_METER, slotHandleSpriteCenterY / PIXELS_PER_METER);
        
        PointLight reelHelperLight = new PointLight(rayHandler, 32);
        reelHelperLight.setActive(true);
        reelHelperLight.setColor(Color.RED);
        reelHelperLight.setDistance(1.0f);
        reelHelperLight.setPosition(48 / PIXELS_PER_METER,  (sprites[0].getY() + 16) / PIXELS_PER_METER);

        lightButtons = new Array<>();
        for (int i = 0; i < 3; i++) {
            LightButton lightButton = new LightButton(world, rayHandler, i * 32 / PIXELS_PER_METER + SlotPuzzleConstants.V_WIDTH / (PIXELS_PER_METER * 2) - (3 * 32 / PIXELS_PER_METER) / 2, SlotPuzzleConstants.V_HEIGHT / (PIXELS_PER_METER * 4), 32, 32, new BitmapFont(), "", "Hold");
            lightButton.getSprite().setSize(32 / PIXELS_PER_METER, 32 / PIXELS_PER_METER);
            lightButtons.add(lightButton);
        }
        
        levelLights = new Array<>();
        System.out.println("slotHandleSpriteCenterY="+slotHandleSpriteCenterY);
        //levelLights.add(createLevelLight((int) slotHandleSpriteCenterX, (int) slotHandleSpriteCenterY));
        //levelLights.add(createLevelLight((int) slotHandleSpriteCenterX, (int) slotHandleSpriteCenterY + 120));
        levelLights.add(createLevelLight((int) slotHandleSpriteCenterX, 300));
        levelLightX = (int) slotHandleSpriteCenterX;
        levelLightY = (int) 300;
    }

    private PointLight createLevelLight(int x, int y) {
        PointLight levelLight = new PointLight(rayHandler,4);
        levelLight.setActive(true);
        levelLight.setColor(Color.GRAY);
        levelLight.setPosition(x / PIXELS_PER_METER, y / PIXELS_PER_METER);
        return levelLight;
    }

    @Override
    protected void loadAssetsOverride() {
        slotHandleAtlas = annotationAssetManager.get(AssetsAnnotation.SLOT_HANDLE);
        pullLeverSound = annotationAssetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
        reelSpinningSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
        reelStoppingSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
     }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        lightViewport.update(width, height);
        hudViewport.update(width, height);
    }

    @Override
    protected void disposeOverride() {
        debugRenderer.dispose();
        rayHandler.dispose();
        world.dispose();
    }

    @Override
    protected void updateOverride(float dt) {
        handleInput();
        tweenManager.update(dt);
        for (AnimatedReel reel : reels) {
            reel.update(dt);
        }
    }

    @Override
    protected void renderOverride(float dt) {
        renderReels();
        if (rowMacthesToDraw.size > 0)
            renderMacthedRows();
        renderLightButtons();
        renderRayHandler();
        renderWorld();
    }

    private void renderMacthedRows() {
        batch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);

        for (Array<Vector2> matchedRow : rowMacthesToDraw) {
            if (matchedRow.size >= 2)
                for (int i = 0; i < matchedRow.size - 1; i++) {
                    shapeRenderer.rectLine(matchedRow.get(i).x, matchedRow.get(i).y, matchedRow.get(i + 1).x, matchedRow.get(i + 1).y, 2);
                }
        }
        shapeRenderer.end();
        batch.end();
    }

    private void renderWorld() {
        debugRenderer.render(world, lightViewport.getCamera().combined);
    }

    private void renderRayHandler() {
        rayHandler.setCombinedMatrix(lightViewport.getCamera().combined);
        rayHandler.updateAndRender();
    }

    private void renderLightButtons() {
        batch.setProjectionMatrix(lightViewport.getCamera().combined);
        batch.begin();
        for (LightButton lightButton : lightButtons) {
            lightButton.getSprite().draw(batch);
        }
        batch.end();
    }

    private void renderReels() {
        batch.begin();
        for (AnimatedReel reel : reels) {
            reel.draw(batch);
            sprites[reelSpriteHelp].setX(32);
            sprites[reelSpriteHelp].draw(batch);
        }
        slotHandleSprite.draw(batch);
        batch.end();
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        initialiseReelSlots();
        createIntroSequence();
        slotHandleSprite = new SlotHandleSprite(slotHandleAtlas, tweenManager);
    }

    private void initialiseReelSlots() {
        random = new Random();
        reels = new Array<>();
        Pixmap slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        Texture slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        for (int i = 0; i < 3; i++) {
            addReel(slotReelScrollTexture, i);
        }
    }

    private void addReel(Texture slotReelScrollTexture, int i) {
        AnimatedReel animatedReel = new AnimatedReel(slotReelScrollTexture, 0, 0, spriteWidth, spriteHeight, spriteWidth, spriteHeight * 3, 0, reelSpinningSound, reelStoppingSound, tweenManager);
        animatedReel.setX(i * spriteWidth + displayWindowWidth / 2);
        animatedReel.setY((displayWindowHeight + 3 * spriteHeight) / 2);
        animatedReel.setSx(0);
        animatedReel.setEndReel(random.nextInt(sprites.length - 1));
        animatedReel.getReel().addListener(new ReelStoppedListener().invoke());
        reels.add(animatedReel);
    }

    private void createIntroSequence() {
        Timeline introSequence = Timeline.createParallel();
        for(int i=0; i < reels.size; i++) {
            introSequence = introSequence
                    .push(buildSequence(reels.get(i).getReel(), i, random.nextFloat() * 5.0f, random.nextFloat() * 5.0f, reels.size));
        }

        introSequence
                .pushPause(0.3f)
                .start(tweenManager);
    }

    private Timeline buildSequence(Sprite target, int id, float delay1, float delay2, int numberOfSprites) {
        Vector2 targetXY = getRandomCorner();
        int targetPositionX = (id * spriteWidth) + (displayWindowWidth - (((spriteWidth * numberOfSprites) +  displayWindowWidth) / 2));
        return Timeline.createSequence()
                .push(SlotPuzzleTween.set(target, SpriteAccessor.POS_XY).target(targetXY.x, targetXY.y))
                .push(SlotPuzzleTween.set(target, SpriteAccessor.SCALE_XY).target(30, 30))
                .push(SlotPuzzleTween.set(target, SpriteAccessor.ROTATION).target(0))
                .push(SlotPuzzleTween.set(target, SpriteAccessor.OPACITY).target(0))
                .pushPause(delay1)
                .beginParallel()
                .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
                .end()
                .pushPause(-0.5f)
                .push(SlotPuzzleTween.to(target, SpriteAccessor.POS_XY, 1.0f).target(targetPositionX, displayWindowHeight / 2                                                                                         ).ease(Back.OUT))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.ROTATION, 0.8f).target(360).ease(Cubic.INOUT))
                .pushPause(delay2)
                .beginParallel()
                .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 0.3f).target(3, 3).ease(Quad.IN))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 0.3f).target(0).ease(Quad.IN))
                .end()
                .pushPause(-0.5f)
                .beginParallel()
                .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
                .end();
    }

    private Vector2 getRandomCorner() {
        int randomCorner = random.nextInt(4);
        switch (randomCorner) {
            case 0:
                return new Vector2(-1 * random.nextFloat(), -1 * random.nextFloat());
            case 1:
                return new Vector2(-1 * random.nextFloat(), displayWindowWidth + random.nextFloat());
            case 2:
                return new Vector2(displayWindowWidth + random.nextFloat(), -1 * random.nextFloat());
            case 3:
                return new Vector2(displayWindowWidth + random.nextFloat(), displayWindowWidth + random.nextFloat());
            default:
                return new Vector2(-0.5f, -0.5f);
        }
    }

    public void handleInput() {
        if (Gdx.input.justTouched()) {
            touch = touch.set(Gdx.input.getX(), Gdx.input.getY());
            touch = viewport.unproject(touch);
            handleReelsTouched();
            handleSlotHandleIsTouch();
            touch = touch.set(Gdx.input.getX(), Gdx.input.getY());
            touch = lightViewport.unproject(touch);
            handleLightButtonTouched();
        }
    }

    private void handleLightButtonTouched() {
        for (LightButton lightButton : lightButtons) {
            if (lightButton.getSprite().getBoundingRectangle().contains(touch.x, touch.y)) {
                if (lightButton.getLight().isActive()) {
                    lightButton.getLight().setActive(false);
                } else {
                    lightButton.getLight().setActive(true);
                }
            }
        }
    }

    private void handleSlotHandleIsTouch() {
        if (slotHandleSprite.getBoundingRectangle().contains(touch)) {
            if (isReelsNotSpinning()) {
                slotHandlePulled();
            } else {
                reelStoppingSound.play();
            }
        }
    }

    private void slotHandlePulled() {
        slotHandleSprite.pullSlotHandle();
        pullLeverSound.play();
        clearRowMatchesToDraw();
        int i = 0;
        for (AnimatedReel animatedReel : reels) {
            if (!lightButtons.get(i).getLight().isActive()) {
                animatedReel.setEndReel(random.nextInt(sprites.length - 1));
                animatedReel.reinitialise();
                animatedReel.getReel().startSpinning();
            }
            i++;
        }
    }

    private void clearRowMatchesToDraw() {
        if (rowMacthesToDraw.size > 0)
            rowMacthesToDraw.removeRange(0, rowMacthesToDraw.size - 1);
    }

    private boolean isReelsNotSpinning() {
        boolean reelsNotSpinning = true;
        for (AnimatedReel animatedReel : reels) {
            if (animatedReel.getReel().isSpinning()) {
                reelsNotSpinning = false;
            }
        }
        return reelsNotSpinning;
    }

    private void handleReelsTouched() {
        for (AnimatedReel animatedReel : reels) {
            if (animatedReel.getReel().getBoundingRectangle().contains(touch)) {
                if (animatedReel.getReel().isSpinning()) {
                    if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                        reelSpriteHelp = animatedReel.getReel().getCurrentReel();
                        animatedReel.getReel().setEndReel(reelSpriteHelp - 1 < 0 ? 0 : reelSpriteHelp - 1);
                    }
                } else {
                    animatedReel.setEndReel(random.nextInt(sprites.length - 1));
                    animatedReel.reinitialise();
                    animatedReel.getReel().startSpinning();
                }
            }
        }
    }

    private class ReelStoppedListener {
        public ReelTileListener invoke() {
            return new ReelTileListener() {
                @Override
                public void actionPerformed(ReelTileEvent event, ReelTile source) {
                    if (event instanceof ReelStoppedSpinningEvent) {
                        System.out.println("Reel Stopped Spinning");
                        matchReels();
                    }
                }
            };
        }
    }

    private void matchReels() {
        captureReelPositions();
        PuzzleGridTypeReelTile puzzleGrid = new PuzzleGridTypeReelTile();
        ReelTileGridValue[][] matchGrid = puzzleGrid.populateMatchGrid(reelGrid);
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        matchGrid = puzzleGridTypeReelTile.createGridLinks(matchGrid);
        matchRowsToDraw(matchGrid, puzzleGridTypeReelTile);
    }

    private void matchRowsToDraw(ReelTileGridValue[][] matchGrid, PuzzleGridTypeReelTile puzzleGridTypeReelTile) {
        rowMacthesToDraw = new Array<Array<Vector2>>();
        for (int row = 0; row < matchGrid.length; row++) {
            Array<ReelTileGridValue> depthSearchResults = puzzleGridTypeReelTile.depthFirstSearchIncludeDiagonals(matchGrid[row][0]);
            if (puzzleGridTypeReelTile.isRow(depthSearchResults, matchGrid)) {
                rowMacthesToDraw.add(drawMatches(depthSearchResults, 580, 510));
            };
        }
    }

    private Array<Vector2> drawMatches(Array<ReelTileGridValue> depthSearchResults, int startX, int startY) {
        Array<Vector2> points = new Array<Vector2>();
        for (ReelTileGridValue cell : depthSearchResults) {
            points.add(new Vector2(startX + cell.c * 60, startY - cell.r * 60 ));
        }
        return points;
    }

    private void captureReelPositions() {
        for (int r = 0; r < reelGrid.length; r++) {
            for (int c = 0; c < reelGrid[0].length; c++) {
                reelGrid[r][c] = getReelPosition(r, c);
            }
        }
    }

    private int getReelPosition(int r, int c) {
        int reelPosition = reels.get(c).getEndReel() + r;
        if (reelPosition < 0)
            reelPosition = sprites.length - 1;
        else {
            if(reelPosition > sprites.length - 1) {
                reelPosition = 0;
            }
        }
        return reelPosition;
    }
}
