package com.ellzone.slotpuzzle2d.prototypes.events;

import com.ellzone.slotpuzzle2d.level.Suit;

public enum  MessageType {
    AddedCompoenent("AddComponent", 0),
    RemovedComponent("RemoveComponent", 1),
    AddedEntity("AddedEntity", 2),
    RemovedEntity("RemovedEntity",3);

    public final String name;
    public final int index;
    private MessageType(String name, int index) {
        this.name = name;
        this.index = index;
    }
    public static int getNumberOfMessages() {
        return Suit.values().length;
    }

    }
