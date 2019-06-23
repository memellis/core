package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ellzone.slotpuzzle2d.level.fixtures.LevelObjectCreatorForTest;
import com.ellzone.slotpuzzle2d.level.fixtures.LevelObjectCreatorForTestWithNoDelegateToMethod;
import com.ellzone.slotpuzzle2d.level.fixtures.LevelObjectCreatorForTestWithNoMethods;
import com.ellzone.slotpuzzle2d.level.fixtures.ReflectionMapCreationClassForTesting;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import box2dLight.RayHandler;

import static junit.framework.TestCase.assertTrue;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LevelObjectCreator.class, World.class})

public class TestLevelObjectorCreator {
    public static final String REFLECTION_MAP_CREATION_CLASS_FOR_TESTING =
            "com.ellzone.slotpuzzle2d.level.fixtures.ReflectionMapCreationClassForTesting";
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
    public static final String VALUE_FALSE = "Value:false";
    public static final String PROPERTY_VISIBLE = "Property.Visible";
    public static final String PROPERTY_NAME = "Property.Name";
    public static final String JAVA_LANG_STRING = "java.lang.String";
    public static final String PROPERTY_NON_EXISTANT = "Property.NonExistant";
    public static final float X_POS_FLOAT_PROPERTY = 320.0f;
    public static final String BUTTON_PROPERTY = "Button";
    public static final float Y_POS_FLOAT_PROPERTY = 360.0f;
    public static final float WITDH_FLOAT_PROPERTY = 40.0f;
    public static final float HEIGHT_FLOAT_PROPERTY = 40.0f;
    public static final String PROPERTY_X = "Property.X";
    public static final String METHOD_GET_TEST_PUBLIC_FLOAT_FIELD = "Method.getTestPublicFloatField";
    public static final String FIELD_TEST_PUBLIC_FLOAT_FIELD = "Field.testPublicFloatField";
    public static final String JAVA_LANG_OBJECT = "java.lang.Object";
    public static final String VALUE_OBJECT = "Value:Object";
    private World worldMock;
    private RayHandler rayHandlerMock;
    private LevelCreatorInjectionInterface levelCreatorInjectionInterfaceMock;
    private Array<RectangleMapObject> rectangleMapObjects;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
        LevelObjectCreatorForTest levelObjectCreator = createLevelObjectCreatorForTest(
                createARectangleMockObjectWithAClassProperty(REFLECTION_MAP_CREATION_CLASS_FOR_TESTING));
        assertThat(rectangleMapObjects.size, is(equalTo(1)));
        assertTrue(levelObjectCreator.getReflectionMapCreationClassForTesting() instanceof ReflectionMapCreationClassForTesting);
        assertTrue(levelObjectCreator.getDelegatedToCallback());
        for (RectangleMapObject rectangleMapObjectMocked : rectangleMapObjects)
            verify(rectangleMapObjectMocked);
    }

    @Test(expected = GdxRuntimeException.class)
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndConstructorWithOneFloatArgumentAndNoValue() {
        createLevelObjectCreatorForTest(
                createARectangleMockObjectWithAClassPropertyAndConstructorWithOneFloatArgumentOnly(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithFloatArgumentAndValue() {
        LevelObjectCreatorForTest levelObjectCreator = createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithFloatArgumentAndValue(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        assertThat(levelObjectCreator.getReflectionMapCreationClassForTestingWithDifferentContstuctors().getTestFloatField(), CoreMatchers.<Float>is(equalTo(THREE_AS_FLOAT)));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithIntArgumentAndValue() {
        LevelObjectCreatorForTest levelObjectCreator = createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithIntArgumentAndValue(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        assertThat(levelObjectCreator.getReflectionMapCreationClassForTestingWithDifferentContstuctors().getTestIntField(), CoreMatchers.<Integer>is(equalTo(THREE_AS_INT)));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndContructorWithBooleanArgumentAndValue() {
        LevelObjectCreatorForTest levelObjectCreator = createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithBooleanArgumentAndValue(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        assertThat(levelObjectCreator.getReflectionMapCreationClassForTestingWithDifferentContstuctors().getTestBooleanField(), is(false));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectantleMapObjectWithClassWithFieldProperty() {
        LevelObjectCreatorForTest levelObjectCreator = newLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassWithFieldProperty(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        levelObjectCreator.testPublicFloatField = THREE_AS_FLOAT;
        levelObjectCreator.createLevel(rectangleMapObjects);
        assertThat(levelObjectCreator.getReflectionMapCreationClassForTestingWithDifferentContstuctors().getTestFloatField(), CoreMatchers.<Float>is(equalTo(THREE_AS_FLOAT)));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectantleMapObjectWithClassWithMethodProperty() {
        LevelObjectCreatorForTest levelObjectCreator = newLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassWithMethodProperty(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        levelObjectCreator.testPublicFloatField = THREE_AS_FLOAT;
        levelObjectCreator.createLevel(rectangleMapObjects);
        assertThat(levelObjectCreator.getReflectionMapCreationClassForTestingWithDifferentContstuctors().getTestFloatField(), CoreMatchers.<Float>is(equalTo(THREE_AS_FLOAT)));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectantleMapObjectWithClassWithPropertyFloatProperty() {
        LevelObjectCreatorForTest levelObjectCreator = createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassWithPropertyFloatProperty(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        assertThat(levelObjectCreator.
                      getReflectionMapCreationClassForTestingWithDifferentContstuctors().
                      getTestFloatField(),
                   CoreMatchers.<Float>is(equalTo(320.0f)));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectantleMapObjectWithClassWithPropertyIntProperty() {
        LevelObjectCreatorForTest levelObjectCreator = createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassWithPropertyIntProperty(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        assertThat(levelObjectCreator.
                     getReflectionMapCreationClassForTestingWithDifferentContstuctors().
                     getTestIntField(),
                   CoreMatchers.<Integer>is(equalTo(320)));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectantleMapObjectWithClassWithPropertyBooleanProperty() {
        LevelObjectCreatorForTest levelObjectCreator = createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassWithPropertyBooleanProperty(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        assertThat(levelObjectCreator.
                     getReflectionMapCreationClassForTestingWithDifferentContstuctors().
                     getTestBooleanField(),
                   CoreMatchers.<Boolean>is(equalTo(true)));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectantleMapObjectWithClassWithPropertyStringProperty() {
        LevelObjectCreatorForTest levelObjectCreator = createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassWithPropertyStringProperty(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        assertThat(levelObjectCreator.
                    getReflectionMapCreationClassForTestingWithDifferentContstuctors().
                    getTestFieldString(),
                   CoreMatchers.<String>is(equalTo(BUTTON_PROPERTY)));
    }

    public void testCreateLevelWhenThereIsOneRectangleMapObjectClassWithNonExistantProperty() {
        thrown.expect(GdxRuntimeException.class);
        thrown.expectCause(isA(LevelObjectCreator.GdxCouldNotParsePropertyException.class));
        createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassWithNonExistantProperty(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassNonParsableParameterValue() {
        thrown.expect(GdxRuntimeException.class);
        thrown.expectCause(isA(LevelObjectCreator.GdxCouldNotParseParameterValueException.class));
        createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassNonParsableParameterValue(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponent() {
        LevelObjectCreatorForTest levelObjectCreator = createLevelObjectCreatorForTest(
            createLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponent(
                       REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        assertThat(levelObjectCreator.getAddedToComponentEntity(), is(true));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndObjectParameterValue() {
        thrown.expect(GdxRuntimeException.class);
        thrown.expectCause(isA(LevelObjectCreator.GdxCouldNotParseParameterValueException.class));
        thrown.expectMessage(VALUE_OBJECT);
        createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassAndObjectParameterValue(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));

    }

    private RectangleMapObject createLevelWhenThereIsOneRectangleMapObjectWithClassAndObjectParameterValue(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, JAVA_LANG_OBJECT);
        customMapProperties.put(PARAMETER_VALUE_1, VALUE_OBJECT);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject createLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponent(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put("Component1", "com.ellzone.slotpuzzle2d.level.fixtures.TestComponent");
        customMapProperties.put(PARAMETER_1, JAVA_LANG_STRING);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_NAME);
        customMapProperties.put("Component1Property1", JAVA_LANG_STRING);
        customMapProperties.put("Component1Value1", "Value:Component1Value1");
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject createLevelWhenThereIsOneRectangleMapObjectWithClassNonParsableParameterValue(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, FLOAT);
        customMapProperties.put(PARAMETER_VALUE_1, "ABC");
        return createARectangleMockObject(customMapProperties);
    }

    private LevelObjectCreatorForTest newLevelObjectCreatorForTest(RectangleMapObject rectangleMapObject) {
        createRectangleMapObjects(rectangleMapObject);
        LevelObjectCreatorForTest levelObjectCreator = new LevelObjectCreatorForTest(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        return levelObjectCreator;
    }

    private LevelObjectCreatorForTest createLevelObjectCreatorForTest(RectangleMapObject rectangleMapObject) {
        createRectangleMapObjects(rectangleMapObject);
        LevelObjectCreatorForTest levelObjectCreator = newLevelObjectCreatorForTest(rectangleMapObject);
        levelObjectCreator.createLevel(rectangleMapObjects);
        return levelObjectCreator;
    }

    private void createRectangleMapObjects(RectangleMapObject rectangleMapObject) {
        rectangleMapObjects = new Array<>();
        rectangleMapObjects.add(rectangleMapObject);
        for (RectangleMapObject rectangleMapObjectMocked : rectangleMapObjects)
            replay(rectangleMapObjectMocked);
    }

    private RectangleMapObject createLevelWhenThereIsOneRectangleMapObjectWithClassWithNonExistantProperty(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, JAVA_LANG_STRING);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_NON_EXISTANT);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject createLevelWhenThereIsOneRectangleMapObjectWithClassWithPropertyStringProperty(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, JAVA_LANG_STRING);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_NAME);
        return createARectangleMockObject(customMapProperties);
    }


    private RectangleMapObject createLevelWhenThereIsOneRectangleMapObjectWithClassWithPropertyBooleanProperty(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, BOOLEAN);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_VISIBLE);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject createLevelWhenThereIsOneRectangleMapObjectWithClassWithPropertyIntProperty(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, INT);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_X);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject createLevelWhenThereIsOneRectangleMapObjectWithClassWithPropertyFloatProperty(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, FLOAT);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_X);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject createLevelWhenThereIsOneRectangleMapObjectWithClassWithMethodProperty(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, FLOAT);
        customMapProperties.put(PARAMETER_VALUE_1, METHOD_GET_TEST_PUBLIC_FLOAT_FIELD);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject createLevelWhenThereIsOneRectangleMapObjectWithClassWithFieldProperty(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, FLOAT);
        customMapProperties.put(PARAMETER_VALUE_1, FIELD_TEST_PUBLIC_FLOAT_FIELD);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject createLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithBooleanArgumentAndValue(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, BOOLEAN);
        customMapProperties.put(PARAMETER_VALUE_1, VALUE_FALSE);
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
        mapProperties1.put("name", BUTTON_PROPERTY);
        mapProperties1.put("visible", true);
        mapProperties1.put("x", X_POS_FLOAT_PROPERTY);
        mapProperties1.put("y", Y_POS_FLOAT_PROPERTY);
        mapProperties1.put("width", WITDH_FLOAT_PROPERTY);
        mapProperties1.put("height", HEIGHT_FLOAT_PROPERTY);
        mapProperties1.put("rotation", 0f);
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

    private void setUpMocks() {
        worldMock = createMock(World.class);
        rayHandlerMock = createMock(RayHandler.class);
        levelCreatorInjectionInterfaceMock = createMock(LevelCreatorInjectionInterface.class);
    }

    private void tearDownMocks() {
        worldMock = null;
        rayHandlerMock = null;
        levelCreatorInjectionInterfaceMock = null;
    }

    private void replayAll() {
        levelCreatorInjectionInterfaceMock = createMock(LevelCreatorInjectionInterface.class);
    }
}
