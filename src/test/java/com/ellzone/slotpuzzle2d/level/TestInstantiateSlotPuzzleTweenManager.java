package com.ellzone.slotpuzzle2d.level;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestInstantiateSlotPuzzleTweenManager {
    @Test
    public void testInstantiateSlotPuzzleTweenManagerViaReflection() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.ellzone.slotpuzzle2d.tweenengine.TweenManager");
        assertThat("TweenManager", is(equalTo(clazz.getSimpleName())));
    }
}
