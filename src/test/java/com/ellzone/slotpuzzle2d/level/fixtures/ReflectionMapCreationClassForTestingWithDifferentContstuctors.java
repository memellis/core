package com.ellzone.slotpuzzle2d.level.fixtures;

public class ReflectionMapCreationClassForTestingWithDifferentContstuctors extends ReflectionMapCreationClassForTesting {
    private float testFloatField;
    private int testIntField;
    private boolean testFieldBoolean;
    private String testFieldString;
    private Object testFieldObject;

    public ReflectionMapCreationClassForTestingWithDifferentContstuctors(float testFloatArgument) {
        this.testFloatField = testFloatArgument;
    }

    public ReflectionMapCreationClassForTestingWithDifferentContstuctors(int testIntField) {
        this.testIntField = testIntField;
    }

    public ReflectionMapCreationClassForTestingWithDifferentContstuctors(boolean testFieldBoolean) {
        this.testFieldBoolean = testFieldBoolean;
    }

    public ReflectionMapCreationClassForTestingWithDifferentContstuctors(String testFieldString) {
        this.testFieldString = testFieldString;
    }

    public ReflectionMapCreationClassForTestingWithDifferentContstuctors(Object testFieldObject) {
        this.testFieldObject = testFieldObject;
    }

    public float getTestFloatField() {
        return testFloatField;
    }

    public int getTestIntField() { return testIntField; }

    public boolean getTestBooleanField() { return testFieldBoolean; }

    public String getTestFieldString() {
        return testFieldString;
    }

    public Object getTestFieldObject() { return testFieldObject; }
}
