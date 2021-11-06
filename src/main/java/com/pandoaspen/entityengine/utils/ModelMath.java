package com.pandoaspen.entityengine.utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ModelMath {

    public static float LARGE_HEAD_SIZE = .625f;
    public static float LARGE_HEAD_SIZE_OFFSET = LARGE_HEAD_SIZE / -2f;
    public static float LARGE_HEAD_STAND_OFFSET = 1.56f;

    public static final float SMALL_HEAD_SIZE = .42f;
    public static final float SMALL_HEAD_SIZE_OFFSET = SMALL_HEAD_SIZE / -2f;
    public static final float SMALL_HEAD_STAND_OFFSET = .86f;

    public static int x = 2, y = 1, z = 0;

    public static void positionStand(ArmorStand body, Vector3f origin, Matrix4f m, boolean small) {
        m.transformPosition(origin);

        double eulerX, eulerY, eulerZ;

        double sy = java.lang.Math.sqrt(m.m00() * m.m00() + m.m10() * m.m10());
        boolean singular = sy < 1e-6;

        if (!singular) {
            eulerX = java.lang.Math.atan2(m.m21(), m.m22());
            eulerY = java.lang.Math.atan2(-m.m20(), sy);
            eulerZ = java.lang.Math.atan2(m.m10(), m.m00());
        } else {
            eulerX = java.lang.Math.atan2(-m.m12(), m.m11());
            eulerY = java.lang.Math.atan2(-m.m20(), sy);
            eulerZ = 0;
        }

        float Sx = (float) Math.sin(eulerX);
        float Sy = (float) Math.sin(eulerY);
        float Sz = (float) Math.sin(eulerZ);
        float Cx = (float) Math.cos(eulerX);
        float Cy = (float) Math.cos(eulerY);
        float Cz = (float) Math.cos(eulerZ);

        float m10 = Cy * Sz;
        float m11 = Cx * Cz + Sx * Sy * Sz;
        float m12 = -Cz * Sx + Cx * Sy * Sz;

        float rx = m10 * (small ? SMALL_HEAD_SIZE_OFFSET : LARGE_HEAD_SIZE_OFFSET);
        float ry = m11 * (small ? SMALL_HEAD_SIZE_OFFSET : LARGE_HEAD_SIZE_OFFSET);
        float rz = m12 * (small ? SMALL_HEAD_SIZE_OFFSET : LARGE_HEAD_SIZE_OFFSET);

        origin.sub(0, small ? SMALL_HEAD_STAND_OFFSET : LARGE_HEAD_STAND_OFFSET, 0);
        origin.add(rx, ry, rz);

        double[] arr = new double[]{eulerX, eulerY, eulerZ};

        body.setHeadPose(new EulerAngle(arr[x], arr[y], arr[z]));

        body.teleport(new Location(body.getWorld(), origin.x, origin.y, origin.z, -90, 0));
    }


    public static void positionStand2(ArmorStand body, Vector3f origin, Matrix4f m) {
        //        float eulerX = (float) Math.atan2(m.m12(), m.m22());
        //        float eulerY = (float) Math.atan2(-m.m02(), Math.sqrt(m.m12() * m.m12() + m.m22() * m.m22()));
        //        float eulerZ = (float) Math.atan2(m.m01(), m.m00());

        m.transformPosition(origin);


        double eulerX, eulerY, eulerZ;

        double sy = java.lang.Math.sqrt(m.m00() * m.m00() + m.m10() * m.m10());
        boolean singular = sy < 1e-6;

        if (!singular) {
            eulerX = java.lang.Math.atan2(m.m21(), m.m22());
            eulerY = java.lang.Math.atan2(-m.m20(), sy);
            eulerZ = java.lang.Math.atan2(m.m10(), m.m00());
        } else {
            eulerX = java.lang.Math.atan2(-m.m12(), m.m11());
            eulerY = java.lang.Math.atan2(-m.m20(), sy);
            eulerZ = 0;
        }
        ;


        float Sx = (float) Math.sin(eulerX);
        float Sy = (float) Math.sin(eulerY);
        float Sz = (float) Math.sin(eulerZ);
        float Cx = (float) Math.cos(eulerX);
        float Cy = (float) Math.cos(eulerY);
        float Cz = (float) Math.cos(eulerZ);

        float m10 = Cy * Sz;
        float m11 = Cx * Cz + Sx * Sy * Sz;
        float m12 = -Cz * Sx + Cx * Sy * Sz;

        float rx = m10 * LARGE_HEAD_SIZE_OFFSET;
        float ry = m11 * LARGE_HEAD_SIZE_OFFSET;
        float rz = m12 * LARGE_HEAD_SIZE_OFFSET;

        origin.sub(0, LARGE_HEAD_STAND_OFFSET, 0);
        origin.add(rx, ry, rz);

        double[] arr = new double[]{eulerX, eulerY, eulerZ};

        body.setHeadPose(new EulerAngle(arr[x], arr[y], arr[z]));

        body.teleport(new Location(body.getWorld(), origin.x, origin.y, origin.z, -90, 0));
    }

    public static void positionStandHead(ArmorStand stand, Vector3f origin, Matrix4f m, boolean small) {
        float headoffset = small ? SMALL_HEAD_SIZE_OFFSET : LARGE_HEAD_SIZE_OFFSET;
        float standoffset = small ? SMALL_HEAD_STAND_OFFSET : LARGE_HEAD_STAND_OFFSET;

        Vector3f euler = m.getEulerAnglesZYX(new Vector3f());
        float[] angs = new float[]{euler.x, euler.y, euler.z};

        float eulerX = angs[x];
        float eulerY = angs[y];
        float eulerZ = angs[z];

        float Sx = (float) Math.sin(eulerX);
        float Sy = (float) Math.sin(eulerY);
        float Sz = (float) Math.sin(eulerZ);
        float Cx = (float) Math.cos(eulerX);
        float Cy = (float) Math.cos(eulerY);
        float Cz = (float) Math.cos(eulerZ);

        float m10 = Cy * Sz;
        float m11 = Cx * Cz + Sx * Sy * Sz;
        float m12 = -Cz * Sx + Cx * Sy * Sz;

        float rx = m10 * headoffset;
        float ry = m11 * headoffset;
        float rz = m12 * headoffset;

        origin.sub(0, standoffset, 0);
        origin.add(rx, ry, rz);

        stand.setHeadPose(new EulerAngle(eulerZ, eulerY, eulerX));
        stand.teleport(new Location(stand.getWorld(), origin.x, origin.y, origin.z, -90, 0));
    }

    public static void positionStandArm(ArmorStand stand, Vector3f origin, Matrix4f m, boolean small) {

    }
}
