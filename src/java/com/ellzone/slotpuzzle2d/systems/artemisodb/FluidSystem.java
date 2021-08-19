/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.systems.artemisodb;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;

import static com.artemis.E.E;

public abstract class FluidSystem extends EntityProcessingSystem {

    public FluidSystem() {
        super(Aspect.all());
    }

    public FluidSystem(Aspect.Builder aspect) {
        super(aspect);
    }

    @Override
    protected void process(Entity e) {
        process(E(e));
    }

    protected abstract void process(E e);

    protected E entityWithTag(String tag) {
        final Entity entity = world.getSystem(TagManager.class).getEntity(tag);
        return entity != null ? E(entity) : null;
    }
}