//package com.pandoaspen.entityengine.engine.animation;
//
//import com.google.common.collect.BiMap;
//import com.google.common.collect.HashBiMap;
//import com.google.common.collect.Sets;
//import com.pandoaspen.entityengine.engine.EEntity;
//import com.pandoaspen.entityengine.engine.api.AnimationHandler;
//import com.pandoaspen.entityengine.engine.api.AnimationObject;
//import com.pandoaspen.entityengine.engine.utils.AnimationUtils;
//import com.pandoaspen.entityengine.engine.utils.MatrixSupplier;
//import de.javagl.jgltf.model.AnimationModel;
//import de.javagl.jgltf.model.GltfAnimations;
//import de.javagl.jgltf.model.GltfModel;
//import de.javagl.jgltf.model.animation.Animation;
//import de.javagl.jgltf.model.animation.AnimationManager;
//import de.javagl.jgltf.model.animation.AnimationRunner;
//import de.javagl.jgltf.model.io.GltfModelReader;
//import lombok.Getter;
//import org.joml.Vector3f;
//
//import java.io.IOException;
//import java.net.URI;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.function.Supplier;
//
//public class DefaultAnimationHandlerOLD implements AnimationHandler {
//
//    private final Supplier<? extends AnimationObject> animationObjectSupplier;
//    private final EEntity host;
//
//    private final AnimationManager animationManager;
//    private final GltfModel gltfModel;
//
//    private final BiMap<String, List<Animation>> animationMap;
//
//    private final Map<String, Set<MatrixSupplier>> animationSupplierMap;
//    private final Map<MatrixSupplier, AnimationObject> currentAnimationMap;
//
//    @Getter private String currentAnimation;
//
//    private AnimationRunner animationRunner;
//
//    public DefaultAnimationHandlerOLD(URI gblURI, EEntity host, Supplier<? extends AnimationObject> animationObjectSupplier) throws IOException {
//        this.host = host;
//        this.animationObjectSupplier = animationObjectSupplier;
//        this.animationManager = GltfAnimations.createAnimationManager(AnimationManager.AnimationPolicy.LOOP);
//        this.gltfModel = new GltfModelReader().read(gblURI);
//
//        this.animationMap = HashBiMap.create();
//        this.animationSupplierMap = new HashMap<>();
//        this.currentAnimationMap = new ConcurrentHashMap<>();
//
//        this.setupAnimations();
//
//        animationRunner = new AnimationRunner(animationManager);
//        animationRunner.start();
//    }
//
//    private void setupAnimations() {
//
//        List<Animation> animations = GltfAnimations.createModelAnimations(gltfModel.getAnimationModels());
//
//        System.out.println(animations.size());
//
//        gltfModel.getAnimationModels().get(0).getChannels().forEach(channel -> System.out.println(channel.getNodeModel().getName()));
//
//        for (AnimationModel animationModel : gltfModel.getAnimationModels()) {
//
//            String animationName = animationModel.getName();
//            List<Animation> animation = GltfAnimations.createModelAnimations(Sets.newHashSet(animationModel));
//
//            this.animationMap.put(animationName, animation);
//            animationManager.addAnimations(animation);
//
//            this.animationSupplierMap.put(animationName, AnimationUtils.getAnimationSuppliers(animationModel));
//        }
//
//        animationManager.addAnimationManagerListener(am -> updateAnimations());
//
//        setCurrentAnimation("IdleAction");
//    }
//
//    private boolean buildModel() {
//        this.currentAnimationMap.values().forEach(AnimationObject::destroy);
//        this.currentAnimationMap.clear();
//
//        Vector3f origin = host.getPosition();
//        Vector3f direction = host.getDirection();
//
//        for (MatrixSupplier matrixSupplier : animationSupplierMap.get(currentAnimation)) {
//            AnimationObject animationObject = animationObjectSupplier.get();
//            animationObject.create(matrixSupplier.get(origin, direction), matrixSupplier.getNodeModel());
//            this.currentAnimationMap.put(matrixSupplier, animationObject);
//        }
//
//        animationManager.removeAnimations(animationManager.getAnimations());
//        animationManager.addAnimations(animationMap.get(currentAnimation));
//
//        return true;
//    }
//
//    public Set<String> listAnimations() {
//        return animationMap.keySet();
//    }
//
//    public boolean setCurrentAnimation(String name) {
//        if (name.equals(currentAnimation)) return true;
//        if (!animationMap.containsKey(name)) return false;
//        this.currentAnimation = name;
//        return buildModel();
//    }
//
//    public void update(long deltaNs) {
//        //        animationManager.performStep(deltaNs);
//    }
//
//    private void updateAnimations() {
//        if (currentAnimationMap == null || currentAnimationMap.isEmpty()) return;
//        Vector3f origin = host.getPosition();
//        Vector3f direction = host.getDirection();
//        currentAnimationMap.forEach(((matrixSupplier, animationObject) -> animationObject.update(matrixSupplier.get(origin, direction))));
//    }
//
//    public void destroy() {
//        animationRunner.stop();
//        animationManager.removeAnimations(animationManager.getAnimations());
//        if (currentAnimationMap != null) currentAnimationMap.values().forEach(AnimationObject::destroy);
//    }
//}
