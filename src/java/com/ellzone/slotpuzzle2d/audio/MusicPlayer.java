package com.ellzone.slotpuzzle2d.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.messaging.MessageType;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;

public class MusicPlayer implements Telegraph {
    public static final String MM_SS_TIME_FORMAT = "%02d:%02d";
    public static final int MINUTES_PER_HOUR = 60;
    public static final int SECONDS_PER_MINITE = 60;
    public static final String PLAYBACK_PNG = "playback.png";
    public static final String UI_UISKIN = "ui/uiskin.json";
    private SpriteBatch batch;
    private Stage stage;
    private Viewport viewport;
    private float x, y;
    private MessageManager messageManager = MessageManager.getInstance();
    private TextureRegion playBackButtons;
    private Rectangle playButton;
    private Rectangle stopButton;
    private Rectangle pauseButton;
    private Slider slider;
    private BitmapFont font = new BitmapFont();
    private boolean sliderUpdating;
    private Music currentTrack;
    private float songDuration = 183;
    private boolean visible;
    private float slidertimerCount = 0.0f;
    private float currentPosition;
    private boolean updateSlider;

    public MusicPlayer(SpriteBatch batch, Stage stage, Viewport viewport, float x, float y) {
        this.batch = batch;
        this.stage = stage;
        this.viewport = viewport;
        this.x = x;
        this.y = y;
        initialise(x, y);
    }

    private void initialise(float x, float y) {
        initialisePlaybackButtons(x, y);
        visible = true;
        updateSlider = false;
    }

    private void initialisePlaybackButtons(float x, float y) {
        playBackButtons = new TextureRegion(new Texture(Gdx.files.internal(PLAYBACK_PNG)));
        playButton = new Rectangle(x + 13,y + 60, 40,42);
        stopButton = new Rectangle(x + 79, y + 60, 40, 42);
        pauseButton = new Rectangle(x + 147, y + 60, 40, 42);

        Skin skin = new Skin(Gdx.files.internal(UI_UISKIN));
        slider = new Slider(0, 100, 0.1f, false, skin);
        slider.setPosition(x + 30, y + 20);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
            if (currentTrack != null && !sliderUpdating && slider.isDragging())
                currentTrack.setPosition((slider.getValue() / 100f) * songDuration);
            }
        });
        slider.setVisible(visible);
        stage.addActor(slider);
    }

    public void update(float delta) {
        handleInput();
        slidertimerCount += delta;
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 unprojectedTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(unprojectedTouch);
            if (playButton.contains(unprojectedTouch.x, unprojectedTouch.y))
                messageManager.dispatchMessage(MessageType.PlayMusic.index, AssetsAnnotation.MUSIC_INTRO_SCREEN);
            if (stopButton.contains(unprojectedTouch.x, unprojectedTouch.y))
                messageManager.dispatchMessage(MessageType.StopMusic.index, AssetsAnnotation.MUSIC_INTRO_SCREEN);
            if (pauseButton.contains(unprojectedTouch.x, unprojectedTouch.y))
                messageManager.dispatchMessage(MessageType.PauseMusic.index, AssetsAnnotation.MUSIC_INTRO_SCREEN);
        }
    }

    public void draw() {
        if (visible) {
            if (slidertimerCount > 0.25f) {
                slidertimerCount = 0.0f;
                if (currentTrack != null) {
                    currentPosition = currentTrack.getPosition();
                    if (updateSlider) {
                        sliderUpdating = true;
                        slider.setValue((currentTrack.getPosition() / songDuration) * 100f);
                        sliderUpdating = false;
                    }
                }
            }
            batch.draw(playBackButtons, x, y + 60);
            font.draw(batch, String.format(MM_SS_TIME_FORMAT,(int)currentPosition / MINUTES_PER_HOUR, (int)currentPosition % SECONDS_PER_MINITE), x + 75, y + 60);
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        slider.setVisible(visible);
    }

    public boolean isVisible() {
        return visible;
    }

    private void setUpdateSlider(boolean updateSlider) {
        this.updateSlider = updateSlider;
    }

    @Override
    public boolean handleMessage(Telegram message) {
        if (message.message == MessageType.GetCurrentMusicTrack.index)
            currentTrack = (Music) message.extraInfo;
        return false;
    }
}

