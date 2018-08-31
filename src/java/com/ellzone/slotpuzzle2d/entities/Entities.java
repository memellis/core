package com.ellzone.slotpuzzle2d.entities;

import com.ellzone.slotpuzzle2d.component.Components;

import java.util.HashMap;
import java.util.UUID;

public abstract class Entities {
    public UUID id;
    protected static HashMap<Class, HashMap<UUID, ? extends Components>> components =
            new HashMap<>();

    protected Entities() {
        this.id = UUID.randomUUID();
    }

    public static <T extends Components> void addComponents(UUID entity, T component) {
        synchronized (components) {
            HashMap<UUID, ? extends Components> store = components.get(component);
            if (store == null) {
                store = new HashMap<>();
                components.put(component.getClass(), store);
            }
            ((HashMap<UUID, T>) store).put(entity, component);
        }
    }

    public <T extends Components> void addComponents(T component){
        synchronized (components) {
            HashMap<UUID, ? extends Components> store = components.get(component);
            if (store == null) {
                store = new HashMap<>();
                components.put(component.getClass(), store);
            }
            ((HashMap<UUID, T>) store).put(this.id, component);
        }
    }

    public static <T> T getComponents(UUID entity, Class<T> component) {
        HashMap<UUID, ? extends Components> store = components.get(component);
        if (store == null)
            throw new IllegalArgumentException("Get Fail: "+entity.toString()+" does not posses Component of class \n missing " + component);
        T results = (T) store.get(entity);
        if (results == null)
            throw new IllegalArgumentException("Get Fail: "+entity.toString()+" does not posses Component of class \n missing " + component);
        return results;
    }

    public <T> T getComponents(Class<T> component) {
        HashMap<UUID, ? extends Components> store = components.get(component);
        if (store == null)
            new IllegalArgumentException("Get Fail: "+this.id.toString()+" does not posses Component of class \n missing " + component);
        T results = (T) store.get(this.id);
        if (results == null)
            throw new IllegalArgumentException("Get Fail: "+this.id.toString()+" does not posses Component of class \n missing " + component);
        return results;
    }

    public static <T> boolean hasComponents(UUID entity, Class<T> component) {
        HashMap<UUID, ? extends Components> store = components.get(component);
        if (store == null)
            return false;
        T results = (T) store.get(entity);
        if (results == null)
            return false;
        return true;
    }

    public <T> boolean hasComponents(Class<T> component) {
        HashMap<UUID, ? extends Components> store = components.get(component);
        if (store == null)
            return false;
        T results = (T) store.get(this.id);
        if (results == null)
           return false;
        return true;
    }
}
