package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.physics.box2d.World;

import box2dLight.RayHandler;

public class LevelObjectCreatorForTest extends LevelObjectCreator {
    private ReflectionMapCreationClassForTesting reflectionMapCreationClassForTesting;
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

    public ReflectionMapCreationClassForTesting getReflectionMapCreationClassForTesting() {
        return reflectionMapCreationClassForTesting;
    }

    public boolean getDelegatedToCallback() {
        return delegatedToCallbackCalled;
    }
}