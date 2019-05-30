package com.ellzone.slotpuzzle2d.components;

import com.badlogic.ashley.core.Component;
import com.ellzone.slotpuzzle2d.sprites.SlotHandleSprite;

public class SlothandleSpriteComponent implements Component {
    public SlotHandleSprite slotHandleSprite;
    public SlothandleSpriteComponent(SlotHandleSprite slotHandleSprite) {
        this.slotHandleSprite = slotHandleSprite;
    }
}
