package com.pandoaspen.entityengine;

import au.edu.federation.caliko.IKBone;
import au.edu.federation.caliko.IKChain;
import au.edu.federation.caliko.IKStructure;
import org.joml.Vector3f;

public class IKTest {

    public static void main(String[] args) {
        IKStructure structure = new IKStructure();

        IKChain chain = new IKChain();

        Vector3f direction = new Vector3f(1, 0, 0);

        Vector3f startLoc = new Vector3f(0.0f, 0.0f, 0);
        Vector3f endLoc = startLoc.add(direction.mul(1, new Vector3f()), new Vector3f());

        IKBone basebone = new IKBone(startLoc, endLoc);
        chain.addBone(basebone);

        for (int boneLoop = 0; boneLoop < 4; boneLoop++) {
            IKBone bone3D = new IKBone(new Vector3f(), direction, 1);
            chain.addConsecutiveBone(bone3D);
        }

        printBones(chain);

        structure.addChain(chain);

        chain.solveForTarget(0, 4, 0);

        System.out.println("---------------------------");

        printBones(chain);

    }

    public static void printBones(IKChain chain3D) {
        chain3D.getChain().forEach(bone -> {
            System.out.println(String.format("%s -> %s", printVec(bone.getStartLocation()), printVec(bone.getEndLocation())));
        });
    }


    public static String printVec(Vector3f v) {
        return String.format("%.2f, %.2f, %.2f", v.x, v.y, v.z);
    }
}
