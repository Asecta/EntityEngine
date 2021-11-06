package com.pandoaspen.entityengine.engine.animation;

import com.pandoaspen.entityengine.engine.EEntity;
import com.pandoaspen.entityengine.engine.api.AnimationHandler;
import com.pandoaspen.entityengine.engine.api.AnimationObject;
import com.pandoaspen.entityengine.engine.utils.AnimationData;
import com.pandoaspen.entityengine.engine.utils.AnimationUtils;
import de.javagl.jgltf.model.AccessorModel;
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
import java.nio.DoubleBuffer;
import java.util.*;
import java.util.function.Supplier;

public class DefaultAnimationHandler implements AnimationHandler {

    private final EEntity host;

    private final AnimationManager animationManager;
    private final GltfModel gltfModel;

    private Map<String, List<Animation>> animationMap;

    @Getter private String currentAnimation;

    private Map<NodeModel, AnimationObject> animationObjectMap;

    private Map<NodeModel, TransformSupplier> nodeModelSupplierMap = new HashMap<>();

    public DefaultAnimationHandler(URI gblURI, EEntity host) throws IOException {
        this.host = host;
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

            float scale = nodeModel.getScale()[0] * 2;

            boolean small = scale < .5;

            AnimationObject animationObject = new StandEntityAnimationObject(small);
            animationObjectMap.put(nodeModel, animationObject);
        });

        for (String animationName : animationNodeMap.keySet()) {
            Map<NodeModel, Animation> nodeModelAnimationMap = animationNodeMap.get(animationName);
            nodeModelAnimationMap.forEach(((nodeModel, animation) -> {
                if (!nodeModel.getName().startsWith("Cube")) return;
                AnimationObject animationObject = animationObjectMap.get(nodeModel);
                Supplier<float[]> globalTransformSupplier = nodeModel.createGlobalTransformSupplier();
                Supplier<float[]> localTransformSupplier = nodeModel.createLocalTransformSupplier();

                float[] translation = nodeModel.getTranslation();
                float[] rotation = nodeModel.getRotation();
                float[] meshTranslation = computeLocalMeshTranslation(nodeModel);

                nodeModelSupplierMap.put(nodeModel, new TransformSupplier(translation, rotation, meshTranslation, globalTransformSupplier, localTransformSupplier));
            }));
        }

        animationObjectMap.forEach((nodeModel, animationObject) -> {
            if (!nodeModel.getName().startsWith("Cube")) return;
            animationObject.create(new Matrix4f(), nodeModel);
        });

        animationManager.removeAnimations(animationManager.getAnimations());

        setCurrentAnimation("IdleAction");
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

    public static float q1X = 0, q1Y = 1, q1Z = 0;
    public static float q2X = 0, q2Y = 1, q2Z = 0, q2W = 1;

    public void update(long deltaNs) {
        deltaNs *= scalar;
        animationManager.performStep(deltaNs);

        Vector3f origin = host.getPosition();
        Vector3f direction = host.getDirection();

        animationObjectMap.forEach(((nodeModel, animationObject) -> {
            TransformSupplier transformSupplier = nodeModelSupplierMap.get(nodeModel);
            updateObject(origin, direction, transformSupplier, animationObject);
        }));
    }

    public double scalar = 1;

    public void setAnimationSpeed(double scalar) {
        this.scalar = scalar;
    }

    private void updateObject(Vector3f origin, Vector3f direction, TransformSupplier transformSupplier, AnimationObject animationObject) {
        Matrix4f globalTransform = transformSupplier.getGlobalTransformMatrix();
        Quaternionf rotation = transformSupplier.getRotationQuat();

        Matrix4f matrix = new Matrix4f();

        Quaternionf dir = new Quaternionf();
        dir.lookAlong(direction, new Vector3f(0, 1, 0));

        matrix.rotate(dir.invert());

        matrix.translateLocal(origin);

        matrix.mul(globalTransform);
        matrix.rotate(rotation);

        animationObject.update(matrix);
    }

    public void destroy() {
        animationManager.removeAnimations(animationManager.getAnimations());
        animationObjectMap.values().forEach(AnimationObject::destroy);
    }

    @Override
    public NodeModel getNodeModel(String name) {
        return gltfModel.getNodeModels().stream().filter(n -> n.getName().equals(name)).findAny().get();
    }

    public static float[] computeLocalMeshTranslation(NodeModel nodeModel) {
        AccessorModel accessorModel = nodeModel.getMeshModels().get(0).getMeshPrimitiveModels().get(0).getAttributes().get("POSITION");
        DoubleBuffer db = accessorModel.getAccessorData().createByteBuffer().asDoubleBuffer();

        List<Double> xCoords = new ArrayList<>();
        List<Double> yCoords = new ArrayList<>();
        List<Double> zCoords = new ArrayList<>();

        while (db.hasRemaining()) {
            xCoords.add(db.get());
            yCoords.add(db.get());
            zCoords.add(db.get());
        }

        float x = (float) xCoords.stream().mapToDouble(f -> f).average().getAsDouble();
        float y = (float) yCoords.stream().mapToDouble(f -> f).average().getAsDouble();
        float z = (float) zCoords.stream().mapToDouble(f -> f).average().getAsDouble();

        return new float[]{x, y, z};
    }
}
