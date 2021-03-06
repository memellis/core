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

package com.ellzone.slotpuzzle2d.sprites.reel;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.Random;

public class ReelTiles {
	private Array<com.ellzone.slotpuzzle2d.sprites.reel.ReelTile> reelTiles;
	private ReelSprites reelSprites;
	private Pixmap slotReelScrollPixmap;
	private Texture slotReelScrollTexture;
	private com.ellzone.slotpuzzle2d.sprites.reel.ReelTile reelTile;
	
	public ReelTiles(com.ellzone.slotpuzzle2d.sprites.reel.ReelSprites reelSprites) {
		this.reelSprites = reelSprites;
		initialiseReelTiles();
	}
	
	private void initialiseReelTiles() {
	    reelTiles = new Array<com.ellzone.slotpuzzle2d.sprites.reel.ReelTile>();
	    slotReelScrollPixmap = new Pixmap(reelSprites.getReelHeight(), reelSprites.getReelHeight(), Pixmap.Format.RGBA8888);
		slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(reelSprites.getSprites());
		slotReelScrollTexture = new Texture(slotReelScrollPixmap);
	    reelTile = new com.ellzone.slotpuzzle2d.sprites.reel.ReelTile(slotReelScrollTexture, slotReelScrollTexture.getHeight() / reelSprites.getReelHeight(), 0, 32, reelSprites.getReelWidth(), reelSprites.getReelHeight(), reelSprites.getReelWidth(), reelSprites.getReelHeight(), 0);
	    reelTile.setX(0);
	    reelTile.setY(0);
	    reelTile.setSx(0);
	    reelTile.setEndReel(Random.getInstance().nextInt(reelSprites.getSprites().length - 1));
	    //reelTile.setSy(slotReelScrollTexture.getHeight() + 128 + reelTile.getEndReel() * reelSprites.getReelHeight() );
		reelTile.setSy(slotReelScrollTexture.getHeight() + reelTile.getEndReel() * reelSprites.getReelHeight() );
	    reelTiles.add(reelTile);
	}
	
	public Array<ReelTile> getReelTiles() {
		return reelTiles;
	}

	public Texture getSlotReelScrollTexture() {
		return this.slotReelScrollTexture;
	}

	public int getReelTileTextureHeight() {
		return slotReelScrollTexture.getHeight();
	}
}
