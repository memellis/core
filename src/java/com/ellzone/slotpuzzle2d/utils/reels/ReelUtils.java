package com.ellzone.slotpuzzle2d.utils.reels;

import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;

public class ReelUtils {
    public static AnimatedReel createAnimatedReel(int x, int y) {
        return new AnimatedReel(
                null,
                x,
                y,
                40,
                40,
                40,
                40,
                0,
                null
        );
    }
}
