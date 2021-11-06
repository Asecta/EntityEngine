package com.pandoaspen.entityengine.engine.utils;

import de.javagl.jgltf.model.AnimationModel;
import de.javagl.jgltf.model.GltfAnimations;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.animation.Animation;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnimationUtils {

    private static final String MODEL_NAME_PREFIX = "CUBE";

    public static AnimationData linkAnimationModel(GltfModel model) {
        List<AnimationModel> animationModelList = model.getAnimationModels();


        List<Animation> animationList = GltfAnimations.createModelAnimations(animationModelList);

        Map<String, Map<NodeModel, Animation>> animationNodeMap = new HashMap<>();
        Map<String, List<Animation>> animationNameMap = new HashMap<>();

        int start = 0;

        for (AnimationModel animationModel : animationModelList) {
            animationNameMap.put(animationModel.getName(), new ArrayList<>());
            for (AnimationModel.Channel channel : animationModel.getChannels()) {
                int channelIdx = animationModel.getChannels().indexOf(channel);

                for (NodeModel node : walkNodeModel(channel.getNodeModel())) {
                    animationNameMap.get(animationModel.getName()).add(animationList.get(start + channelIdx));

                    if (!node.getName().toUpperCase().startsWith(MODEL_NAME_PREFIX)) continue;

                    animationNodeMap.computeIfAbsent(animationModel.getName(), e -> new HashMap<>())
                            .put(node, animationList.get(start + channelIdx));

                }
            }

            start += animationModel.getChannels().size();
        }

        return new AnimationData(animationNameMap, animationNodeMap);

        //        return animationModelList.stream()
        //                 .collect(Collectors.toMap(
        //                         animModel -> animModel.getName(),
        //                         animModel -> animModel.getChannels().stream()
        //                                 .flatMap(channel -> channel.getNodeModel().getChildren().stream())
        //                                 .collect(Collectors.toMap(
        //                                         node -> (NodeModel) node,
        //                                         node -> (Animation) animationList.get(nodeModelList.indexOf(node)),
        //                                         mapMerger()
        //                                 ))));
    }

    public static Stream<NodeModel> streamAnimatableNodes(AnimationModel animationModel) {
        return animationModel.getChannels().stream().flatMap(c -> walkNodeModel(c.getNodeModel()).stream()).filter(node -> node.getName().toUpperCase().startsWith(MODEL_NAME_PREFIX));
    }

    public static Set<MatrixSupplier> getAnimationSuppliers(AnimationModel animationModel) {
        return AnimationUtils.streamAnimatableNodes(animationModel).map(node -> new MatrixSupplier(node, node.createGlobalTransformSupplier())).collect(Collectors.toSet());
    }

    public static ItemStack parseItemStack(NodeModel nodeModel) {
        try {
            String name = nodeModel.getName();
            String matStr = name.substring(name.indexOf('_') + 1).toUpperCase();
            return new ItemStack(Material.valueOf(matStr));
        } catch (Exception e) {
        }

        return new ItemStack(Material.GREEN_WOOL);
    }

    public static Set<NodeModel> walkNodeModel(NodeModel nodeModel) {
        Set<NodeModel> nodesSet = new HashSet<>();
        nodesSet.add(nodeModel);

        Queue<NodeModel> nodeQueue = new ArrayDeque<>();
        nodeQueue.addAll(nodeModel.getChildren());

        while (!nodeQueue.isEmpty()) {
            NodeModel child = nodeQueue.poll();

            if (child.getChildren() != null) {
                nodeQueue.addAll(child.getChildren());
            }

            nodesSet.add(child);
        }

        return nodesSet;
    }
}
