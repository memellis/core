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

package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.text.MessageFormat;

public class LightButton {
    private static final float PIXELS_PER_METER = 100;

    private Sprite lightButtonSprite;
    private World world;
    private RayHandler rayHandler;
    private PointLight light;
    private float positionX, positionY;
    private int buttonWidth, buttonHeight;
    private int buttonTextX, buttonTextY;
    private BitmapFont buttonFont;
    private String buttonText;
    private String buttonTextUsingFrameBuffer;
    private Color buttonColorBorder, buttonColorTop, buttonLightColor;

    public LightButton(World world, RayHandler rayHandler, float positionX, float positionY, int buttonWidth, int buttonHeight) {
        this.world = world;
        this.rayHandler = rayHandler;
        this.positionX = positionX;
        this.positionY = positionY;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        initialiseButtonTextPosition();
        initialiseLightButton();
    }

    private void initialiseButtonTextPosition() {
        buttonTextX = 5;
        buttonTextY = buttonHeight / 2 + 7;
    }

    public LightButton(World world, RayHandler rayHandler, float positionX, float positionY, int buttonWidth, int buttonHeight, boolean pixelsPerMeter) {
        this.world = world;
        this.rayHandler = rayHandler;
        this.positionX = positionX;
        this.positionY = positionY;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        initialiseButtonTextPosition();
        initialiseLightButton(pixelsPerMeter);
    }

    public LightButton(World world, RayHandler rayHandler, float positionX, float positionY, int buttonWidth, int buttonHeight, BitmapFont buttonFont, String buttonText) {
        this.world = world;
        this.rayHandler = rayHandler;
        this.positionX = positionX;
        this.positionY = positionY;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        this.buttonFont = buttonFont;
        this.buttonText = buttonText;
        initialiseButtonTextPosition();
        initialiseLightButton();
    }

    public LightButton(World world, RayHandler rayHandler, float positionX, float positionY, int buttonWidth, int buttonHeight, BitmapFont buttonFont, String buttonText, String buttonTextUsingFrameBuffer) {
        this.world = world;
        this.rayHandler = rayHandler;
        this.positionX = positionX;
        this.positionY = positionY;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        this.buttonFont = buttonFont;
        this.buttonText = buttonText;
        this.buttonTextUsingFrameBuffer = buttonTextUsingFrameBuffer;
        initialiseButtonTextPosition();
        initialiseLightButton();
    }

    public LightButton(World world, RayHandler rayHandler, float positionX, float positionY,
                       int buttonWidth, int buttonHeight, BitmapFont buttonFont, String buttonText,
                       String buttonTextUsingFrameBuffer, boolean pixelsPerMeter) {
        this.world = world;
        this.rayHandler = rayHandler;
        this.positionX = positionX;
        this.positionY = positionY;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        this.buttonFont = buttonFont;
        this.buttonText = buttonText;
        this.buttonTextUsingFrameBuffer = buttonTextUsingFrameBuffer;
        initialiseButtonTextPosition();
        initialiseLightButton(pixelsPerMeter);
    }

    public LightButton(World world, RayHandler rayHandler, float positionX, float positionY, int buttonWidth, int buttonHeight, BitmapFont buttonFont, String buttonText, String buttonTextUsingFrameBuffer, int buttonTextX, int buttonTextY) {
        this.world = world;
        this.rayHandler = rayHandler;
        this.positionX = positionX;
        this.positionY = positionY;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        this.buttonFont = buttonFont;
        this.buttonText = buttonText;
        this.buttonTextUsingFrameBuffer = buttonTextUsingFrameBuffer;
        this.buttonTextX = buttonTextX;
        this.buttonTextY = buttonTextY;
        initialiseButtonTextPosition();
        initialiseLightButton();
    }

    private void initialiseLightButton() {
        light = new PointLight(rayHandler, 32);
        light.setActive(false);
        light.setColor(Color.RED);
        light.setDistance(0.4f);
        float lightButtonCentreX = positionX + (float)buttonWidth / 200.0f;
        float lightButtonCentreY = positionY + (float)buttonHeight / 200.0f;
        light.setPosition(lightButtonCentreX, lightButtonCentreY);
        lightButtonSprite = new Sprite(createButton());
        lightButtonSprite.setPosition(positionX, positionY);
        lightButtonSprite.setSize(buttonWidth, buttonHeight);
    }

    private void initialiseLightButton(boolean pixelPerMeter) {
        if (pixelPerMeter)
            initialiseLightButton();
        else {
            light = new PointLight(rayHandler, 32);
            light.setActive(false);
            light.setColor(Color.RED);
            light.setDistance(0.4f);
            positionX = positionX / PIXELS_PER_METER;
            positionY = positionY / PIXELS_PER_METER;
            float lightButtonCentreX = positionX + (float) buttonWidth / (2 * PIXELS_PER_METER);
            float lightButtonCentreY = positionY + (float) buttonHeight / (2 * PIXELS_PER_METER);
            light.setPosition(lightButtonCentreX, lightButtonCentreY);
            lightButtonSprite = new Sprite(createButton());
            lightButtonSprite.setPosition(positionX, positionY);
            lightButtonSprite.setSize(buttonWidth / PIXELS_PER_METER, buttonHeight / PIXELS_PER_METER);
        }
    }

    private Texture createButton() {
        Pixmap button = new Pixmap(buttonWidth, buttonHeight, Pixmap.Format.RGBA8888);
        Color myOrange = new Color(Color.ORANGE);
        myOrange.a = 60;
        button.setColor(myOrange);
        button.fillRectangle(0, 0, buttonWidth, buttonHeight);
        Color myOrangeTranparent = myOrange;
        myOrangeTranparent.a = 120;
        button.setColor(myOrangeTranparent);
        button.fillRectangle(2, 2, buttonWidth - 4, buttonHeight - 4);
        button.setColor(Color.BROWN);
        button.drawRectangle(0, 0, buttonWidth, buttonHeight);
        button.setColor(Color.YELLOW);
        if (buttonTextUsingFrameBuffer != null) {
            button = createTextUsingFrameBuffer(buttonFont, buttonTextUsingFrameBuffer, button);
        } else {
            if (buttonText != null) {
                button = createText(buttonFont, buttonText, button);
            }
        }
        button.setColor(0, 200, 200, 255);
        button.fillRectangle(0, 0, buttonWidth, buttonHeight);
        return new Texture(button);
    }

    private Pixmap createText(BitmapFont font, String text, Pixmap pixmap) {
        return PixmapProcessors.createDynamicHorizontalFontTextColor(font, Color.YELLOW, text, pixmap, 3, 45);
    }

    private Pixmap createTextUsingFrameBuffer(BitmapFont font, String text, Pixmap pixmap) {
        return PixmapProcessors.createDynamicHorizontalFontTextViaFrameBuffer(font, Color.YELLOW, text, pixmap, buttonTextX, buttonTextY);
    }

    public PointLight getLight() {
        return this.light;
    }

    public Sprite getSprite() {
        return lightButtonSprite;
    }
}
