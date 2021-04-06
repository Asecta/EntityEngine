package com.pandoaspen.entityengine.engine;

import com.pandoaspen.entityengine.engine.animation.DefaultAnimationHandler;
import com.pandoaspen.entityengine.engine.animation.AnimationHelper;
import com.pandoaspen.entityengine.engine.api.EngineEntity;
import com.pandoaspen.entityengine.utils.LAUtils;
import de.javagl.jgltf.model.animation.AnimationRunner;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.net.URI;
import java.util.UUID;

@Data
public abstract class EEntity implements EngineEntity {

    private final UUID id;
    private final DefaultAnimationHandler animationHandler;

    private World world;

    private Vector3f position;
    private Vector3f direction;
    private Vector3f velocity;

    public EEntity(UUID id, Location location) {
        this.id = id;
        this.world = location.getWorld();
        this.position = LAUtils.getVector(location);
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

    public abstract URI getAnimationFile();

}
