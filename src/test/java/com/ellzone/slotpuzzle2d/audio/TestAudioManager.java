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

package com.ellzone.slotpuzzle2d.audio;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.audio.Sound;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.ellzone.slotpuzzle2d.messaging.MessageType.PlayAudio;
import static com.ellzone.slotpuzzle2d.messaging.MessageType.StopAudio;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AnnotationAssetManager.class)

public class TestAudioManager {

    private AnnotationAssetManager annotationAssetManagerMock;
    private AudioManager audioManager;
    private Sound soundMock;
    private MessageManager messageManager;

    @BeforeEach
    public void setUp() {
        createMocks();
    }

    @AfterEach
    public void tearDown() {
        annotationAssetManagerMock = null;
        soundMock = null;
    }

    private void createMocks() {
        annotationAssetManagerMock = createMock(AnnotationAssetManager.class);
        soundMock = createMock(Sound.class);
    }

    private MessageManager setUpMessages() {
        messageManager = MessageManager.getInstance();
        messageManager.addListeners(audioManager,
                PlayAudio.index,
                StopAudio.index);
        return messageManager;
    }

    @Test
    public void testPlayedOneSound() {
        setUpSoundExpectations();
        replayAll();
        audioManager = new AudioManager(annotationAssetManagerMock);
        messageManager = setUpMessages();
        messageManager.dispatchMessage(PlayAudio.index, AssetsAnnotation.SOUND_REEL_SPINNING);
        assertThat(audioManager.getNumberSoundsPlayingSinceTimeInMilliSeconds(System.currentTimeMillis() - 10000L), is(equalTo(1)));
        verifyAll();
    }

    private void setUpSoundExpectations() {
        expect(annotationAssetManagerMock.get(AssetsAnnotation.SOUND_REEL_SPINNING)).andReturn(soundMock);
        expect(annotationAssetManagerMock.get(AssetsAnnotation.SOUND_REEL_SPINNING)).andReturn(soundMock);
        expect(annotationAssetManagerMock.get(AssetsAnnotation.SOUND_REEL_STOPPED)).andReturn(soundMock);
        expect(annotationAssetManagerMock.get(AssetsAnnotation.SOUND_REEL_STOPPED)).andReturn(soundMock);
        expect(annotationAssetManagerMock.get(AssetsAnnotation.SOUND_PULL_LEVER)).andReturn(soundMock);
        expect(annotationAssetManagerMock.get(AssetsAnnotation.SOUND_PULL_LEVER)).andReturn(soundMock);
        expect(annotationAssetManagerMock.get(AssetsAnnotation.SOUND_CHA_CHING)).andReturn(soundMock);
        expect(annotationAssetManagerMock.get(AssetsAnnotation.SOUND_CHA_CHING)).andReturn(soundMock);
        expect(soundMock.play()).andReturn(0L);
    }

    private void replayAll() {
        replay(annotationAssetManagerMock,
               soundMock);
    }

    private void verifyAll() {
        verify(annotationAssetManagerMock,
               soundMock);
    }
}
