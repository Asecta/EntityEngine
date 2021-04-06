package com.pandoaspen.entityengine.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.joml.Vector3f;

public class LAUtils {

    public static Location vectorToBukkitLocation(World world, Vector3f vector3f) {
        return new Location(world, vector3f.x, vector3f.y, vector3f.z);
    }

    public static Vector3f getVector(Location location) {
        return new Vector3f((float) location.getX(), (float) location.getY(), (float) location.getZ());
    }

    public static void printVec(Vector3f v) {
        System.out.println(String.format("%.2f, %.2f, %.2f", v.x, v.y, v.z));
    }

}
