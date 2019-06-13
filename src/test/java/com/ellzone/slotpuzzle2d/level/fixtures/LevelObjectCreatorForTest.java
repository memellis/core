package com.ellzone.slotpuzzle2d.level.fixtures;

import com.badlogic.gdx.physics.box2d.World;
import com.ellzone.slotpuzzle2d.level.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.LevelObjectCreator;

import box2dLight.RayHandler;

public class LevelObjectCreatorForTest extends LevelObjectCreator {
    private ReflectionMapCreationClassForTesting reflectionMapCreationClassForTesting;
    private ReflectionMapCreationClassForTestingWithFloatArgument reflectionMapCreationClassForTestingWithFloatArgument;
    private boolean delegatedToCallbackCalled = false;
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

    public void addTo(ReflectionMapCreationClassForTestingWithFloatArgument reflectionMapCreationClassForTestingWithFloatArgument) {
        this.reflectionMapCreationClassForTestingWithFloatArgument = reflectionMapCreationClassForTestingWithFloatArgument;
    }

    public void delegateToCallback(ReflectionMapCreationClassForTestingWithFloatArgument reflectionMapCreationClassForTestingWithFloatArgument) {
        delegatedToCallbackCalled = true;
    }

    public ReflectionMapCreationClassForTesting getReflectionMapCreationClassForTesting() {
        return reflectionMapCreationClassForTesting;
    }

    public ReflectionMapCreationClassForTestingWithFloatArgument getReflectionMapCreationClassForTestingWithFloatArgument() {
        return reflectionMapCreationClassForTestingWithFloatArgument;
    }

    public boolean getDelegatedToCallback() {
        return delegatedToCallbackCalled;
    }
}