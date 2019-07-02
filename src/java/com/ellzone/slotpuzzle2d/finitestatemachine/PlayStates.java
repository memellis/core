package com.ellzone.slotpuzzle2d.finitestatemachine;

public enum PlayStates {
    INITIALISING,
    INTRO_SEQUENCE,
    INTRO_POPUP,
    INTRO_SPINNING,
    HIT_SINK_BOTTOM,
    INTRO_FLASHING,
    CREATED_REELS_HAVE_FALLEN,
    PLAYING,
    LEVEL_TIMED_OUT,
    LEVEL_LOST,
    WON_LEVEL,
    RESTARTING_LEVEL,
    REELS_SPINNING,
    REELS_FLASHING
}
