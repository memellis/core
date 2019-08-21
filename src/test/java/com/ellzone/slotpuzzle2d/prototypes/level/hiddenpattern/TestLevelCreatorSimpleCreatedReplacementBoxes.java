package com.ellzone.slotpuzzle2d.prototypes.level.hiddenpattern;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimple;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
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
@PrepareForTest(LevelCreatorSimple.class)

public class TestLevelCreatorSimpleCreatedReplacementBoxes {
    public static final String CREATE_REPLACEMENT_REEL_BOXES_METHOD = "createReplacementReelBoxes";
    public static final String HIDDEN_PATTERN_FALLING_REELS_LEVEL_TYPE = "HiddenPatternFallingReels";
    public static final int GAME_LEVEL_WIDTH = 12;
    public static final int GAME_LEVEL_HEIGHT = 9;
    private LevelDoor levelDoorMock;
    private Array<AnimatedReel> animatedReelsMock;
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

    @Test
    public void testCreateReplacementBoxes_WithNoReelTiles() throws Exception {
        setUp();
        replayAll();
        LevelCreatorSimple levelCreatorSimple = new LevelCreatorSimple(
                levelDoorMock,
                animatedReelsMock,
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

    private void setUp() {
        setUpMocks();
        setUpExpectations();
    }

    private void setUpMocks() {
        setUpLevelCreatorArgumentMocks();
        setUpLevelCreatorMocks();
        mockGdx();
        setUpCaptureArguments();
    }

    private void setUpLevelCreatorArgumentMocks() {
        levelDoorMock = createMock(LevelDoor.class);
        animatedReelsMock = new Array<>();
        reelTilesMock = new Array<>();
        levelMock = createMock(TiledMap.class);
        annotationAssetManagerMock = createMock(AnnotationAssetManager.class);
        cardDeckAtlasMock = createMock(TextureAtlas.class);
        tweenManagerMock = createMock(TweenManager.class);
        physicsMock = createMock(PhysicsManagerCustomBodies.class);
        playState = PlayStates.REELS_SPINNING;
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
        expectMatrixDebug();
        expectMatrixDebug();
        setUpGetMapProperties();
    }

    private void expectMatrixDebug() {
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

}
