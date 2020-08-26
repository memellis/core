/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes.speechbubble;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.popups.LevelPopUpTypeWriterText;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;

public class SpeechBubble extends SPPrototypeTemplate {
    public static final String LEVEL_DESC =  "Reveal the hidden pattern to complete the level.";
    public static final String CURRENT_LEVEL = "1-1";
    private Array<Sprite> popUpSprites;
    private TextureAtlas tilesAtlas;
    private LevelPopUpTypeWriterText levelPopUp;
    private BitmapFont currentLevelFont;
    private boolean drawSpeechBubbleText = false;

    @Override
    protected void initialiseOverride() {
        currentLevelFont = new BitmapFont();
        currentLevelFont.getData().scale(1.5f);
        levelPopUp = new LevelPopUpTypeWriterText(batch, tweenManager, popUpSprites, currentLevelFont, CURRENT_LEVEL, LEVEL_DESC);
        Gdx.input.setInputProcessor(inputProcessor);
        levelPopUp.showLevelPopUp(new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                drawSpeechBubbleText = true;
            }
        });
    }

    @Override
    protected void initialiseScreenOverride() {
    }

    @Override
    protected void loadAssetsOverride() {
        tilesAtlas = annotationAssetManager.get(AssetsAnnotation.TILES);

        popUpSprites = new Array<>();
        popUpSprites.add(tilesAtlas.createSprite(AssetsAnnotation.GAME_POPUP));
        popUpSprites.add(tilesAtlas.createSprite(AssetsAnnotation.LEVEL_SPRITE));
        setPopUpSpritePositions();
    }

    private void setPopUpSpritePositions() {
        popUpSprites.get(0).setPosition(
                displayWindowWidth / 2 - popUpSprites.get(0).getWidth() / 2,
                displayWindowHeight / 2 - popUpSprites.get(0).getHeight() /2);
        popUpSprites.get(1).setPosition(
                -200,
                displayWindowHeight / 2 - popUpSprites.get(1).getHeight() / 2);
    }

    @Override
    protected void disposeOverride() {

    }

    @Override
    protected void updateOverride(float dt) {

    }

    @Override
    protected void renderOverride(float dt) {
        levelPopUp.draw(batch);
        if (drawSpeechBubbleText)
            levelPopUp.drawSpeechBubble(batch, dt);
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
    }

    private final InputProcessor inputProcessor = new InputAdapter() {
        @Override
        public boolean touchUp(int x, int y, int pointer, int button) {
            tweenManager.killAll();
            levelPopUp.hideLevelPopUp(null);
            return true;
        }
    };
}
