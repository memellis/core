package com.ellzone.slotpuzzle2d.sprites.reel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;

interface ReelTileInterface {
    void update(float delta);

    float getSnapX();

    float getSnapY();

    float getDestinationX();

    float getDestinationY();

    void setDestinationX(float destinationX);

    void setDestinationY(float destinationY);

    float getSx();

    float getSy();

    void setSx(float sx);

    void setSy(float sy);

    float getTileWidth();

    float getTileHeight();

    void setEndReel();

    int getCurrentReel();

    boolean isReelTileDeleted();

    void deleteReelTile();

    void unDeleteReelTile();

    void setIsFallen(boolean isFallen);

    boolean isFallen();

    boolean isStoppedFalling();

    void setIsStoppedFalling(boolean isStoppedFalling);

    void startSpinning();

    void stopSpinning();

    void stopSpinningSound();

    ReelTile.FlashState getFlashState();

    boolean isFlashing();

    void setFlashMode(boolean reelFlash);

    Color getFlashColor();

    void setFlashColor(Color flashColor);

    void setFlashTween(boolean reelFlashTween);

    boolean getFlashTween();

    void setFlashOn();

    void setFlashOff();

    void setScore(int score);

    int getScore();

    TextureRegion getRegion();

    void setIndex(int index);

    int getIndex();

    void saveRegion();

    void saveFlashRegion();

    void resetReel();

    int getScrollTextureHeight();

    int getNumberOfReelsInTexture();

    void drawFlashSegments(ShapeRenderer shapeRenderer);

    void addReelFlashSegment(float x, float y);

    Array<Vector2> getReelFlashSegments();

    void clearReelFlashSegments();

    void updateReelFlashSegments(float x, float y);
}
