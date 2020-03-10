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

package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.Random;

public class ReelTile extends ReelSprite {
    private Texture scrollTexture;
    private int numberOfReelsInTexture = 0;
    private TextureRegion region, flashReel;
    private float tileWidth;
    private float tileHeight;
    private float reelDisplayWidth = 0, reelDisplayHeight = 0;
    private float x;
    private float y;
    private float destinationX;
    private float destinationY;
    private float sx = 0;
    private float sy = 0;
    private int index = -1;
	private boolean tileDeleted;
	private boolean reelFlash;
	private boolean reelFlashTween;
	private boolean isFallen;
    private boolean isStoppedFalling;

    public enum FlashState {FLASH_OFF, FLASH_ON};
	private FlashState reelFlashState;
	private Color flashColor;
	private Array<Vector2> reelFlashSegments = new Array<>();
	private int score;
	private Sound spinningSound;
	private long spinningSoundId;
	private float spinngPitch;
	private Pixmap flashOnReelPixmap;
	
    public ReelTile(Texture scrollTexture, float x, float y, float tileWidth, float tileHeight, int endReel, Sound spinningSound) {
        this.scrollTexture = scrollTexture;
        this.x = x;
        this.y = y;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        super.setEndReel(endReel);
        this.spinningSound = spinningSound;
        defineReelSlotTileScroll();
    }

    public ReelTile(Texture texture, int numberOfReelsInTexture, float x, float y, float tileWidth, float tileHeight, float reelDisplayWidth, float reelDisplayHeight, int endReel, Sound spinningSound) {
        this.scrollTexture = texture;
        this.numberOfReelsInTexture = numberOfReelsInTexture;
        this.x = x;
        this.y = y;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.reelDisplayWidth = reelDisplayWidth;
        this.reelDisplayHeight = reelDisplayHeight;
        super.setEndReel(endReel);
        this.spinningSound = spinningSound;
        defineReelSlotTileScroll();
    }

    private void defineReelSlotTileScroll() {
    	setPosition((int) x, (int) y);
        if (scrollTexture != null) {
            scrollTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            region = new TextureRegion(scrollTexture);
            int randomSy = 0;
            if (numberOfReelsInTexture > 0) {
                randomSy = Random.getInstance().nextInt(numberOfReelsInTexture) * (int) tileHeight;
                sy = randomSy;
            }
            if ((reelDisplayWidth == 0) && (reelDisplayHeight == 0)) {
                region.setRegion((int) 0, randomSy, (int) tileWidth, (int) tileHeight);
                setBounds((int) x, (int) y, (int) tileWidth, (int) tileHeight);
            } else {
                region.setRegion((int) 0, randomSy, (int) reelDisplayWidth, (int) reelDisplayHeight);
                setBounds((int) x, (int) y, (int) reelDisplayWidth, (int) reelDisplayHeight);
            }
            setRegion(region);
        }
        reelFlash = false;
        reelFlashTween = false;
        reelFlashState = FlashState.FLASH_OFF;
        flashColor = Color.RED;
        tileDeleted = false;
        isFallen = true;
        isStoppedFalling = true;
    }

	@Override
    public void update(float delta) {
        if (super.isSpinning())
            processSpinningState();
        if (reelFlashTween) {
            processFlashTweenState(delta);
        }
    }
	
	private void processSpinningState() {
        float syModulus = sy % scrollTexture.getHeight();
        region.setRegion((int) sx, (int) syModulus, (int)reelDisplayWidth, (int)reelDisplayHeight);
        setRegion(region);
        if (this.spinningSound != null) {
//        	this.spinngPitch = this.spinngPitch * 0.999f;
//        	this.spinningSound.setPitch(this.spinningSoundId, this.spinngPitch);
        }
 	}
		
	private void processFlashTweenState(float delta) {
         setFlashOn();
	}

	public float getDestinationX() {
        return this.destinationX;
    }

    public float getDestinationY() {
        return this.destinationY;
    }

    public void setDestinationX(float destinationX) {
        this.destinationX = destinationX;
    }

    public void setDestinationY(float destinationY) {
        this.destinationY = destinationY;
    }

    public float getSx() {
        return this.sx;
    }

    public float getSy() {
        return this.sy;
    }

    public void setSx(float sx) {
        this.sx = sx;
    }

    public void setSy(float sy) {
        this.sy = sy;
    }

    public float getTileWidth() {
        return tileWidth;
    }

    public float getTileHeight() {
        return tileHeight;
    }

    public void setEndReel() {
        float syModulus = sy % scrollTexture.getHeight();
        super.setEndReel((int) ((int) ((syModulus + (tileHeight / 2)) % scrollTexture.getHeight()) / tileHeight));
    }

	public int getCurrentReel() {
        float syModulus = sy % scrollTexture.getHeight();
 		return (int) ((int) ((syModulus + (tileHeight / 2)) % scrollTexture.getHeight()) / tileHeight);
	}

	public boolean isReelTileDeleted() {
        return this.tileDeleted;
	}
	
	public void deleteReelTile() {
        this.tileDeleted = true;
	}

	public void unDeleteReelTile() { this.tileDeleted = false; }

	public void setIsFallen (boolean isFallen) {
        this.isFallen = isFallen;
    }

    public boolean isFallen() {
        return isFallen;
    }

    public boolean isStoppedFalling() { return isStoppedFalling; }

    public void setIsStoppedFalling(boolean isStoppedFalling) {
        this.isStoppedFalling = isStoppedFalling;
    }

	public void startSpinning() {
		super.setSpinning(true);
//		if (this.spinningSound != null)
//			startSpinningSound();
	}

	private void startSpinningSound() {
		this.spinngPitch = 1.0f;
		this.spinningSoundId = this.spinningSound.play(1.0f, this.spinngPitch, 1.0f);
		this.spinningSound.setLooping(this.spinningSoundId, true);
	}
	
	public void stopSpinning() {
		super.setSpinning(false);
//		if (this.spinningSound != null)
//			stopSpinningSound();
	}

	public void stopSpinningSound() {
//        if (this.spinningSound != null)
//            this.spinningSound.stop(this.spinningSoundId);
	}
	
	public FlashState getFlashState() {
        return reelFlashState;
	}
	
	public boolean isFlashing() {
        return reelFlash;
	}
	
	public void setFlashMode(boolean reelFlash) {
        this.reelFlash = reelFlash;
	}
	
	public Color getFlashColor() {
        return this.flashColor;
	}
	
	public void setFlashColor(Color flashColor) {
        this.flashColor = flashColor;
	}
	
	public void setFlashTween(boolean reelFlashTween) {
        this.reelFlashTween = reelFlashTween;
	}
	
	public boolean getFlashTween() {
        return this.reelFlashTween;
	}
	
	public void setFlashOn() {
        reelFlashState = FlashState.FLASH_ON;
	}

    public void setFlashOff() {
        reelFlashState = FlashState.FLASH_OFF;
	}

	public void setScore(int score) {
        this.score = score;
	}
	
	public int getScore() {
        return this.score;
	}
	
	public TextureRegion getRegion() {
        return this.region;
	}

	@Override
	public void dispose() {
		if (flashOnReelPixmap != null)
            flashOnReelPixmap.dispose();
	}

	public void setIndex(int index) { this.index = index; }

	public int getIndex() { return this.index; }

	public void saveRegion() {
        if (region != null)
            PixmapProcessors.saveTextureRegion(region);
    }

    public void saveFlashRegion() {
        if(flashReel != null)
            PixmapProcessors.saveTextureRegion(flashReel);
    }

    public void resetReel() {
        flashOnReelPixmap = null;
        float syModulus = sy % scrollTexture.getHeight();
        region.setRegion((int) sx, (int) syModulus, (int)reelDisplayWidth, (int)reelDisplayHeight);
        setRegion(region);
    }

    public int getScrollTextureHeight() {
        return scrollTexture.getHeight();
    }

    public int getNumberOfReelsInTexture() {
        return numberOfReelsInTexture;
    }

    public void drawFlashSegments(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(flashColor);
        for (Vector2 reelFlashSegment : reelFlashSegments)
            drawFlashSegmemnt(shapeRenderer, reelFlashSegment);
        shapeRenderer.end();
    }

    private void drawFlashSegmemnt(ShapeRenderer shapeRenderer, Vector2 reelFlashSegment) {
        shapeRenderer.rect(reelFlashSegment.x + 0, reelFlashSegment.y + 0, tileWidth - 0, tileHeight - 0);
        shapeRenderer.rect(reelFlashSegment.x + 1, reelFlashSegment.y + 1, tileWidth - 2, tileHeight - 2);
        shapeRenderer.rect(reelFlashSegment.x + 2, reelFlashSegment.y + 2, tileWidth - 4, tileHeight - 4);
    }

    public void addReelFlashSegment(float x, float y) {
        reelFlashSegments.add(new Vector2(x, y));
    }

    public Array<Vector2> getReelFlashSegments() {
        return reelFlashSegments;
    }

    public void clearReelFlashSegments() {
        reelFlashSegments.clear();
    }

    public void updateReelFlashSegments(float x, float y) {
        for (Vector2 reelFlashSegment : reelFlashSegments)
            reelFlashSegment.set(new Vector2(x, y));
    }
}