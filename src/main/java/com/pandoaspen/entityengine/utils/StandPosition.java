package com.pandoaspen.entityengine.utils;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.EulerAngle;
import org.joml.Vector3f;

@Data
public class StandPosition {

    private final Vector3f location;
    private final EulerAngle headPosition;

    public Location getBukkitLocation(World world) {
        return new Location(world, location.x, location.y, location.z, -90, 0);
    }

}
