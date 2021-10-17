package com.ellzone.slotpuzzle2d.systems.artemisodb;

import com.artemis.E;
import com.ellzone.slotpuzzle2d.component.artemis.Color;

import net.mostlyoriginal.api.system.core.PassiveSystem;

import static net.mostlyoriginal.api.operation.OperationFactory.*;

public class ScriptSystemExample extends PassiveSystem {
    public ScriptSystemExample() {
        E.E().
            script(
                    sequence(
                            delay(0.5f),
                            add(new Color())
                    ));
    }
}
