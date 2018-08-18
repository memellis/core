package com.ellzone.level;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.level.HiddenPattern;
import com.ellzone.slotpuzzle2d.level.HiddenPlayingCard;
import com.ellzone.slotpuzzle2d.level.LevelCreator;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.utils.InputMatrix;
import com.ellzone.slotpuzzle2d.utils.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HiddenPlayingCard.class, ReelTile.class, Random.class})

public class TestHiddenPlayingCard {
    private static final int MAX_NUMBER_OF_HIDDEN_PLAYING_CARDS = 10;
    private static final int MAX_NUMBER_OF_CARD_VALUES = 13;
    private static final int MAX_NUMBER_SUITS = 4;

    private TiledMap levelMock;
    private TextureAtlas textureAtlasMock;
    private MapLayers mapLayersMock;
    private MapLayer mapLayerMock;
    private MapObjects mapObjectsMock;
    private Array<RectangleMapObject> rectangleMapObjectsMock;
    private RectangleMapObject rectangleMapObjectMock;
    private MapProperties mapPropertiesMock;
    private Sprite spriteBackMock, spriteFrontMock;
    private Rectangle rectangleMock;
    private Random randomMock;
    private int[][] testMatrix;
    private TupleValueIndex[][] testGrid;
    private Array<ReelTile> reelTiles;

    @Before
    public void setUp() {
        setUpMocks();
    }

    @After
    public void tearDown() {
        tearDownMocks();
        tearDownTestFixtures();
    }

    @Test
    public void testHidddenPlayCardIsNotRevealed() {
        testMatrix = createMatrixNoCardsRevealed();
        createGrid(testMatrix);
        setExpectations(false);
        replayAll();
        HiddenPlayingCard hiddenPlayingCard = new HiddenPlayingCard(levelMock, textureAtlasMock);
        assertThat(hiddenPlayingCard.isHiddenPatternRevealed(testGrid,
                                                             reelTiles,
                                                              testGrid[0].length,
                                                               testGrid.length),
                   is(false));
        verifyAll();
    }

    @Test
    public void testHiddenPlayingCardIsRevealed() {
        testMatrix = createMatrixCardsRevealed();
        createGrid(testMatrix);
        setExpectations(true);
        replayAll();
        HiddenPlayingCard hiddenPlayingCard = new HiddenPlayingCard(levelMock, textureAtlasMock);
        assertThat(hiddenPlayingCard.isHiddenPatternRevealed(testGrid,
                                                             reelTiles,
                                                             testGrid[0].length,
                                                             testGrid.length),
                   is(true));
        verifyAll();
    }

    @Test
    public void testNumberOfHiddenPlayingCardsCreated() {
        setExpectationsLevelMaxRectangleMapObjects();
        setExpectationsSetNumberOfCardsForTheLevel(2);
        expectSpritesInGetHiddenPlayingCardFromLevel();
        getRectangleMapObject();
        getCardRectangle(80.0f);
        getCardRectangle(240.0f);
        expectSpritesInGetHiddenPlayingCardFromLevel();
        getRectangleMapObject();
        replayAll();
        HiddenPlayingCard hiddenPlayingCard = new HiddenPlayingCard(levelMock, textureAtlasMock);
        assertThat(hiddenPlayingCard.getCards().size, is(2));
        verifyAll();
    }

    @Test
    public void testDuplicateCardsCreated() {
        setUpRandomMock();
        setExpectationsLevelMaxRectangleMapObjects();
        setExpectationsSetNumberOfCardsForTheLevel(2);
        expectSpritesInGetHiddenPlayingCardFromLevel();
        getRectangleMapObject();
        expectRandomNextInt();
        getCardRectangle(80.0f);
        getCardRectangle(240.0f);
        expectSpritesInGetHiddenPlayingCardFromLevel();
        getRectangleMapObject();
        replayAll();
        HiddenPlayingCard hiddenPlayingCard = new HiddenPlayingCard(levelMock, textureAtlasMock);
        Array<Integer> hiddenPlayingCards = hiddenPlayingCard.getHiddenPlayingCards();
        assertThat(hiddenPlayingCards.size, is(2));
        assertThat(hiddenPlayingCards.get(0), is(not(equalTo(hiddenPlayingCards.get(1)))));
        verifyAll();
    }

    @Test
    public void testMaximumCardsForLevel() {
        setUpRandomMock();
        setExpectationsSetNumberOfCardsForTheLevel(10);
        expectRandomNextIntForTenCards();
        for (int cardLoop = 0; cardLoop < 5; cardLoop++) {
            expectTwoCards();
        }
        finalGetRectangleObjects();

        replayAll();
        HiddenPlayingCard hiddenPlayingCard = new HiddenPlayingCard(levelMock, textureAtlasMock);
        Array<Integer> hiddenPlayingCards = hiddenPlayingCard.getHiddenPlayingCards();
        assertThat(hiddenPlayingCards.size, is(MAX_NUMBER_OF_HIDDEN_PLAYING_CARDS));
        verifyAll();
    }

    private void expectTwoCards() {
        setExpectationsLevelMaxRectangleMapObjects();
        expectSpritesInGetHiddenPlayingCardFromLevel();
        getRectangleMapObject();
        getCardRectangle(80.0f);
        getCardRectangle(240.0f);
        expectSpritesInGetHiddenPlayingCardFromLevel();
    }

    private void finalGetRectangleObjects() {
        expect(levelMock.getLayers()).andReturn(mapLayersMock).times(1);
        expect(mapLayersMock.get(LevelCreator.HIDDEN_PATTERN_LAYER_NAME)).andReturn(mapLayerMock).times(1);
        expect(mapLayerMock.getObjects()).andReturn(mapObjectsMock).times(1);
        expect(mapObjectsMock.getByType(RectangleMapObject.class)).andReturn(rectangleMapObjectsMock).times(1);
        expect(rectangleMapObjectsMock.get(anyInt())).andReturn(rectangleMapObjectMock).times(5);
    }

    @Test(expected = HiddenPattern.HiddenPatternPuzzleGridException.class)
    public void isHiddenPlayingCardRevealeThrowsHiddenPlayingCardPuzzleGridException() {
        setUpMocks();
        testMatrix = createMatrixNoCardsRevealed();
        createGrid(testMatrix);
        testGrid[7][1] = null;
        setExpectationsAGridCellIsNull();
        replayAll();
        HiddenPlayingCard hiddenPlayingCard = new HiddenPlayingCard(levelMock, textureAtlasMock);
        assertThat(hiddenPlayingCard.isHiddenPatternRevealed(testGrid,
                                                             reelTiles,
                                                             testGrid[0].length,
                                                             testGrid.length),
                                                             is(false));
        verifyAll();
    }

    private void setUpMocks() {
        levelMock = createMock(TiledMap.class);
        textureAtlasMock = createMock(TextureAtlas.class);
        mapLayersMock = createMock(MapLayers.class);
        mapLayerMock = createMock(MapLayer.class);
        mapObjectsMock = createMock(MapObjects.class);
        rectangleMapObjectsMock = createMock(Array.class);
        rectangleMapObjectMock = createMock(RectangleMapObject.class);
        mapPropertiesMock = createMock(MapProperties.class);
        spriteBackMock = createMock(Sprite.class);
        spriteFrontMock = createMock(Sprite.class);
        rectangleMock = createMock(Rectangle.class);
        randomMock = createMock(Random.class);
    }

    private void setUpRandomMock() {
        PowerMock.mockStatic(Random.class);
    }

    private void createGrid(int[][] matrix) {
        testGrid = new TupleValueIndex[matrix.length][matrix[0].length];
        reelTiles = new Array<>();
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                testGrid[r][c] = new TupleValueIndex(r, c, r * matrix[0].length + c, matrix[r][c]);
                ReelTile reelTileMock = PowerMock.createMock(ReelTile.class);
                Whitebox.setInternalState(reelTileMock,"x",PlayScreen.PUZZLE_GRID_START_X + (c * 40));
                Whitebox.setInternalState(reelTileMock,"y",(r  * 40) - PlayScreen.PUZZLE_GRID_START_Y);
                Whitebox.setInternalState(reelTileMock, "tileDeleted", matrix[r][c] < 0);
                Whitebox.setInternalState(reelTileMock, "index", r * testGrid[0].length + c);
                reelTiles.add(reelTileMock);
            }
        }
    }

    private int[][] createMatrixNoCardsRevealed() {
        String matrixToInput = "11 x 9\n"
                + "0  1  2  3  4  5  6  7  8  9  10\n"
                + "0 -1  2  3  4  5  6  7  8  9  10\n"
                + "0 -1 -1  3  4  5  6  7  8  9  10\n"
                + "0 -1 -1  3  4  5  6  7  8  9  10\n"
                + "0  1  2  3  4  5  6  7  8  9  10\n"
                + "0 -1 -1  3  4  5  6  7  8  9  10\n"
                + "0 -1 -1  3  4  5  6  7  8  9  10\n"
                + "0 -1 -1   3  4  5  6  7  8  9  10\n"
                + "0  1  2  3  4  5  6  7  8  9  10\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    private int[][] createMatrixCardsRevealed() {
        String matrixToInput = "11 x 9\n"
                + "0  1  2  3  4  5  6  7  8  9  10\n"
                + "0 -1 -1  3  4  5  6  7  8  9  10\n"
                + "0 -1 -1  3  4  5  6  7  8  9  10\n"
                + "0 -1 -1  3  4  5  6  7  8  9  10\n"
                + "0  1  2  3  4  5  6  7  8  9  10\n"
                + "0 -1 -1  3  4  5  6  7  8  9  10\n"
                + "0 -1 -1  3  4  5  6  7  8  9  10\n"
                + "0 -1 -1  3  4  5  6  7  8  9  10\n"
                + "0  1  2  3  4  5  6  7  8  9  10\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }


    private void setExpectations(boolean expectCardRevealed) {
        setExpectationsLevelMaxRectangleMapObjects();
        setExpectationsSetNumberOfCardsForTheLevel(2);
        setUpExpectationsIsHiddenCardsRevealed(expectCardRevealed);
    }

    private void setExpectationsSetNumberOfCardsForTheLevel(int numberOfCardsForLevel) {
        expect(levelMock.getProperties()).andReturn(mapPropertiesMock);
        expect(mapPropertiesMock.get("Number Of Cards", String.class)).andReturn(String.valueOf(numberOfCardsForLevel));
    }

    private void setExpectationsLevelMaxRectangleMapObjects() {
        expect(levelMock.getLayers()).andReturn(mapLayersMock);
        expect(mapLayersMock.get(LevelCreator.HIDDEN_PATTERN_LAYER_NAME)).andReturn(mapLayerMock);
        expect(mapLayerMock.getObjects()).andReturn(mapObjectsMock);
        expect(mapObjectsMock.getByType(RectangleMapObject.class)).andReturn(rectangleMapObjectsMock);
        rectangleMapObjectsMock.size = 10;
    }

    private void setUpExpectationsCard(float cardY) {
        expectSpritesInGetHiddenPlayingCardFromLevel();
        getRectangleMapObject();
        getCardRectangle(cardY);
    }

    private void setUpExpectationsIsHiddenCardsRevealed(boolean expectCardRevealed) {
        setUpExpectationsCard(80.0f);
        setUpExpectationsGetCardRectangleFromLevel();
        expectForHiddenCardTile(80.0f);
        setUpExpectationsCardRevealed(7, expectCardRevealed);
        setUpExpectationsCard(240.0f);
        setUpExpectationsGetCardRectangleFromLevel();
        expectForHiddenCardTile(240.0f);
        setUpExpectationsCardRevealed(3, expectCardRevealed);
    }

    private void getCardRectangle(float cardY) {
        rectangleMock.x = 200.0f;
        rectangleMock.y = cardY;
        rectangleMock.width = 80.0f;
        rectangleMock.height = 120.0f;
        expect(rectangleMapObjectMock.getRectangle()).andReturn(rectangleMock);
        expect(rectangleMapObjectMock.getRectangle()).andReturn(rectangleMock);
        expect(rectangleMapObjectMock.getRectangle()).andReturn(rectangleMock);
        expect(rectangleMapObjectMock.getRectangle()).andReturn(rectangleMock);
    }

    private void getRectangleMapObject() {
        expect(levelMock.getLayers()).andReturn(mapLayersMock);
        expect(mapLayersMock.get(LevelCreator.HIDDEN_PATTERN_LAYER_NAME)).andReturn(mapLayerMock);
        expect(mapLayerMock.getObjects()).andReturn(mapObjectsMock);
        expect(mapObjectsMock.getByType(RectangleMapObject.class)).andReturn(rectangleMapObjectsMock);
        expect(rectangleMapObjectsMock.get(anyInt())).andReturn(rectangleMapObjectMock);
    }

    private void expectSpritesInGetHiddenPlayingCardFromLevel() {
        expect(textureAtlasMock.createSprite(HiddenPlayingCard.CARD_BACK, 3)).andReturn(spriteBackMock);
        expect(textureAtlasMock.createSprite(anyString(), anyInt())).andReturn(spriteFrontMock);
    }

    private void expectForHiddenCardTile(float y) {
       expectForCardColumn(rectangleMock.x, rectangleMock.width, 2);
       expectForCardRow(y, rectangleMock.height);
    }

    private void expectForCardRow(float y, float height) {
        expect(rectangleMock.getY()).andReturn(y).times(2);
        expect(rectangleMock.getHeight()).andReturn(height);
    }

    private void expectForCardColumn(float x, float width, int times) {
        expect(rectangleMock.getX()).andReturn(x).times(times);
        expect(rectangleMock.getWidth()).andReturn(width);
    }

    private void setUpExpectationsGetCardRectangleFromLevel() {
        getRectangleMapObject();
        expect(rectangleMapObjectMock.getRectangle()).andReturn(rectangleMock);
    }

    private void setUpExpectationsCardRevealed(int tileRow, boolean isCardRevealed)  {
        expectCardColumn(tileRow, 1);
        expectForRowsAndColumn();
        expect(rectangleMock.getHeight()).andReturn(rectangleMock.height).times(2);
        expectCardColumn(tileRow, 2);
        if (isCardNoteRevealedForTileRowColumn(tileRow, isCardRevealed))
            return;
        expectForRowsAndColumn();
        expect(rectangleMock.getHeight()).andReturn(rectangleMock.height);
    }

    private boolean isCardNoteRevealedForTileRowColumn(int tileRow, boolean isCardRevealed) {
        if ((!isCardRevealed) & (tileRow == 3)) {
            expect(rectangleMock.getY()).andReturn(rectangleMock.y).times(3);
            expect(rectangleMock.getHeight()).andReturn(rectangleMock.height).times(2);
            return true;
        }
        return false;
    }

    private void expectForRowsAndColumn() {
        expectForCardRow(rectangleMock.y, rectangleMock.height);
        expectForCardRow(rectangleMock.y, rectangleMock.height);
        expectForCardColumn(rectangleMock.x, rectangleMock.width, 1);
    }

    private void expectCardColumn(int cardRow, int column) {
        expect(reelTiles.get(cardRow * testMatrix[0].length + column).isReelTileDeleted()).andReturn(testGrid[cardRow][column].value < 0);
        expect(reelTiles.get((cardRow - 1) * testMatrix[0].length + column).isReelTileDeleted()).andReturn(testGrid[cardRow - 1][column].value < 0);
        expect(reelTiles.get((cardRow - 2) * testMatrix[0].length + column).isReelTileDeleted()).andReturn(testGrid[cardRow - 2][column].value < 0);
    }

    private void expectRandomNextInt() {
        expect(Random.getInstance()).andReturn(randomMock).times(15);
        expect(randomMock.nextInt(MAX_NUMBER_OF_HIDDEN_PLAYING_CARDS)).andReturn(1).times(12);
        expect(randomMock.nextInt(MAX_NUMBER_SUITS)).andReturn(1).times(1);
        expect(randomMock.nextInt(MAX_NUMBER_OF_CARD_VALUES)).andReturn(1).times(1);
        expect(randomMock.nextInt(MAX_NUMBER_OF_HIDDEN_PLAYING_CARDS - 1)).andReturn(1).times(1);
    }

    private void expectRandomNextIntForTenCards() {
        expect(Random.getInstance()).andReturn(randomMock).times(119);
        expect(randomMock.nextInt(MAX_NUMBER_OF_HIDDEN_PLAYING_CARDS)).andReturn(1).times(100);
        expect(randomMock.nextInt(MAX_NUMBER_SUITS)).andReturn(1).times(5);
        expect(randomMock.nextInt(MAX_NUMBER_OF_CARD_VALUES)).andReturn(1).times(5);
        for (int nextInt = MAX_NUMBER_OF_HIDDEN_PLAYING_CARDS - 1; nextInt >= 1; nextInt--)
            expect(randomMock.nextInt(nextInt)).andReturn(1).times(1);
    }

    private void setExpectationsAGridCellIsNull() {
        setExpectationsLevelMaxRectangleMapObjects();
        setExpectationsSetNumberOfCardsForTheLevel(2);
        setUpExpectationsIsHiddenCardsRevealedAGridCellIsNull();
    }

    private void setUpExpectationsIsHiddenCardsRevealedAGridCellIsNull() {
        setUpExpectationsCard(80.0f);
        setUpExpectationsGetCardRectangleFromLevel();
        expectForHiddenCardTile(80.0f);
        setUpExpectationsCard(240.0f);
        setUpExpectationsGetCardRectangleFromLevel();
        expectForHiddenCardTile(240.0f);
        setUpExpectationsCardRevealed(3, false);
    }

    private void replayAll() {
        replay(levelMock,
               mapLayersMock,
               mapLayerMock,
               mapObjectsMock,
               rectangleMapObjectsMock,
               rectangleMapObjectMock,
               mapPropertiesMock,
               spriteBackMock,
               spriteFrontMock,
               textureAtlasMock,
               rectangleMock,
               Random.class,
               randomMock);

        if(reelTiles != null)
            for (int loop = 1; loop < 8; loop++)
                replay(reelTiles.get(loop * testMatrix[0].length + 1),
                               reelTiles.get(loop * testMatrix[0].length + 2));
    }

    private void verifyAll() {
        verify(levelMock,
               mapLayersMock,
               mapLayerMock,
               mapObjectsMock,
               rectangleMapObjectsMock,
               rectangleMapObjectsMock,
               mapObjectsMock,
               mapPropertiesMock,
               spriteBackMock,
               spriteFrontMock,
               textureAtlasMock,
               rectangleMock,
               Random.class,
               randomMock);

        if (reelTiles != null)
            for (int loop = 1; loop < 8; loop++)
                verify(reelTiles.get(loop * testMatrix[0].length + 1),
                       reelTiles.get(loop * testMatrix[0].length + 2));
    }

    private void tearDownMocks() {
        levelMock = null;
        textureAtlasMock = null;
        mapLayersMock = null;
        mapLayerMock = null;
        mapObjectsMock = null;
        rectangleMapObjectsMock = null;
        rectangleMapObjectMock = null;
        mapPropertiesMock = null;
        spriteBackMock = null;
        spriteFrontMock = null;
        rectangleMock = null;
        randomMock = null;
    }

    private void tearDownTestFixtures() {
        testMatrix = null;
        testGrid = null;
        reelTiles = null;
    }
}
