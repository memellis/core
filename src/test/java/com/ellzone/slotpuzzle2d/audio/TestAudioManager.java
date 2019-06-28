package com.ellzone.slotpuzzle2d.audio;

import com.badlogic.gdx.ai.msg.MessageManager;

import com.badlogic.gdx.audio.Music;
import com.ellzone.slotpuzzle2d.messaging.MessageType;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.expect;
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
    private MessageManager messageManager;
    private Music musicMock;

    @Before
    public void setUp() {
        createMocks();
        setUpExpectations();
        replayAll();
        audioManager = new AudioManager(annotationAssetManagerMock);
        messageManager = setUpMessages();
    }

    private void setUpExpectations() {
        expect(annotationAssetManagerMock.get(AssetsAnnotation.MUSIC_INTRO_SCREEN)).andReturn(musicMock);
        expect(annotationAssetManagerMock.get(AssetsAnnotation.MUSIC_INTRO_SCREEN)).andReturn(musicMock);
        musicMock.play();
    }

    private void createMocks() {
        musicMock = createMock(Music.class);
        annotationAssetManagerMock = createMock(AnnotationAssetManager.class);
    }

    private MessageManager setUpMessages() {
        messageManager = MessageManager.getInstance();
        messageManager.addListeners(audioManager,
                MessageType.PlayMusic.index,
                MessageType.StopPlayMusic.index);
        return messageManager;
    }

    @After
    public void tearDown() {
        annotationAssetManagerMock = null;
        audioManager = null;
    }

    @Test
    public void testPlayTrack() {
        messageManager.dispatchMessage(MessageType.PlayMusic.index, AssetsAnnotation.MUSIC_INTRO_SCREEN);
        assertThat(audioManager.getCurrentlyPlaying(), is(equalTo(AssetsAnnotation.MUSIC_INTRO_SCREEN)));
    }

    private void replayAll() {
        replay(annotationAssetManagerMock,
               musicMock);
    }
}
