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

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.HoldLightButton;
import com.ellzone.slotpuzzle2d.sprites.ReelHelper;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class LevelObjectCreator {
    public static final String ADD_TO = "addTo";
    public static final String DELEGATE_TO_CALLBACK = "delegateToCallback";
    public static final String FIELD = "field";
    public static final String METHOD = "method";
    public static final String PROPERTY = "property";
    public static final String VALUE = "value";
    public static final String PARAMETER = "Parameter";
    public static final String PARAMETER_VALUE = "ParameterValue";
    public static final String COMPONENT = "Component";
    public static final String PROPERTY_NAME = "Property";
    public static final String PROPERTY_VALUE = "PropertyValue";
    public static final String DOT_REGULAR_EXPRESSION = "\\.";
    public static final String COLON = ":";
    public static final int LENGTH_TWO = 2;
    public static final int SECOND_PART = 1;

    public static final String INT = "int";
    public static final String LONG = "long";
    public static final String DOUBLE = "double";
    public static final String FLOAT = "float";
    public static final String BOOL = "bool";
    public static final String CHAR = "char";
    public static final String BYTE = "byte";
    public static final String VOID = "void";
    public static final String SHORT = "short";
    public static final String CLASS = "Class";
    public static final String ADD_COMPONENT_TO_ENTITY = "addComponentToEntity";

    protected Array<HoldLightButton> lightButtons = new Array<>();
    protected Array<AnimatedReel> reels = new Array<>();
    protected Array<PointLight> pointLights = new Array<>();
    protected SlotHandleSprite handle;
    protected ReelHelper reelHelper;
    protected LevelCreatorInjectionInterface levelCreatorInjectionInterface;
    protected World world;
    protected RayHandler rayHandler;
    protected Color reelPointLightColor = new Color(0.2f, 0.2f, 0.2f, 0.2f);

    public LevelObjectCreator(LevelCreatorInjectionInterface injection, World world, RayHandler rayHandler) {
        this.levelCreatorInjectionInterface = injection;
        this.world = world;
        this.rayHandler = rayHandler;
    };

    public void createLevel(Array<RectangleMapObject> levelRectangleMapObjects) throws GdxRuntimeException {
        Object createdObject = null;
        Object createdComponent = null;
        boolean huntingForComponents = true;
        int componentCount = 1;
        Array<String> components = new Array<>();
        if (levelRectangleMapObjects == null)
            throw new GdxRuntimeException(new IllegalArgumentException());

        for (RectangleMapObject rectangleMapObject : levelRectangleMapObjects) {
            ProcessCustomMapProperties processCustomMapProperties = new ProcessCustomMapProperties(huntingForComponents, componentCount, components, rectangleMapObject).invoke();
            huntingForComponents = processCustomMapProperties.isHuntingForComponents();
            componentCount = processCustomMapProperties.getComponentCount();
        }
    }

    public Array<HoldLightButton> getHoldLightButtons() {
        return lightButtons;
    }

    public Array<AnimatedReel> getAnimatedReels() {
        return reels;
    }

    public ReelSprites getReelSprites() {
        return levelCreatorInjectionInterface.getReelSprites();
    }

    public SlotHandleSprite getHandle() { return handle; }

    public AnnotationAssetManager annotationAssetManager() {
        return levelCreatorInjectionInterface.getAnnotationAssetManager();
    }

    public Texture getSlotReelScrollTexture() {
        return levelCreatorInjectionInterface.getSlotReelScrollTexture();
    }

    public Sound getReelSpinningSound() {
        return levelCreatorInjectionInterface.getAnnotationAssetManager()
                .get(AssetsAnnotation.SOUND_REEL_SPINNING);
    }

    public Sound getReelStoppingSound() {
        return levelCreatorInjectionInterface.getAnnotationAssetManager()
                                             .get(AssetsAnnotation.SOUND_REEL_STOPPED);
    }

    public TweenManager getTweenManager() {
        return levelCreatorInjectionInterface.getTweenManager();
    }

    public TextureAtlas getSlotHandleAtlas() {
        return levelCreatorInjectionInterface.getSlothandleAtlas();
    }

    private Object[] parseConstructorParameterValues(Array<String> constructorParameterValues,
                                                     Class<?>[] classParameters,
                                                     MapProperties rectangleMapProperties)
            throws NoSuchFieldException,
                   IllegalAccessException,
                   NoSuchMethodException,
                   InvocationTargetException {
        Object[] parameterValues = new Object[constructorParameterValues.size];
        int index = 0;
        for (String parameter : constructorParameterValues) {
            parameterValues[index] = parseParameterValue(parameter, classParameters[index], rectangleMapProperties);
            index++;
        }
        return parameterValues;
    }

    private Object parseParameterValue(String parameter,
                                       Class<?> classParam,
                                       MapProperties rectangleMapProperties)
            throws NoSuchFieldException,
                   IllegalAccessException,
                   NoSuchMethodException,
                   InvocationTargetException {
        if (parameter.toLowerCase().startsWith(FIELD))
            return parseField(parameter, classParam);
        if (parameter.toLowerCase().startsWith(METHOD))
            return parseMethod(parameter);
        if (parameter.toLowerCase().startsWith(PROPERTY))
            return parseProperty(parameter, classParam, rectangleMapProperties);
        if (parameter.toLowerCase().startsWith(VALUE))
            return parseValue(parameter, classParam);
        throw new GdxCouldNotParseParameterValueException(
                MessageFormat.format("Parameter={0}", parameter));
    }

    private Object parseValue(String parameter, Class<?> classParam) {
        String[] parts = parameter.split(COLON);
        String valuePart = parts.length == LENGTH_TWO ? parts[SECOND_PART] : null;
        if (isInt(classParam))
            return Integer.valueOf(valuePart);
        if (isFloat(classParam))
            return Float.valueOf(valuePart);
        if (isBoolean(classParam))
            return Boolean.valueOf(valuePart);
        if (isString(classParam))
            return valuePart;
        return null;
    }

    private Object parseField(String parameter, Class<?> classParam) throws NoSuchFieldException, IllegalAccessException {
        String[] parts = parameter.split(DOT_REGULAR_EXPRESSION);
        String fieldPart = parts.length == LENGTH_TWO ? parts[SECOND_PART] : null;
        Field field = this.getClass().getDeclaredField(fieldPart);
        return field.get(this);
    }

    private Object parseMethod(String parameter)
        throws
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] parts = parameter.split(DOT_REGULAR_EXPRESSION);
        String methodPart = parts.length == LENGTH_TWO ? parts[SECOND_PART] : null;
        Method method = this.getClass().getDeclaredMethod(methodPart);
        return method.invoke(this);
    }

    private Object parseProperty(String parameter, Class<?> classParam, MapProperties rectangleMapProperties) {
        String[] parts = parameter.split(DOT_REGULAR_EXPRESSION);
        if (isInt(classParam))
            return getInteger(classParam, rectangleMapProperties, parts);
        if (isFloat(classParam))
            return parts.length == LENGTH_TWO ? (Float) rectangleMapProperties.get(parts[1].toLowerCase(), classParam) : null;
        if (isBoolean(classParam))
            return parts.length == LENGTH_TWO ? (Boolean) (Object) rectangleMapProperties.get(parts[1].toLowerCase(), classParam) : null;

        return getObject(classParam, rectangleMapProperties, parts);
    }

    private Object getObject(Class<?> classParam, MapProperties rectangleMapProperties, String[] parts) {
        if (parts.length == LENGTH_TWO) {
            Object parsedObject = (Object) rectangleMapProperties.get(parts[1].toLowerCase(), classParam);
            if (parsedObject != null)
                return parsedObject;
            else throw new GdxCouldNotParsePropertyException("");
        } else
            throw new GdxCouldNotParsePropertyException("");
    }

    private Integer getInteger(Class<?> classParam, MapProperties rectangleMapProperties, String[] parts) throws GdxRuntimeException {
        if (parts.length == LENGTH_TWO) {
            Integer parsedInteger = convertToIntegerFromFloat((Float) rectangleMapProperties.get(parts[1].toLowerCase(), classParam));
            if (parsedInteger != null)
                return parsedInteger;
            else throw new GdxCouldNotParsePropertyException("");
        } else
            throw new GdxCouldNotParsePropertyException("");
    }

    private boolean isInt(Class clazz) {
        return clazz.equals(int.class);
    }

    private boolean isFloat(Class clazz) {
        return clazz.equals(float.class);
    }

    private boolean isBoolean(Class clazz) {
        return clazz.equals(boolean.class);
    }

    private boolean isString(Class clazz) { return clazz.equals(String.class); }


    private Integer convertToIntegerFromFloat(Float floatParam){
        return new Integer(floatParam.intValue());
    }

    Map<String,Class> builtInMap = new HashMap<String,Class>(); {
        builtInMap.put(INT, Integer.TYPE );
        builtInMap.put(LONG, Long.TYPE );
        builtInMap.put(DOUBLE, Double.TYPE );
        builtInMap.put(FLOAT, Float.TYPE );
        builtInMap.put(BOOL, Boolean.TYPE );
        builtInMap.put(CHAR, Character.TYPE );
        builtInMap.put(BYTE, Byte.TYPE );
        builtInMap.put(VOID, Void.TYPE );
        builtInMap.put(SHORT, Short.TYPE );
    }

    public Class<?>[] getParamTypes(Array<String> parameters){
        Class<?>[] classParameters = new Class<?>[parameters.size];
        int index = 0;

        for( String type : parameters) {
            try {
                if (builtInMap.containsKey(type))
                    classParameters[index++] = builtInMap.get(type);
                else
                    classParameters[index++] = Class.forName(type);
            } catch (ClassNotFoundException cnfe) {
                throw  new GdxRuntimeException(MessageFormat.format("Type {0} not found",type));
            }
        }
        return classParameters;
    }

    private void invokeObjectmethod(Object createdObject, String methodName) {
        try {
            Method method = this.getClass().getDeclaredMethod(methodName, createdObject.getClass());
            method.invoke(this, createdObject);
        } catch (NoSuchMethodException e) {
            throw new GdxRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new GdxRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new GdxRuntimeException(e);
        }
    }

    private void invokeComponentObjectMethod(Object createdObject, Object createdComponent, String methodName) {
        Class[] methodParameters = new Class[2];
        methodParameters[0] = createdObject.getClass();
        methodParameters[1] = Component.class;
        try {
            Method method = this.getClass().getDeclaredMethod(methodName, methodParameters);
            method.invoke(this, createdObject, createdComponent);
        } catch (NoSuchMethodException e) {
            throw new GdxRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new GdxRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new GdxRuntimeException(e);
        }
    }

    private class ProcessCustomMapProperties {
        private boolean huntingForComponents;
        private int componentCount;
        private Array<String> components;
        private RectangleMapObject rectangleMapObject;

        public ProcessCustomMapProperties(boolean huntingForComponents, int componentCount, Array<String> components, RectangleMapObject rectangleMapObject) {
            this.huntingForComponents = huntingForComponents;
            this.componentCount = componentCount;
            this.components = components;
            this.rectangleMapObject = rectangleMapObject;
        }

        public boolean isHuntingForComponents() {
            return huntingForComponents;
        }

        public int getComponentCount() {
            return componentCount;
        }

        public ProcessCustomMapProperties invoke() {
            MapProperties rectangleMapObjectProperties = rectangleMapObject.getProperties();
            Class<?> clazz = getClassFromProperties(rectangleMapObjectProperties);
            if (clazz != null)
                processMapProperties(rectangleMapObjectProperties, clazz);

            return this;
        }

        private void processMapProperties(MapProperties rectangleMapObjectProperties, Class<?> clazz) {
            Object createdObject;
            createdObject = createMapObjectFromProperties(rectangleMapObjectProperties, clazz);
            processComponents(rectangleMapObjectProperties, createdObject);
        }

        private Object createMapObjectFromProperties(MapProperties rectangleMapObjectProperties, Class<?> clazz) {
            Object createdObject;
            Array<String> constructorParameters = getParameters(rectangleMapObjectProperties, PARAMETER);
            Class<?>[] classParameters = getParamTypes(constructorParameters);
            Constructor<?> constructor = getConstructor(clazz, classParameters);
            Array<String> constructorParameterValues = getParameters(rectangleMapObjectProperties, PARAMETER_VALUE);
            createdObject = getCreatedObjectObject(rectangleMapObjectProperties, classParameters, constructor, constructorParameterValues);
            return createdObject;
        }

        private Object getCreatedObjectObject(MapProperties rectangleMapObjectProperties, Class<?>[] classParameters, Constructor<?> constructor, Array<String> constructorParameterValues) {
            Object createdObject;
            try {
                createdObject = getCreatedObject(rectangleMapObjectProperties, classParameters, constructor, constructorParameterValues);
                if (createdObject != null) {
                    invokeObjectmethod(createdObject, ADD_TO);
                    invokeObjectmethod(createdObject, DELEGATE_TO_CALLBACK);
                    componentCount = 1;
                    huntingForComponents = true;
                }
            } catch (Exception e) {
                throw new GdxRuntimeException(e);
            }
            return createdObject;
        }

        private void processComponents(MapProperties rectangleMapObjectProperties, Object createdObject) {
            while (huntingForComponents) {
                String component = parseComponent(COMPONENT + componentCount, rectangleMapObjectProperties);
                if (component != null) {
                    System.out.println(MessageFormat.format("{0}", component));
                    components.add(component);
                    Array<String> componentParameters = getParameters(rectangleMapObjectProperties, COMPONENT + componentCount + PROPERTY_NAME);
                    if (componentParameters != null) {
                        invokeCreatedComponentObjectMethod(rectangleMapObjectProperties, createdObject, component, componentParameters);
                    } else
                        huntingForComponents = false;
                } else
                    huntingForComponents = false;
            }
        }

        private void invokeCreatedComponentObjectMethod(MapProperties rectangleMapObjectProperties, Object createdObject, String component, Array<String> componentParameters) {
            Object createdComponent;
            Class<?>[] componentParameterTypes = getParamTypes(componentParameters);
            Array<String> componentParameterValues = getParameters(rectangleMapObjectProperties, COMPONENT + componentCount + "Value");
            Class<?> componentClass = getClass(component);
            Constructor<?> componentConstructor = getConstructor(componentClass, componentParameterTypes);
            try {
                createdComponent = getCreatedObject(rectangleMapObjectProperties, componentParameterTypes, componentConstructor, componentParameterValues);
                if (createdObject != null) {
                    invokeComponentObjectMethod(createdObject, createdComponent, ADD_COMPONENT_TO_ENTITY);
                    System.out.println(MessageFormat.format("About to create component {0}", component));
                }
            } catch (Exception e) {
                throw new GdxRuntimeException(e);
            }
            componentCount++;
        }

        private Class<?> getClassFromProperties(MapProperties rectangleMapObjectProperties) {
            String className = (String) rectangleMapObjectProperties.get(CLASS);
            System.out.println(String.format("creating className: %s", className));
            return className == null ? null : getClass(className);
        }

        private Class<?> getClass(String className) {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException cnfe) {
                System.out.println(String.format("Class not found %s.", clazz));
            }
            return clazz;
        }

        private Array<String> getParameters(MapProperties rectangleMapObjectProperties, String key) {
            int parameterCount = 1;
            Array<String> parameters = new Array<>();
            String parameterKey = key + String.valueOf(parameterCount);
            String parameterValue =  (String) rectangleMapObjectProperties.get(parameterKey);
            while (parameterValue != null) {
                parameters.add(parameterValue);
                parameterKey = key + String.valueOf(++parameterCount);
                parameterValue =  (String) rectangleMapObjectProperties.get(parameterKey);
            }
            return parameters;
        }

        private Constructor<?> getConstructor(Class<?> clazz, Class<?>[] classParameters)
            throws GdxRuntimeException {
            Constructor<?> constructor = null;
            try {
                constructor = clazz.getConstructor(classParameters);
            } catch (NoSuchMethodException nsme) {
                throw new GdxRuntimeException(
                        MessageFormat.format("No such constructor: {0}",
                                                            nsme.getMessage()));
            }
            return constructor;
        }

        private Object getCreatedObject(MapProperties rectangleMapproperties,
                                            Class<?>[] classParameters,
                                            Constructor<?> constructor,
                                            Array<String> constructorParameterValues)
                throws
                NoSuchFieldException,
                IllegalAccessException,
                InvocationTargetException,
                InstantiationException,
                NoSuchMethodException {
            Object[] constructorParametersValues = parseConstructorParameterValues(
                    constructorParameterValues, classParameters, rectangleMapproperties);
            return  constructor == null ? null : constructor.newInstance(constructorParametersValues);
        }

        private String parseComponent(String component, MapProperties rectangleMapObjectProperties) {
            return (String) rectangleMapObjectProperties.get(component);
        }
    }

    public class GdxCouldNotParsePropertyException extends GdxRuntimeException {
        public GdxCouldNotParsePropertyException(String message) {
            super(message);
        }
    }

    public class GdxCouldNotParseParameterValueException extends GdxRuntimeException {
        public GdxCouldNotParseParameterValueException(String message) {
            super(message);
        }
    }
}
