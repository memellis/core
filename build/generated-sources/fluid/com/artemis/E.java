package com.artemis;

import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.Color;
import com.ellzone.slotpuzzle2d.component.ColorComponent;
import com.ellzone.slotpuzzle2d.component.Example;
import com.ellzone.slotpuzzle2d.component.PositionComponent;
import com.ellzone.slotpuzzle2d.component.Translation;
import java.lang.Class;
import java.lang.String;

public final class E {
  protected static SuperMapper _processingMapper;

  protected SuperMapper mappers;

  protected int entityId;

  public E init(SuperMapper mappers, int entityId) {
    this.mappers = mappers;
    this.entityId = entityId;
    return this;
  }

  public static E E(int entityId) {
    if(_processingMapper==null) throw new RuntimeException("SuperMapper system must be registered before any systems using E().");;
    return _processingMapper.getE(entityId);
  }

  public static E E(Entity entity) {
    return E(entity.getId());
  }

  public static E E() {
    return E(_processingMapper.getWorld().create());
  }

  public int id() {
    return entityId;
  }

  public Entity entity() {
    return mappers.getWorld().getEntity(entityId);
  }

  /**
   * Get all entities matching aspect.
   * For performance reasons do not create the aspect every call.
   * @return {@code EBag} of entities matching aspect. Returns empty bag if no entities match aspect. */
  public static EBag withAspect(Aspect.Builder aspect) {
    if(_processingMapper==null) throw new RuntimeException("SuperMapper system must be registered before any systems using E().");;
    return new EBag(_processingMapper.getWorld().getAspectSubscriptionManager().get(aspect).getEntities());
  }

  /**
   * Get all entities with component.
   * This is a relatively costly operation. For performance use withAspect instead.
   * @return {@code EBag} of entities matching aspect. Returns empty bag if no entities match aspect. */
  public static EBag withComponent(Class<? extends Component> component) {
    if(_processingMapper==null) throw new RuntimeException("SuperMapper system must be registered before any systems using E().");;
    return new EBag(_processingMapper.getWorld().getAspectSubscriptionManager().get(Aspect.all(component)).getEntities());
  }

  public boolean hasColorComponent() {
    return mappers.mColorComponent.has(entityId);
  }

  public boolean hasExample() {
    return mappers.mExample.has(entityId);
  }

  public boolean hasPositionComponent() {
    return mappers.mPositionComponent.has(entityId);
  }

  public boolean hasTranslation() {
    return mappers.mTranslation.has(entityId);
  }

  public E colorComponent() {
    mappers.mColorComponent.create(entityId);
    return this;
  }

  public E example() {
    mappers.mExample.create(entityId);
    return this;
  }

  public E positionComponent() {
    mappers.mPositionComponent.create(entityId);
    return this;
  }

  public E translation() {
    mappers.mTranslation.create(entityId);
    return this;
  }

  public E tag(String tag) {
    mappers.getWorld().getSystem(com.artemis.managers.TagManager.class).register(tag, entityId);
    return this;
  }

  public String tag() {
    return mappers.getWorld().getSystem(com.artemis.managers.TagManager.class).getTag(entityId);
  }

  /**
   * Get entity by tag.
   * @return {@code E}, or {@code null} if no such tag. */
  public static E withTag(String tag) {
    if(_processingMapper==null) throw new RuntimeException("SuperMapper system must be registered before any systems using E().");;
    int id=_processingMapper.getWorld().getSystem(com.artemis.managers.TagManager.class).getEntityId(tag);
    return id != -1 ? E(id) : null;
  }

  public E group(String group) {
    World w = mappers.getWorld();
    w.getSystem(com.artemis.managers.GroupManager.class).add(w.getEntity(entityId), group);
    return this;
  }

  public E groups(String... groups) {
    for (int i = 0; groups.length > i; i++) { group(groups[i]); };
    return this;
  }

  public E removeGroup(String group) {
    World w = mappers.getWorld();
    w.getSystem(com.artemis.managers.GroupManager.class).remove(w.getEntity(entityId), group);
    return this;
  }

  public E removeGroups(String... groups) {
    for (int i = 0; groups.length > i; i++) { removeGroup(groups[i]); };
    return this;
  }

  public E removeGroups() {
    World w = mappers.getWorld();
    w.getSystem(com.artemis.managers.GroupManager.class).removeFromAllGroups(w.getEntity(entityId));
    return this;
  }

  public ImmutableBag<String> groups() {
    World w = mappers.getWorld();
    return w.getSystem(com.artemis.managers.GroupManager.class).getGroups(w.getEntity(entityId));
  }

  public boolean isInGroup(String group) {
    World w = mappers.getWorld();
    return w.getSystem(com.artemis.managers.GroupManager.class).isInGroup(w.getEntity(entityId), group);
  }

  /**
   * Get entities in group..
   * @return {@code EBag} of entities in group. Returns empty bag if group contains no entities. */
  public static EBag withGroup(String groupName) {
    if(_processingMapper==null) throw new RuntimeException("SuperMapper system must be registered before any systems using E().");;
    return new EBag((com.artemis.utils.IntBag)_processingMapper.getWorld().getSystem(com.artemis.managers.GroupManager.class).getEntityIds(groupName));
  }

  public E removeColorComponent() {
    mappers.mColorComponent.remove(entityId);
    return this;
  }

  public E removeExample() {
    mappers.mExample.remove(entityId);
    return this;
  }

  public E removePositionComponent() {
    mappers.mPositionComponent.remove(entityId);
    return this;
  }

  public E removeTranslation() {
    mappers.mTranslation.remove(entityId);
    return this;
  }

  public ColorComponent getColorComponent() {
    return mappers.mColorComponent.get(entityId);
  }

  public Example getExample() {
    return mappers.mExample.get(entityId);
  }

  public PositionComponent getPositionComponent() {
    return mappers.mPositionComponent.get(entityId);
  }

  public Translation getTranslation() {
    return mappers.mTranslation.get(entityId);
  }

  public E colorComponentColor(Color color) {
    mappers.mColorComponent.create(this.entityId).color=color;
    return this;
  }

  public Color colorComponentColor() {
    return mappers.mColorComponent.create(entityId).color;
  }

  public E exampleAge(float age) {
    mappers.mExample.create(this.entityId).age=age;
    return this;
  }

  public float exampleAge() {
    return mappers.mExample.create(entityId).age;
  }

  public E positionComponentY(float y) {
    mappers.mPositionComponent.create(this.entityId).y=y;
    return this;
  }

  public float positionComponentY() {
    return mappers.mPositionComponent.create(entityId).y;
  }

  public E positionComponentX(float x) {
    mappers.mPositionComponent.create(this.entityId).x=x;
    return this;
  }

  public float positionComponentX() {
    return mappers.mPositionComponent.create(entityId).x;
  }

  public E translationX(float x) {
    mappers.mTranslation.create(this.entityId).x=x;
    return this;
  }

  public float translationX() {
    return mappers.mTranslation.create(entityId).x;
  }

  public E translationY(float y) {
    mappers.mTranslation.create(this.entityId).y=y;
    return this;
  }

  public float translationY() {
    return mappers.mTranslation.create(entityId).y;
  }

  public void deleteFromWorld() {
    mappers.getWorld().delete(entityId);
  }
}
