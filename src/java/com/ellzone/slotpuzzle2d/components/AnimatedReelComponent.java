package com.ellzone.slotpuzzle2d.components;

import com.badlogic.ashley.core.Component;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;

public class AnimatedReelComponent implements Component {
    public AnimatedReel animatedReel;
    public AnimatedReelComponent(AnimatedReel animatedReel) {
        this.animatedReel = animatedReel;
    }
}
