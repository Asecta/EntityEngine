package com.pandoaspen.entityengine.utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class StandUtils {

    private static final float STAND_HEAD_GROUND_OFFSET = 1 / 3f + 1;
    private static final float STAND_HEAD_NECK_OFFSET = .25f;

    public static StandPosition getStandPosition(float[] arr) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.set(arr);
        return getStandPosition(matrix4f);
    }

    public static StandPosition getStandPosition(Matrix4f matrix4f) {
        Vector3f position = matrix4f.transformPosition(new Vector3f(0, 0, 0));

        Vector3f direction = matrix4f.transformDirection(new Vector3f(0, 1, 0));
        direction.mul(STAND_HEAD_NECK_OFFSET, direction);
        direction.mul(-1, direction);

        position.add(direction, position);
        position.sub(0, STAND_HEAD_GROUND_OFFSET, 0, position);

        EulerAngle eulerAngle = getRotation(matrix4f);

        return new StandPosition(position, eulerAngle);
    }

    public static void positionStand(Location origin, ArmorStand armorStand, Matrix4f matrix4f) {
        Vector3f position = matrix4f.transformPosition(new Vector3f(0, 0, 0));

        Location l = origin.clone().add(position.x, position.y, position.z);
        l.setDirection(new Vector(1, 0, 0));
        l.subtract(0, 1 / 3f + 1, 0);

        Vector3f dir = matrix4f.transformDirection(new Vector3f(0, 1, 0));
        dir.mul(0.25f, dir);
        dir.mul(-1, dir);
        l.add(dir.x, dir.y, dir.z);

        EulerAngle eulerAngle = getRotation(matrix4f);

        armorStand.setHeadPose(eulerAngle);
        armorStand.teleport(l);
    }

    public static EulerAngle getRotation(Matrix4f m) {

        double x, y, z;

        double sy = Math.sqrt(m.m00() * m.m00() + m.m10() * m.m10());
        boolean singular = sy < 1e-6;

        if (!singular) {
            x = Math.atan2(m.m21(), m.m22());
            y = Math.atan2(-m.m20(), sy);
            z = Math.atan2(m.m10(), m.m00());
        } else {
            x = Math.atan2(-m.m12(), m.m11());
            y = Math.atan2(-m.m20(), sy);
            z = 0;
        }
        return new EulerAngle(z, y, x);
    }

}
