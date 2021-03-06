package com.pandoaspen.entityengine.entity;

import com.pandoaspen.entityengine.engine.EEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.joml.Vector3f;

import java.io.File;
import java.net.URI;
import java.util.UUID;

public class BipodEntity extends EEntity {

    private static final URI ANIMATION_FILE = new File(MODEL_FILE, "bipod.glb").toURI();

    private Vector3f desiredLocation;

    public BipodEntity(UUID id, Location location) {
        super(id, location);
    }

    @Override
    public URI getAnimationFile() {
        return ANIMATION_FILE;
    }

    private boolean isOnGround() {
        return !getLocation().subtract(0, .1, 0).getBlock().getType().isSolid();
    }

    @Override
    public void tick() {
        if (Bukkit.getOnlinePlayers().size() == 0) {
            return;
        }

        if (getAnimationHandler() == null) return;

        Location playerLoc = Bukkit.getOnlinePlayers().stream().findFirst().get().getLocation();
        desiredLocation = new Vector3f((float) playerLoc.getX(), getPosition().y, (float) playerLoc.getZ());

        if (desiredLocation.distance(getPosition()) < 5) {
            if (!"IdleAction".equals(getAnimationHandler().getCurrentAnimation())) getAnimationHandler().setCurrentAnimation("IdleAction");
            return;
        }

        if (!"WalkAction".equals(getAnimationHandler().getCurrentAnimation())) getAnimationHandler().setCurrentAnimation("WalkAction");

        Vector3f newDirection = new Vector3f(desiredLocation);
        newDirection.sub(getPosition()).normalize();

        setDirection(newDirection);

        setPosition(new Vector3f(getPosition()).add(new Vector3f(newDirection).mul(.25f)));
    }

}
