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

package com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimpleScenario;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.score.Score;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeSimpleScenario.class} )

public class TestMiniSlotMachineLevelPrototypeSimpleScenarioRenderOverride {
    private static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    private static final String TILE_MAP_RENDERER_FIELD_NAME = "tileMapRenderer";
    private static final String BATCH_FIELD_NAME = "batch";
    private static final String LEVEL_DOOR_FIELD_NAME = "levelDoor";
    private static final String LEVEL_CREATOR_FIELD_NAME = "levelCreator";
    private static final String VIEWPORT_FIELD_NAME = "viewport";
    private static final String REEL_BOXES_FIELD_NAME = "reelBoxes";
    private static final String ANIMATED_REELS_FIELD_NAME = "animatedReels";
    private static final String PHYSICS_FIELD_NAME = "physics";
    private static final String HUD_FIELD_NAME = "hud";
    private static final String STAGE_FIELD_NAME = "stage";
    private static final String SHAPE_RENDERER_FIELD_NAME = "shapeRenderer";
    private static final String CAMERA_FIELD_NAME = "camera";

    private MiniSlotMachineLevelPrototypeSimpleScenario partialMockMiniSlotMachineLevelPrototypeSimpleScenario;
    private LevelCreatorSimpleScenario levelCreatorSimpleScenarioMock;
    private OrthogonalTiledMapRenderer tileMapRendererMock;
    private SpriteBatch batchMock;
    private LevelDoor levelDoorMock;
    private FitViewport viewportMock;
    private OrthographicCamera cameraMock;
    private Array<Body> reelBoxesMock;
    private Body reelBoxMock;
    private Array animatedReelsMock;
    private AnimatedReel animatedReelMock;
    private ReelTile reelTileMock;
    private Vector2 vector2Mock;
    private PhysicsManagerCustomBodies physicsMock;
    private Hud hudMock;
    private Stage stageMock;
    private ShapeRenderer shapeRendererMock;

    @Before
    public void setUp() {
        setUpPowerMocks();
        setUpEasyMocks();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario = PowerMock.createNicePartialMock(MiniSlotMachineLevelPrototypeSimpleScenario.class,
                "handleInput",
                              "drawPlayingCards");
    }

    private void setUpEasyMocks() {
        setUpLibGDXMocks();
        setUpReelMocks();
        setUpLevelMocks();
        setUpSlotPuzzleMocks();
    }

    private void setUpSlotPuzzleMocks() {
        vector2Mock = createMock(Vector2.class);
        physicsMock = createMock(PhysicsManagerCustomBodies.class);
        hudMock = createMock(Hud.class);
    }

    private void setUpLevelMocks() {
        levelDoorMock = createMock(LevelDoor.class);
        levelCreatorSimpleScenarioMock = createMock(LevelCreatorSimpleScenario.class);
        tileMapRendererMock = createMock(OrthogonalTiledMapRenderer.class);
    }

    private void setUpReelMocks() {
        reelBoxMock = createMock(Body.class);
        reelBoxesMock = new Array<>();
        reelBoxesMock.add(reelBoxMock);
        animatedReelsMock = new Array<AnimatedReel>();
        animatedReelMock = createMock(AnimatedReel.class);
        reelTileMock = createMock(ReelTile.class);
        shapeRendererMock = createMock(ShapeRenderer.class);
    }

    private void setUpLibGDXMocks() {
        batchMock = createMock(SpriteBatch.class);
        viewportMock = createMock(FitViewport.class);
        cameraMock = createMock(OrthographicCamera.class);
        stageMock = createMock(Stage.class);
    }

    @After
    public void tearDown() {
        tearDownPowerMocks();
        tearDownEasyMocks();
     }

    private void tearDownPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario = null;
    }

    private void tearDownEasyMocks() {
        tearDownLibGDXMocks();
        tearDownReelMocks();
        tearDownLevelMocks();
        tearDownSlotPuzzleMocks();
    }

    private void tearDownSlotPuzzleMocks() {
        vector2Mock = null;
        physicsMock = null;
        hudMock = null;
    }

    private void tearDownLevelMocks() {
        levelDoorMock = null;
        levelCreatorSimpleScenarioMock = null;
        tileMapRendererMock = null;
    }

    private void tearDownReelMocks() {
        reelBoxMock = null;
        reelBoxesMock = null;
        animatedReelsMock = null;
        animatedReelMock = null;
        reelTileMock = null;
    }

    private void tearDownLibGDXMocks() {
        batchMock = null;
        viewportMock = null;
        cameraMock = null;
        stageMock = null;
    }

    @Test
    public void testUpRenderOverride() {
        setFields();
        setUpExpectations();
        replayAll();
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario.renderOverride(0.0f);
        verifyAll();
    }

    private void setFields() {
        setLibGDXFields();
        setLevelFields();
        setSlotPuzzleFields();
    }

    private void setSlotPuzzleFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, PHYSICS_FIELD_NAME, physicsMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, HUD_FIELD_NAME, hudMock);
    }

    private void setLevelFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, TILE_MAP_RENDERER_FIELD_NAME, tileMapRendererMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, LEVEL_DOOR_FIELD_NAME, levelDoorMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, LEVEL_CREATOR_FIELD_NAME, levelCreatorSimpleScenarioMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, REEL_BOXES_FIELD_NAME, reelBoxesMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, ANIMATED_REELS_FIELD_NAME, animatedReelsMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, SHAPE_RENDERER_FIELD_NAME, shapeRendererMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, CAMERA_FIELD_NAME, cameraMock);
    }

    private void setLibGDXFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, BATCH_FIELD_NAME, batchMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, STAGE_FIELD_NAME, stageMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, VIEWPORT_FIELD_NAME, viewportMock);
    }

    private void setUpExpectations() {
        setUpExpectationsRenderOverridePart1();
        setUpExpectationsRenderReelsBoxes();
        setUpExpectationsRenderOverridePart2();
    }

    private void setUpExpectationsRenderOverridePart1() {
        tileMapRendererMock.render();
        batchMock.begin();
        expect(levelDoorMock.getLevelType()).andReturn(PLAYING_CARD_LEVEL_TYPE);
        expect(levelCreatorSimpleScenarioMock.getScores()).andReturn(new Array<Score>());
        expect(viewportMock.getCamera()).andReturn(cameraMock);
        expect(reelBoxMock.getAngle()).andReturn(1.0f);
        Whitebox.setInternalState(cameraMock,"combined", new Matrix4());
        shapeRendererMock.setProjectionMatrix(cameraMock.combined);
    }

    private void setUpExpectationsRenderOverridePart2() {
        hudMock.stage = stageMock;
        expect(stageMock.getCamera()).andReturn(cameraMock);
        hudMock.stage.draw();
        stageMock.draw();
    }

        private void setUpExpectationsRenderReelsBoxes() {
        setUpExpectationsRenderReelsBoxesPart1();
        setUpExpectationsRenderReelBoxesPart2();
    }

    private void setUpExpectationsRenderReelBoxesPart2() {
        reelTileMock.setPosition(-20.0f, -20.0f);
        reelTileMock.setOrigin(0, 0);
        reelTileMock.setSize(40, 40);
        reelTileMock.setRotation(MathUtils.radiansToDegrees);
        reelTileMock.draw(batchMock);
        physicsMock.draw(batchMock);
    }

    private void setUpExpectationsRenderReelsBoxesPart1() {
        animatedReelsMock.add(animatedReelMock);
        expect(animatedReelMock.getReel()).andReturn(reelTileMock).times(3);
        expect(reelBoxMock.getPosition()).andReturn(vector2Mock).atLeastOnce();
        expect(reelTileMock.isReelTileDeleted()).andReturn(false).times(2);
        expect(reelTileMock.getFlashState()).andReturn(ReelTile.FlashState.FLASH_OFF);
    }

    private void replayAll() {
        replay(tileMapRendererMock,
               levelDoorMock,
               levelCreatorSimpleScenarioMock,
               viewportMock,
               cameraMock,
               reelBoxMock,
               animatedReelMock,
               reelTileMock,
               shapeRendererMock,
               hudMock,
               stageMock);
    }

    private void verifyAll() {
        verify(tileMapRendererMock,
               levelDoorMock,
               levelCreatorSimpleScenarioMock,
               viewportMock,
               cameraMock,
               reelBoxMock,
               animatedReelMock,
               reelTileMock,
               shapeRendererMock,
               hudMock,
               stageMock);
    }
}
