package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.sprites.AnimatedHandle;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.HoldLightButton;
import com.ellzone.slotpuzzle2d.sprites.Reels;
import com.ellzone.slotpuzzle2d.sprites.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import box2dLight.RayHandler;

public class LevelObjectCreator {
    public static final String ADD_TO = "addTo";
    private Array<HoldLightButton> lightButtons = new Array<>();
    private Array<AnimatedReel> reels = new Array<>();
    private SlotHandleSprite handle;
    private LevelCreatorInjectionInterface levelCreatorInjectionInterface;
    private World world;
    private RayHandler rayHandler;

    public LevelObjectCreator(LevelCreatorInjectionInterface injection, World world, RayHandler rayHandler) {
        this.levelCreatorInjectionInterface = injection;
        this.world = world;
        this.rayHandler = rayHandler;
    };

    public void createLevel(Array<RectangleMapObject> levelRectangleMapObjects) throws IllegalAccessException {
        Object createdObject = null;
        for (RectangleMapObject rectangleMapObject : levelRectangleMapObjects) {
            MapProperties rectangleMapObjectProperties = rectangleMapObject.getProperties();
            Class<?> clazz = getClass(rectangleMapObjectProperties);
            Array<String> constructorParameters = getClassConstructorParameters(rectangleMapObjectProperties, "Parameter");
            Class<?>[] classParameters = getParamTypes(constructorParameters);

            Constructor<?> constructor = getConstructor(clazz, classParameters);
            Array<String> constructorParameterValues = getClassConstructorParameters(rectangleMapObjectProperties, "ParameterValue");
            try {
                createdObject = getCreatedObject(rectangleMapObjectProperties, classParameters, constructor, constructorParameterValues);
                if (createdObject != null)
                    invokeAddToMethod(createdObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Array<HoldLightButton> getHoldLightButtons() {
        return lightButtons;
    }

    public Array<AnimatedReel> getAnimatedReels() {
        return reels;
    }

    public Reels getReels() {
        return levelCreatorInjectionInterface.getReels();
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

    public void addTo(HoldLightButton objectToBeAdded) {
        lightButtons.add(objectToBeAdded);
    }

    public void addTo(AnimatedReel reel) {
        reels.add(reel);
    }

    public void addTo(SlotHandleSprite handle) { this.handle = handle; }

    private void invokeAddToMethod(Object createdObject) {
        try {
            Method method = this.getClass().getDeclaredMethod(ADD_TO, createdObject.getClass());
            method.invoke(this, createdObject);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
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
            NoSuchMethodException, ClassNotFoundException {
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

    private Class<?> getClass(MapProperties rectangleMapObjectProperties) {
        String className = (String) rectangleMapObjectProperties.get("Class");
        System.out.println(String.format("creating className: %s", className));
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException cnfe) {
            System.out.println(String.format("Class not found %s.", clazz));
        }
        return clazz;
    }

    private Object[] parseConstructorParameterValues(Array<String> constructorParameterValues, Class<?>[] classParameters, MapProperties rectangleMapProperties) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        Object[] parameterValues = new Object[constructorParameterValues.size];
        int index = 0;
        for (String parameter : constructorParameterValues) {
            parameterValues[index] = parseParameterValue(parameter, classParameters[index], rectangleMapProperties);
            index++;
        }
        return parameterValues;
    }

    private Object parseParameterValue(String parameter, Class<?> classParam, MapProperties rectangleMapProperties) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        if (parameter.toLowerCase().startsWith("field"))
            return parseField(parameter, classParam);
       if (parameter.toLowerCase().startsWith("method"))
            return parseMethod(parameter, classParam);
        if (parameter.toLowerCase().startsWith("property"))
            return parseProperty(parameter, classParam, rectangleMapProperties);
        if (parameter.toLowerCase().startsWith("value"))
            return parseValue(parameter, classParam);
        return null;
    }

    private Object parseValue(String parameter, Class<?> classParam) {
        String[] parts = parameter.split("\\.");
        String valuePart = parts.length == 2 ? parts[1] : null;
        if (isInt(classParam))
            return Integer.valueOf(valuePart);
        if (isFloat(classParam))
            return Float.valueOf(valuePart);
        return null;
    }

    private Object parseField(String parameter, Class<?> classParam) throws NoSuchFieldException, IllegalAccessException {
        String[] parts = parameter.split("\\.");
        String fieldPart = parts.length == 2 ? parts[1] : null;
        Field field = this.getClass().getDeclaredField(fieldPart);
        return field.get(this);
    }

    private Object parseMethod(String parameter, Class<?> classParam)
        throws
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] parts = parameter.split("\\.");
        String methodPart = parts.length == 2 ? parts[1] : null;
        Method method = this.getClass().getDeclaredMethod(methodPart);
        return method.invoke(this);
    }

    private Object parseProperty(String parameter, Class<?> classParam, MapProperties rectangleMapProperties) {
        String[] parts = parameter.split("\\.");
        if (isInt(classParam))
            return Math.round(parts.length == 2 ? (Float) (Object) rectangleMapProperties.get(parts[1].toLowerCase(), classParam) : null);
        else
            return parts.length == 2 ? (Object) rectangleMapProperties.get(parts[1].toLowerCase(), classParam) : null;
    }

    private boolean isInt(Class clazz) {
        return clazz.equals(int.class);
    }

    private boolean isFloat(Class clazz) {
        return clazz.equals(float.class);
    }

    private Array<String> getClassConstructorParameters(MapProperties rectangleMapObjectProperties, String key) {
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

    Map<String,Class> builtInMap = new HashMap<String,Class>();{
        builtInMap.put("int", Integer.TYPE );
        builtInMap.put("long", Long.TYPE );
        builtInMap.put("double", Double.TYPE );
        builtInMap.put("float", Float.TYPE );
        builtInMap.put("bool", Boolean.TYPE );
        builtInMap.put("char", Character.TYPE );
        builtInMap.put("byte", Byte.TYPE );
        builtInMap.put("void", Void.TYPE );
        builtInMap.put("short", Short.TYPE );
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
