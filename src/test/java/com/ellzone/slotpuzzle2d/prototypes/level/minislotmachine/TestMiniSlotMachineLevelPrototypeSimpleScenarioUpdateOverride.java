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
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimpleScenario;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;

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
@PrepareForTest( {MiniSlotMachineLevelPrototypeSimpleScenario.class, Hud.class} )

public class TestMiniSlotMachineLevelPrototypeSimpleScenarioUpdateOverride {
    private static final String TWEEN_MANAGER_FIELD_NAME = "tweenManager";
    private static final String LEVEL_CREATOR_FIELD_NAME = "levelCreator";
    private static final String TILE_MAP_RENDERER_FIELD_NAME = "tileMapRenderer";
    private static final String ORTHOGRAPHIC_CAMERA_FIELD_NAME = "orthographicCamera";
    private static final String HUD_FIELD_NAME = "hud";
    private static final String IN_RESTART_LEVEL_FIELD_NAME = "inRestartLevel";
    private static final String GAME_OVER_FIELD_NAME = "gameOver";

    private MiniSlotMachineLevelPrototypeSimpleScenario partialMockMiniSlotMachineLevelPrototypeSimpleScenario;
    private LevelCreatorSimpleScenario levelCreatorSimpleScenarioMock;
    private TweenManager tweenManagerMock;
    private OrthogonalTiledMapRenderer tileMapRendererMock;
    private OrthographicCamera orthographicCameraMock;
    private Hud hudMock;

    @Before
    public void setUp() {
        setUpMocks();
    }

    private void setUpMocks() {
        setUpPowerMocks();
        setUpEasyMock();
    }

    private void setUpEasyMock() {
        levelCreatorSimpleScenarioMock = createMock(LevelCreatorSimpleScenario.class);
        tweenManagerMock = createMock(TweenManager.class);
        tileMapRendererMock = createMock(OrthogonalTiledMapRenderer.class);
        orthographicCameraMock = createMock(OrthographicCamera.class);
        hudMock = createMock(Hud.class);
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario = PowerMock.createNicePartialMock(MiniSlotMachineLevelPrototypeSimpleScenario.class,
                "initialiseOverride");
    }

    @After
    public void tearDown() {
        tearDownEasyMocks();
        tearDownPowerMocks();
    }

    private void tearDownEasyMocks() {
        levelCreatorSimpleScenarioMock = null;
        tweenManagerMock = null;
        tileMapRendererMock = null;
        orthographicCameraMock = null;
        hudMock = null;
    }

    private void tearDownPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario = null;
    }

    @Test
    public void testUpdateOverride() {
        setFields();
        setExpectations();
        replayAll();
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario.updateOverride(0.0f);
        verifyAll();
    }

    private void setFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, TWEEN_MANAGER_FIELD_NAME, tweenManagerMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, LEVEL_CREATOR_FIELD_NAME, levelCreatorSimpleScenarioMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, TILE_MAP_RENDERER_FIELD_NAME, tileMapRendererMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, ORTHOGRAPHIC_CAMERA_FIELD_NAME, orthographicCameraMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, HUD_FIELD_NAME, hudMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, IN_RESTART_LEVEL_FIELD_NAME, false);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, GAME_OVER_FIELD_NAME, false);
    }

    private void setExpectations() {
        tweenManagerMock.update(0.0f);
        levelCreatorSimpleScenarioMock.update(0.0f);
        tileMapRendererMock.setView(orthographicCameraMock);
        hudMock.update(0.0f);
        expect(hudMock.getWorldTime()).andReturn(0);
        expect(hudMock.getLives()).andReturn(1);
        levelCreatorSimpleScenarioMock.setPlayState(PlayStates.LEVEL_LOST);
        expect(levelCreatorSimpleScenarioMock.getPlayState()).andReturn(PlayStates.LEVEL_LOST);
    }

    private void replayAll() {
        replay(tweenManagerMock,
                levelCreatorSimpleScenarioMock,
                tileMapRendererMock,
                orthographicCameraMock,
                hudMock);
    }
    
    private void verifyAll() {
        verify(tweenManagerMock,
                levelCreatorSimpleScenarioMock,
                tileMapRendererMock,
                orthographicCameraMock,
                hudMock);
    }
}
