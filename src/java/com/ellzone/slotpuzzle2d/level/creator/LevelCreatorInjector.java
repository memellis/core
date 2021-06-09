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

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelSprites;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsLoader;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class LevelCreatorInjector implements LevelCreatorInjectionInterface {
    private AnnotationAssetManager annotationAssetManager;
    private final TweenManager tweenManager = new TweenManager();
    private ReelSprites reelSprites;
    private Texture slotReelScrollPixmap;

    public LevelCreatorInjector() {
        initialise();
    }

    private void initialise() {
        annotationAssetManager = loadAssets();
        reelSprites = createSprites();
        slotReelScrollPixmap = createSlotReelScrollTexture();
    }

    private AnnotationAssetManager loadAssets() {
        AssetsLoader assetsLoader = new AssetsLoader();
        return assetsLoader.getAnnotationAssetManager();
    }

    private Texture createSlotReelScrollTexture() {
        Pixmap slotReelScrollPixmap =
                PixmapProcessors.createPixmapToAnimate(reelSprites.getSprites());
        return new Texture(slotReelScrollPixmap);
    }

    private ReelSprites createSprites() {
        return new ReelSprites(annotationAssetManager);
    }

    @Override
    public AnnotationAssetManager getAnnotationAssetManager() {
        return getAnnotationAssetManager();
    }

    @Override
    public ReelSprites getReelSprites() {
        return reelSprites;
    }

    @Override
    public Texture getSlotReelScrollTexture() {
        return slotReelScrollPixmap;
    }

    @Override
    public TweenManager getTweenManager() {
        return tweenManager;
    }

    @Override
    public TextureAtlas getSlotHandleAtlas() {
        return annotationAssetManager.get(AssetsAnnotation.SLOT_HANDLE);
    }
}
