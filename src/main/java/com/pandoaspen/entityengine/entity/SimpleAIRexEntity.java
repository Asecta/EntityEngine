package com.pandoaspen.entityengine.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.joml.Vector3f;

import java.util.UUID;

public class SimpleAIRexEntity extends RexEntity {

    private Vector3f desiredLocation;

    private Entity host;

    private Location lastLoc;

    private Location startLoc;

    public SimpleAIRexEntity(UUID id, Entity host) {
        super(id, host.getLocation());
        this.host = host;
        this.lastLoc = host.getLocation();
        this.startLoc = host.getLocation().clone();
    }

    @Override
    public void tick() {

        if (Bukkit.getOnlinePlayers().size() == 0) {
            return;
        }

        if (getAnimationHandler() == null) return;

        Location playerLoc = host.getLocation();
        desiredLocation = new Vector3f((float) playerLoc.getX(), getPosition().y, (float) playerLoc.getZ());

        if (hasMoved()) {
            setAnimation("WalkAction");
            getAnimationHandler().setAnimationSpeed(3);
        } else {
            setAnimation("IdleAction");
            getAnimationHandler().setAnimationSpeed(1);
        }

        Vector3f newDirection = getBukkitVec3f(host.getLocation().getDirection());
        setDirection(newDirection);
        setPosition(getBukkitVec3f(playerLoc.toVector()));

        if (host.getLocation().distance(startLoc) > 20) {
            ((Zombie)host).getPathfinder().moveTo(startLoc);
        }
    }

    long debounce = System.currentTimeMillis();

    public void setAnimation(String action) {
        String currentAnimation = getAnimationHandler().getCurrentAnimation();
        if (action.equals(currentAnimation)) return;

        if (System.currentTimeMillis() < debounce + 500) return;
        debounce = System.currentTimeMillis();

        getAnimationHandler().setCurrentAnimation(action);
    }

    public boolean hasMoved() {
        Location currentLoc = host.getLocation();
        boolean result = lastLoc.getX() != currentLoc.getX() || lastLoc.getY() != currentLoc.getY() || lastLoc.getZ() != currentLoc.getZ();
        lastLoc = currentLoc.clone();
        return result;
    }
}
