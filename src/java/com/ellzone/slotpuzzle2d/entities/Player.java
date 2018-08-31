package com.ellzone.slotpuzzle2d.entities;

import com.ellzone.slotpuzzle2d.component.NameComponent;
import com.ellzone.slotpuzzle2d.component.PositionComponent;

public class Player extends Entities {
    public Player() {
        super();
        PositionComponent position = new PositionComponent();
        position.x = 10.0f;
        position.y = 20.0f;
        addComponents(position);
        NameComponent name = new NameComponent();
        name.name = "name";
        addComponents(name);
    }
}
