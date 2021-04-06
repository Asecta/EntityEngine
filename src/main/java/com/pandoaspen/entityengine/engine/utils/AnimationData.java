package com.pandoaspen.entityengine.engine.utils;

import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.animation.Animation;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class AnimationData {
    private Map<String, List<Animation>> animations;
    private Map<String, Map<NodeModel, Animation>> animationMap;
}
