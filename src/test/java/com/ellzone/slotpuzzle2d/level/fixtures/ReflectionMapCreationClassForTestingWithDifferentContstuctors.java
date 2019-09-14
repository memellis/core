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
