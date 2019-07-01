package com.ellzone.slotpuzzle2d.messaging;

public enum MessageType {
    AddedCompoenent("AddComponent", 0),
    RemovedComponent("RemoveComponent", 1),
    AddedEntity("AddedEntity", 2),
    RemovedEntity("RemovedEntity",3),
    PlayMusic("PlayMusic", 4),
    StopMusic("StopMusic", 5),
    PauseMusic("PauseMusic", 6),
    GetCurrentMusicTrack("GetCurrentMusicTrack", 7);

    public final String name;
    public final int index;

    private MessageType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static int getNumberOfTypes() {
        return MessageType.values().length;
    }
}
