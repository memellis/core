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

package com.ellzone.slotpuzzle2d.puzzlegrid;

import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;

public class Point {
    private int r, c, width;

    public Point() {}

    public Point(int r, int c, int width) {
        this.r = r;
        this.c = c;
        this.width = width;
    }

    public void setR(int r) { this.r = r;}
    public void setC(int c) { this.c = c;}
    public void setWidth(int width) { this.width = width;}
    public int getR() { return r;}
    public int getC() { return c;}
    public int getWidth() {return width;}

    public boolean equals(Object other) {
        return (other instanceof Point) &&
               (r == ((Point) other).getR() & c == ((Point) other).getC());
    }

    public int hashCode() {
        return r * width + c;
    }
}
