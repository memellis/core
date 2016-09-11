package com.ellzone.slotpuzzle2d.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;

public class Hud implements Disposable {

    public Stage stage;
    private Viewport viewport;

    private Integer worldTimer;
    private boolean timeUp; 
    private float timeCount;
    private static Integer score;
    private static Integer lives;
    private static Label scoreLabel, livesLeftLabel;
    private Label countdownLabel, livesLabel, timeLabel, levelLabel, worldLabel, slotPuzzle;

    public Hud(SpriteBatch sb){
        worldTimer = 300;
        timeCount = 0;
        score = 0;
        lives = 3;

        viewport = new FitViewport(SlotPuzzle.V_WIDTH, SlotPuzzle.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        livesLeftLabel = new Label(String.format("%03d", lives), new Label.LabelStyle(new BitmapFont(), Color.WHITE));        
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        slotPuzzle = new Label("Slot Puzzle", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        livesLabel = new Label("LIVES", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(slotPuzzle).expandX().padTop(10);
        table.add(livesLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(livesLeftLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        stage.addActor(table);
    }

    public void update(float dt) {
        timeCount += dt;
        if(timeCount >= 1){
            if (worldTimer > 0) {
                worldTimer--;
            } else {
                timeUp = true;
            }
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public static void addScore(int value) {
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }
    
    public static void resetScore() {
    	score = 0;
    }

    @Override
    public void dispose() { 
    	stage.dispose(); 
    }

    public boolean isTimeUp() { 
    	return timeUp; 
    }
    
    public Integer getWorldTime() {
    	return worldTimer;
    }
    
    public void resetWorldTime() {
    	worldTimer = 30;
    }
    
    public static void loseLife() {
    	if (lives > 0) {
    		lives--;
    	}
    	livesLeftLabel.setText(String.format("%03d", lives));
    }
    
    public static int getLives() {
    	return lives;
    }
}