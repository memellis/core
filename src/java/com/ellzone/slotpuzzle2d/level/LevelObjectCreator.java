package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.sprites.HoldLightButton;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import box2dLight.RayHandler;

public class LevelObjectCreator {
    public static final String ADD_TO = "addTo";
    public Array<HoldLightButton> lightButtons = new Array<>();
    private World world;
    private RayHandler rayHandler;

    public LevelObjectCreator(World world, RayHandler rayHandler) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            addToField(createdObject, rectangleMapObjectProperties);
        }
    }

    public void addTo(HoldLightButton holdLightButton) {
        lightButtons.add(holdLightButton);
    }

    private void addToField(Object createdObject, MapProperties rectangleMapProperties) throws IllegalAccessException {
        if (createdObject != null) {
            Field field = null;
            try {
                String addToFieldMethodName = (String) rectangleMapProperties.get("addToField");
                if (addToFieldMethodName != null)
                    field = this.getClass().getDeclaredField(addToFieldMethodName);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            if (field != null) {
                field.setAccessible(true);
                invokeAddToMethod(createdObject);
            }
        }
    }

    private void invokeAddToMethod(Object createdObject) {
        try {
            Method method = this.getClass().getDeclaredMethod(ADD_TO, HoldLightButton.class);
            method.invoke(this, (HoldLightButton) createdObject);
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

    private Object[] parseConstructorParameterValues(Array<String> constructorParameterValues, Class<?>[] classParameters, MapProperties rectangleMapProperties) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Object[] parameterValues = new Object[constructorParameterValues.size];
        int index = 0;
        for (String parameter : constructorParameterValues) {
            parameterValues[index] = parseParameterValue(parameter, classParameters[index], rectangleMapProperties);
            index++;
        }
        return parameterValues;
    }

    private Object parseParameterValue(String parameter, Class<?> classParam, MapProperties rectangleMapProperties) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (parameter.toLowerCase().startsWith("field"))
            return parseField(parameter, classParam);
        if (parameter.toLowerCase().startsWith("classfield"))
            return parseClassField(parameter, classParam);
        if (parameter.toLowerCase().startsWith("method"))
            return parseMethod(parameter, classParam);
        if (parameter.toLowerCase().startsWith("property"))
            return parseProperty(parameter, classParam, rectangleMapProperties);
        return null;
    }

    private Object parseField(String parameter, Class<?> classParam) throws NoSuchFieldException, IllegalAccessException {
        String[] parts = parameter.split("\\.");
        String fieldPart = parts.length == 2 ? parts[1] : null;
        Field field = this.getClass().getDeclaredField(fieldPart);
        return field.get(this);
    }

    private Object parseClassField(String parameter, Class<?> classParam) {
        String[] parts = parameter.split("\\.");
        return null;
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
        if (isInt(classParam)) {
            int intParameter = Math.round(parts.length == 2 ? (Float) (Object) rectangleMapProperties.get(parts[1].toLowerCase(), classParam) : null);
            return intParameter;
        }
        else
            return parts.length == 2 ? (Object) rectangleMapProperties.get(parts[1].toLowerCase(), classParam) : null;
    }

    private boolean isInt(Class clazz) {
        return clazz.equals(int.class);
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
