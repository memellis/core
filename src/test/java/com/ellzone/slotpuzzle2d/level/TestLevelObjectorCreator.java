package com.ellzone.slotpuzzle2d.level;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.utils.InputMatrix;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import box2dLight.RayHandler;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LevelObjectCreator.class, World.class})

public class TestLevelObjectorCreator {
    private World worldMock;
    private RayHandler rayHandlerMock;
    private TiledMap levelMock;
    private LevelCreatorInjectionInterface levelCreatorInjectionInterfaceMock;
    private int[][] testMatrix;
    private TupleValueIndex[][] testGrid;
    private MapLayers mapLayersMock;
    private MapLayer mapLayerMock;
    private MapObjects mapObjectsMock;
    private Array<RectangleMapObject> rectangleMapObjects;
    private MapProperties mapPropertiesMock;

    private enum LevelMapObjects {
        ANIMATED_REEL(1),
        LIGHT_BUTTON(2),
        POINT_LIGHT(3),
        REEL_HELPER(4);

        int ordinalValue = 0;
        LevelMapObjects(int ordinalValue) {
            this.ordinalValue = ordinalValue;
        }
    }

    @Test
    public void testLevelObjectCreatorNewInstance() {
        LevelObjectCreator levelObjectCreator = new LevelObjectCreator(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
    }

    @Test
    public void testCreateLevelMapWhenThereAreNoLevelObjects() {
        rectangleMapObjects = new Array<>();
        LevelObjectCreator levelObjectCreator = new LevelObjectCreator(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(rectangleMapObjects);
        assertThat(rectangleMapObjects.size, is(equalTo(0)));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithProperties() {
        rectangleMapObjects = new Array<>();
        rectangleMapObjects.add(createARectangleMockObject());
        for (RectangleMapObject rectangleMapObjectMocked : rectangleMapObjects)
            replay(rectangleMapObjectMocked);
        LevelObjectCreator levelObjectCreator = new LevelObjectCreator(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(rectangleMapObjects);
        assertThat(rectangleMapObjects.size, is(equalTo(1)));
        for (RectangleMapObject rectangleMapObjectMocked : rectangleMapObjects)
            verify(rectangleMapObjectMocked);
    }


    private RectangleMapObject createARectangleMockObject() {
        RectangleMapObject rectangleMapObjectMock1 = createMock(RectangleMapObject.class);
        MapProperties mapProperties1 = new MapProperties();
        mapProperties1.put("ID", 148);
        mapProperties1.put("Name", "Button");
        mapProperties1.put("Visible", true);
        mapProperties1.put("X", "320.0");
        mapProperties1.put("Y", "360.0");
        mapProperties1.put("Width", "40.0");
        mapProperties1.put("Height", "40.0");
        mapProperties1.put("Rotation", "0");
        expect(rectangleMapObjectMock1.getProperties()).andReturn(mapProperties1);
        return rectangleMapObjectMock1;
    }


    @Before
    public void setUp() {
        setUpMocks();
    }

    @After
    public void tearDown() {
        tearDownMocks();
    }

    private void setUpRectangleMapObectsWithOneEmptyRectangleMapObjectMock() {
        rectangleMapObjects = new Array<>();
        RectangleMapObject rectangleMapObject = createMock(RectangleMapObject.class);
        rectangleMapObjects.add(rectangleMapObject);
    }

    private void setUpLevel() {
        testMatrix = createLevelMatrix();
        assertThat(testMatrix.length, is(equalTo(9)));
        assertThat(testMatrix[0].length, is(equalTo(12)));
        int noOfEmptyMatrixCells = createGrid(testMatrix);
        assertThat(noOfEmptyMatrixCells, is(equalTo(testGrid.length * testGrid[0].length)));
    }

    private void setUpMocks() {
        worldMock = createMock(World.class);
        rayHandlerMock = createMock(RayHandler.class);
        levelMock = createMock(TiledMap.class);
        levelCreatorInjectionInterfaceMock = createMock(LevelCreatorInjectionInterface.class);
        mapLayersMock = createMock(MapLayers.class);
        mapLayerMock = createMock(MapLayer.class);
        mapObjectsMock = createMock(MapObjects.class);
        mapPropertiesMock = createMock(MapProperties.class);
    }

    private void tearDownMocks() {
        worldMock = null;
        rayHandlerMock = null;
        levelMock = null;
        levelCreatorInjectionInterfaceMock = null;
        mapLayersMock = null;
        mapLayerMock = null;
        mapObjectsMock = null;
        mapPropertiesMock = null;
    }

    private void replayAll() {
        levelMock = createMock(TiledMap.class);
        levelCreatorInjectionInterfaceMock = createMock(LevelCreatorInjectionInterface.class);
        mapLayersMock = createMock(MapLayers.class);
        mapLayerMock = createMock(MapLayer.class);
        mapObjectsMock = createMock(MapObjects.class);
        mapPropertiesMock = createMock(MapProperties.class);
    }

    private void verifyAll() {
    }

    private int[][] createLevelMatrix() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    private int createGrid(int[][] matrix) {
        int noOfEmptyMatrixCells = 0;
        testGrid = new TupleValueIndex[matrix.length][matrix[0].length];
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                testGrid[r][c] = new TupleValueIndex(r, c, r * matrix[0].length + c, matrix[r][c]);
                if (matrix[r][c] == -1)
                    noOfEmptyMatrixCells++;
            }
        }
        return noOfEmptyMatrixCells;
    }

}
