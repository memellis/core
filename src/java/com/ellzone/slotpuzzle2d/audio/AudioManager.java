package com.ellzone.slotpuzzle2d.audio;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.audio.Music;
import com.ellzone.slotpuzzle2d.messaging.MessageType;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import java.util.HashMap;

public class AudioManager implements Telegraph {
    private AnnotationAssetManager annotationAssetManager;
    private HashMap<String, Music> musicLibrary;
    private HashMap<Music, String> musicLibraryToName;
    private Music currentlyPlaying;

    public AudioManager(AnnotationAssetManager annotationAssetManager) {
        this.annotationAssetManager = annotationAssetManager;
        initialise();
    }

    private void initialise() {
        musicLibrary = new HashMap<>();
        musicLibrary.put(
                AssetsAnnotation.MUSIC_INTRO_SCREEN,
                (Music) annotationAssetManager.get(AssetsAnnotation.MUSIC_INTRO_SCREEN));
        musicLibraryToName = new HashMap<>();
        musicLibraryToName.put(
                (Music) annotationAssetManager.get(AssetsAnnotation.MUSIC_INTRO_SCREEN),
                AssetsAnnotation.MUSIC_INTRO_SCREEN);
    }

    @Override
    public boolean handleMessage(Telegram message) {
        if (message.message == MessageType.PlayMusic.index) {
            currentlyPlaying = musicLibrary.get(AssetsAnnotation.MUSIC_INTRO_SCREEN);
            currentlyPlaying.play();
            return true;
        }
        if (message.message == MessageType.StopPlayMusic.index) {
            if (currentlyPlaying != null)
                currentlyPlaying.stop();
            return true;
        }
        return false;
    }

    public String getCurrentlyPlaying() {
        return currentlyPlaying == null ? null : musicLibraryToName.get(currentlyPlaying);
    }
}
