package com.ellzone.slotpuzzle2d.level.creator;

import com.badlogic.ashley.core.Component;
import com.ellzone.slotpuzzle2d.sprites.ReelHelper;

public interface LevelReelHelperCallback {
    public void onEvent(ReelHelper source);
    public void addComponent(Component component);
}
