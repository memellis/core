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

package com.ellzone.slotpuzzle2d.physics;

public class Point {
    public float x, y;
    public int intX, intY;
    
    public Point() {};
    
    public Point(float x, float y) {
    	this.x = x;
    	this.y = y;
    }

    public Point(int x, int y) {
        this.intX = x;
        this.intY = y;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getIntX() { return intX; }

    public int getIntY() { return intY; }

    public void setIntX(int x) { this.intX = x; }

    public void setIntY(int y) { this.intY = y; }

    public Point getLocation() {
        return new Point(this.x, this.y);
    }

    public void setLocation(Point point) {
        this.setX(point.getX());
        this.setY(point.getY());
    }

    public void setLocation(float x, float y) {
        this.setX(x);
        this.setY(y);
    }
}