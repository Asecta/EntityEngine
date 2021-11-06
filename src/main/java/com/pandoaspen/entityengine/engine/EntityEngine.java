package com.pandoaspen.entityengine.engine;

import com.pandoaspen.entityengine.engine.api.EngineEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

public class EntityEngine {

    public static double speedMult = .001;

    private Set<EngineEntity> entities;

    private long previousNs;

    public EntityEngine() {
        this.entities = new HashSet<>();
        this.previousNs = System.nanoTime();
    }

    public void destroy() {
        entities.forEach(EngineEntity::destroy);
        entities.clear();
    }

    public void tick() {
        this.entities.forEach(EngineEntity::tick);
        render();
    }

    private void render() {
        long currentNs = System.nanoTime();
        long deltaNs = (currentNs - previousNs);
        this.entities.forEach(e -> e.render(deltaNs));
        previousNs = currentNs;
    }

    public EEntity spawnEntity(Location location, BiFunction<UUID, Location, ? extends EEntity> supplier) {
        EEntity eEntity = supplier.apply(UUID.randomUUID(), location);
        this.entities.add(eEntity);
        return eEntity;
    }

    public EEntity spawnEntity(EEntity eEntity) {
        this.entities.add(eEntity);
        return eEntity;
    }

    public <T extends EEntity> T spawnEntity(Location location, Class<T> e) {

        try {
            T t = e.getConstructor(UUID.class, Location.class).newInstance(UUID.randomUUID(), location);
            this.entities.add(t);
            return t;
        } catch (Exception instantiationException) {
            instantiationException.printStackTrace();
        }
        return null;
    }

    public void setDirection(Vector direction) {
        entities.forEach(ee -> ee.setDirection(new Vector3f((float) direction.getX(), (float) direction.getY(), (float) direction.getZ())));
    }
}
