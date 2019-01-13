package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.sprites.HoldLightButton;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import box2dLight.RayHandler;

public class LevelObjectCreator {
    public Array<HoldLightButton> lightButtons = new Array<>();
    private World world;
    private RayHandler rayHandler;

    public LevelObjectCreator(World world, RayHandler rayHandler) {
        this.world = world;
        this.rayHandler = rayHandler;
    };

    public void createLevel(Array<RectangleMapObject> levelRectangleMapObjects) throws IllegalAccessException {
        HoldLightButton holdLightButton = null;
        for (RectangleMapObject rectangleMapObject : levelRectangleMapObjects) {
            MapProperties rectangleMapObjectProperties = rectangleMapObject.getProperties();
            String className = (String) rectangleMapObjectProperties.get("Class");
            System.out.println(String.format("creating className: %s", className));
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException cnfe) {
                System.out.println(String.format("Class not found %s.", clazz));
            }
            Array<String> constructorParameters = getClassConstructorParameters(rectangleMapObjectProperties, "Parameter");
            Class<?>[] classParameters = getParamTypes(constructorParameters);

            Constructor<?> constructor = null;
            try {
                constructor = clazz.getConstructor(classParameters);
            } catch (NoSuchMethodException nsme) {
                System.out.println(String.format("No such constructor exception %s",
                        nsme.getMessage()));

            }
            Array<String> constructorParameterValues = getClassConstructorParameters(rectangleMapObjectProperties, "ParameterValue");
            Object[] constructorParanetersValues = null;
            holdLightButton = null;
            try {
                constructorParanetersValues =  parseConstructorParameterValues(constructorParameterValues, classParameters, rectangleMapObjectProperties);
                holdLightButton =  (HoldLightButton) constructor.newInstance(constructorParanetersValues);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            if (holdLightButton != null) {
                Field field = null;
                try {
                    field = this.getClass().getDeclaredField("lightButtons");
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                field.setAccessible(true);
                holdLightButton.addTo(field.get(this));
            }
        }
    }

    private Object[] parseConstructorParameterValues(Array<String> constructorParameterValues, Class<?>[] classParameters, MapProperties rectangleMapProperties) throws NoSuchFieldException, IllegalAccessException {
        Object[] parameterValues = new Object[constructorParameterValues.size];
        int index = 0;
        for (String parameter : constructorParameterValues) {
            parameterValues[index] = parseParameterValue(parameter, classParameters[index], rectangleMapProperties);
            index++;
        }
        return parameterValues;
    }

    private Object parseParameterValue(String parameter, Class<?> classParam, MapProperties rectangleMapProperties) throws NoSuchFieldException, IllegalAccessException {
        if (parameter.toLowerCase().startsWith("field"))
            return parseField(parameter, classParam);
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
