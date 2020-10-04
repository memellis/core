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

package com.ellzone.slotpuzzle2d.sprites.slothandle;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.ellzone.slotpuzzle2d.sprites.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.WorldScreenConvert;

public class SlotHandleTileMap implements SlotHandle {
    private SlotHandleSprite slotHandle;
    private float worldPositionX;
    private float worldPositionY;

    public SlotHandleTileMap(TextureAtlas slotHandleAtlas,
                             TweenManager tweenManager,
                             float xPosition,
                             float yPosition) {
        slotHandle = new SlotHandleSprite(
                slotHandleAtlas,
                tweenManager,
                convertTileMapXToWorldPostionX(xPosition) / 100,
                convertTileMapYToWorldPostionY(yPosition) / 100);
        changeSpriteSize(slotHandle.getSlotHandleSprite(), 50);
        changeSpriteSize(slotHandle.getSlotHandleBaseSprite(), 50);
    }

    private Sprite changeSpriteSize(Sprite sprite, float divisor) {
        sprite.setBounds(
                sprite.getX(),
                sprite.getY(),
                sprite.getWidth() / divisor,
                sprite.getHeight() / divisor);
        return sprite;
    }

    private float convertTileMapXToWorldPostionX(float x) {
        worldPositionX = WorldScreenConvert.convertTileMapXToWorldPostionX(x);
        return worldPositionX;
    }

    private float convertTileMapYToWorldPostionY(float y) {
        worldPositionY = WorldScreenConvert.convertTileMapYToWorldPostionY(y);
        return worldPositionY;
    }

    public float getWorldPositionX() {
        return worldPositionX;
    }

    public float getWorldPositionY() {
        return worldPositionY;
    }

    @Override
    public Sprite getSlotHandleSprite() {
        return slotHandle.getSlotHandleSprite();
    }

    @Override
    public Sprite getSlotHandleBaseSprite() {
        return slotHandle.getSlotHandleBaseSprite();
    }

    @Override
    public void setSlotHandleSprite(Sprite sprite) {
        slotHandle.setSlotHandleSprite(sprite);
    }

    @Override
    public void setSlotHandleBaseSprite(Sprite sprite) {
        slotHandle.setSlotHandleBaseSprite(sprite);
    }

    @Override
    public void pullSlotHandle() {
        slotHandle.pullSlotHandle();
    }

    @Override
    public Rectangle getBoundingRectangle() {
        return slotHandle.getBoundingRectangle();
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        slotHandle.draw(spriteBatch);
    }
}
