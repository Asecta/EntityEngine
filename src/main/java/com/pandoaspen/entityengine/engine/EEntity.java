package com.pandoaspen.entityengine.engine;

import com.pandoaspen.entityengine.engine.animation.AnimationHelper;
import com.pandoaspen.entityengine.engine.animation.DefaultAnimationHandler;
import com.pandoaspen.entityengine.engine.api.EngineEntity;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.File;
import java.net.URI;
import java.util.UUID;

@Data
public abstract class EEntity implements EngineEntity {

    public static final File MODEL_FILE = new File(".");

    private final UUID id;
    private final DefaultAnimationHandler animationHandler;

    private World world;

    private Vector3f position;
    private Vector3f direction;
    private Vector3f velocity;

    public EEntity(UUID id, Location location) {
        this.id = id;
        this.world = location.getWorld();
        this.position = new Vector3f((float) location.getX(), (float) location.getY(), (float) location.getZ());
        this.direction = new Vector3f(1, 0, 0);
        this.animationHandler = AnimationHelper.getStandAnimationHandler(getAnimationFile(), this);
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    @Override
    public void destroy() {
        animationHandler.destroy();
    }

    @Override
    public void render(long deltaNs) {
        this.animationHandler.update(deltaNs);
    }

    @Override
    public Matrix4f getOrigin() {
        Matrix4f matrix4f = new Matrix4f().normal();
        matrix4f.translate(position);

        return matrix4f;
    }

    public Location getLocation() {
        return new Location(world, position.x, position.y, position.z);
    }

    public Vector3f getBukkitVec3f(Vector vector) {
        return new Vector3f((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
    }

    public abstract URI getAnimationFile();

}
