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
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.card.Suit;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreator;
import com.ellzone.slotpuzzle2d.level.creator.LevelObjectCreatorEntityHolder;
import com.ellzone.slotpuzzle2d.level.hidden.HiddenPlayingCard;
import com.ellzone.slotpuzzle2d.level.hidden.Pip;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.InputMatrix;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMock;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

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
    private Array<RectangleMapObject> hiddenPlayingCardRectangleMapObjectsMock;

    @Before
    public void setUp() {
        createMocks();
    }

    @After
    public void tearDown() {
        teardownMocks();
    }

    @Test
    public void testHiddenPatternWithFallingReels_WithNoReplacementReels() throws Exception {
        setUpPartialHiddenPatternWithFallingReelsFields();
        setUpExpectations();
        replayAll();
        Whitebox.invokeMethod(partialHiddenPatternWithFallingReels, "loadlevel");
        partialHiddenPatternWithFallingReels.updateOverride(0);
        assertThat(partialHiddenPatternWithFallingReels.getLevelCreator().getPlayState(), (Matcher<? super PlayStates>) is(equalTo(PlayStates.INITIALISING)));
        verifyAll();
    }

    @Test
    public void testHiddenPatternWithFallingReels_WithTwoReplacementReels() throws Exception {
        testWithNumberOfReelsInARow(2);
    }

    @Test
    public void testHiddenPatternWithFallingReels_WithMoreThanTwoReplacementReels() throws Exception {
        testWithNumberOfReelsInARow(3);
    }

    @Test
    public void testHiddenPatternWithFallingReels_WithAFullRowReplacementReels() throws Exception {
        int[][] testMatrix = createMatrixWithNReplacementReels(13);
        reelTilesMock = createReelTilesFromMatrix(testMatrix);
        assertReelTilesMockVsTestMatrixRC(testMatrix);
        setUpPartialHiddenPatternWithFallingReelsFields();
        setUpExpectations(reelTilesMock.size);
        replayAll();
        Whitebox.invokeMethod(partialHiddenPatternWithFallingReels, "loadlevel");
        partialHiddenPatternWithFallingReels.updateOverride(0);
        assertThat(partialHiddenPatternWithFallingReels.getLevelCreator().getPlayState(),
                (Matcher<? super PlayStates>) is(equalTo(PlayStates.INITIALISING)));
        verifyAll();
    }

    @Test
    public void testHiddenPatternWithFallingReels_WithBottomRowReplacementReels() throws Exception {
        int[][] testMatrix = createMatrixWithBottomRowFull();
        reelTilesMock = createReelTilesFromMatrix(testMatrix);
        assertReelTilesMockVsTestMatrixRC(testMatrix);
        setUpPartialHiddenPatternWithFallingReelsFields();
        setUpExpectations(12);
        replayAll();
        Whitebox.invokeMethod(partialHiddenPatternWithFallingReels, "loadlevel");
        partialHiddenPatternWithFallingReels.updateOverride(0);
        assertThat(partialHiddenPatternWithFallingReels.getLevelCreator().getPlayState(),
                (Matcher<? super PlayStates>) is(equalTo(PlayStates.INITIALISING)));
        verifyAll();
    }

    private void testWithNumberOfReelsInARow(int i) throws Exception {
        int[][] testMatrix = createMatrixWithNReplacementReels(i);
        reelTilesMock = createReelTilesFromMatrix(testMatrix);
        assertReelTilesMockVsTestMatrixRC(testMatrix);
        setUpPartialHiddenPatternWithFallingReelsFields();
        setUpExpectations(i);
        replayAll(applicationMock);
        Whitebox.invokeMethod(partialHiddenPatternWithFallingReels, "loadlevel");
        partialHiddenPatternWithFallingReels.updateOverride(0);
        assertThat(partialHiddenPatternWithFallingReels.getLevelCreator().getPlayState(),
                (Matcher<? super PlayStates>) is(equalTo(PlayStates.INITIALISING)));
        verifyAll();
    }

    private int[][] createMatrixWithNReplacementReels(int numberOfReels) {
        int[][] testMatrix = createEmptyMatrix();
        int numberOfRows = numberOfReels /  testMatrix[0].length;
        int numberOfRemainingReels = numberOfReels % testMatrix[0].length;
        assertThat(numberOfRows, is(lessThan(testMatrix.length)));
        assertThat(numberOfRemainingReels, is(lessThan(testMatrix[0].length)));
        for(int r = 0; r < numberOfRows; r++)
            for (int c = 0; c < testMatrix[0].length; c++)
                testMatrix[r][c] = 0;
        for (int remainingColumn = 0; remainingColumn < numberOfRemainingReels; remainingColumn++)
            testMatrix[numberOfRows][remainingColumn] = 0;
        return testMatrix;
    }

    private void assertReelTilesMockVsTestMatrixRC(int[][] testMatrix) {
        int reelIndex = 0;
        for (int r = 0; r < GAME_LEVEL_HEIGHT; r++)
            for (int c = 0; c < GAME_LEVEL_WIDTH; c++)
                if (testMatrix[r][c] > 0)
                    assertThat((int) Whitebox.getInternalState(
                            reelTilesMock.get(reelIndex++), "endReel"),
                            (Matcher<? super Integer>) is(equalTo(testMatrix[r][c])));
    }

    private Array<ReelTile> createReelTilesFromMatrix(int[][] matrix) {
        Array<ReelTile> reelTiles = new Array<>();
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                if (matrix[r][c] >= 0) {
                    ReelTile reelTileMock = PowerMock.createMock(ReelTile.class);
                    Whitebox.setInternalState(reelTileMock, "x", PlayScreen.PUZZLE_GRID_START_X + (c * 40));
                    Whitebox.setInternalState(reelTileMock, "y", (r * 40) + 40);
                    Whitebox.setInternalState(reelTileMock, "tileDeleted", matrix[r][c] < 0);
                    Whitebox.setInternalState(reelTileMock, "index", r * matrix[0].length + c);
                    Whitebox.setInternalState(reelTileMock, "endReel", matrix[r][c]);
                    reelTiles.add(reelTileMock);
                }
            }
        }
        return reelTiles;
    }

    private void setUpExpectations() throws Exception {
        expectLoadlevel();
        expectLevelCreatorSimpleCreateLevel();
        expectGetMapProperties();
        expectUpdateOverride();
    }

    private void setUpExpectations(int numberOfReplacementReels) throws Exception {
        expectLoadlevel(numberOfReplacementReels);
        expectUpdateOverride();
    }

    private void expectLoadlevel() throws Exception {
        expectNewLevelCreator();
        expectGetLevelAssets();
        expectNew(OrthogonalTiledMapRenderer.class, levelMock).andReturn(tiledMapRendererMock);
        expectExtractLevelAssests();
        expect(annotationAssetManagerMock.get(AssetsAnnotation.CARDDECK)).andReturn(cardDeckAtlasMock);
        expect(partialLevelObjectCreatorEntityHolder.getAnimatedReels()).andReturn(animatedReelsMock);
        expect(partialLevelObjectCreatorEntityHolder.getReelTiles()).andReturn(reelTilesMock);
        reelTilesMock.add(reelTileMock);
        expectInitialiseReels();
    }

    private void expectGetLevelAssets() {
        expect(annotationAssetManagerMock.get(LEVELS_LEVEL_7)).andReturn(levelMock);
    }

    private void expectExtractLevelAssests() {
        expectGetRectangleMapObjects(REELS_LAYER_NAME);
    }

    private void expectExtractLevelAssests(int numberOfReplacementReelBoxes) {
        expectGetRectangleMapObjects(REELS_LAYER_NAME, numberOfReplacementReelBoxes);
    }

    private void expectLoadlevel(int numberOfReplacementReels) throws Exception {
        expectNewLevelCreator();
        expectGetLevelAssets();
        expectNew(OrthogonalTiledMapRenderer.class, levelMock).andReturn(tiledMapRendererMock);
        expectExtractLevelAssests(numberOfReplacementReels);
        expect(annotationAssetManagerMock.get(AssetsAnnotation.CARDDECK)).andReturn(cardDeckAtlasMock);
        expect(partialLevelObjectCreatorEntityHolder.getAnimatedReels()).andReturn(animatedReelsMock);
        expect(partialLevelObjectCreatorEntityHolder.getReelTiles()).andReturn(reelTilesMock);
        for (int i = 0; i<numberOfReplacementReels; i++)
            expectInitialiseReelFromMatrix(reelTilesMock, i);
        expectLevelCreatorSimpleCreateLevel(numberOfReplacementReels);
    }

    private void expectNewLevelCreator() throws Exception {
        expectNew(
                LevelObjectCreatorEntityHolder.class,
                partialHiddenPatternWithFallingReels,
                null,
                null).
                andReturn(partialLevelObjectCreatorEntityHolder);
    }

    private void expectLevelCreatorSimpleCreateLevel() throws Exception {
        expect(levelDoorMock.getLevelType()).andReturn(PLAYING_CARD_LEVEL_TYPE);
        expectSetUpPlayingCards();
        expectInitialiseHiddenPlayingCards();
        expectAddACard();
        expectPopulateLevel();
        expectCheckLevel();
        expectAdjustForAnyLonelyReels();
    }

    private void expectLevelCreatorSimpleCreateLevel(int numberOfReplacementBoxes) throws Exception {
        expect(levelDoorMock.getLevelType()).andReturn(PLAYING_CARD_LEVEL_TYPE);
        expectSetUpPlayingCards();
        expectInitialiseHiddenPlayingCards();
        expectAddACard();
        expectPopulateLevel(numberOfReplacementBoxes);
        expectCheckLevel(numberOfReplacementBoxes);
        expectAdjustForAnyLonelyReels(numberOfReplacementBoxes);
        expectGetMapProperties();
    }

    private void expectPopulateLevel() {
        expectGetRectangleMapObjects(REELS_LAYER_NAME);
    }

    private void expectGetRectangleMapObjects(String layerName) {
        expect(levelMock.getLayers()).andReturn(mapLayersMock);
        expect(mapLayersMock.get(layerName)).andReturn(mapLayerMock);
        expect(mapLayerMock.getObjects()).andReturn(mapObjectsMock);
        expect(mapObjectsMock.getByType(RectangleMapObject.class)).andReturn(rectangleMapObjectsMock);
    }

    private void expectGetRectangleMapObjects(String layerName, int numberOfReplacementBoxes) {
        expect(levelMock.getLayers()).andReturn(mapLayersMock);
        expect(mapLayersMock.get(layerName)).andReturn(mapLayerMock);
        expect(mapLayerMock.getObjects()).andReturn(mapObjectsMock);
        expect(mapObjectsMock.getByType(RectangleMapObject.class)).andReturn(rectangleMapObjectsMock);
        if (rectangleMapObjectsMock.size > 0)
            return;
        int numberOfRows = numberOfReplacementBoxes / 12;
        int numberOfRemainderReels = numberOfReplacementBoxes % 12;
        for (int r = 0; r < numberOfRows; r++)
            for (int c=0; c < 12; c++) {
                RectangleMapObject rectangleMapObject = new RectangleMapObject(160 + c*40, 40 + r * 40, 40, 40);
                rectangleMapObject.setName("Reel");
                rectangleMapObjectsMock.add(rectangleMapObject);
            }
        for (int remainderColumns = 0; remainderColumns < numberOfRemainderReels; remainderColumns++) {
            RectangleMapObject rectangleMapObject = new RectangleMapObject(160 + remainderColumns*40, 40 + 40 * numberOfRows, 40, 40);
            rectangleMapObject.setName("Reel");
            rectangleMapObjectsMock.add(rectangleMapObject);
        }

    }

    private void expectPopulateLevel(int numberOfReplacementBoxes) {
        expectGetRectangleMapObjects(REELS_LAYER_NAME, numberOfReplacementBoxes);
        for (int i=0; i<numberOfReplacementBoxes; i++)
            expectAddReel(i);
    }

    private void expectAddReel(int reelOffset) {
        expectSetUpRelatedReelTileBodyRC(reelOffset);
    }

    private void expectSetUpPlayingCards() {
        expect(levelMock.getLayers()).andReturn(mapLayersMock);
        expect(mapLayersMock.get(LevelCreator.HIDDEN_PATTERN_LAYER_NAME)).andReturn(mapLayerMock);
        expect(mapLayerMock.getObjects()).andReturn(mapObjectsMock);
        hiddenPlayingCardRectangleMapObjectsMock.add(createMock(RectangleMapObject.class));
        expect(mapObjectsMock.getByType(RectangleMapObject.class)).andReturn(hiddenPlayingCardRectangleMapObjectsMock);
    }

    private void expectInitialiseHiddenPlayingCards() {
        expectSetNumberOfCardsForTheLevel(MAX_NUMBER_OF_PLAYING_CARDS_FOR_LEVEL);
    }

    private void expectSetNumberOfCardsForTheLevel(int numberOfCardsForLevel) {
        expectGetMapProperties();
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
        expect(hiddenPlayingCardRectangleMapObjectsMock.get(0).getRectangle()).andReturn(new Rectangle(160,40,80,120)).times(4);
    }

    private void expectGetHiddenPlayingCard() {
        expect(levelMock.getLayers()).andReturn(mapLayersMock);
        expect(mapLayersMock.get(LevelCreator.HIDDEN_PATTERN_LAYER_NAME)).andReturn(mapLayerMock);
        expect(mapLayerMock.getObjects()).andReturn(mapObjectsMock);
        expect(mapObjectsMock.getByType(RectangleMapObject.class)).andReturn(hiddenPlayingCardRectangleMapObjectsMock);
    }

    private void expectInitialiseReels() {
        reelTileMock.startSpinning();
        reelTileMock.setSx(0);
        expect(reelSpritesMock.getSprites()).andReturn(reelsMock);
        expectGetNextRandomInt(0, 0);
        reelTileMock.setEndReel(0);
   }

    private void expectInitialiseReelFromMatrix(Array<ReelTile> reelTiles, int reelOffSet) {
        ReelTile reelTileMock = reelTiles.get(reelOffSet);
        reelTileMock.startSpinning();
        reelTileMock.setSx(0);
        expect(reelSpritesMock.getSprites()).andReturn(reelsMock);
        expectGetNextRandomInt(0, 0);
        reelTileMock.setX((float) Whitebox.getInternalState(reelTileMock, "x"));
        reelTileMock.setY((float) Whitebox.getInternalState(reelTileMock, "y"));
        reelTileMock.setDestinationX((float) Whitebox.getInternalState(reelTileMock, "x"));
        reelTileMock.setDestinationY((float) Whitebox.getInternalState(reelTileMock, "y"));
        reelTileMock.setEndReel((int) Whitebox.getInternalState(reelTileMock, "endReel"));
        reelTileMock.setIndex(reelOffSet);
        reelTileMock.setSx(0);
        expect(reelTileMock.addListener(anyObject(ReelTileListener.class))).andReturn(true);
    }

    private MapProperties createProperties() {
        MapProperties mapProperties = new MapProperties();
        return mapProperties;
    }

    private void expectSetUpRelatedReelTileBody(int reelOffset) {
        int r = reelOffset / 12;
        int c = reelOffset % 12;
        expect(reelTilesMock.get(reelOffset).getX()).andReturn(160.0f + reelOffset * 40);
        expect(reelTilesMock.get(reelOffset).getY()).andReturn(40.0f + reelOffset * 40);
        expect(physicsMock.createBoxBody(
                BodyDef.BodyType.DynamicBody,
                180 + reelOffset * 40,
                400.0f + reelOffset * 40,
                19,
                19,
                true)).andReturn(bodyMock);
        bodyMock.setUserData(reelTilesMock.get(reelOffset));
    }

    private void expectSetUpRelatedReelTileBodyRC(int reelOffset) {
        int r = reelOffset / 12;
        int c = reelOffset % 12;
        expect(reelTilesMock.get(reelOffset).getX()).andReturn(160.0f + c * 40);
        expect(reelTilesMock.get(reelOffset).getY()).andReturn(40.0f + r * 40);
        expect(physicsMock.createBoxBody(
                BodyDef.BodyType.DynamicBody,
                180 + c * 40,
                400.0f + r * 40,
                19,
                19,
                true)).andReturn(bodyMock);
        bodyMock.setUserData(reelTilesMock.get(reelOffset));
    }

    private void expectCheckLevel() {
        expectPopulateMatchGrid();
        expectCheckLevelDebugForOneReel();
    }

    private void expectCheckLevel(int numberOfReplacementBoxes) {
        for (int i=0; i<numberOfReplacementBoxes; i++)
            expectPopulateMatchGridRC(i);
        expectCheckLevelDebugForNReels(numberOfReplacementBoxes);
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

    private void expectPopulateMatchGridRC(int reelOffset) {
        int r = reelOffset / 12;
        int c = reelOffset % 12;
        expect(reelTilesMock.get(reelOffset).getDestinationX()).andReturn(160.0f + 40.0f * c);
        expect(reelTilesMock.get(reelOffset).getDestinationY()).andReturn(40.0f + 40.0f * r);
        expect(reelTilesMock.get(reelOffset).isReelTileDeleted()).andReturn(false);
        expect(reelTilesMock.get(reelOffset).getEndReel()).andReturn(0);
        expect(reelTilesMock.get(reelOffset).getX()).andReturn(160.0f + 40.0f * c);
        expect(reelTilesMock.get(reelOffset).getY()).andReturn(40.0f + 40.0f * r);
        expect(reelTilesMock.get(reelOffset).getDestinationX()).andReturn(160.0f + 40.0f * c);
        expect(reelTilesMock.get(reelOffset).getDestinationY()).andReturn(40.0f + 40.0f * r);
        expect(reelTilesMock.get(reelOffset).getEndReel()).andReturn(0);
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

    private void expectAdjustForAnyLonelyReels(int numberOfReplacementBoxes) {
        for (int i=0; i<numberOfReplacementBoxes; i++)
            expectPopulateMatchGridRC(i);
        expectGetLonelyTiles(numberOfReplacementBoxes);
    }

    private void expectGetLonelyTiles(int numberOfReplacementReels) {
        expectCheckLevelDebugForNReels(numberOfReplacementReels);
    }

    private void expectCheckLevelDebugForNReels(int numberOfReplacementReels) {
        for (int r = 0; r < GAME_LEVEL_HEIGHT; r++)
            for (int c = 0; c < GAME_LEVEL_WIDTH; c++)
                if (!isReplacementReel(r, c))
                    applicationMock.debug(capture(debugCaptureArgument1), capture(debugCaptureArgument2));
    }

    private boolean isReplacementReel(int r, int c) {
        for (RectangleMapObject rectangleMapObject : rectangleMapObjectsMock) {
            int column = (int) (rectangleMapObject.getRectangle().x - 160) / 40;
            int row = (int) ((int) GAME_LEVEL_HEIGHT - rectangleMapObject.getRectangle().y / 40);
            if (r == row && c == column)
                return true;
        }
        return false;
    }

    private void expectGetMapProperties() {
        MapProperties mapProperties = createProperties();
        mapProperties.put(WIDTH_KEY, GAME_LEVEL_WIDTH);
        mapProperties.put(HEIGHT_KEY, GAME_LEVEL_HEIGHT);
        mapProperties.put("Number Of Cards", "1");
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
                partialLevelObjectCreatorEntityHolder,
                "reels",
                animatedReelsMock);
        Whitebox.setInternalState(
                partialHiddenPatternWithFallingReels,
                "reelSprites",
                reelSpritesMock);
        Whitebox.setInternalState(
                partialHiddenPatternWithFallingReels,
                "physics",
                physicsMock);
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
                "getReelTiles", "getAnimatedReels"
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
        hiddenPlayingCardRectangleMapObjectsMock = new Array<>();
        cardDeckAtlasMock =  createMock(TextureAtlas.class);
        levelDoorMock = createMock(LevelDoor.class);
        randomMock = createMock(Random.class);
        spriteMock = createMock(Sprite.class);
        rectangleMapObjectMock = createMock(RectangleMapObject.class);
        reelTilesMock = new Array<>();
        reelTileMock = createMock(ReelTile.class);
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
        debugCaptureArgument2 = EasyMock.newCapture(CaptureType.ALL);
    }

    private void teardownMocks() {
        applicationMock = null;
        mockInput = null;
        tweenManagerMock = null;
        levelMock = null;
        annotationAssetManagerMock = null;
        tiledMapRendererMock = null;
        mapLayersMock = null;
        mapLayerMock = null;
        mapObjectsMock = null;
        rectangleMapObjectsMock = null;
        cardDeckAtlasMock =  null;
        levelDoorMock = null;
        randomMock = null;
        spriteMock = null;
        rectangleMapObjectMock = null;
        reelTilesMock = null;
        reelTileMock = null;
        reelSpritesMock = null;
        reelsMock = null;
        reelTileListenerMock = null;
        physicsMock = null;
        bodyMock = null;
        hudMock = null;
        animatedReelsMock = null;
    }

    private int[][] createMatrixWithBottomRowFull() {
        String matrixToInput = "12 x 9\n"
                + " 0  0  0  0  0  0  0  0  0  0   0   0\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    private int[][] createEmptyMatrix() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1  -1  -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }
}
