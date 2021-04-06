package com.pandoaspen.entityengine;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.io.GltfModelReader;

import java.io.File;
import java.net.URI;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test {

    public static void main(String[] args) throws Exception {

        URI modelURI = new File("C:/users/jack/desktop/modelling/spinner.glb").toURI();
        GltfModel gltfModel = new GltfModelReader().read(modelURI);

        NodeModel armatureNode = gltfModel.getNodeModels().stream().filter(n -> n.getName().equals("Armature")).findAny().get();

        SkinModel skinModel = gltfModel.getSkinModels().get(0);


    }

    public static void printModel(NodeModel node, String prefix) {
        System.out.println(prefix + node.getName());

        System.out.println(prefix + "skin: " + (node.getSkinModel() != null));

        //        System.out.println(prefix + arrayToString(node.getTranslation()));



        node.getChildren().forEach(c -> printModel(c, prefix + "  "));


    }

    public static String arrayToString(float[] arr) {
        return IntStream.range(0, arr.length).mapToObj(i -> String.format("%.2f", arr[i])).collect(Collectors.joining(", "));
    }
}
