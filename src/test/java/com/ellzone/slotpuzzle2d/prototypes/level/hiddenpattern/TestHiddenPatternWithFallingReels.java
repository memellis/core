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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.card.Suit;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreator;
import com.ellzone.slotpuzzle2d.level.creator.LevelObjectCreatorEntityHolder;
import com.ellzone.slotpuzzle2d.level.hidden.HiddenPlayingCard;
import com.ellzone.slotpuzzle2d.level.hidden.Pip;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimple.HIDDEN_PATTERN_LAYER_NAME;
import static com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimple.PLAYING_CARD_LEVEL_TYPE;
import static com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimple.REELS_LAYER_NAME;
import static com.ellzone.slotpuzzle2d.prototypes.level.hiddenpattern.HiddenPatternWithFallingReels.LEVELS_LEVEL_7;
import static com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.MiniSlotMachineLevelPrototypeSimpleScenario.HEIGHT_KEY;
import static com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.MiniSlotMachineLevelPrototypeSimpleScenario.WIDTH_KEY;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_HEIGHT;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_WIDTH;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HiddenPatternWithFallingReels.class,
                 LevelObjectCreatorEntityHolder.class,
                 OrthoCachedTiledMapRenderer.class,
                 Random.class})

public class TestHiddenPatternWithFallingReels {
    public static final int MAX_NUMBER_OF_PLAYING_CARDS_FOR_LEVEL = 1;
    private HiddenPatternWithFallingReels partialHiddenPatternWithFallingReels;
    private Application applicationMock;
    private Input mockInput;
    private TweenManager tweenManagerMock;
    private TiledMap levelMock;
    private AnnotationAssetManager annotationAssetManagerMock;
    private OrthogonalTiledMapRenderer tiledMapRendererMock;
    private MapLayers mapLayersMock;
    private MapLayer mapLayerMock;
    private MapObjects mapObjectsMock;
    private Array<RectangleMapObject> rectangleMapObjectsMock;
    private TextureAtlas cardDeckAtlasMock;
    private LevelDoor levelDoorMock;
    private MapProperties mapPropertiesMock;
    private Random randomMock;
    private Sprite spriteMock;
    private RectangleMapObject rectangleMapObjectMock;
    private Array<ReelTile> reelTilesMock;
    private ReelTile reelTileMock;
    private LevelObjectCreatorEntityHolder partialLevelObjectCreatorEntityHolder;
    private ReelSprites reelSpritesMock;
    private Sprite[] reelsMock;
    private ReelTileListener reelTileListenerMock;
    private PhysicsManagerCustomBodies physicsMock;
    private Body bodyMock;
    private Capture<String> debugCaptureArgument1, debugCaptureArgument2;
    private Hud hudMock;
    private Array<AnimatedReel> animatedReelsMock;

    @Test
    public void testHiddenPatternWithFallingReels_WithNoReplacementReels() throws Exception {
        createMocks();
        setUpPartialHiddenPatternWithFallingReelsFields();
        setUpExpectations();
        replayAll();
        Whitebox.invokeMethod(partialHiddenPatternWithFallingReels, "loadlevel");
        partialHiddenPatternWithFallingReels.updateOverride(0);
//        verifyAll();
    }

    private void setUpExpectations() throws Exception {
        expectGetLevelAssets();
        expectSetUpLoadlevel();
        expectGetRectangleMapObjects(REELS_LAYER_NAME);
        expectCreateLevel();
        expectSetUpPlayingCards();
        expectInitialiseHiddenPlayingCards();
        expectAddACard();
        expectGetMapProperties();
        expectUpdateOverride();
    }

    private void expectGetLevelAssets() {
        expect(annotationAssetManagerMock.get(LEVELS_LEVEL_7)).andReturn(levelMock);
    }

    private void expectSetUpLoadlevel() throws Exception {
        expectNew(
                LevelObjectCreatorEntityHolder.class,
                partialHiddenPatternWithFallingReels,
                null,
                null).
                andReturn(partialLevelObjectCreatorEntityHolder);
        expectNew(OrthogonalTiledMapRenderer.class, levelMock).andReturn(tiledMapRendererMock);
        expect(annotationAssetManagerMock.get(AssetsAnnotation.CARDDECK)).andReturn(cardDeckAtlasMock);
        expect(partialLevelObjectCreatorEntityHolder.getAnimatedReels()).andReturn(animatedReelsMock);
        expect(partialLevelObjectCreatorEntityHolder.getReelTiles()).andReturn(reelTilesMock);
        expectInitialiseReels();
    }

    private void expectGetRectangleMapObjects(String layerName) {
        expect(levelMock.getLayers()).andReturn(mapLayersMock);
        expect(mapLayersMock.get(layerName)).andReturn(mapLayerMock);
        expect(mapLayerMock.getObjects()).andReturn(mapObjectsMock);
        expect(mapObjectsMock.getByType(RectangleMapObject.class)).andReturn(rectangleMapObjectsMock);
    }

    private void expectCreateLevel() throws Exception {
        expect(levelDoorMock.getLevelType()).andReturn(PLAYING_CARD_LEVEL_TYPE);
        expectProcessCustomProperties();
        expectPopulateLevel();
        expectCheckLevel();
        expectAdjustForAnyLonelyReels();
    }

    private void expectProcessCustomProperties() {
        expect(rectangleMapObjectMock.getProperties()).andReturn(createProperties());
    }

    private void expectPopulateLevel() {
        expectGetRectangleMapObjects(REELS_LAYER_NAME);
        expect(rectangleMapObjectMock.getName()).andReturn("Reel");
        expect(rectangleMapObjectMock.getRectangle()).andReturn(new Rectangle(160,40,40,40));
        expectAddReel();
    }

    private void expectAddReel() {
        expectSetUpRelatedReelTileBody();
    }

    private void expectSetUpPlayingCards() {
        expectGetRectangleMapObjects(LevelCreator.HIDDEN_PATTERN_LAYER_NAME);
    }

    private void expectInitialiseHiddenPlayingCards() {
        expectSetNumberOfCardsForTheLevel(MAX_NUMBER_OF_PLAYING_CARDS_FOR_LEVEL);
    }

    private void expectSetNumberOfCardsForTheLevel(int numberOfCardsForLevel) {
        expect(levelMock.getProperties()).andReturn(mapPropertiesMock);
        expect(mapPropertiesMock.get("Number Of Cards", String.class)).andReturn(String.valueOf(numberOfCardsForLevel));
    }

    private void expectAddACard() {
        expectGetNextRandomInt(MAX_NUMBER_OF_PLAYING_CARDS_FOR_LEVEL, 0);
        expectGetNextRandomInt(Suit.getNumberOfSuits(), 0);
        expectGetNextRandomInt(Pip.getNumberOfCards(), 0);
        expectGetHiddenPlayingCardFromLevel();
    }

    private void expectGetNextRandomInt(int randomRange, int value) {
        expect(Random.getInstance()).andReturn(randomMock);
        expect(randomMock.nextInt(randomRange)).andReturn(value);
    }

    private void expectGetHiddenPlayingCardFromLevel() {
        expect(cardDeckAtlasMock.createSprite(HiddenPlayingCard.CARD_BACK, 3)).andReturn(spriteMock);
        expect(cardDeckAtlasMock.createSprite(("clubs"), 1)).andReturn(spriteMock);
        expectGetHiddenPlayingCard();
        expect(rectangleMapObjectMock.getRectangle()).andReturn(new Rectangle(160,40,80,120)).times(4);
    }

    private void expectGetHiddenPlayingCard() {
        expectGetRectangleMapObjects(HIDDEN_PATTERN_LAYER_NAME);
        rectangleMapObjectsMock.add(rectangleMapObjectMock);
    }

    private void expectInitialiseReels() {
        reelTileMock.startSpinning();
        reelTileMock.setSx(0);
        expect(reelSpritesMock.getSprites()).andReturn(reelsMock);
        expectGetNextRandomInt(0, 0);
        reelTileMock.setEndReel(0);
        reelTileMock.setX(160.0f);
        reelTileMock.setY(40.0f);
        reelTileMock.setDestinationX(160.0f);
        reelTileMock.setDestinationY(40.0f);
        reelTileMock.setIndex(0);
        reelTileMock.setSx(0);
        expect(reelTileMock.addListener(anyObject(ReelTileListener.class))).andReturn(true);
    }

    private MapProperties createProperties() {
        MapProperties mapProperties = new MapProperties();
        return mapProperties;
    }

    private void expectSetUpRelatedReelTileBody() {
        expect(reelTileMock.getX()).andReturn(160.0f);
        expect(reelTileMock.getY()).andReturn(40.0f);
        expect(physicsMock.createBoxBody(
                BodyDef.BodyType.DynamicBody,
                180.0f,
                400.0f,
                19,
                19,
                true)).andReturn(bodyMock);
        bodyMock.setUserData(reelTileMock);
    }

    private void expectCheckLevel() {
        expectPopulateMatchGrid();
        expectCheckLevelDebugForOneReel();
    }

    private void expectPopulateMatchGrid() {
        expect(reelTileMock.getDestinationX()).andReturn(160.0f);
        expect(reelTileMock.getDestinationY()).andReturn(40.0f);
        expect(reelTileMock.isReelTileDeleted()).andReturn(false);
        expect(reelTileMock.getEndReel()).andReturn(0);
        expect(reelTileMock.getX()).andReturn(160.0f);
        expect(reelTileMock.getY()).andReturn(40.0f);
        expect(reelTileMock.getDestinationX()).andReturn(160.0f);
        expect(reelTileMock.getDestinationY()).andReturn(40.0f);
        expect(reelTileMock.getEndReel()).andReturn(0);
        applicationMock.debug(capture(debugCaptureArgument1), capture(debugCaptureArgument2));
        applicationMock.debug(capture(debugCaptureArgument1), capture(debugCaptureArgument2));
    }

    private void expectCheckLevelDebugForOneReel() {
        for (int r = 0; r < GAME_LEVEL_HEIGHT; r++)
            for (int c = 0; c < GAME_LEVEL_WIDTH; c++)
                if (!(r == 8 && c == 0))
                    applicationMock.debug(capture(debugCaptureArgument1), capture(debugCaptureArgument2));
    }

    private void expectAdjustForAnyLonelyReels() {
        expectPopulateMatchGrid();
        expectCheckLevelDebugForOneReel();
    }

    private void expectGetMapProperties() {
        MapProperties mapProperties = createProperties();
        mapProperties.put(WIDTH_KEY, GAME_LEVEL_WIDTH);
        mapProperties.put(HEIGHT_KEY, GAME_LEVEL_HEIGHT);
        expect(levelMock.getProperties()).andReturn(mapProperties);
    }

    private void expectUpdateOverride() {
        expect(mockInput.justTouched()).andReturn(false);
        tweenManagerMock.update(0);
        physicsMock.update(0);
        tiledMapRendererMock.setView(null);
        hudMock.update(0);
    }

    private void setUpPartialHiddenPatternWithFallingReelsFields() {
        Whitebox.setInternalState(
                partialHiddenPatternWithFallingReels,
                "tweenManager",
                tweenManagerMock);
        Whitebox.setInternalState(
                partialHiddenPatternWithFallingReels,
                "annotationAssetManager",
                annotationAssetManagerMock);
        Whitebox.setInternalState(
                partialHiddenPatternWithFallingReels,
                "levelDoor",
                levelDoorMock);
        Whitebox.setInternalState(
                partialLevelObjectCreatorEntityHolder,
                "reelTiles",
                reelTilesMock);
        Whitebox.setInternalState(
                partialHiddenPatternWithFallingReels,
                "reelSprites",
                reelSpritesMock);
        Whitebox.setInternalState(
                partialHiddenPatternWithFallingReels,
                "physics",
                physicsMock
        );
        Whitebox.setInternalState(
                partialHiddenPatternWithFallingReels,
                "hud",
                hudMock
        );
        Whitebox.setInternalState(
                partialHiddenPatternWithFallingReels,
                "reels",
                animatedReelsMock
        );
    }

    private void createMocks() {
        createPartialMocks();
        setUpMocks();
        mockGdx();
        setUpRandomMock();
        setUpCaptureArguments();
    }

    private void createPartialMocks() {
        partialHiddenPatternWithFallingReels = PowerMock.createPartialMock(
                HiddenPatternWithFallingReels.class,
                "initialiseOverride");
        partialLevelObjectCreatorEntityHolder = PowerMock.createPartialMock(
                LevelObjectCreatorEntityHolder.class,
                "getReelTiles"
        );
        partialLevelObjectCreatorEntityHolder = PowerMock.createPartialMock(
                LevelObjectCreatorEntityHolder.class,
                "getAnimatedReels"
        );
    }

    private void setUpMocks() {
        applicationMock = createMock(Application.class);
        mockInput = createMock(Input.class);
        tweenManagerMock = createMock(TweenManager.class);
        levelMock = createMock(TiledMap.class);
        annotationAssetManagerMock = createMock(AnnotationAssetManager.class);
        tiledMapRendererMock = createMock(OrthogonalTiledMapRenderer.class);
        mapLayersMock = createMock(MapLayers.class);
        mapLayerMock = createMock(MapLayer.class);
        mapObjectsMock = createMock(MapObjects.class);
        rectangleMapObjectsMock = new Array<>();
        cardDeckAtlasMock =  createMock(TextureAtlas.class);
        levelDoorMock = createMock(LevelDoor.class);
        mapPropertiesMock = createMock(MapProperties.class);
        randomMock = createMock(Random.class);
        spriteMock = createMock(Sprite.class);
        rectangleMapObjectMock = createMock(RectangleMapObject.class);
        reelTilesMock = new Array<>();
        reelTileMock = createMock(ReelTile.class);
        reelTilesMock.add(reelTileMock);
        reelSpritesMock = createMock(ReelSprites.class);
        reelsMock = new Sprite[1];
        reelsMock[0] = createMock(Sprite.class);
        reelTileListenerMock = createMock(ReelTileListener.class);
        physicsMock = createMock(PhysicsManagerCustomBodies.class);
        bodyMock = createMock(Body.class);
        hudMock = createMock(Hud.class);
        animatedReelsMock = new Array<>();
    }

    private void mockGdx() {
        Gdx.input = mockInput;
        Gdx.app = applicationMock;
    }

    private void setUpRandomMock() {
        PowerMock.mockStatic(Random.class);
    }

    private void setUpCaptureArguments() {
        debugCaptureArgument1 = EasyMock.newCapture();
        debugCaptureArgument2 = EasyMock.newCapture();
    }
}
