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

package com.ellzone.slotpuzzle2d.level.creator;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.ellzone.slotpuzzle2d.level.Level;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelSprites;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.convert.TileMapToWorldConvert;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class LevelCreatorInjectorExtended implements LevelCreatorInjectionInterface {
    private final TileMapToWorldConvert tileMapToWorldConvert;
    private LevelCreatorInjector levelCreatorInjector;

    public LevelCreatorInjectorExtended(TileMapToWorldConvert tileMapToWorldConvert) {
        this.tileMapToWorldConvert = tileMapToWorldConvert;
        this.levelCreatorInjector = new LevelCreatorInjector();
    }

    @Override
    public TileMapToWorldConvert getTileMapToWorldConvert() {
        return tileMapToWorldConvert;
    }

    @Override
    public AnnotationAssetManager getAnnotationAssetManager() {
        return levelCreatorInjector.getAnnotationAssetManager();
    }

    @Override
    public ReelSprites getReelSprites() {
        return levelCreatorInjector.getReelSprites();
    }

    @Override
    public Texture getSlotReelScrollTexture() {
        return levelCreatorInjector.getSlotReelScrollTexture();
    }

    @Override
    public TweenManager getTweenManager() {
        return levelCreatorInjector.getTweenManager();
    }

    @Override
    public TextureAtlas getSlotHandleAtlas() {
        return levelCreatorInjector.getSlotHandleAtlas();
    }
}
