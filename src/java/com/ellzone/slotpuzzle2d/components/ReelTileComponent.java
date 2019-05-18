package com.ellzone.slotpuzzle2d.components;

import com.badlogic.ashley.core.Component;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

public class ReelTileComponent implements Component {
    public ReelTile reelTile;
    public ReelTileComponent(ReelTile reelTile) {
        this.reelTile = reelTile;
    }
}
