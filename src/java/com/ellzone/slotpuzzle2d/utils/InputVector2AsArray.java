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

package com.ellzone.slotpuzzle2d.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputVector2AsArray {

    private final String vetctor2ToInput;
    private Scanner scanner;

    public InputVector2AsArray(String vector2ToInput) {
        this.vetctor2ToInput = vector2ToInput;
        scanner = new Scanner(vector2ToInput);
    }

    public Array<Vector2> readVector2s() throws InputMismatchException {
        Array<Vector2> vector2sInput = new Array<>();
        while (scanner.hasNext())
            vector2sInput.add(new Vector2(scanner.nextInt(), scanner.nextInt()));
        return vector2sInput;
    }

    public void printVector2Array(Array<Vector2> vector2sInput) {
        for (Vector2 vector2 : new Array.ArrayIterator<>(vector2sInput))
            System.out.print(vector2);
        System.out.println();
    }
}
