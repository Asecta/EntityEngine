package com.pandoaspen.entityengine.engine.api;

import org.bukkit.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.UUID;

public interface EngineEntity {
    UUID getId();

    void tick();

    void render(long deltaNs);

    void destroy();

    World getWorld();

    Matrix4f getOrigin();

    Vector3f getVelocity();

    void setDirection(Vector3f vector3f);
}
