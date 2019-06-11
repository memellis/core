package com.ellzone.slotpuzzle2d.level.fixtures;

public class ReflectionMapCreationClassForTestingWithFloatArgument extends ReflectionMapCreationClassForTesting {
    private float testFloatField;

    public ReflectionMapCreationClassForTestingWithFloatArgument(float testFloatArgument) {
        this.testFloatField = testFloatArgument;
    }

    public float getTestFloatField() {
        return testFloatField;
    }
}
