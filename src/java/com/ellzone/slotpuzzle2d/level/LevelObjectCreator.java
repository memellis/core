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

    private Array<HoldLightButton> lightButtons = new Array<>();
    private Array<AnimatedReel> reels = new Array<>();
    private Array<PointLight> pointLights = new Array<>();
    private SlotHandleSprite handle;
    private LevelCreatorInjectionInterface levelCreatorInjectionInterface;
    private World world;
    private RayHandler rayHandler;
    private Color reelPointLightColor = new Color(0.2f, 0.2f, 0.2f, 0.2f);
    private LevelHoldLightButtonCallback levelHoldLightButtonCallback;
    private LevelAnimatedReelCallback levelAnimatedReelCallback;
    private LevelSlotHandleSpriteCallback levelSlotHandleSpriteCallback;
    private LevelPointLightCallback levelPointLightCallback;
    private LevelReelHelperCallback levelReelHelperCallback;

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
        for (RectangleMapObject rectangleMapObject : levelRectangleMapObjects) {
            MapProperties rectangleMapObjectProperties = rectangleMapObject.getProperties();
            Class<?> clazz = getClass(rectangleMapObjectProperties);
            Array<String> constructorParameters = getParameters(rectangleMapObjectProperties, PARAMETER);
            Class<?>[] classParameters = getParamTypes(constructorParameters);
            Constructor<?> constructor = getConstructor(clazz, classParameters);
            Array<String> constructorParameterValues = getParameters(rectangleMapObjectProperties, PARAMETER_VALUE);
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
            while (huntingForComponents) {
                String component = parseComponent(COMPONENT + componentCount, rectangleMapObjectProperties);
                if (component != null) {
                    System.out.println(MessageFormat.format("{0}", component));
                    components.add(component);
                    Array<String> componentParameters = getParameters(rectangleMapObjectProperties, COMPONENT + componentCount + PROPERTY_NAME);
                    if (componentParameters != null) {
                        Class<?>[] componentParameterTypes = getParamTypes(componentParameters);
                        Array<String> componentParameterValues = getParameters(rectangleMapObjectProperties, COMPONENT + componentCount + "Value");
                        Class<?> componentClass = getClass(component);
                        Constructor<?> componentConstructor = getConstructor(componentClass, componentParameterTypes);
                        try {
                            createdComponent = getCreatedObject(rectangleMapObjectProperties, componentParameterTypes, componentConstructor, componentParameterValues);
                            if (createdObject != null) {
                                invokeComponentObjectmethod(createdObject, createdComponent, ADD_COMPONENT_TO_ENTITY);
                                System.out.println(MessageFormat.format("About to create component {0}", component));
                            }
                        } catch (Exception e) {
                            throw new GdxRuntimeException(e);
                        }
                        componentCount++;
                    } else
                        huntingForComponents = false;
                } else
                    huntingForComponents = false;
            }
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

    public void addTo(HoldLightButton holdLightButton) {
        lightButtons.add(holdLightButton);
    }

    public void addTo(AnimatedReel reel) {
        reels.add(reel);
    }

    public void addTo(SlotHandleSprite handle) { this.handle = handle; }

    public void addTo(PointLight pointLight) { pointLights.add(pointLight);}

    public void addTo(ReelHelper reelHelper) {}

    public void addHoldLightButtonCallback(LevelHoldLightButtonCallback callback) {
        this.levelHoldLightButtonCallback = callback;
    }

    public void addAnimatedReelCallback(LevelAnimatedReelCallback callback) {
        this.levelAnimatedReelCallback = callback;
    }

    public void addSlotHandleCallback(LevelSlotHandleSpriteCallback callback) {
        this.levelSlotHandleSpriteCallback = callback;
    }

    public void addPointLightCallback(LevelPointLightCallback callback) {
        this.levelPointLightCallback = callback;
    }

    public void addReelHelperCallback(LevelReelHelperCallback callback) {
        this.levelReelHelperCallback = callback;
    }

    private void delegateToCallback(HoldLightButton holdLightButton) {
        if (levelHoldLightButtonCallback != null)
            levelHoldLightButtonCallback.onEvent(holdLightButton);
    }

    public void addComponentToEntity(PointLight pointLight, Component component) {
        if (levelPointLightCallback != null)
            levelPointLightCallback.addComponent(component);
    }

    public void addComponentToEntity(HoldLightButton holdLightButton, Component component) {
        if (levelHoldLightButtonCallback != null)
            levelHoldLightButtonCallback.addComponent(component);
    }

    public void addComponentToEntity(ReelHelper reelHelper, Component component) {
        if (levelReelHelperCallback != null)
            levelReelHelperCallback.addComponent(component);
    }

    private void delegateToCallback(AnimatedReel animatedReel) {
        if (levelPointLightCallback != null)
            levelAnimatedReelCallback.onEvent(animatedReel);
    }

    private void delegateToCallback(SlotHandleSprite slotHandleSprite) {
        if (levelPointLightCallback != null)
            levelSlotHandleSpriteCallback.onEvent(slotHandleSprite);
    }


    private void delegateToCallback(PointLight pointLight) {
        if (levelPointLightCallback != null)
            levelPointLightCallback.onEvent(pointLight);
    }

    private void delegateToCallback(ReelHelper reelHelper) {
        if (levelReelHelperCallback != null)
            levelReelHelperCallback.onEvent(reelHelper);
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

    private void invokeComponentObjectmethod(Object createdObject, Object createdComponent, String methodName) {
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

    private Constructor<?> getConstructor(Class<?> clazz, Class<?>[] classParameters) {
        Constructor<?> constructor = null;
        try {
            constructor = clazz.getConstructor(classParameters);
        } catch (NoSuchMethodException nsme) {
            System.out.println(String.format("No such constructor exception %s",
                    nsme.getMessage()));
        }
        return constructor;
    }

    private String parseComponent(String component, MapProperties rectangleMapObjectProperties) {
        return (String) rectangleMapObjectProperties.get(component);
    }

    private Class<?> getClass(MapProperties rectangleMapObjectProperties) {
        String className = (String) rectangleMapObjectProperties.get(CLASS);
        System.out.println(String.format("creating className: %s", className));
        return getClass(className);
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
        return null;
    }

    private Object parseValue(String parameter, Class<?> classParam) {
        String[] parts = parameter.split(COLON);
        String valuePart = parts.length == LENGTH_TWO ? parts[SECOND_PART] : null;
        if (isInt(classParam))
            return Integer.valueOf(valuePart);
        if (isFloat(classParam))
            return Float.valueOf(valuePart);
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
            return Math.round(parts.length == LENGTH_TWO ? (Float) (Object) rectangleMapProperties.get(parts[1].toLowerCase(), classParam) : null);
        else
            return parts.length == LENGTH_TWO ? (Object) rectangleMapProperties.get(parts[1].toLowerCase(), classParam) : null;
    }

    private boolean isInt(Class clazz) {
        return clazz.equals(int.class);
    }

    private boolean isFloat(Class clazz) {
        return clazz.equals(float.class);
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

    Map<String,Class> builtInMap = new HashMap<String,Class>();
    {
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
                System.out.println(cnfe.getMessage());
            }
        }
        return classParameters;
    }
}
