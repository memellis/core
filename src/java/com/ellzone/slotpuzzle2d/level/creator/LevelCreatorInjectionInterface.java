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
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public interface LevelCreatorInjectionInterface {
    public AnnotationAssetManager getAnnotationAssetManager();
    public ReelSprites getReelSprites();
    public Texture getSlotReelScrollTexture();
    public TweenManager getTweenManager();
    public TextureAtlas getSlotHandleAtlas();
}
