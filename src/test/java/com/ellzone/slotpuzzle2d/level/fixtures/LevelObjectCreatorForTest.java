package com.ellzone.slotpuzzle2d.level.fixtures;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.World;
import com.ellzone.slotpuzzle2d.level.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.LevelObjectCreator;

import box2dLight.RayHandler;

public class LevelObjectCreatorForTest extends LevelObjectCreator {
    private ReflectionMapCreationClassForTesting reflectionMapCreationClassForTesting;
    private ReflectionMapCreationClassForTestingWithDifferentContstuctors reflectionMapCreationClassForTestingWithDifferentContstuctors;
    private boolean delegatedToCallbackCalled = false;
    private boolean addToComponentEntityCalled = false;
    public float testPublicFloatField;


    public LevelObjectCreatorForTest(LevelCreatorInjectionInterface levelCreatorInjectionInterface,
                                     World world,
                                     RayHandler rayHandler) {
        super(levelCreatorInjectionInterface, world, rayHandler);
    }

    public void addTo(ReflectionMapCreationClassForTesting reflectionMapCreationClassForTesting) {
        this.reflectionMapCreationClassForTesting = reflectionMapCreationClassForTesting;
    }

    public void delegateToCallback(ReflectionMapCreationClassForTesting reflectionMapCreationClassForTesting) {
        delegatedToCallbackCalled = true;
    }

    public void addTo(ReflectionMapCreationClassForTestingWithDifferentContstuctors reflectionMapCreationClassForTestingWithDifferentContstuctors) {
        this.reflectionMapCreationClassForTestingWithDifferentContstuctors = reflectionMapCreationClassForTestingWithDifferentContstuctors;
    }

    public void delegateToCallback(ReflectionMapCreationClassForTestingWithDifferentContstuctors reflectionMapCreationClassForTestingWithDifferentContstuctors) {
        delegatedToCallbackCalled = true;
    }

    public void addComponentToEntity(ReflectionMapCreationClassForTestingWithDifferentContstuctors entity, Component component) {
        addToComponentEntityCalled = true;
    }

    public ReflectionMapCreationClassForTesting getReflectionMapCreationClassForTesting() {
        return reflectionMapCreationClassForTesting;
    }

    public ReflectionMapCreationClassForTestingWithDifferentContstuctors getReflectionMapCreationClassForTestingWithDifferentContstuctors() {
        return reflectionMapCreationClassForTestingWithDifferentContstuctors;
    }

    public boolean getDelegatedToCallback() {
        return delegatedToCallbackCalled;
    }

    public boolean getAddedToComponentEntity() { return addToComponentEntityCalled; }

    public float getTestPublicFloatField() { return testPublicFloatField; }
}