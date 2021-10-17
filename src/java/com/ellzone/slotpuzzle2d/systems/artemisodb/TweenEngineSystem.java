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

package com.ellzone.slotpuzzle2d.systems.artemisodb;

import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.score.Score;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;

public class TweenEngineSystem extends FluidSystem {
    private TweenManager tweenManager;

    public TweenEngineSystem(TweenManager tweenManager) {
        this.tweenManager = tweenManager;
        initialiseTweenEngine();
    }

    private void initialiseTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Score.class, new ScoreAccessor());
    }

    @Override
    protected void process(E e) {

    }

    @Override
    protected void begin() {
        super.begin();
        tweenManager.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    protected void end() {
        super.end();
    }
}
