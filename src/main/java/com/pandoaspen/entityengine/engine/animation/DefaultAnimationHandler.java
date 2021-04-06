package com.pandoaspen.entityengine.engine.animation;

import com.pandoaspen.entityengine.engine.EEntity;
import com.pandoaspen.entityengine.engine.api.AnimationHandler;
import com.pandoaspen.entityengine.engine.api.AnimationObject;
import com.pandoaspen.entityengine.engine.utils.AnimationData;
import com.pandoaspen.entityengine.engine.utils.AnimationUtils;
import de.javagl.jgltf.model.GltfAnimations;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.animation.Animation;
import de.javagl.jgltf.model.animation.AnimationManager;
import de.javagl.jgltf.model.io.GltfModelReader;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class DefaultAnimationHandler implements AnimationHandler {

    private final Supplier<? extends AnimationObject> animationObjectSupplier;
    private final EEntity host;

    private final AnimationManager animationManager;
    private final GltfModel gltfModel;

    private Map<String, List<Animation>> animationMap;

    @Getter private String currentAnimation;

    private Map<NodeModel, AnimationObject> animationObjectMap;

    private Map<NodeModel, Supplier<float[]>> nodeModelSupplierMap = new HashMap<>();

    public DefaultAnimationHandler(URI gblURI, EEntity host, Supplier<? extends AnimationObject> animationObjectSupplier) throws IOException {
        this.host = host;
        this.animationObjectSupplier = animationObjectSupplier;
        this.animationManager = GltfAnimations.createAnimationManager(AnimationManager.AnimationPolicy.LOOP);
        this.gltfModel = new GltfModelReader().read(gblURI);

        this.animationObjectMap = new HashMap<>();

        this.setupAnimations();
    }

    private void setupAnimations() {
        AnimationData data = AnimationUtils.linkAnimationModel(gltfModel);
        Map<String, Map<NodeModel, Animation>> animationNodeMap = data.getAnimationMap();

        this.animationMap = data.getAnimations();

        data.getAnimations().values().forEach(l -> {
            animationManager.addAnimations(l);
        });

        animationNodeMap.values().stream().flatMap(map -> map.keySet().stream()).forEach(nodeModel -> {
            AnimationObject animationObject = animationObjectSupplier.get();
            animationObjectMap.put(nodeModel, animationObject);
        });

        for (String animationName : animationNodeMap.keySet()) {
            Map<NodeModel, Animation> nodeModelAnimationMap = animationNodeMap.get(animationName);
            nodeModelAnimationMap.forEach(((nodeModel, animation) -> {
                if (!nodeModel.getName().startsWith("Cube")) return;
                AnimationObject animationObject = animationObjectMap.get(nodeModel);
                Supplier<float[]> supplier = nodeModel.createGlobalTransformSupplier();
                nodeModelSupplierMap.put(nodeModel, supplier);
                //                animation.addAnimationListener((source, timeS, values) -> handleAnimation(animationName, nodeModel, animationObject, timeS, supplier.get()));
            }));
        }

        animationObjectMap.forEach((nodeModel, animationObject) -> {
            if (!nodeModel.getName().startsWith("Cube")) return;
            animationObject.create(new Matrix4f(), nodeModel);
        });

        animationManager.removeAnimations(animationManager.getAnimations());

        setCurrentAnimation("IdleAction");
    }

    public void handleAnimation(String animationName, NodeModel nodeModel, AnimationObject animationObject, float deltaTime, float[] arr) {
        //        if (!animationName.equals(currentAnimation)) return;
        //
        //        Vector3f origin = host.getPosition();
        //        Vector3f direction = host.getDirection();
        //
        //        Matrix4f positionMatrix = new Matrix4f();
        //        positionMatrix.set(arr);
        //
        //        Matrix4f matrix = new Matrix4f();
        //        matrix.translate(origin);
        //        matrix.lookAlong(new Vector3f(direction.x, direction.y, direction.z), new Vector3f(0, -1, 0));
        //        matrix.rotate(new Quaternionf(1, 0, 0, 0));
        //
        //        matrix.mul(positionMatrix);
        //
        //        animationObject.update(matrix);
    }

    public Set<String> listAnimations() {
        return animationMap.keySet();
    }

    public boolean setCurrentAnimation(String name) {
        if (name.equals(currentAnimation)) return true;
        this.currentAnimation = name;
        animationManager.removeAnimations(animationManager.getAnimations());

        if (animationMap.containsKey(name)) {
            animationManager.addAnimations(animationMap.get(name));
        }

        return true;
    }

    public void update(long deltaNs) {
        animationManager.performStep(deltaNs);

        animationObjectMap.forEach(((nodeModel, animationObject) -> {
            Vector3f origin = host.getPosition();
            Vector3f direction = host.getDirection();

            Matrix4f positionMatrix = new Matrix4f();
            positionMatrix.set(nodeModelSupplierMap.get(nodeModel).get());

            Matrix4f matrix = new Matrix4f();
            matrix.translate(origin);
            matrix.lookAlong(new Vector3f(direction.x, direction.y, direction.z), new Vector3f(0, -1, 0));
            matrix.rotate(new Quaternionf(1, 0, 0, 0));
            matrix.mul(positionMatrix);

            animationObject.update(matrix);
        }));
    }

    public void destroy() {
        animationManager.removeAnimations(animationManager.getAnimations());
        animationObjectMap.values().forEach(AnimationObject::destroy);
    }

    @Override
    public NodeModel getNodeModel(String name) {
        return gltfModel.getNodeModels().stream().filter(n -> n.getName().equals(name)).findAny().get();
    }
}
