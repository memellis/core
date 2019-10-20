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

package com.ellzone.slotpuzzle2d.prototypes.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.ellzone.slotpuzzle2d.audio.AudioManager;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypesGame;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import static com.ellzone.slotpuzzle2d.messaging.MessageType.PauseAudio;
import static com.ellzone.slotpuzzle2d.messaging.MessageType.PlayAudio;
import static com.ellzone.slotpuzzle2d.messaging.MessageType.StopAudio;

public class PlayAudio extends SPPrototype {
    private AudioManager audioManager;
    private AnnotationAssetManager annotationAssetManager;
    private MessageManager messageManager;
    private TextureRegion buttons;
    private BitmapFont font;
    private SpriteBatch batch;
    private Stage stage;
    private SelectBox<String> audioBox;
    private String currentAudioTrack;
    private boolean worldScreenPrototype = false;

    @Override
    public void create() {
        setUpAudio();
        messageManager = setUpMessageManager();
        setUpUi();
    }

    private void setUpUi() {
        buttons = new TextureRegion(new Texture(Gdx.files.internal("playback.png")));
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("arial-15.fnt"), false);

        stage = new Stage();
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        audioBox = new SelectBox<String>(skin);
        audioBox.setItems(audioManager.getAudioTracksNames());
        audioBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setAudioTrack(audioBox.getSelected());
            }
        });

        setAudioTrack(audioBox.getSelected());

        createButton("Number of tracks played since last 30 seconds",
                     1, new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        System.out.println("Was "+audioManager.getNumberSoundsPlayingSinceTimeInMilliSeconds(System.currentTimeMillis()-30000L));
                    }
                },
                skin);

        Table table = new Table(skin);
        table.add(audioBox);
        table.setFillParent(true);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    private void createButton(String buttonText, int buttonPostion, ChangeListener buttonChangeListener, Skin skin) {
        TextButton textButton = new TextButton(buttonText, skin);
        textButton.setPosition(SPPrototypesGame.V_WIDTH / 2 - SPPrototypesGame.V_WIDTH / 8,
                SPPrototypesGame.V_HEIGHT - buttonPostion * textButton.getHeight());
        stage.addActor(textButton);
        textButton.addListener(buttonChangeListener);
    }


    private void setAudioTrack(String selected) {
        currentAudioTrack = selected;
    }

    private void setUpAudio() {
        annotationAssetManager = new AnnotationAssetManager();
        annotationAssetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        annotationAssetManager.load(new AssetsAnnotation());
        annotationAssetManager.finishLoading();

        audioManager = new AudioManager(annotationAssetManager);
    }

    private MessageManager setUpMessageManager() {
        messageManager = MessageManager.getInstance();
        messageManager.addListeners(audioManager,
                PlayAudio.index,
                StopAudio.index,
                PauseAudio.index);
        return messageManager;
    }

    @Override
    public void resize(int width, int height) {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    public void resume() {
        System.out.println(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(buttons, 0, 0);
        batch.end();

        stage.act();
        stage.draw();

        if (Gdx.input.justTouched()) {
            if (Gdx.input.getY() > Gdx.graphics.getHeight() - 64) {
                if (Gdx.input.getX() < 64) {
                    System.out.println("play track");
                    playSelectedAudioTrack();
                }
                if (Gdx.input.getX() > 64 && Gdx.input.getX() < 128) {
                    System.out.println("stop track");
                    stopCurrentAudioTrack();
                }
                if (Gdx.input.getX() > 128 && Gdx.input.getX() < 192) {
                    System.out.println("pause track");
                    pauseCurrentAudioTrack();
                }
            }
        }
    }

    private void playSelectedAudioTrack() {
        messageManager.dispatchMessage(PlayAudio.index, currentAudioTrack);
    }

    private void stopCurrentAudioTrack() {
        messageManager.dispatchMessage(StopAudio.index, currentAudioTrack);
    }

    private void pauseCurrentAudioTrack() {
        messageManager.dispatchMessage(PauseAudio.index, currentAudioTrack);
    }
}