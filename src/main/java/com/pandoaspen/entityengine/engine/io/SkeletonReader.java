package com.pandoaspen.entityengine.engine.io;

import au.edu.federation.caliko.IKBone;
import au.edu.federation.caliko.IKChain;
import au.edu.federation.caliko.IKStructure;
import com.google.gson.Gson;
import com.pandoaspen.entityengine.engine.io.model.SkeletonBone;
import com.pandoaspen.entityengine.engine.io.model.SkeletonData;
import org.joml.Vector3f;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkeletonReader {

    public static void main(String[] args) throws Exception {
        IKStructure structure = buildStructure();
        for (int i = 0; i < structure.getNumChains(); i++) {
            System.out.println(structure.getChain(i).getNumBones());
        }
    }

    public static IKStructure buildStructure() throws Exception {
        Gson gson = new Gson();

        File file = new File("./test.json");
        SkeletonData skeletonData = gson.fromJson(new String(Files.readAllBytes(file.toPath())), SkeletonData.class);

        List<SkeletonBone> boneList = skeletonData.getSkeleton();

        Map<String, SkeletonBone> boneMap = boneList.stream().collect(Collectors.toMap(b -> b.getName(), b -> b));
        List<SkeletonBone> roots = boneList.stream().filter(b -> b.getParent() == null).collect(Collectors.toList());

        boneMap.values().forEach(bone -> bone.getChildrenNames().stream().forEach(s -> bone.getChildBones().add(boneMap.get(s))));


        printTree(roots.get(0), "");


        IKStructure ikStructure = new IKStructure();
        buildChain(boneList.get(0), ikStructure, -1, 0);



        return ikStructure;
    }

    static void printTree(SkeletonBone skeletonBone, String prefix) {
        System.out.println(prefix + skeletonBone.getName());
        for (SkeletonBone childBone : skeletonBone.getChildBones()) {
            printTree(childBone, prefix + "  ");
        }
    }

    public static void buildChain(SkeletonBone sBone, IKStructure structure, int chainIdx, int boneIdx) {
        IKChain ikChain = new IKChain();

        ikChain.addBone(parseBone(sBone));

        if (chainIdx == -1) {
            structure.addChain(ikChain);
        } else {
            structure.connectChain(ikChain, chainIdx, boneIdx);
            System.out.println("adding next chain");
        }

        SkeletonBone child = sBone;

        while (child.getChildBones().size() == 1) {
            child = child.getChildBones().get(0);
            ikChain.addConsecutiveBone(parseBone(child));
            System.out.println("adding child to " + chainIdx);
            boneIdx++;
        }

        if (child.getChildBones().size() > 1) {
            for (SkeletonBone childBone : child.getChildBones()) {
                buildChain(childBone, structure, 0, boneIdx);
            }
        }
    }

    public static IKBone parseBone(SkeletonBone sBone) {
        Vector3f head = new Vector3f(sBone.getHead()[0], sBone.getHead()[1], sBone.getHead()[2]);
        Vector3f tail = new Vector3f(sBone.getTail()[0], sBone.getTail()[1], sBone.getTail()[2]);
        IKBone ikBone = new IKBone(head, tail);
        ikBone.setName(sBone.getName());
        return ikBone;
    }
}
