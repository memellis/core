package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreator;
import com.ellzone.slotpuzzle2d.level.hidden.HiddenPattern;
import com.ellzone.slotpuzzle2d.level.hidden.HiddenPlayingCard;
import com.ellzone.slotpuzzle2d.level.hidden.HiddenShape;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle2d.utils.InputMatrix;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HiddenPlayingCard.class, ReelTile.class})

public class TestHiddenShape {
    private TiledMap levelMock;
    private TextureAtlas textureAtlasMock;
    private MapLayers mapLayersMock;
    private MapLayer mapLayerMock;
    private MapObjects mapObjectsMock;
    private Array<RectangleMapObject> rectangleMapObjects;
    private MapProperties mapPropertiesMock;
    private int[][] testMatrix;
    private TupleValueIndex[][] testGrid;
    private Array<ReelTile> reelTiles;
    private int[][] hiddenShapeMatrix;

    @Before
    public void setUp() {
        setUpMocks();
    }

    private void setUpMocks() {
        levelMock = createMock(TiledMap.class);
        textureAtlasMock = createMock(TextureAtlas.class);
        mapLayersMock = createMock(MapLayers.class);
        mapLayerMock = createMock(MapLayer.class);
        mapObjectsMock = createMock(MapObjects.class);
        mapPropertiesMock = createMock(MapProperties.class);
    }

    @After
    public void tearDown() {
        tearDownMocks();
    }

    private void tearDownMocks() {
        levelMock = null;
        textureAtlasMock = null;
        mapLayersMock = null;
        mapLayerMock = null;
        mapObjectsMock = null;
        rectangleMapObjects = null;
        mapPropertiesMock = null;
    }

    @Test
    public void testIsPatternRevealed() {
        testMatrix = createMatrixPatternRevealed();
        createGrid(testMatrix);
        rectangleMapObjects = createMockLevelHiddenShape();
        setExpectations(true);
        replayAll();
        HiddenPattern hiddenPattern = new HiddenShape(levelMock);
        assertThat(hiddenPattern.isHiddenPatternRevealed(testGrid,
                                                         reelTiles,
                                                         testGrid[0].length,
                                                         testGrid.length),
                                                         is(true));
        verifyAll();
    }

    @Test
    public void testIsNotPatternRevealed() {
        testMatrix = createMatrixNoPatternRevealed();
        createGrid(testMatrix);
        rectangleMapObjects = createMockLevelHiddenShape();
        setExpectations(false);
        replayAll();
        HiddenPattern hiddenPattern = new HiddenShape(levelMock);
        assertThat(hiddenPattern.isHiddenPatternRevealed(testGrid,
                                                         reelTiles,
                                                         testGrid[0].length,
                                                         testGrid.length),
                                                         is(false));
        verifyAll();
    }

    @Test(expected = HiddenPattern.HiddenPatternPuzzleGridException.class)
    public void isHiddenPlayingShapeRevealeThrowsHiddenPlayingCardPuzzleGridException() {
        testMatrix = createMatrixPatternRevealed();
        createGrid(testMatrix);
        rectangleMapObjects = createMockLevelHiddenShape();
        testGrid[6][1] = null;

        setExpectations(true);
        replayAll();
        HiddenPattern hiddenPattern = new HiddenShape(levelMock);
        assertThat(hiddenPattern.isHiddenPatternRevealed(testGrid,
                                                         reelTiles,
                                                         testGrid[0].length,
                                                         testGrid.length),
                is(true));
        verifyAll();
    }

    @Test(expected = HiddenPattern.HiddenPatternPuzzleGridException.class)
    public void isHiddenPlayingCardRevealeThrowsHiddenPlayingCardPuzzleGridExceptionForGridColumnLessThan0() {
        testAMapRectangleisOutOfRange(40, 0);
    }

    @Test(expected = HiddenPattern.HiddenPatternPuzzleGridException.class)
    public void isHiddenPlayingCardRevealeThrowsHiddenPlayingCardPuzzleGridExceptionForGridColumnGreaterThanGridWidth() {
        testAMapRectangleisOutOfRange(640, 0);
    }

    @Test(expected = HiddenPattern.HiddenPatternPuzzleGridException.class)
    public void isHiddenPlayingCardRevealeThrowsHiddenPlayingCardPuzzleGridExceptionForGridRowGreaterLessThanGridWidth() {
        testAMapRectangleisOutOfRange(160, -40);
    }

    @Test(expected = HiddenPattern.HiddenPatternPuzzleGridException.class)
    public void isHiddenPlayingCardRevealeThrowsHiddenPlayingCardPuzzleGridExceptionForGridRowGreaterThanGridWidth() {
        testAMapRectangleisOutOfRange(160, 480);
    }

    private void testAMapRectangleisOutOfRange(int cardX, int cardY) {
        testMatrix = createMatrixPatternRevealed();
        createGrid(testMatrix);
        rectangleMapObjects = createMockLevelHiddenShape();
        rectangleMapObjects.get(0).getRectangle().x = cardX;
        rectangleMapObjects.get(0).getRectangle().y = cardY;
        setExpectations(true);
        replayAll();
        HiddenPattern hiddenPattern = new HiddenShape(levelMock);
        assertThat(hiddenPattern.isHiddenPatternRevealed(testGrid,
                                                         reelTiles,
                                                         testGrid[0].length,
                                                         testGrid.length),
                                                         is(true));
        verifyAll();
    }


    private int[][] createMatrixPatternRevealed() {
        String matrixToInput = "11 x 9\n"
                             + "0  1  2  3  4  5  6  7  8  9  10\n"
                             + "0  1 -1 -1  4  5  6  7  8  9  10\n"
                             + "0 -1  2  3 -1  5  6  7  8  9  10\n"
                             + "0 -1  2  3  4  5  6  7  8  9  10\n"
                             + "0  1 -1 -1  4  5  6  7  8  9  10\n"
                             + "0  1  2  3 -1  5  6  7  8  9  10\n"
                             + "0 -1 -1  3 -1  5  6  7  8  9  10\n"
                             + "0  1 -1 -1  4  5  6  7  8  9  10\n"
                             + "0  1  2  3  4  5  6  7  8  9  10\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    private int[][] createMatrixNoPatternRevealed() {
        String matrixToInput = "11 x 9\n"
                             + "0  1  2  3  4  5  6  7  8  9  10\n"
                             + "0  1 -1 -1  4  5  6  7  8  9  10\n"
                             + "0 -1  2  3 -1  5  6  7  8  9  10\n"
                             + "0 -1  2  3  4  5  6  7  8  9  10\n"
                             + "0  1 -1 -1  4  5  6  7  8  9  10\n"
                             + "0  1  2  3 -1  5  6  7  8  9  10\n"
                             + "0 -1  2  3 -1  5  6  7  8  9  10\n"
                             + "0  1  2 -1  4  5  6  7  8  9  10\n"
                             + "0  1  2  3  4  5  6  7  8  9  10\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    private void createGrid(int[][] matrix) {
        testGrid = new TupleValueIndex[matrix.length][matrix[0].length];
        reelTiles = new Array<>();
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                testGrid[r][c] = new TupleValueIndex(r, c, r * matrix[0].length + c, matrix[r][c]);
                ReelTile reelTileMock = PowerMock.createMock(ReelTile.class);
                Whitebox.setInternalState(reelTileMock,"x", PlayScreen.PUZZLE_GRID_START_X + (c * 40));
                Whitebox.setInternalState(reelTileMock,"y",(r  * 40) - PlayScreen.PUZZLE_GRID_START_Y);
                Whitebox.setInternalState(reelTileMock, "tileDeleted", matrix[r][c] < 0);
                Whitebox.setInternalState(reelTileMock, "index", r * testGrid[0].length + c);
                reelTiles.add(reelTileMock);
            }
        }
    }

    private int[][] createHiddenShapeMatix() {
        String matrixToInput = "6 x 12\n"
                             + "200 120 40 40 6 1\n"
                             + "240  80 40 40 7 2\n"
                             + "280  80 40 40 7 3\n"
                             + "320 120 40 40 6 4\n"
                             + "320 160 40 40 5 4\n"
                             + "280 200 40 40 4 3\n"
                             + "240 200 40 40 4 2\n"
                             + "200 240 40 40 3 1\n"
                             + "200 280 40 40 2 1\n"
                             + "240 320 40 40 1 2\n"
                             + "280 320 40 40 1 3\n"
                             + "320 280 40 40 2 4\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    private Array<RectangleMapObject> createMockLevelHiddenShape() {
        hiddenShapeMatrix = createHiddenShapeMatix();
        Array<RectangleMapObject> rectangleMapObjects = new Array<>();
        for (int row = 0; row < hiddenShapeMatrix.length; row++) {
            RectangleMapObject rectangleMapObject =
                    new RectangleMapObject(hiddenShapeMatrix[row][0],
                                           hiddenShapeMatrix[row][1],
                                           hiddenShapeMatrix[row][2],
                                           hiddenShapeMatrix[row][3]);
            rectangleMapObjects.add(rectangleMapObject);
        }
        return rectangleMapObjects;
    }

    private void setExpectations(boolean expectPatternRevealed) {
        setUpExpectationsIsHiddenPatternRevealed(expectPatternRevealed);
    }

    private void setUpExpectationsIsHiddenPatternRevealed(boolean expectPatternRevealed) {
        expectGetRectangleMapObject();
        if (expectPatternRevealed)
            expectReelTiles();
        else
            expectReelTiles();
    }

    private void expectGetRectangleMapObject() {
        expect(levelMock.getLayers()).andReturn(mapLayersMock);
        expect(mapLayersMock.get(LevelCreator.HIDDEN_PATTERN_LAYER_NAME)).andReturn(mapLayerMock);
        expect(mapLayerMock.getObjects()).andReturn(mapObjectsMock);
        expect(mapObjectsMock.getByType(RectangleMapObject.class)).andReturn(rectangleMapObjects);
    }

    private void expectReelTiles() {
        hiddenShapeMatrix = createHiddenShapeMatix();
        for (int row = 0; row < hiddenShapeMatrix.length; row++) {
            if (testGrid[hiddenShapeMatrix[row][4]][hiddenShapeMatrix[row][5]] != null) {
                int reelTileIndex = testGrid[hiddenShapeMatrix[row][4]][hiddenShapeMatrix[row][5]].getIndex();
                int reelTileValue = testGrid[hiddenShapeMatrix[row][4]][hiddenShapeMatrix[row][5]].getValue();

                expect(reelTiles.get(reelTileIndex).isReelTileDeleted()).andReturn(reelTileValue == -1);
            }
        }
    }

    private void replayAll() {
        replay(levelMock,
               mapLayersMock,
               mapLayerMock,
               mapObjectsMock,
               mapPropertiesMock,
               textureAtlasMock);

        if(reelTiles != null)
            for (int row = 0; row < hiddenShapeMatrix.length; row++) {
                if (testGrid[hiddenShapeMatrix[row][4]][hiddenShapeMatrix[row][5]] != null) {
                    int reelTileIndex = testGrid[hiddenShapeMatrix[row][4]][hiddenShapeMatrix[row][5]].getIndex();
                    replay(reelTiles.get(reelTileIndex));
                }
            }
    }

    private void verifyAll() {
        verify(levelMock,
               mapLayersMock,
               mapLayerMock,
               mapObjectsMock,
               mapPropertiesMock,
               textureAtlasMock);

        if(reelTiles != null)
            for (int row = 0; row < hiddenShapeMatrix.length; row++) {
                if (testGrid[hiddenShapeMatrix[row][4]][hiddenShapeMatrix[row][5]] != null) {
                    int reelTileIndex = testGrid[hiddenShapeMatrix[row][4]][hiddenShapeMatrix[row][5]].getIndex();
                    replay(reelTiles.get(reelTileIndex));
                }
            }
    }
}
