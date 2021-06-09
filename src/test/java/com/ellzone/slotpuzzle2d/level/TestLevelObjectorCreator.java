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

package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.creator.LevelObjectCreator;
import com.ellzone.slotpuzzle2d.level.fixtures.LevelObjectCreatorForTest;
import com.ellzone.slotpuzzle2d.level.fixtures.LevelObjectCreatorForTestWithNoAddToComponentMethod;
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
    public static final String EXPECTED_NUMBER_OF_PROPERTY_PARTS_TO_BE_2_ACTUALLY_FOUND_3 = "Expected number of property parts to be 2 actually found 3";
    public static final String COMPONENT_1_PROPERTY_1 = "Component1Property1";
    public static final String INTEGER = "Integer";
    public static final String COMPONENT_1 = "Component1";
    public static final String COM_ELLZONE_SLOTPUZZLE_2D_LEVEL_FIXTURES_TEST_COMPONENT = "com.ellzone.slotpuzzle2d.level.fixtures.TestComponent";
    public static final String COULD_NOT_EXTRACT_INTEGER_FROM_3_14 = "Could not extract integer from <3 14>";
    public static final String COULD_NOT_EXTRACT_FLOAT_FROM_3_14 = "Could not extract float from <3 14>";
    public static final String COULD_NOT_EXTRACT_BOOLEAN_FROM_3_14 = "Could not extract boolean from <3 14>";
    public static final String NON_EXISTANT_CLASS = "non-existant class";
    public static final String PROPERTY_3_14 = "Property.3 14";
    public static final String PROPERTY_3_DOT_14 = "Property.3.14";
    public static final String COMPONENT_1_VALUE_1 = "Component1Value1";
    public static final String VALUE_COMPONENT_1_VALUE_1 = "Value:Component1Value1";
    public static final String ABC = "ABC";
    public static final String ID = "ID";
    public static final String NAME = "name";
    public static final String VISIBLE = "visible";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String ROTATION = "rotation";
    public static final float TEST_FLOAT = 320.0f;
    public static final int TEST_INTEGER = 320;
    public static final String NO_SUCH_GET_TEST_PUBLIC_FLOAT_FIELD = "com.ellzone.slotpuzzle2d.level.fixtures.LevelObjectCreatorForTest.noSuchGetTestPublicFloatField()";
    public static final String FOR_INPUT_STRING_3_14 = "For input string: \"3 14\"";
    public static final String VALUE_3_14 = "Value:3 14";
    public static final String METHOD_NO_SUCH_GET_TEST_PUBLIC_FLOAT_FIELD = "Method.noSuchGetTestPublicFloatField";
    public static final String NO_ADD_TO_COMPONENT_METHOD_ADD_COMPONENT_TO_ENTITY = "com.ellzone.slotpuzzle2d.level.fixtures.LevelObjectCreatorForTestWithNoAddToComponentMethod.addComponentToEntity";
    public static final String TEST_COMPONENT = "com.ellzone.slotpuzzle2d.level.fixtures.TestComponent";
    private World worldMock;
    private RayHandler rayHandlerMock;
    private LevelCreatorInjectionInterface levelCreatorInjectionInterfaceMock;
    private Array<MapObject> mapObjects;

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
        mapObjects = new Array<>();
        LevelObjectCreator levelObjectCreator = new LevelObjectCreator(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(mapObjects);
        assertThat(mapObjects.size, is(equalTo(0)));
    }

    @Test(expected = GdxRuntimeException.class)
    public void testCreateLevelWhenRectangleMapObjectsIsNull() {
        mapObjects = null;
        LevelObjectCreator levelObjectCreator = new LevelObjectCreator(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(mapObjects);
        assertThat(
                mapObjects,
                CoreMatchers.<Array<MapObject>>is((Array<MapObject>) equalTo(nullValue())));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithNoClassProperties() {
        mapObjects = new Array<>();
       mapObjects.add(createARectangleMockObject(new MapProperties()));
        for (MapObject mapObjectMocked : mapObjects)
            replay(mapObjectMocked);
        LevelObjectCreator levelObjectCreator =
                new LevelObjectCreator(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(mapObjects);
        assertThat(mapObjects.size, is(equalTo(1)));
        for (MapObject mapObjectMocked : mapObjects)
            verify(mapObjectMocked);
    }

    @Test(expected = GdxRuntimeException.class)
    public void testCreateLevelWhenThereisOneRectangleMapObjectWithClassPropertiesWithNoAddToMethod() {
        mapObjects = new Array<>();
        mapObjects.add(createARectangleMockObjectWithAClassProperty(REFLECTION_MAP_CREATION_CLASS_FOR_TESTING));
        for (MapObject mapObjectMocked : mapObjects)
            replay(mapObjectMocked);
        LevelObjectCreatorForTestWithNoMethods levelObjectCreator =
                new LevelObjectCreatorForTestWithNoMethods(
                        levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(mapObjects);
    }

    @Test(expected = GdxRuntimeException.class)
    public void testCreateLevelWhenThereisOneRectangleMapObjectWithClassPropertiesWithNoDelegateToMethod() {
        mapObjects = new Array<>();
        mapObjects.add(createARectangleMockObjectWithAClassProperty(REFLECTION_MAP_CREATION_CLASS_FOR_TESTING));
        for (MapObject mapObjectMocked : mapObjects)
            replay(mapObjectMocked);
        LevelObjectCreatorForTestWithNoDelegateToMethod levelObjectCreator =
                new LevelObjectCreatorForTestWithNoDelegateToMethod(
                        levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        levelObjectCreator.createLevel(mapObjects);
    }

    @Test
    public void testCreateLevelWhenThereisOneRectangleMapObjectWithClassProperties() {
        LevelObjectCreatorForTest levelObjectCreator = createLevelObjectCreatorForTest(
                createARectangleMockObjectWithAClassProperty(REFLECTION_MAP_CREATION_CLASS_FOR_TESTING));
        assertThat(mapObjects.size, is(equalTo(1)));
        assertTrue(levelObjectCreator.getReflectionMapCreationClassForTesting() instanceof
                      ReflectionMapCreationClassForTesting);
        assertTrue(levelObjectCreator.getDelegatedToCallback());
        for (MapObject rectangleMapObjectMocked : mapObjects)
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
        assertThat(levelObjectCreator.
                     getReflectionMapCreationClassForTestingWithDifferentContstuctors().
                     getTestFloatField(),
                   CoreMatchers.<Float>is(equalTo(THREE_AS_FLOAT)));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithIntArgumentAndValue() {
        LevelObjectCreatorForTest levelObjectCreator = createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithIntArgumentAndValue(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        assertThat(levelObjectCreator.
                     getReflectionMapCreationClassForTestingWithDifferentContstuctors().
                     getTestIntField(),
                   CoreMatchers.<Integer>is(equalTo(THREE_AS_INT)));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndContructorWithBooleanArgumentAndValue() {
        LevelObjectCreatorForTest levelObjectCreator = createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithBooleanArgumentAndValue(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        assertThat(levelObjectCreator.
                     getReflectionMapCreationClassForTestingWithDifferentContstuctors().
                     getTestBooleanField(),
                   is(false));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectantleMapObjectWithClassWithFieldProperty() {
        LevelObjectCreatorForTest levelObjectCreator = newLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassWithFieldProperty(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        levelObjectCreator.testPublicFloatField = THREE_AS_FLOAT;
        levelObjectCreator.createLevel(mapObjects);
        assertThat(levelObjectCreator.
                     getReflectionMapCreationClassForTestingWithDifferentContstuctors().
                     getTestFloatField(),
                   CoreMatchers.<Float>is(equalTo(THREE_AS_FLOAT)));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectantleMapObjectWithClassWithMethodProperty() {
        LevelObjectCreatorForTest levelObjectCreator = newLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassWithMethodProperty(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        levelObjectCreator.testPublicFloatField = THREE_AS_FLOAT;
        levelObjectCreator.createLevel(mapObjects);
        assertThat(levelObjectCreator.
                     getReflectionMapCreationClassForTestingWithDifferentContstuctors().
                     getTestFloatField(),
                   CoreMatchers.<Float>is(equalTo(THREE_AS_FLOAT)));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectantleMapObjectWithClassWithPropertyFloatProperty() {
        LevelObjectCreatorForTest levelObjectCreator = createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassWithPropertyFloatProperty(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        assertThat(levelObjectCreator.
                      getReflectionMapCreationClassForTestingWithDifferentContstuctors().
                      getTestFloatField(),
                   CoreMatchers.<Float>is(equalTo(TEST_FLOAT)));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectantleMapObjectWithClassWithPropertyIntProperty() {
        LevelObjectCreatorForTest levelObjectCreator = createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassWithPropertyIntProperty(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
        assertThat(levelObjectCreator.
                     getReflectionMapCreationClassForTestingWithDifferentContstuctors().
                     getTestIntField(),
                   CoreMatchers.<Integer>is(equalTo(TEST_INTEGER)));
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

    @Test
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
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndInvalidValueObjectParameterValue() {
        thrown.expect(GdxRuntimeException.class);
        thrown.expectCause(isA(LevelObjectCreator.GdxCouldNotParseParameterValueException.class));
        thrown.expectMessage(VALUE_OBJECT);
        createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassAndInvalidObjectParameterValue(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassObjectAndObjectParameterIsNotValid() {
        thrown.expect(GdxRuntimeException.class);
        thrown.expectCause(isA(LevelObjectCreator.GdxCouldNotParsePropertyException.class));
        thrown.expectMessage(EXPECTED_NUMBER_OF_PROPERTY_PARTS_TO_BE_2_ACTUALLY_FOUND_3);
        createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassObjectAndObjectParameterIsNotValid(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassObjectInvalidIntegerParamterValue() {
        thrown.expect(GdxRuntimeException.class);
        thrown.expectCause(isA(LevelObjectCreator.GdxCouldNotParsePropertyException.class));
        thrown.expectMessage(COULD_NOT_EXTRACT_INTEGER_FROM_3_14);
        createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassObjectInvalidIntegerParamterValue(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassObjectInvalidFloatParamterValue() {
        thrown.expect(GdxRuntimeException.class);
        thrown.expectCause(isA(LevelObjectCreator.GdxCouldNotParsePropertyException.class));
        thrown.expectMessage(COULD_NOT_EXTRACT_FLOAT_FROM_3_14);
        createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassObjectInvalidFloatParamterValue(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassObjectInvalidBooleanParamterValue() {
        thrown.expect(GdxRuntimeException.class);
        thrown.expectCause(isA(LevelObjectCreator.GdxCouldNotParsePropertyException.class));
        thrown.expectMessage(COULD_NOT_EXTRACT_BOOLEAN_FROM_3_14);
        createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassObjectInvalidBooleanParamterValue(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithNonExistantClass() {
        thrown.expect(GdxRuntimeException.class);
        thrown.expectCause(isA(ClassNotFoundException.class));
        thrown.expectMessage(NON_EXISTANT_CLASS);
        createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithNonExistantClass(
                        NON_EXISTANT_CLASS));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithNonExistantParameter() {
        thrown.expect(GdxRuntimeException.class);
        thrown.expectCause(isA(ClassNotFoundException.class));
        thrown.expectMessage(INTEGER);
        createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithNonExistantParameter(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponentWithNonExistantParameterType() {
        thrown.expect(GdxRuntimeException.class);
        thrown.expectCause(isA(ClassNotFoundException.class));
        thrown.expectMessage(INTEGER);
        createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponentWithNonExistantParamterType(
                    REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectantleMapObjectWithClassWithMethodPropertyInokingNonExistantMethod() {
        thrown.expect(GdxRuntimeException.class);
        thrown.expectCause(isA(NoSuchMethodException.class));
        thrown.expectMessage(NO_SUCH_GET_TEST_PUBLIC_FLOAT_FIELD);
        createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectantleMapObjectWithClassWithMethodPropertyInokingNonExistantMethod(
                    REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponentFailedToInvoke() {
        thrown.expect(GdxRuntimeException.class);
        thrown.expectCause(isA(NumberFormatException.class));
        thrown.expectMessage(FOR_INPUT_STRING_3_14);
        createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponentFailedToInvoke(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponentObjectMethodFailedToInvoke() {
        thrown.expect(GdxRuntimeException.class);
        thrown.expectCause(isA(NoSuchMethodException.class));
        thrown.expectMessage(NO_ADD_TO_COMPONENT_METHOD_ADD_COMPONENT_TO_ENTITY);
        createLevelObjectCreatorForTestWithNoAddToComponentMethod(
                createLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponentObjectMethodFailedToInvoke(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
    }

    @Test
    public void testCreateLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponentWithNoParameters() {
        thrown.expect(LevelObjectCreator.GdxComponentHasNoParametersException.class);
        thrown.expectMessage(TEST_COMPONENT);
        createLevelObjectCreatorForTest(
                createLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponentWithNoParameters(
                        REFLECTION_MAP_CREATION_CLASS_FOR_TESTING_WITH_DIFFERENT_CONSTRUCTORS));
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponentWithNoParameters(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(COMPONENT_1, TEST_COMPONENT);
        customMapProperties.put(PARAMETER_1, JAVA_LANG_STRING);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_NAME);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponentObjectMethodFailedToInvoke(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(COMPONENT_1, COM_ELLZONE_SLOTPUZZLE_2D_LEVEL_FIXTURES_TEST_COMPONENT);
        customMapProperties.put(PARAMETER_1, JAVA_LANG_STRING);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_NAME);
        customMapProperties.put(COMPONENT_1_PROPERTY_1, INT);
        customMapProperties.put(COMPONENT_1_VALUE_1, VALUE_3);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponentFailedToInvoke(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(COMPONENT_1, COM_ELLZONE_SLOTPUZZLE_2D_LEVEL_FIXTURES_TEST_COMPONENT);
        customMapProperties.put(PARAMETER_1, JAVA_LANG_STRING);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_NAME);
        customMapProperties.put(COMPONENT_1_PROPERTY_1, INT);
        customMapProperties.put(COMPONENT_1_VALUE_1, VALUE_3_14);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectantleMapObjectWithClassWithMethodPropertyInokingNonExistantMethod(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, FLOAT);
        customMapProperties.put(PARAMETER_VALUE_1,
                METHOD_NO_SUCH_GET_TEST_PUBLIC_FLOAT_FIELD);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponentWithNonExistantParamterType(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(COMPONENT_1, COM_ELLZONE_SLOTPUZZLE_2D_LEVEL_FIXTURES_TEST_COMPONENT);
        customMapProperties.put(PARAMETER_1, JAVA_LANG_STRING);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_NAME);
        customMapProperties.put(COMPONENT_1_PROPERTY_1, INTEGER);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithNonExistantParameter(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, INTEGER);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithNonExistantClass(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassObjectInvalidBooleanParamterValue(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, BOOLEAN);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_3_14);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassObjectInvalidFloatParamterValue(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, FLOAT);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_3_14);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassObjectInvalidIntegerParamterValue(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, INT);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_3_14);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassObjectAndObjectParameterIsNotValid(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, JAVA_LANG_OBJECT);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_3_DOT_14);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassAndInvalidObjectParameterValue(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, JAVA_LANG_OBJECT);
        customMapProperties.put(PARAMETER_VALUE_1, VALUE_OBJECT);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassAndAComponent(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(COMPONENT_1, COM_ELLZONE_SLOTPUZZLE_2D_LEVEL_FIXTURES_TEST_COMPONENT);
        customMapProperties.put(PARAMETER_1, JAVA_LANG_STRING);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_NAME);
        customMapProperties.put(COMPONENT_1_PROPERTY_1, JAVA_LANG_STRING);
        customMapProperties.put(COMPONENT_1_VALUE_1, VALUE_COMPONENT_1_VALUE_1);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassNonParsableParameterValue(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, FLOAT);
        customMapProperties.put(PARAMETER_VALUE_1, ABC);
        return createARectangleMockObject(customMapProperties);
    }

    private LevelObjectCreatorForTest newLevelObjectCreatorForTest(RectangleMapObject rectangleMapObject) {
        createRectangleMapObjects(rectangleMapObject);
        LevelObjectCreatorForTest levelObjectCreator = new LevelObjectCreatorForTest(
                levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        return levelObjectCreator;
    }

    private LevelObjectCreatorForTest createLevelObjectCreatorForTest(RectangleMapObject rectangleMapObject) {
        createRectangleMapObjects(rectangleMapObject);
        LevelObjectCreatorForTest levelObjectCreator = newLevelObjectCreatorForTest(rectangleMapObject);
        levelObjectCreator.createLevel(mapObjects);
        return levelObjectCreator;
    }

    private LevelObjectCreatorForTestWithNoAddToComponentMethod
    createLevelObjectCreatorForTestWithNoAddToComponentMethod(RectangleMapObject rectangleMapObject) {
        createRectangleMapObjects(rectangleMapObject);
        LevelObjectCreatorForTestWithNoAddToComponentMethod levelObjectCreator =
                newLevelObjectCreatorForTestWithNoAddToComponentMethod(rectangleMapObject);
        levelObjectCreator.createLevel(mapObjects);
        return levelObjectCreator;
    }

    private LevelObjectCreatorForTestWithNoAddToComponentMethod
    newLevelObjectCreatorForTestWithNoAddToComponentMethod(RectangleMapObject rectangleMapObject) {
        createRectangleMapObjects(rectangleMapObject);
        LevelObjectCreatorForTestWithNoAddToComponentMethod
                levelObjectCreator = new LevelObjectCreatorForTestWithNoAddToComponentMethod(
                    levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        return levelObjectCreator;
    }

    private void createRectangleMapObjects(RectangleMapObject rectangleMapObject) {
        mapObjects = new Array<>();
        mapObjects.add(rectangleMapObject);
        for (MapObject rectangleMapObjectMocked : mapObjects)
            replay(rectangleMapObjectMocked);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassWithNonExistantProperty(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, JAVA_LANG_STRING);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_NON_EXISTANT);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassWithPropertyStringProperty(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, JAVA_LANG_STRING);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_NAME);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassWithPropertyBooleanProperty(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, BOOLEAN);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_VISIBLE);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassWithPropertyIntProperty(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, INT);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_X);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassWithPropertyFloatProperty(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, FLOAT);
        customMapProperties.put(PARAMETER_VALUE_1, PROPERTY_X);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassWithMethodProperty(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, FLOAT);
        customMapProperties.put(PARAMETER_VALUE_1, METHOD_GET_TEST_PUBLIC_FLOAT_FIELD);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassWithFieldProperty(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, FLOAT);
        customMapProperties.put(PARAMETER_VALUE_1, FIELD_TEST_PUBLIC_FLOAT_FIELD);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithBooleanArgumentAndValue(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, BOOLEAN);
        customMapProperties.put(PARAMETER_VALUE_1, VALUE_FALSE);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithIntArgumentAndValue(String className) {
        MapProperties customMapProperties = createClassProperty(className);
        customMapProperties.put(PARAMETER_1, INT);
        customMapProperties.put(PARAMETER_VALUE_1, VALUE_3);
        return createARectangleMockObject(customMapProperties);
    }

    private RectangleMapObject
    createLevelWhenThereIsOneRectangleMapObjectWithClassAndContructotWithFloatArgumentAndValue(String className) {
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

    private RectangleMapObject
    createARectangleMockObjectWithAClassPropertyAndConstructorWithOneFloatArgumentOnly(String className) {
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
        mapProperties1.put(ID, 148);
        mapProperties1.put(NAME, BUTTON_PROPERTY);
        mapProperties1.put(VISIBLE, true);
        mapProperties1.put(X, X_POS_FLOAT_PROPERTY);
        mapProperties1.put(Y, Y_POS_FLOAT_PROPERTY);
        mapProperties1.put(WIDTH, WITDH_FLOAT_PROPERTY);
        mapProperties1.put(HEIGHT, HEIGHT_FLOAT_PROPERTY);
        mapProperties1.put(ROTATION, 0f);
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
}
