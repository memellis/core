package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ellzone.slotpuzzle2d.level.fixtures.LevelObjectCreatorForTest;
import com.ellzone.slotpuzzle2d.level.fixtures.LevelObjectCreatorForTestWithNoDelegateToMethod;
import com.ellzone.slotpuzzle2d.level.fixtures.LevelObjectCreatorForTestWithNoMethods;
import com.ellzone.slotpuzzle2d.level.fixtures.ReflectionMapCreationClassForTesting;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.utils.InputMatrix;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import box2dLight.RayHandler;

import static junit.framework.TestCase.assertTrue;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LevelObjectCreator.class, World.class})

public class TestLevelObjectorCreator {
    public static final String REFLECTION_MAP_CREATION_CLASS_FOR_TESTING = "com.ellzone.slotpuzzle2d.level.fixtures.ReflectionMapCreationClassForTesting";
    public static final String REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS =
            "com.ellzone.slotpuzzle2d.level.fixtures.ReflectionMapCreationClassForTestingWithDifferentContstuctors";
    public static final String PARAMETER_1 = "Parameter1";
    public static final String FLOAT = "float";
    public static final String INT = "int";
    public static final String BOOLEAN = "bool";
    public static final String PARAMETER_VALUE_1 = "ParameterValue1";
    public static final String VALUE_3 = "Value:3";
    public static final float THREE_AS_FLOAT = 3.0f;
    public static final int THREE_AS_INT = 3;
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

    @Test(expected = GdxRuntimeException.class)
    public void testCreateLevelWhenRectangleMapObjectsIsNull() {
        rectangleMapObjects = null;
        LevelObjectCreator levelObjectCreator = new LevelObjectCreator(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(rectangleMapObjects);
        assertThat(rectangleMapObjects, CoreMatchers.<Array<RectangleMapObject>>is((Array<RectangleMapObject>) equalTo(nullValue())));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithNoClassProperties() {
        rectangleMapObjects = new Array<>();
        rectangleMapObjects.add(createARectangleMockObject(new MapProperties()));
        for (RectangleMapObject rectangleMapObjectMocked : rectangleMapObjects)
            replay(rectangleMapObjectMocked);
        LevelObjectCreator levelObjectCreator =
                new LevelObjectCreator(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(rectangleMapObjects);
        assertThat(rectangleMapObjects.size, is(equalTo(1)));
        for (RectangleMapObject rectangleMapObjectMocked : rectangleMapObjects)
            verify(rectangleMapObjectMocked);
    }

    @Test(expected = GdxRuntimeException.class)
    public void testCreateLevelWhenThereisOneRectangleMapObjectWithClassPropertiesWithNoAddToMethod() {
        rectangleMapObjects = new Array<>();
        rectangleMapObjects.add(createARectangleMockObjectWithAClassProperty(REFLECTION_MAP_CREATION_CLASS_FOR_TESTING));
        for (RectangleMapObject rectangleMapObjectMocked : rectangleMapObjects)
            replay(rectangleMapObjectMocked);
        LevelObjectCreatorForTestWithNoMethods levelObjectCreator =
                new LevelObjectCreatorForTestWithNoMethods(
                        levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(rectangleMapObjects);
    }

    @Test(expected = GdxRuntimeException.class)
    public void testCreateLevelWhenThereisOneRectangleMapObjectWithClassPropertiesWithNoDelegateToMethod() {
        rectangleMapObjects = new Array<>();
        rectangleMapObjects.add(createARectangleMockObjectWithAClassProperty(REFLECTION_MAP_CREATION_CLASS_FOR_TESTING));
        for (RectangleMapObject rectangleMapObjectMocked : rectangleMapObjects)
            replay(rectangleMapObjectMocked);
        LevelObjectCreatorForTestWithNoDelegateToMethod levelObjectCreator =
                new LevelObjectCreatorForTestWithNoDelegateToMethod(
                        levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(rectangleMapObjects);
    }

    @Test
    public void testCreateLevelWhenThereisOneRectangleMapObjectWithClassProperties() {
        rectangleMapObjects = new Array<>();
        rectangleMapObjects.add(createARectangleMockObjectWithAClassProperty(REFLECTION_MAP_CREATION_CLASS_FOR_TESTING));
        for (RectangleMapObject rectangleMapObjectMocked : rectangleMapObjects)
            replay(rectangleMapObjectMocked);
        LevelObjectCreatorForTest levelObjectCreator = new LevelObjectCreatorForTest(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(rectangleMapObjects);
        assertThat(rectangleMapObjects.size, is(equalTo(1)));
        assertTrue(levelObjectCreator.getReflectionMapCreationClassForTesting() instanceof ReflectionMapCreationClassForTesting);
        assertTrue(levelObjectCreator.getDelegatedToCallback());
        for (RectangleMapObject rectangleMapObjectMocked : rectangleMapObjects)
            verify(rectangleMapObjectMocked);
    }

    @Test(expected = GdxRuntimeException.class)
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndConstructorWithOneFloatArgumentAndNoValue() {
        rectangleMapObjects = new Array<>();
        rectangleMapObjects.add(createARectangleMockObjectWithAClassPropertyAndConstructorWithOneFloatArgumentOnly(REFLECTION_MAP_CREATION_CLASS_FOR_TESTING));
        for (RectangleMapObject rectangleMapObjectMocked : rectangleMapObjects)
            replay(rectangleMapObjectMocked);
        LevelObjectCreatorForTest levelObjectCreator = new LevelObjectCreatorForTest(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(rectangleMapObjects);
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithFloatArgumentAndValue() {
        rectangleMapObjects = new Array<>();
        rectangleMapObjects.add(createLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithFloatArgumentAndValue(REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        for (RectangleMapObject rectangleMapObjectMocked : rectangleMapObjects)
            replay(rectangleMapObjectMocked);
        LevelObjectCreatorForTest levelObjectCreator = new LevelObjectCreatorForTest(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(rectangleMapObjects);
        assertThat(levelObjectCreator.getReflectionMapCreationClassForTestingWithDifferentContstuctors().getTestFloatField(), CoreMatchers.<Float>is(equalTo(THREE_AS_FLOAT)));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithIntArgumentAndValue() {
        rectangleMapObjects = new Array<>();
        rectangleMapObjects.add(createLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithIntArgumentAndValue(REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        for (RectangleMapObject rectangleMapObjectMocked : rectangleMapObjects)
            replay(rectangleMapObjectMocked);
        LevelObjectCreatorForTest levelObjectCreator = new LevelObjectCreatorForTest(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(rectangleMapObjects);
        assertThat(levelObjectCreator.getReflectionMapCreationClassForTestingWithDifferentContstuctors().getTestIntField(), CoreMatchers.<Integer>is(equalTo(THREE_AS_INT)));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithBooleanArgumentAndValue() {
        rectangleMapObjects = new Array<>();
        rectangleMapObjects.add(createLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithBooleanArgumentAndValue(REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        for (RectangleMapObject rectangleMapObjectMocked : rectangleMapObjects)
            replay(rectangleMapObjectMocked);
        LevelObjectCreatorForTest levelObjectCreator = new LevelObjectCreatorForTest(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(rectangleMapObjects);
        assertThat(levelObjectCreator.getReflectionMapCreationClassForTestingWithDifferentContstuctors().getTestBooleanField(), is(false));
    }

    private RectangleMapObject createLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithBooleanArgumentAndValue(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, BOOLEAN);
        customMapProperties.put(PARAMETER_VALUE_1, "Value:false");
        return createARectangleMockObject(customMapProperties);
    }


    private RectangleMapObject createLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithIntArgumentAndValue(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, INT);
        customMapProperties.put(PARAMETER_VALUE_1, VALUE_3);
        return createARectangleMockObject(customMapProperties);
    }


    private RectangleMapObject createLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithFloatArgumentAndValue(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, FLOAT);
        customMapProperties.put(PARAMETER_VALUE_1, VALUE_3);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject createARectangleMockObjectWithAClassProperty(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        return createARectangleMockObject(customMapProperties);
    }

    private MapProperties createClassProperty(String className) {
        MapProperties customMapProperties = new MapProperties();
        customMapProperties.put(LevelObjectCreator.CLASS, className);
        return customMapProperties;
    }

    private RectangleMapObject createARectangleMockObjectWithAClassPropertyAndConstructorWithOneFloatArgumentOnly(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, FLOAT);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject createARectangleMockObject(MapProperties customeMapProperties) {
        RectangleMapObject rectangleMapObjectMock1 = createMock(RectangleMapObject.class);
        MapProperties mapProperties = getMapProperties();
        mapProperties =  addCustomMapProperties(mapProperties, customeMapProperties);
        expect(rectangleMapObjectMock1.getProperties()).andReturn(mapProperties);
        return rectangleMapObjectMock1;
    }

    private MapProperties addCustomMapProperties(MapProperties mapProperties, MapProperties customeMapProperties) {
        mapProperties.putAll(customeMapProperties);
        return mapProperties;
    }

    private MapProperties getMapProperties() {
        MapProperties mapProperties1 = new MapProperties();
        mapProperties1.put("ID", 148);
        mapProperties1.put("Name", "Button");
        mapProperties1.put("Visible", true);
        mapProperties1.put("X", "320.0");
        mapProperties1.put("Y", "360.0");
        mapProperties1.put("Width", "40.0");
        mapProperties1.put("Height", "40.0");
        mapProperties1.put("Rotation", "0");
        return mapProperties1;
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
