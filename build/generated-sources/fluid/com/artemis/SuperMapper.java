package com.artemis;

import com.artemis.utils.Bag;
import com.ellzone.slotpuzzle2d.component.ColorComponent;
import com.ellzone.slotpuzzle2d.component.Example;
import com.ellzone.slotpuzzle2d.component.PositionComponent;
import com.ellzone.slotpuzzle2d.component.Translation;

public final class SuperMapper extends BaseSystem {
  protected Bag<E> es = new Bag<>(128);

  public ComponentMapper<ColorComponent> mColorComponent;

  public ComponentMapper<Example> mExample;

  public ComponentMapper<PositionComponent> mPositionComponent;

  public ComponentMapper<Translation> mTranslation;

  protected void initialize() {
    E._processingMapper=this;
  }

  public void processSystem() {
    E._processingMapper=this;
  }

  E getE(int entityId) {
    E e = (E) es.safeGet(entityId);
    if ( e == null ) { e = new E().init(this,entityId); es.set(entityId, e); };
    return e;
  }
}
