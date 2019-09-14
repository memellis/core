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

package com.ellzone.slotpuzzle2d.prototypes.level.hiddenpattern;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimple;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static com.ellzone.slotpuzzle2d.level.TestLevelObjectorCreator.BUTTON_PROPERTY;
import static com.ellzone.slotpuzzle2d.level.TestLevelObjectorCreator.HEIGHT;
import static com.ellzone.slotpuzzle2d.level.TestLevelObjectorCreator.ID;
import static com.ellzone.slotpuzzle2d.level.TestLevelObjectorCreator.NAME;
import static com.ellzone.slotpuzzle2d.level.TestLevelObjectorCreator.ROTATION;
import static com.ellzone.slotpuzzle2d.level.TestLevelObjectorCreator.VISIBLE;
import static com.ellzone.slotpuzzle2d.level.TestLevelObjectorCreator.WIDTH;
import static com.ellzone.slotpuzzle2d.level.TestLevelObjectorCreator.X;
import static com.ellzone.slotpuzzle2d.level.TestLevelObjectorCreator.X_POS_FLOAT_PROPERTY;
import static com.ellzone.slotpuzzle2d.level.TestLevelObjectorCreator.Y;
import static com.ellzone.slotpuzzle2d.level.TestLevelObjectorCreator.Y_POS_FLOAT_PROPERTY;
import static com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimple.REELS_LAYER_NAME;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LevelCreatorSimple.class, Random.class})

public class TestLevelCreatorSimpleCreatedReplacementBoxes {
    public static final String CREATE_REPLACEMENT_REEL_BOXES_METHOD = "createReplacementReelBoxes";
    public static final String HIDDEN_PATTERN_FALLING_REELS_LEVEL_TYPE = "HiddenPatternFallingReels";
    public static final int GAME_LEVEL_WIDTH = 12;
    public static final int GAME_LEVEL_HEIGHT = 9;
    public static final float REEL_TILE_C_IS_0 = 160.0f;
    public static final float REEL_TILE_R_IS_0 = 40.0f;
    private LevelDoor levelDoorMock;
    private Array<AnimatedReel> animatedReels;
    private Array<ReelTile> reelTilesMock;
    private TiledMap levelMock;
    private MapLayers mapLayersMock;
    private AnnotationAssetManager annotationAssetManagerMock;
    private TextureAtlas cardDeckAtlasMock;
    private TweenManager tweenManagerMock;
    private PhysicsManagerCustomBodies physicsMock;
    private PlayStates playState;
    private MapLayer mapLayerMock;
    private MapObjects mapObjectsMock;
    private Array<RectangleMapObject> rectangleMapObjectsMock;
    private Application applicationMock;
    private Capture<String> debugCaptureArgument1, debugCaptureArgument2;
    private ReelTile reelTileMock;
    private Random randomMock;
    private Body bodyMock;
    private Array<Body> reelBoxes;
    private AnimatedReel animatedReelMock;

    @Before
    public void setUp() {
        setUpMocks();
    }

    @After
    public void tearDown() {
        tearDownMocks();
    }

    @Test
    public void testCreateReplacementBoxes_WithNoReelTiles() throws Exception {
        setUpExpectations();
        replayAll();
        LevelCreatorSimple levelCreatorSimple = new LevelCreatorSimple(
                levelDoorMock,
                animatedReels,
                reelTilesMock,
                levelMock,
                annotationAssetManagerMock,
                cardDeckAtlasMock,
                tweenManagerMock,
                 physicsMock,
                GAME_LEVEL_WIDTH,
                GAME_LEVEL_HEIGHT,
                playState);

        Whitebox.invokeMethod(
                levelCreatorSimple,
                CREATE_REPLACEMENT_REEL_BOXES_METHOD);
        verifyAll();
        assertThat(levelCreatorSimple.getReelTiles().size, is(equalTo(0)));
    }

    @Test
    public void testCreateReplacementBoxes_WithOneReelTile() throws Exception {
        setUpMocks();
        expectGetRectangleMapObjects();
        setUpGetMapProperties();
        setUpOneReelTile();
        replayAll();
        LevelCreatorSimple levelCreatorSimple = new LevelCreatorSimple(
                levelDoorMock,
                animatedReels,
                reelTilesMock,
                levelMock,
                annotationAssetManagerMock,
                cardDeckAtlasMock,
                tweenManagerMock,
                physicsMock,
                GAME_LEVEL_WIDTH,
                GAME_LEVEL_HEIGHT,
                playState);

        levelCreatorSimple.setReelBoxes(reelBoxes);
        levelCreatorSimple.setAnimatedReels(animatedReels);

        Whitebox.invokeMethod(
                levelCreatorSimple,
                CREATE_REPLACEMENT_REEL_BOXES_METHOD);

        assertThat(levelCreatorSimple.getReelBoxes().size, is(equalTo(1)));
        verifyAll();
    }

    private void setUpOneReelTile() {
        Whitebox.setInternalState(reelTileMock, "x", REEL_TILE_C_IS_0);
        Whitebox.setInternalState(reelTileMock, "y", 40.0f);
        reelTilesMock.add(reelTileMock);
        setUpRandomMock();
        setUpOneExpectations();
    }

    private void setUpRandomMock() {
        PowerMock.mockStatic(Random.class);
    }

    private void setUpOneExpectations() {
        populateMatchGridExpectations();
        applicationMock.debug(capture(debugCaptureArgument1), capture(debugCaptureArgument2));
        expectCheckLevelDebug();
        populateMatchGridExpectations();
        expectCheckLevelDebug();
        populateMatchGridExpectations();
        expect(reelTileMock.getDestinationX()).andReturn(REEL_TILE_C_IS_0);
        expect(reelTileMock.getDestinationY()).andReturn(REEL_TILE_R_IS_0);
        createReplacementBoxesExpectations();
    }

    private void createReplacementBoxesExpectations() {
        filterExpectations();
        updateReplacementReelTileExpectations();
        replacementReelBodyExpectations();
        updateReplacementAnimatedReelExpectations();
    }

    private void updateReplacementReelTileExpectations() {
        reelTileMock.unDeleteReelTile();
        reelTileMock.setScale(1.0f);
        Color color = new Color();
        expect(reelTileMock.getColor()).andReturn(color);
        reelTileMock.setColor(color);
        expect(reelTileMock.getNumberOfReelsInTexture()).andReturn(8);
        expectRandomNextInt();
        reelTileMock.resetReel();
    }
    private void replacementReelBodyExpectations() {
        expect(physicsMock.createBoxBody(
                BodyDef.BodyType.DynamicBody,
                REEL_TILE_C_IS_0 + 20,
                REEL_TILE_R_IS_0 + 360,
                19.0f,
                19.0f,
                true)).andReturn(bodyMock);
        bodyMock.setUserData(reelTileMock);
        reelBoxes = new Array<>();
        reelBoxes.add(bodyMock);
    }

    private void updateReplacementAnimatedReelExpectations() {
        animatedReels.add(animatedReelMock);
        animatedReelMock.reinitialise();
    }

    private void filterExpectations() {
        expect(reelTileMock.isReelTileDeleted()).andReturn(true);
        expect(reelTileMock.getIndex()).andReturn(0);
    }

    private void populateMatchGridExpectations() {
        expect(reelTileMock.isReelTileDeleted()).andReturn(true);
        expect(reelTileMock.getDestinationX()).andReturn(REEL_TILE_C_IS_0).times(2);
        expect(reelTileMock.getDestinationY()).andReturn(REEL_TILE_R_IS_0).times(2);
        expect(reelTileMock.getX()).andReturn(REEL_TILE_C_IS_0);
        expect(reelTileMock.getY()).andReturn(REEL_TILE_R_IS_0);
        expect(reelTileMock.getEndReel()).andReturn(0);
    }

    private void expectRandomNextInt() {
        expect(Random.getInstance()).andReturn(randomMock).times(2);
        expect(randomMock.nextInt(1)).andReturn(0);
        expect(randomMock.nextInt(7)).andReturn(1);
        reelTileMock.setEndReel(1);
    }

    private void setUpMocks() {
        setUpLevelCreatorArgumentMocks();
        setUpLevelCreatorMocks();
        mockGdx();
        setUpCaptureArguments();
    }

    private void setUpLevelCreatorArgumentMocks() {
        levelDoorMock = createMock(LevelDoor.class);
        animatedReels = new Array<>();
        reelTilesMock = new Array<>();
        levelMock = createMock(TiledMap.class);
        annotationAssetManagerMock = createMock(AnnotationAssetManager.class);
        cardDeckAtlasMock = createMock(TextureAtlas.class);
        tweenManagerMock = createMock(TweenManager.class);
        physicsMock = createMock(PhysicsManagerCustomBodies.class);
        playState = PlayStates.REELS_SPINNING;
        reelTileMock = createMock(ReelTile.class);
        randomMock = createMock(Random.class);
        bodyMock = createMock(Body.class);
        animatedReelMock = createMock(AnimatedReel.class);
    }

    private void setUpLevelCreatorMocks() {
        mapLayersMock = createMock(MapLayers.class);
        mapLayerMock = createMock(MapLayer.class);
        mapObjectsMock = createMock(MapObjects.class);
        rectangleMapObjectsMock = new Array<>();
        applicationMock = createMock(Application.class);
    }

    private void mockGdx() {
        Gdx.app = applicationMock;
    }

    private void setUpCaptureArguments() {
        debugCaptureArgument1 = EasyMock.newCapture();
        debugCaptureArgument2 = EasyMock.newCapture();
    }

    private void setUpExpectations() {
        expectGetRectangleMapObjects();
        expectCheckLevelDebug();
        expectCheckLevelDebug();
        setUpGetMapProperties();
    }

    private void expectCheckLevelDebug() {
        for (int r = 0; r < GAME_LEVEL_HEIGHT; r++)
            for (int c = 0; c < GAME_LEVEL_WIDTH; c++)
                applicationMock.debug(capture(debugCaptureArgument1), capture(debugCaptureArgument2));
    }

    private void expectGetRectangleMapObjects() {
        expect(levelDoorMock.getLevelType()).andReturn(HIDDEN_PATTERN_FALLING_REELS_LEVEL_TYPE);
        expect(levelMock.getLayers()).andReturn(mapLayersMock);
        expect(mapLayersMock.get(REELS_LAYER_NAME)).andReturn(mapLayerMock);
        expect(mapLayerMock.getObjects()).andReturn(mapObjectsMock);
        expect(mapObjectsMock.getByType(RectangleMapObject.class)).andReturn(rectangleMapObjectsMock);
    }

    private void setUpGetMapProperties() {
        MapProperties mapProperties = getMapProperties();
        expect(levelMock.getProperties()).andReturn(mapProperties);
    }

    private MapProperties getMapProperties() {
        MapProperties mapProperties1 = new MapProperties();
        mapProperties1.put(ID, 148);
        mapProperties1.put(NAME, BUTTON_PROPERTY);
        mapProperties1.put(VISIBLE, true);
        mapProperties1.put(X, X_POS_FLOAT_PROPERTY);
        mapProperties1.put(Y, Y_POS_FLOAT_PROPERTY);
        mapProperties1.put(WIDTH, GAME_LEVEL_WIDTH);
        mapProperties1.put(HEIGHT, GAME_LEVEL_WIDTH);
        mapProperties1.put(ROTATION, 0f);
        return mapProperties1;
    }

    private void tearDownMocks() {
        tearDownLevelCreatorArgumentMocks();
        tearDownLevelCreatorMocks();
        tearDownMockGdx();
        tearDownCaptureArguments();
    }

    private void tearDownLevelCreatorArgumentMocks() {
        levelDoorMock = null;
        animatedReels = null;
        reelTilesMock = null;
        levelMock = null;
        annotationAssetManagerMock = null;
        cardDeckAtlasMock = null;
        tweenManagerMock = null;
        physicsMock = null;
        playState = null;
        reelTileMock = null;
        randomMock = null;
        bodyMock = null;
        animatedReelMock = null;
    }

    private void tearDownLevelCreatorMocks() {
        mapLayersMock = createMock(MapLayers.class);
        mapLayerMock = createMock(MapLayer.class);
        mapObjectsMock = createMock(MapObjects.class);
        rectangleMapObjectsMock = new Array<>();
        applicationMock = createMock(Application.class);
    }

    private void tearDownMockGdx() {
        Gdx.app = null;
    }

    private void tearDownCaptureArguments() {
        debugCaptureArgument1 = EasyMock.newCapture();
        debugCaptureArgument2 = EasyMock.newCapture();
    }
}
